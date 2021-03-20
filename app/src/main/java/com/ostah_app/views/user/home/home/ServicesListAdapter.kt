package com.ostah_app.views.user.home.home

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ostah_app.R
import com.ostah_app.data.remote.objects.Services
import com.ostah_app.views.user.home.home.orders.ostahs_list.OstahsListActivity
import com.ostah_app.views.user.home.home.orders.ostahs_list.new_order.MapsActivity
import kotlinx.android.synthetic.main.service_list_item.view.*

class ServicesListAdapter(
        private val mContext: Context,
        private val list: MutableList<Services>
) : RecyclerView.Adapter<ServicesListAdapter.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val convertView = inflater.inflate(R.layout.service_list_item, parent, false)
        return ViewHolder(convertView)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val service = list[position]
       holder.service_name!!.text=service.name
        var offerImage=""
        if(service.image.isNotEmpty()){
            offerImage=service.image
        }else{
            offerImage=""
        }
        Glide.with(mContext).applyDefaultRequestOptions(
                RequestOptions()
                        .placeholder(R.drawable.person_ic)
                        .error(R.drawable.person_ic))
                .load(offerImage)
                .centerCrop()
                .into(holder.service_img!!)

        holder.service_lay!!.setOnClickListener {
           val intent= Intent(mContext, MapsActivity::class.java)
            intent.putExtra("service_id",service.id)
            intent.putExtra("service_name",service.name)
            intent.putExtra("service_img",service.image)

            mContext.startActivity(intent)
        }

    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val service_img: ImageView? =view.service_img
        val service_name: TextView? =view.service_title
        val service_lay: LinearLayout? =view.service_item_lay



    }


}