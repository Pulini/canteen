package com.pzx.canteen.newApp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cn.jiguang.analytics.android.api.CalculateEvent
import cn.jiguang.analytics.android.api.CountEvent
import cn.jiguang.analytics.android.api.JAnalyticsInterface
import com.google.gson.Gson
import com.pzx.canteen.App.context
import com.pzx.canteen.R
import com.pzx.canteen.newApp.Response.Companion.ResultSuccess
import com.pzx.canteen.utils.LOG
import kotlinx.coroutines.launch


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


    fun login() {
        viewModelScope.launch {
            loading.value = getContextString(R.string.logging)
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
        }
    }

    fun updateCanteenQRCard(result: String?) {
        viewModelScope.launch {
            val code = analysis(result)
            if (code.isNullOrEmpty()) {
                msg.value = "无效餐券"
                JAnalyticsInterface.onEvent(
                        context,
                        CountEvent("codeError").apply {
                            addKeyValue("UserID", sharedGet(SHP_KEY_USER_PHONE, "").toString())
                            addKeyValue("QRCode", result)
                        }
                )
            } else {
                loading.value = getContextString(R.string.submitting)
                retrofit.updateCanteenQRCard(code).apply {
                    loading.value = ""
                    msg.value = Message
                    JAnalyticsInterface.onEvent(
                            context,
                            CountEvent(
                                    if (ResultCode == ResultSuccess) "scanSuccess" else "scanError"
                            ).apply {
                                addKeyValue("UserID", sharedGet(SHP_KEY_USER_PHONE, "").toString())
                                addKeyValue("QRCode", code)
                                addKeyValue("Message", Message)
                            }
                    )
                }
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
