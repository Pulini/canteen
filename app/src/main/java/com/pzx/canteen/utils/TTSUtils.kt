package com.pzx.canteen.utils

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import com.iflytek.cloud.*

/**
 * File Name : TTSUtils
 * Created by : PanZX on 2020/03/16
 * Email : 644173944@qq.com
 * Github : https://github.com/Pulini
 * Remark：讯飞在线语音合成
 */
class TTSUtils(context: Context) : InitListener, SynthesizerListener {
    private val mTts: SpeechSynthesizer = SpeechSynthesizer.createSynthesizer(context, this)
    private val mEngineType = "cloud"
    private val voicer = "xiaoyan"

    fun startSpeaking(paramString: String) {
        if (mTts.isSpeaking) {
            Handler().postDelayed({ startSpeaking(paramString) }, 1000L)
            return
        }
        val code = mTts.startSpeaking(paramString, this)
        if (code != ErrorCode.SUCCESS) {
            LOG.e("语音合成失败,错误码: $code,请点击网址https://www.xfyun.cn/document/error-code查询解决方案")
        }
    }

    /**
     * 参数设置
     * @return
     */
    private fun setParam() {
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null)
        // 根据合成引擎设置相应参数
        if (mEngineType == SpeechConstant.TYPE_CLOUD) {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD)
            //支持实时音频返回，仅在synthesizeToUri条件下支持
            mTts.setParameter(SpeechConstant.TTS_DATA_NOTIFY, "1")
            //	mTts.setParameter(SpeechConstant.TTS_BUFFER_TIME,"1");
            // 设置在线合成发音人
            mTts.setParameter(SpeechConstant.VOICE_NAME, voicer)
            //设置合成语速
            mTts.setParameter(SpeechConstant.SPEED, "50")
            //设置合成音调
            mTts.setParameter(SpeechConstant.PITCH, "50")
            //设置合成音量
            mTts.setParameter(SpeechConstant.VOLUME, "20")
        } else {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL)
            mTts.setParameter(SpeechConstant.VOICE_NAME, "")
        }

        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3")
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "false")

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "pcm")
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory().toString() + "/msc/tts.pcm")
    }

    override fun onInit(code: Int) {
        LOG.e("InitListener init() code = $code")
        if (code != ErrorCode.SUCCESS) {
            LOG.e("初始化失败,错误码：$code,请点击网址https://www.xfyun.cn/document/error-code查询解决方案")
        } else {
            // 初始化成功，之后可以调用startSpeaking方法
            // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
            // 正确的做法是将onCreate中的startSpeaking调用移至这里
            setParam()
        }
    }

    override fun onSpeakBegin() {
        LOG.e("开始播放")
    }

    override fun onSpeakPaused() {
        LOG.e("暂停播放")
    }

    override fun onSpeakResumed() {
        LOG.e("继续播放")
    }

    override fun onBufferProgress(percent: Int, beginPos: Int, endPos: Int, info: String) {
        // 合成进度
        LOG.e("合成进度:percent =$percent")
    }

    override fun onSpeakProgress(percent: Int, beginPos: Int, endPos: Int) {
        // 播放进度
//        LOG.e("播放进度:percent =" + percent);
//        SpannableStringBuilder style=new SpannableStringBuilder(texts);
//        LOG.e("beginPos = "+beginPos +"  endPos = "+endPos);
//        style.setSpan(new BackgroundColorSpan(Color.RED),beginPos,endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        ((EditText) findViewById(R.id.tts_text)).setText(style);
    }

    override fun onCompleted(error: SpeechError) {
        LOG.e(error.getPlainDescription(true))
    }

    override fun onEvent(eventType: Int, i1: Int, i2: Int, obj: Bundle) {
        //	 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
        //	 若使用本地能力，会话id为null
        if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            val sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID)
            LOG.e("session id =$sid")
        }

        //当设置SpeechConstant.TTS_DATA_NOTIFY为1时，抛出buf数据
        if (SpeechEvent.EVENT_TTS_BUFFER == eventType) {
            val buf = obj.getByteArray(SpeechEvent.KEY_EVENT_TTS_BUFFER)
            LOG.e("bufis =" + buf!!.size)
        }
    }

    fun destroy() {
        // 退出时释放连接
        mTts.stopSpeaking()
        mTts.destroy()
    }

    fun stop() {
        mTts.stopSpeaking()
    }

    fun resume() {
        mTts.resumeSpeaking()
    }

}