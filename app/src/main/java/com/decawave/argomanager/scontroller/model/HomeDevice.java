package com.decawave.argomanager.scontroller.model;

/**
 * Created by Hu_codeman on 2019/5/31.
 */

public class HomeDevice {
    private int imageId;
    private String name;
    private  String state;

    public HomeDevice(int imageId,String name,String state ) {
        this.imageId = imageId;
        this.name = name;
        this.state = state;
    }


    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
