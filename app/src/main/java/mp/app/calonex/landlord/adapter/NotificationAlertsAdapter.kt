package mp.app.calonex.landlord.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.item_alerts.view.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.FindLeaseCredentials
import mp.app.calonex.common.apiCredentials.LinkPropertyActionCredential
import mp.app.calonex.common.apiCredentials.NotificationCredential
import mp.app.calonex.common.apiCredentials.SignatureLeaseCredentials
import mp.app.calonex.common.network.*
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.LeaseRequestDetailScreen
import mp.app.calonex.landlord.dashboard.DashboardLdFragment.Companion.alertsList
import mp.app.calonex.landlord.model.*
import mp.app.calonex.landlord.model.ResponseDto
import mp.app.calonex.landlord.response.*

class NotificationAlertsAdapter(
    var context: Context,
    val pbAlert: ProgressBar,
    val activity: FragmentActivity?,
    val notifyList: List<AppNotifications>
) : RecyclerView.Adapter<NotificationAlertsAdapter.ViewHolder>() {
    lateinit var listNotify: List<AppNotifications>

    init {
        this.listNotify = notifyList
    }

    var mNotificationAlertsModel = NotificationAlertsModel()
    var notificationId: String = ""
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val gson = Gson()

        val objectList = gson.fromJson(
            listNotify[position].notificationData,
            NotificationAlertsModel::class.java
        )
        mNotificationAlertsModel = objectList

        holder.message.text = mNotificationAlertsModel.info
        if (holder.message.text.toString().contains("Lease") || holder.message.text.toString()
                .contains("lease")
        ) {
            holder.txtViewLease.visibility = View.VISIBLE
            holder.imgDelAlert.visibility = View.GONE
        } else {
            holder.txtViewLease.visibility = View.VISIBLE
            holder.imgDelAlert.visibility = View.VISIBLE
        }
        holder.txtViewLease.setOnClickListener {

            holder.txtViewLease.isClickable = false
            holder.imgDelAlert.isClickable = false

            val objectList = gson.fromJson(
                listNotify[holder.adapterPosition].notificationData,
                NotificationAlertsModel::class.java
            )

            getLeaseList(objectList.leaseId)

        }

        holder.imgDelAlert.setOnClickListener {

            holder.imgDelAlert.isClickable = false
            holder.txtViewLease.isClickable = false

            notificationId = listNotify[holder.adapterPosition].notificationId.toString()
            actionDelete()
        }

    }

    fun setData(listNotify: List<AppNotifications>) {
        this.listNotify = listNotify
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return listNotify.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val message = itemView.alert_message!!
        val txtViewLease = itemView.txt_view_lease!!
        val imgDelAlert = itemView.img_del_alert!!


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(context).inflate(R.layout.item_alerts, parent, false)
        return ViewHolder(v)
    }

    private fun getLeaseList(idLease: String) {
        if (NetworkConnection.isNetworkConnected(context)) {
            //Create retrofit Service
            pbAlert!!.visibility = View.VISIBLE
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
                        pbAlert!!.visibility = View.GONE
                        e.message?.let {
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        }
                    })
        } else {
            Toast.makeText(context, context.getString(R.string.error_network), Toast.LENGTH_SHORT)
                .show()

        }
    }

    private fun getSignatureList(idLease: String) {
        if (NetworkConnection.isNetworkConnected(context)) {
            //Create retrofit Service
            pbAlert!!.visibility = View.VISIBLE
            val credential = SignatureLeaseCredentials()

            credential.leaseId = idLease
            val signatureApi: ApiInterface =
                ApiClient(activity!!).provideService(ApiInterface::class.java)
            val apiCall: Observable<SignatureLeaseResponse> =
                signatureApi.getLeaseSignature(credential)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", it.responseDto?.responseDescription.toString())

                    pbAlert!!.visibility = View.GONE

                    if (it.data != null) {
                        alertListSignature = it.data!!

                    } else {
                        alertListSignature = ArrayList<LeaseSignature>()
                    }
                    fetchSecurityList(idLease)

                },
                    { e ->
                        Log.e("onFailure", e.toString())
                        pbAlert!!.visibility = View.GONE
                        e.message?.let {
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        }
                    })
        } else {
            Toast.makeText(context, context.getString(R.string.error_network), Toast.LENGTH_SHORT)
                .show()

        }
    }

    private fun fetchSecurityList(idLease: String) {
        if (NetworkConnection.isNetworkConnected(context)) {
            //Create retrofit Service
            pbAlert!!.visibility = View.VISIBLE
            val credential = SignatureLeaseCredentials()

            credential.leaseId = idLease
            val signatureApi: ApiInterface =
                ApiClient(activity!!).provideService(ApiInterface::class.java)
            val apiCall: Observable<SecurityFetchResponse> =
                signatureApi.securityFetchInfo(credential)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", it.responseDto?.responseDescription.toString())

                    pbAlert!!.visibility = View.GONE

                    if (it.data != null) {
                        alertSecurityList = it.data!!

                    }
                    val intent = Intent(activity!!, LeaseRequestDetailScreen::class.java)
                    intent.putExtra(context.getString(R.string.intent_alert_lease), true)
                    activity!!.startActivity(intent)
                    Util.navigationNext(activity!!)

                },
                    { e ->
                        Log.e("onFailure", e.toString())
                        pbAlert!!.visibility = View.GONE
                        e.message?.let {
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        }
                    })
        } else {
            Toast.makeText(context, context.getString(R.string.error_network), Toast.LENGTH_SHORT)
                .show()

        }
    }

    private fun actionDelete() {
        activity!!.getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        pbAlert!!.visibility = View.VISIBLE
        var credential = LinkPropertyActionCredential()
        credential.notificationId = notificationId

        val alertDelAction: ApiInterface =
            ApiClient(context).provideService(ApiInterface::class.java)
        val apiCall: Observable<ResponseDto> =
            alertDelAction.delAlert(credential) //Test API Key

        RxAPICallHelper().call(apiCall, object : RxAPICallback<ResponseDto> {
            override fun onSuccess(response: ResponseDto) {


                getNotifyList()


            }

            override fun onFailed(t: Throwable) {
                // show error
                activity!!.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                pbAlert!!.visibility = View.GONE

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
                alertsList.clear()
                if (!response.data?.isEmpty()!!) {
                    appNotifications = response.data!!


                    for (i in 0..appNotifications.size - 1) {

                        if (appNotifications[i].activityType == "2") {

                            alertsList.add(appNotifications[i])
                        }
                    }

                }
                setData(alertsList)
                pbAlert.visibility = View.GONE
                activity!!.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }

            override fun onFailed(throwable: Throwable) {
                // show error
                Log.e("onFailure", throwable.toString())
                pbAlert.visibility = View.GONE
                activity!!.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })

    }

    companion object {
        var alertLeaseInfo = LeaseRequestInfo()
        var alertListSignature = ArrayList<LeaseSignature>()
        var alertSecurityList = ArrayList<FetchSecurityInfo>()
    }
}