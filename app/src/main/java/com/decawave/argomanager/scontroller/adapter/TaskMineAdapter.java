package com.decawave.argomanager.scontroller.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.decawave.argomanager.R;
import com.decawave.argomanager.scontroller.activity.tasks.TasksWorkflowD3Activity;
import com.decawave.argomanager.scontroller.model.Task;
import com.decawave.argomanager.scontroller.util.Global;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by Hu_codeman on 2019/5/31.
 */

public class TaskMineAdapter extends BaseAdapter {
    private Context mContext;
    private List<Task> mList;

    public TaskMineAdapter(Context context, List<Task> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Task getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_tasks_mine, null);
        }
        final Task task = getItem(position);
        LinearLayout l = (LinearLayout)convertView;
        TextView nameTextView = (TextView) l.findViewById(R.id.tv_task_mine_name);
        nameTextView.setText(task.getName());
        TextView  progressTextView = (TextView) l.findViewById(R.id.tv_task_mine_progress);
        progressTextView.setText(task.getState()+"%");
        TextView  timeTextView = (TextView) l.findViewById(R.id.tv_task_mine_time);
        timeTextView.setText(Global.getTime(task.getPublishTime()));
        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开始传值
                Intent intent = new Intent(mContext, TasksWorkflowD3Activity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("key", task.getId());
                intent.putExtras(bundle);
                //利用上下文开启跳转
                mContext.startActivity(intent);
            }
        });
        return l;
    }
}
