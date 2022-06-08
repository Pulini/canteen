package com.pzx.canteen.utils

import android.util.Log

/**
 * File Name : LOG
 * Created by : PanZX on 2020/03/16
 * Email : 644173944@qq.com
 * Github : https://github.com/Pulini
 * Remark： Log工具
 */
object LOG {
    private const val TAG = "Pan"
    private const val TAG2 = "PanBT"
    private const val TAG3 = "BLELOG"
    private const val TAG4 = "PanJson"
    private const val TAG5 = "PanFaceRec"
    private const val show = true
    fun w(msg: String?) {
        if (show) {
            Log.w(TAG, msg!!)
        }
    }

    fun BLE(msg: String?) {
        if (show) {
            Log.e(TAG3, msg!!)
        }
    }

    fun e(msg: String?) {
        if (show) {
            Log.e(TAG, msg!!)
        }
    }

    fun p(msg: String?) {
        if (show) {
            Log.e(TAG2, msg!!)
        }
    }

    fun J(msg: String?) {
        if (show) {
            Log.e(TAG4, msg!!)
        }
    }

    fun F(msg: String?) {
        if (show) {
            Log.e(TAG5, msg!!)
        }
    }

    //规定每段显示的长度
    private const val LOG_MAXLENGTH = 2000
    fun E(msg: String?) {
        var msg = msg
        if (msg == null) {
            msg = ""
        }
        val strLength = msg.length
        var start = 0
        var end = LOG_MAXLENGTH
        for (i in 0..999) {
            //剩下的文本还是大于规定长度则继续重复截取并输出
            if (strLength > end) {
                Log.e(TAG + i, msg.substring(start, end))
                start = end
                end = end + LOG_MAXLENGTH
            } else {
                Log.e(TAG, msg.substring(start, strLength))
                break
            }
        }
    }
}