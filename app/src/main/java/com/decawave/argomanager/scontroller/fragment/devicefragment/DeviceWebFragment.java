package com.decawave.argomanager.scontroller.fragment.devicefragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.decawave.argomanager.R;

import org.jetbrains.annotations.Nullable;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DeviceWebFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DeviceWebFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeviceWebFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private WebView webView;
    private String mstrLoginUrl = "http://10.131.253.117:5050/fdse";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_device_web, container, false);

        webView = (WebView) view.findViewById(R.id.wv_home_device);

        webView.getSettings().setJavaScriptEnabled(true);
        //支持屏幕缩放
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        //不显示webview缩放按钮
        webView.getSettings().setDisplayZoomControls(false);

//todo 暂时先把下面的注销掉，免得每次运行的时候都一直在请求hass服务，影响效率
        webView.loadUrl(mstrLoginUrl);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
    }
}
