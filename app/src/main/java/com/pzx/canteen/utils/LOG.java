package com.pzx.canteen.utils;

import android.util.Log;



/**
 * File Name : LOG
 * Created by : PanZX on 2020/03/16
 * Email : 644173944@qq.com
 * Github : https://github.com/Pulini
 * Remark： Log工具
 */
public class LOG {

    private static String TAG = "Pan";
    private static String TAG2 = "PanBT";
    private static String TAG3 = "BLELOG";
    private static String TAG4 = "PanJson";
    private static String TAG5 = "PanFaceRec";
    private static boolean show = true;

    public static void w(String msg) {
        if (show) {
            Log.w(TAG, msg);
        }
    }

    public static void BLE(String msg) {
        if (show) {
            Log.e(TAG3, msg);
        }
    }

    public static void e(String msg) {
        if (show) {
            Log.e(TAG, msg);
        }
    }

    public static void p(String msg) {
        if (show) {
            Log.e(TAG2, msg);
        }
    }

    public static void J(String msg) {
        if (show) {
            Log.e(TAG4, msg);
        }
    }
    public static void F(String msg) {
        if (show) {
            Log.e(TAG5, msg);
        }
    }

    //规定每段显示的长度
    private static int LOG_MAXLENGTH = 2000;

    public static void E(String msg) {
        if (msg == null) {
            msg = "";
        }
        int strLength = msg.length();
        int start = 0;
        int end = LOG_MAXLENGTH;
        for (int i = 0; i < 1000; i++) {
            //剩下的文本还是大于规定长度则继续重复截取并输出
            if (strLength > end) {
                Log.e(TAG + i, msg.substring(start, end));
                start = end;
                end = end + LOG_MAXLENGTH;
            } else {
                Log.e(TAG, msg.substring(start, strLength));
                break;
            }
        }
    }


}
