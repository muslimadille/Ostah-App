package com.ostah_app.data.remote.apiServices

import com.ostah_app.data.remote.objects.*
import com.ostah_app.utiles.Q
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST(Q.LOGIN_API)
    fun login(
        @Query("email")email:String,
        @Query("password")password:String, @Query("user_type_id") userType:Int): Call<BaseResponseModel<LoginResponseModel>>

    @GET(Q.SERVICES_API)
    fun getAllServices():Call<BaseResponseModel<ServicesModel>>

}