package com.decawave.argomanager.scontroller.activity.scenario;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.decawave.argo.api.struct.Position;
import com.decawave.argomanager.R;
import com.decawave.argomanager.scontroller.http.HttpUtil;
import com.decawave.argomanager.scontroller.util.TagPosition;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ExerciseRoomActivity extends AppCompatActivity {
    private Button mButton;
    private Button mButtonPos;
    private TextView mTextView;
    private EditText mEditText;
    private final String TAG="ExerciseRoomActivity";
    private String enabledDeviceList;
    private final String url = "http://10.222.197.120:8080/YancloudDemo_war_exploded/process/execute?location=";

    private TagPosition tagPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_room);

        initView();
        setListener();
        tagPosition = new TagPosition();

    }

    private void setListener() {
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roomNumber = mEditText.getText().toString();
                getRoomState(roomNumber);

                try {
                    int count_i = 0;
                    while (count_i < 4 && (enabledDeviceList == null || enabledDeviceList.isEmpty())) {
                        count_i++;
                        Thread.sleep(500);
                    }

                    if (enabledDeviceList == null || enabledDeviceList.isEmpty()){
                        Toast.makeText(ExerciseRoomActivity.this,"Failed to connect Device Server",Toast.LENGTH_SHORT).show();
                        throw new InterruptedException("Connect Time out!Failed to connect Device Server！");
                    }

                    // {"result":true,"ariPurifier":true,"getWeight":true,"getAirCondition":true,"drink":true}
                    JSONObject fullJson = new JSONObject(enabledDeviceList);
                    String result = fullJson.getString("result");
                    String ariPurifier = fullJson.getString("ariPurifier");
                    String getWeight = fullJson.getString("getWeight");
                    String getAirCondition = fullJson.getString("getAirCondition");
                    String drink = fullJson.getString("drink");

                    if("false".equals(result)){
                        mTextView.setText("No enough enabled device");
                        Toast.makeText(ExerciseRoomActivity.this,"No enough enabled device",Toast.LENGTH_SHORT).show();
                    }else{
                        String tmp = "ariPurifier:\t"+ariPurifier+"\n"
                                + "getWeight:\t"+getWeight+"\n"
                                + "getAirCondition:\t"+getAirCondition+"\n"
                                + "drink:\t"+drink+"\n"
                                + "Running...";
                        mTextView.setText(tmp);
                        Toast.makeText(ExerciseRoomActivity.this,"Success！",Toast.LENGTH_SHORT).show();
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        });
        mButtonPos.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                tagPosition.configureGridView();
                Position p = tagPosition.getTagLocation();
                if(p!=null){
                    Log.d(TAG, "onClick: z坐标"+p.z);
                }
            }
        });
    }

    private void getRoomState(String roomNumber) {
        HttpUtil httpUtil = new HttpUtil();
        httpUtil.doGet(url+roomNumber, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure: ",e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "onResponse: OK!");
                enabledDeviceList = response.body().string();
            }
        });
    }


    private void initView() {
        mButton = findViewById(R.id.button_exercise_room);
        mTextView = findViewById(R.id.textView_exercise_room);
        mEditText = findViewById(R.id.editText_exerciseroom);

        mButtonPos = findViewById(R.id.button_exercise_room_getposition);
    }
}

