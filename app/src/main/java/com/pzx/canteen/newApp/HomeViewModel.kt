package com.pzx.canteen.newApp

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cn.jiguang.analytics.android.api.CountEvent
import cn.jiguang.analytics.android.api.JAnalyticsInterface
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.pzx.canteen.R
import com.pzx.canteen.newApp.Response.Companion.ResultSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.net.UnknownHostException


/**
 * File Name : HomeViewModel
 * Created by : PanZX on  2021/03/10 10:53
 * Email : 644173944@qq.com
 * Github : https://github.com/Pulini
 * Remark :
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    val phone = MutableLiveData(sharedGet(SHP_KEY_USER_PHONE, "").toString())
    val password = MutableLiveData("")
    val loading = MutableLiveData("")
    val tips = MutableLiveData("")
    val loginResult = MutableLiveData(false)
    val msg = MutableLiveData("")
    val qrCode = MutableLiveData("")

    val saveCode = MutableLiveData("")

    private suspend fun tryCatch(
        tryBlock: suspend CoroutineScope.() -> Unit,
        finallyBlock: suspend CoroutineScope.() -> Unit
    ) {
        coroutineScope {
            try {
                tryBlock()
            } catch (e: Throwable) {
                tips.value = when (e) {
                    is SocketTimeoutException -> "服务器访问超时"
                    is UnknownHostException -> "无法连接域名"
                    is JsonParseException -> "数据解析错误"
                    else -> e.message
                }
            } finally {
                finallyBlock()
            }
        }
    }


    fun login() {
        viewModelScope.launch {
            loading.value = getContextString(R.string.logging)
            tryCatch(tryBlock = {
                retrofit.login(phone.value!!, password.value!!).apply {
                    loading.value = ""
                    if (ResultCode == ResultSuccess) {
                        sharedPut(SHP_KEY_USER_DATA, Gson().toJson(Data))
                        sharedPut(SHP_KEY_USER_PHONE, phone.value!!)
                        sharedPut(SHP_KEY_USER_PASSWORD, password.value!!)
                    } else {
                        tips.value = Message
                    }
                    loginResult.value = ResultCode == ResultSuccess
                }
            }, finallyBlock = {
                loading.value = ""
            })
        }
    }

    fun updateCanteenQRCard(result: String?) {
        viewModelScope.launch {
            val code = analysis(result)
            if (code.isNullOrEmpty()) {
                msg.value = "无效餐券"
            } else {
                loading.value = getContextString(R.string.submitting)
                qrCode.value = code
                tryCatch(tryBlock = {
                    retrofit.updateCanteenQRCard(code).apply {
                        loading.value = ""
                        msg.value = Message
                        saveCode.value = result
                    }
                }, finallyBlock = {
                    loading.value = ""
                })
            }
        }
    }

    private fun analysis(result: String?): String? {
        if (result.isNullOrEmpty()) {
            return null
        }
        result.run {
            if (startsWith("START") && contains(",") && endsWith("END")) {
                split(",").toTypedArray().run {
                    return if (this.size == 2) {
                        this[0]
                    } else {
                        null
                    }
                }
            } else {
                return null
            }
        }
    }
}
