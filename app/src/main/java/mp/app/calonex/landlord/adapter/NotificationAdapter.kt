package mp.app.calonex.landlord.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.landlord.fragment.AlertsFragment
import mp.app.calonex.landlord.fragment.PropertyLinkRequestFragment

class NotificationAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    // this is for fragment tabs
    override fun getItem(position: Int): Fragment {
        var myPosition: Int = position
        if (Kotpref.userRole.contains("CX-Tenant", true)) {
            myPosition = 1
        }

        when (myPosition) {
            0 -> {
                return PropertyLinkRequestFragment()
            }
            else -> {
                return AlertsFragment()
            }
        }
    }

    // this counts total number of tabs
    override fun getCount(): Int {
        if (Kotpref.userRole.contains("CX-Tenant", true)) {
            return 1
        } else {
            return 2
        }
    }

    override fun getPageTitle(position: Int): CharSequence {
        var myPosition: Int = position
        if (Kotpref.userRole.contains("CX-Tenant", true)) {
            myPosition = 1
        }

        return when (myPosition) {
            0 -> "Property Link Request"
            else -> {
                return "Alerts"
            }
        }
    }
}