package mp.app.calonex.agent.fragment


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mp.app.calonex.R
import mp.app.calonex.agent.activity.NotifyScreenAgent
import mp.app.calonex.agent.adapter.NotificationLinkRequestAdapterAgent
import mp.app.calonex.landlord.model.AppNotifications

class PropertyLinkRequestFragmentAgent : Fragment() {

    lateinit var appContext: Context
    private var recyclerView: RecyclerView? = null
    private var pbLink: ProgressBar? = null
    private var txtNotifyEmpty: TextView? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_property_link_request, container, false)
        recyclerView = rootView.findViewById<View>(R.id.rv_link_request) as RecyclerView
        pbLink = rootView.findViewById(R.id.pb_link)
        txtNotifyEmpty = rootView.findViewById(R.id.txt_notify_empty)


        setNotificationList(NotifyScreenAgent.linkRequestListt)


        /*if(Kotpref.loginRole.equals("broker"))
        {
            setNotificationList(HomeFragmentBroker.linkRequestListt)
        }*/ /*else {
            // test it
            setNotificationList(HomeFragmentAgent.linkRequestList)
        }*/

       //getNotifyList()
        //Log.d("PropertyLint", "onCreateView: agent property list"+linkRequestList.toString())
        return rootView
    }

    fun setNotificationList(listResponse: List<AppNotifications>) {
        if (listResponse.size > 0) {
            recyclerView!!.visibility = View.VISIBLE
            txtNotifyEmpty!!.visibility = View.GONE
            recyclerView?.layoutManager = LinearLayoutManager(appContext)

            val adapter = NotificationLinkRequestAdapterAgent(appContext
                ,
                activity,
                pbLink!!,
                txtNotifyEmpty!!,
                listResponse
            )
            recyclerView?.adapter = adapter
        } else {
            recyclerView!!.visibility = View.GONE
            txtNotifyEmpty!!.visibility = View.VISIBLE
        }
    }
    /*fun getNotifyList() {
        val credentials = NotificationCredential()
        linkRequestList.clear()


        credentials.userCatalog = Kotpref.userId
        val notifyListService: ApiInterface =
            ApiClient(requireActivity()).provideService(ApiInterface::class.java)
        val apiCall: Observable<AppNotificationResponse> =
            notifyListService.getAppNotifications(credentials) //Test API Key 3600248I

        RxAPICallHelper().call(apiCall, object : RxAPICallback<AppNotificationResponse> {
            override fun onSuccess(response: AppNotificationResponse) {
                Log.e("onSuccess", response.responseCode.toString())
                var appNotifications = ArrayList<AppNotifications>()
                DashboardLdFragment.linkRequestList.clear()
                if (!response.data?.isEmpty()!!) {
                    appNotifications = response.data!!


                    for (i in 0..appNotifications.size - 1) {
                        val gson = Gson()

                        if (appNotifications[i].activityType == "1") {
                            val objectList = gson.fromJson(
                                appNotifications[i].notificationData,
                                NotificationLinkRequestModel::class.java
                            )
                            //  mNotificationLinkRequest = objectList
                            linkRequestList.add(appNotifications[i])


                        }
                    }

                }

                setNotificationList(linkRequestList)


                // setData(DashboardLdFragment.linkRequestList)
                //  pb_link.visibility = View.GONE

                //  activity!!.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }

            override fun onFailed(throwable: Throwable) {
                // show error
                Log.e("onFailure", throwable.toString())
                // pb_link.visibility = View.GONE
                activity!!.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })

    }*/

}