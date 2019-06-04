package com.decawave.argomanager.scontroller.activity.tasks;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.decawave.argomanager.R;
import com.decawave.argomanager.scontroller.constant.UrlConstant;

public class TasksWorkflowD3Activity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_workflow_d3);
        //设置actionbar
        ActionBar supportActionBar=getSupportActionBar();
        supportActionBar.setHomeButtonEnabled(true);//设置主键按钮可以点击
        supportActionBar.setDisplayHomeAsUpEnabled(true);//设置显示返回图标

        //设置webview内容，加载流程图
        Intent intent =getIntent();
        int key = (int) intent.getSerializableExtra("key");

        webView = (WebView)findViewById(R.id.wv_tasks_workflow_d3);

        webView.getSettings().setJavaScriptEnabled(true);
        //支持屏幕缩放
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        //不显示webview缩放按钮
        webView.getSettings().setDisplayZoomControls(false);
        String serviceURL = UrlConstant.getAppBackEndServiceURL("?taskId="+key);
        webView.loadUrl(serviceURL);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        //调用mqtt
//        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if(EventBus.getDefault().isRegistered(this)) {
//            EventBus.getDefault().unregister(this);
//        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void getMqttMessage2(MQTTMessage mqttMessage){
//        Log.i(MQTTService.TAG,"TaskWorkflowD3Activity收到消息:"+mqttMessage.getMessage());
//        Toast.makeText(this,"TaskWorkflowD3Activity收到消息:"+mqttMessage.getMessage(), Toast.LENGTH_SHORT).show();
//    }
}
