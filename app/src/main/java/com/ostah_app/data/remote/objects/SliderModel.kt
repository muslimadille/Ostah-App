package com.ostah_app.data.remote.objects

import com.google.gson.annotations.SerializedName

data class SliderModel (
        @SerializedName("slides")
        var slides:ArrayList<Slides>,
        )
data class Slides(
        @SerializedName("id")
        var id:Int,
        @SerializedName("image")
        var image:String,
        @SerializedName("main_title")
        var main_title:String,
        @SerializedName("sub_title")
        var sub_title:String,
)