package com.ostah_app.data.remote.objects

import com.google.gson.annotations.SerializedName

data  class OrderItemModel (
    @SerializedName("tickets")
    var tickets:ArrayList<Tickets>,
        )
data class Tickets(
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
    var lat:Double,
    @SerializedName("lng")
    var lng:Double,
    @SerializedName("reformer")
    var reformer:UserResposeModel,
    @SerializedName("status")
    var status:OrderStatus,
    @SerializedName("service")
    var service:OrderService,
    @SerializedName("user")
    var user:SubUser,
)
/****************************************************************/
data class OrderStatus(
    @SerializedName("id")
    var id:Int,
    @SerializedName("name")
    var name:String,
)
data class singleTicket(
        @SerializedName("ticket")
        var ticket:Tickets,
)
/****************************************************************/
data class OrderService (
    @SerializedName("id")
    var id:Int,
    @SerializedName("name")
    var name:String,
    @SerializedName("image")
    var image:String,
        )
/****************************************************************/
data class SubUser (
    @SerializedName("id")
    var id:Int,
    @SerializedName("name")
    var name:String,
    @SerializedName("image")
    var image:String,
    @SerializedName("phonenumber")
    var phonenumber:String,
    @SerializedName("email")
    var email:String,
    @SerializedName("gender_id")
    var gender_id:Int,
)
