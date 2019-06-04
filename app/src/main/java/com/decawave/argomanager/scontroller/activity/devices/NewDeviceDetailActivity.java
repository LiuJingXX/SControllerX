package com.decawave.argomanager.scontroller.activity.devices;

import android.content.Intent;
import android.net.http.SslError;
import android.os.Build;
import android.support.annotation.Nullable;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NewDeviceDetailActivity extends AppCompatActivity {

    private final String TAG = "[NewDeviceDetailActivity]";
    private Button mButton;
    private WebView mWebView;
    private TextView mTextView_detail;
    private EditText mEditText_detail_deviceName;
    private EditText mEditText_detail_deviceState;
    private EditText mEditText_detail_deviceID;
    private EditText mEditText_detail_devicePosition;


    private void initView(){
        mButton = findViewById(R.id.button_detail_return);
        mTextView_detail = findViewById(R.id.textView_detail);

        mEditText_detail_deviceName = findViewById(R.id.editText_detail_deviceName);
        mEditText_detail_deviceState = findViewById(R.id.editText_detail_deviceState);
        mEditText_detail_deviceID = findViewById(R.id.editText_detail_deviceID);
        mEditText_detail_devicePosition = findViewById(R.id.editText_detail_devicePosition);


    }

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


        // 选择需要加载的布局
        if("webview".equals(layout)){
            String url = intent.getStringExtra("url");

            setContentView(R.layout.activity_device_detail_webview);
            mWebView = findViewById(R.id.device_detail_webview);
            mWebView.getSettings().setJavaScriptEnabled(true);
            //支持屏幕缩放
            mWebView.getSettings().setSupportZoom(true);
            mWebView.getSettings().setBuiltInZoomControls(true);
            //不显示webview缩放按钮
            mWebView.getSettings().setDisplayZoomControls(false);
            mWebView.getSettings().setLoadWithOverviewMode(true);
            mWebView.loadUrl(url);
            mWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return super.shouldOverrideUrlLoading(view, url);
                }
                @Override
                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mWebView.getSettings()
                                .setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
                    }else{
                        try{
                            Method setMixedContentMode = WebSettings.class.getMethod("setMixedContentMode", int.class);
                            try {
                                setMixedContentMode.invoke(mWebView.getSettings(), WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE); // 2 = MIXED_CONTENT_COMPATIBILITY_MODE
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                            Log.d("WebSettings", "Successfully set MIXED_CONTENT_COMPATIBILITY_MODE");
                        }catch (NoSuchMethodException e){
                            Log.e("WebSettings", "Error getting setMixedContentMode method");
                        }catch (IllegalAccessException e){
                            Log.e("WebSettings", "Error getting setMixedContentMode method");
                        }
                    }
                }
            });
            mWebView.onResume();
        }else{
            setContentView(R.layout.activity_device_detail);
            initView();
            mTextView_detail.setText(device_detail);
            mEditText_detail_deviceName.setText(device_name);
            mEditText_detail_deviceState.setText(device_state);
            mEditText_detail_deviceID.setText(entity_id);
            mEditText_detail_devicePosition.setText("(null,null)");

            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 返回主界面
                    // Toast.makeText(NewDeviceDetailActivity.this,"To be continue...",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(NewDeviceDetailActivity.this, ScMainActivity.class);
                    intent.putExtra("fragment_id",0);
                    startActivity(intent);
                }
            });
        }


    }
}
