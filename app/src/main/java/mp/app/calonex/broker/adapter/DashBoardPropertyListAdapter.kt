package mp.app.calonex.broker.adapter

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
import kotlinx.android.synthetic.main.dashboard_property_list_item.view.*
import mp.app.calonex.R
import mp.app.calonex.agent.activity.PropertyDetailScreenAgent
import mp.app.calonex.broker.activity.AgentAssenedActivity
import mp.app.calonex.common.apiCredentials.FetchDocumentCredential
import mp.app.calonex.common.apiCredentials.GetPropertyByIdApiCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.model.BrokerAllProperties
import mp.app.calonex.landlord.model.FetchDocumentModel
import mp.app.calonex.landlord.model.PropertyDetail
import mp.app.calonex.landlord.response.FetchDocumentResponse
import mp.app.calonex.landlord.response.PropertyDetailResponse
import kotlin.collections.ArrayList

class DashBoardPropertyListAdapter(
    var context: Context,
    var activity: FragmentActivity?,
    var list: ArrayList<BrokerAllProperties>,
    var pb_detail: ProgressBar

    ) : RecyclerView.Adapter<DashBoardPropertyListAdapter.ViewHolder>() {

    private var propertylist = ArrayList<BrokerAllProperties>()

    init {
        propertylist = list
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var Bp = propertylist.get(position);
        if (Bp.propertyAddress == null || TextUtils.isEmpty(Bp.propertyAddress)) {
            holder.locations.text = "NA"
        } else {
            holder.locations.text = Bp.propertyAddress
        }
        if (Bp.propertyEmail == null || TextUtils.isEmpty(Bp.propertyEmail)) {
            holder.email.text = "NA"
        } else {
            holder.email.text = Bp.propertyEmail
        }
        if (Bp.propertyContact == null || TextUtils.isEmpty(Bp.propertyContact)) {
            holder.phone.text = "NA"
        } else {
            holder.phone.text = Bp.propertyContact
        }


        if (Kotpref.userRole.contains("Landlord")) {
            if (Bp.propertyOwnerName == null || TextUtils.isEmpty(Bp.propertyOwnerName)) {
                holder.name.text = "NA"
            } else {
                holder.name.text = Bp.propertyOwnerName
            }
        } else {
            if (Bp.propertyOwnerName == null || TextUtils.isEmpty(Bp.propertyOwnerName)) {
                holder.name.text = "NA"
            } else {
                holder.name.text = Bp.propertyOwnerName
            }
        }

        Glide.with(context)
            .load(context.getString(R.string.base_url_img2) + Bp.propertyImgURI)
            .placeholder(R.drawable.bg_default_property)
            .into(holder.image);


        holder.viewassignedagent.setOnClickListener {
            val intent = Intent(context, AgentAssenedActivity::class.java)
            intent.putExtra("propertyId", Bp.propertyId)
            intent.putExtra("brokerId", Kotpref.userId)

            activity!!.startActivity(intent)
        }


        holder.ll_parent_view.setOnClickListener {
            getPropertyById(Bp.propertyId)
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
        val viewassignedagent = itemView.viewassignedagent!!
        val ll_parent_view = itemView.ll_parent_view!!

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(context)
            .inflate(R.layout.dashboard_property_list_item, parent, false)

        return ViewHolder(v)
    }


    private fun getPropertyById(propertyId: String?) {
        activity!!.window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        pb_detail.visibility = View.VISIBLE
        var credential = GetPropertyByIdApiCredential()
        credential.propertyId = propertyId!!.toLong()

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

                    propertyDetailResponseHome = propertyDetail.data!!
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
                        listPropertyImagesHome = it.data!!
                        pb_detail.visibility = View.GONE
                        activity!!.window
                            .clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        val intent = Intent(context, PropertyDetailScreenAgent::class.java)
                        intent.putExtra("from","dashboard")


                        context.startActivity(intent)
                        Util.navigationNext(activity!!)
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

    companion object {
        var propertyDetailResponseHome = PropertyDetail()
        var listPropertyImagesHome = ArrayList<FetchDocumentModel>()
        //var leasePropertyList= ArrayList<LeaseRequestInfo>()
    }


}