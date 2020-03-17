package com.pzx.canteen;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;


import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.pzx.canteen.http.Constants;
import com.pzx.canteen.http.DoWebService;
import com.pzx.canteen.listener.OnAfterTextWatcherListener;
import com.pzx.canteen.listener.OnHttpListener;
import com.pzx.canteen.listener.OnSelectedListener;
import com.pzx.canteen.model.AccountSuitModel;
import com.pzx.canteen.model.LoginModel;
import com.pzx.canteen.model.OrganizationModel;
import com.pzx.canteen.model.ResultModel;
import com.pzx.canteen.utils.AfterTextWatcher;
import com.pzx.canteen.utils.DialogUtil;
import com.pzx.canteen.utils.LanguageUtil;
import com.pzx.canteen.utils.SPUtils;
import com.pzx.canteen.utils.SpinnerSelectedListener;
import com.pzx.canteen.utils.TTSUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * File Name : LoginActivity
 * Created by : PanZX on 2020/03/13
 * Email : 644173944@qq.com
 * Github : https://github.com/Pulini
 * Remark： 登录
 */
public class LoginActivity extends AppCompatActivity implements OnHttpListener {
    Activity mActivity;
    LinearLayout ll_BKG;
    Spinner sp_account_suit;
    Spinner sp_organization;
    EditText et_user;
    EditText et_password;
    Button bt_login;
    String AS_ID = "";
    String O_ID = "";

    TTSUtils TTS;
    DoWebService ws = new DoWebService(this);
    ProgressDialog progressDialog;
    List<AccountSuitModel.table> ASlist = new ArrayList<>();
    List<OrganizationModel.table> Olist = new ArrayList<>();
    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mActivity = this;
        initview();
        initdata();

    }

    /**
     * 初始化控件
     */
    public void initview(){
        ll_BKG = findViewById(R.id.ll_BKG);
        sp_account_suit = findViewById(R.id.sp_account_suit);
        sp_organization = findViewById(R.id.sp_organization);
        et_user = findViewById(R.id.et_user);
        et_password = findViewById(R.id.et_password);
        bt_login = findViewById(R.id.bt_login);
        sp_account_suit.setOnItemSelectedListener(new SpinnerSelectedListener(new OnSelectedListener() {
            @Override
            public void OnItemSelected(int position) {
                AS_ID = ASlist.get(position).getFAccountSuitID();
                progressDialog.show();
                ws.GetOrganization(AS_ID);
            }
        }));
        sp_organization.setOnItemSelectedListener(new SpinnerSelectedListener(new OnSelectedListener() {
            @Override
            public void OnItemSelected(int position) {
                O_ID = ASlist.get(position).getFAccountSuitID();
            }
        }));

        et_user.addTextChangedListener(new AfterTextWatcher(new OnAfterTextWatcherListener() {
            @Override
            public void afterTextChanged(String s) {
                if (s.length() == 11)
                    et_password.requestFocus();
            }
        }));
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    /**
     * 初始化参数
     */
    public void initdata(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setIcon(R.mipmap.logo);
        progressDialog.setTitle(getString(R.string.wait));
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setIndeterminate(true);// 是否形成一个加载动画  true表示不明确加载进度形成转圈动画  false 表示明确加载进度
        progressDialog.setCancelable(false);//点击返回键或者dialog四周是否关闭dialog  true表示可以关闭 false表示不可关闭

        et_user.setText((String) SPUtils.get(Constants.SPKEY_USER_PHONE, ""));
        et_password.setText((String) SPUtils.get(Constants.SPKEY_USER_PASSWORD, ""));
        ws.GetDataTableAccountSuit();
        progressDialog.show();
        TTS = new TTSUtils(this);
        TTS.startSpeaking(getString(R.string.welcome));
    }
    /**
     * 登录
     */
    public void login(){
        String user = et_user.getText().toString();
        String password = et_password.getText().toString();
        if (AS_ID.isEmpty()) {
            showSnackBar(getString(R.string.spinner_account_suit));
            return;
        }
        if (O_ID.isEmpty()) {
            showSnackBar(getString(R.string.spinner_organization));
            return;
        }
        if (user.isEmpty()) {
            showSnackBar(getString(R.string.login_user));
            return;
        }
        if (password.isEmpty()) {
            showSnackBar(getString(R.string.login_passward));
            return;
        }
        sp_account_suit.setEnabled(false);
        sp_organization.setEnabled(false);
        et_user.setEnabled(false);
        et_password.setEnabled(false);
        bt_login.setEnabled(false);
        progressDialog.show();
        ws.NewLogin(AS_ID, O_ID, user, password);
    }

    /**
     * 提示框
     * @param msg
     */
    public void showSnackBar(String msg) {
        Snackbar.make(ll_BKG, msg, Snackbar.LENGTH_LONG).show();
    }

    /**
     * 处理账套数据
     * @param rm
     */
    public void setAccountSuitAdapter(final ResultModel rm) {
        ASlist.clear();
        if (rm.isSuccess()) {
            ASlist.addAll(((AccountSuitModel) rm.getData()).getDbHelperTable());
            List<String> data = new ArrayList<>();
            for (AccountSuitModel.table table : ASlist) {
                data.add(table.getCode());
            }
            String[] list = data.toArray(new String[data.size()]);
            ArrayAdapter<String> AA = new ArrayAdapter<>(
                    mActivity,
                    R.layout.support_simple_spinner_dropdown_item,
                    list
            );
            sp_account_suit.setAdapter(AA);
        } else {
            DialogUtil.showASListFailedDialog(mActivity,rm.getData().toString(),new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ws.GetDataTableAccountSuit();
                    progressDialog.show();
                    dialog.dismiss();
                }
            });
        }

    }

    /**
     * 处理组织数据
     * @param rm
     */
    public void setOrganizationAdapter(final ResultModel rm) {
        Olist.clear();
        if (rm.isSuccess()) {
            Olist.addAll(((OrganizationModel) rm.getData()).getDbHelperTable());
            List<String> data = new ArrayList<>();
            for (OrganizationModel.table table : Olist) {
                data.add(table.getCode());
            }
            String[] list = data.toArray(new String[data.size()]);
            ArrayAdapter<String> AA = new ArrayAdapter<>(
                    mActivity,
                    R.layout.support_simple_spinner_dropdown_item,
                    list
            );
            sp_organization.setAdapter(AA);
        } else {
            DialogUtil.showOListFailedDialog(mActivity,rm.getData().toString(),new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ws.GetOrganization(AS_ID);
                    progressDialog.show();
                    dialog.dismiss();
                }
            });

        }
    }

    /**
     * 登录结果
     * @param rm
     */
    public void loginResult(final ResultModel rm) {
        if (rm.isSuccess()) {
            TTS.startSpeaking(getString(R.string.login_successfully));
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    LoginModel LM = (LoginModel) rm.getData();
                    SPUtils.put(Constants.SPKEY_USER_DATA, new Gson().toJson(LM));
                    SPUtils.put(Constants.SPKEY_USER_PHONE, et_user.getText().toString());
                    SPUtils.put(Constants.SPKEY_USER_PASSWORD, et_password.getText().toString());
                    startActivity(new Intent(mActivity, MainActivity.class));
                    finish();
                }
            }, 1000);

        } else {
            sp_account_suit.setEnabled(true);
            sp_organization.setEnabled(true);
            et_user.setEnabled(true);
            et_password.setEnabled(true);
            bt_login.setEnabled(true);
            DialogUtil.showLoginFailedDialog(mActivity,rm.getData().toString(),new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        DialogUtil.showExitDialog(mActivity);
    }

    @Override
    public void Result(final ResultModel rm) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                switch (rm.getName()) {
                    case DoWebService.WEB_GETDATATABLEACCOUNTSUIT:
                        setAccountSuitAdapter(rm);
                        break;
                    case DoWebService.WEB_GETORGANIZATION:
                        setOrganizationAdapter(rm);
                        break;
                    case DoWebService.WEB_NEWPHONELOGIN:
                        loginResult(rm);
                        break;

                }
            }
        });


    }
}
