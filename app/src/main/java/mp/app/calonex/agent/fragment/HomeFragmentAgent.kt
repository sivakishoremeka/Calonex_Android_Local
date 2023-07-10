package mp.app.calonex.agent.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_home_agent.badge_notification
import mp.app.calonex.R
import mp.app.calonex.agent.AgentChartAdapter
import mp.app.calonex.agent.activity.NotifyScreenAgent
import mp.app.calonex.common.apiCredentials.NotificationCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.Util

import mp.app.calonex.landlord.model.AppNotifications
import mp.app.calonex.landlord.model.NotificationLinkRequestModel
import mp.app.calonex.landlord.response.AppNotificationResponse

class HomeFragmentAgent : Fragment() {
    lateinit var appContext: Context
    var viewPager: ViewPager? = null
    var tabLayout: TabLayout? = null

    private var imgNotify: ImageView?=null
    private var pbNotification: ProgressBar?=null
    private var layoutNotify: RelativeLayout?=null
    var appNotifications = ArrayList<AppNotifications>()
    var mNotificationLinkRequest = NotificationLinkRequestModel()
    var messageList = ArrayList<AppNotifications>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_home_agent, container, false)
        initComponents(rootView)
        return rootView
    }

    private fun initComponents(viewRoot: View) {
        viewPager = viewRoot.findViewById<View>(R.id.view_pager) as ViewPager
        tabLayout = viewRoot.findViewById<View>(R.id.slidingTabs) as TabLayout

        imgNotify = viewRoot.findViewById(R.id.img_notify)
        pbNotification = viewRoot.findViewById(R.id.progressBar_notification)
        layoutNotify = viewRoot.findViewById(R.id.layout_notify)

        setData()
    }

    private fun setData() {
        val  adapter = AgentChartAdapter(appContext, requireFragmentManager())
        viewPager!!.offscreenPageLimit = 4
        viewPager!!.adapter = adapter
        tabLayout!!.setupWithViewPager(viewPager)

        imgNotify!!.setOnClickListener{
            Util.navigationNext(requireActivity(), NotifyScreenAgent::class.java)
        }
    }

    override fun onResume() {
        super.onResume()
        layoutNotify!!.visibility = View.VISIBLE
        linkRequestList.clear()
        alertsList.clear()
        messageList.clear()
        getNotificationList()
    }

    private fun getNotificationList() {
        pbNotification!!.visibility = View.VISIBLE
        requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        val credentials = NotificationCredential()

        credentials.userCatalog = Kotpref.userId
        val notifyListService: ApiInterface =
            ApiClient(appContext).provideService(ApiInterface::class.java)
        val apiCall: Observable<AppNotificationResponse> =
            notifyListService.getAppNotifications(credentials) //Test API Key 3600248I

        RxAPICallHelper().call(apiCall, object : RxAPICallback<AppNotificationResponse> {
            override fun onSuccess(response: AppNotificationResponse) {
                Log.d("onSuccess Home agent", response.data.toString())

                if (!response.data?.isEmpty()!!) {
                    appNotifications = response.data!!
                    linkRequestList.clear()
                    alertsList.clear()
                    messageList.clear()
                    for (i in 0..appNotifications.size-1) {
                        val gson = Gson()

                        if(appNotifications[i].activityType == "1"){
                            val objectList = gson.fromJson(
                                appNotifications[i].notificationData,
                                NotificationLinkRequestModel::class.java
                            )
                            mNotificationLinkRequest = objectList
                            linkRequestList.add(appNotifications[i])
                        } else if(appNotifications[i].activityType == "2") {
                            alertsList.add(appNotifications[i])
                        } else if (appNotifications[i].activityType == "3"){
                            messageList.add(appNotifications[i])
                        }
                    }
                }

                Log.d("HomeFragmentAgent", "onSuccess: link request size "+linkRequestList.size);

                Kotpref.notifyCount = (linkRequestList.size+alertsList.size).toString()
                badge_notification!!.text= Kotpref.notifyCount
                //(activity as HomeActivityAgent?)!!.addBadgeNew(messageList.size.toString())
                pbNotification!!.visibility = View.GONE
                activity!!.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                if( Integer.parseInt(Kotpref.notifyCount)<1 )
                {
                    badge_notification!!.visibility = View.GONE

                }
                else
                {
                    badge_notification!!.visibility = View.VISIBLE

                }            }

            override fun onFailed(throwable: Throwable) {
                // show error
                Log.e("onFailure", throwable.toString())
                pbNotification!!.visibility = View.GONE
                activity!!.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                badge_notification!!.text=(linkRequestList.size+alertsList.size).toString()
                if( Integer.parseInt(Kotpref.notifyCount)<1 )
                {
                    badge_notification!!.visibility = View.GONE

                }
                else
                {
                    badge_notification!!.visibility = View.VISIBLE

                }
           }
        })
    }

    companion object {
        var linkRequestList = ArrayList<AppNotifications>()
        var alertsList = ArrayList<AppNotifications>()
        var messageList = ArrayList<AppNotifications>()
    }
}