package com.ostah_app.views.user.base

import android.app.Application
import android.os.Build
import com.ostah_app.R
import com.ostah_app.data.remote.gcm.NotificationUtils
import com.ostah_app.utiles.Q
import com.ostah_app.utiles.ComplexPreferences
import java.util.*

class App : Application() {
    var preferences: ComplexPreferences? = null

    override fun onCreate() {
        super.onCreate()

        var change = ""
        preferences = ComplexPreferences.getComplexPreferences(this, Q.PREF_FILE, Q.MODE_PRIVATE)
        val language = preferences!!.getString("language", "en")
        if (language =="Arabic") {
            change="ar"
        } else if (language=="English" ) {
            change = "en"
        }else {
            change =""
        }
        createNotificationsChannels()
        BaseActivity.dLocale = Locale(change) //set any locale you want here
    }
    private fun createNotificationsChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationUtils.createChannel(this,
                "general",
                "general",
                "general")
        }
    }
}