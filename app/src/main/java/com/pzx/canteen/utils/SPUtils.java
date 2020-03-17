package com.pzx.canteen.utils;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.pzx.canteen.App;
import com.pzx.canteen.http.Constants;
import com.pzx.canteen.model.LoginModel;

import java.util.HashMap;



/**
 * File Name : LOG
 * Created by : PanZX on 2020/03/16
 * Email : 644173944@qq.com
 * Github : https://github.com/Pulini
 * Remark： SharedPreferences存储
 */
public class SPUtils {


    private static HashMap<String, SharedPreferences> sUserSP = new HashMap<>();

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param key
     * @param object
     */
    public static void put(String key, Object object) {
        if(object==null){
            return;
        }
        String type = object.getClass().getSimpleName();
        SharedPreferences.Editor editor = App.mSharedPreferences.edit();

        if ("String".equals(type)) {
            editor.putString(key, (String) object);
        } else if ("Integer".equals(type)) {
            editor.putInt(key, (Integer) object);
        } else if ("Boolean".equals(type)) {
            editor.putBoolean(key, (Boolean) object);
        } else if ("Float".equals(type)) {
            editor.putFloat(key, (Float) object);
        } else if ("Long".equals(type)) {
            editor.putLong(key, (Long) object);
        }
        editor.commit();
    }


    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param key
     * @param defaultObject
     * @return
     */
     public static Object get(String key, Object defaultObject) {
        String type = defaultObject.getClass().getSimpleName();
        SharedPreferences sharedPreferences = App.mSharedPreferences;

        if ("String".equals(type)) {
            return sharedPreferences.getString(key, (String) defaultObject);
        } else if ("Integer".equals(type)) {
            return sharedPreferences.getInt(key, (Integer) defaultObject);
        } else if ("Boolean".equals(type)) {
            return sharedPreferences.getBoolean(key, (Boolean) defaultObject);
        } else if ("Float".equals(type)) {
            return sharedPreferences.getFloat(key, (Float) defaultObject);
        } else if ("Long".equals(type)) {
            return sharedPreferences.getLong(key, (Long) defaultObject);
        }

        return null;
    }


    public static LoginModel GetUserInfo() {
        return new Gson().fromJson((String) SPUtils.get(Constants.SPKEY_USER_DATA, ""), LoginModel.class);
    }




}
