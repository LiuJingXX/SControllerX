package com.decawave.argomanager.scontroller.activity.devices;



import static java.security.AccessController.getContext;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import com.decawave.argomanager.R;
import com.decawave.argomanager.scontroller.activity.BaseActivity;
import com.decawave.argomanager.scontroller.constant.Constant;
import com.decawave.argomanager.scontroller.constant.UrlConstant;
import com.decawave.argomanager.scontroller.http.HttpUtil;
import com.decawave.argomanager.scontroller.util.PhotoUtils;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import okhttp3.Call;
import okhttp3.Response;


public class AddDeviceActivity extends BaseActivity {

    private ImageView photo;
    private ImageView previous;
    private ImageView next;
    private SwipeRefreshLayout swipeRefreshView;
    private FloatingActionButton fab;
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    private File fileUri = new File(Environment.getExternalStorageDirectory().getPath() + "/photo.jpg");
    private File fileCropUri = new File(Environment.getExternalStorageDirectory().getPath() + "/crop_photo.jpg");
    private Uri imageUri;
    private Uri cropImageUri;
    SharedPreferences preferences;
    private ListView listView;
    private String[] listViewData;
    private JSONArray listDeviceData;
    private List<String> urlList = new ArrayList<>();
    private int currentImage;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);
        preferences = getSharedPreferences(Constant.PREFERENCES_USER_INFO, Activity.MODE_PRIVATE);
        initView();
        //设置下拉刷新
        setSwipeRefresh();
        gotoCaptureCrop();
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.list_view_serach_devices);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
//            String info=listViewData[i];
            JSONObject device = null;
            try {
                device = (JSONObject) listDeviceData.get(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String deivceInfo = device.toString();
            saveDeviceInfo(deivceInfo);
//            Toast.makeText(AddDeviceActivity.this, "第" + i + "行"+info, Toast.LENGTH_LONG).show();
        });
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        photo = (ImageView) findViewById(R.id.photo);
        previous = (ImageView) findViewById(R.id.previous_photo);
        next = (ImageView) findViewById(R.id.next_photo);
        photo.setOnClickListener(v -> {
            if(currentImage<0){
                return;
            }
            if(currentImage==0){
                photo.setImageResource(R.mipmap.ic_launcher);
            }
            urlList.remove(currentImage);
            currentImage=urlList.size()-1;
            showDeviceInfo();
        });
        previous.setOnClickListener(v -> {
            currentImage-=1;
            showDeviceInfo();
        });
        next.setOnClickListener(v -> {
            currentImage+=1;
            showDeviceInfo();
        });
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> gotoCaptureCrop());
    }

    // 拍照 + 裁切
    private void gotoCaptureCrop() {
        requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, new RequestPermissionCallBack() {
            @Override
            public void granted() {
                if (hasSdcard()) {
                    imageUri = Uri.fromFile(fileUri);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        //通过FileProvider创建一个content类型的Uri
                        imageUri = FileProvider.getUriForFile(AddDeviceActivity.this, "com.decawave.argomanager.fileprovider", fileUri);
                    PhotoUtils.takePicture(AddDeviceActivity.this, imageUri, CODE_CAMERA_REQUEST);
                } else {
                    Toast.makeText(AddDeviceActivity.this, "设备没有SD卡！", Toast.LENGTH_SHORT).show();
                    Log.e("asd", "设备没有SD卡");
                }
            }

            @Override
            public void denied() {
                Toast.makeText(AddDeviceActivity.this, "部分权限获取失败，正常功能受到影响", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int output_X = 480, output_Y = 480;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CODE_CAMERA_REQUEST://拍照完成回调
                    cropImageUri = Uri.fromFile(fileCropUri);
                    PhotoUtils.cropImageUri(this, imageUri, cropImageUri, 1, 1, output_X, output_Y, CODE_RESULT_REQUEST);
                    break;
                case CODE_GALLERY_REQUEST://访问相册完成回调
                    if (hasSdcard()) {
                        cropImageUri = Uri.fromFile(fileCropUri);
                        Uri newUri = Uri.parse(PhotoUtils.getPath(this, data.getData()));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                            newUri = FileProvider.getUriForFile(this, "com.decawave.argomanager.fileprovider", new File(newUri.getPath()));
                        PhotoUtils.cropImageUri(this, newUri, cropImageUri, 1, 1, output_X, output_Y, CODE_RESULT_REQUEST);
                    } else {
                        Toast.makeText(AddDeviceActivity.this, "设备没有SD卡!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case CODE_RESULT_REQUEST:
                    Bitmap bitmap = PhotoUtils.getBitmapFromUri(cropImageUri, this);
                    if (bitmap != null) {
//                        showImages(bitmap);
                        uploadImage(cropImageUri.getPath());
                    }
                    break;
            }
        }
    }


    //将网络图片转换成bitmap
    public Bitmap getHttpBitmap(String url) {
        URL httpUrl = null;
        Bitmap bitmap = null;
        try {
            httpUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
            conn.setConnectTimeout(6000);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            InputStream in = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(in);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "图片下载失败，请稍后再试", Toast.LENGTH_SHORT).show();
        }
        return bitmap;

    }

    private void showImages(Bitmap bitmap) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(currentImage==0){
                    previous.setVisibility(View.INVISIBLE);
                }else {
                    previous.setVisibility(View.VISIBLE);
                }

                if(currentImage==(urlList.size()-1)){
                    next.setVisibility(View.INVISIBLE);
                }else {
                    next.setVisibility(View.VISIBLE);
                }
                photo.setImageBitmap(bitmap);
            }
        });

    }

    private void uploadImage(String filePath) {
        final HashMap<String, String> postData = new HashMap<String, String>();
        String serviceURL = UrlConstant.getFlieServiceURL(UrlConstant.FILE_ADD_DEVICE_IMAGE);
        postData.put("userId", String.valueOf(preferences.getInt("userId", 0)));
        HttpUtil.uploadImage(serviceURL, filePath, postData, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showUploadFailed("图片上传失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String url = response.body().string();
                    if (url.contains("url")) {
                        url = url.split("url = ")[1];
                    }
                    urlList.add(url);
                    currentImage = urlList.size() - 1;
                    searchDeviceInfo(url);
                    showDeviceInfo();
                } catch (Exception e) {
                    e.printStackTrace();
                    showUploadFailed("图片上传失败，获取url失败");
                }

            }
        });
    }


    private void searchDeviceInfo(String url) {
        final HashMap<String, String> postData = new HashMap<String, String>();
        String serviceURL = UrlConstant.getPailitaoServiceURL(UrlConstant.PAILITAO_SEARCH_DEVICE_INFO);
        postData.put("url", url);
        HttpUtil.doPost(serviceURL, postData, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showUploadFailed("设备信息请求错误");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                showUploadFailed("正在查询设备信息，请稍等。");
//                try {
//                    String responceData = response.body().string();
//                    //设置数据
//                    showDeviceList(responceData);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    showUploadFailed("设备信息解析失败");
//                }
            }
        });
    }

    private void showDeviceInfo() {
        //清空数据显示
        listViewData = new String[0];
        initAdapter();
        if(currentImage<0){
            swipeRefreshView.setRefreshing(false);
            showUploadFailed("请添加设备照片");
            return;
        }
        //显示上面的图片
        String url = urlList.get(currentImage);
        new Thread(() -> showImages(getHttpBitmap(url))).start();
        //查找下面数据数据信息
        final HashMap<String, String> postData = new HashMap<String, String>();
        String serviceURL = UrlConstant.getPailitaoServiceURL(UrlConstant.PAILITAO_GET_DEVICE_INFO);
        postData.put("url", url);
        HttpUtil.doPost(serviceURL, postData, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showUploadFailed("获取设备信息失败,加载默认数据");
                String responceData = "{\n" +
                        "\"url\":\"www.baidu.com\",\n" +
                        "\"data\":[\n" +
                        "         {\n" +
                        "             \"商品类别\": \"办公鼠标\",\n" +
                        "             \"商品品牌\": \"罗技（Logitech）\",\n" +
                        "             \"商品型号\": \"罗技M185\"\n" +
                        "           },\n" +
                        "         {\n" +
                        "             \"商品类别\": \"办公鼠标\",\n" +
                        "             \"商品品牌\": \"罗技（Logitech）\",\n" +
                        "             \"商品型号\": \"罗技m186灰黑色\"\n" +
                        "           },\n" +
                        "         {\n" +
                        "             \"商品类别\": \"办公鼠标\",\n" +
                        "             \"商品品牌\": \"罗技（Logitech）\",\n" +
                        "             \"商品型号\": \"罗技B175\"\n" +
                        "         }\n" +
                        "       ]\n" +
                        "}";
                try {
                    showDeviceList(responceData);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responceData = response.body().string();
                    //设置数据
                    showDeviceList(responceData);
                    swipeRefreshView.setRefreshing(false);
                } catch (Exception e) {
                    e.printStackTrace();
                    showUploadFailed("设备信息解析失败");
                }
            }
        });
    }

    private void showDeviceList(String responceData) throws JSONException {
        JSONObject jsonObject = null;
        jsonObject = new JSONObject(responceData);
        String url = (String) jsonObject.get("url");
        Object dataObject=jsonObject.get("data");
        if("null".equals(dataObject)){
            showUploadFailed("正在查询设备信息，请稍等");
            return;
        }
        listDeviceData = (JSONArray) jsonObject.get("data");
        listViewData = new String[listDeviceData.length()];
        for (int i = 0; i < listDeviceData.length(); i++) {
            JSONObject device = (JSONObject) listDeviceData.get(i);
//            String deivceInfo = device.toString();
            String deivceInfo = "";
            deivceInfo+="名称：";
            deivceInfo+=device.get("名称");
            deivceInfo+="\n";
            deivceInfo+="品牌：";
            deivceInfo+=device.get("品牌");
            deivceInfo+="\n";
            deivceInfo+="类别：";
            deivceInfo+=device.get("类别");
            listViewData[i] = deivceInfo;
        }
        initAdapter();
    }

    private void initAdapter() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter = new ArrayAdapter<String>(
                        AddDeviceActivity.this, android.R.layout.simple_list_item_1, listViewData);
                listView.setAdapter(adapter);
            }
        });
    }


    private void saveDeviceInfo(String deivceInfo) {
        final HashMap<String, String> postData = new HashMap<String, String>();
        String serviceURL = UrlConstant.getAppBackEndServiceURL(UrlConstant.APP_BACK_END_DEVICE_SAVE_DEVICE_INFO);
        postData.put("url", urlList.get(currentImage));
        postData.put("deviceInfo", deivceInfo);
        urlList.remove(currentImage);
        currentImage=urlList.size()-1;
        showDeviceInfo();
        HttpUtil.doPost(serviceURL, postData, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showUploadFailed("保存设备信息出错");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                showUploadFailed("保存设备信息成功");
                Intent intent = new Intent(AddDeviceActivity.this,NewDeviceManageActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("url","21");
                intent.putExtras(bundle);
                startActivity(intent);
//                try {
//                    String responceData = response.body().string();
//                    //设置数据
//                    showDeviceList(responceData);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    showUploadFailed("设备信息解析失败");
//                }
            }
        });
    }


    private void showUploadFailed(String error) {
        Snackbar.make(fab, error, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    /**
     * 检查设备是否存在SDCard的工具方法
     */
    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    private void setSwipeRefresh(){
        swipeRefreshView = (SwipeRefreshLayout) findViewById(R.id.swipe_add_device);
        // 设置颜色属性的时候一定要注意是引用了资源文件还是直接设置16进制的颜色，因为都是int值容易搞混
        // 设置下拉进度的背景颜色，默认就是白色的
        swipeRefreshView.setProgressBackgroundColorSchemeResource(android.R.color.white);
        // 设置下拉进度的主题颜色
        swipeRefreshView.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.yellow);

        // 下拉时触发SwipeRefreshLayout的下拉动画，动画完毕之后就会回调这个方法
        swipeRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 开始刷新，设置当前为刷新状态
                //swipeRefreshLayout.setRefreshing(true);
                showDeviceInfo();
            }
        });

    }
}
