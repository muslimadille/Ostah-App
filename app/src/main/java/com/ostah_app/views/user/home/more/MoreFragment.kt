package com.ostah_app.views.user.home.more

import android.Manifest
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.ostah_app.R
import com.ostah_app.data.remote.apiServices.ApiClient
import com.ostah_app.data.remote.apiServices.SessionManager
import com.ostah_app.data.remote.objects.AboutUsModel
import com.ostah_app.data.remote.objects.BaseResponseModel
import com.ostah_app.utiles.Q
import com.ostah_app.views.user.home.MainActivity
import com.ostah_app.views.user.splash.SplashActivity
import kotlinx.android.synthetic.main.activity_info.*
import kotlinx.android.synthetic.main.fragment_more.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MoreFragment : Fragment() {

    private lateinit var sessionManager: SessionManager
    private lateinit var apiClient: ApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for mContext fragment
        return inflater.inflate(R.layout.fragment_more, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(mContext!!)
        onShareClicked()
        onLogoutClicked()
        onLAboutClicked()
        onprivacyClicked()
        onCallclicked()
    }
    private fun onShareClicked(){
        share_btn.setOnClickListener {
            shareText(mContext!!,"www.google.com","osta app")
        }
    }
    fun shareText(activity: Activity, text: String, title: String) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_TEXT, text)
        activity.startActivity(Intent.createChooser(sharingIntent, title))
    }
    var mContext: MainActivity? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context as MainActivity
    }
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mContext = activity as MainActivity
    }

    private fun onLogoutClicked(){
        logout_btn.setOnClickListener {
            FirebaseMessaging.getInstance().isAutoInitEnabled=false
            deleteFirebaseId()
            //FirebaseInstanceId.getInstance().deleteInstanceId()
            sessionManager.saveAuthToken("")
            mContext!!.preferences!!.putBoolean(Q.IS_FIRST_TIME,true)
            mContext!!.preferences!!.putBoolean(Q.IS_LOGIN,false)
            mContext!!.preferences!!.putInteger(Q.USER_ID,-1)
            mContext!!.preferences!!.putString(Q.USER_NAME,"")
            mContext!!.preferences!!.putString(Q.USER_EMAIL,"")
            mContext!!.preferences!!.putString(Q.USER_PHONE,"")
            mContext!!.preferences!!.putInteger(Q.USER_GENDER,-1)
            mContext!!.preferences!!.commit()
            val intent = Intent(mContext, SplashActivity::class.java)
            startActivity(intent)
            mContext!!.finish()
        }
    }
    private fun onprivacyClicked(){
        privacy_btn.setOnClickListener {
            val intent = Intent(mContext, InfoActivity::class.java)
            intent.putExtra("title","سياسة الخصوصية")
            intent.putExtra("content","جاري التحميل....")
            intent.putExtra("id",2)
            startActivity(intent)
        }
    }
    private fun onLAboutClicked(){
        about_app_btn.setOnClickListener {
            val intent = Intent(mContext, InfoActivity::class.java)
            intent.putExtra("title","عن التطبيق")
            intent.putExtra("content","جاري التحميل....")
            intent.putExtra("id",1)
            startActivity(intent)
        }
    }
    private fun contactUs() {
        apiClient = ApiClient()
        sessionManager = SessionManager(mContext!!)
        apiClient.getApiService(mContext!!).getPhoneInfo()
            .enqueue(object : Callback<BaseResponseModel<AboutUsModel>> {
                override fun onFailure(call: Call<BaseResponseModel<AboutUsModel>>, t: Throwable) {
                    // alertNetwork(true)
                    Toast.makeText(mContext, "network error", Toast.LENGTH_SHORT).show()
                    val intent=Intent(Intent.ACTION_CALL, Uri.parse("tel:"+"${mContext!!.preferences!!.getString("app_phone","")
                        .replace("+","").toString()}"))
                    mContext!!.startActivity(intent)
                }

                override fun onResponse(
                    call: Call<BaseResponseModel<AboutUsModel>>,
                    response: Response<BaseResponseModel<AboutUsModel>>
                ) {
                    if (response!!.isSuccessful) {
                        if (response.body()!!.success) {
                            response.body()!!.data!!.let {
                                if(it.contact_us.isNotEmpty()){
                                    mContext!!.preferences!!.putString("app_phone",it.contact_us)
                                    mContext!!.preferences!!.commit()
                                    val intent=Intent(Intent.ACTION_CALL, Uri.parse("tel:"+"${it.contact_us.replace("+","").toString()}"))
                                    mContext!!.startActivity(intent)
                                }
                            }
                        } else {
                            Toast.makeText(mContext, "faid", Toast.LENGTH_SHORT).show()
                        }

                    } else {
                        Toast.makeText(mContext, "connect faid", Toast.LENGTH_SHORT).show()

                    }
                }

            })
    }
    private fun onCallclicked(){
        call_us.setOnClickListener {
            //contactUs()
            checkPermission()
        }
    }
    fun checkPermission() {
        if (ContextCompat.checkSelfPermission(mContext!!,
                Manifest.permission.CALL_PHONE)
            != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(mContext!!,
                    Manifest.permission.CALL_PHONE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(mContext!!,
                    arrayOf(Manifest.permission.CALL_PHONE),
                    42)
            }
        } else {
            // Permission has already been granted
            callPhone()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 42) {
            // If request is cancelled, the result arrays are empty.
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // permission was granted, yay!
                callPhone()
            } else {
                // permission denied, boo! Disable the
                // functionality
            }
            return
        }
    }

    fun callPhone(){
        contactUs()
    }
    private fun deleteFirebaseId() {
        try {
            (mContext!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancelAll()
            Thread(Runnable {
                FirebaseInstanceId.getInstance().deleteInstanceId()
            }).start()
        } catch (e: Exception) {
            //AdLog.logCrashlytics(TAG, "deleteFirebaseId", e)
        }
    }


}