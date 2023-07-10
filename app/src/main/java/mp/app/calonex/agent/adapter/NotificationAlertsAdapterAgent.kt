package mp.app.calonex.agent.adapter


import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.item_alerts_agent.view.*
import mp.app.calonex.R
import mp.app.calonex.agent.activity.LeaseRequestDetailScreenAgent
import mp.app.calonex.agent.fragment.AlertsFragmentAgent
import mp.app.calonex.common.apiCredentials.FindLeaseCredentials
import mp.app.calonex.common.apiCredentials.LinkPropertyActionCredential
import mp.app.calonex.common.apiCredentials.SignatureLeaseCredentials
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.landlord.model.*
import mp.app.calonex.landlord.response.FindLeaseResponse
import mp.app.calonex.landlord.response.SecurityFetchResponse
import mp.app.calonex.landlord.response.SignatureLeaseResponse

class NotificationAlertsAdapterAgent(
    var context: AlertsFragmentAgent,
    val pbAlert: ProgressBar,
    val activity: Context,
    val notifyList: List<AppNotifications>

) : RecyclerView.Adapter<NotificationAlertsAdapterAgent.ViewHolder>() {

    lateinit var listNotify: List<AppNotifications>
    var mNotificationAlertsModel = NotificationAlertsModel()
    var notificationId: String = ""

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {



        val gson = Gson()
        val objectList = gson.fromJson(
            notifyList[position].notificationData,
            NotificationAlertsModel::class.java
        )

        mNotificationAlertsModel = objectList

        holder.message.text = mNotificationAlertsModel.info

        /*TODO ganram*/
        if (holder.message.text.toString().contains("Lease") || holder.message.text.toString()
                .contains("lease")
        ) {
            //condition not required
            holder.txtViewLease.visibility = View.GONE
            holder.imgDelAlert.visibility = View.VISIBLE
        } else {
            holder.txtViewLease.visibility = View.GONE
            holder.imgDelAlert.visibility = View.VISIBLE
        }
        holder.txtViewLease.setOnClickListener {
            val objectList = gson.fromJson(
                notifyList[holder.adapterPosition].notificationData,
                NotificationAlertsModel::class.java
            )

            getLeaseList(objectList.leaseId)

        }

        holder.imgDelAlert.setOnClickListener {

            notificationId = notifyList[holder.adapterPosition].notificationId.toString()
            actionDelete()
        }

    }

    fun setData(listNotify: List<AppNotifications>) {
        this.listNotify = listNotify
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return notifyList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val message = itemView.alert_message!!
        val txtViewLease = itemView.txt_view_lease!!
        val imgDelAlert = itemView.img_del_alert!!


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(activity).inflate(R.layout.item_alerts_agent, parent, false)
        return ViewHolder(v)
    }

    private fun getLeaseList(idLease: String) {
        if (NetworkConnection.isNetworkConnected(activity)) {
            //Create retrofit Service
            pbAlert.visibility = View.VISIBLE
            val credential = FindLeaseCredentials()

            credential.leaseId = idLease
            val findApi: ApiInterface =
                ApiClient(activity!!).provideService(ApiInterface::class.java)
            val apiCall: Observable<FindLeaseResponse> =
                findApi.findLease(credential)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", it.responseDto?.responseDescription.toString())



                    if (it.data != null) {
                        alertLeaseInfo = it.data!![0]
                        getSignatureList(idLease)
                    }

                },
                    { e ->
                        Log.e("onFailure", e.toString())
                        pbAlert.visibility = View.GONE
                        e.message?.let {
                            Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
                        }
                    })
        } else {
            Toast.makeText(activity, context.getString(R.string.error_network), Toast.LENGTH_SHORT)
                .show()

        }
    }

    private fun getSignatureList(idLease: String) {
        if (NetworkConnection.isNetworkConnected(activity)) {
            //Create retrofit Service
            pbAlert.visibility = View.VISIBLE
            val credential = SignatureLeaseCredentials()

            credential.leaseId = idLease
            val signatureApi: ApiInterface =
                ApiClient(activity!!).provideService(ApiInterface::class.java)
            val apiCall: Observable<SignatureLeaseResponse> =
                signatureApi.getLeaseSignature(credential)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", it.responseDto?.responseDescription.toString())

                    pbAlert.visibility = View.GONE

                    if (it.data != null) {
                        alertListSignature = it.data!!

                    }
                    fetchSecurityList(idLease)

                },
                    { e ->
                        Log.e("onFailure", e.toString())
                        pbAlert.visibility = View.GONE
                        e.message?.let {
                            Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
                        }
                    })
        } else {
            Toast.makeText(activity, context.getString(R.string.error_network), Toast.LENGTH_SHORT)
                .show()

        }
    }

    private fun fetchSecurityList(idLease: String) {
        if (NetworkConnection.isNetworkConnected(activity)) {
            //Create retrofit Service
            pbAlert.visibility = View.VISIBLE
            val credential = SignatureLeaseCredentials()

            credential.leaseId = idLease
            val signatureApi: ApiInterface =
                ApiClient(activity!!).provideService(ApiInterface::class.java)
            val apiCall: Observable<SecurityFetchResponse> =
                signatureApi.securityFetchInfo(credential)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", it.responseDto?.responseDescription.toString())

                    pbAlert.visibility = View.GONE

                    if (it.data != null) {
                        alertSecurityList = it.data!!

                    }
                    val intent = Intent(activity, LeaseRequestDetailScreenAgent::class.java)
                    intent.putExtra(context.getString(R.string.intent_alert_lease), true)
                    activity.startActivity(intent)
                   // Util.navigationNext(activity)

                },
                    { e ->
                        Log.e("onFailure", e.toString())
                        pbAlert.visibility = View.GONE
                        e.message?.let {
                            Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
                        }
                    })
        } else {
            Toast.makeText(activity, context.getString(R.string.error_network), Toast.LENGTH_SHORT)
                .show()

        }
    }

    private fun actionDelete() {
       /* context.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )*/
        pbAlert.visibility = View.VISIBLE
        var credential = LinkPropertyActionCredential()
        credential.notificationId = notificationId

        val alertDelAction: ApiInterface =
            ApiClient(activity).provideService(ApiInterface::class.java)
        val apiCall: Observable<ResponseDto> =
            alertDelAction.delAlert(credential) //Test API Key







        RxAPICallHelper().call(apiCall, object : RxAPICallback<ResponseDto> {
            override fun onSuccess(response: ResponseDto) {


               (context as AlertsFragmentAgent).getNotifyList()








              //  getNotifyList()

           /*  var    intent: Intent= Intent(context,NotifyScreenAgent::class.java)
                context.startActivity(intent)
*/



              // (activity as AlertsFragmentAgent).getNotifyList()



            }

            override fun onFailed(t: Throwable) {
                // show error
               // activity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                pbAlert.visibility = View.GONE

            }

        })


    }

/*
    fun getNotifyList() {
        val credentials = NotificationCredential()

        credentials.userCatalog = Kotpref.userId
        val notifyListService: ApiInterface =
            ApiClient(context).provideService(ApiInterface::class.java)
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

              //  setData(alertsList)
                pbAlert.visibility = View.GONE

               // activity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }

            override fun onFailed(throwable: Throwable) {
                // show error
                Log.e("onFailure", throwable.toString())
                pbAlert.visibility = View.GONE
           //     activity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })

    }
*/

    companion object {
        var alertLeaseInfo = LeaseRequestInfo()
        var alertListSignature = ArrayList<LeaseSignature>()
        var alertSecurityList = ArrayList<FetchSecurityInfo>()
    }
}