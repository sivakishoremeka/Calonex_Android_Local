package mp.app.calonex.agent.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Filter
import android.widget.Filterable
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.item_property_agent.view.*
import mp.app.calonex.R
import mp.app.calonex.agent.activity.PropertyDetailScreenAgent
import mp.app.calonex.agent.model.OnboardPropertyAgent
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


class OnboardPropertyListAdapterAgent(
    var context: Context,
    var activity: FragmentActivity?,
    var pb_detail: ProgressBar,
    var list: ArrayList<OnboardPropertyAgent>
) : RecyclerView.Adapter<OnboardPropertyListAdapterAgent.ViewHolder>(), Filterable {
    private var listProperty = ArrayList<OnboardPropertyAgent>()

    init {
        listProperty = list
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtPropertyAdd.text =
            listProperty[position].address + ", " + listProperty[position].city + ", " + listProperty[position].state +
                    ", " + listProperty[position].zipCode + "\n" + listProperty[position].broker

        //holder.txtTenantReq.text = "Tenant Requests (" + listProperty[position].tenantRequest.toString() + ")"

        holder.txtRevenue.text = listProperty[position].unitsVacants.toString()
        holder.txtPropertyUnits.text = listProperty[position].unitsOccupied.toString()
        holder.txtUnitsOccupied.text = listProperty[position].tenantApproved.toString()

        val propertyStatus = listProperty[position].propertyStatusID

        /*
        Property Status:
        1 -> Pending
        2 -> Active
        3 -> Rejected
        4 -> APPROVED
         */
        if (propertyStatus.equals("1")) {
            holder.viewPropertyStatus.setBackgroundColor(Color.GRAY)
        } else if (propertyStatus.equals("2")) {
            holder.viewPropertyStatus.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.colorGreen
                )
            )
        } else if (propertyStatus.equals("3")) {
            holder.viewPropertyStatus.setBackgroundColor(Color.RED)
        } else if (propertyStatus.equals("4")) {
            holder.viewPropertyStatus.setBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.colorGreen
                )
            )
        } else if (propertyStatus.equals("5")) {
            holder.viewPropertyStatus.setBackgroundColor(Color.GRAY)
        }

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
        val txtPropertyAdd = itemView.txt_property_add!!
        val txtTenantReq = itemView.txt_tenant_req!!
        val txtRevenue = itemView.txt_revenue!!
        val txtUnitsOccupied = itemView.txt_unit_occupied!!
        val txtPropertyUnits = itemView.txt_property_units!!
        val viewPropertyStatus = itemView.view_property_status!!
        val layoutPropertyView = itemView.layout_property_view!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(context).inflate(R.layout.item_property_agent, parent, false)

        return ViewHolder(v)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    listProperty = list
                } else {
                    val resultList = ArrayList<OnboardPropertyAgent>()
                    for (row in list) {
                        if (row.address.lowercase(Locale.getDefault())
                                .contains(charSearch.lowercase(Locale.getDefault())) || row.city.lowercase(Locale.getDefault())
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
                listProperty = results?.values as ArrayList<OnboardPropertyAgent>
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
                Log.e("onSuccess", "userDetail.firstName")
                if (propertyDetail.data != null) {

                    propertyDetailResponse = propertyDetail.data!!
                    fetchImages(propertyId.toString())
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
                        activity!!.window
                            .clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        val intent = Intent(context, PropertyDetailScreenAgent::class.java)



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