package com.ostah_app.views.user.splash

import BaseActivity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.ostah_app.R
import com.ostah_app.utiles.Q
import com.ostah_app.views.user.introSlider.IntroWizardActivity
import com.ostah_app.views.user.login.LoginActivity
import java.util.*

class SplashActivity : BaseActivity() {
    private var change=""
    private var isLogin=false
    var isFristTime=true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        initFirbaseMessaging()
        isFristTime=preferences!!.getBoolean(Q.IS_FIRST_TIME, Q.FIRST_TIME)
        if (true) {
            preferences!!.putString("language", "Arabic")
            preferences!!.commit()
        }
        setLocalization()
        handelSpalash()

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
            if (true/*isFristTime*/) {
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
}