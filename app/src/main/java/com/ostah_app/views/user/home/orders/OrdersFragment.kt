package com.ostah_app.views.user.home.orders

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ostah_app.R
import com.ostah_app.data.remote.apiServices.ApiClient
import com.ostah_app.data.remote.apiServices.SessionManager
import com.ostah_app.data.remote.objects.BaseResponseModel
import com.ostah_app.data.remote.objects.OrderItemModel
import com.ostah_app.data.remote.objects.Tickets
import com.ostah_app.utiles.Q
import com.ostah_app.views.user.home.MainActivity
import kotlinx.android.synthetic.main.fragment_orders.*
import kotlinx.android.synthetic.main.fragment_orders.no_data_lay
import kotlinx.android.synthetic.main.fragment_orders.profile_lay
import kotlinx.android.synthetic.main.fragment_orders.user_orders_rv
import kotlinx.android.synthetic.main.fragment_orders.progrss_lay
import kotlinx.android.synthetic.main.fragment_orders.visitor_lay
import kotlinx.android.synthetic.main.fragment_previos.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class OrdersFragment : Fragment() {

    private lateinit var sessionManager: SessionManager
    private lateinit var apiClient: ApiClient

    private var ordersList:MutableList<Tickets> = ArrayList()

    private var ordersListAddapter: UserOrdersAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_orders, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(mContext!!.preferences!!.getString(Q.USER_NAME,"").isNotEmpty()){
            initRVAdapter()
            if(mContext!!.preferences!!.getInteger(Q.USER_TYPE,0)== Q.TYPE_USER){
                getUserOrders()
            }else{
                getOstahOrders()
            }
        }else{
            profile_lay.visibility=View.GONE
            visitor_lay.visibility=View.VISIBLE
        }

    }
    private fun initRVAdapter(){
        val layoutManager = LinearLayoutManager(mContext!!, RecyclerView.VERTICAL, false)
        user_orders_rv?.layoutManager = layoutManager
        ordersListAddapter = UserOrdersAdapter(mContext!!, ordersList)
        user_orders_rv?.adapter = ordersListAddapter
    }
    private fun getUserOrders() {
        onObserveStart()
        apiClient = ApiClient()
        sessionManager = SessionManager(mContext!!)
        apiClient.getApiService(mContext!!).getUserOrders()
            .enqueue(object : Callback<BaseResponseModel<OrderItemModel>> {
                override fun onFailure(call: Call<BaseResponseModel<OrderItemModel>>, t: Throwable) {
                    alertNetwork(false)
                }

                override fun onResponse(
                    call: Call<BaseResponseModel<OrderItemModel>>,
                    response: Response<BaseResponseModel<OrderItemModel>>
                ) {
                    if (response!!.isSuccessful) {
                        if (response.body()!!.success) {
                            response.body()!!.data!!.tickets.let {
                                it.forEach {  tiket->
                                    if(tiket.status_id!=5&&tiket.status_id!=4){

                                        ordersList.add(tiket)

                                    }
                                }

                                ordersListAddapter!!.notifyDataSetChanged()
                                onObserveSuccess()
                                if(ordersList.isEmpty()){
                                    no_data_lay?.visibility=View.VISIBLE
                                    user_orders_rv?.visibility=View.GONE
                                }else{
                                    no_data_lay?.visibility=View.GONE
                                    user_orders_rv?.visibility=View.VISIBLE
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
    private fun getOstahOrders() {
        onObserveStart()
        apiClient = ApiClient()
        sessionManager = SessionManager(mContext!!)
        apiClient.getApiService(mContext!!).getOstahOrders()
            .enqueue(object : Callback<BaseResponseModel<List<Tickets>>> {
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
                                    user_orders_rv?.visibility=View.GONE
                                }else{
                                    no_data_lay?.visibility=View.GONE
                                    user_orders_rv?.visibility=View.VISIBLE
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
    fun alertNetwork(isExit: Boolean = true) {
        val alertBuilder = AlertDialog.Builder(mContext!!)
        //alertBuilder.setTitle(R.string.error)
        alertBuilder.setMessage(R.string.no_internet)
        if (isExit) {
            // alertBuilder.setPositiveButton(R.string.exit) { dialog: DialogInterface, _: Int -> context!!.finish() }
        } else {
            alertBuilder.setPositiveButton(R.string.try_again) { dialog: DialogInterface, _: Int ->
                getUserOrders()
                dialog.dismiss()}
            alertBuilder.setNegativeButton(R.string.dismiss) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        }
        alertBuilder.show()
    }
    private fun onObserveStart(){
        progrss_lay?.let {
            it.visibility= View.VISIBLE
        }
        user_orders_rv?.visibility= View.GONE
    }
    private fun onObserveSuccess(){
        progrss_lay?.let {
            it.visibility= View.GONE
        }
        user_orders_rv?.let {
            it.visibility= View.VISIBLE
        }
    }
    private fun onObservefaled(){
        progrss_lay?.let {
            it.visibility= View.GONE
        }
        user_orders_rv?.let {
            it.visibility= View.VISIBLE
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