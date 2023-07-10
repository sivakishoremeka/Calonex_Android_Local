package mp.app.calonex.agent.adapter


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
import mp.app.calonex.agent.activity.HomeActivityAgent
import mp.app.calonex.broker.activity.GetAgentCommisionActivityBroker
import mp.app.calonex.broker.activity.HomeActivityBroker
import mp.app.calonex.common.apiCredentials.LinkPropertyActionCredential
import mp.app.calonex.common.apiCredentials.NotificationCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.dashboard.DashboardLdFragment.Companion.linkRequestList
import mp.app.calonex.landlord.model.AppNotifications
import mp.app.calonex.landlord.model.NotificationLinkRequestModel
import mp.app.calonex.landlord.response.AppNotificationResponse
import mp.app.calonex.landlord.response.LinkRequestActionResponse
import mp.app.calonex.landlord.response.LinkRequestActionResponseNew

class NotificationLinkRequestAdapterAgent(
    var context: Context, var activity: FragmentActivity?,
    var pb_link: ProgressBar, var txtEmpty: TextView, val notifyList: List<AppNotifications>
) :
    RecyclerView.Adapter<NotificationLinkRequestAdapterAgent.ViewHolder>() {

    private var mContext: Context = context
    lateinit var listNotify: List<AppNotifications>
    var mNotificationLinkRequest = NotificationLinkRequestModel()
    var notificationId: String = ""
    var linkRequestId: String = ""

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val gson = Gson()

        var objectList = gson.fromJson(notifyList[position].notificationData, NotificationLinkRequestModel::class.java)

        mNotificationLinkRequest = objectList

        holder.address.text = mNotificationLinkRequest.agentName
        holder.txtRequestedByName.text = mNotificationLinkRequest.agentLicenseNo
        holder.txtRequestedById.text = mNotificationLinkRequest.location
        holder.agentEmailID.text = mNotificationLinkRequest.agentEmailId
        holder.agentLicenseNumber = mNotificationLinkRequest.agentLicenseNo

        //holder.txtRequestedDate.text = Util.getDateTime(mNotificationLinkRequest.createdOn.toString())

        holder.button_accept.setOnClickListener {
            val itemPosition : Int = holder.absoluteAdapterPosition

            val objectList = gson.fromJson(notifyList[itemPosition].notificationData, NotificationLinkRequestModel::class.java)
            val intent = Intent(context, GetAgentCommisionActivityBroker::class.java)
            intent.putExtra("NOTIFICATION ID", notifyList[itemPosition].notificationId.toString())
            intent.putExtra("AGENT NAME", holder.address.text)
            intent.putExtra("AGENT EMAIL", holder.agentEmailID.text)
            intent.putExtra("AGENT LICENSE", holder.agentLicenseNumber)
            mContext.startActivity(intent)
            Util.navigationNext(activity!!)
        }

        holder.button_reject.setOnClickListener {
            val objectList = gson.fromJson(notifyList[holder.adapterPosition].notificationData, NotificationLinkRequestModel::class.java)
            notificationId = notifyList[holder.adapterPosition].notificationId.toString()
            linkRequestId = objectList.agentLicenseNo.toString()
            actionPropertyReject()
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
        val agentEmailID = itemView.link_property_requested_by_name
        var agentLicenseNumber :String = ""
        val button_accept = itemView.btn_accept
        val button_reject = itemView.btn_reject


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(context).inflate(R.layout.item_link_request_agent, parent, false)
        return ViewHolder(v)
    }

    fun actionPropertyReject() {
        activity!!.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        pb_link!!.visibility = View.VISIBLE

        var credential = LinkPropertyActionCredential()
        credential.notificationId = notificationId
        credential.commissionPercentage = ""
        credential.agentLicenseNo = linkRequestId
        credential.action = "REJECTED"

        val propertyLinkAction: ApiInterface = ApiClient(context).provideService(ApiInterface::class.java)
        val apiCall: Observable<LinkRequestActionResponseNew> = propertyLinkAction.propertyActionAgentFromBroker(credential) //Test API Key
        RxAPICallHelper().call(apiCall, object : RxAPICallback<LinkRequestActionResponseNew> {
            override fun onSuccess(propertyDetail: LinkRequestActionResponseNew) {

                // todo this is correct method
                //getNotifyList()

                val intent = Intent(context, HomeActivityBroker::class.java)
                mContext.startActivity(intent)
                Util.navigationNext(activity!!)
            }

            override fun onFailed(t: Throwable) {
                // show error
                activity!!.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                pb_link!!.visibility = View.GONE
                Log.e("onFailure", t.toString())
            }
        })
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

                // todo this is correct method
                //getNotifyList()

                if(Kotpref.loginRole.equals("broker")) {
                    val intent = Intent(context, HomeActivityBroker::class.java)
                    mContext.startActivity(intent)
                    Util.navigationNext(activity!!)
                } else {
                    val intent = Intent(context, HomeActivityAgent::class.java)
                    mContext.startActivity(intent)
                    Util.navigationNext(activity!!)
                }
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
                    linkRequestList.clear()

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

/*import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import io.reactivex.Observable
import kotlinx.android.synthetic.main.item_link_request.view.*
import mp.app.calonex.R
import mp.app.calonex.agent.activity.NotifyScreenAgent
import mp.app.calonex.agent.fragment.PropertyLinkRequestFragmentAgent
import mp.app.calonex.common.apiCredentials.LinkPropertyActionAgentCredential
import mp.app.calonex.common.apiCredentials.LinkPropertyActionCredential
import mp.app.calonex.common.apiCredentials.LinkPropertyActionCredentialLandlord
import mp.app.calonex.common.apiCredentials.NotificationCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.NotifyScreen
import mp.app.calonex.landlord.dashboard.DashboardLdFragment.Companion.linkRequestList
import mp.app.calonex.landlord.model.AppNotifications
import mp.app.calonex.landlord.model.NotificationLinkRequestModel
import mp.app.calonex.landlord.response.AppNotificationResponse
import mp.app.calonex.landlord.response.LinkRequestActionAgentResponse
import mp.app.calonex.landlord.response.LinkRequestActionResponse

class NotificationLinkRequestAdapter(
    var context: Context, var activity: FragmentActivity?,
    var pb_link: ProgressBar, var txtEmpty: TextView, val notifyList: List<AppNotifications>
) :
    RecyclerView.Adapter<NotificationLinkRequestAdapter.ViewHolder>() {


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
        var notfcsn = gson.fromJson(notifyList[position].notificationData, AppNotifications::class.java)

        if (!mNotificationLinkRequest.agentRequest) {
            holder.address.text =
                mNotificationLinkRequest.propAddress + ", " + mNotificationLinkRequest.city + ", " + mNotificationLinkRequest.state + ", " + mNotificationLinkRequest.zipCode
               holder.txtRequestedByName.text = notfcsn.landlordName+" (LANDLORD)"
            holder.ll_agnet_broker_details.visibility = View.GONE
            holder.txtRequestedDate.text = Util.getDateTime(mNotificationLinkRequest.createdOn.toString())
        } else if (mNotificationLinkRequest.agentRequest) {

            holder.txtRequestedByName.text = mNotificationLinkRequest.agentName+" (AGENT)"
            holder.txtRequestedById.text = mNotificationLinkRequest.agentLicenseNo
            holder.address.text = mNotificationLinkRequest.location
            holder.txtRequestedDate.text = Util.getDateTime(mNotificationLinkRequest.createdOn.toString())
        }

        holder.button_reject.setOnClickListener {
            val objectList = gson.fromJson(
                notifyList[holder.adapterPosition].notificationData,
                NotificationLinkRequestModel::class.java
            )
            if (!objectList.agentRequest) {
                notificationId = notifyList[holder.adapterPosition].notificationId.toString()
                linkRequestId = objectList.propertyId.toString()
                actionProperty("REJECTED", "");
            }
            else   if (objectList.agentRequest) {
                notificationId = notifyList[holder.adapterPosition].notificationId.toString()
                actionPropertyAgent("REJECTED","", notfcsn.agentLicenseNo,notificationId);
            }
        }


        holder.button_accept.setOnClickListener {
            val objectList = gson.fromJson(
                notifyList[holder.adapterPosition].notificationData,
                NotificationLinkRequestModel::class.java
            )
            if (!objectList.agentRequest) {
                notificationId = notifyList[holder.adapterPosition].notificationId.toString()
                linkRequestId = objectList.propertyId.toString()
                actionProperty("APPROVED", notifyList[holder.adapterPosition].fromEmailId);
            } else if (objectList.agentRequest) {
                notificationId = notifyList[holder.adapterPosition].notificationId.toString()
                showDialog(notfcsn.agentName,notfcsn.agentEmailId,"APPROVED", notfcsn.agentLicenseNo,notificationId)
                Log.d("TAG", "onBindViewHolder: agent details "+ notfcsn.agentLicenseNo)
            }
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
        var ll_agnet_broker_details = itemView.ll_agnet_broker_details
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(context).inflate(R.layout.item_link_request, parent, false)
        return ViewHolder(v)
    }

    fun actionProperty(action: String, email: String) {
        activity!!.getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        var nId:Long=0;
        pb_link!!.visibility = View.VISIBLE
        var credential = LinkPropertyActionCredentialLandlord()
        if(!notificationId.isNullOrEmpty())
        {
           nId=notificationId.toLong()
        }

        credential.notificationId = nId
        credential.propertyId = linkRequestId
        credential.action = action
        credential.onBoardedBy = email


        val propertyLinkAction: ApiInterface =
            ApiClient(context).provideService(ApiInterface::class.java)
        val apiCall: Observable<LinkRequestActionResponse> =
            propertyLinkAction.propertyAction2(credential) //Test API Key

        RxAPICallHelper().call(apiCall, object : RxAPICallback<LinkRequestActionResponse> {
            override fun onSuccess(propertyDetail: LinkRequestActionResponse) {
                Log.e("onSuccess","success accept"+ propertyDetail.responseCode.toString())

             //   getNotifyList()

                //var  intent : Intent=Intent(context,NotifyScreenAgent::class.java)
                var  intent : Intent=Intent(context, NotifyScreen::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
            }

            override fun onFailed(t: Throwable) {
                // show error
                activity!!.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                pb_link!!.visibility = View.GONE
                Log.e("onFailure", t.toString())
            }
        })
    }

    fun actionPropertyAgent(action: String, commission: String,agentLicenseNo:String,notiId:String) {
        activity!!.getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        var nId:Long=0;
        pb_link!!.visibility = View.VISIBLE
        var credential = LinkPropertyActionAgentCredential()

        if(!notiId.isNullOrEmpty()) {
            nId=notificationId.toLong()
        }

        credential.notificationId = nId
        credential.agentLicenseNo = agentLicenseNo
        credential.action = action
        credential.commissionPercentage = commission
        credential.userCatalogId=Kotpref.userId


        val propertyLinkAction: ApiInterface =
            ApiClient(context).provideService(ApiInterface::class.java)
        val apiCall: Observable<LinkRequestActionAgentResponse> =
            propertyLinkAction.propertyActionLinkAgent(credential) //Test API Key

        RxAPICallHelper().call(apiCall, object : RxAPICallback<LinkRequestActionAgentResponse> {
            override fun onSuccess(propertyDetail: LinkRequestActionAgentResponse) {
                Log.e("onSuccess", propertyDetail.responseCode.toString())


              var  intent : Intent=Intent(context,NotifyScreenAgent::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
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
        linkRequestList.clear()


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



    private fun showDialog(name:String,emaild:String,action: String,agentLicenseNo: String,notiId: String) {
        val dialog = Dialog(mContext)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.custom_dialog_commission)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val body = dialog.findViewById(R.id.txt_commission_description) as TextView
        val txtDone = dialog.findViewById(R.id.payment_info_action_done) as TextView
        var comm:String="0"
        body.text = "Agent $name ($emaild) please set a commission percentage for the agent to complete the onboarding process"
        val txt_comission_percnent = dialog.findViewById(R.id.txt_comission_percnent) as TextInputEditText

        txtDone.setOnClickListener {
            if(txt_comission_percnent.text.toString().trim().length>0)
            {
                comm=txt_comission_percnent.text.toString().trim()
            }
            actionPropertyAgent(action,comm,agentLicenseNo,notiId)
            dialog.dismiss()
        }


        dialog.show()
}
}*/

