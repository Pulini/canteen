package com.pzx.canteen;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.input.InputManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.InputDevice;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.pzx.canteen.http.Constants;
import com.pzx.canteen.http.DoWebService;
import com.pzx.canteen.listener.OnAnimationEndListener;
import com.pzx.canteen.listener.OnHttpListener;
import com.pzx.canteen.listener.OnAfterTextWatcherListener;
import com.pzx.canteen.listener.OnUsbListener;
import com.pzx.canteen.model.ResultModel;
import com.pzx.canteen.utils.ActivityUtils;
import com.pzx.canteen.utils.AfterTextWatcher;
import com.pzx.canteen.utils.AnimationEndListener;
import com.pzx.canteen.utils.DialogUtil;
import com.pzx.canteen.utils.LOG;
import com.pzx.canteen.utils.TTSUtils;
import com.pzx.canteen.utils.USBMonitor;

/**
 * File Name : MainActivity
 * Created by : PanZX on 2020/03/13
 * Email : 644173944@qq.com
 * Github : https://github.com/Pulini
 * Remark： 主页
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class MainActivity extends AppCompatActivity implements OnHttpListener, OnUsbListener {

    private LinearLayout ll_BKG;
    private Button bt_sign_out;
    private TextView tv_msg;
    private TextView tv_valid;
    private TextView tv_invalid;
    private EditText et_code;
    private Button bt_scan;
    private TTSUtils TTS;
    private DoWebService ws = new DoWebService(this);
    private ProgressDialog progressDialog;
    private Activity mActivity;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    boolean isSubmiting=false;
    //输入框文本监听
    private AfterTextWatcher atw = new AfterTextWatcher(new OnAfterTextWatcherListener() {
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public void afterTextChanged(final String s) {
            if(s.length()>0){
                if (s.startsWith("START")&&s.contains(",") && s.endsWith("END")) {
                    String[] str = s.split(",");
                    if (str.length == 2) {
                        String Address=str[0].substring(5);
                        String Code=str[1].substring(0, str[1].length() - 3);
                        isSubmiting=true;
                        ws.submit(Address,Code);
                        progressDialog.show();
                    }
                }else{
                    if (mHandler.hasCallbacks(removeText)) {
                        mHandler.removeCallbacks(removeText);
                    }
                    if(!isSubmiting){
                        mHandler.postDelayed(removeText, 1000);
                    }
                }
            }
        }
    });


    Runnable removeText = new Runnable() {
        @Override
        public void run() {
            LOG.e("removeText");
            et_code.removeTextChangedListener(atw);
            et_code.setText("");
            if(!isSubmiting){
                showErrorCode();
            }
            et_code.addTextChangedListener(atw);
            et_code.requestFocus();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mActivity = this;
        initview();
        TTS = new TTSUtils(this);
        getUsbDeviceName();

    }

    /**
     * 初始化控件
     */
    private void initview() {
        ll_BKG = findViewById(R.id.ll_BKG);
        bt_sign_out = findViewById(R.id.bt_sign_out);
        tv_msg = findViewById(R.id.tv_msg);
        et_code = findViewById(R.id.et_code);
        tv_valid = findViewById(R.id.tv_valid);
        tv_invalid = findViewById(R.id.tv_invalid);
        bt_scan = findViewById(R.id.bt_scan);
        bt_sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.showSignOutDialog(mActivity);
            }
        });
        bt_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, ScanActivity.class));
            }
        });
        et_code.addTextChangedListener(atw);
        progressDialog = new ProgressDialog(this);
        progressDialog.setIcon(R.mipmap.logo);
        progressDialog.setTitle(getString(R.string.wait));
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setIndeterminate(true);// 是否形成一个加载动画  true表示不明确加载进度形成转圈动画  false 表示明确加载进度
        progressDialog.setCancelable(false);//点击返回键或者dialog四周是否关闭dialog  true表示可以关闭 false表示不可关闭

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
        isSubmiting=false;
        et_code.setText("");
        et_code.requestFocus();
    }

    /**
     * 执行二维码错误动画
     */
    public void showErrorCode() {
        TTS.startSpeaking(getString(R.string.invalid_qr_code));
        Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.add_score_anim);
        animation.setAnimationListener(new AnimationEndListener(new OnAnimationEndListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                tv_invalid.setVisibility(View.GONE);
            }
        }));
        tv_invalid.setVisibility(View.VISIBLE);
        tv_invalid.startAnimation(animation);
    }

    /**
     * 获取USB设备名称
     * @return
     */
    public String getUsbDeviceName() {
        String name = "";
        InputManager inputManager = (InputManager) getSystemService(Context.INPUT_SERVICE);
        int[] devIds = inputManager.getInputDeviceIds();
        for (int devId : devIds) {
            InputDevice device = inputManager.getInputDevice(devId);
            if (device != null) {
                name = device.getName();
            }
        }
        return name;
    }

    @Override
    public void onBackPressed() {
        DialogUtil.showExitDialog(mActivity);
    }


    @Override
    protected void onResume() {
        super.onResume();
        TTS.Resume();
        et_code.requestFocus();
        USBMonitor.RegisterReceiver(this, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        TTS.Stop();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        TTS.Destroy();
        USBMonitor.unRegisterReceiver(this);
    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
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

    @Override
    public void onUSBChangeListener(boolean isConnect) {
        //当前界面处于前台
        if (ActivityUtils.isForeground(this)) {
            //商米的小闪扫码器连接设备后 USB依然显示未接通 但是不影响读取设备名称 所以先判断名称 再判断设备是否连接
            if (getUsbDeviceName().equals(Constants.SUNMI_DEVICE_NAME)) {
                tv_msg.setText(getString(R.string.connected));
                tv_msg.setTextColor(Color.parseColor("#3DDC84"));
                TTS.startSpeaking(getString(R.string.connected));
            } else {
                //判断USB是否接通
                if (isConnect) {
                    tv_msg.setText(getString(R.string.device_not_recognized));
                    tv_msg.setTextColor(Color.parseColor("#ff0000"));
                    TTS.startSpeaking(getString(R.string.device_not_recognized));
                } else {
                    tv_msg.setText(getString(R.string.unconnected));
                    tv_msg.setTextColor(Color.parseColor("#ff0000"));
                    TTS.startSpeaking(getString(R.string.unconnected));
                }
            }
        }
    }
}
