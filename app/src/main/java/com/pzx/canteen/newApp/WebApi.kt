package com.pzx.canteen.newApp

import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * File Name : WebApi
 * Created by : PanZX on  2021/03/10 08:52
 * Email : 644173944@qq.com
 * Github : https://github.com/Pulini
 * Remark :
 */

interface  WebApi {

    @POST("api/User/CanteenLogin")
    suspend fun login(
            @Query("Phone") Phone: String,
            @Query("Password") Password: String,
            @Header("Language") Language: String ="zh"
    ): Response<UserBean>

    @POST("api/SsDormitory/UpdateCanteenQRCard")
    suspend fun updateCanteenQRCard(
            @Query("CanteenQRCardID") QRCard: String,
            @Query("CanteenQRCardAddress") Address: Int=getUserInfo()!!.DiningRoomID,
    ): Response<String>

}