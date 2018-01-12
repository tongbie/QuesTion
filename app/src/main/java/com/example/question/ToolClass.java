package com.example.question;

import android.widget.LinearLayout;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by aaa on 2018/1/12.
 */

public class ToolClass {

    public static LinearLayout.LayoutParams spaceParams;

    public static OkHttpClient client;

    public ToolClass(){
        spaceParams= new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        spaceParams.setMargins(0,26,0,0);
        client = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
    }
}
