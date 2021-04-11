package com.ostah_app.data.remote.gcm

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ostah_app.R
import com.ostah_app.utiles.ComplexPreferences
import com.ostah_app.utiles.Q
import com.ostah_app.views.user.home.MainActivity
import com.ostah_app.views.user.splash.SplashActivity

class MyFirebaseMessagingService: FirebaseMessagingService() {
    private var preferences: ComplexPreferences? = null
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        preferences = ComplexPreferences.getComplexPreferences(this, Q.PREF_FILE, Q.MODE_PRIVATE)
        val refreshedToken= p0.toString()
        Log.d(TAG,"refreshed firebase messageng token:"+refreshedToken!!)
        preferences!!.putString(Q.NOTIFICATION_TOKEN,refreshedToken)
        preferences!!.commit()
        //this token send to server user information
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        var resultIntent = Intent(this, SplashActivity::class.java)

        if(p0?.data!=null){
            Log.d(TAG,"NOTIFICATION MESSAGE DATA :"+p0.data.toString())
        }
        val resultPendingIntent = PendingIntent.getActivity(this,
            1,
            resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        if(p0?.notification!=null){
            Log.d(TAG,"NOTIFICATION:"+p0.notification!!.toString())
                NotificationUtils.sendNotification(this,
                    "general",
                    p0.notification!!.title!!,
                    p0.notification!!.body!!, resultPendingIntent)

        }
    }

}