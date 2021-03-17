package com.ostah_app.views.user.home.previos

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ostah_app.R
import com.ostah_app.data.remote.objects.Tickets
import com.ostah_app.views.user.home.MainActivity
import kotlinx.android.synthetic.main.previous_order_item.view.*


class PreviousOrdersAdapter(
    private val mContext: MainActivity,
    private val list: MutableList<Tickets>
) : RecyclerView.Adapter<PreviousOrdersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val convertView = inflater.inflate(R.layout.previous_order_item, parent, false)
        return ViewHolder(convertView)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = list[position]
        holder.order_type_txt.text=order.service.name
        holder.order_topic_txt.text=order.title
        holder.order_status_txt.text=order.status.name
        holder.order_start_txt.text=order.date
        holder.order_end_txt.text=order.end_date
        holder.order_coast_txt.text=order.price.toString()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val order_type_txt: TextView =view.order_type_txt
        val order_topic_txt: TextView =view.order_topic_txt
        val order_status_txt: TextView =view.order_status_txt
        val order_coast_txt: TextView =view.order_coast_txt
        val order_start_txt: TextView =view.order_start_txt
        val order_end_txt: TextView =view.order_end_txt





    }
}