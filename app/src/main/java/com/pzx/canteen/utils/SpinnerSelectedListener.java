package com.pzx.canteen.utils;

import android.view.View;
import android.widget.AdapterView;

import com.pzx.canteen.listener.OnSelectedListener;

/**
 * File Name : SpinnerSelectedListener
 * Created by : PanZX on 2020/03/17
 * Email : 644173944@qq.com
 * Github : https://github.com/Pulini
 * Remark：自定义Spinner选择监听
 */
public class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {
    OnSelectedListener OSL;

    public SpinnerSelectedListener(OnSelectedListener osl) {
        OSL=osl;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        OSL.OnItemSelected(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
