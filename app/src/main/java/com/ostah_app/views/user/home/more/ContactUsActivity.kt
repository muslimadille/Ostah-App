package com.ostah_app.views.user.home.more

import BaseActivity
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ostah_app.R
import com.ostah_app.data.remote.apiServices.ApiClient
import com.ostah_app.data.remote.apiServices.SessionManager
import com.ostah_app.data.remote.objects.AboutUsModel
import com.ostah_app.data.remote.objects.BaseResponseModel
import kotlinx.android.synthetic.main.activity_contact_us.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContactUsActivity : BaseActivity() {
    val whatsAppUri="com.whatsapp"
    var phone1=""
    var icon1=""
    var name1=""
    var phone2=""
    var icon2=""
    var name2=""
    var whatsNum=""
    var callPermission=false;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)
        checkPermission()
        getPrefrances()
        onWhatsAppClicked()
        onPhoneClicked()


    }
    fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
            != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CALL_PHONE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CALL_PHONE),
                    42)
            }
        } else {
            callPermission=true
            // Permission has already been granted
            //callPhone()
        }
    }
    private fun getPrefrances(){
        whatsNum=preferences!!.getString("app_whats","").replace("964","")
        phone1=preferences!!.getString("app_phone1_number","").replace("964","")
        phone2=preferences!!.getString("app_phone2_number","").replace("964","")
        icon1=preferences!!.getString("app_phone1_icon","").replace("964","")
        icon2=preferences!!.getString("app_phone2_icon","").replace("964","")
        name1=preferences!!.getString("app_phone1_name","").replace("964","")
        name2=preferences!!.getString("app_phone2_name","").replace("964","")
        setData()

    }
    private  fun setData(){
        Glide.with(this).applyDefaultRequestOptions(
            RequestOptions()
                .placeholder(R.drawable.ic_phone)
                .error(R.drawable.ic_phone))
            .load(icon1)
            .centerCrop()
            .into(phone1_img)
        Glide.with(this).applyDefaultRequestOptions(
            RequestOptions()
                .placeholder(R.drawable.ic_phone)
                .error(R.drawable.ic_phone))
            .load(icon2)
            .centerCrop()
            .into(phone2_img)
        phone1_name.text=name1
        phone2_name.text=name2
    }
    private fun onWhatsAppClicked(){
        whatsapp_btn.setOnClickListener {
            openWhatsApp("964"+whatsNum)
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

        phone1_btn.setOnClickListener {
            if (callPermission){
                val intent=Intent(Intent.ACTION_CALL, Uri.parse("tel:"+"${phone1.replace("+","0").toString()}"))
                startActivity(intent)
            }else{
                checkPermission()
            }

        }
        phone2_btn.setOnClickListener {
            if (callPermission){
                val intent=Intent(Intent.ACTION_CALL, Uri.parse("tel:"+"${phone2.replace("+","0").toString()}"))
                startActivity(intent)
            }else{
                checkPermission()
            }
        }
    }
}