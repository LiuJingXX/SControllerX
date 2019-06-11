package com.decawave.argomanager.scontroller.activity.devices;

import android.content.Intent;
import android.net.http.SslError;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.decawave.argomanager.R;
import com.decawave.argomanager.scontroller.ScMainActivity;
import com.decawave.argomanager.scontroller.fragment.devicefragment.details.NewDeviceGroupFragment;
import com.decawave.argomanager.scontroller.fragment.devicefragment.details.NewDeviceNormalFragment;
import com.decawave.argomanager.scontroller.fragment.devicefragment.details.NewDeviceSensorFragment;
import com.decawave.argomanager.scontroller.fragment.devicefragment.details.NewDeviceSwitchFragment;
import com.decawave.argomanager.scontroller.fragment.devicefragment.details.NewDeviceWebviewFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NewDeviceDetailActivity extends AppCompatActivity {

    private final String TAG = "[NewDeviceDetailActivity]";


    // Fragment
    private NewDeviceWebviewFragment webviewFragment;
    private NewDeviceGroupFragment groupFragment;
    private NewDeviceSensorFragment sensorFragment;
    private NewDeviceSwitchFragment switchFragment;
    private NewDeviceNormalFragment normalFragment;

//    private void initView(){
//        mButton = findViewById(R.id.button_detail_return);
//        mTextView_detail = findViewById(R.id.textView_detail);
//
//        mEditText_detail_deviceName = findViewById(R.id.editText_detail_deviceName);
//        mEditText_detail_deviceState = findViewById(R.id.editText_detail_deviceState);
//        mEditText_detail_deviceID = findViewById(R.id.editText_detail_deviceID);
//        mEditText_detail_devicePosition = findViewById(R.id.editText_detail_devicePosition);
//
//    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 获取参数
        Intent intent = getIntent();
        int position = intent.getIntExtra("position",0);
        String device_name = intent.getStringExtra("device_name");
        String device_state = intent.getStringExtra("device_state");
        String device_detail = intent.getStringExtra("device_detail");
        String layout = intent.getStringExtra("layout");
        String entity_id;
        try{
            JSONObject result = null;
            try {
                result = new JSONObject(device_detail);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            entity_id = result.getString("entity_id");
        }catch (JSONException e){
            e.printStackTrace();
            return;
        }

        setContentView(R.layout.activity_device_detail);
        Bundle bundle = new Bundle();

        // 选择需要加载的布局
        switch(layout){
            case "webview":
                String url = intent.getStringExtra("url");
                webviewFragment = new NewDeviceWebviewFragment();
                bundle.putString("url",url);
                webviewFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().add(R.id.fl_container, webviewFragment).commitAllowingStateLoss();

                break;
            case "group":
                groupFragment = new NewDeviceGroupFragment();
                bundle.putString("device_detail",device_detail);
                bundle.putString("device_name",device_name);
                bundle.putString("device_state",device_state);
                bundle.putString("entity_id",entity_id);
                groupFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().add(R.id.fl_container, groupFragment).commitAllowingStateLoss();
                break;
            case "normal":
                normalFragment = new NewDeviceNormalFragment();
                //bundle.putString("device_detail",device_detail);
//                bundle.putString("device_id",device_id);
//                bundle.putString("device_name",device_name);
//                bundle.putString("device_type",device_type);
//                bundle.putString("locationX",locationX);
//                bundle.putString("locationY",locationY);
                normalFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().add(R.id.fl_container, normalFragment).commitAllowingStateLoss();
                break;
            case "switch":
                break;
            case "sensor":
                break;
            default:break;

        }


    }
}
