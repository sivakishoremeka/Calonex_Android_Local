package mp.app.calonex.agent.adapter

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
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.item_property_broker.view.iv_property_image
import kotlinx.android.synthetic.main.item_property_broker.view.layout_property_view
import kotlinx.android.synthetic.main.item_property_broker.view.txt_non_cx_unit
import kotlinx.android.synthetic.main.item_property_broker.view.txt_property_add
import kotlinx.android.synthetic.main.item_property_broker.view.txt_property_owner
import kotlinx.android.synthetic.main.item_property_broker.view.txt_property_status
import kotlinx.android.synthetic.main.item_property_broker.view.txt_property_units
import kotlinx.android.synthetic.main.item_property_broker.view.txt_revenue
import kotlinx.android.synthetic.main.item_property_broker.view.txt_tenant_req
import kotlinx.android.synthetic.main.item_property_broker.view.txt_unit_occupied
import kotlinx.android.synthetic.main.item_property_broker.view.view_property_status
import mp.app.calonex.R
import mp.app.calonex.agent.activity.PropertyDetailScreenAgent
import mp.app.calonex.agent.model.PropertyAgent
import mp.app.calonex.common.apiCredentials.FetchDocumentCredential
import mp.app.calonex.common.apiCredentials.GetPropertyByIdApiCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util.navigationNext
import mp.app.calonex.landlord.activity.TenantLeaseRequestScreen
import mp.app.calonex.landlord.model.FetchDocumentModel
import mp.app.calonex.landlord.model.PropertyDetail
import mp.app.calonex.landlord.response.FetchDocumentResponse
import mp.app.calonex.landlord.response.PropertyDetailResponse
import java.util.*
import kotlin.collections.ArrayList


class PropertyListAdapterAgent(
    var context: Context,
    var activity: FragmentActivity?,
    var pb_detail: ProgressBar,
    var list: ArrayList<PropertyAgent>
) : RecyclerView.Adapter<PropertyListAdapterAgent.ViewHolder>(), Filterable {
    private var listProperty = ArrayList<PropertyAgent>()

    init {
        listProperty = list
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtPropertyAdd.text =
            listProperty[position].address + ", " + listProperty[position].city + ", " + listProperty[position].state +
                    ", " + listProperty[position].zipCode + "\n" + listProperty[position].broker

        //holder.txtTenantReq.text = "Tenant Requests (" + listProperty[position].tenantRequest.toString() + ")"

        holder.txt_property_owner.text = listProperty[position].landlordFullName.toString()

        holder.txtRevenue.text = listProperty[position].numberOfUnits.toString()


//Added 02-03-2023
        holder.txtPropertyUnits.text =
            listProperty[position].unitsOccupiedInsideCX.toString() + "(cx)"
        holder.txt_NonCXUnit.text =
            listProperty[position].unitsOccupiedOutsideCX.toString() + "(non cx)"

//        holder.txtUnitsOccupied.text = listProperty[position].tenantApproved.toString()

        holder.txtUnitsOccupied.text = listProperty[position].unitsVacants.toString()
        //"${listProperty[position].numberOfUnits.toInt() - listProperty[position].unitsOccupied}"

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
                for(i in 0 until listProperty[position].fileList.size){
                    if(listProperty[position].fileList[i].uploadFileType==context.getString(R.string.key_img_property)){
                        propertyDocPath = listProperty[position].fileList[i].fileName
                    }
                }
                if(propertyDocPath.isNotEmpty()) {
                    Glide.with(context)
                        .load(context.getString(R.string.base_url_img2) + propertyDocPath)
                        .placeholder(context.getDrawable(R.drawable.bg_default_property))
                        .into(holder.iv_property_image)
                }else{
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
        /*try {
            if (listProperty[position].fileList.isNotEmpty()) {
                Glide.with(context)
                    .load(context.getString(R.string.base_url_img2) + listProperty[position].fileList[0].fileName)
                    .placeholder(context.getDrawable(R.drawable.bg_default_property))
                    .into(holder.iv_property_image)
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
        }*/

        holder.txtTenantReq.setOnClickListener {
            val intent = Intent(context, TenantLeaseRequestScreen::class.java)
            intent.putExtra(
                context.getString(R.string.intent_property_add),
                listProperty[holder.adapterPosition].address + ", " + listProperty[holder.adapterPosition].city + ", " + listProperty[holder.adapterPosition].state +
                        ", " + listProperty[holder.adapterPosition].zipCode
            )
            intent.putExtra(
                context.getString(R.string.intent_property_id),
                listProperty[holder.adapterPosition].propertyId.toString()
            )
            context.startActivity(intent)
            navigationNext(activity!!)
        }

        holder.layoutPropertyView.setOnClickListener {
            // Navigate to property Detail Scree
            getPropertyById(listProperty[holder.adapterPosition].propertyId)

        }
    }


    override fun getItemCount(): Int {
        return listProperty.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txt_property_owner = itemView.txt_property_owner!!
        val txtPropertyAdd = itemView.txt_property_add!!
        val txtTenantReq = itemView.txt_tenant_req!!
        val txtRevenue = itemView.txt_revenue!!
        val txtUnitsOccupied = itemView.txt_unit_occupied!!
        val txt_NonCXUnit = itemView.txt_non_cx_unit!!
        val txtPropertyUnits = itemView.txt_property_units!!
        val viewPropertyStatus = itemView.view_property_status!!
        val iv_property_image = itemView.iv_property_image!!
        val txt_property_status = itemView.txt_property_status!!
        val layoutPropertyView = itemView.layout_property_view!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(context).inflate(R.layout.item_property_broker, parent, false)

        return ViewHolder(v)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    listProperty = list
                } else {
                    val resultList = ArrayList<PropertyAgent>()
                    for (row in list) {
                        if (row.address.lowercase(Locale.getDefault())
                                .contains(charSearch.lowercase(Locale.getDefault())) || row.city.lowercase(
                                Locale.getDefault()
                            )
                                .contains(charSearch.lowercase(Locale.getDefault())) ||
                            row.state.lowercase(Locale.getDefault())
                                .contains(charSearch.lowercase(Locale.getDefault())) || row.zipCode.lowercase(
                                Locale.getDefault()
                            )
                                .contains(charSearch.lowercase(Locale.getDefault()))
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
                listProperty = results?.values as ArrayList<PropertyAgent>
                notifyDataSetChanged()
            }
        }
    }

    private fun getPropertyById(propertyId: Long?) {
        activity!!.window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        pb_detail.visibility = View.VISIBLE
        var credential = GetPropertyByIdApiCredential()
        credential.propertyId = propertyId

        val propertyDetails: ApiInterface =
            ApiClient(context).provideService(ApiInterface::class.java)
        val apiCall: Observable<PropertyDetailResponse> =
            propertyDetails.getPropertyById(credential) //Test API Key

        RxAPICallHelper().call(apiCall, object : RxAPICallback<PropertyDetailResponse> {
            override fun onSuccess(propertyDetail: PropertyDetailResponse) {
                Log.e("Get", "property Detail >> " + Gson().toJson(propertyDetail))
                activity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                pb_detail.visibility = View.GONE
                if (propertyDetail.data != null) {

                    propertyDetailResponse = propertyDetail.data!!
                    fetchImages(propertyId.toString())
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
                pb_detail.visibility = View.GONE
                Log.e("onFailure", t.toString())
                //  progressBarList?.visibility = View.GONE
            }
        })
    }

    private fun fetchImages(propertyId: String) {
        if (NetworkConnection.isNetworkConnected(context)) {
            activity!!.window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            pb_detail.visibility = View.VISIBLE
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
                        pb_detail.visibility = View.GONE
                        activity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        val intent = Intent(context, PropertyDetailScreenAgent::class.java)
                        intent.putExtra("from","propertylistAdapter")
                        context.startActivity(intent)
                        navigationNext(activity!!)
                    } else {
                        pb_detail.visibility = View.GONE
                        activity!!.window
                            .clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        Toast.makeText(context, it.responseDto!!.message, Toast.LENGTH_SHORT).show()
                    }
                },
                    { e ->
                        Log.e("onFailure", e.toString())
                        pb_detail.visibility = View.GONE
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

    /*private fun getLeaseList(propertyId: String) {
        if (NetworkConnection.isNetworkConnected(context)) {
            //Create retrofit Service
            pb_detail!!.visibility = View.VISIBLE
            val credential = FindLeaseCredentials()
            credential.propertyId=propertyId
            credential.userId = Kotpref.userId
            credential.userRole = Kotpref.userRole
            val findApi: ApiInterface = ApiClient(context).provideService(ApiInterface::class.java)
            val apiCall: Observable<FindLeaseResponse> =
                findApi.findLease(credential)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
                Log.e("onSuccess", it.responseDto?.responseDescription.toString())

                pb_detail!!.visibility = View.GONE

                if (it.data != null) {
                    leasePropertyList=it.data!!
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
            Toast.makeText(context, context.getString(R.string.error_network), Toast.LENGTH_SHORT).show()
        }
    }*/
    companion object {
        var propertyDetailResponse = PropertyDetail()
        var listPropertyImages = ArrayList<FetchDocumentModel>()
        //var leasePropertyList= ArrayList<LeaseRequestInfo>()
    }

}