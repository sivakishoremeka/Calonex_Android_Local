package mp.app.calonex.tenant.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_tenant_dashboard_unit_rent_list.view.*
import mp.app.calonex.R
import mp.app.calonex.tenant.model.TenantUnitData
import java.sql.Date
import java.text.SimpleDateFormat

class TenantDashboardUnitRentListAdapter(
    var context: Context,
    val list: ArrayList<TenantUnitData>,
    val onClick: (TenantUnitData) -> Unit

) :
    RecyclerView.Adapter<TenantDashboardUnitRentListAdapter.ViewHolder>() {

    var tenantList = ArrayList<TenantUnitData>()

    init {
        tenantList = list
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.tv_unit_lcoation.text = tenantList[position].propertyAddress

        holder.tv_total_amount.text = "Total Amount : \$"+tenantList[position].finalAmount

        holder.tv_invoice_date.text = "Invoice : "+tenantList[position].invoiceDate
        holder.tv_invoice_due_date.text = "Due : "+tenantList[position].dueDate
        holder.tv_invoice_due_date.visibility=View.GONE
        holder.tv_pay_rent.setOnClickListener {
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
        val tv_unit_lcoation = itemView.tv_unit_lcoation!!
        val tv_total_amount = itemView.tv_total_amount!!
        val tv_invoice_date = itemView.tv_invoice_date!!
        val tv_invoice_due_date = itemView.tv_invoice_due_date!!
        val tv_pay_rent = itemView.tv_pay_rent!!



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v =
            LayoutInflater.from(context).inflate(R.layout.item_tenant_dashboard_unit_rent_list, parent, false)
        return ViewHolder(v)
    }


}