package com.decawave.argomanager.scontroller.fragment.devicefragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.decawave.argomanager.R;
import com.decawave.argomanager.scontroller.adapter.HomeDeviceAdapter;
import com.decawave.argomanager.scontroller.model.HomeDevice;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DeviceListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DeviceListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeviceListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshView;
    private List<HomeDevice> homeDeviceList = new ArrayList<HomeDevice>();
    private HomeDeviceAdapter homeDeviceAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_device_list, container, false);

        //获取传感器等物理设备数据
        initHomeDevice();
        homeDeviceAdapter = new HomeDeviceAdapter(getActivity(),
                R.layout.item_home_deivce, homeDeviceList);
        listView = (ListView) view.findViewById(R.id.list_view);
        listView.setAdapter(homeDeviceAdapter);

        //设置下拉刷新
        setSwipeRefresh(view);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
    }


    private void initHomeDevice(){
        HomeDevice homeDevice1 =new HomeDevice(R.drawable.home_saodijiqiren,"扫地机器人","正在运行");
        homeDeviceList.add(homeDevice1);
        HomeDevice homeDevice2 =new HomeDevice(R.drawable.home_saodijiqiren,"扫地机器人","正在运行");
        homeDeviceList.add(homeDevice2);
        HomeDevice homeDevice3 =new HomeDevice(R.drawable.home_saodijiqiren,"扫地机器人","正在运行");
        homeDeviceList.add(homeDevice3);
    }





    private void setSwipeRefresh(View view){
        swipeRefreshView = (SwipeRefreshLayout) view.findViewById(R.id.swipe_home_device);
        // 设置颜色属性的时候一定要注意是引用了资源文件还是直接设置16进制的颜色，因为都是int值容易搞混
        // 设置下拉进度的背景颜色，默认就是白色的
        swipeRefreshView.setProgressBackgroundColorSchemeResource(android.R.color.white);
        // 设置下拉进度的主题颜色
        swipeRefreshView.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.yellow);

        // 下拉时触发SwipeRefreshLayout的下拉动画，动画完毕之后就会回调这个方法
        swipeRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                // 开始刷新，设置当前为刷新状态
                //swipeRefreshLayout.setRefreshing(true);

                // 这里是主线程
                // 一些比较耗时的操作，比如联网获取数据，需要放到子线程去执行
                // TODO 获取数据
                final Random random = new Random();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        HomeDevice homeDevice1 =new HomeDevice(R.drawable.home_xiaomidfb,"小米电饭煲","暂停运行");
                        homeDeviceList.add(homeDevice1);
                        homeDeviceAdapter.notifyDataSetChanged();

                        Toast.makeText(getActivity(), "刷新了一条数据", Toast.LENGTH_SHORT).show();

                        // 加载完数据设置为不刷新状态，将下拉进度收起来
                        swipeRefreshView.setRefreshing(false);
                    }
                }, 1200);

                // System.out.println(Thread.currentThread().getName());

                // 这个不能写在外边，不然会直接收起来
                //swipeRefreshLayout.setRefreshing(false);
            }
        });

    }
}
