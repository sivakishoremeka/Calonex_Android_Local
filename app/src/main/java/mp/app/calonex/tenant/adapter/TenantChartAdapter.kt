package mp.app.calonex.tenant.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import mp.app.calonex.tenant.fragment.TenandDashboardDetailsFragment
import mp.app.calonex.tenant.fragment.TenantDashboardChart2Fragment
import mp.app.calonex.tenant.fragment.TenantDashboardChartFragment
import mp.app.calonex.tenant.fragment.TenantDashboardUnitFragment


class TenantChartAdapter(showGarphActivity: Context, supportFragmentManager: FragmentManager) :
    FragmentPagerAdapter(supportFragmentManager) {

    // This determines the fragment for each tab
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                TenantDashboardChartFragment.newInstance()
            }
            1 -> {
                TenantDashboardChart2Fragment.newInstance()
            }
            2 -> {
                TenandDashboardDetailsFragment.newInstance()
            }


            3 -> {
                TenantDashboardUnitFragment.newInstance()
            }


            else -> {
                TenantDashboardChartFragment.newInstance()
            }
        }
    }

    // This determines the number of tabs
    override fun getCount(): Int {
        return 4
    }

    // This determines the title for each tab
    /*override fun getPageTitle(position: Int): CharSequence? {

        val businessInfo = "Revenue"

        val stakeHolderData = "Rent Payments"

        val documentData = "Occupancy"
        val transfer ="Rating"

        // Generate title based on item position
        return when (position) {
            0 -> businessInfo
            1 -> stakeHolderData
            2 -> documentData
            3 -> transfer
            else -> null
        }
    }*/
}