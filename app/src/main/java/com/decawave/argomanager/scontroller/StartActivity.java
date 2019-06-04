package com.decawave.argomanager.scontroller;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.decawave.argomanager.R;
import com.decawave.argomanager.scontroller.web.WebService;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onStop() {
        super.onStop();
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        final Context context = getApplicationContext();
        try {
            Thread.sleep(4000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        TokenMessage.getInstance().deleteUserToken(context,"Token");
        if(TokenMessage.getInstance().getUserToken(context,"Token") == null){
            Toast.makeText(StartActivity.this,"AutoLogin Failed",Toast.LENGTH_LONG).show();
            Intent intent0 = new Intent(StartActivity.this,LoginActivity.class);
            startActivity(intent0);
        }else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String message = WebService.login("token=" + TokenMessage.getInstance().getUserToken(context,"Token"),"login");
                    //Toast.makeText(StartActivity.this,"Have Token",Toast.LENGTH_LONG).show();
                    //TokenMessage.getInstance().saveUserToken(context,message);
                }
            }).start();

        }
    }
}
