package com.ostah_app.views.user.home

import BaseActivity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.ostah_app.R
import com.ostah_app.utiles.Q
import com.ostah_app.views.user.base.GlideObject
import com.ostah_app.views.user.home.home.HomeFragment
import com.ostah_app.views.user.home.more.MoreFragment
import com.ostah_app.views.user.home.orders.OrdersFragment
import com.ostah_app.views.user.home.previos.PreviosFragment
import com.ostah_app.views.user.home.profile.OstahProfileFragment
import com.ostah_app.views.user.home.profile.ProfileFragment
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_profile.*
import java.io.File

class MainActivity : BaseActivity() {
    var key=false
    var navK=0
    var selectedImage: File? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        key=intent.getBooleanExtra("key",false)
        navK=intent.getIntExtra("navK",0)
        initBottomNavigation()
    }
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            var result: CropImage.ActivityResult? = null
            data?.let { result = CropImage.getActivityResult(data) }
            if (resultCode == RESULT_OK) {
                result?.let {
                    selectedImage = File(result!!.uri!!.path!!)

                    GlideObject.GlideProfilePic(this, selectedImage!!.path, user_img)
//                    Picasso.get().load(selectedImage!!).fit().centerCrop().into(ivUserImage )
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                result!!.error!!.printStackTrace()
            }
        }
    }
    private fun initBottomNavigation(){

        val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    val fragment = HomeFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.container, fragment, fragment.javaClass.getSimpleName())
                            .commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_orders -> {
                    val fragment = OrdersFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.container, fragment, fragment.javaClass.getSimpleName())
                            .commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_previous -> {
                    val fragment = PreviosFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.container, fragment, fragment.javaClass.getSimpleName())
                            .commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_profile->{
                    if(preferences!!.getInteger(Q.USER_TYPE,0)== Q.TYPE_USER){
                        val fragment = ProfileFragment()
                        supportFragmentManager.beginTransaction().replace(R.id.container, fragment, fragment.javaClass.getSimpleName())
                            .commit()
                        return@OnNavigationItemSelectedListener true
                    }
                    if(preferences!!.getInteger(Q.USER_TYPE,0)== Q.TYPE_OSTA){
                        val fragment = OstahProfileFragment()
                        supportFragmentManager.beginTransaction().replace(R.id.container, fragment, fragment.javaClass.getSimpleName())
                            .commit()
                        return@OnNavigationItemSelectedListener true
                    }

                }
                R.id.navigation_extras->{
                    val fragment = MoreFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.container, fragment, fragment.javaClass.getSimpleName())
                            .commit()
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }
        bottomNavigationView.labelVisibilityMode= LabelVisibilityMode.LABEL_VISIBILITY_LABELED
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
       /* if(key){
            bottomNavigationView.selectedItemId = R.id.navigation_appointment
        }*/
        when (navK) {
            0 -> {
                bottomNavigationView.selectedItemId = R.id.navigation_home
            }
            1 -> {
                bottomNavigationView.selectedItemId = R.id.navigation_orders
            }
            2 -> {
                bottomNavigationView.selectedItemId = R.id.navigation_previous
            }
            3 -> {
                bottomNavigationView.selectedItemId = R.id.navigation_profile
            }
            4 -> {
                bottomNavigationView.selectedItemId = R.id.navigation_extras
            }
        }
    }

}