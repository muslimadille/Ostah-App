package com.ostah_app.views.user.home.home.orders.ostahs_list

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.ostah_app.R
import com.ostah_app.data.remote.objects.UserResposeModel
import com.ostah_app.utiles.Q
import com.ostah_app.views.user.base.GlideObject
import com.ostah_app.views.user.home.home.orders.ostahs_list.new_order.CreateOrderActivity
import kotlinx.android.synthetic.main.fragment_orders.*
import kotlinx.android.synthetic.main.ostahs_list_item.view.*
import kotlinx.android.synthetic.main.user_order_list_item.view.order_osta_name
import kotlinx.android.synthetic.main.user_order_list_item.view.order_user_img


class OstahsListAdapter(
    private val mContext: OstahsListActivity,
    private val list: MutableList<UserResposeModel>
) : RecyclerView.Adapter<OstahsListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val convertView = inflater.inflate(R.layout.ostahs_list_item, parent, false)
        return ViewHolder(convertView)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ostah = list[position]
        holder.order_osta_name.text=ostah.name
        holder.rating_rate.rating=ostah.rating.toFloat()
        holder.department_txt.text=ostah.service.name
        holder.distance_txt.text=ostah.distance_user_osta.toInt().toString()+" كم"
        GlideObject.GlideProfilePic(mContext, ostah.image,holder.order_user_img)
        holder.order_btn.setOnClickListener {
            if(mContext!!.preferences!!.getString(Q.USER_NAME,"").isNotEmpty()){
                val intent= Intent(mContext, CreateOrderActivity::class.java)
                intent.putExtra("ostah_name",ostah.name)
                intent.putExtra("ostah_img",ostah.image)
                intent.putExtra("service",ostah.service.name)
                intent.putExtra("ostah_id",ostah.id.toInt())
                intent.putExtra("lat",mContext.lat)
                intent.putExtra("lng",mContext.lng)

                mContext.startActivity(intent)
            }else{
                Toast.makeText(mContext, "لطلب تصليح قم بتسجيل الدخول ", Toast.LENGTH_SHORT).show()
            }

        }

    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val order_user_img: ImageView = view.order_user_img
        val order_osta_name: TextView =view.order_osta_name
        val rating_rate: RatingBar =view.rating_rate
        val order_btn: Button =view.order_btn
        val department_txt: TextView =view.department_txt
        val distance_txt: TextView =view.distance_txt







    }
}