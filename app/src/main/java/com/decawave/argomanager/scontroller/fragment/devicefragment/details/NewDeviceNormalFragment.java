package com.decawave.argomanager.scontroller.fragment.devicefragment.details;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.decawave.argomanager.R;
import com.decawave.argomanager.scontroller.ScMainActivity;

public class NewDeviceNormalFragment extends Fragment {
    private Button mButton;
    private TextView mTextView_detail;
    private EditText mEditText_detail_deviceName;
    private EditText mEditText_detail_deviceType;
    private EditText mEditText_detail_deviceID;
    private EditText mEditText_detail_devicePosition;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_device_detail_normal, container,false);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        Bundle args = this.getArguments();
        String device_detail = args.getString("device_detail");
        String device_id = args.getString("device_id");
        String device_name = args.getString("device_name");
        String device_type = args.getString("device_type");
        String locationX = args.getString("locationX");
        String locationY = args.getString("locationY");
        String axis = "("+locationX+","+locationY+")";
        mTextView_detail.setText(device_detail);
        mEditText_detail_deviceName.setText(device_name);
        mEditText_detail_deviceType.setText(device_type);
        mEditText_detail_deviceID.setText(device_id);
        mEditText_detail_devicePosition.setText(axis);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 返回主界面
                Intent intent = new Intent(getActivity(), ScMainActivity.class);
                intent.putExtra("fragment_id",0);
                startActivity(intent);
            }
        });
    }

    private void initView(View view){
        mButton = view.findViewById(R.id.button_detail_normal_return);
        mTextView_detail = view.findViewById(R.id.textView_detail_normal);
        mEditText_detail_deviceName = view.findViewById(R.id.editText_detail_normal_deviceName);
        mEditText_detail_deviceType = view.findViewById(R.id.editText_detail_normal_deviceType);
        mEditText_detail_deviceID = view.findViewById(R.id.editText_detail_normal_deviceID);
        mEditText_detail_devicePosition = view.findViewById(R.id.editText_detail_normal_devicePosition);
    }
}
