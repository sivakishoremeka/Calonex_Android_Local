package mp.app.calonex.tenant.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.item_tenant_application_history.view.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.FetchDocumentCredential
import mp.app.calonex.common.apiCredentials.FindLeaseCredentials
import mp.app.calonex.common.apiCredentials.GetPropertyWithUnitDetailsCred
import mp.app.calonex.common.apiCredentials.SignatureLeaseCredentials
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.common.utility.Util.convertLongToTime
import mp.app.calonex.common.utility.Util.getLeaseStatus
import mp.app.calonex.landlord.activity.LeaseRequestDetailScreen
import mp.app.calonex.landlord.adapter.NotificationAlertsAdapter
import mp.app.calonex.landlord.model.*
import mp.app.calonex.landlord.response.*
import mp.app.calonex.tenant.activity.*
import mp.app.calonex.tenant.model.LeaseTenantInfo

class TenantApplicationHistoryAdapter(
    var context: Context,
    var pblr: ProgressBar,
    var activity: Activity,
    var list: ArrayList<LeaseTenantInfo>,
    val onClick: (LeaseTenantInfo) -> Unit

) : RecyclerView.Adapter<TenantApplicationHistoryAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtItemLrUnitNmbr.text = "UNIT# : " + list[position].unitNumber
        holder.txtItemLrTenantName.text = "Broker : " + list[position].brokerName
        holder.txtItemLrDateReq.text = context.getString(R.string.text_lease_date) + "" +
                convertLongToTime(list[position].leaseStartDate.toLong(), "MMM dd, yyyy")
        holder.txtItemLrRentAmt.text =
            context.getString(R.string.text_lease_rent_amount) + " $" + list[position].rentAmount
        holder.txtItemLrLeaseDuration.text =
            context.getString(R.string.text_lease_duration) + "" + list[position].leaseDuration + " Months"

        try {
           // Log.e("Unit Image", "File list ==> " + Gson().toJson(listUnit[position].fileList))

            val imgList = ArrayList<String>()
            for (item in TenantPropertyDetailViewActivity.propertyFileResponseLocal) {
                if (item.uploadFileType.equals(context.getString(R.string.key_img_property))) {
                    imgList.add(item.fileName)
                }
            }

            if (imgList.size > 0) {
                Glide.with(context)
                    .load(context.getString(R.string.base_url_img) + imgList[0])
                    .placeholder(Util.customProgress(context))
                    .into(holder.iv_property_image)
            } else {
                Glide.with(context)
                    .load(R.drawable.profile_default_new)
                    .placeholder(Util.customProgress(context))
                    .into(holder.iv_property_image)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Glide.with(context)
                .load(R.drawable.unit_sample_image)
                .placeholder(Util.customProgress(context))
                .into(holder.iv_property_image)
        }



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
            Kotpref.propertyId = list[position].propertyId
            Kotpref.leaseId = list[position].leaseId
            Kotpref.unitNumber = list[position].unitNumber
            Kotpref.unitId = list[position].unitId
            Kotpref.landlordId = list[position].landlordId

            LeaseListActivity.leaseItem = list[position]

            getPropertyById(
                list[position].propertyId,
                list[position].unitId,
                position
            )

        }


        holder.tv_interested.setOnClickListener {
            getLeaseList(list.get(position).leaseId)
           /* Kotpref.propertyId = list[position].propertyId
            Kotpref.leaseId = list[position].leaseId
            Kotpref.unitNumber = list[position].unitNumber
            Kotpref.unitId = list[position].unitId
            Kotpref.landlordId = list[position].landlordId



            LeaseListActivity.leaseItem = list[position]
            val intent = Intent(context, TenantLeaseDetailsActivity::class.java)
            intent.putExtra("leasePosition", position)
            context.startActivity(intent)*/

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
        val tv_interested = itemView.tv_interested


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v =
            LayoutInflater.from(context)
                .inflate(R.layout.item_tenant_application_history, parent, false)
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


    private fun getPropertyById(propertyId: String?, unitId: String?, position: Int) {
        activity!!.window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        pblr.visibility = View.VISIBLE
        var credential = GetPropertyWithUnitDetailsCred()
        credential.propertyId = propertyId!!.toLong()
        credential.propertyUnitId = unitId!!.toLong()

        val propertyDetails: ApiInterface =
            ApiClient(context).provideService(ApiInterface::class.java)
        val apiCall: Observable<PropertyDetailResponse> =
            propertyDetails.getPropertyAndUnitById(credential) //Test API Key

        RxAPICallHelper().call(apiCall, object : RxAPICallback<PropertyDetailResponse> {
            override fun onSuccess(propertyDetail: PropertyDetailResponse) {
                Log.e("Get", "property Detail >> " + Gson().toJson(propertyDetail))
                activity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                pblr.visibility = View.GONE
                if (propertyDetail.data != null) {

                    propertyDetailViewResponse = propertyDetail.data!!
                    fetchImages(propertyId.toString(), position)
                } else {
                    /*Toast.makeText(
                        context,
                        "" + propertyDetail.responseDescription,
                        Toast.LENGTH_LONG
                    ).show()*/
                }
            }

            override fun onFailed(t: Throwable) {
                // show error
                activity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                pblr.visibility = View.GONE
                Log.e("onFailure", t.toString())
                //  progressBarList?.visibility = View.GONE
            }
        })
    }

    private fun fetchImages(propertyId: String, position: Int) {
        if (NetworkConnection.isNetworkConnected(context)) {
            activity!!.window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            pblr.visibility = View.VISIBLE
            val credentials = FetchDocumentCredential()
            credentials.propertyId = propertyId

            val signatureApi: ApiInterface =
                ApiClient(context).provideService(ApiInterface::class.java)
            val apiCall: Observable<FetchDocumentResponse> =
                signatureApi.fetchDocument(credentials)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", "All File List ====>> " + Gson().toJson(it.data!!))

                    if (it.responseDto!!.responseCode.equals(200)) {
                        //getLeaseList(propertyId)
                        listPropertyViewImages = it.data!!
                        pblr.visibility = View.GONE
                        activity!!.window
                            .clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        val intent = Intent(context, TenantPropertyDetailViewActivity::class.java)
                        intent.putExtra("position", ""+position)
                        context.startActivity(intent)
                        Util.navigationNext(activity!!)
                    } else {
                        pblr.visibility = View.GONE
                        activity!!.window
                            .clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        Toast.makeText(context, it.responseDto!!.message, Toast.LENGTH_SHORT).show()
                    }
                },
                    { e ->
                        Log.e("onFailure", e.toString())
                        pblr.visibility = View.GONE
                        activity!!.window
                            .clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
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
        var leaseRequestInfo = LeaseTenantInfo()
        var signList = ArrayList<LeaseSignature>()
        var leaseSecurityList = ArrayList<FetchSecurityInfo>()
        var dataFetch = ArrayList<FetchDocumentModel>()

        var propertyDetailViewResponse = PropertyDetail()
        var listPropertyViewImages = ArrayList<FetchDocumentModel>()
    }

    private fun getLeaseList(idLease: String) {
        if (NetworkConnection.isNetworkConnected(context)) {
            //Create retrofit Service
            //pbAlert!!.visibility = View.VISIBLE
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
                        NotificationAlertsAdapter.alertLeaseInfo = it.data!![0]
                        getSignatureListfromNotification(idLease)
                    }

                },
                    { e ->
                        Log.e("onFailure", e.toString())
              //          pbAlert!!.visibility = View.GONE
                        e.message?.let {
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        }
                    })
        } else {
            Toast.makeText(context, context.getString(R.string.error_network), Toast.LENGTH_SHORT)
                .show()

        }
    }

    private fun getSignatureListfromNotification(idLease: String) {
        if (NetworkConnection.isNetworkConnected(context)) {
            //Create retrofit Service
          //  pbAlert!!.visibility = View.VISIBLE
            val credential = SignatureLeaseCredentials()

            credential.leaseId = idLease
            val signatureApi: ApiInterface =
                ApiClient(activity!!).provideService(ApiInterface::class.java)
            val apiCall: Observable<SignatureLeaseResponse> =
                signatureApi.getLeaseSignature(credential)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", it.responseDto?.responseDescription.toString())

            //        pbAlert!!.visibility = View.GONE

                    if (it.data != null) {
                        NotificationAlertsAdapter.alertListSignature = it.data!!

                    } else {
                        NotificationAlertsAdapter.alertListSignature = ArrayList<LeaseSignature>()
                    }
                    fetchSecurityList(idLease)

                },
                    { e ->
                        Log.e("onFailure", e.toString())
              //          pbAlert!!.visibility = View.GONE
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
          //  pbAlert!!.visibility = View.VISIBLE
            val credential = SignatureLeaseCredentials()

            credential.leaseId = idLease
            val signatureApi: ApiInterface =
                ApiClient(activity!!).provideService(ApiInterface::class.java)
            val apiCall: Observable<SecurityFetchResponse> =
                signatureApi.securityFetchInfo(credential)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", it.responseDto?.responseDescription.toString())

            //        pbAlert!!.visibility = View.GONE

                    if (it.data != null) {
                        NotificationAlertsAdapter.alertSecurityList = it.data!!

                    }
                    val intent = Intent(activity!!, LeaseRequestDetailScreen::class.java)
                    intent.putExtra(context.getString(R.string.intent_alert_lease), true)
                    activity!!.startActivity(intent)
                    Util.navigationNext(activity!!)

                },
                    { e ->
                        Log.e("onFailure", e.toString())
              //          pbAlert!!.visibility = View.GONE
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