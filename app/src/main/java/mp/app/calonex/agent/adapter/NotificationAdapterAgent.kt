package mp.app.calonex.agent.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import mp.app.calonex.agent.fragment.AlertsFragmentAgent
import mp.app.calonex.agent.fragment.PropertyLandlordLinkRequestFragmentAgent
import mp.app.calonex.agent.fragment.PropertyLinkRequestFragmentAgent
import mp.app.calonex.common.utility.Kotpref

class NotificationAdapterAgent(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    // this is for fragment tabs
    override fun getItem(position: Int): Fragment {

        when (position) {
            0 -> {
                return PropertyLandlordLinkRequestFragmentAgent()
            }
            1 -> {
                return AlertsFragmentAgent()
            }
            else -> {
                return PropertyLinkRequestFragmentAgent()
            }
        }
    }

    // this counts total number of tabs
    override fun getCount(): Int {
        if (Kotpref.userRole.contains("Broker", true)) {
            return 3
        } else {
            return 2
        }
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "Landlord Request"
            1 -> "Alerts"
            else -> {
                return "Agent Request"
            }
        }
    }
}