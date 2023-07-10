package mp.app.calonex.landlord.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_summary.view.*
import mp.app.calonex.R
import mp.app.calonex.landlord.adapter.PropertyListAdapter.Companion.leasePropertyList
import mp.app.calonex.landlord.model.LeaseRequestInfo
import mp.app.calonex.landlord.model.ObTenantPayload
import mp.app.calonex.landlord.model.UnitDetailsPD

class SummaryAdapter (var context: Context, val list: ArrayList<UnitDetailsPD>) : RecyclerView.Adapter<SummaryAdapter.ViewHolder>() {


    lateinit var tasks: ArrayList<UnitDetailsPD>

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        holder.txtUnitNumber.text ="Unit : "+ list[position].unitNumber
        if(list[position].unitStatus.equals("1")){
            holder.txtTenantName.text="No Tenant"
            holder.txtTenantName.setTextColor(ContextCompat.getColor(context,android.R.color.holo_red_light))
        }else{
            val nameValue=getfinalLease(list[position].propertyUnitID)
            //leasePropertyList
            holder.txtTenantName.text = nameValue
            holder.txtTenantName.setTextColor(ContextCompat.getColor(context,android.R.color.black))
        }


        holder.txtlastPaymentDate.text = list[position].lastPaymentDate

        if(list[position].rentDue.equals("false")){
            holder.txtRentDueStatus.text = "NO"
            holder.txtRentDueStatus.setTextColor(ContextCompat.getColor(context,R.color.colorGreen))
        }else{
            holder.txtRentDueStatus.text = "YES"
            holder.txtRentDueStatus.setTextColor(ContextCompat.getColor(context,android.R.color.holo_red_light))
        }
        holder.txtPaymentdue.text ="$ 0"



    }

    fun setData(tasks: ArrayList<UnitDetailsPD>) {
        notifyDataSetChanged()
        this.tasks = tasks
    }


    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtUnitNumber = itemView.txt_unit_number!!
        val txtTenantName = itemView.txt_tenant_name!!
        val txtlastPaymentDate = itemView.txt_last_payment_date!!
        val txtRentDueStatus = itemView.txt_rent_due_status!!
        val txtPaymentdue = itemView.txt_payment_due!!

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(context).inflate(R.layout.item_summary, parent, false)
        return ViewHolder(v)
    }

    private fun  getfinalLease(idUnit:String):String {
        var name:String=""
        val filterList:List<LeaseRequestInfo>?= leasePropertyList.filter { it.unitId==idUnit && it.leaseStatus=="19"}
        if(filterList!!.size>0){
            val tenantInfo:List<ObTenantPayload>?= filterList?.get(0)?.tenantBaseInfoDto?.filter {it.role=="CX-PrimaryTenant"  }
            if(tenantInfo!!.size>0){
                 name=tenantInfo?.get(0)?.tenantFirstName+" "+tenantInfo?.get(0)?.tenantLastName
                if(tenantInfo?.get(0)?.tenantFirstName==null && tenantInfo?.get(0)?.tenantLastName==null){
                    name =tenantInfo?.get(0)?.emailId!!
                }
                return name
            }
        }

        return name


    }
}