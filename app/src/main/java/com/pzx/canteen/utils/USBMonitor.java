package com.pzx.canteen.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.pzx.canteen.listener.OnUsbListener;

/**
 * File Name : USBMonitor
 * Created by : PanZX on 2020/03/16
 * Email : 644173944@qq.com
 * Github : https://github.com/Pulini
 * Remark： USB监听
 */
public class USBMonitor extends BroadcastReceiver {
    private static final String ACTION_ID = "android.hardware.usb.action.USB_STATE";

    private static OnUsbListener USBL;
    private static USBMonitor USBM;

    public USBMonitor(OnUsbListener srl) {
        LOG.e("注册");
        USBL = srl;
    }

    public static void RegisterReceiver(Activity activity, OnUsbListener srl) {
        LOG.e("注册广播");
        USBM = new USBMonitor(srl);
        activity.registerReceiver(USBM, new IntentFilter(ACTION_ID));
    }


    public static void unRegisterReceiver(Activity activity) {
        LOG.e("注销广播");
        activity.unregisterReceiver(USBM);
        USBM = null;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        LOG.e("接收广播");
        if (intent.getAction().equals(ACTION_ID)) {
            boolean connected = intent.getExtras().getBoolean("connected");
            USBL.onUSBChangeListener(connected);
        }

    }
}
