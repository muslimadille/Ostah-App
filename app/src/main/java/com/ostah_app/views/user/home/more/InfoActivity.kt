package com.ostah_app.views.user.home.more

import BaseActivity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.ostah_app.R
import com.ostah_app.data.remote.apiServices.ApiClient
import com.ostah_app.data.remote.apiServices.SessionManager
import com.ostah_app.data.remote.objects.BaseResponseModel
import com.ostah_app.data.remote.objects.AboutUsModel
import com.ostah_app.data.remote.objects.Slides
import com.ostah_app.utiles.Q
import kotlinx.android.synthetic.main.activity_info.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InfoActivity : BaseActivity() {
    private var title=""
    private var content=""
    private var id=0
    private lateinit var sessionManager: SessionManager
    private lateinit var apiClient: ApiClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
        title=intent.getStringExtra("title")!!
        content=intent.getStringExtra("content")!!
        id=intent.getIntExtra("id",0)
        info_title.text=title
        info_content.text=content
        if(id==1){aboutUs()}
        if(id==2){privacy()}

    }
    private fun aboutUs() {
        apiClient = ApiClient()
        sessionManager = SessionManager(this)
        apiClient.getApiService(this).getAppInfo()
            .enqueue(object : Callback<BaseResponseModel<AboutUsModel>> {
                override fun onFailure(call: Call<BaseResponseModel<AboutUsModel>>, t: Throwable) {
                   // alertNetwork(true)
                    info_content.text=preferences!!.getString("about_us","")
                }

                override fun onResponse(
                    call: Call<BaseResponseModel<AboutUsModel>>,
                    response: Response<BaseResponseModel<AboutUsModel>>
                ) {
                    if (response!!.isSuccessful) {
                        if (response.body()!!.success) {
                            response.body()!!.data!!.let {
                                info_content.text=it.about_us
                                if(it.about_us.isNotEmpty()){
                                    preferences!!.putString("about_us",it.about_us)
                                preferences!!.commit()
                                }
                            }
                        } else {
                            Toast.makeText(this@InfoActivity, "faid", Toast.LENGTH_SHORT).show()

                        }

                    } else {
                        Toast.makeText(this@InfoActivity, "connect faid", Toast.LENGTH_SHORT).show()

                    }

                }


            })
    }
    private fun privacy() {
        apiClient = ApiClient()
        sessionManager = SessionManager(this)
        apiClient.getApiService(this).getPrivacyInfo()
            .enqueue(object : Callback<BaseResponseModel<AboutUsModel>> {
                override fun onFailure(call: Call<BaseResponseModel<AboutUsModel>>, t: Throwable) {
                    // alertNetwork(true)
                    info_content.text=preferences!!.getString("privacy","")
                }

                override fun onResponse(
                    call: Call<BaseResponseModel<AboutUsModel>>,
                    response: Response<BaseResponseModel<AboutUsModel>>
                ) {
                    if (response!!.isSuccessful) {
                        if (response.body()!!.success) {
                            response.body()!!.data!!.let {
                                info_content.text=it.privacy_policy
                                if(it.about_us.isNotEmpty()){
                                    preferences!!.putString("privacy",it.privacy_policy)
                                    preferences!!.commit()
                                }
                            }
                        } else {
                            Toast.makeText(this@InfoActivity, "faid", Toast.LENGTH_SHORT).show()
                        }

                    } else {
                        Toast.makeText(this@InfoActivity, "connect faid", Toast.LENGTH_SHORT).show()

                    }

                }


            })
    }
}