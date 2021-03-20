package com.ostah_app.views.user.login

import BaseActivity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.ostah_app.R
import com.ostah_app.data.remote.apiServices.ApiClient
import com.ostah_app.data.remote.apiServices.SessionManager
import com.ostah_app.data.remote.objects.BaseResponseModel
import com.ostah_app.data.remote.objects.LoginResponseModel
import com.ostah_app.utiles.Q
import com.ostah_app.views.user.home.MainActivity
import com.ostah_app.views.user.registeration.RegisterationActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sms_verification.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : BaseActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var apiClient: ApiClient
    private var userType = -1

    //var preferences: ComplexPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //preferences = ComplexPreferences.getComplexPreferences(this, Q.PREF_FILE, Q.MODE_PRIVATE)
        directLogin()
        handelRdioStates()
        onLoginClicked()
        onRegisterClicked()
    }
    private fun directLogin(){
        if(preferences!!.getBoolean(Q.IS_LOGIN, false)){
            val intent =
                    Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    private fun onLoginClicked(){
        val loginBtn:Button=findViewById(R.id.Login_btn)
        val nameField:EditText=findViewById(R.id.username)
        val passwordField:EditText=findViewById(R.id.login_password)

        loginBtn.setOnClickListener {

            if (nameField.text.isNotEmpty() && passwordField.text.isNotEmpty()) {
                onObserveStart()
                apiClient = ApiClient()
                sessionManager = SessionManager(this)
                apiClient.getApiService(this)
                    .login(nameField.text.toString(), passwordField.text.toString(),userType)
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
                                        Intent(this@LoginActivity, MainActivity::class.java)
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
                                            this@LoginActivity,
                                            SmsVerificationActivity::class.java
                                        )
                                    intent.putExtra("phone", "")
                                    intent.putExtra("type",user)
                                    intent.putExtra("email", nameField.text.toString())
                                    intent.putExtra("password", passwordField.text.toString())
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(
                                        this@LoginActivity,
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
    }
    private fun handelRdioStates() {
        val userBtn:RadioButton=findViewById(R.id.user_btn)
        val ostaBtn:RadioButton=findViewById(R.id.osta_btn)

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
    private fun onObserveStart(){
        progrss_lay?.let {
            it.visibility= View.VISIBLE
        }
        login_lay.visibility= View.GONE
    }
    private fun onObserveSuccess(){
        progrss_lay?.let {
            it.visibility= View.GONE
        }
        login_lay?.let {
            it.visibility= View.VISIBLE
        }
    }
    private fun onObservefaled(){
        progrss_lay?.let {
            it.visibility= View.GONE
        }
        login_lay?.let {
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
                onLoginClicked()
                dialog.dismiss()}
            alertBuilder.setNegativeButton(R.string.dismiss) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        }
        alertBuilder.show()
    }
    private fun onRegisterClicked(){
        register_btn.setOnClickListener {
            val intent=Intent(this,RegisterationActivity::class.java)
            intent.putExtra("type",userType)
            startActivity(intent)
        }
    }
}