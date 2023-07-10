package mp.app.calonex.landlord.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import mp.app.calonex.landlord.fragment.PropertyDescriptionFragment
import mp.app.calonex.landlord.fragment.PropertySummaryFragment
import mp.app.calonex.landlord.fragment.UnitDescriptionFragment

class OnBoardingLdAdapter (fm: FragmentManager) : FragmentPagerAdapter(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {


    override fun getItem(position: Int): Fragment {

        when (position) {
            0 -> return PropertySummaryFragment()
            1 -> return PropertyDescriptionFragment()
            else -> {
                return UnitDescriptionFragment()
            }

        }


    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "Lease Specification"
            1 -> "Tenants"
            else -> { "Rent Distribution"


            }
        }
    }
}