package com.ostah_app.data.remote.objects

import com.google.gson.annotations.SerializedName

data class NormalUserModel (
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

        )