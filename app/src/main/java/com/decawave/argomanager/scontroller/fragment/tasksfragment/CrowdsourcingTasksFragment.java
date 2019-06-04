package com.decawave.argomanager.scontroller.fragment.tasksfragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.decawave.argomanager.R;
import com.decawave.argomanager.scontroller.activity.tasks.TasksWorkflowActivity;

import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CrowdsourcingTasksFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CrowdsourcingTasksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CrowdsourcingTasksFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private View view;
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshView;
    private String[] listViewData;
    private ArrayAdapter<String> adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tasks_crowdsourcing, container, false);

        //获取发布的任务
        initView();
        initData();
        initAdapter();
        //设置下拉刷新
        setSwipeRefresh(view);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
    }

    private void initView() {
        listView = (ListView) view.findViewById(R.id.lv_tasks_publish);
    }

    //添加bean类数据临时的先添加，再连数据库
    private void initData() {
        listViewData = new String[20];
        for (int i = 0; i < listViewData.length; i++) {
            Date day=new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time=df.format(day);
            String sTask = "任务"+i+"             "+time;
            listViewData[i]=sTask;
            //item的点击事件，里面可以设置跳转并传值
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Toast.makeText(getActivity(), "第" + i + "行", Toast.LENGTH_LONG).show();
                    //开始传值
                    Intent intent=new Intent(getActivity(), TasksWorkflowActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("key",i);
                    intent.putExtras(bundle);
                    //利用上下文开启跳转
                    startActivity(intent);
                }
            });
        }
    }

    private void initAdapter() {
        adapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_list_item_1, listViewData);
        listView.setAdapter(adapter);
    }

    private void setSwipeRefresh(View view){

        swipeRefreshView = (SwipeRefreshLayout) view.findViewById(R.id.swipe_tasks_publish);
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
//                        HomeDevice homeDevice1 =new HomeDevice(R.drawable.home_xiaomidfb,"小米电饭煲","暂停运行");
//                        homeDeviceList.add(homeDevice1);
//                        homeDeviceAdapter.notifyDataSetChanged();

                        Toast.makeText(getActivity(), "刷新成功", Toast.LENGTH_SHORT).show();

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
