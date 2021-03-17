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

    @GET(Q.USER_PROFILE)
    fun getUserProfile():Call<BaseResponseModel<NormalUserModel>>

    @GET(Q.USER_ALL_ORDERS)
    fun getUserOrders():Call<BaseResponseModel<OrderItemModel>>
    @GET(Q.USER_PREVIOUS_ORDERS)
    fun getUserPreviousOrders():Call<BaseResponseModel<OrderItemModel>>

    @GET(Q.OSTAHS_LIST_API)
    fun getOstahsList(@Query("service_id") service_id:Int,
                      @Query("lat")lat:String,
                      @Query("lng")lng:String):Call<BaseResponseModel<OstahList>>
    @POST(Q.CREATE_ORDER)
    fun createOrder(
        @Query("reformer_id")reformer_id:Int,
        @Query("details")details:String,
        @Query("title") title:String,
        @Query("date")date:String,
        @Query("now")now:Int,
        @Query("lat")lat:String,
        @Query("lng")lng:String): Call<BaseResponseModel<OrderTecket>>
    @POST(Q.DIRECT_ORDER)
    fun createDirectOrder(
        @Query("service_id")service_id:Int,
        @Query("details")details:String,
        @Query("title") title:String,
        @Query("date")date:String,
        @Query("now")now:Int,
        @Query("lat")lat:String,
        @Query("lng")lng:String): Call<BaseResponseModel<OrderTecket>>
    @GET
    fun cancelOrder(@Url url: String): Call<BaseResponseModel<OrderTecket>>
    @GET
    fun doneOrder(@Url url: String): Call<BaseResponseModel<OrderTecket>>
    /******************************OSTAH APIS****************************************/
    @GET(Q.OSTAH_LAST_ORDERS)
    fun getOstahOrders():Call<BaseResponseModel<List<Tickets>>>
    @GET(Q.OSTAH_PROFILE)
    fun getOstahProfile():Call<BaseResponseModel<OstahUserModel>>
    @GET
    fun getSenderData(@Url url: String): Call<BaseResponseModel<NormalUserModel>>


}