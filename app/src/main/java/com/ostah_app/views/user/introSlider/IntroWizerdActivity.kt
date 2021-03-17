package com.ostah_app.views.user.introSlider
import BaseActivity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.fragment.app.Fragment
import com.ostah_app.R
import com.ostah_app.utiles.Q
import com.ostah_app.views.user.introSlider.adapters.IntroPagerAdapter
import com.ostah_app.views.user.introSlider.fragments.*
import com.ostah_app.views.user.login.LoginActivity
import kotlinx.android.synthetic.main.activity_intro_wizerd.*
import java.util.*
import kotlin.collections.ArrayList

class IntroWizardActivity :BaseActivity() {
    private val fragmentList = ArrayList<Fragment>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_wizerd)

        // making the status bar transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        if(intent.getBooleanExtra("key",false)){
            intro_register_btn.visibility=View.GONE
        }

        val adapter = IntroPagerAdapter(this)
        vpIntroSlider.adapter = adapter
        fragmentList.addAll(listOf(
            Intro1Fragment(), Intro2Fragment(), Intro3Fragment(),Intro4Fragment(),Intro5Fragment(),Intro6Fragment()
        ))
        adapter.setFragmentList(fragmentList)

        val handler = Handler()
        val update = Runnable {
            if (vpIntroSlider.getCurrentItem() == 0) {
                vpIntroSlider.currentItem = 1
            } else if (vpIntroSlider.getCurrentItem() == 1) {
                vpIntroSlider.currentItem = 2
            }
            else if (vpIntroSlider.getCurrentItem() == 2) {
                vpIntroSlider.currentItem = 3
            }
            else if (vpIntroSlider.getCurrentItem() == 3) {
                vpIntroSlider.currentItem = 4
            }
            else if (vpIntroSlider.getCurrentItem() == 4) {
                vpIntroSlider.currentItem = 5
            }else {
                vpIntroSlider.currentItem = 0
            }

        }

        Timer().schedule(object : TimerTask() {
            override fun run() {
                handler.post(update)
            }
        }, 3500, 3500)


        intro_register_btn.setOnClickListener {
            if (vpIntroSlider.getCurrentItem() == 0) {
                vpIntroSlider.currentItem = 1
            } else if (vpIntroSlider.getCurrentItem() == 1) {
                vpIntroSlider.currentItem = 2
            }
            else if (vpIntroSlider.getCurrentItem() == 2) {
                vpIntroSlider.currentItem = 3
            }
            else if (vpIntroSlider.getCurrentItem() == 3) {
                vpIntroSlider.currentItem = 4
            }
            else if (vpIntroSlider.getCurrentItem() == 4) {
                vpIntroSlider.currentItem = 5
            }else {
                vpIntroSlider.currentItem = 0
            }
        }
        skip_btn.setOnClickListener {
            preferences!!.putBoolean(Q.IS_FIRST_TIME, false)
            preferences!!.commit()
            val intent = Intent(this@IntroWizardActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }


}