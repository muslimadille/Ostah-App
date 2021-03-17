package com.ostah_app.data.remote.objects

import com.google.gson.annotations.SerializedName

data class ServicesModel (
        @SerializedName("services")
        var services:List<Services>,

        )
data class Services(
        @SerializedName("id")
        var id:Int,
        @SerializedName("name")
        var name:String,
        @SerializedName("image")
        var image:String,
        @SerializedName("active")
        var active:Int
)