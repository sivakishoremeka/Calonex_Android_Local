package mp.app.calonex.agent.activity

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_home_agent.*
import mp.app.calonex.R
import mp.app.calonex.agent.fragment.*
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.landlord.activity.CxBaseActivity
import mp.app.calonex.landlord.model.User
import mp.app.calonex.service.FeaturesService


class HomeActivityAgent : CxBaseActivity() {

    val BackgroundIntentServiceAction = "android.intent.action.CUSTOME_ACTION_1"

    private val fragmentHome: Fragment = AgentDashBoardFragment() // done-dashboard
    private val fragmentProfile: Fragment = ProfileFragmentAgent() // done-profile
    private val fragmentMsg: Fragment = CxMessagingFragmentAgent() // done-message
    private val fragmentProperty: Fragment = PropertyFragmentAgent() // done-properties
    private val fragmentHistory: Fragment = PaymentHistoryFragmentAgent() // done-payments

    // book keeping

    // PropertyDetailScreenAgent [need to add more things]

    private val fm: FragmentManager = supportFragmentManager
    var active = fragmentHome

    private var isMessage: Boolean? = false
    private lateinit var userList: List<User>
    private var featureRefreshed: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_agent)

        Kotpref.isLogin = true

        btm_nav_ld!!.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        fm.beginTransaction().add(R.id.container, fragmentHome, "1").commit()
        fm.beginTransaction().add(R.id.container, fragmentProfile, "2").hide(fragmentProfile)
            .commit()
        fm.beginTransaction().add(R.id.container, fragmentProperty, "3").hide(fragmentProperty)
            .commit()
        fm.beginTransaction().add(R.id.container, fragmentMsg, "4").hide(fragmentMsg).commit()
        fm.beginTransaction().add(R.id.container, fragmentHistory, "5").hide(fragmentHistory)
            .commit()

       /* isMessage = intent.getBooleanExtra(getString(R.string.intent_pn_msg), false)
        if (isMessage!!) {
            fm.beginTransaction().hide(active).show(fragmentMsg).commit()
            active = fragmentMsg
            btm_nav_ld.selectedItemId = R.id.navigation_message
        }*/

        featureRefreshed = intent.getBooleanExtra("isFeatureRefreshed", false)
        if (!featureRefreshed) {
            val intent = Intent(this, FeaturesService::class.java).apply {
                intent.action = BackgroundIntentServiceAction
            }
            startService(intent)
        }
    }

    /*fun addBadgeNew(count: String) {
        val badge: BadgeDrawable = btm_nav_ld.getOrCreateBadge(
            R.id.navigation_message
        )
        badge.number = count.toInt()
        badge.badgeTextColor = Color.WHITE
        badge.backgroundColor = Color.RED
        badge.isVisible = true
    }*/

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

                R.id.navigation_property -> {
                    fm.beginTransaction().hide(active).show(fragmentProperty).commit()
                    active = fragmentProperty
                    return@OnNavigationItemSelectedListener true
                }

                R.id.navigation_payment -> {
                    fm.beginTransaction().hide(active).show(fragmentHistory).commit()
                    active = fragmentHistory
                    return@OnNavigationItemSelectedListener true
                }

                R.id.navigation_message -> {
                    fm.beginTransaction().hide(active).show(fragmentMsg).commit()
                    active = fragmentMsg
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
}

/*
LeaseRequestDetailScreenAgent [clean up code and check the api calls]
PropertyLinkRequestFragmentAgent [start work on it]
PropertyDetailScreenAgent [add the TODO part for edit property]
OnboardPropertyActivityAgent [add the link part]
 */

/*
SelectSubcriptionPlanNew
LoginCredentialScreenNew
UserContactDetailScreenNew
UserPropertyDetailScreenNew
UserDocumentScreenNew
UserAccountScreenNew
 */

/*
   Log.e("DATA1", Gson().toJson(subscriptionPayload))
           Log.e("DATA2", Gson().toJson(regPropertyUnit))
    */

