package com.pzx.canteen.listener;

/**
 * File Name : USBListener
 * Created by : PanZX on 2020/03/16
 * Email : 644173944@qq.com
 * Github : https://github.com/Pulini
 * Remark：自定义USB插口监听
 */
public interface OnUsbListener {
    void onUSBChangeListener(boolean isConnect);
}
