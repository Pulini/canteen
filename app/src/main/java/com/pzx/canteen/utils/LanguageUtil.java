package com.pzx.canteen.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;

import java.util.Locale;

/**
 * File Name : LanguageUtil
 * Created by : PanZX on 2020/03/16
 * Email : 644173944@qq.com
 * Github : https://github.com/Pulini
 * Remark： 语言切换工具
 */
public class LanguageUtil {
    /**
     * 切换语言
     * @param context   上下文
     * @param locale    目标语言
     */
    public static  void changeAppLanguage(Context context , Locale locale){
        if(locale.getLanguage().isEmpty()){
            return;
        }
        Resources res = context.getResources();
        DisplayMetrics displayMetrics = res.getDisplayMetrics();
        Configuration configuration = res.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
        }else{
            configuration.locale=locale;
        }
        res.updateConfiguration(configuration,displayMetrics);
    }
}
