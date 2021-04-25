package com.ostah_app.data.remote.objects

import com.google.gson.annotations.SerializedName

data class UserResposeModel (
    @SerializedName("id")
    var id: Long,
    @SerializedName("image")
    var image: String,
    @SerializedName("name")
    var name: String,
    @SerializedName("phonenumber")
    var phonenumber: String,
    @SerializedName("email")
    var email: String,
    @SerializedName("gender_id")
    var gender_id: Int,
    @SerializedName("service_id")
    var service_id: Int,
    @SerializedName("lat")
    var lat: Float,
    @SerializedName("lng")
    var lng: Float,
    @SerializedName("distance")
    var distance: Long,
    @SerializedName("rating")
    var rating: Double,
    @SerializedName("service")
    var service: Services,
    @SerializedName("distance_user_osta")
    var distance_user_osta :Double,

)
data class OstahList(
    @SerializedName("users")
    var ostahList:ArrayList<UserResposeModel>
)