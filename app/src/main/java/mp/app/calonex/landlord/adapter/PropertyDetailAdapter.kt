package mp.app.calonex.landlord.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import mp.app.calonex.landlord.fragment.PropertyDescriptionFragment
import mp.app.calonex.landlord.fragment.UnitDescriptionFragment

class PropertyDetailAdapter (fm: FragmentManager) : FragmentPagerAdapter(fm) {


    override fun getItem(position: Int): Fragment {

        when (position) {
           // 0 -> return PropertySummaryFragment()
            0 -> return PropertyDescriptionFragment()
            1 -> return UnitDescriptionFragment()
            else -> {
                return UnitDescriptionFragment()
            }

        }


    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            //0 -> "Summary"
            0 -> "Property Description"
            1 -> "Unit Description"
            else -> { "Property Expenses"


            }
        }
    }
}