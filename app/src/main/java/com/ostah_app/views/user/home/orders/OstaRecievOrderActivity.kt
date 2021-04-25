package com.ostah_app.views.user.home.orders

import BaseActivity
import SpinnerAdapterCustomFont
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.ostah_app.R
import com.ostah_app.data.remote.apiServices.ApiClient
import com.ostah_app.data.remote.apiServices.SessionManager
import com.ostah_app.data.remote.objects.BaseResponseModel
import com.ostah_app.data.remote.objects.singleTicket
import com.ostah_app.utiles.Q
import com.ostah_app.views.user.base.GlideObject
import com.ostah_app.views.user.home.MainActivity
import com.ostah_app.views.user.home.profile.SenderDetailsActivity
import kotlinx.android.synthetic.main.activity_osta_reciev_order.*
import kotlinx.android.synthetic.main.activity_osta_reciev_order.description_txt
import kotlinx.android.synthetic.main.activity_osta_reciev_order.order_title_txt
import kotlinx.android.synthetic.main.activity_osta_reciev_order.profile_lay
import kotlinx.android.synthetic.main.activity_osta_reciev_order.progrss_lay
import kotlinx.android.synthetic.main.activity_osta_reciev_order.save_btn_lay
import kotlinx.android.synthetic.main.activity_osta_reciev_order.user_img
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OstaRecievOrderActivity : BaseActivity() {
    var ticketId=0
    var userName=""
    var userImg=""
    var comment=""
    var details=""
    var lat=""
    var lng=""
    var incomeSttatus=0
    var selectedStatus=0
    var statusList:ArrayList<String> = ArrayList()
    private lateinit var servicesSpinnerAdapter: SpinnerAdapterCustomFont
    private lateinit var sessionManager: SessionManager
    private lateinit var apiClient: ApiClient

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_osta_reciev_order)
        getIntentValues()
        setDAataInfo()
        initStatusList()
        initSpinner()
        implementListeners()
        onShowUserClicked()
        onRefreshClicked()
        initBottomNavigation()
    }
    private fun onShowUserClicked(){
        show_user_info_btn.setOnClickListener {
            val intent= Intent(this, SenderDetailsActivity::class.java)
            intent.putExtra("tecket_id",ticketId)
            intent.putExtra("lat",lat.toString())
            intent.putExtra("lng",lng.toString())

            startActivity(intent)
        }

    }
    private fun getIntentValues(){
        ticketId=intent.getIntExtra("id",0)
        lat=intent.getStringExtra("lat")!!
        lng=intent.getStringExtra("lng")!!

        incomeSttatus=intent.getIntExtra("status",0)
        selectedStatus=incomeSttatus
        if(incomeSttatus==3){
            coat_lay.visibility=View.VISIBLE
        }else{
            coat_lay.visibility=View.GONE
        }
        userName=intent.getStringExtra("name")!!
        userImg=intent.getStringExtra("image")!!
        comment=intent.getStringExtra("comment")!!
        details=intent.getStringExtra("details")!!
        
    }
    private fun initStatusList(){
        /*if(incomeSttatus==1){statusList.add("انتظر حتي يتم تأكيد الطلب ")
        }else */
            statusList.add("تم إستلام الطلب")
            statusList.add("الطلب قيد المعالجة")
            statusList.add("تم تنفيذ الطلب")
            statusList.add("إلغاء الطلب")



    }
    private fun initSpinner(){
        servicesSpinnerAdapter = SpinnerAdapterCustomFont(this, android.R.layout.simple_spinner_item, statusList)
        servicesSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        servicesSpinnerAdapter.textSize = 12
        order_state_spinner.adapter = servicesSpinnerAdapter
    }
    private fun implementListeners() {
        if(selectedStatus==1){
            order_state_spinner.setSelection(selectedStatus-1)
        }else{
            order_state_spinner.setSelection(selectedStatus-2)
        }
        order_state_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    selectedStatus=position+2

            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }


    }
    private fun setDAataInfo(){
        GlideObject.GlideProfilePic(this,userImg, user_img)
        ostah_name_txt.text=userName
        order_title_txt.setText(comment)
        description_txt.setText(details)

    }
    private fun updatOrder() {

        onObserveStart()
        val url = Q.UPDATE_ORDER +"/${ticketId}"
        apiClient = ApiClient()
        sessionManager = SessionManager(this)
        apiClient.getApiService(this).updateOrder(url,selectedStatus,coast_title_txt.text.toString())
            .enqueue(object : Callback<BaseResponseModel<singleTicket>> {
                override fun onFailure(call: Call<BaseResponseModel<singleTicket>>, t: Throwable) {
                    alertNetwork(false)
                }
                override fun onResponse(
                    call: Call<BaseResponseModel<singleTicket>>,
                    response: Response<BaseResponseModel<singleTicket>>
                ) {
                    if (response!!.isSuccessful) {
                        if (response.body()!!.success) {
                            response.body()!!.data!!.let {
                                onObserveSuccess()
                                Toast.makeText(this@OstaRecievOrderActivity, "تم تحديث الطلب بنجاح", Toast.LENGTH_SHORT).show()
                                val intent= Intent(this@OstaRecievOrderActivity, MainActivity::class.java)
                                intent.putExtra("navK",1.toInt())
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            onObservefaled()
                            Toast.makeText(this@OstaRecievOrderActivity, "faid", Toast.LENGTH_SHORT).show()

                        }

                    } else {
                        onObservefaled()
                        Toast.makeText(this@OstaRecievOrderActivity, "connect faid", Toast.LENGTH_SHORT).show()

                    }

                }


            })
    }
    private fun onRefreshClicked(){
        save_btn_lay.setOnClickListener {
            if(incomeSttatus==2||incomeSttatus==3||incomeSttatus==1){
                if(coast_title_txt.text!=null&&coast_title_txt.text.isNotEmpty()){
                    updatOrder()
                }else{
                    updatOrder()
                }
            }else{
                if (incomeSttatus==4){
                    Toast.makeText(this, "تم تأكيد تنفيذ الطلب من قبل", Toast.LENGTH_SHORT).show()
                }else if(incomeSttatus==5){
                    Toast.makeText(this, "تم تأكيد إلغاء الطلب من قبل", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, "الرجاء انتظر حتي يتم معالجة الطلب ليتم تعديل الحالة", Toast.LENGTH_SHORT).show()
                }
            }
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
                dialog.dismiss()}
            alertBuilder.setNegativeButton(R.string.dismiss) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        }
        alertBuilder.show()
    }
    private fun onObserveStart(){
        progrss_lay?.let {
            it.visibility= View.VISIBLE
        }
        profile_lay?.visibility= View.GONE
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
}