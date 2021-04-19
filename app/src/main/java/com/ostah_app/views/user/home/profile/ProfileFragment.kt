package com.ostah_app.views.user.home.profile

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.ostah_app.data.remote.objects.BaseResponseModel
import com.ostah_app.data.remote.objects.LoginResponseModel
import com.ostah_app.data.remote.objects.NormalUserModel
import com.ostah_app.data.remote.objects.OrderTecket
import com.ostah_app.utiles.Q
import com.ostah_app.views.user.base.GlideObject
import com.ostah_app.views.user.home.MainActivity
import com.ostah_app.views.user.login.SmsVerificationActivity
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_verification.*
import kotlinx.android.synthetic.main.fragment_orders.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.profile_lay
import kotlinx.android.synthetic.main.fragment_profile.progrss_lay
import kotlinx.android.synthetic.main.fragment_profile.visitor_lay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*


class ProfileFragment : Fragment() {
    private lateinit var sessionManager: SessionManager
    private lateinit var apiClient: ApiClient
    var userImg=""
    var userType=""
    var useremail=""
    var userpassword=""
    var currentPhone=""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(mContext!!.preferences!!.getString(Q.USER_NAME,"").isNotEmpty()){
            getUserData()
            activeEdit()
            onSelectEdite()
            onSelectIMageClicked()
            onSaveClicked()
        }else{
            profile_lay.visibility=View.GONE
            visitor_lay.visibility=View.VISIBLE
        }


    }
    private fun setUserData(userModel: NormalUserModel){
        currentPhone=userModel.phonenumber
        username?.setText(userModel.name)
        email?.setText(userModel.email)
        phone?.setText(userModel.phonenumber.replace("+964",""))
        user_img?.let {
            Glide.with(mContext!!).applyDefaultRequestOptions(
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
        sessionManager = SessionManager(mContext!!)
        apiClient.getApiService(mContext!!).getUserProfile()
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
                                        userImg=it.image
                                        onObserveSuccess()
                                    } else {
                                        onObservefaled()
                                        Toast.makeText(mContext, "empty", Toast.LENGTH_SHORT).show()
                                    }

                                }
                            } else {
                                onObservefaled()
                                Toast.makeText(mContext, "faid", Toast.LENGTH_SHORT).show()

                            }

                        } else {
                            Toast.makeText(mContext, "connect faid", Toast.LENGTH_SHORT).show()

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
            save_btn_lay.visibility=View.VISIBLE
            passwords_lay.visibility=View.VISIBLE
            username.isEnabled=true
            email.isEnabled=true
            phone.isEnabled=true
        }else{
            save_btn_lay.visibility=View.GONE
            passwords_lay.visibility=View.GONE
            username.isEnabled=false
            email.isEnabled=false
            phone.isEnabled=false


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
        val alertBuilder = AlertDialog.Builder(mContext!!)
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
    var mContext: MainActivity? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context as MainActivity
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mContext = activity as MainActivity
    }
    private fun onSelectIMageClicked(){
        user_img.setOnClickListener {
            if(edit_profile_cb.isChecked){
                selectImage()
            }
        }
    }
    fun selectImage() {
        TedPermission.with(mContext)
            .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setCropShape(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) CropImageView.CropShape.RECTANGLE else CropImageView.CropShape.OVAL)
                        .setAllowFlipping(false)
                        .setAllowRotation(false)
                        .setCropMenuCropButtonIcon(R.drawable.ic_add)
                        .setAspectRatio(1, 1)
                        .start(mContext!!)
                }

                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    Toast.makeText(mContext!!,
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
    private fun updateUser(){
        onObserveStart()
        var img=if(mContext!!.selectedImage!=null){"data:image/${mContext!!.selectedImage!!.extension};base64,"+toBase64(mContext!!.selectedImage.toString())}else{""}
        apiClient = ApiClient()
        sessionManager = SessionManager(mContext!!)
        apiClient.getApiService(mContext!!).updateUser(email.text.toString(),username.text.toString(),img,0,1,"32.0005","+964"+phone.text.toString(),"34.3333")
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
                                    Toast.makeText(mContext, "success", Toast.LENGTH_SHORT).show()
                                    if(!currentPhone.equals(phone.text.toString())){
                                        updatePhone()
                                    }
                                } else {
                                    onObservefaled()
                                    Toast.makeText(mContext, response.body()!!.message.toString(), Toast.LENGTH_SHORT).show()

                                }

                            }
                        } else {
                            onObservefaled()
                            Toast.makeText(mContext, "faid", Toast.LENGTH_SHORT).show()

                        }

                    } else {
                        onObservefaled()
                        Toast.makeText(mContext, "connect faid", Toast.LENGTH_SHORT).show()

                    }

                }


            })
    }
    private fun updateUserPassword(){
        onObserveStart()
        apiClient = ApiClient()
        sessionManager = SessionManager(mContext!!)
        apiClient.getApiService(mContext!!).changeUserPassword(old_password.text.toString(),new_password.text.toString())
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
                                    Toast.makeText(mContext, "password changed", Toast.LENGTH_SHORT).show()
                                } else {
                                    onObservefaled()
                                    Toast.makeText(mContext, it.toString(), Toast.LENGTH_SHORT).show()

                                }

                            }
                        } else {
                            onObservefaled()
                            Toast.makeText(mContext, response.body()!!.message.toString(), Toast.LENGTH_SHORT).show()

                        }

                    } else {
                        Toast.makeText(mContext, "connect faid", Toast.LENGTH_SHORT).show()

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
    @RequiresApi(Build.VERSION_CODES.O)
    private fun validatePassword(){
        if(old_password.text.isNotEmpty()){
            if(new_password.text.isNotEmpty()&&conf_password.text.isNotEmpty()&&(new_password.text.toString().equals(conf_password.text.toString()))){
                updateUser()
                updateUserPassword()
            }else{
                Toast.makeText(mContext, "كلمة المرور الجديدة غير متطابقة مع التأكيد", Toast.LENGTH_SHORT).show()
            }
        }else{
            updateUser()
        }

    }
    private fun updatePhone(){
        send_message_btn.setOnClickListener {
            onObserveStart()
            apiClient = ApiClient()
            sessionManager = SessionManager(mContext!!)
            apiClient.getApiService(mContext!!)
                .updateUserPhone(phone.text.toString())
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
                            val intent=Intent(mContext,SmsVerificationActivity::class.java)
                            intent.putExtra("phone", phone.text.toString())
                            intent.putExtra("type","")
                            intent.putExtra("email", "")
                            intent.putExtra("password", "")
                            intent.putExtra("key",1)
                            startActivity(intent)

                        } else {
                            onObservefaled()
                            Toast.makeText(
                                mContext!!,
                                "خطا حاول مرة أخري",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }


                })
        }
    }

}