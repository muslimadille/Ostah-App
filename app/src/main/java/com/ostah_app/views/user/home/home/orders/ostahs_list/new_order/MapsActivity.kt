package com.ostah_app.views.user.home.home.orders.ostahs_list.new_order

import BaseActivity
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.ostah_app.R
import com.ostah_app.views.user.home.home.orders.ostahs_list.OstahsListActivity
import kotlinx.android.synthetic.main.activity_maps.*
import java.io.IOException
import java.util.*

class MapsActivity : BaseActivity(), OnMapReadyCallback, LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

//map values
    private var mMap: GoogleMap? = null
    internal lateinit var mLastLocation: Location
    var mCurrLocationMarker: Marker? = null
    internal var mGoogleApiClient: GoogleApiClient? = null
    internal lateinit var mLocationRequest: LocationRequest
    var fusedLocationProviderClient: FusedLocationProviderClient?=null


    var lat="3.000"
    var lng="2.000"
    //logic values
    var servceId=0
    var servceName="قائمة الفنيين"
    var servicesImg=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this)
        fetchLocation()
        onnextClicked()
        onSearchClicked()
        servceId=intent.getIntExtra("service_id",0)
        servceName=intent.getStringExtra("service_name")!!
        servicesImg=intent.getStringExtra("service_img")!!

    }
    private fun fetchLocation(){
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),1000)
            return
        }
        val task=fusedLocationProviderClient?.lastLocation
        task?.addOnSuccessListener { location->
            if(location!=null){
                this.mLastLocation=location
                val mapFragment = supportFragmentManager
                        .findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync(this)
            }
        }
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient()
                mMap!!.isMyLocationEnabled = true
            }
        } else {
            buildGoogleApiClient()
            mMap!!.isMyLocationEnabled = true
        }
        val lat_long=LatLng(mLastLocation?.latitude!!,mLastLocation?.longitude!!)
        drawMarker(lat_long)
        mMap!!.setOnMarkerDragListener(object :GoogleMap.OnMarkerDragListener{
            override fun onMarkerDragStart(p0: Marker?) {
            }

            override fun onMarkerDrag(p0: Marker?) {
            }

            override fun onMarkerDragEnd(p0: Marker?) {

                if(mCurrLocationMarker!=null){
                    mCurrLocationMarker?.remove()
                    var newLatLng=LatLng(p0?.position!!.latitude,p0.position.longitude)
                    drawMarker(newLatLng)
                    lat=p0?.position!!.latitude.toString()
                    lng=p0.position.longitude.toString()

                }
            }
        })

    }

    @Synchronized
    protected fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build()
        mGoogleApiClient!!.connect()
    }

    override fun onConnected(bundle: Bundle?) {

        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 1000
        mLocationRequest.fastestInterval = 1000
        mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.getFusedLocationProviderClient(this)
        }
    }

    override fun onConnectionSuspended(i: Int) {

    }

    override fun onLocationChanged(location: Location) {

        mLastLocation = location
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker!!.remove()
        }
        //Place current location marker
        val latLng = LatLng(location.latitude, location.longitude)
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.title("Current Position")
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        mCurrLocationMarker = mMap!!.addMarker(markerOptions)

        //move map camera
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap!!.animateCamera(CameraUpdateFactory.zoomTo(11f))

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.getFusedLocationProviderClient(this)
        }

    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {

    }

    private fun onSearchClicked(){
        search_btn.setOnClickListener {
            searchLocation()
        }
    }
    fun searchLocation() {
        val locationSearch:EditText = findViewById<EditText>(R.id.editText)
        lateinit var location: String
        location = locationSearch.text.toString()
        var addressList: List<Address>? = null

        if (location == null || location == "") {
            Toast.makeText(applicationContext,"provide location",Toast.LENGTH_SHORT).show()
        }
        else{
            val geoCoder = Geocoder(this)
            try {
                addressList = geoCoder.getFromLocationName(location, 1)

            } catch (e: IOException) {
                e.printStackTrace()
                addressList=null
            }

            if(addressList==null||addressList.isEmpty()){
                Toast.makeText(applicationContext, "الرجاء إدخال عنوان صحيح", Toast.LENGTH_LONG).show()
            }else{
                val address = addressList!![0]
                val latLng = LatLng(address.latitude, address.longitude)
                if(mCurrLocationMarker!=null){
                    mCurrLocationMarker?.remove()
                }
                drawMarker(latLng)
                mMap!!.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                //Toast.makeText(applicationContext, address.latitude.toString() + " " + address.longitude, Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun drawMarker(lat_long:LatLng){
        val markerOption= MarkerOptions().position(lat_long).snippet(getAddress(lat_long.latitude,lat_long.latitude)).draggable(true)
        mMap!!.animateCamera(CameraUpdateFactory.newLatLng(lat_long))
        mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(lat_long,11f))
        mCurrLocationMarker=mMap!!.addMarker(markerOption)
        mCurrLocationMarker?.showInfoWindow()
        //edit_location_txt.text=getAddress(lat_long.latitude,lat_long.longitude).toString()
        lat=lat_long.latitude.toString()
        lng=lat_long.longitude.toString()
        //Toast.makeText(applicationContext,lat +" "+ lng, Toast.LENGTH_LONG).show()


    }
    private  fun getAddress(lat:Double,lng:Double):String{
        val getCoder= Geocoder(this, Locale.getDefault())
        val address=getCoder.getFromLocation(lat,lng,1)
        return address[0].getAddressLine(0).toString()
    }
    private fun onnextClicked(){
        next_btn.setOnClickListener {
            val intent= Intent(this, OstahsListActivity::class.java)
            intent.putExtra("service_id",servceId)
            intent.putExtra("service_name",servceName)
            intent.putExtra("service_img",servicesImg)
            intent.putExtra("lat",lat)
            intent.putExtra("lng",lng)
            startActivity(intent)
        }

    }

}