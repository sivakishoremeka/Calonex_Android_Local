package mp.app.calonex.landlord.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Filter
import android.widget.Filterable
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.item_property.view.*
import kotlinx.android.synthetic.main.item_property.view.layout_property_view
import kotlinx.android.synthetic.main.item_property.view.txt_property_add
import kotlinx.android.synthetic.main.item_property.view.txt_property_status
import kotlinx.android.synthetic.main.item_property.view.txt_property_units
import kotlinx.android.synthetic.main.item_property.view.txt_revenue
import kotlinx.android.synthetic.main.item_property.view.txt_tenant_req
import kotlinx.android.synthetic.main.item_property.view.txt_unit_occupied
import kotlinx.android.synthetic.main.item_property.view.view_property_status
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.FetchDocumentCredential
import mp.app.calonex.common.apiCredentials.FindLeaseCredentials
import mp.app.calonex.common.apiCredentials.GetPropertyByIdApiCredential
import mp.app.calonex.common.network.*
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util.navigationNext
import mp.app.calonex.landlord.activity.ApplicantTenantActivity
import mp.app.calonex.landlord.activity.PropertyDetailScreen
import mp.app.calonex.landlord.activity.TenantLeaseRequestScreen
import mp.app.calonex.landlord.model.FetchDocumentModel
import mp.app.calonex.landlord.model.LeaseRequestInfo
import mp.app.calonex.landlord.model.Property
import mp.app.calonex.landlord.model.PropertyDetail
import mp.app.calonex.landlord.response.FetchDocumentResponse
import mp.app.calonex.landlord.response.FindLeaseResponse
import mp.app.calonex.landlord.response.PropertyDetailResponse


class PropertyListAdapter(
    var context: Context,
    var activity: FragmentActivity?,
    var pb_detail: ProgressBar,
    var list: ArrayList<Property>
) : RecyclerView.Adapter<PropertyListAdapter.ViewHolder>(),
    Filterable {


    private var listProperty = ArrayList<Property>()

    init {
        listProperty = list
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.txtPropertyAdd.text =
            listProperty[position].address + ", " + listProperty[position].city + ", " + listProperty[position].state +
                    ", " + listProperty[position].zipCode + "\n" + listProperty[position].broker

        /*holder.txtTenantReq.text =
            "View Applicants (" + listProperty[position].tenantRequest.toString() + ")"
        */

        holder.txt_property_owner.text = listProperty[position].ownerName.toString()

        holder.txtRevenue.text = listProperty[position].numberOfUnits.toString()


        //Added 02-03-2023
        holder.txtPropertyUnits.text =
            listProperty[position].unitsOccupiedInsideCX.toString() + "(cx)"
        holder.txt_NonCXUnit.text =
            listProperty[position].unitsOccupiedOutsideCX.toString() + "(non cx)"

        holder.txtUnitsOccupied.text = listProperty[position].unitsVacants.toString()
        val propertyStatus = listProperty[position].propertyStatusID

        /*
        Property Status:
        1 -> Pending
        2 -> Active
        3 -> Rejected
        4 -> APPROVED
         */
        if (propertyStatus.equals("1")) {
            //holder.viewPropertyStatus.setBackgroundColor(Color.GRAY)
            holder.viewPropertyStatus.setBackgroundResource(R.drawable.ic_property_in_active)
            holder.txt_property_status.text = "Pending"
        } else if (propertyStatus.equals("2")) {
            holder.viewPropertyStatus.setBackgroundResource(R.drawable.ic_property_active)
            holder.txt_property_status.text = "Active"
            /*holder.viewPropertyStatus.setBackgroundColor(
                               ContextCompat.getColor(
                                   context,
                                   R.color.colorGreen
                               )
                           )*/
        } else if (propertyStatus.equals("3")) {
            //holder.viewPropertyStatus.setBackgroundColor(Color.RED)
            holder.txt_property_status.text = "In active"
            holder.viewPropertyStatus.setBackgroundResource(R.drawable.ic_property_in_active)
        } else if (propertyStatus.equals("4")) {
            /*holder.viewPropertyStatus.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.colorGreen
                )
            )*/
            holder.txt_property_status.text = "Active"
            holder.viewPropertyStatus.setBackgroundResource(R.drawable.ic_property_active)
        } else if (propertyStatus.equals("5")) {
            //holder.viewPropertyStatus.setBackgroundColor(Color.GRAY)
            holder.txt_property_status.text = "In active"
            holder.viewPropertyStatus.setBackgroundResource(R.drawable.ic_property_in_active)
        }

        try {
            if (listProperty[position].fileList.isNotEmpty()) {
                var propertyDocPath = ""
                for (i in 0 until listProperty[position].fileList.size) {
                    if (listProperty[position].fileList[i].uploadFileType == context.getString(R.string.key_img_property)) {
                        propertyDocPath = listProperty[position].fileList[i].fileName
                    }
                }
                if (propertyDocPath.isNotEmpty()) {
                    Glide.with(context)
                        .load(context.getString(R.string.base_url_img2) + propertyDocPath)
                        .placeholder(context.getDrawable(R.drawable.bg_default_property))
                        .into(holder.iv_property_image)
                } else {
                    Glide.with(context)
                        .load(context.getDrawable(R.drawable.bg_default_property))
                        .placeholder(context.getDrawable(R.drawable.bg_default_property))
                        .into(holder.iv_property_image)
                }
            } else {
                Glide.with(context)
                    .load(context.getDrawable(R.drawable.bg_default_property))
                    .placeholder(context.getDrawable(R.drawable.bg_default_property))
                    .into(holder.iv_property_image)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Glide.with(context)
                .load(context.getDrawable(R.drawable.bg_default_property))
                .placeholder(context.getDrawable(R.drawable.bg_default_property))
                .into(holder.iv_property_image)
        }


        holder.txtTenantReq.setOnClickListener {
            val intent = Intent(context, ApplicantTenantActivity::class.java)
            Kotpref.propertyId = listProperty[holder.adapterPosition].propertyId.toString()
            activity!!.startActivity(intent)

        }

        holder.layoutPropertyView.setOnClickListener {
            // Navigate to property Detail Scree
            getPropertyById(
                listProperty[holder.adapterPosition].propertyId,
                listProperty[holder.adapterPosition].tenantFirstName + " " + listProperty[holder.adapterPosition].tenantLastName,
                listProperty[holder.adapterPosition].tenantEmailId
            )
        }
    }


    override fun getItemCount(): Int {
        return listProperty.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtPropertyAdd = itemView.txt_property_add!!
        val txtTenantReq = itemView.txt_tenant_req!!
        val txtRevenue = itemView.txt_revenue!!
        val txt_property_owner = itemView.txt_property_owner!!
        val txtUnitsOccupied = itemView.txt_unit_occupied!!
        val txtPropertyUnits = itemView.txt_property_units!!
        val txt_NonCXUnit = itemView.txt_non_cx_unit!!
        val viewPropertyStatus = itemView.view_property_status!!
        val txt_property_status = itemView.txt_property_status!!
        val iv_property_image = itemView.iv_property_image!!

        val layoutPropertyView = itemView.layout_property_view!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(context).inflate(R.layout.item_property, parent, false)

        return ViewHolder(v)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    listProperty = list
                } else {
                    val resultList = ArrayList<Property>()
                    for (row in list) {
                        if (row.address!!.toLowerCase()
                                .contains(charSearch.toLowerCase()) || row.city!!.toLowerCase()
                                .contains(charSearch.toLowerCase()) ||
                            row.state!!.toLowerCase()
                                .contains(charSearch.toLowerCase()) || row.zipCode!!.toLowerCase()
                                .contains(charSearch.toLowerCase())
                        ) {
                            resultList.add(row)
                        }
                    }
                    listProperty = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = listProperty
                return filterResults
            }


            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                listProperty = results?.values as ArrayList<Property>
                notifyDataSetChanged()
            }

        }
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

                    propertyDetailResponse = propertyDetail.data!!
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
                        listPropertyImages = it.data!!
                        pb_detail!!.visibility = View.GONE
                        activity!!.getWindow()
                            .clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        val intent = Intent(context, PropertyDetailScreen::class.java)
                        intent.putExtra("from","propertyList")
                        intent.putExtra("tenantName", tenantName)
                        intent.putExtra("tenantEmail", tenantEmail)
                        context.startActivity(intent)
                        navigationNext(activity!!)


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

    private fun getLeaseList(propertyId: String) {
        if (NetworkConnection.isNetworkConnected(context)) {
            //Create retrofit Service
            pb_detail!!.visibility = View.VISIBLE
            val credential = FindLeaseCredentials()
            credential.propertyId = propertyId
            credential.userId = Kotpref.userId
            credential.userRole = Kotpref.userRole
            val findApi: ApiInterface = ApiClient(context).provideService(ApiInterface::class.java)
            val apiCall: Observable<FindLeaseResponse> =
                findApi.findLease(credential)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", it.responseDto?.responseDescription.toString())

                    pb_detail!!.visibility = View.GONE

                    if (it.data != null) {
                        leasePropertyList = it.data!!
                    }
                    pb_detail!!.visibility = View.GONE
                    activity!!.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    val intent = Intent(context, TenantLeaseRequestScreen::class.java)
                    context.startActivity(intent)
                    navigationNext(activity!!)

                },
                    { e ->
                        Log.e("onFailure", e.toString())
                        pb_detail!!.visibility = View.GONE
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
        var propertyDetailResponse = PropertyDetail()
        var listPropertyImages = ArrayList<FetchDocumentModel>()
        var leasePropertyList = ArrayList<LeaseRequestInfo>()
    }

}