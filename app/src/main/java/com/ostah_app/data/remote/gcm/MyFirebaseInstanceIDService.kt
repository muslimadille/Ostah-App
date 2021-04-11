package com.ostah_app.data.remote.gcm

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.ostah_app.utiles.ComplexPreferences
import com.ostah_app.utiles.Q

const val TAG="MyFirebase"

class MyFirebaseInstanceIDService:FirebaseInstanceIdService() {
    private var preferences: ComplexPreferences? = null
    override fun onTokenRefresh() {
        preferences = ComplexPreferences.getComplexPreferences(this, Q.PREF_FILE, Q.MODE_PRIVATE)
        super.onTokenRefresh()
        val refreshedToken=FirebaseInstanceId.getInstance().token
        Log.d(TAG,"refreshed firebase messageng token:"+refreshedToken!!)
        preferences!!.putString(Q.NOTIFICATION_TOKEN,refreshedToken)
        preferences!!.commit()
        //this token send to server user information
    }

}
