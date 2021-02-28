package com.ostah_app.views.home.home

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ostah_app.R
import com.ostah_app.data.remote.apiServices.ApiClient
import com.ostah_app.data.remote.apiServices.SessionManager
import com.ostah_app.data.remote.objects.BaseResponseModel
import com.ostah_app.data.remote.objects.Services
import com.ostah_app.data.remote.objects.ServicesModel
import com.ostah_app.views.home.MainActivity
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class HomeFragment : Fragment() {


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRVAdapter()
        offersObserver()

    }
    private fun initRVAdapter() {

        val servicesLayoutManager = GridLayoutManager(mContext,3,RecyclerView.VERTICAL,false)

        services_rv.layoutManager = servicesLayoutManager
        servicesAddapter= ServicesListAdapter(mContext!!, servicesList)
        services_rv.adapter = servicesAddapter

    }
    private fun offersObserver() {
        onObserveStart()
        apiClient = ApiClient()
        sessionManager = SessionManager(mContext!!)
        apiClient.getApiService(mContext!!).getAllServices()
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
                                        servicesAddapter!!.notifyDataSetChanged()
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
    fun alertNetwork(isExit: Boolean = true) {
        val alertBuilder = AlertDialog.Builder(mContext!!)
        //alertBuilder.setTitle(R.string.error)
        alertBuilder.setMessage(R.string.no_internet)
        if (isExit) {
            // alertBuilder.setPositiveButton(R.string.exit) { dialog: DialogInterface, _: Int -> context!!.finish() }
        } else {
            alertBuilder.setPositiveButton(R.string.try_again) { dialog: DialogInterface, _: Int ->
                offersObserver()
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

}