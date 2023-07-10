package mp.app.calonex.broker

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import mp.app.calonex.R
import mp.app.calonex.landlord.activity.CxBaseActivity


class HomeActivityBroker2 : CxBaseActivity() {

    private val fragmentDashboard: Fragment = DashboardBrokerFragment()
    private val fm: FragmentManager = supportFragmentManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_broker)

        fm.beginTransaction().add(R.id.container, fragmentDashboard, "1").commit()
        fm.beginTransaction().show(fragmentDashboard).commit()
    }


}