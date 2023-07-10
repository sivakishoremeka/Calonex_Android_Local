package mp.app.calonex.broker


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import mp.app.calonex.R

class DashboardBrokerFragment : Fragment() {


    lateinit var appContext: Context
    var viewPager: ViewPager? = null
    var tabLayout: TabLayout? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.activity_show_garph, container, false)
        viewPager = rootView.findViewById<View>(R.id.view_pager) as ViewPager
        tabLayout = rootView.findViewById<View>(R.id.slidingTabs) as TabLayout

        setViewPager()

        return rootView
    }

    private fun setViewPager() {
        val adapter = BrokerChartAdapter(appContext, fragmentManager!!)
        viewPager!!.offscreenPageLimit = 4
        viewPager!!.adapter = adapter
        tabLayout!!.setupWithViewPager(viewPager)
    }


}