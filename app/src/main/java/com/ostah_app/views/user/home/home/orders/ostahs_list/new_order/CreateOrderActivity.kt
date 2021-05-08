package com.ostah_app.views.user.home.home.orders.ostahs_list.new_order

import BaseActivity
import android.Manifest
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.ostah_app.R
import com.ostah_app.data.remote.apiServices.ApiClient
import com.ostah_app.data.remote.apiServices.SessionManager
import com.ostah_app.data.remote.objects.BaseResponseModel
import com.ostah_app.data.remote.objects.OrderTecket
import com.ostah_app.views.user.base.GlideObject
import com.ostah_app.views.user.home.MainActivity
import com.ostah_app.views.user.home.more.ContactUsActivity
import kotlinx.android.synthetic.main.activity_create_order.*
import kotlinx.android.synthetic.main.activity_create_order.bottomNavigationView
import kotlinx.android.synthetic.main.activity_create_order.contactus_btn
import kotlinx.android.synthetic.main.activity_create_order.date_header
import kotlinx.android.synthetic.main.activity_create_order.date_picker_btn
import kotlinx.android.synthetic.main.activity_create_order.date_txt
import kotlinx.android.synthetic.main.activity_create_order.description_header
import kotlinx.android.synthetic.main.activity_create_order.description_txt
import kotlinx.android.synthetic.main.activity_create_order.isNow_cb
import kotlinx.android.synthetic.main.activity_create_order.order_title_header
import kotlinx.android.synthetic.main.activity_create_order.order_title_txt
import kotlinx.android.synthetic.main.activity_create_order.profile_lay
import kotlinx.android.synthetic.main.activity_create_order.progrss_lay
import kotlinx.android.synthetic.main.activity_create_order.save_btn_lay
import kotlinx.android.synthetic.main.activity_create_order.user_img
import kotlinx.android.synthetic.main.activity_direct_order.*
import kotlinx.android.synthetic.main.fragment_profile.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class CreateOrderActivity : BaseActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var apiClient: ApiClient
    var ostah_name=""
    var ostah_img=""
    var ostah_id=0
    //inputs
    var title=""
    var decription=""
    var date=""
    var isNow:Int=0
    var lat=""
    var lng=""
    var ostaService=""
    //date

    lateinit var dpd: DatePickerDialog

    //location-----------------------------------------------------
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var lastLocation: Location? = null
    private var latitudeLabel: String? = "36.222"
    private var longitudeLabel: String? = "35.666"

    private val TAG = "LocationProvider"
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 52
    lateinit var locationRequest:LocationRequest
    //-------------------------------------------------------------

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_order)
        initLocation()
        getIntentValues()
        setOstahData()
        onSendClicked()
        pickDate()
        onNowChecked()
        initBottomNavigation()
        onContatUs()

    }
    private fun onContatUs(){
        contactus_btn.setOnClickListener {
            contactUs()
        }
    }
    private fun contactUs() {
        val intent=Intent(this@CreateOrderActivity, ContactUsActivity::class.java)
        startActivity(intent)
    }
    public override fun onStart() {
        super.onStart()
        if (!checkPermissions()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions()
            }
        }
        else {
            getLastLocation()
        }
    }
    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        fusedLocationClient?.lastLocation!!.addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                if(task.result != null){
                    lastLocation = task.result
                    latitudeLabel = (lastLocation)!!.latitude.toString()
                    longitudeLabel= (lastLocation)!!.longitude.toString()
                }else{
                    getNewLocation()
                }

            }
            else {
                Log.w(TAG, "getLastLocation:exception", task.exception)
                showMessage("No location detected. Make sure location is enabled on the device.")
            }
        }
    }
    private fun getNewLocation(){
        locationRequest= LocationRequest()
        locationRequest.priority=LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval=0
        locationRequest.fastestInterval=0
        locationRequest.numUpdates=2
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient!!.requestLocationUpdates(locationRequest,locationCallback,Looper.myLooper())
    }
    private val locationCallback=object :LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            var lastLocation=p0.lastLocation
            latitudeLabel = (lastLocation)!!.latitude.toString()
            longitudeLabel= (lastLocation)!!.longitude.toString()
        }
    }
    private fun getIntentValues(){
        ostaService =intent.getStringExtra("service")!!

        ostah_name=intent.getStringExtra("ostah_name")!!
        ostah_img=intent.getStringExtra("ostah_img")!!
        ostah_id=intent.getIntExtra("ostah_id",0)
        lat=intent.getStringExtra("lat")!!
        lng=intent.getStringExtra("lng")!!
    }
    private fun setOstahData(){
        GlideObject.GlideProfilePic(this, ostah_img,user_img)
        osata_name.text=ostah_name
        osata_department.text=ostaService

    }
    private fun initLocation(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }
    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return permissionState == PackageManager.PERMISSION_GRANTED
    }
    private fun startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_PERMISSIONS_REQUEST_CODE
        )
    }
    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.")
            showSnackbar("Location permission is needed for core functionality", "Okay",
                View.OnClickListener {
                    startLocationPermissionRequest()
                })
        }
        else {
            Log.i(TAG, "Requesting permission")
            startLocationPermissionRequest()
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.i(TAG, "onRequestPermissionResult")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            when {
                grantResults.isEmpty() -> {
                    // If user interaction was interrupted, the permission request is cancelled and you
                    // receive empty arrays.
                    Log.i(TAG, "User interaction was cancelled.")
                }
                grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                    // Permission granted.
                    getLastLocation()
                }
                else -> {
                    showSnackbar("Permission was denied", "Settings",
                        View.OnClickListener {
                            // Build intent that displays the App settings screen.
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts(
                                "package",
                                Build.DISPLAY, null
                            )
                            intent.data = uri
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }

    private fun showMessage(string: String) {
        Toast.makeText(this, string, Toast.LENGTH_LONG).show()
    }
    private fun showSnackbar(
        mainTextStringId: String, actionStringId: String,
        listener: View.OnClickListener
    ) {
        Toast.makeText(this, mainTextStringId, Toast.LENGTH_LONG).show()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun creatNewOrder(){
        onObserveStart()
        if(isNow==1){date=getCurrnetDate()}
        apiClient = ApiClient()
        sessionManager = SessionManager(this)
        apiClient.getApiService(this).createOrder(ostah_id,decription,title,date,isNow,lat.toString(),lng.toString())
            .enqueue(object : Callback<BaseResponseModel<OrderTecket>> {
                override fun onFailure(call: Call<BaseResponseModel<OrderTecket>>, t: Throwable) {
                    alertNetwork(false)
                }

                override fun onResponse(
                    call: Call<BaseResponseModel<OrderTecket>>,
                    response: Response<BaseResponseModel<OrderTecket>>
                ) {
                    if (response!!.isSuccessful) {
                        if (response.body()!!.success) {
                            response.body()!!.data!!.let {
                                if (it!=null) {
                                    onObserveSuccess()
                                    Toast.makeText(this@CreateOrderActivity, "تم إرسال طلبك بنجاح", Toast.LENGTH_SHORT).show()

                                    val intent= Intent(this@CreateOrderActivity, MainActivity::class.java)
                                    intent.putExtra("navK",1.toInt())
                                    this@CreateOrderActivity.startActivity(intent)
                                } else {
                                    onObservefaled()
                                    Toast.makeText(this@CreateOrderActivity, "faid", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            onObservefaled()
                            Toast.makeText(this@CreateOrderActivity, "faid", Toast.LENGTH_SHORT).show()

                        }

                    } else {
                        Toast.makeText(this@CreateOrderActivity, "connect faid", Toast.LENGTH_SHORT).show()

                    }

                }


            })
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun onSendClicked(){
        save_btn_lay.setOnClickListener {
            validatInputs()
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
    @RequiresApi(Build.VERSION_CODES.O)
    fun alertNetwork(isExit: Boolean = true) {
        val alertBuilder = AlertDialog.Builder(this)
        //alertBuilder.setTitle(R.string.error)
        alertBuilder.setMessage(R.string.no_internet)
        if (isExit) {
            // alertBuilder.setPositiveButton(R.string.exit) { dialog: DialogInterface, _: Int -> context!!.finish() }
        } else {
            alertBuilder.setPositiveButton(R.string.try_again) { dialog: DialogInterface, _: Int ->
                creatNewOrder()
                dialog.dismiss()}
            alertBuilder.setNegativeButton(R.string.dismiss) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        }
        alertBuilder.show()
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun validatInputs(){
        if(order_title_txt.text.isNotEmpty()){
            title=order_title_txt.text.toString()
            order_title_header.setTextColor(getColor(R.color.gray))

        }else{
            order_title_header.setTextColor(getColor(R.color.red))
            Toast.makeText(this, "أدخل جميع البيانات", Toast.LENGTH_SHORT).show()
        }
        if(date_txt.text.isNotEmpty()||isNow==1){
            date=date_txt.text.toString()
            date_header.setTextColor(getColor(R.color.gray))

        }else{
            date_header.setTextColor(getColor(R.color.red))
            Toast.makeText(this, "أدخل جميع البيانات", Toast.LENGTH_SHORT).show()}
        if(description_txt.text.isNotEmpty()){
            decription=description_txt.text.toString()
            description_header.setTextColor(getColor(R.color.gray))

        }else{
            description_header.setTextColor(getColor(R.color.red))
            Toast.makeText(this, "أدخل جميع البيانات", Toast.LENGTH_SHORT).show()}

        if(title.isNotEmpty()&&decription.isNotEmpty()){
            if(isNow==1){
                date=getCurrnetDate()
                creatNewOrder()

            }else{
                if(date.isEmpty()){
                    Toast.makeText(this, "أدخل جميع البيانات", Toast.LENGTH_SHORT).show()

                }else{
                    creatNewOrder()
                }
            }

        }
    }
    private fun pickDate(){
        var calendar= Calendar.getInstance()

        var year=calendar.get(Calendar.YEAR)
        var month=calendar.get(Calendar.MONTH)
        var day=calendar.get(Calendar.DAY_OF_MONTH)
        date_picker_btn.setOnClickListener {
            dpd= DatePickerDialog(this,
                { view, myear, mMonth, mdayOfMonth ->

                    var month = ""
                    var day = ""
                    if (mMonth < 10) {
                        month = "0${mMonth+1}"
                    } else {
                        month = "${mMonth+1}"
                    }
                    if (mdayOfMonth < 10) {
                        day = "0$mdayOfMonth"
                    } else {
                        day = "$mdayOfMonth"
                    }
                    date="$myear-$month-$day"+" "+"00:00:00"
                    date_txt.text=date.toString()
                }, year, month, day
            )
            dpd.show()

        }
    }
    private fun onNowChecked(){
        if(isNow_cb.isChecked){
            isNow=1
        }else{
            isNow=0
        }
        isNow_cb.setOnClickListener {
            if(isNow_cb.isChecked){
                isNow=1
            }else{
                isNow=0
            }
        }

    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrnetDate():String{
        val currentDateTime= LocalDateTime.now()
        return currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toString()

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