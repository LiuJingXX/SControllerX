package com.decawave.argomanager.scontroller.fragment.tasksfragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.decawave.argomanager.R;
import com.decawave.argomanager.scontroller.activity.tasks.ServiceListActivity;
import com.decawave.argomanager.scontroller.activity.tasks.TasksWorkflowD3Activity;
import com.decawave.argomanager.scontroller.adapter.TaskMineAdapter;
import com.decawave.argomanager.scontroller.constant.Constant;
import com.decawave.argomanager.scontroller.constant.UrlConstant;
import com.decawave.argomanager.scontroller.http.HttpUtil;
import com.decawave.argomanager.scontroller.model.Task;
import com.melnykov.fab.FloatingActionButton;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyTasksFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyTasksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyTasksFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private View view;
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshView;
    private FloatingActionButton floatingActionButton;
    private String[] listViewData;
    BaseAdapter baseAdapter;
    private ArrayAdapter<String> adapter;
    SharedPreferences preferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tasks_mine, container, false);
        preferences = getActivity().getSharedPreferences(Constant.PREFERENCES_USER_INFO, Activity.MODE_PRIVATE);
        //获取发布的任务
        initView();
        showOngoingTasks();
//        initData();
//        initAdapter();
        //设置下拉刷新
        setSwipeRefresh(view);

        //设置voice附着在ListView，跟随ListView滚动滑入滑出,并设置其点击事件
        setfloatingActionButton();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        showOngoingTasks();
        super.onResume();
    }


    private void initView() {
        listView = (ListView) view.findViewById(R.id.lv_tasks_publish);
    }

    //添加bean类数据临时的先添加，再连数据库
    private void initData() {
        listViewData = new String[20];
        for (int i = 0; i < listViewData.length; i++) {
            Date day = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = df.format(day);
            String sTask = "任务" + i + "             " + time;
            listViewData[i] = sTask;
            //item的点击事件，里面可以设置跳转并传值
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Toast.makeText(getActivity(), "第" + i + "行", Toast.LENGTH_LONG).show();
                    //开始传值
                    Intent intent = new Intent(getActivity(), TasksWorkflowD3Activity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("key", i);
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

    private void setSwipeRefresh(View view) {

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
                        showOngoingTasks();
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

    private void setfloatingActionButton() {
        //随着listview显示隐藏
        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab_voice);
        floatingActionButton.attachToListView(listView);
        floatingActionButton.setType(FloatingActionButton.TYPE_MINI);
        //设置其点击事件
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //向本体库发送任务id,请求owls
//                saveTaskInfo();
                //跳转到服务列表页面，调用服务
                Intent intent=new Intent(getActivity(), ServiceListActivity.class);
                //利用上下文开启跳转
                startActivity(intent);
            }
        });

    }

    /**
     * 向本体库发送任务id,请求owls
     */
    private void getOwls() {
        //发送post数据
//        final HashMap<String, String> postData = new HashMap<String, String>();
//        postData.put("serviceId", "1234");

        String serviceURL = UrlConstant.getOntologyServiceURL(UrlConstant.ONTOLOGY_GET_OWLS);
//        String serviceURL = UrlConstant.getAppBackEndServiceURL(UrlConstant.APP_BACK_END_TASKS_GET_OWLS);
        HttpUtil.sendOkHttpRequestByGet(serviceURL, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String sOwlsJson = response.body().string();
//                try {
//                    org.json.JSONObject jsonObject = new org.json.JSONObject(sOwlsJson);
//                    String code= (String) jsonObject.get("code");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
                //向流程引擎发送owls+用户id,返回BPMN，把BPNM解析成List<Node>,Node存储流程节点信息（节点位置，节点名称）
//                saveTaskInfo(sOwlsJson);
            }
        });
    }



    //todo 把这一块放在后台做
    //向流程引擎发送owls+用户id,返回BPMN，把BPNM解析成List<Node>,Node存储流程节点信息（节点位置，节点名称）
    private void getBPMN(String taskId,String sOwlsJson) {
        //发送post数据
        final HashMap<String, String> postData = new HashMap<String, String>();
        postData.put("userId", String.valueOf(preferences.getInt("userId", 0)));
        postData.put("processId", taskId);
        postData.put("owls", sOwlsJson);

        String serviceURL = UrlConstant.getActivitiServiceURL(UrlConstant.ACTIVITI_RUN_BPMN_ENGINE);
        HttpUtil.doPost(serviceURL, postData, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
            }
        });
    }

    //获取任务列表信息（根据用户id 获取所有任务信息：任务表，和运行完成度）
    private void showOngoingTasks() {

        String serviceURL = UrlConstant.getAppBackEndServiceURL(UrlConstant.APP_BACK_END_TASKS_GET_ONGOING_TASKS);
        HttpUtil.sendOkHttpRequestByGet(serviceURL, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //将所有任务信息显示在界面上。去看看众包平台是怎么写这类事件的，学习一下。
                String responceData = response.body().string();
                List<Task> list = JSONObject.parseArray(responceData, Task.class);
                //TODO  JSONObject 出错，{ 而不是 [
                baseAdapter = new TaskMineAdapter(getActivity(), list);
                showList();
            }
        });
    }

    private void showList() {
        ((Activity) getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(baseAdapter);
            }
        });
    }
}
