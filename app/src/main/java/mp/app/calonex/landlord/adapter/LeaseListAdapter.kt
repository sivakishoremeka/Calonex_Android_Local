package mp.app.calonex.landlord.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_lease_list.view.*
import mp.app.calonex.R
import mp.app.calonex.common.constant.StatusConstant
import mp.app.calonex.common.constant.StatusConstant.Companion.globalStatus
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.tenant.activity.*
import mp.app.calonex.tenant.activity.LeaseListActivity.Companion.leaseItem
import mp.app.calonex.tenant.model.LeaseTenantInfo

class LeaseListAdapter(var context: Context, val leaseList: ArrayList<LeaseTenantInfo>) :
    RecyclerView.Adapter<LeaseListAdapter.ViewHolder>() {


    var listLease = ArrayList<LeaseTenantInfo>()

    init {
        listLease = leaseList
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.address.text = listLease[position].propertyAddress+","+listLease[position].propertyCity+","+listLease[position].propertyState
        holder.landlord_name.text = listLease[position].landlordName
        holder.rent_amount.text = listLease[position].rentAmount
        holder.unti_number.text = listLease[position].unitNumber
        holder.view_status.text = globalStatus(listLease[position].leaseStatus)
        holder.img_app

        if (listLease.size == 1) {
            Kotpref.propertyId = listLease[position].propertyId
            Kotpref.leaseId = listLease[position].leaseId
            Kotpref.unitNumber = listLease[position].unitNumber
            Kotpref.unitId = listLease[position].unitId
            Kotpref.landlordId = listLease[position].landlordId

          /*  if (!listLease[holder]isNullOrEmpty()) {
                Glide.with(applicationContext)
                    .load(getString(R.string.base_url_img2) + valLicenceFront)
                    .placeholder(customPb)
                    .into(imgLdFrontLicence!!)
                Log.d("TAG", "initComponent: url is: "+getString(R.string.base_url_img2) + valLicenceFront)
            }*/


            if (leaseList[position].leaseStatus == StatusConstant.FINALIZED) {
                leaseItem = leaseList[position]
                /*TODO ganram*/
           //    val intent = Intent(context, HomeActivityTenant::class.java)
            //   context.startActivity(intent)
            } else {
                for (item in listLease[position].tenantBaseInfoDto) {

                    if (item.userId == Kotpref.userId) {
                        Kotpref.tenantId = item.tenantId!!
                        Kotpref.leaseId=item.leaseId

                        if (!item.tenantDataModifed!!) {
                            val intent =
                                Intent(context, TenantVerifyUpdateDetailsFirstActivity::class.java)
                            intent.putExtra("tenantDetails", item)
                            context.startActivity(intent)
                        } else {
                            val intent =
                                Intent(context, TenantListActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            context.startActivity(intent)
                        }

                    }
                }
            }
        }

        holder.view_lease.setOnClickListener {

            Kotpref.propertyId = listLease[position].propertyId
            Kotpref.leaseId = listLease[position].leaseId
            Kotpref.unitNumber = listLease[position].unitNumber
            Kotpref.unitId = listLease[position].unitId
            Kotpref.landlordId = listLease[position].landlordId

            if (leaseList[position].leaseStatus == StatusConstant.FINALIZED && leaseList[position].renewLease) {
                val builder = AlertDialog.Builder(context)
                builder.setTitle(context.getString(R.string.lease_renew_title))
                builder.setMessage(context.getString(R.string.lease_renew_message))
                // Navigate to permission screen of app in setting
                builder.setPositiveButton(context.getString(R.string.ok)) { dialog, which ->
                    dialog.cancel()
                }
                builder.show()

            }
            else if (leaseList[position].leaseStatus == StatusConstant.FINALIZED && !leaseList[position].renewLease) {
                leaseItem = leaseList[position]
                //val intent = Intent(context, HomeActivityTenant::class.java)
                val intent = Intent(context, LeaseListDetailsOptionsActivity::class.java)
                context.startActivity(intent)
            }
            else if (leaseList[position].leaseStatus == StatusConstant.LEASE_TERMINATED) {
                leaseItem = leaseList[position]
                val intent = Intent(context, TenantPropertyUnitDetailActivityCx::class.java)
                intent.putExtra("leasePosition", position)
                context.startActivity(intent)
            }
            else {
                for (item in listLease[position].tenantBaseInfoDto) {

                    if (item.userId == Kotpref.userId) {
                        Kotpref.tenantId = item.tenantId!!

                        if (!item.tenantDataModifed!!) {
                            val intent =
                                Intent(context, TenantVerifyUpdateDetailsFirstActivity::class.java)
                            intent.putExtra("tenantDetails", item)
                            intent.putExtra("leasePosition", position)
                            context.startActivity(intent)
                        } else {
                            val intent =
                                Intent(context, TenantListActivity::class.java)
                            intent.putExtra("leasePosition", position)
                            context.startActivity(intent)
                        }

                    }
                }

            }

        }

    }

    override fun getItemCount(): Int {
        return leaseList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val address = itemView.lease_list_property_address
        val landlord_name = itemView.lease_list_landlord_name
        val unti_number = itemView.lease_list_unit_number
        val rent_amount = itemView.lease_list_rent_amount
        val view_lease = itemView.view_lease_list
        val view_status = itemView.lease_list_status
        val img_app=itemView.img_app


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_lease_list, parent, false)
        return ViewHolder(v)
    }
}