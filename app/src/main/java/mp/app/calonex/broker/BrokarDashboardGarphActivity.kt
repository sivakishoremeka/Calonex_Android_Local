package mp.app.calonex.broker


import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_show_garph.*
import mp.app.calonex.R

class BrokarDashboardGarphActivity : AppCompatActivity() {
    var viewPager: ViewPager? = null
    var tabLayout: TabLayout? = null
    var header1 :TextView? = null
    var showTopTenTextView :TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_garph)
        viewPager = findViewById<View>(R.id.view_pager) as ViewPager
        tabLayout = findViewById<View>(R.id.slidingTabs) as TabLayout
        header1 = findViewById<View>(R.id.header1) as TextView
        showTopTenTextView = findViewById<View>(R.id.show_top_ten) as TextView

        setData()
    }

    private fun setData() {
        sub_header_layout.visibility = View.VISIBLE
        val  adapter = BrokarDashboradChartAdapter(this@BrokarDashboardGarphActivity,supportFragmentManager,header1,showTopTenTextView)
        viewPager!!.offscreenPageLimit = 4
        viewPager!!.adapter = adapter
        tabLayout!!.setupWithViewPager(viewPager)
    }
}