package mp.app.calonex.agent.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import mp.app.calonex.agent.fragment.PropertyDescriptionFragmentAgent
import mp.app.calonex.agent.fragment.UnitDescriptionFragmentAgent

class PropertyDetailAdapterAgent (fm: FragmentManager) : FragmentPagerAdapter(fm) {


    override fun getItem(position: Int): Fragment {

        when (position) {
           // 0 -> return PropertySummaryFragment()
            0 -> return PropertyDescriptionFragmentAgent()
            1 -> return UnitDescriptionFragmentAgent()
            else -> {
                //return PropertyExpensesFragmentAgent()
                return UnitDescriptionFragmentAgent()
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