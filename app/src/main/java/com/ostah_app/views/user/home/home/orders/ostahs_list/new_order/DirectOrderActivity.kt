package com.ostah_app.views.user.home.home.orders.ostahs_list.new_order

import BaseActivity
import SpinnerAdapterCustomFont
import android.Manifest
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.ostah_app.R
import com.ostah_app.data.remote.apiServices.ApiClient
import com.ostah_app.data.remote.apiServices.SessionManager
import com.ostah_app.data.remote.objects.BaseResponseModel
import com.ostah_app.data.remote.objects.OrderTecket
import com.ostah_app.data.remote.objects.Services
import com.ostah_app.data.remote.objects.ServicesModel
import com.ostah_app.views.user.base.GlideObject
import com.ostah_app.views.user.home.MainActivity
import kotlinx.android.synthetic.main.activity_direct_order.*
import kotlinx.android.synthetic.main.activity_direct_order.profile_lay
import kotlinx.android.synthetic.main.activity_direct_order.progrss_lay
import kotlinx.android.synthetic.main.activity_direct_order.save_btn_lay
import kotlinx.android.synthetic.main.activity_direct_order.sercices_spinner_lay
import kotlinx.android.synthetic.main.activity_direct_order.service_spinner
import kotlinx.android.synthetic.main.activity_direct_order.user_img
import kotlinx.android.synthetic.main.activity_osta_profile_details.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class DirectOrderActivity : BaseActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var apiClient: ApiClient
    var service_name=""
    var service_img=""
    var service_id=0
    //inputs
    var title=""
    var decription=""
    var date=""
    var isNow:Int=0
    //date

    private lateinit var servicesSpinnerAdapter: SpinnerAdapterCustomFont
    private var servicesNamesList = ArrayList<String>()
    private var servicesList = ArrayList<Services>()
    private var selectedServiceId=0
    
    lateinit var dpd: DatePickerDialog

    //location-----------------------------------------------------
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var lastLocation: Location? = null
    private var latitudeLabel: String? = null
    private var longitudeLabel: String? = null

    private val TAG = "LocationProvider"
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 52
    lateinit var locationRequest: LocationRequest

    var lat="33.22092649999999736110112280584871768951416015625"
    var lng="43.6847594999999984111127560026943683624267578125"
    //-------------------------------------------------------------

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_direct_order)
        getIntentValues()
        setOstahData()
        onSendClicked()
        pickDate()

        onNowChecked()
       if(service_id==0){
           sercices_spinner_lay.visibility=View.VISIBLE
           map_lay.visibility=View.VISIBLE
           servicesObserver()
           initSpinner()
           implementListeners()
       }else{
           sercices_spinner_lay.visibility=View.GONE
           map_lay.visibility=View.GONE

       }
    }

    private fun getIntentValues(){
        service_name=intent.getStringExtra("service_name")!!
        service_img=intent.getStringExtra("service_img")!!
        service_id=intent.getIntExtra("service_id",0)
        lat=intent.getStringExtra("lat")!!
        lng=intent.getStringExtra("lng")!!
    }
    private fun setOstahData(){
        GlideObject.GlideProfilePic(this, service_img,user_img)
        ostah_name_txt.text=service_name

    }







    private fun creatNewOrder(){
        onObserveStart()
        apiClient = ApiClient()
        sessionManager = SessionManager(this)
        apiClient.getApiService(this).createDirectOrder(service_id,decription,title,date,isNow,lat,lng)
                .enqueue(object : Callback<BaseResponseModel<OrderTecket>> {
                    override fun onFailure(call: Call<BaseResponseModel<OrderTecket>>, t: Throwable) {
                        alertNetwork(false)
                    }

                    override fun onResponse(
                            call: Call<BaseResponseModel<OrderTecket>>,
                            response: Response<BaseResponseModel<OrderTecket>>
                    ) {
                        if (response!!.isSuccessful) {
                            if (response.body()!!.success) {
                                response.body()!!.data!!.let {
                                    if (it!=null) {
                                        onObserveSuccess()
                                        Toast.makeText(this@DirectOrderActivity, "success", Toast.LENGTH_SHORT).show()

                                        val intent= Intent(this@DirectOrderActivity, MainActivity::class.java)
                                        this@DirectOrderActivity.startActivity(intent)
                                    } else {
                                        onObservefaled()
                                        Toast.makeText(this@DirectOrderActivity, "faid", Toast.LENGTH_SHORT).show()

                                    }

                                }
                            } else {
                                onObservefaled()
                                Toast.makeText(this@DirectOrderActivity, "faid", Toast.LENGTH_SHORT).show()

                            }

                        } else {
                            Toast.makeText(this@DirectOrderActivity, "connect faid", Toast.LENGTH_SHORT).show()

                        }

                    }


                })
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun onSendClicked(){
        save_btn_lay.setOnClickListener {
            validatInputs()
        }
    }
    private fun onObserveStart(){
        progrss_lay?.let {
            it.visibility= View.VISIBLE
        }
        profile_lay.visibility= View.GONE
    }
    private fun onObserveSuccess(){
        progrss_lay?.let {
            it.visibility= View.GONE
        }
        profile_lay?.let {
            it.visibility= View.VISIBLE
        }
    }
    private fun onObservefaled(){
        progrss_lay?.let {
            it.visibility= View.GONE
        }
        profile_lay?.let {
            it.visibility= View.VISIBLE
        }
    }
    fun alertNetwork(isExit: Boolean = true) {
        val alertBuilder = AlertDialog.Builder(this)
        //alertBuilder.setTitle(R.string.error)
        alertBuilder.setMessage(R.string.no_internet)
        if (isExit) {
            // alertBuilder.setPositiveButton(R.string.exit) { dialog: DialogInterface, _: Int -> context!!.finish() }
        } else {
            alertBuilder.setPositiveButton(R.string.try_again) { dialog: DialogInterface, _: Int ->
                creatNewOrder()
                dialog.dismiss()}
            alertBuilder.setNegativeButton(R.string.dismiss) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        }
        alertBuilder.show()
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun validatInputs(){
        if(order_title_txt.text.isNotEmpty()){
            title=order_title_txt.text.toString()
            order_title_header.setTextColor(getColor(R.color.gray))

        }else{
            order_title_header.setTextColor(getColor(R.color.red))
            Toast.makeText(this, "أدخل جميع البيانات", Toast.LENGTH_SHORT).show()
        }
        if(date_txt.text.isNotEmpty()){
            date=date_txt.text.toString()
            date_header.setTextColor(getColor(R.color.gray))

        }else{
            date_header.setTextColor(getColor(R.color.red))
            Toast.makeText(this, "أدخل جميع البيانات", Toast.LENGTH_SHORT).show()}
        if(description_txt.text.isNotEmpty()){
            decription=description_txt.text.toString()
            description_header.setTextColor(getColor(R.color.gray))

        }else{
            description_header.setTextColor(getColor(R.color.red))
            Toast.makeText(this, "أدخل جميع البيانات", Toast.LENGTH_SHORT).show()}

        if(title.isNotEmpty()&&decription.isNotEmpty()&&date.isNotEmpty()){
            creatNewOrder()
        }
    }
    private fun pickDate(){
        var calendar= Calendar.getInstance()

        var year=calendar.get(Calendar.YEAR)
        var month=calendar.get(Calendar.MONTH)
        var day=calendar.get(Calendar.DAY_OF_MONTH)
        date_picker_btn.setOnClickListener {
            dpd= DatePickerDialog(this,
                    { view, myear, mMonth, mdayOfMonth ->

                        var month = ""
                        var day = ""
                        if (mMonth < 10) {
                            month = "0${mMonth+1}"
                        } else {
                            month = "${mMonth+1}"
                        }
                        if (mdayOfMonth < 10) {
                            day = "0$mdayOfMonth"
                        } else {
                            day = "$mdayOfMonth"
                        }
                        date="$myear-$month-$day"+" "+"00:00:00"
                        date_txt.text=date.toString()
                    }, year, month, day
            )
            dpd.show()

        }
    }
    private fun onNowChecked(){
        if(isNow_cb.isChecked){
            isNow=1
        }else{
            isNow=0
        }
        isNow_cb.setOnClickListener {
            if(isNow_cb.isChecked){
                isNow=1
            }else{
                isNow=0
            }
        }

    }
    private fun initSpinner(){
        servicesSpinnerAdapter = SpinnerAdapterCustomFont(this, android.R.layout.simple_spinner_item, servicesNamesList)
        servicesSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        servicesSpinnerAdapter.textSize = 12
        service_spinner.adapter = servicesSpinnerAdapter
    }
    private fun implementListeners() {

        service_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if(servicesList.isNotEmpty()){
                    selectedServiceId=servicesList[position].id
                    service_id=selectedServiceId
                }

            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }


    }
    private fun servicesObserver() {
        onObserveStart()
        apiClient = ApiClient()
        sessionManager = SessionManager(this)
        apiClient.getApiService(this).getAllServices()
            .enqueue(object : Callback<BaseResponseModel<ServicesModel>> {
                override fun onFailure(call: Call<BaseResponseModel<ServicesModel>>, t: Throwable) {
                    alertNetwork(false)
                }

                override fun onResponse(
                    call: Call<BaseResponseModel<ServicesModel>>,
                    response: Response<BaseResponseModel<ServicesModel>>
                ) {
                    if (response!!.isSuccessful) {
                        if (response.body()!!.success) {
                            response.body()!!.data!!.let {
                                if (it.services.isNotEmpty()) {
                                    selectedServiceId=it.services[0].id
                                    service_id=selectedServiceId
                                    servicesList.addAll(it.services)
                                    servicesList.forEach { ser->
                                        if(!servicesNamesList.contains(ser.name))servicesNamesList.add(ser.name)
                                    }
                                    servicesSpinnerAdapter!!.notifyDataSetChanged()
                                    onObserveSuccess()
                                } else {
                                    onObservefaled()
                                    Toast.makeText(this@DirectOrderActivity, "empty", Toast.LENGTH_SHORT).show()
                                }

                            }
                        } else {
                            onObservefaled()
                            Toast.makeText(this@DirectOrderActivity, "faid", Toast.LENGTH_SHORT).show()

                        }

                    } else {
                        Toast.makeText(this@DirectOrderActivity, "connect faid", Toast.LENGTH_SHORT).show()

                    }

                }


            })
    }
}