package com.decawave.argomanager.scontroller.activity.devices;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.decawave.argomanager.R;
import com.decawave.argomanager.scontroller.util.MarketUtil;

public class NewDeviceManageActivity extends AppCompatActivity {

    private ImageView iv_device_photo;
    //    private Button btn_download;
    private Button btn_hass;
    private Button btn_locate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_device_manage);
        Intent intent =getIntent();
        String url = (String) intent.getSerializableExtra("url");
        initView();
    }

    private void initView() {
//        btn_download=findViewById(R.id.btn_download);
//        btn_download.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // 查询指定包名信息
//                try {
//                    String packageName = "com.xiaomi.smarthome";
//                    String className = "com.xiaomi.smarthome.SmartHomeMainActivity";
//                    Intent intent=new Intent();
//                    intent.setClassName(packageName, className);
//                    startActivity(intent);
//                }catch (Exception e){
//                    Toast.makeText(NewDeviceManageActivity.this,"没有找到对应软件，转到下载页面",Toast.LENGTH_LONG);
//                    MarketUtil.toXiaoMi(NewDeviceManageActivity.this,"com.xiaomi.smarthome");
//                }
//            }
//        });

        btn_hass=findViewById(R.id.btn_hass);
        btn_hass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 查询指定包名信息
                try {
                    String packageName = "com.xiaomi.smarthome";
                    String className = "com.xiaomi.smarthome.SmartHomeMainActivity";
                    Intent intent=new Intent();
                    intent.setClassName(packageName, className);
                    startActivity(intent);
                }catch (Exception e){
                    Toast.makeText(NewDeviceManageActivity.this,"没有找到对应软件，转到下载页面",Toast.LENGTH_LONG);
                    MarketUtil.toXiaoMi(NewDeviceManageActivity.this,"com.xiaomi.smarthome");
                }
            }
        });

        btn_locate=findViewById(R.id.btn_locate);
        btn_locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(NewDeviceManageActivity.this,MeasureDistActivity.class);
                startActivity(intent1);
            }
        });
    }
}
