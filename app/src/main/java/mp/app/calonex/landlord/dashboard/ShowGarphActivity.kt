package mp.app.calonex.landlord.dashboard

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import mp.app.calonex.R

class ShowGarphActivity : AppCompatActivity() {
    var viewPager: ViewPager? = null
     var tabLayout: TabLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_garph)
        viewPager = findViewById<View>(R.id.view_pager) as ViewPager
        tabLayout = findViewById<View>(R.id.slidingTabs) as TabLayout
        setData()
    }

    private fun setData() {

        val  adapter = ChartAdapter(this,supportFragmentManager)
        viewPager!!.offscreenPageLimit = 4
        viewPager!!.adapter = adapter
        tabLayout!!.setupWithViewPager(viewPager)


    }
}