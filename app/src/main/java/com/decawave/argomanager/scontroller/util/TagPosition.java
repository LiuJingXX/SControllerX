package com.decawave.argomanager.scontroller.util;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.decawave.argo.api.struct.NetworkNode;
import com.decawave.argo.api.struct.NodeType;
import com.decawave.argo.api.struct.Position;
import com.decawave.argo.api.struct.TagNode;
import com.decawave.argo.api.struct.UwbMode;
import com.decawave.argomanager.R;
import com.decawave.argomanager.components.BlePresenceApi;
import com.decawave.argomanager.components.EnhancedNetworkNodeContainer;
import com.decawave.argomanager.components.EnhancedNetworkNodeContainerFactory;
import com.decawave.argomanager.components.NetworkModel;
import com.decawave.argomanager.components.NetworkModelManager;
import com.decawave.argomanager.components.NetworkNodeManager;
import com.decawave.argomanager.components.NetworksNodesStorage;
import com.decawave.argomanager.components.impl.NetworksNodesStorageImpl;
import com.decawave.argomanager.components.struct.NetworkNodeEnhanced;
import com.decawave.argomanager.components.struct.TrackMode;
import com.decawave.argomanager.prefs.AppPreferenceAccessor;
import com.decawave.argomanager.prefs.ApplicationMode;
import com.decawave.argomanager.ui.view.GridView;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;

public class TagPosition {
    private final String TAG="ExerciseRoomActivity";
    private final NetworkModelManager networkModelManager=null;
    private EnhancedNetworkNodeContainer nodes;
    private NetworksNodesStorage storage = new NetworksNodesStorageImpl();

    @BindView(R.id.gridView)
    GridView grid;

    @Inject
    NetworkNodeManager networkNodeManager;
    @Inject
    BlePresenceApi presenceApi;
    @Inject
    AppPreferenceAccessor appPreferenceAccessor;

    public void load(){
        storage.load(this::onLoadedFromStorage);
    }

    //    public class NetworkRunner{
//        private NetworkNodeManager networkNodeManager;
//
//        public void setI(NetworkNodeManager networkNodeManager){
//            this.networkNodeManager = networkNodeManager;
//        }
//
//        public NetworkNodeEnhanced call(int id){
//            return networkNodeManager.getNode(id);
//        }
//    }

    public void configureGridView() {
        if (networkNodeManager.getActiveNetwork() != null) {
            grid.setDependencies(Stream.of(networkNodeManager.getActiveNetworkNodes())
                            // filter
                            .filter(this::showNode)
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
            grid.setDependencies(Collections.emptyList(), null, null, null,
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
        //adjustViewsVisibility();
    }
    private boolean drawTag(String bleAddress) {
        // node is tag, presence API result is not sufficient, the tag needs to be tracked
        return presenceApi.isTagTrackedDirectly(bleAddress) || presenceApi.isTagTrackedViaProxy(bleAddress);
    }

    public boolean showNode(NetworkNodeEnhanced nne) {
        NetworkNode plainNode = nne.asPlainNode();
        return isNodeTracked(plainNode.getType(), plainNode.extractPositionDirect(), nne.getTrackMode(), plainNode.getUwbMode(), appPreferenceAccessor.getShowAverage());
    }

    public static boolean isNodeTracked(NodeType nodeType,
                                        Position positionNow,
                                        TrackMode trackMode,
                                        UwbMode uwbMode, boolean tagPositionCanBeNull) {
        return ((tagPositionCanBeNull && nodeType == NodeType.TAG) || positionNow != null)
                // active
                && uwbMode == UwbMode.ACTIVE
                && (
                // tracked tags
                (nodeType == NodeType.TAG && trackMode.tracked)
                        ||
                        // anchors
                        (nodeType == NodeType.ANCHOR));
    }

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
        //grid.initNodeSet(NodeList);

        if(NodeList.size()==1){
            TagNode tagNode = (TagNode)NodeList.get(0);

            try{
                Position ps = grid.getNodePosition(tagNode);
                String tmp = "x: "+ps.x+"y: "+ps.y+"z: "+ps.z;
                Log.d(TAG, "x: "+ps.x+"y: "+ps.y+"z: "+ps.z);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

}
