package com.ostah_app.views.user.home.profile
import BaseActivity
import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.ostah_app.R
import com.ostah_app.data.remote.apiServices.ApiClient
import com.ostah_app.data.remote.apiServices.SessionManager
import com.ostah_app.data.remote.objects.BaseResponseModel
import com.ostah_app.data.remote.objects.NormalUserModel
import com.ostah_app.utiles.Q
import com.ostah_app.views.user.home.MainActivity
import kotlinx.android.synthetic.main.activity_contact_us.*
import kotlinx.android.synthetic.main.activity_create_order.*
import kotlinx.android.synthetic.main.activity_sender_details.*
import kotlinx.android.synthetic.main.activity_sender_details.bottomNavigationView
import kotlinx.android.synthetic.main.activity_sender_details.osata_name
import kotlinx.android.synthetic.main.activity_sender_details.profile_lay
import kotlinx.android.synthetic.main.activity_sender_details.progrss_lay
import kotlinx.android.synthetic.main.activity_sender_details.user_img
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SenderDetailsActivity : BaseActivity() {
    var id=0
    var name=""
    var lat=""
    var lng=""
    var callPermission=false;
    var phoneNum=""

    private lateinit var sessionManager: SessionManager
    private lateinit var apiClient: ApiClient
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sender_details)
        id=intent.getIntExtra("tecket_id",0)
        lat=intent.getStringExtra("lat")!!
        lng=intent.getStringExtra("lng")!!
        checkPermission()
        getUserData()
        onBackClicked()
        navToMap()
        initBottomNavigation()
        onPhoneClicked()
    }
    private fun onBackClicked(){
        back_btn.setOnClickListener {
            finish()
        }
    }
    private fun getUserData() {
        onObserveStart()
        val url = Q.SENDER_DATA +"/${id}"
        apiClient = ApiClient()
        sessionManager = SessionManager(this)
        apiClient.getApiService(this).getSenderData(url)
            .enqueue(object : Callback<BaseResponseModel<NormalUserModel>> {
                override fun onFailure(call: Call<BaseResponseModel<NormalUserModel>>, t: Throwable) {
                    alertNetwork(false)
                }

                override fun onResponse(
                    call: Call<BaseResponseModel<NormalUserModel>>,
                    response: Response<BaseResponseModel<NormalUserModel>>
                ) {
                    if (response!!.isSuccessful) {
                        if (response.body()!!.success) {
                            response.body()!!.data!!.let {
                                if (it!=null) {
                                    setUserData(it)
                                    onObserveSuccess()
                                } else {
                                    onObservefaled()
                                    Toast.makeText(this@SenderDetailsActivity, "empty", Toast.LENGTH_SHORT).show()
                                }

                            }
                        } else {
                            onObservefaled()
                            Toast.makeText(this@SenderDetailsActivity, "faid", Toast.LENGTH_SHORT).show()

                        }

                    } else {
                        Toast.makeText(this@SenderDetailsActivity, "connect faid", Toast.LENGTH_SHORT).show()

                    }

                }


            })
    }
    private fun setUserData(userModel: NormalUserModel){
        osata_name?.setText(userModel.name)
        phone_txt?.text=userModel.phonenumber.replace("+964","0").toString()
        phoneNum=userModel.phonenumber.replace("+964","0").toString()
        user_img?.let {
            Glide.with(this).applyDefaultRequestOptions(
                RequestOptions()
                    .placeholder(R.drawable.person_ic)
                    .error(R.drawable.person_ic))
                .load(userModel.image)
                .centerCrop()
                .into(it)
        }



    }
    private fun onObserveStart(){
        progrss_lay?.let {
            it.visibility= View.VISIBLE
        }
        profile_lay.visibility= View.GONE
    }
    private fun onObserveSuccess(){
        progrss_lay?.let {
            it.visibility= View.GONE
        }
        profile_lay?.let {
            it.visibility= View.VISIBLE
        }
    }
    private fun onObservefaled(){
        progrss_lay?.let {
            it.visibility= View.GONE
        }
        profile_lay?.let {
            it.visibility= View.VISIBLE
        }
    }
    fun alertNetwork(isExit: Boolean = true) {
        val alertBuilder = AlertDialog.Builder(this)
        //alertBuilder.setTitle(R.string.error)
        alertBuilder.setMessage(R.string.no_internet)
        if (isExit) {
            // alertBuilder.setPositiveButton(R.string.exit) { dialog: DialogInterface, _: Int -> context!!.finish() }
        } else {
            alertBuilder.setPositiveButton(R.string.try_again) { dialog: DialogInterface, _: Int ->
                getUserData()
                dialog.dismiss()}
            alertBuilder.setNegativeButton(R.string.dismiss) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        }
        alertBuilder.show()
    }
    private fun navToMap(){

        val zoom=10
        var lable="hojfhv"
        val intent= Intent(Intent.ACTION_VIEW)
        map_btn.setOnClickListener {
            intent.data= Uri.parse("geo:0,0?q=$lat,$lng")
            if(intent.resolveActivity(packageManager)!=null){
                startActivity(intent)
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun initBottomNavigation(){

        val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    intent= Intent(this, MainActivity::class.java)
                    intent.putExtra("navK",0)
                    startActivity(intent)
                }
                R.id.navigation_orders -> {
                    intent= Intent(this, MainActivity::class.java)
                    intent.putExtra("navK",1)
                    startActivity(intent)
                }
                R.id.navigation_previous -> {
                    intent= Intent(this, MainActivity::class.java)
                    intent.putExtra("navK",2)
                    startActivity(intent)
                }
                R.id.navigation_profile->{
                    intent= Intent(this, MainActivity::class.java)
                    intent.putExtra("navK",3)
                    startActivity(intent)
                }
                R.id.navigation_extras->{
                    intent= Intent(this, MainActivity::class.java)
                    intent.putExtra("navK",4)
                    startActivity(intent)
                }
            }
            false
        }
        bottomNavigationView.labelVisibilityMode= LabelVisibilityMode.LABEL_VISIBILITY_LABELED
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

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
    private fun onPhoneClicked(){

        call_btn.setOnClickListener {
            if (callPermission){
                val intent=Intent(Intent.ACTION_CALL, Uri.parse("tel:"+"${phoneNum}"))
                startActivity(intent)
            }else{
                checkPermission()
            }

        }

    }

}