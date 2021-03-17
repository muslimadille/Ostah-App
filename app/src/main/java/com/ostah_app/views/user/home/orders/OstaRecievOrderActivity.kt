package com.ostah_app.views.user.home.orders

import BaseActivity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ostah_app.R
import com.ostah_app.views.user.home.profile.SenderDetailsActivity
import kotlinx.android.synthetic.main.activity_osta_reciev_order.*

class OstaRecievOrderActivity : BaseActivity() {
    var ticketId=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_osta_reciev_order)
        ticketId=intent.getIntExtra("id",0)
        onShowUserClicked()
    }
    private fun onShowUserClicked(){
        show_user_info_btn.setOnClickListener {
            val intent= Intent(this, SenderDetailsActivity::class.java)
            intent.putExtra("tecket_id",ticketId)
            startActivity(intent)
        }

    }
}