package com.decawave.argomanager.scontroller.activity.devices;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.decawave.argomanager.R;
import com.decawave.argomanager.scontroller.constant.UrlConstant;
import com.decawave.argomanager.scontroller.http.HttpUtil;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

public class ManualAddDeviceActivity extends AppCompatActivity {
    private EditText device_type;
    private EditText device_company;
    private EditText device_name;
    private Button button_manual_add_device_confirm;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_add_device);
        Intent intent = getIntent();
        url= (String) intent.getSerializableExtra("url");
        initView();
    }

    private void initView() {
        device_type = (EditText) findViewById(R.id.device_type);
        device_company = (EditText) findViewById(R.id.device_company);
        device_name = (EditText) findViewById(R.id.device_name);
        button_manual_add_device_confirm = (Button) findViewById(R.id.button_manual_add_device_confirm);
        button_manual_add_device_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadDevice();
            }
        });
    }

    private void uploadDevice() {
        String deviceName=device_name.getText().toString();
        String deviceCompany=device_company.getText().toString();
        String deviceType=device_type.getText().toString();
        final HashMap<String, String> postData = new HashMap<String, String>();
        String serviceURL = UrlConstant.getAppBackEndServiceURL(UrlConstant.APP_BACK_END_DEVICE_SAVE_DEVICE_INFO);
        postData.put("url", url);
        String deivceInfo= "{\"名称\":\""+deviceName+"\",\"品牌\":\""+deviceCompany+"\",\"类别\":\""+deviceType+"\"}";
        postData.put("deviceInfo", deivceInfo);

        HttpUtil.doPost(serviceURL, postData, new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Intent intent = new Intent(ManualAddDeviceActivity.this,NewDeviceManageActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("url","21");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
