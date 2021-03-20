package com.ostah_app.data.remote.objects

import com.google.gson.annotations.SerializedName

data class AboutUsModel(
    @SerializedName("about_us")
    var about_us: String,
    @SerializedName("privacy_policy")
    var privacy_policy: String,
    @SerializedName("contact_us")
    var contact_us: String

)