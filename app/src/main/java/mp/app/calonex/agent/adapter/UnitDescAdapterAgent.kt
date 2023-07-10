package mp.app.calonex.agent.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_property_description_agent.*
import kotlinx.android.synthetic.main.item_unit_description_agent.view.*
import mp.app.calonex.R
import mp.app.calonex.agent.activity.PropertyDetailScreenAgent.Companion.propertyDetailResponseLocal
import mp.app.calonex.common.apiCredentials.FindLeaseCredentials
import mp.app.calonex.common.apiCredentials.SignatureLeaseCredentials
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util

import mp.app.calonex.landlord.activity.LeaseRequestDetailScreen
import mp.app.calonex.landlord.activity.ObLeaseSpecificationScreen
import mp.app.calonex.landlord.activity.TenantLeaseRequestScreen

import mp.app.calonex.broker.adapter.PropertyFeatureAdapter
import mp.app.calonex.landlord.adapter.DescriptionParkingAdapter
import mp.app.calonex.landlord.model.*
import mp.app.calonex.landlord.response.FindLeaseResponse
import mp.app.calonex.landlord.response.SecurityFetchResponse
import mp.app.calonex.landlord.response.SignatureLeaseResponse
import mp.app.calonex.service.FeaturesService
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class UnitDescAdapterAgent(
    var context: Context,
    val pbUnit: ProgressBar,
    var activity: FragmentActivity?,
    val unitList: ArrayList<UnitDetailsPD>
) : RecyclerView.Adapter<UnitDescAdapterAgent.ViewHolder>(), Filterable {

    private var listUnit: ArrayList<UnitDetailsPD>
    private var isRenew: Boolean = false

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Log.e("keyName", "Property Details $position = is => " + Gson().toJson(listUnit[position]))

        holder.txtUnitId!!.text = "Unit #" + listUnit[position].unitNumber
        holder.txtUnitType!!.text = listUnit[position].unitType
        holder.txtUnitRent!!.text = "$" + listUnit[position].rentPerMonth
        holder.txtBathroomType!!.text = listUnit[position].bathroomTypeName
        holder.txtUnitSize!!.text = listUnit[position].squareFootArea

        holder.txtSecAmt!!.text = "$" + listUnit[position].securityAmount

        try {
            holder.txtAvlAftr!!.text = SimpleDateFormat("dd/MM/yyyy").format(Date(listUnit[position].dateOfAvailibility.toLong()))
                    .toString()
        }catch (E:Exception){
           Log.e("dateOfAvailibility",E.toString())
        }



        try {
            //Log.e("Unit Image", "File list ==> " + Gson().toJson(listUnit[position].fileList))

            if (listUnit[position].fileList.size > 0) {
                Glide.with(context)
                    .load(context.getString(R.string.base_url_img) + listUnit[position].fileList[0].fileName)
                    .placeholder(Util.customProgress(context))
                    .into(holder.imgUnit)
            } else {
                Glide.with(context)
                    .load(R.drawable.default_property)
                    .placeholder(Util.customProgress(context))
                    .into(holder.imgUnit)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


        holder.txtUnitMore.setOnClickListener {
            moreDetailDialog(holder.adapterPosition)
        }
        if (propertyDetailResponseLocal.propertyStatus.equals("4")) {
            holder.txtTenantLease!!.visibility = View.VISIBLE
            holder.imgUnitLease!!.visibility = View.GONE
        } else {
            holder.txtTenantLease!!.visibility = View.GONE
            holder.imgUnitLease!!.visibility = View.GONE
        }
        isRenew = false//listUnit[position].canRenewalLease

        if (listUnit[position].unitStatus.equals("1")) {
            // "Vacant"
            holder.txtUnitStatus.setTextColor(ContextCompat.getColor(context, R.color.black_text))
            holder.txtUnitStatus.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_vaccant_active,
                0,
                0,
                0
            )

            holder.txtUnitCXRent.setTextColor(ContextCompat.getColor(context, R.color.gray_text))
            holder.txtUnitCXRent.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_cx_rent_inactive,
                0,
                0,
                0
            )
            holder.txtUnitNonCXRent.setTextColor(ContextCompat.getColor(context, R.color.gray_text))
            holder.txtUnitNonCXRent.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_noc_cx_rent_inactive,
                0,
                0,
                0
            )


            holder.txtTenantLease!!.text = context.getString(R.string.text_add_rental)
        } else if (listUnit[position].unitStatus.equals("2")) {
            // "Non CX-Tenant"

            holder.txtUnitStatus.setTextColor(ContextCompat.getColor(context, R.color.gray_text))
            holder.txtUnitStatus.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_vaccant_inactive,
                0,
                0,
                0
            )

            holder.txtUnitCXRent.setTextColor(ContextCompat.getColor(context, R.color.gray_text))
            holder.txtUnitCXRent.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_cx_rent_inactive,
                0,
                0,
                0
            )
            holder.txtUnitNonCXRent.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.black_text
                )
            )
            holder.txtUnitNonCXRent.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_noc_cx_rent_active,
                0,
                0,
                0
            )

            holder.txtTenantLease!!.visibility=View.GONE
            holder.txtTenantLease!!.text = context.getString(R.string.onboard_tenant)
        } else {
            // "CX-Tenant"
            holder.txtUnitStatus.setTextColor(ContextCompat.getColor(context, R.color.gray_text))
            holder.txtUnitStatus.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_vaccant_inactive,
                0,
                0,
                0
            )

            holder.txtUnitCXRent.setTextColor(ContextCompat.getColor(context, R.color.black_text))
            holder.txtUnitCXRent.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_cx_rent_active,
                0,
                0,
                0
            )
            holder.txtUnitNonCXRent.setTextColor(ContextCompat.getColor(context, R.color.gray_text))
            holder.txtUnitNonCXRent.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_noc_cx_rent_inactive,
                0,
                0,
                0
            )

            if (isRenew) {
                holder.txtTenantLease!!.text = context.getString(R.string.renew_lease)
            } else {
                if (listUnit[position].canAddNewLease) {
                    //holder.txtTenantLease!!.text=context.getString(R.string.add_tenants)
                    holder.txtTenantLease!!.visibility = View.GONE
                } else {
                    holder.txtTenantLease!!.visibility = View.GONE
                }
            }


        }


        holder.txtTenantLease.setOnClickListener {

            Log.e("TAG", "Unit Details > " + Gson().toJson(listUnit.get(position)))
            if (holder.txtTenantLease.text.equals(context.getString(R.string.add_tenants)) || holder.txtTenantLease.text.equals(
                    context.getString(R.string.onboard_tenant)
                ) || holder.txtTenantLease.text.equals(context.getString(R.string.text_add_rental))
            ) {
                val intent = Intent(context, ObLeaseSpecificationScreen::class.java)
                intent.putExtra(
                    context.getString(R.string.intent_unit_no),
                    listUnit.get(position).unitNumber
                )
                intent.putExtra(
                    context.getString(R.string.intent_unit_id),
                    listUnit.get(position).propertyUnitID
                )
                intent.putExtra(
                    context.getString(R.string.intent_rent_mnth),
                    listUnit.get(position).rentPerMonth
                )
                intent.putExtra(
                    context.getString(R.string.intent_security_amt),
                    listUnit.get(position).securityAmount
                )
                intent.putExtra(
                    context.getString(R.string.intent_property_id),
                    listUnit.get(position).propertyID
                )
                intent.putExtra(context.getString(R.string.is_renew_lease), isRenew)

                intent.putExtra("MONTH FREE", listUnit.get(position).monthsFree)
                intent.putExtra("LATE FEE", listUnit.get(position).lateFee)
                intent.putExtra("DISCOUNT", listUnit.get(position).discount)
                intent.putExtra("RENT BEFORE DUE DATE", listUnit.get(position).rentBeforeDueDate)
                intent.putExtra("LANDLORD ID", propertyDetailResponseLocal.landlordId)
                intent.putExtra("PROPERTY ID", propertyDetailResponseLocal.propertyId)

                context.startActivity(intent)
                Util.navigationNext(activity!!)
            } else {
                isRenew = listUnit[position].canRenewalLease
                getLeaseList(listUnit[position].propertyUnitID)

            }


        }

        holder.imgUnitLease.setOnClickListener {
            val intent = Intent(context, TenantLeaseRequestScreen::class.java)
            intent.putExtra(
                context.getString(R.string.intent_property_add),
                propertyDetailResponseLocal.address + ", " + propertyDetailResponseLocal.city + ", " + propertyDetailResponseLocal.state +
                        ", " + propertyDetailResponseLocal.zipCode
            )
            intent.putExtra(
                context.getString(R.string.intent_property_id),
                propertyDetailResponseLocal.propertyId.toString()
            )
            intent.putExtra(
                context.getString(R.string.intent_unit_id),
                listUnit.get(position).propertyUnitID
            )
            intent.putExtra(context.getString(R.string.is_lease_history), true)

            context.startActivity(intent)
            Util.navigationNext(activity!!)
        }

        //Load Unit features

        try {
            var listDefaultFeature =
                FeaturesService.allModel?.unitFeatureListResponse?.data as java.util.ArrayList<UnitFeature>
            var listDefaultUtility =
                FeaturesService.allModel?.unitUtilitiesResponse?.data as java.util.ArrayList<UnitUtility>

            var featureListPD: ArrayList<UnitFeaturePD> = propertyDetailResponseLocal!!.unitFeatureDTO
            var utilityListPD: ArrayList<UnitUtilityPD> = propertyDetailResponseLocal!!.unitUtilitiesDTO
            var hashMapFeature: HashMap<String, Boolean> = HashMap()
            var hashMapUtility: HashMap<String, Boolean> = HashMap()

            for (item in listDefaultFeature) {

                hashMapFeature.put(item.unitFeature, false)
            }
            for (item in listDefaultUtility) {

                hashMapUtility.put(item.utilitieName, false)
            }
            if (!featureListPD.isNullOrEmpty()) {
                for (item in featureListPD) {
                    if (listUnit[position].propertyUnitID.equals(item.propertyUnitID)) {
                        if (hashMapFeature.containsKey(item.unitFeature)) {
                            hashMapFeature.put(item.unitFeature, true)
                        }
                    }
                }
            }
            if (!utilityListPD.isNullOrEmpty()) {
                for (item in utilityListPD) {
                    if (listUnit[position].propertyUnitID.equals(item.propertyUnitID)) {
                        if (hashMapUtility.containsKey(item.utilitieName)) {
                            hashMapUtility.put(item.utilitieName, true)
                        }
                    }
                }
            }
            val featureAdapter = PropertyFeatureAdapter(
                context,
                java.util.ArrayList(hashMapFeature.keys),
                java.util.ArrayList(hashMapFeature.values)
            )
            val utilityAdapter = PropertyFeatureAdapter(
                context,
                java.util.ArrayList(hashMapUtility.keys),
                java.util.ArrayList(hashMapUtility.values)
            )
            holder.rv_units_feature?.layoutManager = GridLayoutManager(context, 1)
            holder.rv_units_feature?.adapter = featureAdapter
            holder.rv_units_utility?.layoutManager = GridLayoutManager(context, 1)
            holder.rv_units_utility?.adapter = utilityAdapter
        } catch (e: Exception) {
            e.printStackTrace()
        }


        if (listUnit[position].unitStatus.equals("2")) {
            holder.layout_tenant?.visibility = View.VISIBLE

            listUnit[position].tenantFirstName?.let {
                holder.txt_ud_tenant_name?.text =
                    it + " " + listUnit[position].tenantLastName

            }
            holder.txt_ud_tenant_email.text = listUnit[position].tenantEmailId
            holder.txt_ud_tenant_phn.text = listUnit[position].tenantPhoneNumber
        } else {
            holder.layout_tenant?.visibility = View.GONE
        }

    }

    init {
        listUnit = unitList
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return listUnit.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtUnitId = itemView.txt_ud_unit_id!!
        val txtUnitType = itemView.txt_ud_unit_type!!
        val txtUnitRent = itemView.txt_ud_unit_rent!!
        val txtUnitSize = itemView.txt_ud_ft_area!!
        val txtBathroomType = itemView.txt_ud_bathrum_type!!
        val txtUnitStatus = itemView.txt_unit_status!!
        val txtUnitCXRent = itemView.txt_unit_cx_rent!!
        val txtUnitNonCXRent = itemView.txt_unit_non_cx_rent!!
        val txtUnitMore = itemView.txt_ud_more!!
        val txtTenantLease = itemView.txt_tenant_lease!!
        val imgUnitLease = itemView.img_unit_lease!!
        val imgUnit = itemView.iv_unit_image!!
        val txtSecAmt = itemView.txt_sec_amount!!
        val txtAvlAftr = itemView.txt_avail_after!!

        val rv_units_feature = itemView.rv_units_feature!!
        val rv_units_utility = itemView.rv_units_utility!!
        val layout_tenant = itemView.layout_tenant!!
        val txt_ud_tenant_name = itemView.txt_ud_tenant_name!!
        val txt_ud_tenant_email = itemView.txt_ud_tenant_email!!
        val txt_ud_tenant_phn = itemView.txt_ud_tenant_phn!!


        // val txtUnitUtility=itemView.txt_ud_unit_utility!!
    }

    private fun moreDetailDialog(position: Int) {
        //Inflate the dialog with custom view
        val mDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_unit_desc, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(context)
            .setView(mDialogView)
        mBuilder.show()

        //mDialogView.txt_unit_desc_title?.text = "Unit : " + listUnit[position].unitNumber


        var listDefaultFeature = FeaturesService.allModel?.unitFeatureListResponse?.data as java.util.ArrayList<UnitFeature>
        var listDefaultUtility =
            FeaturesService.allModel?.unitUtilitiesResponse?.data as java.util.ArrayList<UnitUtility>
        var featureListPD: ArrayList<UnitFeaturePD> = propertyDetailResponseLocal!!.unitFeatureDTO
        var utilityListPD: ArrayList<UnitUtilityPD> = propertyDetailResponseLocal!!.unitUtilitiesDTO
        var hashMapFeature: HashMap<String, Boolean> = HashMap()
        var hashMapUtility: HashMap<String, Boolean> = HashMap()
        for (item in listDefaultFeature) {

            hashMapFeature.put(item.unitFeature, false)
        }
        for (item in listDefaultUtility) {

            hashMapUtility.put(item.utilitieName, false)
        }
        if (!featureListPD.isNullOrEmpty()) {
            for (item in featureListPD) {
                if (listUnit[position].propertyUnitID.equals(item.propertyUnitID)) {
                    if (hashMapFeature.containsKey(item.unitFeature)) {
                        hashMapFeature.put(item.unitFeature, true)
                    }
                }
            }
        }
        if (!utilityListPD.isNullOrEmpty()) {
            for (item in utilityListPD) {
                if (listUnit[position].propertyUnitID.equals(item.propertyUnitID)) {
                    if (hashMapUtility.containsKey(item.utilitieName)) {
                        hashMapUtility.put(item.utilitieName, true)
                    }
                }
            }
        }
        val featureAdapter = DescriptionParkingAdapter(
            context,
            java.util.ArrayList(hashMapFeature.keys),
            java.util.ArrayList(hashMapFeature.values)
        )
        val utilityAdapter = DescriptionParkingAdapter(
            context,
            java.util.ArrayList(hashMapUtility.keys),
            java.util.ArrayList(hashMapUtility.values)
        )
        mDialogView.rv_units_feature?.layoutManager = GridLayoutManager(context, 2)
        mDialogView.rv_units_feature?.adapter = featureAdapter
        mDialogView.rv_units_utility?.layoutManager = GridLayoutManager(context, 2)
        mDialogView.rv_units_utility?.adapter = utilityAdapter
        if (listUnit[position].unitStatus.equals("2")) {
            mDialogView.layout_tenant?.visibility = View.VISIBLE
            mDialogView.txt_ud_tenant_name?.text =
                listUnit[position].tenantFirstName + " " + listUnit[position].tenantLastName
            mDialogView.txt_ud_tenant_email.text = listUnit[position].tenantEmailId
            mDialogView.txt_ud_tenant_phn.text = listUnit[position].tenantPhoneNumber
        } else {
            mDialogView.layout_tenant?.visibility = View.GONE
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(context)
            .inflate(R.layout.item_unit_description_agent, parent, false)
        return ViewHolder(v)
    }

    private fun getLeaseList(idUnit: String) {
        if (NetworkConnection.isNetworkConnected(context)) {
            //Create retrofit Service
            pbUnit!!.visibility = View.VISIBLE
            val credential = FindLeaseCredentials()

            credential.unitId = idUnit
            val findApi: ApiInterface =
                ApiClient(activity!!).provideService(ApiInterface::class.java)
            val apiCall: Observable<FindLeaseResponse> =
                findApi.findLease(credential)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", it.responseDto?.responseDescription.toString())



                    if (it.data != null) {

                        if (isRenew) {
                            for (item in it.data!!) {
                                if (item.leaseStatus.toInt() == 19) {
                                    unitLeaseInfo = item
                                    pbUnit!!.visibility = View.GONE
                                    val intent =
                                        Intent(context, ObLeaseSpecificationScreen::class.java)
                                    intent.putExtra(
                                        context.getString(R.string.is_renew_lease),
                                        isRenew
                                    )
                                    context.startActivity(intent)
                                    Util.navigationNext(activity!!)
                                }
                            }

                        } else {
                            getSignatureList(unitLeaseInfo.leaseId)
                        }

                    }

                },
                    { e ->
                        Log.e("onFailure", e.toString())
                        pbUnit!!.visibility = View.GONE
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
            pbUnit!!.visibility = View.VISIBLE
            val credential = SignatureLeaseCredentials()

            credential.leaseId = idLease
            val signatureApi: ApiInterface =
                ApiClient(activity!!).provideService(ApiInterface::class.java)
            val apiCall: Observable<SignatureLeaseResponse> =
                signatureApi.getLeaseSignature(credential)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", it.responseDto?.responseDescription.toString())

                    pbUnit!!.visibility = View.GONE

                    if (it.data != null) {
                        unitListSignature = it.data!!

                    }
                    fetchSecurityList(idLease)

                },
                    { e ->
                        Log.e("onFailure", e.toString())
                        pbUnit!!.visibility = View.GONE
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
            pbUnit!!.visibility = View.VISIBLE
            val credential = SignatureLeaseCredentials()

            credential.leaseId = idLease
            val signatureApi: ApiInterface =
                ApiClient(activity!!).provideService(ApiInterface::class.java)
            val apiCall: Observable<SecurityFetchResponse> =
                signatureApi.securityFetchInfo(credential)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", it.responseDto?.responseDescription.toString())

                    pbUnit!!.visibility = View.GONE

                    if (it.data != null) {
                        unitSecurityList = it.data!!

                    }
                    val intent = Intent(activity!!, LeaseRequestDetailScreen::class.java)
                    intent.putExtra(context.getString(R.string.intent_unit_lease), true)
                    activity!!.startActivity(intent)
                    Util.navigationNext(activity!!)

                },
                    { e ->
                        Log.e("onFailure", e.toString())
                        pbUnit!!.visibility = View.GONE
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
        var unitLeaseInfo = LeaseRequestInfo()
        var unitListSignature = ArrayList<LeaseSignature>()
        var unitSecurityList = ArrayList<FetchSecurityInfo>()
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    listUnit = unitList
                } else {
                    val resultList = ArrayList<UnitDetailsPD>()
                    for (row in unitList) {
                        Log.e(
                            "Check Unit >> ",
                            "Check =" + ("Unit #" + row.unitNumber).lowercase(Locale.getDefault()) + " with " + charSearch.lowercase(
                                Locale.getDefault()
                            )
                        )
                        Log.e(
                            "Check Avai >> ",
                            "Check =" + ("Unit #" + row.unitStatus).lowercase(Locale.getDefault()) + " with " + charSearch.lowercase(
                                Locale.getDefault()
                            )
                        )

                        if (("Unit #" + row.unitNumber).lowercase(Locale.getDefault())
                                .equals(charSearch.lowercase(Locale.getDefault())) ||
                            row.unitStatus.lowercase(Locale.getDefault())
                                .equals(charSearch.lowercase(Locale.getDefault()))
                        ) {
                            resultList.add(row)
                        }
                    }
                    listUnit = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = listUnit
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                listUnit = results?.values as ArrayList<UnitDetailsPD>
                notifyDataSetChanged()
            }
        }
    }

}