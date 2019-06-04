package com.decawave.argomanager.scontroller.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.decawave.argomanager.R;
import com.decawave.argomanager.scontroller.model.HomeDevice;

import java.util.List;

/**
 * Created by Hu_codeman on 2019/5/31.
 */

public class HomeDeviceAdapter extends ArrayAdapter {
    private final int resourceId;

    public HomeDeviceAdapter(@NonNull Context context, int resource, List<HomeDevice> objects) {
        super(context, resource, objects);
        resourceId=resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HomeDevice homeDevice = (HomeDevice) getItem(position); // 获取当前项的HomeDevice实例
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);//实例化一个对象
        ImageView homeDeviceImage = (ImageView) view.findViewById(R.id.iv_home_device);//获取该布局内的图片视图
        TextView homeDeviceName = (TextView) view.findViewById(R.id.tv_home_device_name);//获取该布局内的文本视图
        TextView homeDeviceState = (TextView) view.findViewById(R.id.tv_home_device_sate);//获取该布局内的文本视图
        homeDeviceImage.setImageResource(homeDevice.getImageId());//为图片视图设置图片资源
        homeDeviceName.setText(homeDevice.getName());//为文本视图设置文本内容
        homeDeviceState.setText(homeDevice.getState());//为文本视图设置文本内容
        return view;
    }
}
