package com.decawave.argomanager.scontroller.hooks;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Created by Hu_codeman on 2019/5/31.
 */

public class SensorHook implements IXposedHookLoadPackage {
    private static String MI_PACKAGE_NAME = "com.xiaomi.smarthome";

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) {
        if (lpparam.packageName.equals(MI_PACKAGE_NAME)) {
            try {
                mijia(lpparam);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    private void mijia(final XC_LoadPackage.LoadPackageParam lpparam) {
        try {
//            跳转到设备选择
            final Class deviceMainPageClazz = XposedHelpers.findClass("com.xiaomi.smarthome.newui.DeviceMainPage", lpparam.classLoader);
            findAndHookMethod(deviceMainPageClazz, "G", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    //com.xiaomi.smarthome.newui.DeviceMainPage
                    super.afterHookedMethod(param);
                    Log.i("VirtualXposed_", "in after hooked method 1");
                    //获取 activity
//                    Context context = AndroidAppHelper.currentApplication().getApplicationContext();
                    Context context = (Context) XposedHelpers.callMethod(param.thisObject, "getActivity");
                    //调用方法
                    Class ChooseDeviceActivity = lpparam.classLoader.loadClass("com.xiaomi.smarthome.device.choosedevice.ChooseDeviceActivity");
                    Object chooseDeviceActivity = ChooseDeviceActivity.newInstance();
                    XposedHelpers.callMethod(chooseDeviceActivity, "openChooseDevice", context);
                }
            });

            //点击输入框
            final Class chooseDeviceManuallyClazz = XposedHelpers.findClass("com.xiaomi.smarthome.device.ChooseDeviceManually", lpparam.classLoader);
            findAndHookMethod(chooseDeviceManuallyClazz, "onResume", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    //com.xiaomi.smarthome.newui.DeviceMainPage
                    super.afterHookedMethod(param);
                    Log.i("VirtualXposed_", "in after hooked method 2");
                    View comfirBtn = (View) XposedHelpers.findField(param.thisObject.getClass(), "i").get(param.thisObject);
                    comfirBtn.setEnabled(true);
                    comfirBtn.performClick();
                }
            });

            //输入设备名称
            final Class chooseDeviceActivityClazz = XposedHelpers.findClass("com.xiaomi.smarthome.device.choosedevice.ChooseDeviceActivity", lpparam.classLoader);
            findAndHookMethod(chooseDeviceActivityClazz, "onCreate", Bundle.class,new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    //com.xiaomi.smarthome.newui.DeviceMainPage
                    super.afterHookedMethod(param);
                    Log.i("VirtualXposed_", "in after hooked method 3");
                    EditText mSearchEt = (EditText) XposedHelpers.findField(param.thisObject.getClass(), "mSearchEt").get(param.thisObject);
                    mSearchEt.setEnabled(true);
                    Thread.sleep(10000);
                    mSearchEt.setText("米家门窗传感");
                }
            });

//            //输入设备名称
//            final Class verticalSlidingTabClazz = XposedHelpers.findClass("com.xiaomi.smarthome.device.choosedevice.VerticalSlidingTab", lpparam.classLoader);
//            findAndHookMethod(verticalSlidingTabClazz, "a", int.class, String.class, new XC_MethodHook() {
//                @Override
//                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                    //com.xiaomi.smarthome.newui.DeviceMainPage
//                    super.afterHookedMethod(param);
//                    //调用方法
//                    Log.i("VirtualXposed_", "in after hooked method 4 +" + param.args[0].toString()+"+" +param.args[1].toString());
//                    LinearLayout linearLayout = (LinearLayout) XposedHelpers.findField(param.thisObject.getClass(), "e").get(param.thisObject);
//                    Log.i("VirtualXposed_", "in after hooked method 40 +" + linearLayout.getChildCount());
//                    LinearLayout b=(LinearLayout) linearLayout.getChildAt(2);
//                    b.performClick();
//
//
////                    Class AnimPageView = lpparam.classLoader.loadClass("com.xiaomi.smarthome.device.choosedevice.AnimPageView");
////                    Class  AnimPageView = XposedHelpers.findClass("com.xiaomi.smarthome.device.choosedevice.AnimPageView", lpparam.classLoader);
////                    Log.i("VirtualXposed_", "in after hooked method 44");
////                    Object animPageView = AnimPageView.newInstance();
////                    Log.i("VirtualXposed_", "in after hooked method 444");
////                    Boolean b = false;
////                    while (!b) {
////                        XposedHelpers.callMethod(animPageView, "setPosition", 2);
////                        Log.i("VirtualXposed_", "in after hooked method 4444");
////                        b = true;
////                    }
//                }
//            });


            final Class loginPwdViewClazz = XposedHelpers.findClass("com.xiaomi.smarthome.frame.login.ui.view.LoginPwdView", lpparam.classLoader);
            findAndHookMethod(loginPwdViewClazz, "d", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    //com.xiaomi.smarthome.newui.DeviceMainPage
                    Log.i("VirtualXposed_", "5");
                    super.afterHookedMethod(param);
                    EditText username = (EditText) XposedHelpers.findField(param.thisObject.getClass(), "d").get(param.thisObject);
                    username.setEnabled(true);
                    username.setText("18706716126");
                    EditText password = (EditText) XposedHelpers.findField(param.thisObject.getClass(), "f").get(param.thisObject);
                    password.setEnabled(true);
                    password.setText("a24337111abd11");
                    TextView comfirBtn = (TextView) XposedHelpers.findField(param.thisObject.getClass(), "g").get(param.thisObject);
                    comfirBtn.setEnabled(true);
                    Boolean b = false;
                    while (!b) {
                        comfirBtn.performClick();
                        b = true;
                    }

                }
            });

            //点击类别,获取参数值
            final Class animPageViewClazz = XposedHelpers.findClass("com.xiaomi.smarthome.device.choosedevice.AnimPageView", lpparam.classLoader);
            findAndHookMethod(animPageViewClazz, "setPosition", int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    //com.xiaomi.smarthome.newui.DeviceMainPage
                    super.afterHookedMethod(param);
                    Log.i("VirtualXposed_", "in after hooked method 6 +" + param.args[0].toString());
                }
            });


            findAndHookMethod(TextView.class, "setText", CharSequence.class, TextView.BufferType.class, boolean.class, int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    Log.i("VirtualXposed", "7");
//                    Log.i("VX param", param.method.getDeclaringClass().getName());
//                    XposedBridge.log("VirtualXposed sample: VirtualXposed");
                    if (param.args[0] != null) {
                        String stringArgs = param.args[0].toString();
//                        Log.i("VX param", "param " + stringArgs+"-"+param.thisObject.toString());
//                        XposedBridge.log("VirtualXposed sample: " + "param " + stringArgs);
//                        param.args[0] = stringArgs + "-1";
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
