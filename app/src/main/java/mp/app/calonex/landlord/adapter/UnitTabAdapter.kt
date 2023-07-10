package mp.app.calonex.landlord.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import mp.app.calonex.landlord.fragment.UnitDetailsDynamicFragment

class UnitTabAdapter(fm: FragmentManager, NumOfTabs: Int) : FragmentStatePagerAdapter(fm, NumOfTabs) {

    var mNumOfTabs: Int = NumOfTabs
    var isLast: Boolean = false

    override fun getItem(position: Int): Fragment {
        if((position + 1) == mNumOfTabs){
            isLast = true
        }

        return UnitDetailsDynamicFragment.addFrag(position, isLast)
    }

    override fun getCount(): Int {
        return mNumOfTabs
    }

}