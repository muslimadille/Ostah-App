package com.ostah_app.views.login

import BaseActivity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ostah_app.R
import com.ostah_app.data.remote.apiServices.ApiClient
import com.ostah_app.data.remote.apiServices.SessionManager
import com.ostah_app.data.remote.objects.BaseResponseModel
import com.ostah_app.data.remote.objects.LoginResponseModel
import com.ostah_app.utiles.ComplexPreferences
import com.ostah_app.utiles.Q
import com.ostah_app.views.home.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Error

class LoginActivity : BaseActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var apiClient: ApiClient
    private var userType = -1

    //var preferences: ComplexPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //preferences = ComplexPreferences.getComplexPreferences(this, Q.PREF_FILE, Q.MODE_PRIVATE)
        handelRdioStates()
        onLoginClicked()
    }

    private fun onLoginClicked(){
        val loginBtn:Button=findViewById(R.id.Login_btn)
        val nameField:EditText=findViewById(R.id.username)
        val passwordField:EditText=findViewById(R.id.login_password)

        loginBtn.setOnClickListener {

            if (nameField.text.isNotEmpty() && passwordField.text.isNotEmpty()) {
                //onObserveStart()
                apiClient = ApiClient()
                sessionManager = SessionManager(this)
                apiClient.getApiService(this)
                    .login(nameField.text.toString(), passwordField.text.toString(),userType)
                    .enqueue(object : Callback<BaseResponseModel<LoginResponseModel>> {
                        override fun onFailure(call: Call<BaseResponseModel<LoginResponseModel>>, t: Throwable) {
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
                                    //onObserveSuccess()
                                    val intent =
                                        Intent(this@LoginActivity, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            }

                            else {
                               // onObservefaled()
                               // username.text.clear()
                               // login_password.text.clear()
                                Toast.makeText(
                                    this@LoginActivity,
                                    "كلمة المرور او البريد الالكتروني غير صحيح ",
                                    Toast.LENGTH_SHORT
                                ).show()
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
    fun alertNetwork(isExit: Boolean = false) {
        val alertBuilder = AlertDialog.Builder(this)
        //alertBuilder.setTitle(R.string.error)
        alertBuilder.setMessage(R.string.no_internet)
        if (isExit) {
            alertBuilder.setPositiveButton(R.string.exit) { dialog: DialogInterface, _: Int -> finish() }
        } else {
            alertBuilder.setPositiveButton(R.string.dismiss) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        }
        try {
            if(!this.isFinishing){
                alertBuilder.show()
            }

        }catch (e: Error){}
    }
}