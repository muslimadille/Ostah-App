package com.ostah_app.views.user.home.profile

import BaseActivity
import SpinnerAdapterCustomFont
import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.ostah_app.R
import com.ostah_app.data.remote.apiServices.ApiClient
import com.ostah_app.data.remote.apiServices.SessionManager
import com.ostah_app.data.remote.objects.*
import com.ostah_app.views.user.base.GlideObject
import com.ostah_app.views.user.home.home.orders.ostahs_list.new_order.MapsActivity
import com.ostah_app.views.user.login.SmsVerificationActivity
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_osta_profile_details.*
import kotlinx.android.synthetic.main.activity_osta_profile_details.address_name_txt
import kotlinx.android.synthetic.main.activity_osta_profile_details.conf_password
import kotlinx.android.synthetic.main.activity_osta_profile_details.edit_profile_cb
import kotlinx.android.synthetic.main.activity_osta_profile_details.email
import kotlinx.android.synthetic.main.activity_osta_profile_details.map_lay
import kotlinx.android.synthetic.main.activity_osta_profile_details.new_password
import kotlinx.android.synthetic.main.activity_osta_profile_details.old_password
import kotlinx.android.synthetic.main.activity_osta_profile_details.passwords_lay
import kotlinx.android.synthetic.main.activity_osta_profile_details.phone
import kotlinx.android.synthetic.main.activity_osta_profile_details.profile_lay
import kotlinx.android.synthetic.main.activity_osta_profile_details.progrss_lay
import kotlinx.android.synthetic.main.activity_osta_profile_details.save_btn_lay
import kotlinx.android.synthetic.main.activity_osta_profile_details.service_spinner
import kotlinx.android.synthetic.main.activity_osta_profile_details.user_img
import kotlinx.android.synthetic.main.activity_osta_profile_details.username
import kotlinx.android.synthetic.main.activity_registeration.*
import kotlinx.android.synthetic.main.activity_verification.*
import kotlinx.android.synthetic.main.fragment_profile.*

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*

class OstaProfileDetailsActivity : BaseActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var apiClient: ApiClient
    private lateinit var servicesSpinnerAdapter: SpinnerAdapterCustomFont
    private var servicesNamesList = ArrayList<String>()
    private var servicesList = ArrayList<Services>()
    private var selectedServiceId=0
    var selectedImage: File? = null
    var userImg=""
    var lat=""
    var lng=""
    var currentPhone=""
    private val SECOND_ACTIVITY_REQUEST_CODE = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_osta_profile_details)
        getUserData()
        activeEdit()
        onSelectEdite()
        initSpinner()
        servicesObserver()
        onSelectIMageClicked()
        implementListeners()
        onSaveClicked()
        onSelectLocatinoClicked()
    }
    private fun servicesObserver() {
        onObserveStart()
        apiClient = ApiClient()
        sessionManager = SessionManager(this)
        apiClient.getApiService(this).getAllServices()
            .enqueue(object : Callback<BaseResponseModel<ServicesModel>> {
                override fun onFailure(call: Call<BaseResponseModel<ServicesModel>>, t: Throwable) {
                    alertNetwork(false)
                }

                override fun onResponse(
                    call: Call<BaseResponseModel<ServicesModel>>,
                    response: Response<BaseResponseModel<ServicesModel>>
                ) {
                    if (response!!.isSuccessful) {
                        if (response.body()!!.success) {
                            response.body()!!.data!!.let {
                                if (it.services.isNotEmpty()) {
                                    servicesList.addAll(it.services)
                                    servicesList.forEach { ser->
                                        if(!servicesNamesList.contains(ser.name))servicesNamesList.add(ser.name)
                                    }
                                    servicesSpinnerAdapter!!.notifyDataSetChanged()
                                    onObserveSuccess()
                                } else {
                                    onObservefaled()
                                    Toast.makeText(this@OstaProfileDetailsActivity, "empty", Toast.LENGTH_SHORT).show()
                                }

                            }
                        } else {
                            onObservefaled()
                            Toast.makeText(this@OstaProfileDetailsActivity, "faid", Toast.LENGTH_SHORT).show()

                        }

                    } else {
                        Toast.makeText(this@OstaProfileDetailsActivity, "connect faid", Toast.LENGTH_SHORT).show()

                    }

                }


            })
    }

    private fun setUserData(userModel: OstahUserModel){
        currentPhone=userModel.phonenumber.replace("+964","")
        username?.setText(userModel.name)
        email?.setText(userModel.email)
        phone?.setText(currentPhone)
        service_text?.text=userModel.service.name
        selectedServiceId=userModel.service_id
        lat=userModel.lat.toString()
        lng=userModel.lng.toString()
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
    private fun getUserData() {
        onObserveStart()
        apiClient = ApiClient()
        sessionManager = SessionManager(this)
        apiClient.getApiService(this).getOstahProfile()
            .enqueue(object : Callback<BaseResponseModel<OstahUserModel>> {
                override fun onFailure(call: Call<BaseResponseModel<OstahUserModel>>, t: Throwable) {
                    alertNetwork(false)
                }

                override fun onResponse(
                    call: Call<BaseResponseModel<OstahUserModel>>,
                    response: Response<BaseResponseModel<OstahUserModel>>
                ) {
                    if (response!!.isSuccessful) {
                        if (response.body()!!.success) {
                            response.body()!!.data!!.let {
                                if (it!=null) {
                                    setUserData(it)
                                    onObserveSuccess()
                                } else {
                                    onObservefaled()
                                    Toast.makeText(this@OstaProfileDetailsActivity, "empty", Toast.LENGTH_SHORT).show()
                                }

                            }
                        } else {
                            onObservefaled()
                            Toast.makeText(this@OstaProfileDetailsActivity, "faid", Toast.LENGTH_SHORT).show()

                        }

                    } else {
                        Toast.makeText(this@OstaProfileDetailsActivity, "connect faid", Toast.LENGTH_SHORT).show()

                    }

                }


            })
    }
    private fun onSelectEdite(){
        edit_profile_cb.setOnClickListener {
            activeEdit()
        }
    }
    private fun activeEdit(){
        if(edit_profile_cb.isChecked){
            save_btn_lay.visibility= View.VISIBLE
            passwords_lay.visibility= View.VISIBLE
            sercices_spinner_lay.visibility=View.VISIBLE
            map_lay.visibility=View.VISIBLE
            service_name_lay.visibility=View.GONE

            username.isEnabled=true
            email.isEnabled=true
            phone.isEnabled=true
        }else{
            sercices_spinner_lay.visibility=View.GONE
            map_lay.visibility=View.GONE

            service_name_lay.visibility=View.VISIBLE
            save_btn_lay.visibility= View.GONE
            passwords_lay.visibility= View.GONE
            username.isEnabled=false
            email.isEnabled=false
            phone.isEnabled=false


        }
    }

    private fun initSpinner(){
        servicesSpinnerAdapter = SpinnerAdapterCustomFont(this, android.R.layout.simple_spinner_item, servicesNamesList)
        servicesSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        servicesSpinnerAdapter.textSize = 12
        service_spinner.adapter = servicesSpinnerAdapter
    }
    private fun implementListeners() {

        service_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if(servicesList.isNotEmpty()){
                    selectedServiceId=servicesList[position].id
                }

            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
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

    private fun onSelectIMageClicked(){
        user_img.setOnClickListener {
            if(edit_profile_cb.isChecked){
                selectImage()
            }
        }
    }
    fun selectImage() {
        TedPermission.with(this)
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setCropShape(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) CropImageView.CropShape.RECTANGLE else CropImageView.CropShape.OVAL)
                        .setAllowFlipping(false)
                        .setAllowRotation(false)
                        .setCropMenuCropButtonIcon(R.drawable.ic_add)
                        .setAspectRatio(1, 1)
                        .start(this@OstaProfileDetailsActivity)
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    Toast.makeText(this@OstaProfileDetailsActivity,
                        "getString(R.string.permissionDenied)", Toast.LENGTH_SHORT)
                        .show()
                }
            })
            .setPermissions(Manifest.permission.CAMERA)
            .check()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun toBase64(filePath: String): String{
        val bytes = File(filePath).readBytes()
        val base64 = Base64.getEncoder().encodeToString(bytes)
        return base64
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateOstah(){
        onObserveStart()
        var img=if(selectedImage!=null){"data:image/${selectedImage!!.extension};base64,"+toBase64(selectedImage.toString())}else{""}
        apiClient = ApiClient()
        sessionManager = SessionManager(this!!)
        apiClient.getApiService(this!!).updateOstah(email.text.toString(),username.text.toString(),img,0,selectedServiceId,lat,phone.text.toString(),lng)
            .enqueue(object : Callback<BaseResponseModel<LoginResponseModel>> {
                override fun onFailure(call: Call<BaseResponseModel<LoginResponseModel>>, t: Throwable) {
                    alertNetwork(false)
                }

                override fun onResponse(
                    call: Call<BaseResponseModel<LoginResponseModel>>,
                    response: Response<BaseResponseModel<LoginResponseModel>>
                ) {
                    if (response!!.isSuccessful) {
                        if (response.body()!!.success) {
                            response.body()!!.data!!.let {
                                if (it!=null) {
                                    onObserveSuccess()
                                    Toast.makeText(this@OstaProfileDetailsActivity, "success", Toast.LENGTH_SHORT).show()
                                    if(!currentPhone.equals(phone.text.toString())){
                                        updatePhone()
                                    }
                                } else {
                                    onObservefaled()
                                    Toast.makeText(this@OstaProfileDetailsActivity, "faid", Toast.LENGTH_SHORT).show()

                                }

                            }
                        } else {
                            onObservefaled()
                            Toast.makeText(this@OstaProfileDetailsActivity, "faid", Toast.LENGTH_SHORT).show()

                        }

                    } else {
                        Toast.makeText(this@OstaProfileDetailsActivity, "connect faid", Toast.LENGTH_SHORT).show()

                    }

                }


            })
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun onSaveClicked(){
        save_btn_lay.setOnClickListener {
            validatePassword()
        }
    }
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // Get String data from Intent
                address_name_txt.text = data!!.getStringExtra("address")
                lat=data!!.getStringExtra("lat")!!
                lng=data!!.getStringExtra("lng")!!

            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            var result: CropImage.ActivityResult? = null
            data?.let { result = CropImage.getActivityResult(data) }
            if (resultCode == RESULT_OK) {
                result?.let {
                    selectedImage = File(result!!.uri!!.path!!)

                    GlideObject.GlideProfilePic(this, selectedImage!!.path, user_img)
//                    Picasso.get().load(selectedImage!!).fit().centerCrop().into(ivUserImage )
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                result!!.error!!.printStackTrace()
            }
        }
    }
    private fun updateOstahPassword(){
        onObserveStart()
        apiClient = ApiClient()
        sessionManager = SessionManager(this)
        apiClient.getApiService(this).changeOstahPassword(old_password.text.toString(),new_password.text.toString())
            .enqueue(object : Callback<BaseResponseModel<Any>> {
                override fun onFailure(call: Call<BaseResponseModel<Any>>, t: Throwable) {
                    alertNetwork(false)
                }

                override fun onResponse(
                    call: Call<BaseResponseModel<Any>>,
                    response: Response<BaseResponseModel<Any>>
                ) {
                    if (response!!.isSuccessful) {
                        if (response.body()!!.success) {
                            response.body()!!.message!!.let {
                                if (it!=null&&it.toString().contains("Successfully")) {
                                    onObserveSuccess()
                                    Toast.makeText(this@OstaProfileDetailsActivity, "password changed", Toast.LENGTH_SHORT).show()
                                } else {
                                    onObservefaled()
                                    Toast.makeText(this@OstaProfileDetailsActivity, it.toString(), Toast.LENGTH_SHORT).show()

                                }

                            }
                        } else {
                            onObservefaled()
                            Toast.makeText(this@OstaProfileDetailsActivity, "faid", Toast.LENGTH_SHORT).show()

                        }

                    } else {
                        Toast.makeText(this@OstaProfileDetailsActivity, "connect faid", Toast.LENGTH_SHORT).show()

                    }

                }


            })
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun validatePassword(){
        if(old_password.text.isNotEmpty()){
            if(new_password.text.isNotEmpty()&&conf_password.text.isNotEmpty()&&(new_password.text.toString().equals(conf_password.text.toString()))){
                updateOstah()
                updateOstahPassword()
            }else{
                Toast.makeText(this@OstaProfileDetailsActivity, "كلمة المرور الجديدة غير متطابقة مع التأكيد", Toast.LENGTH_SHORT).show()
            }
        }else{
            updateOstah()
        }

    }
    private fun updatePhone(){
        send_message_btn.setOnClickListener {
            onObserveStart()
            apiClient = ApiClient()
            sessionManager = SessionManager(this)
            apiClient.getApiService(this)
                .updateOstahPhone("+964"+phone.text.toString())
                .enqueue(object : Callback<BaseResponseModel<Any>> {
                    override fun onFailure(call: Call<BaseResponseModel<Any>>, t: Throwable) {
                        alertNetwork(false)
                        onObservefaled()
                    }

                    override fun onResponse(
                        call: Call<BaseResponseModel<Any>>,
                        response: Response<BaseResponseModel<Any>>
                    ) {
                        val loginResponse = response.body()
                        if (loginResponse != null && loginResponse!!.success) {
                            onObserveSuccess()
                            val intent=Intent(this@OstaProfileDetailsActivity, SmsVerificationActivity::class.java)
                            intent.putExtra("phone", phone.text.toString())
                            intent.putExtra("type","")
                            intent.putExtra("email", "")
                            intent.putExtra("password", "")
                            intent.putExtra("key",1)
                            startActivity(intent)

                        } else {
                            onObservefaled()
                            Toast.makeText(
                                this@OstaProfileDetailsActivity,
                                "خطا حاول مرة أخري",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }


                })
        }
    }
    private fun onSelectLocatinoClicked(){
        // Start the SecondActivity
        map_lay.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("service_id",0)
            intent.putExtra("service_name","")
            intent.putExtra("service_img","")
            intent.putExtra("key",1)

            startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE)
        }

    }
    // This method is called when the second activity finishes


}