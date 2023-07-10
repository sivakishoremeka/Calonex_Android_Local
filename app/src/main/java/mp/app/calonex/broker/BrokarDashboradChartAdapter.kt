package mp.app.calonex.broker

import android.content.Context
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import mp.app.calonex.broker.fragment.BrokerDashboardChartFragment


class BrokarDashboradChartAdapter(
    showGarphActivity: Context,
    supportFragmentManager: FragmentManager,
    val header1: TextView?,
    val showTopTenTextView: TextView?
) : FragmentPagerAdapter(supportFragmentManager) {

    // This determines the fragment for each tab
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                AllPropertiesChartFragment.newInstance(header1,showTopTenTextView)

            }
            1 -> {
                BookOfBusinessAgentChartFragment.newInstance(header1,showTopTenTextView)
            }


            2 -> {
              //  MyBookOfBussinesFragment.newInstance(showTopTenTextView,header1)
                BrokerDashboardChartFragment.newInstance();
            }


            3 -> {
                MyBookOfBussinesFragment.newInstance(showTopTenTextView,header1)
            }
            else -> {
                BrokarMarketPlaceChartFragment.newInstance(header1,showTopTenTextView)
            }
        }
    }

    // This determines the number of tabs
    override fun getCount(): Int {
        return 5
    }

    // This determines the title for each tab
    override fun getPageTitle(position: Int): CharSequence? {

        val businessInfo = ""

        val stakeHolderData = ""

        val documentData = ""
        val transfer = ""

        // Generate title based on item position
        return when (position) {
            0 -> businessInfo
            1 -> stakeHolderData
            2 -> documentData
            3 -> transfer
            else -> null
        }
    }
}