package com.decawave.argomanager.scontroller.util;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.Supplier;
import com.decawave.argo.api.struct.NetworkNode;
import com.decawave.argo.api.struct.NodeType;
import com.decawave.argo.api.struct.Position;
import com.decawave.argo.api.struct.TagNode;
import com.decawave.argo.api.struct.UwbMode;
import com.decawave.argomanager.Constants;
import com.decawave.argomanager.R;
import com.decawave.argomanager.argoapi.ext.NodeFactory;
import com.decawave.argomanager.components.BlePresenceApi;
import com.decawave.argomanager.components.EnhancedNetworkNodeContainer;
import com.decawave.argomanager.components.EnhancedNetworkNodeContainerFactory;
import com.decawave.argomanager.components.NetworkModel;
import com.decawave.argomanager.components.NetworkModelManager;
import com.decawave.argomanager.components.NetworkNodeManager;
import com.decawave.argomanager.components.NetworksNodesStorage;
import com.decawave.argomanager.components.PositionObservationManager;
import com.decawave.argomanager.components.impl.NetworksNodesStorageImpl;
import com.decawave.argomanager.components.struct.NetworkNodeEnhanced;
import com.decawave.argomanager.components.struct.TrackMode;
import com.decawave.argomanager.ioc.ArgoComponent;
import com.decawave.argomanager.ioc.IocContext;
import com.decawave.argomanager.prefs.AppPreference;
import com.decawave.argomanager.prefs.AppPreferenceAccessor;
import com.decawave.argomanager.prefs.ApplicationMode;
import com.decawave.argomanager.prefs.IhAppPreferenceListener;
import com.decawave.argomanager.prefs.LengthUnit;
import com.decawave.argomanager.ui.fragment.AbstractArgoFragment;
import com.decawave.argomanager.ui.fragment.FragmentType;
import com.decawave.argomanager.ui.view.FloorPlan;
import com.decawave.argomanager.ui.view.GridView;
import com.google.common.base.Preconditions;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.inject.Inject;

import butterknife.BindView;
import rx.functions.Action1;

public class TagPosition extends AbstractArgoFragment {
    private static final boolean DEBUG = false;
    private final String TAG="ExerciseRoomActivity";
    private final NetworkModelManager networkModelManager=null;
    private EnhancedNetworkNodeContainer nodes;
    private NetworksNodesStorage storage = new NetworksNodesStorageImpl();
    private Map<Long, TagAvg> avgNodesById;
    private Map<Long, NetworkNode> nodesById;
    private Position tagPosition_;
    private NetworkNode tagNode_=null;
    public static Map<String,Double> map = new HashMap<>(3);

    //
    private Function<Short, NetworkNode> networkNodeByShortIdResolver;
    private Function<Long, TrackMode> trackModeResolver;
    private Supplier<FloorPlan> floorPlanProvider;
    private Supplier<Boolean> showDebugInfoSupplier;
    private Supplier<Boolean> showGridSupplier;
    private Supplier<Boolean> showAverageSupplier;
    private Supplier<Boolean> highlightIconsistentRangingDistances;

    // dependencies
    private Function<String, Boolean> anchorPresenceResolver;
    private Function<String, Boolean> tagPresenceResolver;
    private LengthUnit lengthUnit;

    //
    private Action1<FloorPlan> floorPlanChangedCallback;

    @BindView(R.id.gridView)
    GridView grid;

    @Inject
    NetworkNodeManager networkNodeManager;
    @Inject
    BlePresenceApi presenceApi;
    @Inject
    AppPreferenceAccessor appPreferenceAccessor;

    @Inject
    PositionObservationManager positionObservationManager;

    private void mkSurePositionObservationRunning() {
        if (positionObservationManager.isObservingPosition()) {
            // cancel possible stop
            positionObservationManager.cancelScheduledPositionObservationStop();
        } else {
            // start observing
            positionObservationManager.startPositionObservation();
        }
    }


    public TagPosition()
    {
        super(FragmentType.GRID);
        injectFrom(IocContext.daCtx);
        mkSurePositionObservationRunning();
    }

    private class TagAvg {
        int idx;
        boolean ready = false;
        float x_avg;
        float y_avg;
        float z_avg;
        float x[]  = new float[10];
        float y[]  = new float[10];
        float z[]  = new float[10];
        Position p = new Position();
        //
        Position lastReportedPosition = null;

        float average(float a[]) {
            float a_avg = 0;
            float div;

            if(ready) {
                div = 10 ;
            } else {
                div = idx;
            }

            if(div == 0) return 0;

            for(int i=0; i<div; i++) {
                a_avg += a[i];
            }
            a_avg /= div;

            return a_avg;
        }

        Position averagep() {
            if (!ready && idx == 0) {
                // there is no value to make average from yet
                if (DEBUG) log.d("averagep() returning null");
                return null;
            }
            p.x = (int) (float) x_avg;
            p.y = (int) (float) y_avg;
            p.z = (int) (float) z_avg;
            if (DEBUG) log.d("averagep() returning p: " + p);
            return p;
        }

        void updatexyz(Position position){
            //
            if (position == null) {
                // there should be a position update, but there is nothing
                if (lastReportedPosition != null) {
                    // pretend that we are moving towards the last reported position
                    updatexyz(lastReportedPosition);
                }
                return;
            }
            x[idx] = position.x;
            y[idx] = position.y;
            z[idx] = position.z;

            idx++;
            if(idx >= 10) {
                ready = true;
                idx = 0;
            }

            //calculate averages
            x_avg = average(x);
            y_avg = average(y);
            z_avg = average(z);

            lastReportedPosition = position;
        }
    }
    private Float storedScale, storedFocalPointX, storedFocalPointY;
    private NetworkModel networkModel;
    private float extraAnimatedZoomFactor = 1f;

    private IhAppPreferenceListener ihActiveNetworkPreferenceListener = (IhAppPreferenceListener) (element, oldValue, newValue) -> {
        if (element == AppPreference.Element.ACTIVE_NETWORK_ID) {
            NetworkModel prevNetworkModel = this.networkModel;
            networkModel = networkNodeManager.getActiveNetwork();
            safeFloorPlanBitmapRecycle(prevNetworkModel);
            configureGridView();
        }
    };

    private void safeFloorPlanBitmapRecycle(NetworkModel activeNetwork) {
        if (activeNetwork != null) {
            FloorPlan floorPlan = activeNetwork.getFloorPlan();
            if (floorPlan != null) {
                floorPlan.recycleBitmap();
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void injectFrom(ArgoComponent injector) {
        injector.inject(this);
    }

    public void load(){
        storage.load(this::onLoadedFromStorage);
    }
    
    public void init(){
    }

    public void initNodeSet(List<NetworkNode> initialNodeSet) {
        nodesById = new HashMap<>();
        avgNodesById = new HashMap<>();

        // initialize the node set
        Stream.of(initialNodeSet).forEach(networkNode -> {
            // create a copy of the node
            networkNode = NodeFactory.newNodeCopy(networkNode);
            // initial fill with position (if any)
            if (networkNode.isTag()) {
                initTagAvg(networkNode);
            }
            // now organize
            Position p = getNodePosition(networkNode);
            if (p!=null && networkNode.isTag()){
                tagNode_ = networkNode;
                tagPosition_ = p;
                Log.d(TAG, "[LOCATION_NODE]: x="+p.x+" y="+p.y+"z="+p.z+"\n");
            }
            //if (p != null) nodesByZAxis.add(networkNode);
            nodesById.put(networkNode.getId(), networkNode);
        });

    }

    private void initTagAvg(NetworkNode networkNode) {
        TagAvg tagAvg = new TagAvg();
        tagAvg.updatexyz(networkNode.extractPositionDirect());
        avgNodesById.put(networkNode.getId(), tagAvg);
    }

    public Position getNodePosition(NetworkNode networkNode) {
        Position p;
        if (networkNode.getType() == NodeType.TAG && showAverageSupplier.get()) {
            p = avgNodesById.get(networkNode.getId()).averagep();
            // this might still return null
        } else {
            p = networkNode.extractPositionDirect();
        }

        return p;
    }

    public Position getTagLocation(){
//        map.put("x_location",0.0);
//        map.put("y_location",0.0);
//        map.put("z_location",0.0);
        networkModel = networkModel;
        if(tagNode_ != null){
            Position p = getNodePosition(tagNode_);
            String tmp = "x: "+p.x+"y: "+p.y+"z: "+p.z;
            Log.d(TAG, "getTagLocation: "+tmp);
            return p;
        }else{
            return null;
        }


    }

    public void configureGridView() {
        if (networkNodeManager.getActiveNetwork() != null) {
            setDependencies(Stream.of(networkNodeManager.getActiveNetworkNodes())
//                            // filter
//                            .filter(this::showNode)
                            // transform to a plain node
                            .map(NetworkNodeEnhanced::asPlainNode)
                            .collect(Collectors.toList()),
                    (nodeId) -> {
                        // lookup by short
                        NetworkNodeEnhanced node = networkNodeManager.getNodeByShortId(nodeId);
                        if (node != null) {
                            return node.asPlainNode();
                        } else {
                            return null;
                        }
                    },
                    (nodeId) -> networkNodeManager.getNodeTrackMode(nodeId),
                    () -> networkNodeManager.getActiveNetwork().getFloorPlan(),
                    () -> appPreferenceAccessor.getShowGridDebugInfo() && appPreferenceAccessor.getApplicationMode() == ApplicationMode.ADVANCED,
                    () -> appPreferenceAccessor.getShowGrid(),
                    () -> appPreferenceAccessor.getShowAverage(),
                    () -> appPreferenceAccessor.getApplicationMode() == ApplicationMode.ADVANCED,
                    presenceApi::isNodePresent,
                    this::drawTag,
                    null,
                    appPreferenceAccessor.getLengthUnit());
        } else {
            setDependencies(Collections.emptyList(), null, null, null,
                    () -> appPreferenceAccessor.getShowGridDebugInfo(),
                    () -> appPreferenceAccessor.getShowGrid(),
                    () -> appPreferenceAccessor.getShowAverage(),
                    () -> false,
                    (bleAddress) -> false,
                    (bleAddress) -> false,
                    null,
                    appPreferenceAccessor.getLengthUnit()
            );
        }
    }

    public void setDependencies(List<NetworkNode> initialNodeSet,
                                Function<Short, NetworkNode> networkNodeByShortIdResolver,
                                Function<Long, TrackMode> trackModeResolver,
                                Supplier<FloorPlan> floorPlanProvider,
                                Supplier<Boolean> showDebugInfoSupplier,
                                Supplier<Boolean> showGridSupplier,
                                Supplier<Boolean> showAverageSupplier,
                                Supplier<Boolean> highlightInconsistentRangingDistances,
                                Function<String, Boolean> anchorPresenceResolver,
                                Function<String, Boolean> tagPresenceResolver,
                                Action1<FloorPlan> floorPlanChangedCallback,
                                LengthUnit lengthUnit) {
        this.networkNodeByShortIdResolver = networkNodeByShortIdResolver;
        this.trackModeResolver = trackModeResolver;
        this.anchorPresenceResolver = anchorPresenceResolver;
        this.tagPresenceResolver = tagPresenceResolver;
        this.lengthUnit = lengthUnit;
        this.floorPlanProvider = floorPlanProvider;
        this.showDebugInfoSupplier = showDebugInfoSupplier;
        this.showGridSupplier = showGridSupplier;
        this.showAverageSupplier = showAverageSupplier;
        this.highlightIconsistentRangingDistances = highlightInconsistentRangingDistances;
        this.floorPlanChangedCallback = floorPlanChangedCallback;
        // initialize node set
        initNodeSet(initialNodeSet);
    }



    private boolean drawTag(String bleAddress) {
        // node is tag, presence API result is not sufficient, the tag needs to be tracked
        return presenceApi.isTagTrackedDirectly(bleAddress) || presenceApi.isTagTrackedViaProxy(bleAddress);
    }

//    public boolean showNode(NetworkNodeEnhanced nne) {
//        NetworkNode plainNode = nne.asPlainNode();
//        return isNodeTracked(plainNode.getType(), plainNode.extractPositionDirect(), nne.getTrackMode(), plainNode.getUwbMode(), appPreferenceAccessor.getShowAverage());
//    }
//
//    public static boolean isNodeTracked(NodeType nodeType,
//                                        Position positionNow,
//                                        TrackMode trackMode,
//                                        UwbMode uwbMode, boolean tagPositionCanBeNull) {
//        return ((tagPositionCanBeNull && nodeType == NodeType.TAG) || positionNow != null)
//                // active
//                && uwbMode == UwbMode.ACTIVE
//                && (
//                // tracked tags
//                (nodeType == NodeType.TAG && trackMode.tracked)
//                        ||
//                        // anchors
//                        (nodeType == NodeType.ANCHOR));
//    }

    private void onLoadedFromStorage(Collection<NetworkNodeEnhanced> nodeList, Collection<NetworkModel>networkList){
        String log = "onNetworksLoaded: " + "nodeList = [" + nodeList + "], networkList = [" + networkList + "]";
        //Toast.makeText(this, log,Toast.LENGTH_SHORT).show();

        Log.d(TAG,log);

        this.nodes = EnhancedNetworkNodeContainerFactory.createContainer(nodeList);

        Stream<NetworkNode> stream = Stream.of(nodes.getNodes(false))
                // compute initial warnings
                .map(NetworkNodeEnhanced::asPlainNode)
                .filter((nn) -> nn.isTag());

        List<NetworkNode> NodeList = stream.toList();

        configureGridView();

        if(NodeList.size()==1){
            TagNode tagNode = (TagNode)NodeList.get(0);

            try{
                Position ps = getNodePosition(tagNode);
                String tmp = "x: "+ps.x+"y: "+ps.y+"z: "+ps.z;
                Log.d(TAG, "x: "+ps.x+"y: "+ps.y+"z: "+ps.z);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

}
