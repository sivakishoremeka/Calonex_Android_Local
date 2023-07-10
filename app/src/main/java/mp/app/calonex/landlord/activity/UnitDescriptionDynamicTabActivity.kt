package mp.app.calonex.landlord.activity

import android.graphics.Color
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import kotlinx.android.synthetic.main.activity_unit_description_dynamic_tab.*
import mp.app.calonex.R
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.AddNewPropertyFirstScreen.Companion.numOfUnits
import mp.app.calonex.landlord.activity.AddNewPropertyFirstScreen.Companion.result
import mp.app.calonex.landlord.adapter.UnitTabAdapter

class UnitDescriptionDynamicTabActivity : CxBaseActivity2() {

    private var adapter: UnitTabAdapter? = null
    private var numberOfUnit: Int = 0
    private var numberOfTab: Int = 0
    private var lastTabCliked = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unit_description_dynamic_tab)

        startHandler()

        try {
            pager = findViewById(R.id.viewPager)


            if (numOfUnits != null) {
                numberOfUnit = numOfUnits!!.toInt()
            }

            if (numberOfUnit == 0)
                numberOfUnit = 1

            pager.offscreenPageLimit = numberOfUnit


            for (k in 1..numberOfUnit) {
                tabLayout.addTab(tabLayout.newTab().setText("Unit#" + k))
                if (k == numberOfUnit) {
                    lastTabCliked = k
                    //this is the last iteration of for loop
                    //tabLayout.addTab(tabLayout.newTab().setText("ADD UNIT"))
                }
                tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#0071B6"))
                tabLayout.setTabTextColors(Color.parseColor("#727272"), Color.parseColor("#0071B6"))
            }
            //numberOfTab = tabLayout.tabCount - 1
            numberOfTab = tabLayout.tabCount
            adapter = UnitTabAdapter(
                supportFragmentManager,
                tabLayout.tabCount// tabLayout.tabCount - 1
            )
            pager.adapter = adapter
            pager.addOnPageChangeListener(TabLayoutOnPageChangeListener(tabLayout))

            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(p0: TabLayout.Tab?) {

                }

                override fun onTabUnselected(p0: TabLayout.Tab?) {

                }

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    if (tab != null) {
                        if (tab.text?.toString().equals("ADD UNIT")) {

                            return;
                            // Add a new tab
                            //viewPager!!.currentItem = tab.position
                        }
                        viewPager!!.currentItem = tab.position

                        /*if (viewPager!!.currentItem == tab.position - 1) {
                            val tabpostion = tab.position - 1
                            numberOfTab++
                            tabLayout.getTabAt(tabpostion + 1)!!.text = "Unit#" + numberOfTab
                            //tabLayout.addTab(tabLayout.newTab().setText("ADD UNIT"))

                            adapter = UnitTabAdapter(
                                supportFragmentManager,
                                numberOfTab
                            )
                            pager.adapter = adapter
                            // tabLayout.getTabAt(tabpostion)!!.select()
                            println("numberOfTab" + numberOfTab)
                            pager.offscreenPageLimit = numberOfTab
                            result.numberOfUnits = numberOfTab.toString()
                            // AddUnitFragment.newInstance()
                            println("onTabsleted" + tab.position)
                            // Toast.makeText(this, "last cliked", Toast.LENGTH_SHORT).show()

                        }*/
                    }
                }

            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

        iv_add_unit.setOnClickListener {
            //if (viewPager!!.currentItem == tabLayout.tabCount - 1) {
                numberOfTab++
                //tabLayout.getTabAt(tabpostion + 1)!!.text = "Unit#" + numberOfTab
                //tabLayout.addTab(tabLayout.newTab().setText("ADD UNIT"))
                tabLayout.addTab(tabLayout.newTab().setText("Unit#" + (numberOfTab)))
                adapter = UnitTabAdapter(
                    supportFragmentManager,
                    numberOfTab
                )
                pager.adapter = adapter
                // tabLayout.getTabAt(tabpostion)!!.select()
                println("numberOfTab" + numberOfTab)
                pager.offscreenPageLimit = numberOfTab
                result.numberOfUnits = numberOfTab.toString()
                // AddUnitFragment.newInstance()
                println("onTabsleted" + tabLayout.tabCount)
                // Toast.makeText(this, "last cliked", Toast.LENGTH_SHORT).show()

           // }
        }


        iv_back.setOnClickListener {
            onBackPressed()
        }

        /* tabLayout.getChildAt(1).setOnClickListener(View.OnClickListener {
             println("onTabsletedCliked")

             // custom code
                 //Toast.makeText(this, "last cliked", Toast.LENGTH_SHORT).show()
             })*/
    }

    companion object {
        lateinit var pager: ViewPager

        fun moveNext() {
            pager.currentItem = pager.currentItem + 1
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Util.navigationBack(this)
    }
}