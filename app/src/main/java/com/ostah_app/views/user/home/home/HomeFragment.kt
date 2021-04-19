package com.ostah_app.views.user.home.home

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ostah_app.R
import com.ostah_app.data.remote.apiServices.ApiClient
import com.ostah_app.data.remote.apiServices.SessionManager
import com.ostah_app.data.remote.objects.*
import com.ostah_app.utiles.Q
import com.ostah_app.views.user.home.MainActivity
import com.ostah_app.views.user.home.home.orders.ostahs_list.OstahLastOrdersAdapter
import com.ostah_app.views.user.home.home.orders.ostahs_list.new_order.DirectOrderActivity
import com.ostah_app.views.user.home.home.orders.ostahs_list.new_order.MapsActivity
import com.ostah_app.views.user.home.orders.UserOrdersAdapter
import kotlinx.android.synthetic.main.activity_order_state.*
import kotlinx.android.synthetic.main.activity_ostahs_list.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.no_data_lay
import kotlinx.android.synthetic.main.fragment_home.progrss_lay
import kotlinx.android.synthetic.main.fragment_home.refresh_btn
import kotlinx.android.synthetic.main.fragment_home.refresh_lay
import me.relex.circleindicator.CircleIndicator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {
    private var sliderAddapter: SliderAdapter? = null

    private val slidsList: MutableList<Slides> = ArrayList()

    private var ordersList:MutableList<Tickets> = ArrayList()
    private var ordersListAddapter: OstahLastOrdersAdapter?=null
    private val servicesList: MutableList<Services> = ArrayList()
    private var servicesAddapter: ServicesListAdapter? = null
    private lateinit var sessionManager: SessionManager
    private lateinit var apiClient: ApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userCheck()
        initSlider()
        OnDirectOrderClicked()
        refresh()
        if (mContext!!.preferences!!.getString(Q.USER_NAME,"").isEmpty()){
            contact_ostah_lay.visibility=View.GONE
            refresh_lay.visibility=View.VISIBLE
        }
    }
    private fun getOstahOrders() {
        onObserveStart()
        apiClient = ApiClient()
        sessionManager = SessionManager(mContext!!)
        apiClient.getApiService(mContext!!).getOstahLastOrders()
            .enqueue(object : Callback<BaseResponseModel<List<Tickets>>> {
                @RequiresApi(Build.VERSION_CODES.M)
                override fun onFailure(call: Call<BaseResponseModel<List<Tickets>>>, t: Throwable) {
                    alertNetwork(false)
                }

                override fun onResponse(
                    call: Call<BaseResponseModel<List<Tickets>>>,
                    response: Response<BaseResponseModel<List<Tickets>>>
                ) {
                    if (response!!.isSuccessful) {
                        if (response.body()!!.success) {
                            response.body()!!.data!!.let {
                                ordersList.addAll(it)
                                ordersListAddapter!!.notifyDataSetChanged()
                                onObserveSuccess()
                                if(it.isEmpty()){
                                    no_data_lay?.visibility=View.VISIBLE
                                    services_rv?.visibility=View.GONE
                                }else{
                                    no_data_lay?.visibility=View.GONE
                                    services_rv?.visibility=View.VISIBLE
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
    private fun initRVAdapter() {

        val servicesLayoutManager = GridLayoutManager(mContext,3,RecyclerView.VERTICAL,false)

        services_rv.layoutManager = servicesLayoutManager
        servicesAddapter= ServicesListAdapter(mContext!!, servicesList)
        services_rv.adapter = servicesAddapter

    }
    private fun initOstahRVAdapter() {

        val servicesLayoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)

        services_rv.layoutManager = servicesLayoutManager
        ordersListAddapter= OstahLastOrdersAdapter(mContext!!, ordersList)
        services_rv.adapter = ordersListAddapter

    }
    private fun offersObserver() {
        onObserveStart()
        apiClient = ApiClient()
        sessionManager = SessionManager(mContext!!)
        apiClient.getApiService(mContext!!).getAllServices()
                .enqueue(object : Callback<BaseResponseModel<ServicesModel>> {
                    @RequiresApi(Build.VERSION_CODES.M)
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
                                        servicesList.clear()
                                        servicesList.addAll(it.services)
                                        servicesAddapter!!.notifyDataSetChanged()
                                        onObserveSuccess()
                                        contact_ostah_lay?.visibility=View.VISIBLE
                                        refresh_lay.visibility=View.GONE

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
    @RequiresApi(Build.VERSION_CODES.M)
    fun alertNetwork(isExit: Boolean = true) {
        val alertBuilder = AlertDialog.Builder(mContext!!)
        //alertBuilder.setTitle(R.string.error)
        alertBuilder.setMessage(R.string.no_internet)
        if (isExit) {
            // alertBuilder.setPositiveButton(R.string.exit) { dialog: DialogInterface, _: Int -> context!!.finish() }
        } else {
            alertBuilder.setPositiveButton(R.string.try_again) { dialog: DialogInterface, _: Int ->
                userCheck()
                dialog.dismiss()}
            alertBuilder.setNegativeButton(R.string.dismiss) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        }
        alertBuilder.show()
    }
    private fun onObserveStart(){
        progrss_lay?.let {
            it.visibility= View.VISIBLE
        }
        services_rv.visibility= View.GONE
    }
    private fun onObserveSuccess(){
        progrss_lay?.let {
            it.visibility= View.GONE
        }
        services_rv?.let {
            it.visibility= View.VISIBLE
        }
    }
    private fun onObservefaled(){
        progrss_lay?.let {
            it.visibility= View.GONE
        }
        services_rv?.let {
            it.visibility= View.GONE
        }
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
    @RequiresApi(Build.VERSION_CODES.M)
    private fun userCheck(){
        if(mContext!!.preferences!!.getInteger(Q.USER_TYPE,0)==Q.TYPE_USER){
            sliderImagesObserver()
            initRVAdapter()
            offersObserver()


        }
        if(mContext!!.preferences!!.getInteger(Q.USER_TYPE,0)==Q.TYPE_OSTA){
            home_fragment_lay.setBackgroundColor(mContext!!.getColor(R.color.white))
            logo_txt.setTextColor(mContext!!.getColor(R.color.base))
            ostah_last_olders_txt.visibility=View.VISIBLE
            ostahSliderImagesObserver()
            initOstahRVAdapter()
            getOstahOrders()
            contact_ostah_lay.visibility=View.GONE
            refresh_lay.visibility=View.VISIBLE

        }

    }

    private fun initSlider(){
        sliderAddapter = SliderAdapter(mContext!!, slidsList)
        offers_pager_Slider.adapter=sliderAddapter
        val indicator: CircleIndicator = requireView().findViewById(R.id.indicator) as CircleIndicator
        indicator.setViewPager(offers_pager_Slider)
        sliderAddapter!!.registerDataSetObserver(indicator.getDataSetObserver());

        val handler = Handler()
        val update = Runnable {
            if(offers_pager_Slider!=null){

                if(offers_pager_Slider.currentItem<(slidsList.size-1)){
                    offers_pager_Slider.currentItem=offers_pager_Slider.currentItem+1
                }else{
                    offers_pager_Slider.currentItem=0
                }
            }


        }
        Timer().schedule(object : TimerTask() {
            override fun run() {
                handler.post(update)
            }
        }, 3500, 3500)

    }
    private fun sliderImagesObserver() {
        apiClient = ApiClient()
        sessionManager = SessionManager(mContext!!)
        apiClient.getApiService(mContext!!).fitchUserSLiderImages()
                .enqueue(object : Callback<BaseResponseModel<SliderModel>> {
                    @RequiresApi(Build.VERSION_CODES.M)
                    override fun onFailure(call: Call<BaseResponseModel<SliderModel>>, t: Throwable) {
                        alertNetwork(true)
                    }

                    override fun onResponse(
                            call: Call<BaseResponseModel<SliderModel>>,
                            response: Response<BaseResponseModel<SliderModel>>
                    ) {
                        if (response!!.isSuccessful) {
                            if (response.body()!!.success) {
                                response.body()!!.data!!.let {
                                    if (it.slides.isNotEmpty()) {
                                        slidsList.clear()
                                        it.slides.forEach { offer: Slides ->
                                            slidsList.add(offer)
                                            sliderAddapter!!.notifyDataSetChanged()
                                        }
                                    } else {
                                        Toast.makeText(mContext, "empty", Toast.LENGTH_SHORT).show()
                                    }

                                }
                            } else {
                                Toast.makeText(mContext, "faid", Toast.LENGTH_SHORT).show()

                            }

                        } else {
                            Toast.makeText(mContext, "connect faid", Toast.LENGTH_SHORT).show()

                        }

                    }


                })
    }

    private fun ostahSliderImagesObserver() {
        apiClient = ApiClient()
        sessionManager = SessionManager(mContext!!)
        apiClient.getApiService(mContext!!).fitchOstahSLiderImages()
                .enqueue(object : Callback<BaseResponseModel<SliderModel>> {
                    @RequiresApi(Build.VERSION_CODES.M)
                    override fun onFailure(call: Call<BaseResponseModel<SliderModel>>, t: Throwable) {
                        alertNetwork(true)
                    }

                    override fun onResponse(
                            call: Call<BaseResponseModel<SliderModel>>,
                            response: Response<BaseResponseModel<SliderModel>>
                    ) {
                        if (response!!.isSuccessful) {
                            if (response.body()!!.success) {
                                response.body()!!.data!!.let {
                                    if (it.slides.isNotEmpty()) {
                                        it.slides.forEach { offer: Slides ->
                                            slidsList.add(offer)
                                            sliderAddapter!!.notifyDataSetChanged()
                                        }
                                    } else {
                                        Toast.makeText(mContext, "empty", Toast.LENGTH_SHORT).show()
                                    }

                                }
                            } else {
                                Toast.makeText(mContext, "faid", Toast.LENGTH_SHORT).show()

                            }

                        } else {
                            Toast.makeText(mContext, "connect faid", Toast.LENGTH_SHORT).show()

                        }

                    }


                })
    }


    private fun OnDirectOrderClicked(){

        contact_ostah_lay.setOnClickListener {
            val intent= Intent(mContext, MapsActivity::class.java)
            intent.putExtra("service_id",0)
            intent.putExtra("service_name","")
            intent.putExtra("service_img","")
            startActivity(intent)

        }
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun refresh(){
        refresh_btn.setOnClickListener {
            userCheck()
        }
    }
}