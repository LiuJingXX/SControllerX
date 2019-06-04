package com.decawave.argomanager.scontroller.web;

public interface HttpCallbackListener {
    void onFinish(String response);

    void onError(Exception e);
}
