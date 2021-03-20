package com.ostah_app.views.user.home.profile

import BaseActivity
import SpinnerAdapterCustomFont
import android.Manifest
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
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_osta_profile_details.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.edit_profile_cb
import kotlinx.android.synthetic.main.fragment_profile.email
import kotlinx.android.synthetic.main.fragment_profile.passwords_lay
import kotlinx.android.synthetic.main.fragment_profile.phone
import kotlinx.android.synthetic.main.fragment_profile.profile_lay
import kotlinx.android.synthetic.main.fragment_profile.progrss_lay
import kotlinx.android.synthetic.main.fragment_profile.save_btn_lay
import kotlinx.android.synthetic.main.fragment_profile.user_img
import kotlinx.android.synthetic.main.fragment_profile.username
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
        username?.setText(userModel.name)
        email?.setText(userModel.email)
        phone?.setText(userModel.phonenumber)
        service_text?.text=userModel.service.name
        selectedServiceId=userModel.service_id
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
            service_name_lay.visibility=View.GONE

            username.isEnabled=true
            email.isEnabled=true
            phone.isEnabled=true
        }else{
            sercices_spinner_lay.visibility=View.GONE
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
    private fun userRegister(){
        onObserveStart()
        var img=if(selectedImage!=null){"data:image/${selectedImage!!.extension};base64,"+toBase64(selectedImage.toString())}else{userImg}
        apiClient = ApiClient()
        sessionManager = SessionManager(this!!)
        apiClient.getApiService(this!!).updateUser(email.text.toString(),username.text.toString(),img,0,1,"32.0005",phone.text.toString(),"34.3333")
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
            userRegister()
        }
    }
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
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
}