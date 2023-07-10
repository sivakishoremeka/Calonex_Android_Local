package mp.app.calonex.tenant.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_tenant_dashboard_list.view.*
import kotlinx.android.synthetic.main.item_tenant_list.view.*
import kotlinx.android.synthetic.main.item_tenant_list.view.tenant_list_name
import mp.app.calonex.R
import mp.app.calonex.common.constant.StatusConstant
import mp.app.calonex.landlord.model.TenantInfoPayload
import java.sql.Date
import java.text.SimpleDateFormat

class TenantDashboardListAdapter(
    var context: Context,
    val list: ArrayList<TenantInfoPayload>,
    var updateTenantList: Boolean
) :
    RecyclerView.Adapter<TenantDashboardListAdapter.ViewHolder>() {


    var tenantList = ArrayList<TenantInfoPayload>()
    var updateTenant: Boolean

    init {
        tenantList = list
        updateTenant = updateTenantList
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val name: String =
            tenantList[position].tenantFirstName + " " + tenantList[position].tenantLastName

        if (tenantList[position].role!!.contains("Primary", true)) {
            //  holder.tenantName.text = name.plus(" (Primary)")
            holder.tenantName.text = name
        }


        holder.rent.text = "$"+tenantList[position].rentAmount
        holder.leaseDate.text = convertLongToTime(tenantList[position].requestdate)
        //holder.datep.text = "$"+tenantList[position].requestdate
        holder.unit.text = tenantList[position].unitNumber


        holder.leaseStatus.text = StatusConstant.getLeaseText(tenantList[position].leaseStatus.toString())

        holder.ll_details.setOnClickListener {
           /* val intent = Intent(context, AddTenantActivity::class.java)
            intent.putExtra("editTenant", true)
            intent.putExtra("tenantInfo", tenantList[position])
            context.startActivity(intent)*/
        }
    }

    fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("yyyy.MM.dd")
        return format.format(date)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tenantName = itemView.tenant_list_name!!
        val rent = itemView.tv_monthly_rent!!
        val unit = itemView.tenant_unit!!
        val leaseDate = itemView.tv_lease_date!!
        val leaseStatus = itemView.tv_lease_status!!
        var ll_details=itemView.ll_details!!


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(context).inflate(R.layout.item_tenant_dashboard_list, parent, false)
        return ViewHolder(v)
    }


}