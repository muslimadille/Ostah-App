package com.ostah_app.base

import android.app.Application
import com.ostah_app.utiles.ComplexPreferences
import com.ostah_app.utiles.Q
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

        BaseActivity.dLocale = Locale(change) //set any locale you want here
    }
}