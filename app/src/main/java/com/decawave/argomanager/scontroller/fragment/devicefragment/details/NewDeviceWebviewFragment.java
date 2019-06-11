package com.decawave.argomanager.scontroller.fragment.devicefragment.details;

import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.decawave.argomanager.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NewDeviceWebviewFragment extends Fragment {
    private WebView mWebView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_device_detail_webview, container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = this.getArguments();
        String url = args.getString("url");

        mWebView = view.findViewById(R.id.device_detail_webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        //支持屏幕缩放
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        //不显示webview缩放按钮
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.loadUrl(url);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mWebView.getSettings()
                            .setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
                }else{
                    try{
                        Method setMixedContentMode = WebSettings.class.getMethod("setMixedContentMode", int.class);
                        try {
                            setMixedContentMode.invoke(mWebView.getSettings(), WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE); // 2 = MIXED_CONTENT_COMPATIBILITY_MODE
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        Log.d("WebSettings", "Successfully set MIXED_CONTENT_COMPATIBILITY_MODE");
                    }catch (NoSuchMethodException e){
                        Log.e("WebSettings", "Error getting setMixedContentMode method");
                    }catch (IllegalAccessException e){
                        Log.e("WebSettings", "Error getting setMixedContentMode method");
                    }
                }
            }
        });
        mWebView.onResume();
    }
}
