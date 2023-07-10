package mp.app.calonex.landlord.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import mp.app.calonex.landlord.adapter.NotificationLinkRequestAdapter
import mp.app.calonex.landlord.model.AppNotifications
import mp.app.calonex.landlord.model.NotificationLinkRequestModel
import mp.app.calonex.landlord.response.AppNotificationResponse

class PropertyLinkRequestFragment : Fragment() {

    lateinit var appContext: Context
    private var recyclerView: RecyclerView? = null
    private var pbLink: ProgressBar? = null
    private var txtNotifyEmpty: TextView? = null
    var mNotificationLinkRequest = NotificationLinkRequestModel()
    private var allowRefresh = false

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

        return rootView
    }

    override fun onResume() {
        super.onResume()
        getNotifyList()
    }

    fun setNotificationList(listResponse: List<AppNotifications>) {
        if (listResponse.isNotEmpty()) {
            recyclerView!!.visibility = View.VISIBLE
            txtNotifyEmpty!!.visibility = View.GONE
            recyclerView?.layoutManager = LinearLayoutManager(appContext)

            val adapter = NotificationLinkRequestAdapter(
                appContext,
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

    var linkRequestList = ArrayList<AppNotifications>()
    fun getNotifyList() {
        val credentials = NotificationCredential()
        linkRequestList.clear()


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
                    for (i in 0 until appNotifications.size) {
                        val gson = Gson()

                        if (appNotifications[i].activityType == "1") {
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
                // show error
                Log.e("onFailure", throwable.toString())
                pb_link.visibility = View.GONE
                activity!!.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })

    }


}