package com.pzx.canteen.utils;

import android.view.animation.Animation;

import com.pzx.canteen.listener.OnAnimationEndListener;

/**
 * File Name : AnimationEndListener
 * Created by : PanZX on 2020/03/16
 * Email : 644173944@qq.com
 * Github : https://github.com/Pulini
 * Remark：自定义动画监听
 */
public class AnimationEndListener implements Animation.AnimationListener {

    private  OnAnimationEndListener AEL;

    public AnimationEndListener(OnAnimationEndListener ael) {
        AEL=ael;
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        AEL.onAnimationEnd(animation);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
