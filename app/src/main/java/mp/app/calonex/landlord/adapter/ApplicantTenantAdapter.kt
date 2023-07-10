package mp.app.calonex.landlord.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.item_applicant_tenant.view.*

import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.FindLeaseCredentials
import mp.app.calonex.common.apiCredentials.SignatureLeaseCredentials
import mp.app.calonex.common.constant.StatusConstant.Companion.globalStatus
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.LeaseRequestDetailScreen
import mp.app.calonex.landlord.model.LeaseSignature
import mp.app.calonex.landlord.response.FindLeaseResponse
import mp.app.calonex.landlord.response.SecurityFetchResponse
import mp.app.calonex.landlord.response.SignatureLeaseResponse
import mp.app.calonex.tenant.activity.*
import mp.app.calonex.tenant.model.LeaseTenantInfo

class ApplicantTenantAdapter(
    var context: Context,
    val leaseList: ArrayList<LeaseTenantInfo>,
    var type: String
) :
    RecyclerView.Adapter<ApplicantTenantAdapter.ViewHolder>() {


    var listLease = ArrayList<LeaseTenantInfo>()

    init {
        listLease = leaseList
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.propertyAddress.text =
            listLease[position].propertyAddress + "," + listLease[position].propertyCity + "," + listLease[position].propertyState

        when (type) {
            "applicant" -> {
                holder.broker_name_txt.text = context.getString(R.string.text_applicant)
            }
            "tenant" -> {
                holder.broker_name_txt.text = context.getString(R.string.text_tenant)
            }
        }
        val gson = Gson()

        if (listLease[position].tenantBaseInfoDto.size > 0) {
            holder.tenant.text =
                listLease[position].tenantBaseInfoDto[0].tenantFirstName + " " + listLease[position].tenantBaseInfoDto[0].tenantLastName

        }

        holder.amount.text = listLease[position].rentAmount
        holder.unitNo.text = listLease[position].leaseDuration
        holder.status.text = globalStatus(listLease[position].leaseStatus)
        holder.unit.text = "#" + listLease[position].unitId
        holder.paymentDate.text = Util.getDateTime(listLease[position].leaseStartDate)

        holder.listitem.setOnClickListener {

            var alert= SweetAlertDialog(context,SweetAlertDialog.PROGRESS_TYPE)
            alert.setContentText("Loading")
              alert.show()
            getLeaseList(leaseList.get(position).leaseId,alert)
        }


    }

    override fun getItemCount(): Int {
        return leaseList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val unit = itemView.landlord_payment_tenant_name
        val tenant = itemView.landlord_payment_broker_name
        val broker_name_txt = itemView.broker_name_txt
        val paymentDate = itemView.landlord_payment_date
        val propertyAddress = itemView.landlord_payment_prop_address
        val status = itemView.txt_payment_status
        val amount = itemView.landlord_payment_amount
        val unitNo = itemView.landlord_payment_unit
        val listitem = itemView.listitem


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_applicant_tenant, parent, false)
        return ViewHolder(v)
    }
    private fun getLeaseList(idLease: String,alertDialog: SweetAlertDialog) {
        if (NetworkConnection.isNetworkConnected(context)) {
            //Create retrofit Service
           // pbAlert!!.visibility = View.VISIBLE
            val credential = FindLeaseCredentials()

            credential.leaseId = idLease
            val findApi: ApiInterface =
                ApiClient(context).provideService(ApiInterface::class.java)
            val apiCall: Observable<FindLeaseResponse> =
                findApi.findLease(credential)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", it.responseDto?.responseDescription.toString())



                    if (it.data != null) {
                        NotificationAlertsAdapter.alertLeaseInfo = it.data!![0]
                        getSignatureList(idLease,alertDialog)
                    }

                },
                    { e ->
                        Log.e("onFailure", e.toString())
                       // pbAlert!!.visibility = View.GONE
                        e.message?.let {
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        }
                    })
        } else {
            Toast.makeText(context, context.getString(R.string.error_network), Toast.LENGTH_SHORT)
                .show()

        }
    }

    private fun getSignatureList(idLease: String,alertDialog: SweetAlertDialog) {
        if (NetworkConnection.isNetworkConnected(context)) {
            //Create retrofit Service
            //pbAlert!!.visibility = View.VISIBLE
            val credential = SignatureLeaseCredentials()

            credential.leaseId = idLease
            val signatureApi: ApiInterface =
                ApiClient(context).provideService(ApiInterface::class.java)
            val apiCall: Observable<SignatureLeaseResponse> =
                signatureApi.getLeaseSignature(credential)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", it.responseDto?.responseDescription.toString())

                   // pbAlert!!.visibility = View.GONE

                    if (it.data != null) {
                        NotificationAlertsAdapter.alertListSignature = it.data!!

                    } else {
                        NotificationAlertsAdapter.alertListSignature = ArrayList<LeaseSignature>()
                    }
                    fetchSecurityList(idLease,alertDialog)

                },
                    { e ->
                        Log.e("onFailure", e.toString())
                     //   pbAlert!!.visibility = View.GONE
                        e.message?.let {
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        }
                    })
        } else {
            Toast.makeText(context, context.getString(R.string.error_network), Toast.LENGTH_SHORT)
                .show()

        }
    }

    private fun fetchSecurityList(idLease: String,alertDialog: SweetAlertDialog) {
        if (NetworkConnection.isNetworkConnected(context)) {
            //Create retrofit Service
           // pbAlert!!.visibility = View.VISIBLE
            val credential = SignatureLeaseCredentials()

            credential.leaseId = idLease
            val signatureApi: ApiInterface =
                ApiClient(context!!).provideService(ApiInterface::class.java)
            val apiCall: Observable<SecurityFetchResponse> =
                signatureApi.securityFetchInfo(credential)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", it.responseDto?.responseDescription.toString())
                    alertDialog.dismiss()
                   // pbAlert!!.visibility = View.GONE
                    if (it.data != null) {
                        NotificationAlertsAdapter.alertSecurityList = it.data!!

                    }
                    val intent = Intent(context!!, LeaseRequestDetailScreen::class.java)
                    intent.putExtra(context.getString(R.string.intent_alert_lease), true)
                    context!!.startActivity(intent)
                  //  Util.navigationNext(context)

                },
                    { e ->
                        Log.e("onFailure", e.toString())
                     //   pbAlert!!.visibility = View.GONE
                        e.message?.let {
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        }
                    })
        } else {
            Toast.makeText(context, context.getString(R.string.error_network), Toast.LENGTH_SHORT)
                .show()

        }
    }

}