package com.decawave.argomanager.scontroller.activity.tasks;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.decawave.argomanager.R;

public class TasksWorkflowActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_workflow);
        Intent intent =getIntent();
        int key = (int) intent.getSerializableExtra("key");
    }
}
