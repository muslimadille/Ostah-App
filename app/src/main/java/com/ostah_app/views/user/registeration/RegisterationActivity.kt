package com.ostah_app.views.user.registeration

import BaseActivity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.ostah_app.R
import com.ostah_app.data.remote.apiServices.ApiClient
import com.ostah_app.data.remote.apiServices.SessionManager
import com.ostah_app.data.remote.objects.BaseResponseModel
import com.ostah_app.data.remote.objects.LoginResponseModel
import com.ostah_app.views.user.login.SmsVerificationActivity
import kotlinx.android.synthetic.main.activity_registeration.*
import kotlinx.android.synthetic.main.activity_registeration.Login_btn
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterationActivity : BaseActivity() {
    private var isValid=true
    private var userType=0
    private var gendarType=0
    private var termsState=0

    private lateinit var sessionManager: SessionManager
    private lateinit var apiClient: ApiClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registeration)
        handelTypeRdioStates()
        handelGendarRdioStates()
        terms()
        onStratClicked()
    }
    private fun onStratClicked(){
        Login_btn.setOnClickListener {

            inputValidator()
        }
    }
    private fun inputValidator(){
        if(name_txt.text.isEmpty()||
            phone_txt.text.isEmpty()||
            email_txt.text.isEmpty()||
            pass_txt.text.isEmpty()||
            con_pass_txt.text.isEmpty()){
            isValid=false
            Toast.makeText(this, "أكمل جميع البيانات", Toast.LENGTH_SHORT).show()
        }
        if(!pass_txt.text.toString().equals(con_pass_txt.text.toString())){
            isValid=false
            Toast.makeText(this, "كلمة المرور غير متطابقة", Toast.LENGTH_SHORT).show()
        }
        if(phone_txt.text.toString().length>10||phone_txt.text.toString().length>10){
            isValid=false
            Toast.makeText(this, "ادخل رقم موبايل صحيح", Toast.LENGTH_SHORT).show()
        }
        if (isValid){
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
            } else if (ostaBtn.isChecked) {
                userType = 2
                userBtn.isChecked = false
            }
        }
        ostaBtn.setOnClickListener {
            ostaBtn.isChecked = true
            userBtn.isChecked = false
            userType = 2
            if (userBtn.isChecked) {
                userType = 1
                ostaBtn.isChecked = false
            } else if (ostaBtn.isChecked) {
                userType = 2
                userBtn.isChecked = false
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
        var user = if (userType == 1) {
            "user"
        } else {
            "reformer"
        }
        onObserveStart()
        apiClient = ApiClient()
        sessionManager = SessionManager(this)
        apiClient.getApiService(this).userRegister(name_txt.text.toString(),email_txt.text.toString(),userType,"+964"+(phone_txt.text.toString()),gendarType,1,0,"34.3333","34.3333",pass_txt.text.toString(),con_pass_txt.text.toString(),1)
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
                                            SmsVerificationActivity::class.java
                                        )
                                    intent.putExtra("phone", "")
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

}