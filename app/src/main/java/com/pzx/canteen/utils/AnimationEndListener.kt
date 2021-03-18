package com.pzx.canteen.utils

import android.view.animation.Animation

/**
 * File Name : AnimationEndListener
 * Created by : PanZX on 2020/03/16
 * Email : 644173944@qq.com
 * Github : https://github.com/Pulini
 * Remark：自定义动画监听
 */
class AnimationEndListener(private val end:(Animation)->Unit) : Animation.AnimationListener {
    override fun onAnimationStart(animation: Animation) {}
    override fun onAnimationEnd(animation: Animation) {
        end.invoke(animation)
    }
    override fun onAnimationRepeat(animation: Animation) {}
}