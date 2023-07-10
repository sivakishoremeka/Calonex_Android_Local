package mp.app.calonex.agent.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import mp.app.calonex.R
import mp.app.calonex.agent.activity.NotifyScreenAgent.Companion.alertsListt
import mp.app.calonex.agent.adapter.NotificationAlertsAdapterAgent
import mp.app.calonex.common.apiCredentials.NotificationCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.landlord.model.AppNotifications
import mp.app.calonex.landlord.response.AppNotificationResponse

class AlertsFragmentAgent : Fragment() {


    private var recyclerView: RecyclerView? = null
    private var pbAlert: ProgressBar? = null
    val adapter: NotificationAlertsAdapterAgent? = null
    var alertsList = ArrayList<AppNotifications>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // appContext = context

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_alerts_agent, container, false)
        recyclerView = rootView.findViewById(R.id.rv_alerts)
        pbAlert = rootView.findViewById(R.id.pb_alert)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        val dividerItemDecoration =
            DividerItemDecoration(recyclerView!!.context, DividerItemDecoration.VERTICAL)
        dividerItemDecoration.setDrawable(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.item_list_divider
            )!!
        )
        alertsList = alertsListt
        setPropertyList(alertsList)

        /*if(Kotpref.loginRole.equals( "Broker",true))
        {
            setPropertyList(alertsListt)

        }
        else if(Kotpref.loginRole.equals("Agent",true))
        {
            setPropertyList(alertsList)
        }

        else if(Kotpref.loginRole.equals("Tenant",true))
        {
            setPropertyList(alertsListTenant)
        }*/


        return rootView
    }

    private fun setPropertyList(listResponse: List<AppNotifications>) {
        recyclerView?.layoutManager = LinearLayoutManager(context)


        val adapter = NotificationAlertsAdapterAgent(
            this@AlertsFragmentAgent,
            pbAlert!!,
            requireActivity(),
            listResponse
        )
        adapter.setData(listResponse)
        recyclerView?.adapter = adapter

        //refresh()
    }


    private fun refresh() {
        /*
            for(items in 0..listNotify.size)
            {
                if(listNotify[items].notificationId.toInt()==notificationId.toInt())
                {
                    listNotify.drop(items)
                    setData(listNotify)
                    break
                }
            }
    */
        getNotifyList()


    }


    public fun getNotifyList() {
        val credentials = NotificationCredential()

        credentials.userCatalog = Kotpref.userId
        val notifyListService: ApiInterface =
            ApiClient(requireActivity()).provideService(ApiInterface::class.java)
        val apiCall: Observable<AppNotificationResponse> =
            notifyListService.getAppNotifications(credentials) //Test API Key 3600248I

        RxAPICallHelper().call(apiCall, object : RxAPICallback<AppNotificationResponse> {
            override fun onSuccess(response: AppNotificationResponse) {
                Log.e("onSuccess agent", response.data.toString())
                var appNotifications = ArrayList<AppNotifications>()
                alertsList.clear()
                if (!response.data?.isEmpty()!!) {
                    appNotifications = response.data!!


                    for (i in 0..appNotifications.size - 1) {

                        if (appNotifications[i].activityType == "2") {

                            alertsList.add(appNotifications[i])
                        }
                    }

                }
                val adapter = NotificationAlertsAdapterAgent(
                    this@AlertsFragmentAgent,
                    pbAlert!!,
                    requireActivity(),
                    alertsList
                )
                adapter.setData(alertsList)
                recyclerView?.adapter = adapter

                pbAlert?.visibility = View.GONE
                adapter.notifyDataSetChanged()


            }

            override fun onFailed(throwable: Throwable) {
                // show error
                Log.e("onFailure", throwable.toString())
                //  pbAlert.visibility = View.GONE
                //activity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })

    }

}