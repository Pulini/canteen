package com.pzx.canteen.newApp

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


/**
 * File Name : RetrofitBuild
 * Created by : PanZX on 2020/07/01
 * Email : 644173944@qq.com
 * Github : https://github.com/Pulini
 * Remark： Retrofit初始化工具
 */

//毫秒
private const val millisecond: Long = 1000

// WebService 连接超时时间
private const val connectTimeOut: Long = millisecond * 20

//WebService  读取超时时间(默认20s)
private const val readTimeOut: Long = millisecond * 20

//WebService  写入超时时间(默认20s)
private const val writeTimeOut: Long = millisecond * 20


/**
 * 创建Retrofit实体
 */
val retrofit: WebApi = Retrofit.Builder()
        .baseUrl(BaseUrl)
        .addConverterFactory(GsonConverterFactory.create())//Gson格式工厂
        .client( //创建Http委托
                OkHttpClient.Builder().apply {
                    //请求超时
                    readTimeout(readTimeOut, TimeUnit.SECONDS)

                    //发送超时
                    writeTimeout(writeTimeOut, TimeUnit.SECONDS)

                    //重连超时
                    connectTimeout(connectTimeOut, TimeUnit.SECONDS)

                    //重连失败自动重试
                    retryOnConnectionFailure(true)

                    //添加接口日志
                    addInterceptor(HttpLoggingInterceptor{
                        Log.e("Pan",it)
                    }.setLevel(HttpLoggingInterceptor.Level.BODY))

                }.build()
        )
        .build()
        .create(WebApi::class.java)

data class Response<out T>(
        val ResultCode: Int = ResultError,//0失败、1成功、2需要重新登录
        val Data: T,
        val Message: String = ""
) {
    companion object {
        const val ResultError = 0
        const val ResultSuccess = 1
    }
}