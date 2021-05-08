package com.ostah_app.data.remote.objects

import com.google.gson.annotations.SerializedName

data class AboutUsModel(
    @SerializedName("about_us")
    var about_us: String,
    @SerializedName("privacy_policy")
    var privacy_policy: String,
    @SerializedName("contact_us")
    var contact_us: String,
    @SerializedName("google_play")
    var google_play: String,
    @SerializedName("app_store")
    var app_store: String,
    @SerializedName("icon1")
    var icon1: String,
    @SerializedName("name1")
    var name1: String,
    @SerializedName("phone1")
    var phone1: String,
    @SerializedName("icon2")
    var icon2: String,
    @SerializedName("name2")
    var name2: String,
    @SerializedName("phone2")
    var phone2: String,

)
data class ContactUs(
    @SerializedName("contact_us")
    var contact_us: AboutUsModel,
)