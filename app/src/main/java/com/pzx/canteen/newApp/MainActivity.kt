//package com.pzx.canteen.newApp
//
//import android.app.Activity
//import android.hardware.Camera
//import android.hardware.Camera.AutoFocusCallback
//import android.hardware.Camera.PreviewCallback
//import android.os.AsyncTask
//import android.os.Bundle
//import android.os.Handler
//import android.os.Vibrator
//import android.util.Log
//import android.view.SurfaceHolder
//import android.view.SurfaceView
//import android.view.View
//import android.widget.TextView
//import com.pzx.canteen.R
//import com.pzx.canteen.scan.FinderView
//import com.pzx.canteen.scan.SoundUtils
//import com.sunmi.scan.*
//import java.util.concurrent.atomic.AtomicBoolean
//
//class MainActivity : Activity(), SurfaceHolder.Callback {
//    private var mCamera: Camera? = null
//    private var surfaceHolder: SurfaceHolder? = null
//    private var surface_view: SurfaceView? = null
//    private var scanner: ImageScanner? = null
//    private var autoFocusHandler: Handler? = null
//    private var asyncDecode: AsyncDecode? = null
//    var soundUtils: SoundUtils? = null
//    private var vibrate = false
//    var decode_count = 0
//    private val isRUN = AtomicBoolean(false)
//    private var finder_view: FinderView? = null
//    private var textview: TextView? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.ac_sunmi_scan_finder)
//        init()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        if (scanner != null) {
//            scanner!!.destroy()
//        }
//        if (soundUtils != null) {
//            soundUtils!!.release()
//        }
//    }
//
//    private fun init() {
//        surface_view = findViewById<View>(R.id.surface_view) as SurfaceView
//        finder_view = findViewById<View>(R.id.finder_view) as FinderView
//        textview = findViewById<View>(R.id.textview) as TextView
//        surfaceHolder = surface_view!!.holder
//        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
//        surfaceHolder.addCallback(this)
//        scanner = ImageScanner() //创建扫描器
//        scanner!!.setConfig(0, Config.X_DENSITY, 2) //行扫描间隔
//        scanner!!.setConfig(0, Config.Y_DENSITY, 2) //列扫描间隔
//        scanner!!.setConfig(0, Config.ENABLE_MULTILESYMS, 0) //是否开启同一幅图一次解多个条码,0表示只解一个，1为多个
//        scanner!!.setConfig(0, Config.ENABLE_INVERSE, 0) //是否解反色的条码
//        scanner!!.setConfig(Symbol.PDF417, Config.ENABLE, 0) //是否禁止PDF417码，默认开启
//        autoFocusHandler = Handler()
//        asyncDecode = AsyncDecode()
//        decode_count = 0
//    }
//
//    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
//        if (surfaceHolder!!.surface == null) {
//            return
//        }
//        try {
//            mCamera!!.stopPreview()
//        } catch (e: Exception) {
//        }
//        try {
//            //摄像头预览分辨率设置和图像放大参数设置，非必须，根据实际解码效果可取舍
////			Camera.Parameters parameters = mCamera.getParameters();
////            parameters.setPreviewSize(800, 480);  //设置预览分辨率
//            //     parameters.set("zoom", String.valueOf(27 / 10.0));//放大图像2.7倍
////            mCamera.setParameters(parameters);
//            mCamera!!.setDisplayOrientation(90) //竖屏显示
//            mCamera!!.setPreviewDisplay(surfaceHolder)
//            mCamera!!.setPreviewCallback(previewCallback)
//            mCamera!!.startPreview()
//            mCamera!!.autoFocus(autoFocusCallback)
//        } catch (e: Exception) {
//            Log.d("DBG", "Error starting camera preview: " + e.message)
//        }
//    }
//
//    /**
//     * 预览数据
//     */
//    var previewCallback = PreviewCallback { data, camera ->
//        if (isRUN.compareAndSet(false, true)) {
//            val parameters = camera.parameters
//            val size = parameters.previewSize //获取预览分辨率
//
//            //创建解码图像，并转换为原始灰度数据，注意图片是被旋转了90度的
//            val source = Image(size.width, size.height, "Y800")
//            val scanImageRect = finder_view!!.getScanImageRect(size.height, size.width)
//            //图片旋转了90度，将扫描框的TOP作为left裁剪
//            source.setCrop(scanImageRect.top, scanImageRect.left, scanImageRect.height(), scanImageRect.width())
//            source.data = data //填充数据
//            asyncDecode = AsyncDecode()
//            asyncDecode!!.execute(source) //调用异步执行解码
//        }
//    }
//
//    private inner class AsyncDecode : AsyncTask<Image?, Int?, Int>() {
//        private var str: String? = ""
//        protected override fun doInBackground(vararg params: Image): Int {
//            val sb = StringBuilder()
//            val src_data = params[0] //获取灰度数据
//            val startTimeMillis = System.currentTimeMillis()
//
//            //解码，返回值为0代表失败，>0表示成功
//            val nsyms = scanner!!.scanImage(src_data)
//            val endTimeMillis = System.currentTimeMillis()
//            val cost_time = endTimeMillis - startTimeMillis
//            if (nsyms != 0) {
//                playBeepSoundAndVibrate() //解码成功播放提示音
//                decode_count++
//                sb.append("计数: $decode_count, 耗时: $cost_time ms \n")
//                val syms = scanner!!.results //获取解码结果
//                for (sym in syms) {
//                    sb.append("""[ ${sym.symbolName} ]: ${sym.result}
//""")
//                }
//            }
//            str = sb.toString()
//            return nsyms
//        }
//
//        override fun onPostExecute(result: Int) {
//            super.onPostExecute(result)
//            isRUN.set(false)
//            if (null == str || str == "") {
//            } else {
//                textview!!.text = str //显示解码结果
//            }
//        }
//    }
//
//    /**
//     * 自动对焦回调
//     */
//    var autoFocusCallback: AutoFocusCallback? = AutoFocusCallback { success, camera -> autoFocusHandler!!.postDelayed(doAutoFocus, 1000) }
//
//    //自动对焦
//    private val doAutoFocus = Runnable {
//        if (null == mCamera || null == autoFocusCallback) {
//            return@Runnable
//        }
//        mCamera!!.autoFocus(autoFocusCallback)
//    }
//
//    override fun surfaceCreated(holder: SurfaceHolder) {
//        mCamera = try {
//            Camera.open()
//        } catch (e: Exception) {
//            Log.d("DBG", "surfaceCreated: " + e.message)
//            null
//        }
//    }
//
//    override fun surfaceDestroyed(holder: SurfaceHolder) {
//        if (mCamera != null) {
//            mCamera!!.setPreviewCallback(null)
//            mCamera!!.release()
//            mCamera = null
//        }
//    }
//
//    private fun initBeepSound() {
//        if (soundUtils == null) {
//            soundUtils = SoundUtils(this, SoundUtils.RING_SOUND)
//            soundUtils!!.putSound(0, R.raw.beep)
//        }
//    }
//
//    override fun onResume() {
//        // TODO Auto-generated method stub
//        super.onResume()
//        initBeepSound()
//        vibrate = false
//    }
//
//    private fun playBeepSoundAndVibrate() {
//        if (soundUtils != null) {
//            soundUtils!!.playSound(0, SoundUtils.SINGLE_PLAY)
//        }
//        if (vibrate) {
//            val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
//            vibrator.vibrate(VIBRATE_DURATION)
//        }
//    }
//
//    companion object {
//        private const val VIBRATE_DURATION = 200L
//    }
//}