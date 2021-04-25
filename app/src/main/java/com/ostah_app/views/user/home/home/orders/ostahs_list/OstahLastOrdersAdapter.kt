package com.ostah_app.views.user.home.home.orders.ostahs_list

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.ostah_app.R
import com.ostah_app.data.remote.objects.Tickets
import com.ostah_app.data.remote.objects.UserResposeModel
import com.ostah_app.utiles.Q
import com.ostah_app.views.user.base.GlideObject
import com.ostah_app.views.user.home.MainActivity
import com.ostah_app.views.user.home.orders.OrderStateActivity
import com.ostah_app.views.user.home.orders.OstaRecievOrderActivity
import kotlinx.android.synthetic.main.user_order_list_item.view.*


class OstahLastOrdersAdapter(
    private val mContext: MainActivity,
    private val list: MutableList<Tickets>
) : RecyclerView.Adapter<OstahLastOrdersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val convertView = inflater.inflate(R.layout.user_order_list_item, parent, false)
        return ViewHolder(convertView)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = list[position]


        if(mContext!!.preferences!!.getInteger(Q.USER_TYPE,0 )==2) {
            holder.osta_btn_lay.visibility=View.VISIBLE
            holder.order_osta_name.text = order.user.name
            order.service.name.let { holder.department_txt.text = it }
            order.title.let { holder.subject_txt.text = it }
            GlideObject.GlideProfilePic(
                mContext,
                order.user.image,
                holder.order_user_img
            )

            holder.reciev_order_btn.setOnClickListener {
                val intent = Intent(mContext, OstaRecievOrderActivity::class.java)
                intent.putExtra("status", order.status_id.toInt())
                intent.putExtra("id", order.id.toInt())
                intent.putExtra("details", order.details)
                intent.putExtra("comment", order.title)
                intent.putExtra("image", order.user.image)
                intent.putExtra("name", order.user.name)
                intent.putExtra("lat",order.lat.toString())
                intent.putExtra("lng",order.lng.toString())

                mContext.startActivity(intent)
            }
            holder.show_order_btn.setOnClickListener {
                /*val intent = Intent(mContext, OrderStateActivity::class.java)
                intent.putExtra("status", order.status_id.toInt())
                intent.putExtra("id", order.id.toInt())*/
                val intent = Intent(mContext, OstaRecievOrderActivity::class.java)
                intent.putExtra("status", order.status_id.toInt())
                intent.putExtra("id", order.id.toInt())
                intent.putExtra("details", order.details)
                intent.putExtra("comment", order.title)
                intent.putExtra("image", order.user.image)
                intent.putExtra("name", order.user.name)
                intent.putExtra("lat",order.lat.toString())
                intent.putExtra("lng",order.lng.toString())
                mContext.startActivity(intent)
            }
        }

    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val order_user_img: ImageView = view.order_user_img
        val order_osta_name: TextView =view.order_osta_name
        val department_txt: TextView =view.department_txt
        val subject_txt: TextView =view.subject_txt
        val edit_order_btn: Button =view.edit_order_btn
        val order_lay: CardView =view.order_lay
        val reciev_order_btn:Button=view.reciev_order_btn
        val show_order_btn: LinearLayout =view.show_order_btn
        val osta_btn_lay: LinearLayout =view.osta_btn_lay
        val normal_btn_lay: LinearLayout =view.normal_btn_lay





    }
}