package com.decawave.argomanager.scontroller.service;

/**
 * Created by Hu_codeman on 2019/5/31.
 */

public class MessageEvent {
    //事件类型
    private int eventType;
    //用于任务流程节点完成时的推送
    private int taskId;
    private  String nodeId;
    private  String completeTime;

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(String completeTime) {
        this.completeTime = completeTime;
    }
}
