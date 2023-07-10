package mp.app.calonex.agent.adapter

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
import kotlinx.android.synthetic.main.agent_dashboard_property_list_item.view.*
import kotlinx.android.synthetic.main.agent_dashboard_property_list_item.view.email
import kotlinx.android.synthetic.main.agent_dashboard_property_list_item.view.locations
import kotlinx.android.synthetic.main.agent_dashboard_property_list_item.view.name
import kotlinx.android.synthetic.main.agent_dashboard_property_list_item.view.phone
import kotlinx.android.synthetic.main.agent_dashboard_property_list_item.view.property_image
import kotlinx.android.synthetic.main.agent_dashboard_property_list_item.view.viewassignedagent

import mp.app.calonex.R
import mp.app.calonex.agent.activity.PropertyDetailScreenAgent
import mp.app.calonex.agent.model.PropertyAgent
import mp.app.calonex.broker.activity.AgentAssenedActivity
import mp.app.calonex.broker.adapter.DashBoardPropertyListAdapter.Companion.listPropertyImagesHome
import mp.app.calonex.broker.adapter.DashBoardPropertyListAdapter.Companion.propertyDetailResponseHome
import mp.app.calonex.common.apiCredentials.FetchDocumentCredential
import mp.app.calonex.common.apiCredentials.GetPropertyByIdApiCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.response.FetchDocumentResponse
import mp.app.calonex.landlord.response.PropertyDetailResponse


class AgentDashboardPropertyAdepter (
    var context: Context,
    var activity: FragmentActivity?,
    var list: ArrayList<PropertyAgent>,
    var pb_detail: ProgressBar
) : RecyclerView.Adapter<AgentDashboardPropertyAdepter.ViewHolder>() {

    private var propertylist = ArrayList<PropertyAgent>()

    init {
        propertylist = list
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var Bp = propertylist.get(position);
         if (Bp.landLordName==null || TextUtils.isEmpty(Bp.landLordName)){
             holder.name.text="NA"
         }else{
             holder.name.text=Bp.landLordName
         }
        if (Bp.address == null || TextUtils.isEmpty(Bp.address)) {
            holder.locations.text = "NA"
        } else {
            holder.locations.text = Bp.address
        }
        if (Bp.brokerEmailID == null || TextUtils.isEmpty(Bp.brokerEmailID)) {
            holder.email.text = "NA"
        } else {
            holder.email.text = Bp.brokerEmailID
        }
        if (Bp.primaryContact == null || TextUtils.isEmpty(Bp.primaryContact)) {
            holder.phone.text = "NA"
        } else {
            holder.phone.text = Bp.primaryContact
        }

        if (Bp.landlordFullName == null || TextUtils.isEmpty(Bp.landlordFullName)) {
            holder.name.text = "NA"
        } else {
            holder.name.text = Bp.landlordFullName
        }
          //need to check for image
        try {
            if (Bp.fileList.get(0).uploadFileType.equals(context.getString(R.string.key_img_property))){
                Glide.with(context)
                    .load(context.getString(R.string.base_url_img2)+Bp.fileList.get(0).fileName )
                    .placeholder(R.drawable.bg_default_property)
                    .into(holder.image);
            }
        }catch (e:Exception){
            Glide.with(context)
                .load(R.drawable.bg_default_property)
                .placeholder(R.drawable.bg_default_property)
                .into(holder.image);
        }




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
    private fun getPropertyById(propertyId: Long?) {
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
                        activity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
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
            .inflate(R.layout.agent_dashboard_property_list_item, parent, false)

        return ViewHolder(v)
    }


}