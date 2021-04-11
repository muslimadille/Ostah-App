package com.ostah_app.data.remote.objects

import com.google.gson.annotations.SerializedName

data class OrderTecket (
    @SerializedName("id")
    var id:Int,
    @SerializedName("user_id")
    var user_id:Int,
    @SerializedName("title")
    var title:String,
    @SerializedName("details")
    var details:String,
    @SerializedName("status_id")
    var status_id:Int,
    @SerializedName("reformer_id")
    var reformer_id:Int,
    @SerializedName("service_id")
    var service_id:Int,
    @SerializedName("date")
    var date:String,
    @SerializedName("price")
    var price:Int,
    @SerializedName("rate")
    var rate:Int,
    @SerializedName("comment")
    var comment:String,
    @SerializedName("end_date")
    var end_date:String,
    @SerializedName("user_price")
    var user_price:Int,
    @SerializedName("ticket_type_id")
    var ticket_type_id:Int,
    @SerializedName("lat")
    var lat:Float,
    @SerializedName("lng")
    var lng:Float,
    @SerializedName("reformer")
    var reformer:UserResposeModel,
    @SerializedName("status")
    var status:Int,
    @SerializedName("service")
    var service:OrderService,
        )