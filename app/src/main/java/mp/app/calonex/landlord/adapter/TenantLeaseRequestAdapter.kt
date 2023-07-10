package mp.app.calonex.landlord.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.item_lease_request.view.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.FetchDocumentCredential
import mp.app.calonex.common.apiCredentials.SignatureLeaseCredentials
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.common.utility.Util.convertLongToTime
import mp.app.calonex.common.utility.Util.getLeaseStatus
import mp.app.calonex.landlord.activity.LeaseRequestDetailScreen
import mp.app.calonex.landlord.model.*
import mp.app.calonex.landlord.response.FetchDocumentResponse
import mp.app.calonex.landlord.response.SecurityFetchResponse
import mp.app.calonex.landlord.response.SignatureLeaseResponse

class TenantLeaseRequestAdapter(
    var context: Context,
    var pblr: ProgressBar,
    var activity: Activity,
    var list: ArrayList<LeaseRequestInfo>
) : RecyclerView.Adapter<TenantLeaseRequestAdapter.ViewHolder>() {


    private var isFetchSecurity: Boolean = false

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtItemLrUnitNmbr.text = "Unit : #" + list[position].unitNumber
        holder.txtItemLrDateReq.text = context.getString(R.string.text_lease_date) + "" +
                convertLongToTime(list[position].leaseStartDate.toLong(), "MMM dd, yyyy")
        holder.txtItemLrRentAmt.text =
            context.getString(R.string.text_lease_amount) + "" + list[position].rentAmount
        holder.txtItemLrLeaseDuration.text =
            context.getString(R.string.text_lease_duration) + "" + list[position].leaseDuration
        holder.txtItemLrBrkrAgent.text =
            context.getString(R.string.text_lease_broker_agent) + "" + list[position].brokerEmailId
        holder.txtItemLrLeaseStatus.text =
            context.getString(R.string.text_lease_status) + "" + getLeaseStatus(list[position].leaseStatus)

        if (list[position].leaseStatus.equals("23") || list[position].leaseStatus.equals("24")) {
            holder.txtItemLrLeaseStatus.setTextColor(Color.RED)
        }
        if (list[position].leaseStatus.equals("19")) {
            holder.txtItemLrLeaseStatus.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.colorGreen
                )
            )
        }
        val tenantBaseInfoDto: ArrayList<ObTenantPayload>? = list[position].tenantBaseInfoDto
        if (tenantBaseInfoDto!!.size > 0) {
            for (item in tenantBaseInfoDto) {
                var mName: String? = ""
                if (item.role.equals("CX-PrimaryTenant")) {

                    if (item.tenantMiddleName != null)
                        mName = item.tenantMiddleName

                    holder.txtItemLrTenantName.text =
                        context.getString(R.string.text_tenant) + "" + item.tenantFirstName + " " + mName + " " +
                                item.tenantLastName /*+ "\n" + item.emailId*/
                    break
                } else {
                    holder.txtItemLrTenantName.text =
                        context.getString(R.string.text_tenant) + "" + item.tenantFirstName + " " + mName + " " +
                                item.tenantLastName
                    break
                }
            }
        }


        holder.itemView.setOnClickListener {
            leaseRequestInfo = list[holder.adapterPosition]
            getSignatureList(list[holder.adapterPosition].leaseId)
            isFetchSecurity = list[holder.adapterPosition].leaseStatus.toInt() > 23
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val txtItemLrUnitNmbr = itemView.txt_item_lr_unit_nmbr
        val txtItemLrTenantName = itemView.txt_item_lr_tenant_name
        val txtItemLrDateReq = itemView.txt_item_lr_date_req
        val txtItemLrRentAmt = itemView.txt_item_lr_rent_amt
        val txtItemLrLeaseDuration = itemView.txt_item_lr_lease_duration
        val txtItemLrBrkrAgent = itemView.txt_item_lr_brkr_agent
        val txtItemLrLeaseStatus = itemView.txt_item_lr_lease_status


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(context).inflate(R.layout.item_lease_request, parent, false)
        return ViewHolder(v)
    }

    private fun getSignatureList(idLease: String) {
        if (NetworkConnection.isNetworkConnected(context)) {
            //Create retrofit Service
            pblr!!.visibility = View.VISIBLE
            val credential = SignatureLeaseCredentials()

            credential.leaseId = idLease
            val signatureApi: ApiInterface =
                ApiClient(activity!!).provideService(ApiInterface::class.java)
            val apiCall: Observable<SignatureLeaseResponse> =
                signatureApi.getLeaseSignature(credential)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", it.responseDto?.responseDescription.toString())

                    pblr!!.visibility = View.GONE

                    if (it.data != null) {
                        signList = it.data!!

                    }
                    //  if(isFetchSecurity){
                    securityFetchList(idLease)
                    /*}else{
                        val intent=Intent(activity!!,LeaseRequestDetailScreen::class.java)
                        intent.putExtra(context.getString(R.string.intent_tenant_req_adap),true)
                        activity!!.startActivity(intent)
                        Util.navigationNext(activity!!)
                    }*/

                },
                    { e ->
                        Log.e("onFailure", e.toString())
                        pblr!!.visibility = View.GONE
                        e.message?.let {
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        }
                    })
        } else {
            Toast.makeText(context, context.getString(R.string.error_network), Toast.LENGTH_SHORT)
                .show()

        }
    }

    private fun securityFetchList(idLease: String) {
        if (NetworkConnection.isNetworkConnected(context)) {
            //Create retrofit Service
            pblr!!.visibility = View.VISIBLE
            val credential = SignatureLeaseCredentials()

            credential.leaseId = idLease
            val securityApi: ApiInterface =
                ApiClient(activity!!).provideService(ApiInterface::class.java)
            val apiCall: Observable<SecurityFetchResponse> =
                securityApi.securityFetchInfo(credential)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", it.responseDto?.responseDescription.toString())

                    pblr!!.visibility = View.GONE

                    if (it.data != null) {
                        leaseSecurityList = it.data!!

                    }

                    dataFetchList(idLease)

                },
                    { e ->
                        Log.e("onFailure", e.toString())
                        pblr!!.visibility = View.GONE
                        e.message?.let {
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        }
                    })
        } else {
            Toast.makeText(context, context.getString(R.string.error_network), Toast.LENGTH_SHORT)
                .show()

        }
    }

    private fun dataFetchList(idLease: String) {
        if (NetworkConnection.isNetworkConnected(context)) {
            //Create retrofit Service
            pblr!!.visibility = View.VISIBLE
            val credential = FetchDocumentCredential()

            credential.leaseId = idLease
            val securityApi: ApiInterface =
                ApiClient(activity!!).provideService(ApiInterface::class.java)
            val apiCall: Observable<FetchDocumentResponse> =
                securityApi.fetchDocument(credential)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", it.responseDto?.responseDescription.toString())

                    pblr!!.visibility = View.GONE

                    if (it.data != null) {
                        dataFetch = it.data!!

                    }

                    val intent = Intent(activity!!, LeaseRequestDetailScreen::class.java)
                    intent.putExtra(context.getString(R.string.intent_tenant_req_adap), true)
                    activity!!.startActivity(intent)
                    Util.navigationNext(activity!!)

                },
                    { e ->
                        Log.e("onFailure", e.toString())
                        pblr!!.visibility = View.GONE
                        e.message?.let {
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        }
                    })
        } else {
            Toast.makeText(context, context.getString(R.string.error_network), Toast.LENGTH_SHORT)
                .show()

        }
    }

    companion object {
        var leaseRequestInfo = LeaseRequestInfo()
        var signList = ArrayList<LeaseSignature>()
        var leaseSecurityList = ArrayList<FetchSecurityInfo>()
        var dataFetch = ArrayList<FetchDocumentModel>()
    }

}