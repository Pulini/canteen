package com.pzx.canteen.newApp

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pzx.canteen.App

//const val BaseUrl = "https://192.168.99.79:1226/"
const val BaseUrl = "https://geapp.goldemperor.com:1226/"
const val APPLICATION_NAME = "appDetails.xml"
const val SUNMI_DEVICE_NAME = "SM SM-2D PRODUCT HID KBW"
const val SHP_KEY_USER_DATA = "UserData"
const val SHP_KEY_USER_PHONE = "UserPhone"
const val SHP_KEY_USER_PASSWORD = "UserPassword"


/**
 *  dp转px
 *  @param      dp
 *  @return     px
 */
fun dp2px(dp: Float) = (dp * App.context.resources.displayMetrics.density + 0.5).toInt()

/**
 *  获取资源文件string
 *  @param      id      资源ID
 *  @return     字符串
 */
fun getContextString(id: Int) = App.context.getString(id)


/**
 *  SharedPreferences 存储
 *  @param  key     存入的关键字
 *  @param  value   存储的内容
 */
fun sharedPut(key: String, value: Any) {
    val shp = App.context.getSharedPreferences(APPLICATION_NAME, Context.MODE_PRIVATE)
    val editor: SharedPreferences.Editor = shp.edit()
    when (value.javaClass.simpleName) {
        "String" -> editor.putString(key, value as String)
        "Integer" -> editor.putInt(key, value as Int)
        "Boolean" -> editor.putBoolean(key, value as Boolean)
        "Float" -> editor.putFloat(key, value as Float)
        "Long" -> editor.putLong(key, value as Long)
    }
    editor.apply()
}

/**
 *  SharedPreferences 读取
 *  @param  key     读取的关键字
 *  @param  value   读取失败的默认值
 */
fun sharedGet(key: String, value: Any): Any? {
    val shp = App.context.getSharedPreferences(APPLICATION_NAME, Context.MODE_PRIVATE)
    return when (value.javaClass.simpleName) {
        "String" -> shp.getString(key, value as String)
        "Integer" -> shp.getInt(key, value as Int)
        "Boolean" -> shp.getBoolean(key, value as Boolean)
        "Float" -> shp.getFloat(key, value as Float)
        "Long" -> shp.getLong(key, value as Long)
        else -> value
    }
}

/**
 * 获取当前登录的用户数据
 */
fun getUserInfo(): UserBean? {
    (sharedGet(SHP_KEY_USER_DATA, "") as String).apply {
        return if (isNotEmpty()) {
            Gson().fromJson(this, object : TypeToken<UserBean>() {}.type)
        } else {
            null
        }
    }
}