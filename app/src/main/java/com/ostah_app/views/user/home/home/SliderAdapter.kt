package com.ostah_app.views.user.home.home

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ostah_app.R
import com.ostah_app.data.remote.objects.SliderModel
import com.ostah_app.data.remote.objects.Slides


class SliderAdapter(mContext: Context, list:MutableList<Slides>) : PagerAdapter(){
    private val mContext=mContext
    private var itemsList=list
    private var layoutInflater:LayoutInflater?=null
    override fun getCount(): Int {
        return itemsList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view==`object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater= LayoutInflater.from(mContext)
        val view=layoutInflater!!.inflate(R.layout.slider_item,container,false)
        var title:TextView=view.findViewById(R.id.slider_title)
        var content:TextView=view.findViewById(R.id.slider_content)
        var img:ImageView=view.findViewById(R.id.slider_img)

        title.text=itemsList[position].main_title
        content.text=itemsList[position].sub_title


        Glide.with(mContext).applyDefaultRequestOptions(
                RequestOptions()
                        .placeholder(R.color.gray_light)
                        .error(R.color.gray_light))
                .load(itemsList[position].image)
                .centerCrop()
                .into(img)
        container.addView(view,position)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object`as View)
    }
}