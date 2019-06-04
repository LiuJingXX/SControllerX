package com.decawave.argomanager.scontroller.util;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MarketUtil {
    private static final String TAG = "MarketUtil";

    public static List<String> checkMarket(Context context) {
        List<String> arrayList = new ArrayList();
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.APP_MARKET");
        int i = 0;
        List queryIntentActivities = context.getPackageManager().queryIntentActivities(intent, 0);
        int size = queryIntentActivities.size();
        while (i < size) {
            String str = ((ResolveInfo) queryIntentActivities.get(i)).activityInfo.packageName;
            String str2 = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("packageName : ");
            stringBuilder.append(str);
            Log.d(str2, stringBuilder.toString());
            arrayList.add(str);
            i++;
        }
        return arrayList;
    }

    @SuppressLint("WrongConstant")
    public static boolean goToSamsungMarket(Context context, String str) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("http://www.samsungapps.com/appquery/appDetail.as?appId=");
        stringBuilder.append(str);
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(stringBuilder.toString()));
        intent.setPackage("com.sec.android.app.samsungapps");
        intent.addFlags(SQLiteDatabase.CREATE_IF_NECESSARY);
        try {
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    @SuppressLint("WrongConstant")
    public static boolean goToSonyMarket(Context context, String str) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("http://m.sonyselect.cn/");
        stringBuilder.append(str);
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(stringBuilder.toString()));
        intent.addFlags(SQLiteDatabase.CREATE_IF_NECESSARY);
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean to360Download(Context context, String str) {
        return toMarket(context, str, "com.qihoo.appstore");
    }

    @SuppressLint("WrongConstant")
    public static boolean toMarket(Context context, String str, String str2) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("market://details?id=");
        stringBuilder.append(str);
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(stringBuilder.toString()));
        intent.addFlags(SQLiteDatabase.CREATE_IF_NECESSARY);
        if (str2 != null) {
            intent.setPackage(str2);
        }
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean toMeizu(Context context, String str) {
        return toMarket(context, str, "com.meizu.mstore");
    }

    public static boolean toQQDownload(Context context, String str) {
        return toMarket(context, str, "com.tencent.android.qqdownloader");
    }

    public static boolean toWandoujia(Context context, String str) {
        return toMarket(context, str, "com.wandoujia.phoenix2");
    }

    public static boolean toXiaoMi(Context context, String str) {
        return toMarket(context, str, "com.xiaomi.market");
    }
}