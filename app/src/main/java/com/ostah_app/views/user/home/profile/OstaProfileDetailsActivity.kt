package com.ostah_app.views.user.home.profile

import BaseActivity
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ostah_app.R
import com.ostah_app.data.remote.apiServices.ApiClient
import com.ostah_app.data.remote.apiServices.SessionManager
import com.ostah_app.data.remote.objects.BaseResponseModel
import com.ostah_app.data.remote.objects.NormalUserModel
import kotlinx.android.synthetic.main.fragment_profile.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OstaProfileDetailsActivity : BaseActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var apiClient: ApiClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_osta_profile_details)
        getUserData()
        activeEdit()
        onSelectEdite()
    }
    private fun setUserData(userModel: NormalUserModel){
        username?.setText(userModel.name)
        email?.setText(userModel.email)
        phone?.setText(userModel.phonenumber)
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
        apiClient.getApiService(this).getUserProfile()
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
            pass_word_input_layout.visibility= View.VISIBLE
            username.isEnabled=true
            email.isEnabled=true
            phone.isEnabled=true
        }else{
            save_btn_lay.visibility= View.GONE
            pass_word_input_layout.visibility= View.GONE
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
}