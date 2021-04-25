package com.ostah_app.data.remote.objects

import com.google.gson.annotations.SerializedName

data class OstahUserModel (
    @SerializedName("id")
    var id:Int,
    @SerializedName("image")
    var image:String,
    @SerializedName("name")
    var name:String,
    @SerializedName("phonenumber")
    var phonenumber:String,
    @SerializedName("email")
    var email:String,
    @SerializedName("gender_id")
    var gender_id:Int,
    @SerializedName("service_id")
    var service_id:Int,
    @SerializedName("distance")
    var distance:Int,
    @SerializedName("lat")
    var lat:Float,
    @SerializedName("lng")
    var lng:Float,
    @SerializedName("bonus")
    var bonus:Int,
    @SerializedName("service")
    var service:Services,
    @SerializedName("tickets_success")
    var tickets_success:Int,
    @SerializedName("tickets_cancel")
    var tickets_cancel:Int,
    @SerializedName("rating")
    var rating:Int,
    @SerializedName("distance_user_osta")
    var distance_user_osta :Double,



)