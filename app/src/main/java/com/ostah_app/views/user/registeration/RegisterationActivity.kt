package com.ostah_app.views.user.registeration

import BaseActivity
import SpinnerAdapterCustomFont
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.ostah_app.R
import com.ostah_app.data.remote.apiServices.ApiClient
import com.ostah_app.data.remote.apiServices.SessionManager
import com.ostah_app.data.remote.objects.BaseResponseModel
import com.ostah_app.data.remote.objects.LoginResponseModel
import com.ostah_app.data.remote.objects.Services
import com.ostah_app.data.remote.objects.ServicesModel
import com.ostah_app.utiles.Q
import com.ostah_app.views.user.home.home.orders.ostahs_list.new_order.MapsActivity
import com.ostah_app.views.user.login.SmsVerificationActivity
import com.ostah_app.views.user.login.VerificationActivity
import kotlinx.android.synthetic.main.activity_registeration.*
import kotlinx.android.synthetic.main.activity_registeration.Login_btn
import kotlinx.android.synthetic.main.activity_registeration.progrss_lay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class RegisterationActivity : BaseActivity() {
    private val SECOND_ACTIVITY_REQUEST_CODE = 0
    private var isValid=true
    private var userType=0
    private var gendarType=0
    private var termsState=0
    private var selectedServiceId=0
    private var servicesNamesList = ArrayList<String>()
    private var servicesList = ArrayList<Services>()
    private lateinit var servicesSpinnerAdapter: SpinnerAdapterCustomFont
    private var lat=""
    private var lng=""


    private lateinit var sessionManager: SessionManager
    private lateinit var apiClient: ApiClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registeration)
        initSpinner()
        servicesObserver()
        implementListeners()
        handelTypeRdioStates()
        handelGendarRdioStates()
        terms()
        onStratClicked()
        onSelectLocatinoClicked()
    }
    private fun onStratClicked(){
        Login_btn.setOnClickListener {
            inputValidator()
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
                                    servicesList.addAll(it.services)
                                    selectedServiceId=it.services[0].id
                                    servicesList.forEach { ser->
                                        if(!servicesNamesList.contains(ser.name))servicesNamesList.add(ser.name)
                                    }
                                    servicesSpinnerAdapter!!.notifyDataSetChanged()
                                    onObserveSuccess()
                                } else {
                                    onObservefaled()
                                    Toast.makeText(this@RegisterationActivity, "empty", Toast.LENGTH_SHORT).show()
                                }

                            }
                        } else {
                            onObservefaled()
                            Toast.makeText(this@RegisterationActivity, "faid", Toast.LENGTH_SHORT).show()

                        }

                    } else {
                        Toast.makeText(this@RegisterationActivity, "connect faid", Toast.LENGTH_SHORT).show()

                    }

                }


            })
    }
    private fun inputValidator(){
        if(name_txt.text.toString().isEmpty()||
            phone_txt.text.toString().isEmpty()||
            pass_txt.text.toString().isEmpty()||
            con_pass_txt.text.toString().isEmpty()){
           // isValid=false
            Toast.makeText(this, "أكمل جميع البيانات", Toast.LENGTH_SHORT).show()
        }else
        if(!pass_txt.text.toString().equals(con_pass_txt.text.toString())){
            //isValid=false
            Toast.makeText(this, "كلمة المرور غير متطابقة", Toast.LENGTH_SHORT).show()
        }else
        if(phone_txt.text.toString().length>10||phone_txt.text.toString().length>10){
            //isValid=false
            Toast.makeText(this, "ادخل رقم موبايل صحيح", Toast.LENGTH_SHORT).show()
        }else
        if(selectedServiceId==0&&userType==2){
           // isValid=false
            Toast.makeText(this, "اختر نوع الخدمة", Toast.LENGTH_SHORT).show()
        }else
        if((lat.isEmpty()||lng.isEmpty())&&userType==2){
            //isValid=false
            Toast.makeText(this, "قم بتحديد الموقع", Toast.LENGTH_SHORT).show()
        }
        else {
            register()
        }

        
    }
    private fun handelTypeRdioStates() {
        val userBtn: RadioButton =findViewById(R.id.user_btn)
        val ostaBtn: RadioButton =findViewById(R.id.osta_btn)

        userBtn.isChecked = true
        userType = 1
        userBtn.setOnClickListener {
            userBtn.isChecked = true
            ostaBtn.isChecked = false
            userType = 1
            if (userBtn.isChecked) {
                userType = 1
                ostaBtn.isChecked = false
                select_service_lay.visibility=View.GONE
                map_lay.visibility=View.GONE
            } else if (ostaBtn.isChecked) {
                userType = 2
                userBtn.isChecked = false
                select_service_lay.visibility=View.VISIBLE
                map_lay.visibility=View.GONE
            }
        }
        ostaBtn.setOnClickListener {
            ostaBtn.isChecked = true
            userBtn.isChecked = false
            userType = 2
            if (userBtn.isChecked) {
                userType = 1
                ostaBtn.isChecked = false
                select_service_lay.visibility=View.GONE
                map_lay.visibility=View.GONE

            } else if (ostaBtn.isChecked) {
                userType = 2
                userBtn.isChecked = false
                select_service_lay.visibility=View.VISIBLE
                map_lay.visibility=View.VISIBLE

            }
        }

    }
    private fun handelGendarRdioStates() {
        val malBtn: RadioButton =findViewById(R.id.male_btn)
        val femailBtn: RadioButton =findViewById(R.id.female_btn)

        malBtn.isChecked = true
        gendarType = 1
        malBtn.setOnClickListener {
            malBtn.isChecked = true
            femailBtn.isChecked = false
            gendarType = 1
            if (malBtn.isChecked) {
                gendarType = 1
                femailBtn.isChecked = false
            } else if (femailBtn.isChecked) {
                gendarType = 2
                malBtn.isChecked = false
            }
        }
        femailBtn.setOnClickListener {
            femailBtn.isChecked = true
            malBtn.isChecked = false
            gendarType = 2
            if (malBtn.isChecked) {
                gendarType = 1
                femailBtn.isChecked = false
            } else if (femailBtn.isChecked) {
                gendarType = 2
                malBtn.isChecked = false
            }
        }

    }
    private fun terms(){
        terms_btn.isChecked = true
        terms_btn.setOnClickListener {
            if (terms_btn.isChecked) {
                termsState = 1
            } else  {
                termsState = 0
            }
        }
    }
    private fun register(){
        val devicToken=preferences!!.getString(Q.NOTIFICATION_TOKEN,"")
        var user = if (userType == 1) {
            "user"
        } else {
            "reformer"
        }
        onObserveStart()
        apiClient = ApiClient()
        sessionManager = SessionManager(this)
        apiClient.getApiService(this).userRegister(name_txt.text.toString(),email_txt.text.toString(),userType,"+964"+(phone_txt.text.toString()),gendarType,selectedServiceId,0,lat,lng,pass_txt.text.toString(),con_pass_txt.text.toString(),1,devicToken)
            .enqueue(object : Callback<BaseResponseModel<LoginResponseModel>> {
                override fun onFailure(call: Call<BaseResponseModel<LoginResponseModel>>, t: Throwable) {
                    alertNetwork(false)
                    onObservefaled()
                }

                override fun onResponse(
                    call: Call<BaseResponseModel<LoginResponseModel>>,
                    response: Response<BaseResponseModel<LoginResponseModel>>
                ) {
                    if (response!!.isSuccessful) {
                        if (response.body()!!.success) {
                            response.body()!!.data!!.let {
                                    onObserveSuccess()
                                    val intent =
                                        Intent(
                                            this@RegisterationActivity,
                                            VerificationActivity::class.java
                                        )
                                    intent.putExtra("phone", it.user.phonenumber.toString())
                                    intent.putExtra("type",user)
                                    intent.putExtra("email", email_txt.text.toString())
                                    intent.putExtra("password", pass_txt.text.toString())
                                    startActivity(intent)
                                    Toast.makeText(this@RegisterationActivity, "success", Toast.LENGTH_SHORT).show()


                            }
                        } else {
                            onObservefaled()
                            Toast.makeText(this@RegisterationActivity, response.body()!!.message.toString(), Toast.LENGTH_SHORT).show()


                        }

                    } else {
                        Toast.makeText(this@RegisterationActivity, "connect faid", Toast.LENGTH_SHORT).show()

                    }

                }


            })
    }
    private fun onObserveStart(){
        progrss_lay?.let {
            it.visibility= View.VISIBLE
        }
        register_lay.visibility= View.GONE
    }
    private fun onObserveSuccess(){
        progrss_lay?.let {
            it.visibility= View.GONE
        }
        register_lay?.let {
            it.visibility= View.VISIBLE
        }
    }
    private fun onObservefaled(){
        progrss_lay?.let {
            it.visibility= View.GONE
        }
        register_lay?.let {
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
                register()
                dialog.dismiss()}
            alertBuilder.setNegativeButton(R.string.dismiss) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        }
        alertBuilder.show()
    }
    private fun initSpinner(){
        servicesSpinnerAdapter = SpinnerAdapterCustomFont(this, android.R.layout.simple_spinner_item, servicesNamesList)
        servicesSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        servicesSpinnerAdapter.add("إختر نوع الخدمة")
        servicesSpinnerAdapter.textSize = 14
        service_spinner.adapter = servicesSpinnerAdapter
    }
    private fun implementListeners() {

        service_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if(servicesList.isNotEmpty()&&position!=0){
                    selectedServiceId=servicesList[position-1].id
                }

            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }


    }
    private fun onSelectLocatinoClicked(){
        // Start the SecondActivity
        map_lay.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("service_id",0)
            intent.putExtra("service_name","")
            intent.putExtra("service_img","")
            intent.putExtra("key",1)

            startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE)
        }

    }
    // This method is called when the second activity finishes
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Check that it is the SecondActivity with an OK result
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // Get String data from Intent
                address_name_txt.text = data!!.getStringExtra("address")
                lat=data!!.getStringExtra("lat")!!
                lng=data!!.getStringExtra("lng")!!

            }
        }
    }
}


