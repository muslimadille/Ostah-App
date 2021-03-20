package com.ostah_app.views.user.login

import BaseActivity
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.ostah_app.R
import com.ostah_app.data.remote.apiServices.ApiClient
import com.ostah_app.data.remote.apiServices.SessionManager
import com.ostah_app.data.remote.objects.BaseResponseModel
import kotlinx.android.synthetic.main.activity_sms_verification.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SmsVerificationActivity : BaseActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var apiClient: ApiClient
    private var phone =""
    private var type =""
    private var email =""
    private var password =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sms_verification)
        email=intent.getStringExtra("email")!!
        password=intent.getStringExtra("password")!!
        type=intent.getStringExtra("type")!!
        onResendClicked()
    }
    private fun onObserveStart() {
        ver_progrss_lay.visibility = View.VISIBLE
    }

    private fun onObserveSuccess() {
        ver_progrss_lay.visibility = View.GONE
    }

    private fun onObservefaled() {
        ver_progrss_lay.visibility = View.GONE
    }

    private fun onResendClicked(){
        send_message_btn.setOnClickListener {
            phone="+964"+message_tf.text.toString()
            onObserveStart()
            apiClient = ApiClient()
            sessionManager = SessionManager(this)
            apiClient.getApiService(this)
                .userVerification(phone, type)
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
                        if (loginResponse!=null&&loginResponse!!.success) {
                            onObserveSuccess()
                            val intent =
                                Intent(this@SmsVerificationActivity, VerificationActivity::class.java)
                            intent.putExtra("phone","+964"+message_tf.text.toString())
                            intent.putExtra("type","client")
                            intent.putExtra("email",email)
                            intent.putExtra("password",password)
                            startActivity(intent)

                        } else {
                            onObservefaled()
                            Toast.makeText(
                                this@SmsVerificationActivity,
                                "خطا حاول مرة أخري",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }


                })
        }
    }
    fun alertNetwork(isExit: Boolean = true) {
        val alertBuilder = AlertDialog.Builder(this)
        //alertBuilder.setTitle(R.string.error)
        alertBuilder.setMessage(R.string.no_internet)
        if (isExit) {
            // alertBuilder.setPositiveButton(R.string.exit) { dialog: DialogInterface, _: Int -> context!!.finish() }
        } else {
            alertBuilder.setNegativeButton(R.string.dismiss) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        }
        alertBuilder.show()
    }
}