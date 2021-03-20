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
    fun cancelOrder(@Url url: String): Call<BaseResponseModel<singleTicket>>

    @POST
    fun doneOrder(@Url url: String,
                  @Query("price")price:String,
                  @Query("rate")rate:String,
                  @Query("comment")comment:String): Call<BaseResponseModel<singleTicket>>

    @GET(Q.USER_SLIDERS)
    fun fitchUserSLiderImages():Call<BaseResponseModel<SliderModel>>

    @GET(Q.ABOUT)
    fun getAppInfo():Call<BaseResponseModel<AboutUsModel>>

    @GET(Q.PRIVACY)
    fun getPrivacyInfo():Call<BaseResponseModel<AboutUsModel>>

    @GET(Q.CONTACT_US)
    fun getPhoneInfo():Call<BaseResponseModel<AboutUsModel>>
    @POST(Q.USER_UPDATE)
    @FormUrlEncoded
    fun updateUser(
        @Field("email")email:String,
        @Field("name")name:String,
        @Field("image")image:String,
        @Field("distance")distance:Int,
        @Field("service_id") service_id:Int,
        @Field("lat")lat:String,
        @Field("phonenumber")phonenumber:String,
        @Field("lng")lng:String): Call<BaseResponseModel<LoginResponseModel>>
    @POST(Q.USER_REGISTER)
    @FormUrlEncoded
    fun userRegister(
        @Field("name")name:String,
        @Field("email")email:String,
        @Field("user_type_id")user_type_id:Int,
        @Field("phonenumber")phonenumber:String,
        @Field("gender_id") gender_id:Int,
        @Field("service_id")service_id:Int,
        @Field("distance")distance:Int,
        @Field("lat")lat:String,
        @Field("lng")lng:String,
        @Field("password")password:String,
        @Field("password_confirmation")password_confirmation:String,
        @Field("accept_privacy_terms")accept_privacy_terms:Int,): Call<BaseResponseModel<LoginResponseModel>>
    @POST(Q.VERIFICATION_API)
    @FormUrlEncoded
    fun userVerification(
        @Field("phonenumber") phonenumber:String,
        @Field("user_type") user_type:String): Call<BaseResponseModel<Any>>
    @POST(Q.SEND_CODE_API)
    @FormUrlEncoded
    fun sendVerificationNum(
        @Field("phonenumber") phonenumber:String,
        @Field("user_type") user_type:String,
        @Field("code") code:String): Call<BaseResponseModel<Any>>
    /******************************OSTAH APIS****************************************/
    @GET(Q.OSTAH_LAST_ORDERS)
    fun getOstahOrders():Call<BaseResponseModel<List<Tickets>>>

    @GET(Q.OSTAH_PROFILE)
    fun getOstahProfile():Call<BaseResponseModel<OstahUserModel>>

    @GET
    fun getSenderData(@Url url: String): Call<BaseResponseModel<NormalUserModel>>

    @GET(Q.OSTAH_SLIDERS)
    fun fitchOstahSLiderImages():Call<BaseResponseModel<SliderModel>>


}