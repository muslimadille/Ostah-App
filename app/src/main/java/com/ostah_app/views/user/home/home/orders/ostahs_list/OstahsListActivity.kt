package com.ostah_app.views.user.home.home.orders.ostahs_list

import BaseActivity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ostah_app.R
import com.ostah_app.data.remote.apiServices.ApiClient
import com.ostah_app.data.remote.apiServices.SessionManager
import com.ostah_app.data.remote.objects.*
import com.ostah_app.views.user.home.home.orders.ostahs_list.new_order.DirectOrderActivity
import kotlinx.android.synthetic.main.activity_ostahs_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OstahsListActivity : BaseActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var apiClient: ApiClient
    var lat="33.22092649999999736110112280584871768951416015625"
    var lng="43.6847594999999984111127560026943683624267578125"
    var servceId=0
    var servceName="قائمة الفنيين"
    var servicesImg=""


    private var ostahssList:MutableList<UserResposeModel> = ArrayList()

    private var ostahsListAddapter: OstahsListAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ostahs_list)
        servceId=intent.getIntExtra("service_id",0)
        intent.getStringExtra("service_name")?.let { servceName=it }
        servicesImg=intent.getStringExtra("service_img")!!
        lat=intent.getStringExtra("lat")!!
        lng=intent.getStringExtra("lng")!!
        service_name_txt.text=servceName
        initRVAdapter()
        getOstahsList()
        OnDirectOrderClicked()
    }
    private fun initRVAdapter(){
        val layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        ostahs__list_rv?.layoutManager = layoutManager
        ostahsListAddapter = OstahsListAdapter(this, ostahssList)
        ostahs__list_rv?.adapter = ostahsListAddapter
    }
    private fun getOstahsList() {
        onObserveStart()
        apiClient = ApiClient()
        sessionManager = SessionManager(this)
        apiClient.getApiService(this).getOstahsList(servceId,lat,lng)
            .enqueue(object : Callback<BaseResponseModel<OstahList>> {
                override fun onFailure(call: Call<BaseResponseModel<OstahList>>, t: Throwable) {
                    alertNetwork(false)
                }

                override fun onResponse(
                    call: Call<BaseResponseModel<OstahList>>,
                    response: Response<BaseResponseModel<OstahList>>
                ) {
                    if (response!!.isSuccessful) {
                        if(response.body()!!.message.toString().contains("User not Found!")){
                            onObserveSuccess()
                            no_data_lay?.visibility= View.VISIBLE
                            ostahs__list_rv?.visibility= View.GONE
                        }
                        if (response.body()!!.success&&response.body()!!.data!=null) {
                            response.body()!!.data!!.let {
                                ostahssList.addAll(it.ostahList)
                                ostahsListAddapter!!.notifyDataSetChanged()
                                onObserveSuccess()
                                if(it.ostahList.isNotEmpty()){
                                    no_data_lay?.visibility= View.GONE
                                    ostahs__list_rv?.visibility= View.VISIBLE

                                }else{
                                    no_data_lay?.visibility= View.VISIBLE
                                    ostahs__list_rv?.visibility= View.GONE
                                }
                            }
                        } else  {
                            onObserveSuccess()
                            no_data_lay?.visibility= View.VISIBLE
                            ostahs__list_rv?.visibility= View.GONE
                        }

                    } else {
                        onObservefaled()
                        Toast.makeText(this@OstahsListActivity, "connect faid", Toast.LENGTH_SHORT).show()

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
                getOstahsList()
                dialog.dismiss()}
            alertBuilder.setNegativeButton(R.string.dismiss) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        }
        alertBuilder.show()
    }
    private fun onObserveStart(){
        progrss_lay?.let {
            it.visibility= View.VISIBLE
        }
        ostahs__list_rv?.visibility= View.GONE
    }
    private fun onObserveSuccess(){
        progrss_lay?.let {
            it.visibility= View.GONE
        }
        ostahs__list_rv?.let {
            it.visibility= View.VISIBLE
        }
    }
    private fun onObservefaled(){
        progrss_lay?.let {
            it.visibility= View.GONE
        }
        ostahs__list_rv?.let {
            it.visibility= View.VISIBLE
        }
    }
    private fun OnDirectOrderClicked(){
        direct_order_btn.setOnClickListener {
            val intent=Intent(this,DirectOrderActivity::class.java)
            intent.putExtra("service_id",servceId)
            intent.putExtra("service_name",servceName)
            intent.putExtra("service_img",servicesImg)
            startActivity(intent)

        }
    }
}