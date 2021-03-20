package com.ostah_app.views.user.home.orders

import BaseActivity
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.RatingBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.ostah_app.R
import com.ostah_app.data.remote.apiServices.ApiClient
import com.ostah_app.data.remote.apiServices.SessionManager
import com.ostah_app.data.remote.objects.BaseResponseModel
import com.ostah_app.data.remote.objects.OrderItemModel
import com.ostah_app.data.remote.objects.OrderTecket
import com.ostah_app.data.remote.objects.singleTicket
import com.ostah_app.utiles.Q
import com.ostah_app.views.user.home.MainActivity
import kotlinx.android.synthetic.main.activity_order_state.*
import kotlinx.android.synthetic.main.activity_order_state.progrss_lay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderStateActivity : BaseActivity() {
    private lateinit var apiClient: ApiClient
    private lateinit var sessionManager: SessionManager


    var status=0
    var id=0
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_state)
        status=intent.getIntExtra("status",0)
        id=intent.getIntExtra("id",0)
        setStatus()
        onCancelClicked()
        onDoneOrderClicked()
        onShowRateClicked()




    }
    private fun setStatus(){
        order_recieved_indecator.setImageResource(R.color.white)
        order_aproved_indecator.setImageResource(R.color.white)
        order_inprogres_indecator.setImageResource(R.color.white)
        order_done_indecator.setImageResource(R.color.white)
        order_deleted_indecator.setImageResource(R.color.white)
        when(status){
            1->{order_recieved_indecator.setImageResource(R.drawable.ic_base_true)}
            2->{order_aproved_indecator.setImageResource(R.drawable.ic_base_true)}
            3->{order_inprogres_indecator.setImageResource(R.drawable.ic_base_true)}
            4->{order_done_indecator.setImageResource(R.drawable.ic_base_true)}
            5->{order_deleted_indecator.setImageResource(R.drawable.ic_red_true)}
        }
    }
    private fun onCancelClicked(){
        cancel_btn.setOnClickListener {
            cancelOrder()
        }
    }
    private fun onShowRateClicked() {
        show_rate_view_btn.setOnClickListener {
            order_status_view.visibility=View.GONE
            order_rate_view.visibility=View.VISIBLE
        }
    }
    private fun cancelOrder() {
        onObserveStart()
        val url = Q.CANCEL_ORDER +"/${id}"
        apiClient = ApiClient()
        sessionManager = SessionManager(this)
        apiClient.getApiService(this).cancelOrder(url)
            .enqueue(object : Callback<BaseResponseModel<singleTicket>> {
                override fun onFailure(call: Call<BaseResponseModel<singleTicket>>, t: Throwable) {
                    alertNetwork(false)
                }
                override fun onResponse(
                    call: Call<BaseResponseModel<singleTicket>>,
                    response: Response<BaseResponseModel<singleTicket>>
                ) {
                    if (response!!.isSuccessful) {
                        if (response.body()!!.success) {
                            response.body()!!.data!!.let {
                                status=it.ticket.status_id
                                setStatus()
                                onObserveSuccess()
                                Toast.makeText(this@OrderStateActivity, "success", Toast.LENGTH_SHORT).show()
                                /*val intent= Intent(this@OrderStateActivity,MainActivity::class.java)
                                startActivity(intent)
                                finish()*/
                            }
                        } else {
                            onObservefaled()
                            Toast.makeText(this@OrderStateActivity, "faid", Toast.LENGTH_SHORT).show()

                        }

                    } else {
                        onObservefaled()
                        Toast.makeText(this@OrderStateActivity, "connect faid", Toast.LENGTH_SHORT).show()

                    }

                }


            })
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun onDoneOrderClicked(){
        done_order_btn.setOnClickListener {
            if(rating_price.text.isNotEmpty()&&order_rate_comment.text.isNotEmpty()){
                doneOrder()
            }else{
                comment_title.setTextColor(getColor(R.color.red))
                price_title.setTextColor(getColor(R.color.red))
                Toast.makeText(this@OrderStateActivity, "أكمل البيانات", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun doneOrder() {

        onObserveStart()
        val url = Q.DONE_ORDER +"/${id}"
        apiClient = ApiClient()
        sessionManager = SessionManager(this)
        apiClient.getApiService(this).doneOrder(url,rating_price.text.toString(),rating_order.rating.toString(),order_rate_comment.text.toString())
                .enqueue(object : Callback<BaseResponseModel<singleTicket>> {
                    override fun onFailure(call: Call<BaseResponseModel<singleTicket>>, t: Throwable) {
                        alertNetwork(false)
                    }
                    override fun onResponse(
                            call: Call<BaseResponseModel<singleTicket>>,
                            response: Response<BaseResponseModel<singleTicket>>
                    ) {
                        if (response!!.isSuccessful) {
                            if (response.body()!!.success) {
                                response.body()!!.data!!.let {
                                    status=it.ticket.status_id
                                    setStatus()
                                    onObserveSuccess()
                                    Toast.makeText(this@OrderStateActivity, "success", Toast.LENGTH_SHORT).show()
                                    order_status_view.visibility=View.VISIBLE
                                    order_rate_view.visibility=View.GONE
                                    /*val intent= Intent(this@OrderStateActivity,MainActivity::class.java)
                                    startActivity(intent)
                                    finish()*/
                                }
                            } else {
                                onObservefaled()
                                Toast.makeText(this@OrderStateActivity, "faid", Toast.LENGTH_SHORT).show()
                                order_status_view.visibility=View.VISIBLE
                                order_rate_view.visibility=View.GONE
                            }

                        } else {
                            onObservefaled()
                            Toast.makeText(this@OrderStateActivity, "connect faid", Toast.LENGTH_SHORT).show()
                            order_status_view.visibility=View.VISIBLE
                            order_rate_view.visibility=View.GONE

                        }

                    }


                })
    }
    fun alertNetwork(isExit: Boolean = true) {
        val alertBuilder = AlertDialog.Builder(this)
        //alertBuilder.setTitle(R.string.error)
        alertBuilder.setMessage(R.string.no_internet)
        if (isExit) {
            // alertBuilder.setPositiveButton(R.string.exit) { dialog: DialogInterface, _: Int -> context!!.finish() }
        } else {
            alertBuilder.setPositiveButton(R.string.try_again) { dialog: DialogInterface, _: Int ->
                dialog.dismiss()}
            alertBuilder.setNegativeButton(R.string.dismiss) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        }
        alertBuilder.show()
    }
    private fun onObserveStart(){
        progrss_lay?.let {
            it.visibility= View.VISIBLE
        }
        state_lay?.visibility= View.GONE
    }
    private fun onObserveSuccess(){
        progrss_lay?.let {
            it.visibility= View.GONE
        }
        state_lay?.let {
            it.visibility= View.VISIBLE
        }
    }
    private fun onObservefaled(){
        progrss_lay?.let {
            it.visibility= View.GONE
        }
        state_lay?.let {
            it.visibility= View.VISIBLE
        }
    }

}