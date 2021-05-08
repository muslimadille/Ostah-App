package com.ostah_app.views.user.splash

import BaseActivity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.ostah_app.R
import com.ostah_app.data.remote.apiServices.ApiClient
import com.ostah_app.data.remote.apiServices.SessionManager
import com.ostah_app.data.remote.objects.AboutUsModel
import com.ostah_app.data.remote.objects.BaseResponseModel
import com.ostah_app.data.remote.objects.ContactUs
import com.ostah_app.utiles.Q
import com.ostah_app.views.user.home.more.ContactUsActivity
import com.ostah_app.views.user.introSlider.IntroWizardActivity
import com.ostah_app.views.user.login.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class SplashActivity : BaseActivity() {
    private var change=""
    private var isLogin=false
    var isFristTime=true
    private lateinit var sessionManager: SessionManager
    private lateinit var apiClient: ApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        contactUs()
        initFirbaseMessaging()

        isFristTime=preferences!!.getBoolean(Q.IS_FIRST_TIME, Q.FIRST_TIME)
        if (true) {
            preferences!!.putString("language", "Arabic")
            preferences!!.commit()
        }


    }
    private fun setLocalization(){
        val language = preferences!!.getString("language", "en")
        if (language =="Arabic") {
            change="ar"
        } else if (language=="English" ) {
            change = "en"
        }else {
            change =""
        }
        dLocale = Locale(change) //set any locale you want here

    }
    private fun handelSpalash(){

        Handler().postDelayed({
            if (isFristTime) {
                preferences!!.putBoolean(Q.IS_FIRST_TIME, false)
                preferences!!.putString("language", "Arabic")
                preferences!!.commit()
                val intent = Intent(this@SplashActivity, IntroWizardActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 2000)

    }
    private fun initFirbaseMessaging(){
       // FirebaseMessaging.getInstance().isAutoInitEnabled=true
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                //Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            val msg = token.toString()
           // Log.d(TAG, msg)
            //Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            preferences!!.putString(Q.NOTIFICATION_TOKEN,msg)
            preferences!!.commit()
        })

    }
    private fun contactUs() {
        apiClient = ApiClient()
        sessionManager = SessionManager(this)
        apiClient.getApiService(this).getPhoneInfo()
            .enqueue(object : Callback<BaseResponseModel<ContactUs>> {
                override fun onFailure(call: Call<BaseResponseModel<ContactUs>>, t: Throwable) {
                    alertNetwork(false)
                    //Toast.makeText(this@SplashActivity, "تاكد من اتصال الانترنت", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(
                    call: Call<BaseResponseModel<ContactUs>>,
                    response: Response<BaseResponseModel<ContactUs>>
                ) {
                    if (response!!.isSuccessful) {
                        if (response.body()!!.success) {
                            response.body()!!.data!!.let {
                                if(it.contact_us!=null){
                                    preferences!!.putString("app_whats",it.contact_us.contact_us)
                                    preferences!!.putString("app_phone1_number",it.contact_us.phone1)
                                    preferences!!.putString("app_phone1_icon",it.contact_us.icon1)
                                    preferences!!.putString("app_phone1_name",it.contact_us.name1)
                                    preferences!!.putString("app_phone2_number",it.contact_us.phone2)
                                    preferences!!.putString("app_phone2_icon",it.contact_us.icon2)
                                    preferences!!.putString("app_phone2_name",it.contact_us.name2)
                                    preferences!!.putString("app_link",it.contact_us.google_play)
                                    preferences!!.commit()
                                    setLocalization()
                                    handelSpalash()
                                }
                            }
                        } else {
                            Toast.makeText(this@SplashActivity, "faid", Toast.LENGTH_SHORT).show()
                        }

                    } else {
                        Toast.makeText(this@SplashActivity, "connect faid", Toast.LENGTH_SHORT).show()

                    }
                }

            })
    }
    fun alertNetwork(isExit: Boolean = true) {
        val alertBuilder = AlertDialog.Builder(this)
        //alertBuilder.setTitle(R.string.error)
        alertBuilder.setMessage("تأكد من إتصال الإنترنت")
        if (isExit) {
            // alertBuilder.setPositiveButton(R.string.exit) { dialog: DialogInterface, _: Int -> context!!.finish() }
        } else {
            alertBuilder.setPositiveButton(R.string.try_again) { dialog: DialogInterface, _: Int ->
                contactUs()
                dialog.dismiss()}
            alertBuilder.setNegativeButton(R.string.dismiss) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        }
        alertBuilder.show()
    }

}