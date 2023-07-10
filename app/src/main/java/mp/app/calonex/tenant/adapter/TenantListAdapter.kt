package mp.app.calonex.tenant.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.child_tenant_list_for_lease.view.*
import kotlinx.android.synthetic.main.item_tenant_list.view.*
import mp.app.calonex.R
import mp.app.calonex.landlord.model.TenantInfoPayload

class TenantListAdapter(
    var context: Context,
    val list: ArrayList<TenantInfoPayload>,
    var updateTenantList: Boolean
) :
    RecyclerView.Adapter<TenantListAdapter.ViewHolder>() {


    var tenantList = ArrayList<TenantInfoPayload>()
    var updateTenant: Boolean

    init {
        tenantList = list
        updateTenant = updateTenantList
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.tv_tenant_name.text =
            tenantList[position].tenantFirstName + " " + tenantList[position].tenantLastName
        holder.tv_rent_percentage.text = tenantList[position].rentPercentage + "%"
        holder.tv_rent_amount.text = "$" + tenantList[position].rentAmount
        if (tenantList[position].coSignerFlag!!) {
            holder.tv_co_signer.text = "" + context.getString(R.string.text_tenants)
        } else {
            holder.tv_co_signer.text = ""
        }

        if ((tenantList.size - 1) == position) {
            holder.view_separator.visibility = View.GONE
        } else {
            holder.view_separator.visibility = View.VISIBLE
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_tenant_name = itemView.tv_tenant_name!!
        val tv_rent_percentage = itemView.tv_rent_percentage!!
        val tv_rent_amount = itemView.tv_rent_amount!!
        val tv_co_signer = itemView.tv_co_signer!!
        val view_separator = itemView.view_separator!!


        /*val tenantName = itemView.tenant_list_name!!
        val rent = itemView.tenant_list_rent!!
        val security = itemView.tenant_list_security!!
        val rent_percent = itemView.tenant_list_rent_percent!!
        val approval = itemView.tenant_list_landlord_approval!!
        val leaseSignStatus = itemView.tenant_list_lease_signing_status!!
        val edit = itemView.tenant_list_edit!!
        val remove = itemView.tenant_list_remove!!
        val action_view = itemView.tenant_list_edit_action_view
        val co_signer_Switch = itemView.tenant_list_is_co_signer
        val co_tenant_Switch = itemView.tenant_list_is_co_tenant*/


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //var v = LayoutInflater.from(context).inflate(R.layout.item_tenant_list, parent, false)
        var v = LayoutInflater.from(context)
            .inflate(R.layout.child_tenant_list_for_lease, parent, false)
        return ViewHolder(v)
    }


}