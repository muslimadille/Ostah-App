package com.ostah_app.views.user.home.more

import BaseActivity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.ostah_app.R
import kotlinx.android.synthetic.main.activity_contact_us.*

class ContactUsActivity : BaseActivity() {
    val whatsAppUri="com.whatsapp"
    var phone=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)
        phone=preferences!!.getString("app_phone","").replace("964","")
        onWhatsAppClicked()
        onPhoneClicked()


    }
    private fun onWhatsAppClicked(){
        whatsapp_btn.setOnClickListener {
            openWhatsApp("964"+phone)
        }
    }
    private fun whatsAppInstalledOrNot(uri: String): Boolean {
        val pm = this.packageManager
        return try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
    private fun openWhatsApp(num: String) {
        val isAppInstalled = whatsAppInstalledOrNot(whatsAppUri)
        if (isAppInstalled) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=$num"))
            startActivity(intent)
        } else {
            Toast.makeText(this, "تطبيق whatApp غير موجود علي جهازك", Toast.LENGTH_SHORT).show()
            // WhatsApp not installed show toast or dialog
        }
    }
    private fun onPhoneClicked(){
        phone_btn.setOnClickListener {

            val intent=Intent(Intent.ACTION_CALL, Uri.parse("tel:"+"${phone.replace("+","").toString()}"))
            startActivity(intent)
        }
    }
}