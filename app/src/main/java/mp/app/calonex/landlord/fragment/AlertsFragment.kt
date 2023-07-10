package mp.app.calonex.landlord.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_property_link_request.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.NotificationCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.landlord.adapter.NotificationAlertsAdapter
import mp.app.calonex.landlord.dashboard.DashboardLdFragment
import mp.app.calonex.landlord.model.AppNotifications
import mp.app.calonex.landlord.model.NotificationLinkRequestModel
import mp.app.calonex.landlord.response.AppNotificationResponse

class AlertsFragment : Fragment() {

    lateinit var appContext: Context
    private var recyclerView: RecyclerView? = null
    private var pbAlert:ProgressBar?=null
    var mNotificationLinkRequest = NotificationLinkRequestModel()



    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_alerts, container, false)
        recyclerView = rootView.findViewById(R.id.rv_alerts)
        pbAlert=rootView.findViewById(R.id.pb_alert)
        recyclerView?.layoutManager = LinearLayoutManager(appContext)
        val dividerItemDecoration =
            DividerItemDecoration(recyclerView!!.context, DividerItemDecoration.VERTICAL)
        dividerItemDecoration.setDrawable(
            ContextCompat.getDrawable(
                appContext,
                R.drawable.item_list_divider
            )!!
        )

        /*if (Kotpref.userRole.contains("CX-Tenant", true)) {
            setNotificationList(alertsListTenant)
        } else {
            setNotificationList(alertsList)
        }*/


        return rootView
    }

    override fun onResume() {
        super.onResume()
        getNotifyList()
    }

    private fun setNotificationList(listResponse: List<AppNotifications>) {
        recyclerView?.layoutManager = LinearLayoutManager(appContext)
        val adapter = NotificationAlertsAdapter(appContext,pbAlert!!,activity, listResponse)
        recyclerView?.adapter = adapter
       // adapter.setData(listResponse as List<AppNotifications>)
    }

    var linkRequestList = ArrayList<AppNotifications>()
    fun getNotifyList() {

        var alert= SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
        alert.setContentText("Loading")
        alert.show()
        val credentials = NotificationCredential()
        DashboardLdFragment.linkRequestList.clear()
        credentials.userCatalog = Kotpref.userId
        val notifyListService: ApiInterface =
            ApiClient(appContext).provideService(ApiInterface::class.java)
        val apiCall: Observable<AppNotificationResponse> =
            notifyListService.getAppNotifications(credentials) //Test API Key 3600248I

        RxAPICallHelper().call(apiCall, object : RxAPICallback<AppNotificationResponse> {
            override fun onSuccess(response: AppNotificationResponse) {
                Log.e("onSuccess", response.responseCode.toString())
                var appNotifications = ArrayList<AppNotifications>()
                linkRequestList.clear()
                appNotifications.clear()
                if (!response.data?.isEmpty()!!) {
                    appNotifications = response.data!!
                    alert.dismiss()

                    for (i in 0 until appNotifications.size) {
                        val gson = Gson()

                        if (appNotifications[i].activityType == "2") {
                            val objectList = gson.fromJson(
                                appNotifications[i].notificationData,
                                NotificationLinkRequestModel::class.java
                            )
                            mNotificationLinkRequest = objectList
                            linkRequestList.add(appNotifications[i])
                        }
                    }

                }
                // setData(linkRequestList)
                //  pb_link.visibility = View.GONE
                //  activity!!.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                setNotificationList(linkRequestList)
            }

            override fun onFailed(throwable: Throwable) {
                alert.dismiss()
                // show error
                Log.e("onFailure", throwable.toString())
                pb_link.visibility = View.GONE
                activity!!.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })

    }

}