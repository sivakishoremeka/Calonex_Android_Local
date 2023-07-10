package mp.app.calonex.landlord.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_noitfy_screen.*
import mp.app.calonex.R
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.adapter.NotificationAdapter

class NotifyScreen : AppCompatActivity() {

    var tabLayoutNotify:TabLayout?=null
    var vpNotify:ViewPager?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_noitfy_screen)
        tabLayoutNotify=findViewById(R.id.tabLayout_notify)
        vpNotify=findViewById(R.id.vp_notify)
        Kotpref.isNotifyRefresh = false
        setViewPager()

    }

    private fun setViewPager(){

        val fragmentAdapter = NotificationAdapter(supportFragmentManager)
        vpNotify!!.adapter = fragmentAdapter

        tabLayoutNotify?.setupWithViewPager(vpNotify)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        Util.navigationBack(this@NotifyScreen)
    }
}
