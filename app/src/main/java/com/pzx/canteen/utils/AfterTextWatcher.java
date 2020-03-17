package com.pzx.canteen.utils;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;

import com.pzx.canteen.listener.OnAfterTextWatcherListener;


/**
 * File Name : AfterTextWatcher
 * Created by : PanZX on  2018/12/5 11:06
 * Email : 644173944@qq.com
 * Github : https://github.com/Pulini
 * Remark：自定义文本框监听
 */
public class AfterTextWatcher implements TextWatcher {
    private OnAfterTextWatcherListener MTWL;
    String str = "";
    private Handler handler = new Handler();
    /**
     * 延迟线程，看是否还有下一个字符输入
     */
    private Runnable delayRun = new Runnable() {

        @Override
        public void run() {
            //在这里调用服务器的接口，获取数据
            MTWL.afterTextChanged(str);
        }
    };

    public AfterTextWatcher(OnAfterTextWatcherListener mtwl) {
        MTWL = mtwl;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(final Editable s) {
//        MTWL.afterTextChanged(s.toString());
        if (delayRun != null) {
            //每次editText有变化的时候，则移除上次发出的延迟线程
            handler.removeCallbacks(delayRun);
        }
        str = s.toString();

        //延迟800ms，如果不再输入字符，则执行该线程的run方法
        handler.postDelayed(delayRun, 500);

    }

}
