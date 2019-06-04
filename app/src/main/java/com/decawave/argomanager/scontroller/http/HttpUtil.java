package com.decawave.argomanager.scontroller.http;

import com.decawave.argomanager.scontroller.util.Global;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Hu_codeman on 2019/5/31.
 */

public class HttpUtil {
    //运行值
//    private static final String resource_name = "MobiCrowdsourcing";
    //测试值
    private static final String resource_name = "MobiCrowdsourcingTest";
    private static final String UPLOAD_FILE_SERVLET = "UploadFile";
    private static final String UPLOAD_IMAGE_SERVLET = "UploadImage";
    private static final MediaType MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg");
    private static final MediaType MEDIA_TYPE_XML = MediaType.parse("text/xml; charset=utf-8");

    //Get请求，获取HASS设备列表
    public void getHASSApiState(String serviceURL,String hassPwd, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .addHeader("Content-Type","application/json")
                .addHeader("X-HA-Access", hassPwd)
                .url(serviceURL)
                .build();
        client.newCall(request).enqueue(callback);
    }

    //Get请求，只获取数据，不发送数据
    public static void sendOkHttpRequestByGet(String serviceURL,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .addHeader("cookie", Global.sessionId)
                .url(serviceURL)
                .build();
        client.newCall(request).enqueue(callback);
    }

    //Post请求，发送数据的同时获取并返回数据
    public static void doPost(String serviceURL, HashMap<String, String> map, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBody = new FormBody.Builder();
        //添加要发送的数据
        for (Map.Entry<String, String> entry : map.entrySet()) {
            formBody.add(entry.getKey(), entry.getValue());
        }
        RequestBody body = formBody.build();
        //发送请求
        Request request = new Request.Builder()
                .addHeader("cookie", Global.sessionId)
                .url(serviceURL)
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }

    //上传文件
    public static void uploadFile(String filePath, HashMap<String, String> map, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        String fileName = map.get("fileName");

        //构建请求体
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("fileName", fileName);
        builder.addFormDataPart("upload", filePath,
                RequestBody.create(MEDIA_TYPE_XML, new File(filePath)));
        RequestBody body = builder.build();

        //发送请求
//        Request request = new Request.Builder()
//                .url(String.format("http://%s:%s/%s/%s", ip, port, resource_name,UPLOAD_FILE_SERVLET))
//                .post(body)
//                .build();
//        client.newCall(request).enqueue(callback);
    }

    //上传图片
    public static void uploadImage(String serviceURL,String filePath, HashMap<String, String> maps, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();

        //构建请求体
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        if (maps != null) {
            for (String key : maps.keySet()) {
                builder.addFormDataPart(key, maps.get(key));
            }
        }
        File file=new File(filePath);
        String fileName= String.valueOf(System.currentTimeMillis());
        fileName+=".jpg";
        builder.addFormDataPart("fileName", fileName,
                RequestBody.create(MEDIA_TYPE_JPEG, file));

        RequestBody body = builder.build();

        Request request = new Request.Builder()
                .addHeader("cookie", Global.sessionId)
                .url(serviceURL)
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }

    //下载文件
    public static void downloadFile(String fileUrl, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        //发送请求
        Request request = new Request.Builder()
                .url(fileUrl)
                .build();
        client.newCall(request).enqueue(callback);

    }
}
