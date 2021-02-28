package com.ostah_app.data.remote.objects

import com.google.gson.annotations.SerializedName

data class LoginResponseModel(
    @SerializedName("user")
    var user: UserResposeModel,
    @SerializedName("status")
    var status: Int,
    @SerializedName("token")
    var token: String )