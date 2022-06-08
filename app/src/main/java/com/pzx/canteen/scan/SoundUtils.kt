package com.pzx.canteen.scan

import android.content.Context
import android.media.AudioManager
import android.media.SoundPool
import java.util.*

/**
 * File Name : SoundUtils
 * Created by : PanZX on 2020/03/14
 * Email : 644173944@qq.com
 * Github : https://github.com/Pulini
 * Remark： 蜂鸣工具
 */
class SoundUtils(private val context: Context, private val soundVolType: Int=3) {
    /**
     * 声音池
     */
    private val soundPool=SoundPool(5, AudioManager.STREAM_MUSIC, 0)

    /**
     * 添加的声音资源参数
     */
    private val soundPoolMap: HashMap<Int, Int> = HashMap()


    /**
     * 添加声音文件进声音池
     *
     * @param order    所添加声音的编号，播放的时候指定
     * @param soundRes 添加声音资源的id
     * @author Leonardo
     * @date 2015-8-20 下午3:50:53
     * @see
     */
    fun putSound(order: Int, soundRes: Int) {
        // 上下文，声音资源id，优先级
        soundPoolMap[order] = soundPool.load(context, soundRes, 1)
    }

    /**
     * 播放声音
     *
     * @param order 所添加声音的编号
     * @param times 循环次数，0无不循环，-1无永远循环
     * @author Leonardo
     * @date 2015-8-20 下午3:52:44
     * @see
     */
    fun playSound(order: Int, times: Int) {
        // 实例化AudioManager对象
        val am = context
                .getSystemService(Context.AUDIO_SERVICE) as AudioManager
        // 返回当前AudioManager对象播放所选声音的类型的最大音量值
        val maxVolumn = am.getStreamMaxVolume(soundVolType).toFloat()
        // 返回当前AudioManager对象的音量值
        val currentVolumn = am.getStreamVolume(soundVolType).toFloat()
        // 比值
        val volumnRatio = currentVolumn / maxVolumn
        soundPool.play(soundPoolMap[order]!!, volumnRatio, volumnRatio, 1,
                times, 1f)
    }



    fun release() {
        soundPool.release()
    }

    companion object {
        /**
         * 无限循环播放
         */
        const val INFINITE_PLAY = -1

        /**
         * 单次播放
         */
        const val SINGLE_PLAY = 0

        /**
         * 铃声音量
         */
        const val RING_SOUND = 2

        /**
         * 媒体音量
         */
        const val MEDIA_SOUND = 3
    }

}