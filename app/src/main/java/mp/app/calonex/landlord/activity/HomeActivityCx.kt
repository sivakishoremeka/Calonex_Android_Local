package mp.app.calonex.landlord.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.btm_nav_ld
import kotlinx.android.synthetic.main.activity_home_agent.*
import mp.app.calonex.R
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.landlord.dashboard.DashboardLdFragment
import mp.app.calonex.landlord.fragment.CxMessagingLdFragment
import mp.app.calonex.landlord.fragment.PaymentHistoryLdFragment
import mp.app.calonex.landlord.fragment.ProfileLdFragment
import mp.app.calonex.landlord.fragment.PropertyLdFragment
import mp.app.calonex.landlord.model.User
import mp.app.calonex.service.FeaturesService


class HomeActivityCx : CxBaseActivity() {

    val BackgroundIntentServiceAction = "android.intent.action.CUSTOME_ACTION_1"

    private var fragmentDashboard: Fragment = DashboardLdFragment()
    private var fragmentProperty: Fragment = PropertyLdFragment()
    private var fragmentProfile: Fragment = ProfileLdFragment()
    private var fragmentHistory: Fragment = PaymentHistoryLdFragment()
    private var fragmentMsg: Fragment = CxMessagingLdFragment()
    private val fm: FragmentManager = supportFragmentManager
    var active = fragmentDashboard
    private var isMessage: Boolean? = false
    private lateinit var userList: List<User>
    private var featureRefreshed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        Kotpref.isLogin = true
        Kotpref.linkBrokerLicenceNo = ""

        Log.d("TAG", "onCreate: bank account not verified " + !Kotpref.bankAccountVerified)

        btm_nav_ld!!.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        fm.beginTransaction().add(R.id.container, fragmentDashboard, "1").commit()
        /* fm.beginTransaction().add(R.id.container, fragmentProperty, "2").hide(fragmentProperty)
             .commit()
         fm.beginTransaction().add(R.id.container, fragmentProfile, "3").hide(fragmentProfile)
             .commit()
         fm.beginTransaction().add(R.id.container, fragmentHistory, "4").hide(fragmentHistory)
             .commit()
         fm.beginTransaction().add(R.id.container, fragmentMsg, "5").hide(fragmentMsg).commit()
 */
        /*isMessage=intent.getBooleanExtra(getString(R.string.intent_pn_msg),false)
        if(isMessage!!){
            fm.beginTransaction().hide(active).show(fragmentMsg).commit()
            active = fragmentMsg
            btm_nav_ld.selectedItemId = R.id.navigation_message
        }*/
        featureRefreshed = intent.getBooleanExtra("isFeatureRefreshed", false)

        if (!featureRefreshed) {
            val intent = Intent(this, FeaturesService::class.java).apply {
                intent.setAction(BackgroundIntentServiceAction)
            }
            startService(intent)
        }

    }

    fun navigateTo(menuItem: String) {
        when (menuItem) {
            "dashboard" -> {
                btm_nav_ld.selectedItemId = R.id.navigation_dashboard
            }
            "property" -> {
                btm_nav_ld.selectedItemId = R.id.navigation_property
            }
            "payment" -> {
                btm_nav_ld.selectedItemId = R.id.navigation_payment
            }
            "message" -> {
                btm_nav_ld.selectedItemId = R.id.navigation_message
            }
            "profile" -> {
                btm_nav_ld.selectedItemId = R.id.navigation_profile
            }
            else -> {
                btm_nav_ld.selectedItemId = R.id.navigation_dashboard
            }
        }

    }

    /*fun addBadgeNew(count: String) {
        val badge: BadgeDrawable = btm_nav_ld.getOrCreateBadge(
            R.id.navigation_message
        )
        if (!count.isNullOrEmpty() && !count.equals("0")) {
            badge.number = count.toInt()
            badge.badgeTextColor = Color.WHITE
            badge.backgroundColor = Color.RED
            badge.isVisible = true
        }
        else
        {
            badge.backgroundColor = Color.WHITE

        }
    }*/

    override fun onBackPressed() {
        if (active != fragmentDashboard) {
            fm.beginTransaction().hide(active).show(fragmentDashboard).commit()
            active = fragmentDashboard
            btm_nav_ld.selectedItemId = R.id.navigation_dashboard

        } else {
            val startMain = Intent(Intent.ACTION_MAIN)
            startMain.addCategory(Intent.CATEGORY_HOME)
            startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(startMain)
            //super.onBackPressed()
        }
    }

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener {
            when (it.itemId) {

                R.id.navigation_dashboard -> {
                    fragmentDashboard = DashboardLdFragment()
                    fm.beginTransaction().replace(R.id.container, fragmentDashboard).commit()
                    active = fragmentDashboard
                    return@OnNavigationItemSelectedListener true
                }

                R.id.navigation_property -> {
                    fragmentProperty = PropertyLdFragment()
                    fm.beginTransaction().replace(R.id.container, fragmentProperty).commit()
                    active = fragmentProperty
                    return@OnNavigationItemSelectedListener true
                }

                R.id.navigation_payment -> {
                    fragmentHistory = PaymentHistoryLdFragment()
                    fm.beginTransaction().replace(R.id.container, fragmentHistory).commit()
                    active = fragmentHistory
                    return@OnNavigationItemSelectedListener true
                }

                R.id.navigation_message -> {
                    fragmentMsg = CxMessagingLdFragment()
                    fm.beginTransaction().replace(R.id.container, fragmentMsg).commit()
                    active = fragmentMsg
                    return@OnNavigationItemSelectedListener true
                }

                R.id.navigation_profile -> {
                    fragmentProfile = ProfileLdFragment()
                    fm.beginTransaction().replace(R.id.container, fragmentProfile).commit()
                    active = fragmentProfile
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}