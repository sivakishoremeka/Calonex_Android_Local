package mp.app.calonex.landlord.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import mp.app.calonex.tenant.fragment.PaymentHistoryFragment

class ReceiptsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    // this is for fragment tabs
    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                return PaymentHistoryFragment()
            }

            else -> {
                return PaymentHistoryFragment()
            }
        }
    }

    // this counts total number of tabs
    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "History"
            else -> {
                return "Receipts"
            }
        }
    }
}