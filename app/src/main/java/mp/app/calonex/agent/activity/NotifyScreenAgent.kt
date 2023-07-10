package mp.app.calonex.agent.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import io.reactivex.Observable
import mp.app.calonex.R
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.Util
import mp.app.calonex.agent.adapter.NotificationAdapterAgent
import mp.app.calonex.common.apiCredentials.NotificationCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.landlord.model.AppNotifications
import mp.app.calonex.landlord.model.NotificationLinkRequestModel
import mp.app.calonex.landlord.model.NotificationLinkRequestModelNew
import mp.app.calonex.landlord.response.AppNotificationResponse

class NotifyScreenAgent : AppCompatActivity() {

    var tabLayoutNotify: TabLayout? = null
    var vpNotify: ViewPager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_noitfy_screen_agent)
        tabLayoutNotify = findViewById(R.id.tabLayout_notify)
        vpNotify = findViewById(R.id.vp_notify)
        Kotpref.isNotifyRefresh = false

    }

    override fun onResume() {
        super.onResume()
        getNotificationList()
    }

    private fun setViewPager() {
        val fragmentAdapter = NotificationAdapterAgent(supportFragmentManager)
        vpNotify!!.adapter = fragmentAdapter
        tabLayoutNotify?.setupWithViewPager(vpNotify)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Util.navigationBack(this@NotifyScreenAgent)
    }

   companion object{
       var appNotifications = ArrayList<AppNotifications>()
       var mNotificationLinkRequest = NotificationLinkRequestModel()
       var mNotificationLinkRequestNew = NotificationLinkRequestModelNew()

       var linkRequestListt = ArrayList<AppNotifications>()
       var linkRequestListForLdReq = ArrayList<AppNotifications>()
       var alertsListt = ArrayList<AppNotifications>()
       var messageList = ArrayList<AppNotifications>()
   }

    private fun getNotificationList() {
        val credentials = NotificationCredential()

        credentials.userCatalog = Kotpref.userId
        val notifyListService: ApiInterface =
            ApiClient(this@NotifyScreenAgent).provideService(ApiInterface::class.java)
        val apiCall: Observable<AppNotificationResponse> =
            notifyListService.getAppNotifications(credentials) //Test API Key 3600248I

        RxAPICallHelper().call(apiCall, object : RxAPICallback<AppNotificationResponse> {
            override fun onSuccess(response: AppNotificationResponse) {
                try {
                    if (!response.data?.isEmpty()!!) {
                        appNotifications = response.data!!
                        linkRequestListt.clear()
                        linkRequestListForLdReq.clear()
                        alertsListt.clear()
                        messageList.clear()
                        for (i in 0..appNotifications.size - 1) {
                            val gson = Gson()

                            if (appNotifications[i].activityType == "1" && appNotifications[i].agentRequest == "true") {
                                val objectList = gson.fromJson(
                                    appNotifications[i].notificationData,
                                    NotificationLinkRequestModel::class.java
                                )
                                mNotificationLinkRequest = objectList
                                linkRequestListt.add(appNotifications[i])
                            } else if (appNotifications[i].activityType == "1" && appNotifications[i].agentRequest == "false") {
                                val objectList = gson.fromJson(
                                    appNotifications[i].notificationData,
                                    NotificationLinkRequestModelNew::class.java
                                )
                                mNotificationLinkRequestNew = objectList
                                linkRequestListForLdReq.add(appNotifications[i])
                            } else if (appNotifications[i].activityType == "2") {
                                alertsListt.add(appNotifications[i])
                            } else if (appNotifications[i].activityType == "3") {
                                messageList.add(appNotifications[i])
                            }
                        }
                    }

                    setViewPager()

                    Kotpref.notifyCount =
                        (linkRequestListt.size + alertsListt.size + linkRequestListForLdReq.size).toString()

                    } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailed(throwable: Throwable) {
                // show error
                Log.e("onFailure", throwable.toString())
            }
        })
    }



}
