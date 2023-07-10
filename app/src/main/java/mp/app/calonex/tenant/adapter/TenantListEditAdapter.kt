package mp.app.calonex.tenant.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_tenant_list.view.*
import mp.app.calonex.R
import mp.app.calonex.common.constant.StatusConstant
import mp.app.calonex.landlord.model.TenantInfoPayload
import mp.app.calonex.tenant.activity.AddTenantActivity
import mp.app.calonex.tenant.activity.RentDistributionTenantActivity
import mp.app.calonex.tenant.activity.TenantListActivity.Companion.tenantListTemporary
import mp.app.calonex.tenant.activity.TenantListEditActivity.Companion.isEdited
import mp.app.calonex.tenant.activity.TenantListEditActivity.Companion.isRemoved

class TenantListEditAdapter(
    var context: Context,
    val list: ArrayList<TenantInfoPayload>,
    var updateTenantList: Boolean
) :
    RecyclerView.Adapter<TenantListEditAdapter.ViewHolder>() {


    var tenantList = ArrayList<TenantInfoPayload>()
    var updateTenant: Boolean

    init {
        tenantList = list
        updateTenant = updateTenantList
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        var name  : String = tenantList[position].tenantFirstName + " "+ tenantList[position].tenantLastName

        if(tenantList[position].role?.contains("Primary",true)!!){
            holder.action_view.visibility = View.GONE
            holder.tenantName.text = name.plus(" (Primary)")
        } else {
            holder.action_view.visibility = View.VISIBLE
            holder.tenantName.text = name
        }

        holder.rent.text = "$"+tenantList[position].rentAmount
        holder.rent_percent.text = tenantList[position].rentPercentage+"%"
        holder.security.text = "$"+tenantList[position].securityAmount

        holder.approval.text = StatusConstant.innerLandLordApprovalStatus(tenantList[position].landlordApprovalStatus)

        holder.leaseSignStatus.text = StatusConstant.globalStatus(tenantList[position].leaseSigningStatus)


        if (tenantList[position].coSignerFlag!!) {
            holder.co_signer_Switch.isChecked = true
        }
        if (tenantList[position].coTenantFlag!!) {
            holder.co_tenant_Switch.isChecked = true
        }

        holder.remove.setOnClickListener {

            val builder = AlertDialog.Builder(context)

            builder.setTitle("Remove Tenant")

            // Display a message on alert dialog
            builder.setMessage("Are you sure you want to remove this tenant?")

            // Set a positive button and its click listener on alert dialog
            builder.setPositiveButton("OK") { dialog, which ->
                isRemoved = true
                isEdited = true
                tenantListTemporary.removeAt(position)
//                tenantList.removeAt(position)
                notifyDataSetChanged()

                val intent = Intent(context, RentDistributionTenantActivity::class.java)
                context.startActivity(intent)
            }


            // Display a negative button on alert dialog
            builder.setNegativeButton("No") { dialog, which ->
                dialog.cancel()
                dialog.dismiss()
            }


            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
        holder.edit.setOnClickListener {
            val intent = Intent(context, AddTenantActivity::class.java)
            intent.putExtra("editTenant", true)
            intent.putExtra("tenantInfo", tenantListTemporary[position])
            intent.putExtra("position", position)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tenantName = itemView.tenant_list_name!!
        val rent = itemView.tenant_list_rent!!
        val security = itemView.tenant_list_security!!
        val rent_percent = itemView.tenant_list_rent_percent!!
        val approval = itemView.tenant_list_landlord_approval!!
        val leaseSignStatus = itemView.tenant_list_lease_signing_status!!
        val edit = itemView.tenant_list_edit!!
        val remove = itemView.tenant_list_remove!!
        val action_view = itemView.tenant_list_edit_action_view
        val co_signer_Switch = itemView.tenant_list_is_co_signer
        val co_tenant_Switch = itemView.tenant_list_is_co_tenant


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(context).inflate(R.layout.item_tenant_list, parent, false)
        return ViewHolder(v)
    }


}