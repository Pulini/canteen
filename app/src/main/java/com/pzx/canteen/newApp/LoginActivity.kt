package com.pzx.canteen.newApp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModelProvider
import com.pzx.canteen.R
import com.pzx.canteen.databinding.ActivityLoginBinding


/**
 * File Name : LoginActivity
 * Created by : PanZX on  2021/03/10 10:47
 * Email : 644173944@qq.com
 * Github : https://github.com/Pulini
 * Remark :
 */
class LoginActivity : AppCompatActivity(), LifecycleObserver {

    private val loadingDialog :LoadingDialog by lazy{
        LoadingDialog(this)
    }
    private val tipsDialog :TipsDialog by lazy{
        TipsDialog(this)
    }

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(sharedGet(SHP_KEY_USER_PASSWORD,"").toString().isNotEmpty()){
            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
            finish()
        }
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java).apply {
            loading.observe(this@LoginActivity) {
                if (it.isEmpty()) {
                    loadingDialog.dismiss()
                } else {
                    loadingDialog.show(it)
                }
            }

            tips.observe(this@LoginActivity) {
                if (it.isNotEmpty()) {
                    tipsDialog.show {
                        message(it)
                    }
                }
            }

            loginResult.observe(this@LoginActivity) {
                if (it) {
                    startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                }
            }
        }

        binding = DataBindingUtil.setContentView<ActivityLoginBinding>(this, R.layout.activity_login).apply {
            lifecycleOwner = this@LoginActivity
            data = viewModel
            btLogin.setOnClickListener { viewModel.login() }
        }
    }
}