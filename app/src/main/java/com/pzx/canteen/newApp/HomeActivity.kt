package com.pzx.canteen.newApp

import android.Manifest
import android.content.Intent
import android.hardware.Camera
import android.hardware.Camera.AutoFocusCallback
import android.hardware.Camera.PreviewCallback
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.pzx.canteen.R
import com.pzx.canteen.databinding.ActivityHomeBinding
import com.pzx.canteen.scan.SoundUtils
import com.pzx.canteen.utils.AnimationEndListener
import com.pzx.canteen.utils.LOG
import com.pzx.canteen.utils.TTSUtils
import com.sunmi.scan.*
import java.util.concurrent.atomic.AtomicBoolean


/**
 * File Name : HomeActivity
 * Created by : PanZX on  2021/03/10 14:06
 * Email : 644173944@qq.com
 * Github : https://github.com/Pulini
 * Remark :
 */
class HomeActivity : AppCompatActivity(), LifecycleObserver, SurfaceHolder.Callback {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: HomeViewModel
    private val loadingDialog: LoadingDialog by lazy {
        LoadingDialog(this)
    }
    private val tipsDialog: TipsDialog by lazy {
        TipsDialog(this)
    }

    private val tts: TTSUtils by lazy {
        TTSUtils(this)
    }
    private val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            init()
        } else {
            finish()
        }
    }


    private var mCamera: Camera? = null
    private var surfaceHolder: SurfaceHolder? = null

    private val scanner: ImageScanner by lazy {
        ImageScanner().apply {
            setConfig(0, Config.X_DENSITY, 2) //行扫描间隔
            setConfig(0, Config.Y_DENSITY, 2) //列扫描间隔
            setConfig(0, Config.ENABLE_MULTILESYMS, 0) //是否开启同一幅图一次解多个条码,0表示只解一个，1为多个
            setConfig(0, Config.ENABLE_INVERSE, 0) //是否解反色的条码
            setConfig(Symbol.PDF417, Config.ENABLE, 0) //是否禁止PDF417码，默认开启
        }
    }
    private val soundUtils: SoundUtils by lazy {
        SoundUtils(this, SoundUtils.RING_SOUND).apply {
            putSound(0, R.raw.beep)
        }
    }

    private val isRUN = AtomicBoolean(false)


    private var mHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == -99) {
                if (null != mCamera) {
                    mCamera?.autoFocus(this@HomeActivity.autoFocusCallback)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun init() {

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java).apply {
          
            loading.observe(this@HomeActivity) {
                if (it.isEmpty()) {
                    mCamera?.startPreview()
                    loadingDialog.dismiss()
                } else {
                    mCamera?.stopPreview()
                    loadingDialog.show(it)
                }
            }

            tips.observe(this@HomeActivity) {
                if (it.isNotEmpty()) {
                    tipsDialog.show {
                        message(it)
                    }
                }
            }


            msg.observe(this@HomeActivity) {
                if (it.isNotEmpty()) {
                    tts.startSpeaking(it)
                    when (it) {
                        "无效餐券", "餐券扫码失败" -> showInvalid()
                        "该餐券已使用" -> showUsed()
                        else -> showValid()
                    }
                    isRUN.set(false)
                    msg.value = ""
                    mCamera?.startPreview()
                }
            }
        }

        binding = DataBindingUtil.setContentView<ActivityHomeBinding>(this, R.layout.activity_home).apply {
            lifecycleOwner = this@HomeActivity
            data = viewModel

            surfaceHolder = surfaceView.holder
            surfaceHolder?.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
            surfaceHolder?.addCallback(this@HomeActivity)


            btSignOut.setOnClickListener {
                sharedPut(SHP_KEY_USER_DATA, "")
                sharedPut(SHP_KEY_USER_PASSWORD, "")
                startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
                finish()
            }
        }
    }


    private fun showValid() {
        binding.run {
            tvValid.text = getString(R.string.voucher_valid)
            tvValid.visibility = View.VISIBLE
            tvValid.startAnimation(
                    AnimationUtils.loadAnimation(
                            this@HomeActivity,
                            R.anim.add_score_anim
                    ).apply {
                        setAnimationListener(AnimationEndListener {
                            tvValid.visibility = View.GONE
                            isRUN.set(false)
                        })
                    }
            )
        }
    }


    private fun showUsed() {
        binding.run {
            tvInvalid.text = getString(R.string.voucher_used)
            tvInvalid.visibility = View.VISIBLE
            tvInvalid.startAnimation(
                    AnimationUtils.loadAnimation(
                            this@HomeActivity,
                            R.anim.add_score_anim
                    ).apply {
                        setAnimationListener(AnimationEndListener {
                            tvInvalid.visibility = View.GONE
                            isRUN.set(false)
                        })
                    }
            )
        }
    }

    private fun showInvalid() {
        binding.run {
            tvInvalid.text = getString(R.string.voucher_invalid)
            tvInvalid.visibility = View.VISIBLE
            tvInvalid.startAnimation(
                    AnimationUtils.loadAnimation(
                            this@HomeActivity,
                            R.anim.add_score_anim
                    ).apply {
                        setAnimationListener(AnimationEndListener {
                            tvInvalid.visibility = View.GONE
                            isRUN.set(false)
                        })
                    }
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scanner.destroy()
        soundUtils.release()
    }


    override fun surfaceCreated(p0: SurfaceHolder) {
        mCamera = try {
            Camera.open()
        } catch (e: Exception) {
            Log.d("DBG", "surfaceCreated: " + e.message)
            null
        }
    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
        if (surfaceHolder!!.surface == null) {
            return
        }
        try {
            mCamera?.stopPreview()
            mCamera?.setDisplayOrientation(90) //竖屏显示
            mCamera?.setPreviewDisplay(surfaceHolder)
            mCamera?.setPreviewCallback(previewCallback)
            mCamera?.startPreview()
            mCamera?.autoFocus(autoFocusCallback)
        } catch (e: Exception) {
            Log.d("DBG", "Error starting camera preview: " + e.message)
        }
    }


    override fun surfaceDestroyed(p0: SurfaceHolder) {
        if (mCamera != null) {
            mCamera?.setPreviewCallback(null)
            mCamera?.release()
            mCamera = null
        }
    }

    /**
     * 预览数据
     */
    private var previewCallback = PreviewCallback { data, camera ->
        if (isRUN.compareAndSet(false, true)) {
            val size = camera.parameters.previewSize //获取预览分辨率

            //创建解码图像，并转换为原始灰度数据，注意图片是被旋转了90度的
            val source = Image(size.width, size.height, "Y800")
            val scanImageRect = binding.finderView.getScanImageRect(size.height, size.width)
            //图片旋转了90度，将扫描框的TOP作为left裁剪
            source.setCrop(scanImageRect.top, scanImageRect.left, scanImageRect.height(), scanImageRect.width())
            source.data = data //填充数据

            if (scanner.scanImage(source) != 0) {
                soundUtils.playSound(0, SoundUtils.SINGLE_PLAY)
                scanner.results.map {
                    mCamera?.stopPreview()
                    LOG.e("Result=${it.result}")
                    viewModel.updateCanteenQRCard(it.result)
                }
            } else {
                isRUN.set(false)
            }

        }
    }

    /**
     * 自动对焦回调
     */
    var autoFocusCallback: AutoFocusCallback = AutoFocusCallback { _, _ ->
        mHandler.sendEmptyMessageDelayed(-99, 1000)
    }

}