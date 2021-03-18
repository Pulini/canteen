package com.pzx.canteen;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import cn.jiguang.analytics.android.api.JAnalyticsInterface;


/**
 * File Name : App
 * Created by : PanZX on 2020/03/16
 * Email : 644173944@qq.com
 * Github : https://github.com/Pulini
 * Remark： Application
 */
public class App extends Application {

    public static SharedPreferences mSharedPreferences;
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context=this;
//        mSharedPreferences = getSharedPreferences(APPLICATION_NAME, MODE_PRIVATE);
        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=5bf3520c");
        JAnalyticsInterface.init(this);
//        JAnalyticsInterface.setDebugMode(true);
    }
}
