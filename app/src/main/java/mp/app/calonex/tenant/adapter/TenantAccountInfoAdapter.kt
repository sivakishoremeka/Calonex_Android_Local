package mp.app.calonex.tenant.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.child_account_info_for_tenant.view.*
import mp.app.calonex.R
import mp.app.calonex.tenant.model.CxScoreLeaseDetail
import java.sql.Date
import java.text.SimpleDateFormat

class TenantAccountInfoAdapter(
    var context: Context,
    val list: ArrayList<CxScoreLeaseDetail>,
    val onClick: (CxScoreLeaseDetail) -> Unit

) :
    RecyclerView.Adapter<TenantAccountInfoAdapter.ViewHolder>() {

    var tenantList = ArrayList<CxScoreLeaseDetail>()

    init {
        tenantList = list
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.tv_tenant_address.text = tenantList[position].tenantAddress

        holder.tv_tenant_time_occupied.text =
            "" + tenantList[position].leaseStart + "" + tenantList[position].leaseEnd

        holder.tv_id_number.text = "" + tenantList[position].csn_No
        holder.tv_property_proprietor.text = "" + tenantList[position].landLordName

        holder.tv_view_details.setOnClickListener {
            onClick.invoke(tenantList[position])

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
        val tv_tenant_address = itemView.tv_tenant_address
        val tv_tenant_time_occupied = itemView.tv_tenant_time_occupied
        val tv_id_number = itemView.tv_id_number
        val tv_property_proprietor = itemView.tv_property_proprietor
        val tv_view_details = itemView.tv_view_details


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v =
            LayoutInflater.from(context)
                .inflate(R.layout.child_account_info_for_tenant, parent, false)
        return ViewHolder(v)
    }


}