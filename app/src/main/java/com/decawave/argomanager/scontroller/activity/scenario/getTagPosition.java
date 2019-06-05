package com.decawave.argomanager.scontroller.activity.scenario;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.annimon.stream.Stream;
import com.decawave.argo.api.struct.NetworkNode;
import com.decawave.argo.api.struct.Position;
import com.decawave.argo.api.struct.TagNode;
import com.decawave.argomanager.R;
import com.decawave.argomanager.components.EnhancedNetworkNodeContainer;
import com.decawave.argomanager.components.EnhancedNetworkNodeContainerFactory;
import com.decawave.argomanager.components.NetworkModel;
import com.decawave.argomanager.components.NetworkModelManager;
import com.decawave.argomanager.components.NetworkNodeManager;
import com.decawave.argomanager.components.NetworksNodesStorage;
import com.decawave.argomanager.components.impl.NetworksNodesStorageImpl;
import com.decawave.argomanager.components.struct.NetworkNodeEnhanced;
import com.decawave.argomanager.ui.view.GridView;

import java.util.Collection;
import java.util.List;

public class getTagPosition {
//            private Button mButton;
//            private TextView mTextView;
//            private TagNode tag;
//            private final String TAG="ExerciseRoomActivity";
//            private final NetworkModelManager networkModelManager=null;
//            private EnhancedNetworkNodeContainer nodes;
//
//            private NetworksNodesStorage storage = new NetworksNodesStorageImpl();
//
//            @Override
//            protected void onCreate(@Nullable Bundle savedInstanceState) {
//                super.onCreate(savedInstanceState);
//                setContentView(R.layout.activity_exercise_room);
//
//                initView();
//                setListener();
//
//            }
//
//            public void load(){
//                storage.load(this::onLoadedFromStorage);
//            }
//            public class NetworkRunner{
//                private NetworkNodeManager networkNodeManager;
//
//                public void setI(NetworkNodeManager networkNodeManager){
//                    this.networkNodeManager = networkNodeManager;
//                }
//
//                public NetworkNodeEnhanced call(int id){
//                    return networkNodeManager.getNode(id);
//                }
//            }
//
//
//            private void setListener() {
//                mButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        //GridView gv = new GridView(getApplicationContext());
//                        load();
//                    }
//                });
//            }
//
//            private void onLoadedFromStorage(Collection<NetworkNodeEnhanced> nodeList, Collection<NetworkModel>networkList){
//                String log = "onNetworksLoaded: " + "nodeList = [" + nodeList + "], networkList = [" + networkList + "]";
//                //Toast.makeText(this, log,Toast.LENGTH_SHORT).show();
//
//                Log.d(TAG,log);
//
//                this.nodes = EnhancedNetworkNodeContainerFactory.createContainer(nodeList);
//
//                Stream<NetworkNode> stream = Stream.of(nodes.getNodes(false))
//                        // compute initial warnings
//                        .map(NetworkNodeEnhanced::asPlainNode)
//                        .filter((nn) -> nn.isTag());
//
//                List<NetworkNode> NodeList = stream.toList();
//                GridView gv = new GridView(getContext());
//
//                gv.initNodeSet(NodeList);
//
//                if(NodeList.size()==1){
//                    TagNode tagNode = (TagNode)NodeList.get(0);
//
//                    try{
////                GridFragment gridFragment = new GridFragment();
////                gridFragment.configureGridView();
//                        Position ps = gv.getNodePosition(tagNode);
//                        String tmp = "x: "+ps.x+"y: "+ps.y+"z: "+ps.z;
//                        mTextView.setText(tmp);
//                        Log.d(TAG, "x: "+ps.x+"y: "+ps.y+"z: "+ps.z);
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//                }
//
////        Log.d(TAG, NodeList.toString());
////        mTextView.setText(NodeList.toString());
//    }
//
//    private void initView() {
//        mButton = findViewById(R.id.button_exercise_room);
//        mTextView = findViewById(R.id.textView_exercise_room);
//    }
}
