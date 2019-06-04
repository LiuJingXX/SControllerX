package com.decawave.argomanager.scontroller;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Hu_codeman on 2019/5/30.
 */

public class TokenMessage {
    private static TokenMessage instance;

    public static TokenMessage getInstance() {
        if (instance == null) {
            instance = new TokenMessage();
        }
        return instance;
    }

    public void saveUserToken(Context context, String token) {
        SharedPreferences sp = context.getSharedPreferences("userToken", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("Token", token);
        editor.apply();
    }
    public String getUserToken(Context context,String key){
        SharedPreferences sp = context.getSharedPreferences("userToken", Context.MODE_PRIVATE);
        return sp.getString(key,null);
    }
    public void deleteUserToken(Context context,String key){
        SharedPreferences sp = context.getSharedPreferences("userToken", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove("Token");
        editor.apply();
    }
}
