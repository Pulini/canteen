package com.pzx.canteen

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import cn.jiguang.analytics.android.api.JAnalyticsInterface
import com.iflytek.cloud.SpeechConstant
import com.iflytek.cloud.SpeechUtility

/**
 * File Name : App
 * Created by : PanZX on 2020/03/16
 * Email : 644173944@qq.com
 * Github : https://github.com/Pulini
 * Remark： Application
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        context = this
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5bf3520c")
        JAnalyticsInterface.init(this)
//        JAnalyticsInterface.setDebugMode(true)
    }

    companion object {
        lateinit var context: Context
    }
}