package mp.app.calonex.tenant.activity

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_home_tenant.*
import mp.app.calonex.R
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.landlord.activity.CxBaseActivity
import mp.app.calonex.landlord.fragment.CxMessagingLdFragment
import mp.app.calonex.landlord.fragment.ProfileLdFragment
import mp.app.calonex.tenant.fragment.*


class HomeActivityTenant : CxBaseActivity() {

    /*
        add marketplace filter
        add marketplace details
        Add dashboard
        add marketplace payment
        add tenant "pay now" with card option
     */

    private val fragmentHome: Fragment = HomeFragment()
    private val fragmentCXReport: Fragment = CXReportTenantFragment()
    private val fragmentHistory: Fragment = TenantReceiptsFragment()
    private val fragmentPayment: Fragment = CxMessagingLdFragment()
    private val fragmentProfile: Fragment = ProfileLdFragment()

    private val fm: FragmentManager = supportFragmentManager
    var active = fragmentHome

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_tenant)

        Kotpref.isLogin = true

        btm_nav_ld!!.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        fm.beginTransaction().add(R.id.container, fragmentHome, "1").commit()
        fm.beginTransaction().add(R.id.container, fragmentCXReport, "4").hide(fragmentCXReport)
            .commit()
        fm.beginTransaction().add(R.id.container, fragmentProfile, "2").hide(fragmentProfile)
            .commit()
        fm.beginTransaction().add(R.id.container, fragmentPayment, "3").hide(fragmentPayment)
            .commit()
        fm.beginTransaction().add(R.id.container, fragmentHistory, "5").hide(fragmentHistory)
            .commit()

    }


    override fun onBackPressed() {
        if (active != fragmentHome) {
            fm.beginTransaction().hide(active).show(fragmentHome).commit()
            active = fragmentHome
            btm_nav_ld.selectedItemId = R.id.navigation_dashboard

        } else {
            //super.onBackPressed()
            val startMain = Intent(Intent.ACTION_MAIN)
            startMain.addCategory(Intent.CATEGORY_HOME)
            startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(startMain)
        }
    }

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener {
            when (it.itemId) {

                R.id.navigation_dashboard -> {
                    fm.beginTransaction().hide(active).show(fragmentHome).commit()
                    active = fragmentHome
                    return@OnNavigationItemSelectedListener true
                }

                R.id.navigation_cx_report -> {
                    fm.beginTransaction().hide(active).show(fragmentCXReport).commit()
                    active = fragmentCXReport
                    return@OnNavigationItemSelectedListener true
                }

                R.id.navigation_payment -> {
                    fm.beginTransaction().hide(active).show(fragmentHistory).commit()
                    active = fragmentHistory
                    return@OnNavigationItemSelectedListener true
                }

                R.id.navigation_message -> {
                    fm.beginTransaction().hide(active).show(fragmentPayment).commit()
                    active = fragmentPayment
                    return@OnNavigationItemSelectedListener true
                }

                R.id.navigation_profile -> {
                    fm.beginTransaction().hide(active).show(fragmentProfile).commit()
                    active = fragmentProfile
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }


}