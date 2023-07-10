package mp.app.calonex.landlord.adapter

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.Toast

import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.property_list_item_landlord.view.*

import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.FetchDocumentCredential
import mp.app.calonex.common.apiCredentials.GetPropertyByIdApiCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback

import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util

import mp.app.calonex.landlord.activity.ApplicantTenantActivity
import mp.app.calonex.landlord.activity.PropertyDetailScreen
import mp.app.calonex.landlord.activity.RentPaymentActivity
import mp.app.calonex.landlord.model.*
import mp.app.calonex.landlord.response.FetchDocumentResponse
import mp.app.calonex.landlord.response.PropertyDetailResponse
import kotlin.collections.ArrayList

class DashBoardPropertyListAdapter(
    var context: Context,
    var activity: FragmentActivity?,
    var list: ArrayList<Property>,
    var pb_detail: ProgressBar,
) : RecyclerView.Adapter<DashBoardPropertyListAdapter.ViewHolder>() {

    private var propertylist = ArrayList<Property>()

    init {
        propertylist = list
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var Bp = propertylist.get(position);
        if (TextUtils.isEmpty(Bp.city)) {
            holder.locations.text = "NA"
        } else {
            holder.locations.text = Bp.city + ", " + Bp.state + ", " + Bp.zipCode
        }
        if (Bp.brokerEmailID == null || TextUtils.isEmpty(Bp.brokerEmailID)) {
            holder.email.text = "NA"
        } else {
            holder.email.text = Bp.brokerEmailID
        }
        if (Bp.brokerPhone == null || TextUtils.isEmpty(Bp.brokerPhone)) {
            holder.phone.text = "NA"
        } else {
            holder.phone.text = Bp.brokerPhone
        }
        if (Bp.address == null || TextUtils.isEmpty(Bp.address)) {
            holder.name.text = "NA"
        } else {
            holder.name.text = Bp.address
        }
        //if (Bp.broker == null || TextUtils.isEmpty(Bp.broker)) {

        /*} else {
            holder.name.text = Bp.broker
        }*/

        if (Bp.fileList.isNotEmpty()) {
            var propertyDocPath = ""

            for (i in 0 until Bp.fileList.size) {
                if (Bp.fileList[i].uploadFileType == context.getString(R.string.key_img_property)) {
                    propertyDocPath = Bp.fileList[i].fileName
                    break
                }
            }
            if (propertyDocPath.isNotEmpty()) {
                Glide.with(context)
                    .load(context.getString(R.string.base_url_img2) + propertyDocPath)
                    .placeholder(R.drawable.bg_default_property)
                    //.placeholder(R.drawable.default_property)
                    .into(holder.image);
            } else {
                Glide.with(context)
                    .load(R.drawable.bg_default_property)
                    .placeholder(R.drawable.bg_default_property)
                    //.load(R.drawable.default_property)
                    //.placeholder(R.drawable.default_property)
                    .into(holder.image);
            }


        } else {
            Glide.with(context)
                .load(R.drawable.bg_default_property)
                .placeholder(R.drawable.bg_default_property)
                //.load(R.drawable.default_property)
                //.placeholder(R.drawable.default_property)
                .into(holder.image);

        }

        holder.rent_payment.setOnClickListener {
            val intent = Intent(context, RentPaymentActivity::class.java)
            intent.putExtra("propertyId", Bp.propertyId)
            intent.putExtra("brokerId", Kotpref.userId)

            activity!!.startActivity(intent)
        }
        holder.applicant_tenant.setOnClickListener {
            val intent = Intent(context, ApplicantTenantActivity::class.java)

            Kotpref.propertyId = Bp.propertyId.toString()

            activity!!.startActivity(intent)
        }

        holder.tv_total_unit.text = "" + Bp.numberOfUnits
        holder.txt_cx_units.text = "" + Bp.unitsOccupiedInsideCX + "(CX)"
        holder.txt_non_cx_unit.text = "" + Bp.unitsOccupiedOutsideCX + "(non CX)"
        holder.tv_vacant.text = "" + Bp.unitsVacants

        holder.layoutPropertyView.setOnClickListener {
            // Navigate to property Detail Scree
            getPropertyById(
                Bp.propertyId,
                Bp.tenantFirstName + " " + Bp.tenantLastName,
                Bp.tenantEmailId
            )

        }


    }

    override fun getItemCount(): Int {
        return propertylist.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.name!!
        val locations = itemView.locations!!
        val email = itemView.email!!
        val phone = itemView.phone!!
        val image = itemView.property_image!!
        val rent_payment = itemView.rent_payment!!
        val applicant_tenant = itemView.applicanttenant!!
        val tv_total_unit = itemView.tv_total_unit!!
        val txt_cx_units = itemView.txt_cx_units!!
        val txt_non_cx_unit = itemView.txt_non_cx_unit!!
        val tv_vacant = itemView.tv_vacant!!
        val layoutPropertyView = itemView.layout_property_view!!

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(context)
            .inflate(R.layout.property_list_item_landlord, parent, false)

        return ViewHolder(v)
    }


    private fun getPropertyById(propertyId: Long?, tenantName: String, tenantEmail: String) {
        activity!!.getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        pb_detail!!.visibility = View.VISIBLE
        var credential = GetPropertyByIdApiCredential()
        credential.propertyId = propertyId

        val propertyDetails: ApiInterface =
            ApiClient(context).provideService(ApiInterface::class.java)
        val apiCall: Observable<PropertyDetailResponse> =
            propertyDetails.getPropertyById(credential) //Test API Key

        RxAPICallHelper().call(apiCall, object : RxAPICallback<PropertyDetailResponse> {
            override fun onSuccess(propertyDetail: PropertyDetailResponse) {
                Log.e("onSuccess", "userDetail.firstName")
                if (propertyDetail.data != null) {
                    Log.e("property dashboard", Gson().toJson(propertyDetail.data))

                    propertyDetailResponseHome = propertyDetail.data!!
                    fetchImages(propertyId.toString(), tenantName, tenantEmail)

                }


            }

            override fun onFailed(t: Throwable) {
                // show error
                activity!!.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                pb_detail!!.visibility = View.GONE
                Log.e("onFailure", t.toString())
                //  progressBarList?.visibility = View.GONE
            }

        })


    }

    private fun fetchImages(propertyId: String, tenantName: String, tenantEmail: String) {
        if (NetworkConnection.isNetworkConnected(context)) {
            activity!!.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            pb_detail!!.visibility = View.VISIBLE
            val credentials = FetchDocumentCredential()
            credentials.propertyId = propertyId

            val signatureApi: ApiInterface =
                ApiClient(context).provideService(ApiInterface::class.java)
            val apiCall: Observable<FetchDocumentResponse> =
                signatureApi.fetchDocument(credentials)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", it.responseDto!!.responseDescription.toString())
                    if (it.responseDto!!.responseCode.equals(200)) {
                        //getLeaseList(propertyId)
                        listPropertyImagesHome = it.data!!
                        pb_detail!!.visibility = View.GONE
                        activity!!.getWindow()
                            .clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        val intent = Intent(context, PropertyDetailScreen::class.java)
                        intent.putExtra("from", "dashboard")
                        intent.putExtra("tenantName", tenantName)
                        intent.putExtra("tenantEmail", tenantEmail)
                        context.startActivity(intent)
                        Util.navigationNext(activity!!)


                    } else {
                        pb_detail!!.visibility = View.GONE
                        activity!!.getWindow()
                            .clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        Toast.makeText(context, it.responseDto!!.message, Toast.LENGTH_SHORT).show()
                    }


                },
                    { e ->
                        Log.e("onFailure", e.toString())
                        pb_detail!!.visibility = View.GONE
                        activity!!.getWindow()
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
        var propertyDetailResponseHome = PropertyDetail()
        var listPropertyImagesHome = ArrayList<FetchDocumentModel>()
        var leasePropertyList = ArrayList<LeaseRequestInfo>()
    }
}