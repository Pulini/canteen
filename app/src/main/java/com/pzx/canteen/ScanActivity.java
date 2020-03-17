package com.pzx.canteen;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.pzx.canteen.http.DoWebService;
import com.pzx.canteen.listener.OnAnimationEndListener;
import com.pzx.canteen.listener.OnHttpListener;
import com.pzx.canteen.model.ResultModel;
import com.pzx.canteen.scan.FinderView;
import com.pzx.canteen.scan.SoundUtils;
import com.pzx.canteen.utils.AnimationEndListener;
import com.pzx.canteen.utils.LOG;
import com.pzx.canteen.utils.TTSUtils;
import com.sunmi.scan.Config;
import com.sunmi.scan.Image;
import com.sunmi.scan.ImageScanner;
import com.sunmi.scan.Symbol;
import com.sunmi.scan.SymbolSet;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * File Name : ScanActivity
 * Created by : PanZX on 2020/03/13
 * Email : 644173944@qq.com
 * Github : https://github.com/Pulini
 * Remark： 扫码页面
 */
public class ScanActivity extends Activity implements SurfaceHolder.Callback, OnHttpListener {
    private Camera mCamera;
    private SurfaceHolder mHolder;
    private SurfaceView surface_view;
    private ImageScanner scanner;//声明扫描器
//    private AsyncDecode asyncDecode;
    SoundUtils soundUtils;
    private boolean vibrate;
    public int decode_count = 0;
    private AtomicBoolean isRUN = new AtomicBoolean(false);
    private FinderView finder_view;
    private TTSUtils TTS;
    private DoWebService ws = new DoWebService(this);
    private ProgressDialog progressDialog;
    private Activity mActivity;
    private TextView tv_valid;
    private TextView tv_invalid;
    Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        mActivity = this;
        init();
        tv_valid = findViewById(R.id.tv_valid);
        tv_invalid = findViewById(R.id.tv_invalid);
        TTS = new TTSUtils(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setIcon(R.mipmap.ic_launcher);
        progressDialog.setTitle(getString(R.string.wait));
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setIndeterminate(true);// 是否形成一个加载动画  true表示不明确加载进度形成转圈动画  false 表示明确加载进度
        progressDialog.setCancelable(false);//点击返回键或者dialog四周是否关闭dialog  true表示可以关闭 false表示不可关闭
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                TTS.startSpeaking(getString(R.string.camera_scan));
            }
        }, 500);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (scanner != null) {
            scanner.destroy();
        }
        if (soundUtils != null) {
            soundUtils.release();
        }
    }

    private void init() {
        surface_view = (SurfaceView) findViewById(R.id.surface_view);
        finder_view = (FinderView) findViewById(R.id.finder_view);
        mHolder = surface_view.getHolder();
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mHolder.addCallback(this);

        scanner = new ImageScanner();//创建扫描器
        scanner.setConfig(0, Config.X_DENSITY, 2);//行扫描间隔
        scanner.setConfig(0, Config.Y_DENSITY, 2);//列扫描间隔
        scanner.setConfig(0, Config.ENABLE_MULTILESYMS, 0);//是否开启同一幅图一次解多个条码,0表示只解一个，1为多个
        scanner.setConfig(0, Config.ENABLE_INVERSE, 0);//是否解反色的条码
        scanner.setConfig(Symbol.PDF417, Config.ENABLE, 0);//是否禁止PDF417码，默认开启


//        asyncDecode = new AsyncDecode();
        decode_count = 0;
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mHolder.getSurface() == null) {
            return;
        }
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
        }
        try {
            //摄像头预览分辨率设置和图像放大参数设置，非必须，根据实际解码效果可取舍
//			Camera.Parameters parameters = mCamera.getParameters();
//            parameters.setPreviewSize(800, 480);  //设置预览分辨率
            //     parameters.set("zoom", String.valueOf(27 / 10.0));//放大图像2.7倍
//            mCamera.setParameters(parameters);
            mCamera.setDisplayOrientation(90);//竖屏显示
            mCamera.setPreviewDisplay(mHolder);
            mCamera.setPreviewCallback(previewCallback);
            mCamera.startPreview();
            mCamera.autoFocus(autoFocusCallback);
        } catch (Exception e) {
            Log.d("DBG", "Error starting camera preview: " + e.getMessage());
        }
    }

    /**
     * 预览数据
     */
    Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            if (isRUN.compareAndSet(false, true)) {
                Camera.Parameters parameters = camera.getParameters();
                Camera.Size size = parameters.getPreviewSize();//获取预览分辨率

                //创建解码图像，并转换为原始灰度数据，注意图片是被旋转了90度的
                Image source = new Image(size.width, size.height, "Y800");
                Rect scanImageRect = finder_view.getScanImageRect(size.height, size.width);
                //图片旋转了90度，将扫描框的TOP作为left裁剪
                source.setCrop(scanImageRect.top, scanImageRect.left, scanImageRect.height(), scanImageRect.width());
                source.setData(data);//填充数据
//                if (!isAsyncDecode) {
//                    asyncDecode = new AsyncDecode();
//                    asyncDecode.execute(source);//调用异步执行解码
//                }
                asyncCode(source);
            }
        }
    };

    /**
     * 二维码解析
     * @param source
     */
    public void asyncCode(Image source) {
        if (scanner.scanImage(source) != 0) {
            playBeepSoundAndVibrate();//解码成功播放提示音
            SymbolSet syms = scanner.getResults();//获取解码结果
            for (Symbol sym : syms) {
                String Result = sym.getResult();
                LOG.e("Result=" + Result);
                if (!Result.isEmpty()) {
                    if(Result.startsWith("START") && Result.contains(",") && Result.endsWith("END")){
                        String[] str = Result.split(",");
                        String Address = str[0].substring(5);
                        String Code = str[1].substring(0, str[1].length() - 3);
                        if (str.length == 2) {
                            ws.submit(Address, Code);
                            progressDialog.show();
                        } else {
                            showErrorCode();
                        }
                    }else{
                        showErrorCode();
                    }
                } else {
                    isRUN.set(false);
                }
            }
        } else {
            isRUN.set(false);
        }
    }

    boolean isAsyncDecode = false;

    @Override
    public void Result(final ResultModel rm) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                if (DoWebService.WEB_UPDATECANTEENQRCARD.equals(rm.getName())) {
                    showAnimation(rm);
                }
            }
        });
    }
    /**
     * 执行扫码结果动画
     */
    public void showAnimation(ResultModel rm) {
        Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.add_score_anim);
        animation.setAnimationListener(new AnimationEndListener(new OnAnimationEndListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                tv_valid.setVisibility(View.GONE);
                tv_invalid.setVisibility(View.GONE);
                isAsyncDecode = false;
                isRUN.set(false);
            }
        }));
        if(rm.isSuccess()){
            tv_valid.setVisibility(View.VISIBLE);
            tv_valid.startAnimation(animation);
        }else{
            tv_invalid.setVisibility(View.VISIBLE);
            tv_invalid.startAnimation(animation);
        }
        TTS.startSpeaking(rm.getData().toString());
    }


    public void showErrorCode() {
        TTS.startSpeaking(getString(R.string.invalid_qr_code));
        Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.add_score_anim);
        animation.setAnimationListener(new AnimationEndListener(new OnAnimationEndListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                tv_invalid.setVisibility(View.GONE);
                isAsyncDecode = false;
                isRUN.set(false);
            }
        }));
        tv_invalid.setVisibility(View.VISIBLE);
        tv_invalid.startAnimation(animation);
    }

    /*private class AsyncDecode extends AsyncTask<Image, Integer, Integer> {
        String code = "";

        @Override
        protected Integer doInBackground(Image... params) {
            isAsyncDecode = true;
            //解码，返回值为0代表失败，>0表示成功
            int nsyms = scanner.scanImage(params[0]);
            if (nsyms != 0) {
                playBeepSoundAndVibrate();//解码成功播放提示音
                decode_count++;
                SymbolSet syms = scanner.getResults();//获取解码结果
                for (Symbol sym : syms) {
                    code = sym.getResult();
//                    sb.append("[ " + sym.getSymbolName() + " ]: " + sym.getResult() + "\n");
                }
            }
            return nsyms;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (!code.isEmpty()) {
                LOG.e("code=" + code);
                if (code.startsWith("START") && code.contains(",") && code.endsWith("END")) {
                    String[] str = code.split(",");
                    String Address = str[0].substring(5);
                    String Code = str[1].substring(0, str[1].length() - 3);
                    if (str.length == 2) {
                        ws.submit(Address, Code);
                        progressDialog.show();
                    }
                } else {
                    isAsyncDecode = false;
                    isRUN.set(false);
                    TTS.startSpeaking(getString(R.string.invalid_qr_code));
                }
            } else {
                isAsyncDecode = false;
                isRUN.set(false);
            }

        }
    }
*/
    /**
     * 自动对焦回调
     */
    Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            mHandler.postDelayed(doAutoFocus, 1000);
        }
    };

    //自动对焦
    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (null == mCamera || null == autoFocusCallback) {
                return;
            }
            mCamera.autoFocus(autoFocusCallback);
        }
    };

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera = Camera.open();
        } catch (Exception e) {
            Log.d("DBG", "surfaceCreated: " + e.getMessage());
            mCamera = null;
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private void initBeepSound() {
        if (soundUtils == null) {
            soundUtils = new SoundUtils(this, SoundUtils.RING_SOUND);
            soundUtils.putSound(0, R.raw.beep);
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        initBeepSound();
        vibrate = false;
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (soundUtils != null) {
            soundUtils.playSound(0, SoundUtils.SINGLE_PLAY);
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

}
