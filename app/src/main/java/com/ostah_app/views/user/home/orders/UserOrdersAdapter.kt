package com.ostah_app.views.user.home.orders

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.ostah_app.R
import com.ostah_app.data.remote.objects.Tickets
import com.ostah_app.utiles.Q
import com.ostah_app.views.user.base.GlideObject
import com.ostah_app.views.user.home.MainActivity
import kotlinx.android.synthetic.main.user_order_list_item.view.*


class UserOrdersAdapter(
    private val mContext: MainActivity,
    private val list: MutableList<Tickets>
) : RecyclerView.Adapter<UserOrdersAdapter.ViewHolder>() {

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





        if(mContext!!.preferences!!.getInteger(Q.USER_TYPE,0)==Q.TYPE_USER){
            holder.normal_btn_lay.visibility=View.VISIBLE

            if (order.reformer != null) holder.order_osta_name.text = order.reformer.name
            order.service.name.let { holder.department_txt.text = it }
            order.title.let { holder.subject_txt.text = it }
            if (order.reformer != null) GlideObject.GlideProfilePic(
                mContext,
                order.reformer.image,
                holder.order_user_img
            )
        }else{
            holder.normal_btn_lay.visibility=View.VISIBLE
            holder.order_osta_name.text = order.user.name
            order.service.name.let { holder.department_txt.text = it }
            order.title.let { holder.subject_txt.text = it }
            GlideObject.GlideProfilePic(
                mContext,
                order.user.image,
                holder.order_user_img
            )
        }

            holder.edit_order_btn.setOnClickListener {
                if(mContext!!.preferences!!.getInteger(Q.USER_TYPE,0)!=Q.TYPE_USER){
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
                }else{
                    val intent = Intent(mContext, OrderStateActivity::class.java)
                    intent.putExtra("status", order.status_id.toInt())
                    intent.putExtra("id", order.id.toInt())
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
        val order_lay:CardView=view.order_lay
        val reciev_order_btn:Button=view.reciev_order_btn
        val show_order_btn:LinearLayout=view.show_order_btn
        val osta_btn_lay:LinearLayout=view.osta_btn_lay
        val normal_btn_lay:LinearLayout=view.normal_btn_lay





    }
}