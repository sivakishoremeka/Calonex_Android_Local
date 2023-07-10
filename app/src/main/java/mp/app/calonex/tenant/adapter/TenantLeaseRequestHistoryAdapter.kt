package mp.app.calonex.tenant.adapter

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
import com.bumptech.glide.Glide
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.item_tenant_lease_request.view.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.FetchDocumentCredential
import mp.app.calonex.common.apiCredentials.SignatureLeaseCredentials
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.common.utility.Util.convertLongToTime
import mp.app.calonex.common.utility.Util.getLeaseStatus
import mp.app.calonex.landlord.activity.LeaseRequestDetailScreen
import mp.app.calonex.landlord.model.*
import mp.app.calonex.landlord.response.FetchDocumentResponse
import mp.app.calonex.landlord.response.SecurityFetchResponse
import mp.app.calonex.landlord.response.SignatureLeaseResponse
import mp.app.calonex.tenant.activity.*
import mp.app.calonex.tenant.model.LeaseTenantInfo

class TenantLeaseRequestHistoryAdapter(
    var context: Context,
    var pblr: ProgressBar,
    var activity: Activity,
    var list: ArrayList<LeaseTenantInfo>,
    val onClick: (LeaseTenantInfo) -> Unit

) : RecyclerView.Adapter<TenantLeaseRequestHistoryAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtItemLrUnitNmbr.text = "UNIT# : " + list[position].unitNumber

        if (list[position].brokerName != null && list[position].brokerName != "null") {
            holder.txtItemLrTenantName.text = "Broker : " + list[position].brokerName
        } else {
            holder.txtItemLrTenantName.text = "Broker : NA"
        }

        holder.txtItemLrDateReq.text = context.getString(R.string.text_lease_date) + "" +
                convertLongToTime(list[position].leaseStartDate.toLong(), "MMM dd, yyyy")
        holder.txtItemLrRentAmt.text =
            context.getString(R.string.text_lease_rent_amount) + " $" + list[position].rentAmount
        holder.txtItemLrLeaseDuration.text =
            context.getString(R.string.text_lease_duration) + "" + list[position].leaseDuration + " Months"

        Glide.with(context)
            .load(context.getString(R.string.base_url_img) + list[position].unitId)
            .placeholder(R.drawable.unit_sample_image)
            .into(holder.iv_property_image)


        holder.txtItemLrLeaseStatus.text =
            "" + getLeaseStatus(list[position].leaseStatus)

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

        try {
            val tenantBaseInfoDto: ArrayList<TenantInfoPayload>? = list[position].tenantBaseInfoDto
            if (tenantBaseInfoDto!!.size > 0) {
                holder.txtItemLrBrkrAgent.text =
                    context.getString(R.string.text_lease_tenant_email) + "" + list[position].tenantBaseInfoDto[0].emailId
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        holder.tv_view_details.setOnClickListener {
            //onClick.invoke(list[position])

            Kotpref.propertyId = list[position].propertyId
            Kotpref.leaseId = list[position].leaseId
            Kotpref.unitNumber = list[position].unitNumber
            Kotpref.unitId = list[position].unitId
            Kotpref.landlordId = list[position].landlordId

            LeaseListActivity.leaseItem = list[position]
            //val intent = Intent(context, HomeActivityTenant::class.java)
            val intent = Intent(context, LeaseDetailsForTenantsActivity::class.java)
            context.startActivity(intent)

         /*   if (list[position].leaseStatus == StatusConstant.FINALIZED && list[position].renewLease) {
                val builder = AlertDialog.Builder(context)
                builder.setTitle(context.getString(R.string.lease_renew_title))
                builder.setMessage(context.getString(R.string.lease_renew_message))
                // Navigate to permission screen of app in setting
                builder.setPositiveButton(context.getString(R.string.ok)) { dialog, which ->
                    dialog.cancel()
                }
                builder.show()

            } else if (list[position].leaseStatus == StatusConstant.FINALIZED && !list[position].renewLease) {
                LeaseListActivity.leaseItem = list[position]
                //val intent = Intent(context, HomeActivityTenant::class.java)
                val intent = Intent(context, LeaseListDetailsOptionsActivity::class.java)
                context.startActivity(intent)
            } else if (list[position].leaseStatus == StatusConstant.LEASE_TERMINATED) {
                LeaseListActivity.leaseItem = list[position]
                val intent = Intent(context, TenantPropertyUnitDetailActivityCx::class.java)
                intent.putExtra("leasePosition", position)
                context.startActivity(intent)
            } else {
                for (item in list[position].tenantBaseInfoDto) {

                    if (item.userId == Kotpref.userId) {
                        Kotpref.tenantId = item.tenantId!!

                        if (!item.tenantDataModifed!!) {
                            *//*val intent =
                                Intent(context, TenantVerifyUpdateDetailsFirstActivity::class.java)
                          *//*
                            val intent =
                                Intent(context, TenantListActivity::class.java)
                            //  intent.putExtra("tenantDetails", item)
                            intent.putExtra("leasePosition", position)
                            context.startActivity(intent)
                        } else {
                            val intent =
                                Intent(context, TenantListActivity::class.java)
                            intent.putExtra("leasePosition", position)
                            context.startActivity(intent)
                        }

                    }
                }

            }
*/
        }


        holder.itemView.setOnClickListener {
            /*leaseRequestInfo = list[holder.adapterPosition]
            getSignatureList(list[holder.adapterPosition].leaseId)
            isFetchSecurity = list[holder.adapterPosition].leaseStatus.toInt() > 23*/

            /*getPropertyById(
                list[position].propertyId,
                list[position].unitId
            )*/
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
        val iv_property_image = itemView.iv_property_image
        val tv_view_details = itemView.tv_view_details


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v =
            LayoutInflater.from(context).inflate(R.layout.item_tenant_lease_request, parent, false)
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