package com.decawave.argomanager.scontroller;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.decawave.argomanager.R;
import com.decawave.argomanager.scontroller.activity.tasks.TasksWorkflowD3Activity;
import com.decawave.argomanager.scontroller.constant.Constant;
import com.decawave.argomanager.scontroller.constant.UrlConstant;
import com.decawave.argomanager.scontroller.fragment.subfragment.HomeFragment;
import com.decawave.argomanager.scontroller.fragment.subfragment.PersonFragment;
import com.decawave.argomanager.scontroller.fragment.subfragment.TaskFragment;
import com.decawave.argomanager.scontroller.http.HttpUtil;
import com.decawave.argomanager.scontroller.service.MessageEvent;
import com.decawave.argomanager.scontroller.util.NotificationUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ScMainActivity extends FragmentActivity {

    //fragmet嵌入在这里
    private FrameLayout main_frameLayout;
    //Fragment管理
    private FragmentManager fragmentManager;
    //Fragment类
    private HomeFragment homeFragment;
    private PersonFragment personFragment;
    private TaskFragment taskFragment;
    private LocationManager locationManager;
    private String locationProvider;       //位置提供器

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            hideAllFragment(transaction);
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if (homeFragment == null) {
                        homeFragment = new HomeFragment();
                        transaction.add(R.id.main_fragment_container, homeFragment);
                    } else {
                        transaction.show(homeFragment);
                    }
                    break;
                case R.id.navigation_tasks:
                    if (taskFragment == null) {
                        taskFragment = new TaskFragment();
                        transaction.add(R.id.main_fragment_container, taskFragment);
                    } else {
                        transaction.show(taskFragment);
                    }
                    break;
                case R.id.navigation_settings:
                    if (personFragment == null) {
                        personFragment = new PersonFragment();
                        transaction.add(R.id.main_fragment_container, personFragment);
                    } else {
                        transaction.show(personFragment);
                    }
                    break;
            }
            transaction.commit();
            return true;
        }
    };

    //隐藏所有Fragment
    public void hideAllFragment(FragmentTransaction transaction) {
        if (homeFragment != null) {
            transaction.hide(homeFragment);
        }
        if (personFragment != null) {
            transaction.hide(personFragment);
        }
        if (taskFragment != null) {
            transaction.hide(taskFragment);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainsc);

        //Todo 这目前没有用
//        Intent serviceIntent = new Intent(MainActivity.this, HeartBeatService.class);
//        startService(serviceIntent);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //碎片管理
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        //初始时加入homeFragment碎片
        homeFragment = new HomeFragment();
        transaction.add(R.id.main_fragment_container, homeFragment);
        transaction.commit();

        //调用mqtt
        EventBus.getDefault().register(this);
//        Todo 开启Mqtt
//        startService(new Intent(this, MQTTService.class));

        //获取定位
        getLocation(this);
    }

    @Override
    protected void onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getMqttMessage1(MessageEvent messageEvent) {
        int eventType = messageEvent.getEventType();
        if (eventType == Constant.EVENT_TASK_MINE_NODE_COMPLETE) {
            int taskId = messageEvent.getTaskId();
            String nodeId = messageEvent.getNodeId();
            String contentTitle = "任务流程更新！";
            String contentText = "任务" + taskId + "已完成至节点" + nodeId;
            int notificationId = taskId * 10 + eventType;//唯一标识该通知，之后用于唯一表示这个任务的pendingintent
            Intent intent = new Intent(this, TasksWorkflowD3Activity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("key", taskId);
            intent.putExtras(bundle);
            NotificationUtils.initNotification(this, contentTitle, contentText, intent, notificationId);
        } else if (eventType == Constant.EVENT_TASK_CROWDSOURCING_NEW) {

        } else if (eventType == Constant.EVENT_TASK_SPECIFY_NEW) {

        }
//        Log.i(MQTTService.TAG, "MainActivity收到消息:" + messageEvent.getMessage());
//        Toast.makeText(this, "MainActivity收到消息:" + messageEvent.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void getLocation(Context context) {
        //1.获取位置管理器
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //2.获取位置提供器，GPS或是NetWork
        List<String> providers = locationManager.getProviders(true);
        if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是网络定位
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS定位
            locationProvider = LocationManager.GPS_PROVIDER;
        } else {
            Toast.makeText(this, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
            return;
        }

        //3.获取上次的位置，一般第一次运行，此值为null
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationProvider);
        if (location!=null){
            saveLocation(location);
        }else{
            // 监视地理位置变化，第二个和第三个参数分别为更新的最短时间minTime和最短距离minDistace
            locationManager.requestLocationUpdates(locationProvider, 0, 0,mListener);
        }
    }

    private void saveLocation(Location location){
        //发送post数据
        final HashMap<String, String> postData = new HashMap<String, String>();
        postData.put("longitude", String.valueOf(location.getLongitude()));
        postData.put("latitude", String.valueOf(location.getLatitude()));

        String serviceURL = UrlConstant.getAppBackEndServiceURL(UrlConstant.APP_BACK_END_USER_SAVE_LOCATION);
        HttpUtil.doPost(serviceURL,postData, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }

    LocationListener mListener = new LocationListener() {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
        @Override
        public void onProviderEnabled(String provider) {
        }
        @Override
        public void onProviderDisabled(String provider) {
        }
        // 如果位置发生变化，重新显示
        @Override
        public void onLocationChanged(Location location) {
            saveLocation(location);
        }
    };
}
