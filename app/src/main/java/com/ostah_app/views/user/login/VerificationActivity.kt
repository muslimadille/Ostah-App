package com.ostah_app.views.user.login

import BaseActivity
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.ostah_app.R
import com.ostah_app.data.remote.apiServices.ApiClient
import com.ostah_app.data.remote.apiServices.SessionManager
import com.ostah_app.data.remote.objects.BaseResponseModel
import com.ostah_app.data.remote.objects.LoginResponseModel
import com.ostah_app.utiles.Q
import com.ostah_app.views.user.home.MainActivity
import kotlinx.android.synthetic.main.activity_verification.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VerificationActivity : BaseActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var apiClient: ApiClient
    private var phone =""
    private var type =""
    private var email =""
    private var password =""
    private var key=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)
        key=intent.getIntExtra("key",0)
        type=intent.getStringExtra("type")!!
        email=intent.getStringExtra("email")!!
        password=intent.getStringExtra("password")!!
        phone=intent.getStringExtra("phone")!!
        resendMessage()
        onSendClicked()
    }
    private fun onObserveStart() {
        ver_progrss_lay.visibility = View.VISIBLE
    }

    private fun onObserveSuccess() {
        ver_progrss_lay.visibility = View.GONE
    }

    private fun onObservefaled() {
        ver_progrss_lay.visibility = View.GONE
    }

    private fun onResendClicked(){
        
            onObserveStart()
            apiClient = ApiClient()
            sessionManager = SessionManager(this)
            apiClient.getApiService(this)
                .userVerification(phone, type)
                .enqueue(object : Callback<BaseResponseModel<Any>> {
                    override fun onFailure(call: Call<BaseResponseModel<Any>>, t: Throwable) {
                        alertNetwork(false)
                        onObservefaled()
                    }

                    override fun onResponse(
                        call: Call<BaseResponseModel<Any>>,
                        response: Response<BaseResponseModel<Any>>
                    ) {
                        val loginResponse = response.body()
                        if (loginResponse != null && loginResponse!!.success) {
                            onObserveSuccess()
                            Toast.makeText(this@VerificationActivity, "الرجاء الإنتظار سيتم ارسال رقم تأكيد جديد", Toast.LENGTH_SHORT).show()
                        } else {
                            onObservefaled()
                            Toast.makeText(
                                this@VerificationActivity,
                                "خطا حاول مرة أخري",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }


                })

    }
    private fun resendMessage(){
        retry_btn.setOnClickListener {
            onResendClicked()
        }
    }

    fun alertNetwork(isExit: Boolean = true) {
        val alertBuilder = AlertDialog.Builder(this)
        //alertBuilder.setTitle(R.string.error)
        alertBuilder.setMessage(R.string.no_internet)
        if (isExit) {
            // alertBuilder.setPositiveButton(R.string.exit) { dialog: DialogInterface, _: Int -> context!!.finish() }
        } else {
            alertBuilder.setNegativeButton(R.string.dismiss) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        }
        alertBuilder.show()
    }
    private  fun sendNumber(){
        if (message_tf.text.isNotEmpty()){
            onObserveStart()
            apiClient = ApiClient()
            sessionManager = SessionManager(this)
            apiClient.getApiService(this).sendVerificationNum(phone, type,message_tf.text.toString())
                .enqueue(object : Callback<BaseResponseModel<Any>> {
                    override fun onFailure(call: Call<BaseResponseModel<Any>>, t: Throwable) {
                        alertNetwork(true)
                    }

                    override fun onResponse(
                        call: Call<BaseResponseModel<Any>>,
                        response: Response<BaseResponseModel<Any>>
                    ) {
                        val registerResponse = response.body()
                        if (response.code() != 500 && registerResponse!!.success) {
                            if (registerResponse.message.toString()
                                    .contains("{success=not verified}")
                            ) {
                                onObserveSuccess()
                                Toast.makeText(this@VerificationActivity, "تأكد من إدخال كود صحيح", Toast.LENGTH_SHORT).show()
                            } else {
                                onObserveSuccess()
                                login()
                            }


                        } else {

                            //username.text.clear()
                            //login_password.text.clear()
                            onObservefaled()
                            Toast.makeText(
                                this@VerificationActivity,
                                registerResponse!!.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }


                })
        }else{
            Toast.makeText(
                this@VerificationActivity,
                "من فضلك أدخل رقم صحيح ",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    private fun onSendClicked(){
        send_message_btn.setOnClickListener {
            keySelector()
        }
    }
    private fun login(){
        val devicToken=preferences!!.getString(Q.NOTIFICATION_TOKEN,"")
        if (true) {
            var userType=if(type.equals("user")){1}else{2}
            onObserveStart()
            apiClient = ApiClient()
            sessionManager = SessionManager(this)
            apiClient.getApiService(this)
                .login(email,password,userType,devicToken)
                .enqueue(object : Callback<BaseResponseModel<LoginResponseModel>> {
                    override fun onFailure(
                        call: Call<BaseResponseModel<LoginResponseModel>>,
                        t: Throwable
                    ) {
                        alertNetwork(true)
                    }

                    override fun onResponse(
                        call: Call<BaseResponseModel<LoginResponseModel>>,
                        response: Response<BaseResponseModel<LoginResponseModel>>
                    ) {
                        val loginResponse = response.body()

                        if (loginResponse!!.success) {
                            if (loginResponse?.data!!.status == 200 && loginResponse!!.data!!.user != null) {
                                //username.text.clear()
                                // login_password.text.clear()
                                sessionManager.saveAuthToken(loginResponse!!.data!!.token)
                                preferences!!.putBoolean(Q.IS_FIRST_TIME, false)
                                preferences!!.putBoolean(Q.IS_LOGIN, true)
                                preferences!!.putInteger(Q.USER_TYPE, userType)
                                preferences!!.putInteger(
                                    Q.USER_ID,
                                    loginResponse!!.data!!.user.id.toInt()
                                )

                                preferences!!.putString(
                                    Q.USER_NAME,
                                    loginResponse!!.data!!.user.name
                                )
                                preferences!!.putString(
                                    Q.USER_EMAIL,
                                    loginResponse!!.data!!.user.email
                                )
                                preferences!!.putString(
                                    Q.USER_PHONE,
                                    loginResponse!!.data!!.user.phonenumber.toString()
                                )
                                preferences!!.putInteger(
                                    Q.USER_GENDER,
                                    loginResponse!!.data!!.user.gender_id
                                )

                                preferences!!.commit()
                                onObserveSuccess()
                                val intent =
                                    Intent(this@VerificationActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            if (loginResponse.message.toString()
                                    .contains("User not verified")
                            ) {
                                var user = if (userType == 1) {
                                    "user"
                                } else {
                                    "reformer"
                                }
                                val intent =
                                    Intent(
                                        this@VerificationActivity,
                                        SmsVerificationActivity::class.java
                                    )
                                intent.putExtra("phone", "")
                                intent.putExtra("type",user)
                                intent.putExtra("email", email)
                                intent.putExtra("password", password)
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    this@VerificationActivity,
                                    loginResponse.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                                onObservefaled()
                            }
                            // username.text.clear()
                            // login_password.text.clear()

                        }

                    }


                })
        }
    }
    private fun keySelector(){
        if(key!=0){
            if( preferences!!.getInteger(Q.USER_TYPE,0)==1){
                updateUserPhone()
            }
            if( preferences!!.getInteger(Q.USER_TYPE,0)==2){
                updateOstahPhone()
            }

        }else{
            sendNumber()
        }
    }

    private  fun updateUserPhone(){
        if (message_tf.text.isNotEmpty()){
            onObserveStart()
            apiClient = ApiClient()
            sessionManager = SessionManager(this)
            apiClient.getApiService(this).updateUserPhoneVerify(phone,message_tf.text.toString())
                .enqueue(object : Callback<BaseResponseModel<Any>> {
                    override fun onFailure(call: Call<BaseResponseModel<Any>>, t: Throwable) {
                        alertNetwork(true)
                    }

                    override fun onResponse(
                        call: Call<BaseResponseModel<Any>>,
                        response: Response<BaseResponseModel<Any>>
                    ) {
                        val registerResponse = response.body()
                        if (response.code() != 500 && registerResponse!!.success) {
                            if (registerResponse.message.toString()
                                    .contains("{success=not verified}")
                            ) {
                                onObserveSuccess()
                                Toast.makeText(this@VerificationActivity, "تأكد من إدخال كود صحيح", Toast.LENGTH_SHORT).show()
                            } else {
                                onObserveSuccess()
                                val intent=Intent(this@VerificationActivity,LoginActivity::class.java)
                                startActivity(intent)
                            }


                        } else {

                            //username.text.clear()
                            //login_password.text.clear()
                            onObservefaled()
                            Toast.makeText(
                                this@VerificationActivity,
                                registerResponse!!.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }


                })
        }else{
            Toast.makeText(
                this@VerificationActivity,
                "من فضلك أدخل رقم صحيح ",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    private  fun updateOstahPhone(){
        if (message_tf.text.isNotEmpty()){
            onObserveStart()
            apiClient = ApiClient()
            sessionManager = SessionManager(this)
            apiClient.getApiService(this).updateOstahPhoneVerify(phone,message_tf.text.toString())
                .enqueue(object : Callback<BaseResponseModel<Any>> {
                    override fun onFailure(call: Call<BaseResponseModel<Any>>, t: Throwable) {
                        alertNetwork(true)
                    }

                    override fun onResponse(
                        call: Call<BaseResponseModel<Any>>,
                        response: Response<BaseResponseModel<Any>>
                    ) {
                        val registerResponse = response.body()
                        if (response.code() != 500 && registerResponse!!.success) {
                            if (registerResponse.message.toString()
                                    .contains("{success=not verified}")
                            ) {
                                onObserveSuccess()
                                Toast.makeText(this@VerificationActivity, "تأكد من إدخال كود صحيح", Toast.LENGTH_SHORT).show()
                            } else {
                                onObserveSuccess()
                                val intent=Intent(this@VerificationActivity,LoginActivity::class.java)
                                startActivity(intent)
                            }


                        } else {

                            //username.text.clear()
                            //login_password.text.clear()
                            onObservefaled()
                            Toast.makeText(
                                this@VerificationActivity,
                                registerResponse!!.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }


                })
        }else{
            Toast.makeText(
                this@VerificationActivity,
                "من فضلك أدخل رقم صحيح ",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}