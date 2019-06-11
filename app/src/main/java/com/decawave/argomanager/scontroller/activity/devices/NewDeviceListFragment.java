package com.decawave.argomanager.scontroller.activity.devices;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.decawave.argomanager.R;
import com.decawave.argomanager.scontroller.adapter.HomeDeviceAdapter;
import com.decawave.argomanager.scontroller.http.HttpUtil;
import com.decawave.argomanager.scontroller.model.HomeDevice;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class NewDeviceListFragment extends Fragment {

    private int firstPosition;
    private int top;
    private ListView listView;
    private String deviceListHASS;
    private String deviceListDB;
    private SwipeRefreshLayout swipeRefreshView;
    private HomeDeviceAdapter homeDeviceAdapter;
    private List<HomeDevice> homeDeviceList = new ArrayList<HomeDevice>();
    private SharedPreferences.Editor editor;
    private SharedPreferences sp;
    private JSONArray showDeviceListJSON = new JSONArray();

    private void initView(View view){
        listView = (ListView) view.findViewById(R.id.list_view);
        swipeRefreshView = (SwipeRefreshLayout) view.findViewById(R.id.swipe_home_device);

        // 设置SharedPreference，保存列表当前位置
        sp=getActivity().getPreferences(MODE_PRIVATE);
        editor=sp.edit();
    }

    private boolean is_urlDevice(String state){
        return (state.indexOf("http") == 0);
    }

    private void setListener(){
        // 监听点击ListView事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final TextView device_name = (TextView)view.findViewById(R.id.tv_home_device_name);
                final TextView device_sate = (TextView)view.findViewById(R.id.tv_home_device_sate);

                String deviceName = device_name.getText().toString();
                String deviceState = device_sate.getText().toString();
                Intent intent = new Intent(getActivity(), NewDeviceDetailActivity.class);
                intent.putExtra("device_name",deviceName);
                intent.putExtra("device_state", deviceState);

                try{
                    // 获取当前点击的设备信息，跳过没有entity的部分
                    JSONArray jarr = showDeviceListJSON;//new JSONArray(deviceList);
                    JSONObject result = jarr.getJSONObject(position);
                    intent.putExtra("device_detail", result.toString());

                    // 获取特殊设备，通过url跳转到第三方页面
                    String entity_id= result.getString("entity_id");
                    if(is_urlDevice(deviceState)){
                        intent.putExtra("layout","webview");
                        intent.putExtra("url", deviceState);
                    }else{
                        String strHead = entity_id.split("\\.")[0];
                        intent.putExtra("layout",strHead);
//                        // Test
//                        intent.putExtra("layout","webview");
//                        intent.putExtra("url", "http://10.131.253.117:8123/api/states");
                    }

                }catch(JSONException e){
                    e.printStackTrace();
                }

                startActivity(intent);

                //Toast.makeText(getActivity(),"device_name=" + device_name.getText().toString()+",device_sate="+device_sate.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState== AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
                    firstPosition=listView.getFirstVisiblePosition();
                }
                View v=listView.getChildAt(0);
                top=v.getTop();

                editor.putInt("Device_ListView_FirstPosition", firstPosition);
                editor.putInt("Device_ListView_TopPosition", top);
                editor.commit();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }
    private void initHomeDevice(){
        final String TAG = "[initHomeDevice]";
        getDeviceListHASS();
        getDeviceListDB();

        try {
            int count_i = 0;
            while (count_i<4&&(deviceListHASS==null || deviceListHASS.isEmpty())){
                count_i++;
                Thread.sleep(500);
            }
            if(deviceListHASS==null || deviceListHASS.isEmpty()){
                Toast.makeText(getActivity(),"Failed to connect HASS",Toast.LENGTH_SHORT).show();
                throw new InterruptedException("Failed to connect to HASS");
            }
            if(deviceListDB==null||deviceListDB.isEmpty()){
                Toast.makeText(getActivity(),"Failed to connect Device Database",Toast.LENGTH_SHORT).show();
                throw new InterruptedException("Failed to connect to Device Database");
            }
            // Parse listString from HASS
            JSONArray jsonArray = new JSONArray(deviceListHASS);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject result = jsonArray.getJSONObject(i);

                // GET attributes,entity_id and friendly_name
                String attributes = result.getString("attributes");
                String entity_id = result.getString("entity_id");
                String state = result.getString("state");
                String friendly_name;
                try {
                    JSONObject jobj = new JSONObject(attributes);
                    friendly_name = jobj.getString("friendly_name");
                } catch (JSONException e) {
                    Log.d(TAG, "Device No Friendly Name:" + entity_id);
                   friendly_name = "";
                }

                // if entity_id contains "group" then add to list else drop
                if (entity_id.contains("xiaomi")){
                    String DeviceListString = "{\"source\":\"HASS\"," +
                            "\"deviceId\":\""+entity_id+"\"," +
                            "\"deviceName\":\""+friendly_name+"\"," +
                            "\"deviceState\":\""+state+"\"" +
                            "}";

                    JSONObject DeviceListJSON = new JSONObject(DeviceListString);
                    showDeviceListJSON.put(DeviceListJSON);
                    AppendDeviceList(entity_id, friendly_name, state);
                }


            }

            // Parse listString from DB
            jsonArray = new JSONArray(deviceListDB);
            Log.d(TAG, "initHomeDevice: "+jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject result = jsonArray.getJSONObject(i);

                // GET deviceInfo,locationX,locationY and imageurl
                // "deviceInfo": "{\"商品名称\":\"罗技无线鼠标M170\",\"商品编号\":\"2177845\",\"产品类型\":\"便携鼠标\"}",
                //String deviceInfo = result.getString("deviceInfo");
                String deviceId = result.getString("id");
                String locationX = result.getString("locationX");
                String locationY = result.getString("locationY");
                String imageUrl = result.getString("url");

                String deviceName = result.getString("deviceName");
                String deviceType = result.getString("deviceType");

                String DeviceListString = "{\"source\":\"Database\"," +
                        "\"deviceId\":\""+deviceId+"\"," +
                        "\"deviceName\":\""+deviceName+"\"," +
                        "\"deviceType\":\""+deviceType+"\"" +
                        "\"locationX\":\"" +locationX+"\""+
                        "\"locationY\":\"" +locationY+"\""+
                        "}";

                JSONObject DeviceListJSON = new JSONObject(DeviceListString);
                showDeviceListJSON.put(DeviceListJSON);
                AppendDeviceList("", deviceName, deviceType);
            }


        }catch (JSONException e){
            e.printStackTrace();
        }catch (InterruptedException e){
            e.printStackTrace();
        }

    }

    private void getDeviceListDB() {
//        //请求url	http://192.168.1.106:8080/device/getAllDeviceList
//        final String TAG = "[getDeviceListDB]";
//        String url = "http://192.168.1.106:8080/device/getAllDeviceList";
//
//        HttpUtil httpUtil = new HttpUtil();
//        httpUtil.doGet(url, new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.e(TAG, "onFailure: ", e);
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                deviceListDB = response.body().string();
//            }
//        });
        deviceListDB = "[{\"id\":30,\"ownerType\":null,\"ownerId\":0,\"name\":\"罗技M275\",\"deviceType\":\"#电脑、办公#外设产品#鼠标#办公鼠标#\",\"deviceCompany\":\"罗技（Logitech）\",\"deviceVersion\":null,\"deviceInfo\":\"{\\\"名称\\\":\\\"罗技M275\\\",\\\"品牌\\\":\\\"罗技（Logitech）\\\",\\\"类别\\\":\\\"#电脑、办公#外设产品#鼠标#办公鼠标#\\\"}\",\"locationX\":null,\"locationY\":null,\"url\":\"http://10.141.221.88:12321/upload/files/20190522/1558526938242.jpg\",\"addUserId\":5,\"addTime\":\"2019-05-22T12:09:17.000+0000\"},{\"id\":31,\"ownerType\":null,\"ownerId\":0,\"name\":\"罗技M275\",\"deviceType\":\"#电脑、办公#外设产品#鼠标#办公鼠标#\",\"deviceCompany\":\"罗技（Logitech）\",\"deviceVersion\":null,\"deviceInfo\":\"{\\\"名称\\\":\\\"罗技M275\\\",\\\"品牌\\\":\\\"罗技（Logitech）\\\",\\\"类别\\\":\\\"#电脑、办公#外设产品#鼠标#办公鼠标#\\\"}\",\"locationX\":null,\"locationY\":null,\"url\":\"http://10.141.221.88:12321/upload/files/20190522/1558527101302.jpg\",\"addUserId\":5,\"addTime\":\"2019-05-22T12:12:11.000+0000\"},{\"id\":32,\"ownerType\":null,\"ownerId\":0,\"name\":\"富光拾喜保温杯保温壶男女士水杯学生情侣便携杯子大容量户外运动车载不锈钢茶杯 黑色 450ml 450ml\",\"deviceType\":\"#厨具#水具酒具#保温杯#\",\"deviceCompany\":\"富光拾喜（FUGURNG BESTJOY）\",\"deviceVersion\":null,\"deviceInfo\":\"{\\\"名称\\\":\\\"富光拾喜保温杯保温壶男女士水杯学生情侣便携杯子大容量户外运动车载不锈钢茶杯 黑色 450ml 450ml\\\",\\\"品牌\\\":\\\"富光拾喜（FUGURNG BESTJOY）\\\",\\\"类别\\\":\\\"#厨具#水具酒具#保温杯#\\\"}\",\"locationX\":null,\"locationY\":null,\"url\":\"http://10.141.221.88:12321/upload/files/20190522/1558526951994.jpg\",\"addUserId\":5,\"addTime\":\"2019-05-22T12:13:31.000+0000\"},{\"id\":33,\"ownerType\":null,\"ownerId\":0,\"name\":\"罗技M275\",\"deviceType\":\"#电脑、办公#外设产品#鼠标#办公鼠标#\",\"deviceCompany\":\"罗技（Logitech）\",\"deviceVersion\":null,\"deviceInfo\":\"{\\\"名称\\\":\\\"罗技M275\\\",\\\"品牌\\\":\\\"罗技（Logitech）\\\",\\\"类别\\\":\\\"#电脑、办公#外设产品#鼠标#办公鼠标#\\\"}\",\"locationX\":null,\"locationY\":null,\"url\":\"http://10.141.221.88:12321/upload/files/20190522/1558527256602.jpg\",\"addUserId\":5,\"addTime\":\"2019-05-22T12:14:29.000+0000\"},{\"id\":34,\"ownerType\":null,\"ownerId\":0,\"name\":\"家庭网关2代温湿度门窗传感器开关空调伴侣 米家智能插座(增强版)\",\"deviceType\":\"#家装建材#电工电料#开关插座#\",\"deviceCompany\":\"骁熊（XIAOXIONG）\",\"deviceVersion\":null,\"deviceInfo\":\"{\\\"名称\\\":\\\"家庭网关2代温湿度门窗传感器开关空调伴侣 米家智能插座(增强版)\\\",\\\"品牌\\\":\\\"骁熊（XIAOXIONG）\\\",\\\"类别\\\":\\\"#家装建材#电工电料#开关插座#\\\"}\",\"locationX\":null,\"locationY\":null,\"url\":\"http://10.141.221.88:12321/upload/files/20190522/1558527271207.jpg\",\"addUserId\":5,\"addTime\":\"2019-05-22T12:15:16.000+0000\"}]";
    }

    private void getDeviceListHASS(){
        // url = http://192.168.1.8:8123/api/states
        // url = http://10.131.253.117:8123/api/states
        // -H Content-Type=application/json
        // -H X-HA-Access=123456
        // GET
        final String TAG = "[getDeviceList]";
        String url = "http://10.131.253.117:8123/api/states";

        HttpUtil httpUtil = new HttpUtil();
        httpUtil.getHASSApiState(url, "123456", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: ", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                deviceListHASS = response.body().string();
                //Log.d(TAG, "onResponse: " + deviceList);

            }
        });
    }

    private void AppendDeviceList(String entity_id,String friendly_name, String state) {
        // sensor group switch
        //entity_id.split(".");
        HomeDevice homeDevice = new HomeDevice(R.drawable.home_saodijiqiren,friendly_name,state);
        homeDeviceList.add(homeDevice);
    }

    private void setSwipeRefresh(View view){

        // 下拉时触发SwipeRefreshLayout的下拉动画，动画完毕之后就会回调这个方法
        swipeRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                homeDeviceList.clear();
                initHomeDevice();
                homeDeviceAdapter.notifyDataSetChanged();
                swipeRefreshView.setRefreshing(false);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_device_list, container, false);
        initView(view);

        //获取传感器等物理设备数据
        initHomeDevice();

        homeDeviceAdapter = new HomeDeviceAdapter(getActivity(),
                R.layout.item_home_deivce, homeDeviceList);
        listView.setAdapter(homeDeviceAdapter);
        //设置监听
        setListener();
        //设置下拉刷新
        setSwipeRefresh(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        firstPosition=sp.getInt("Device_ListView_FirstPosition", 0);
        top=sp.getInt("Device_ListView_TopPosition", 0);
        if(firstPosition!=0&&top!=0){
            listView.setSelectionFromTop(firstPosition, top);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        editor.remove("Device_ListView_FirstPosition");
        editor.remove("Device_ListView_TopPosition");
        editor.commit();
    }
}
