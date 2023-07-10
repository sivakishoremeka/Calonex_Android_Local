package mp.app.calonex.landlord.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import io.reactivex.Observable
import kotlinx.android.synthetic.main.item_link_request.view.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.LinkPropertyActionCredential
import mp.app.calonex.common.apiCredentials.NotificationCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.LinkRequestActionActivity
import mp.app.calonex.landlord.dashboard.DashboardLdFragment.Companion.linkRequestList
import mp.app.calonex.landlord.model.AppNotifications
import mp.app.calonex.landlord.model.NotificationLinkRequestModel
import mp.app.calonex.landlord.response.AppNotificationResponse
import mp.app.calonex.landlord.response.LinkRequestActionResponse

class NotificationLinkRequestAgentAdapter(
    var context: Context, var activity: FragmentActivity?,
    var pb_link: ProgressBar, var txtEmpty: TextView, val notifyList: List<AppNotifications>
) :
    RecyclerView.Adapter<NotificationLinkRequestAgentAdapter.ViewHolder>() {


    private var mContext: Context = context
    lateinit var listNotify: List<AppNotifications>
    var mNotificationLinkRequest = NotificationLinkRequestModel()
    var notificationId: String = ""
    var linkRequestId: String = ""

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val gson = Gson()

        var objectList = gson.fromJson(
            notifyList[position].notificationData,
            NotificationLinkRequestModel::class.java
        )

        mNotificationLinkRequest = objectList

        holder.address.text =
            mNotificationLinkRequest.location + ", " + mNotificationLinkRequest.city + ", " + mNotificationLinkRequest.state + ", " + mNotificationLinkRequest.zipCode
        holder.txtRequestedByName.text = mNotificationLinkRequest.agentName
        holder.txtRequestedById.text = mNotificationLinkRequest.agentLicenseNo
        holder.txtRequestedDate.text =
            Util.getDateTime(mNotificationLinkRequest.createdOn.toString())

        holder.button_accept.setOnClickListener {
            val objectList = gson.fromJson(
                notifyList[holder.adapterPosition].notificationData,
                NotificationLinkRequestModel::class.java)

            val intent = Intent(context, LinkRequestActionActivity::class.java)
            intent.putExtra(context.getString(R.string.notification_id), notifyList[holder.adapterPosition].notificationId.toString())
            intent.putExtra(context.getString(R.string.link_request_property), objectList)
            mContext.startActivity(intent)
            Util.navigationNext(activity!!)
        }

        holder.button_reject.setOnClickListener {
            val objectList = gson.fromJson(
                notifyList[holder.adapterPosition].notificationData,
                NotificationLinkRequestModel::class.java
            )
            notificationId = notifyList[holder.adapterPosition].notificationId.toString()
            linkRequestId = objectList.propertyLinkRequestId.toString()
            actionProperty();
        }
    }

    fun setData(listNotify: List<AppNotifications>) {
        this.listNotify = listNotify
        if (listNotify.size > 0) {
            txtEmpty.visibility = View.GONE
        } else {
            txtEmpty.visibility = View.VISIBLE
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return notifyList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val address = itemView.link_property_name!!
        val txtRequestedByName = itemView.link_property_requested_by_name
        val txtRequestedById = itemView.link_property_requested_by_id
        val txtRequestedDate = itemView.link_property_req_date
        val button_accept = itemView.btn_accept
        val button_reject = itemView.btn_reject
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(context).inflate(R.layout.item_link_request, parent, false)
        return ViewHolder(v)
    }

    fun actionProperty() {
        activity!!.getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        pb_link!!.visibility = View.VISIBLE
        var credential = LinkPropertyActionCredential()
        credential.notificationId = notificationId
        credential.propertyLinkRequestId = linkRequestId
        credential.action = "REJECTED"

        val propertyLinkAction: ApiInterface =
            ApiClient(context).provideService(ApiInterface::class.java)
        val apiCall: Observable<LinkRequestActionResponse> =
            propertyLinkAction.propertyAction(credential) //Test API Key

        RxAPICallHelper().call(apiCall, object : RxAPICallback<LinkRequestActionResponse> {
            override fun onSuccess(propertyDetail: LinkRequestActionResponse) {


                getNotifyList()



            }

            override fun onFailed(t: Throwable) {
                // show error
                activity!!.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                pb_link!!.visibility = View.GONE
                Log.e("onFailure", t.toString())

            }

        })


    }

     fun getNotifyList() {
        val credentials = NotificationCredential()

        credentials.userCatalog = Kotpref.userId
        val notifyListService: ApiInterface =
            ApiClient(context).provideService(ApiInterface::class.java)
        val apiCall: Observable<AppNotificationResponse> =
            notifyListService.getAppNotifications(credentials) //Test API Key 3600248I

        RxAPICallHelper().call(apiCall, object : RxAPICallback<AppNotificationResponse> {
            override fun onSuccess(response: AppNotificationResponse) {
                Log.e("onSuccess", response.responseCode.toString())
                var appNotifications = ArrayList<AppNotifications>()
                linkRequestList.clear()
                if (!response.data?.isEmpty()!!) {
                    appNotifications = response.data!!


                    for (i in 0..appNotifications.size - 1) {
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
                setData(linkRequestList)
                pb_link.visibility = View.GONE
                activity!!.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
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

