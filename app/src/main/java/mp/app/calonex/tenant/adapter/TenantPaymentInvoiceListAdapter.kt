package mp.app.calonex.tenant.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_tenant_rent_invoice_list.view.*
import mp.app.calonex.R
import mp.app.calonex.tenant.model.TenantUnitData
import java.sql.Date
import java.text.SimpleDateFormat

class TenantPaymentInvoiceListAdapter(
    var context: Context,
    val list: ArrayList<TenantUnitData>,
    val onClick: (TenantUnitData) -> Unit

) :
    RecyclerView.Adapter<TenantPaymentInvoiceListAdapter.ViewHolder>() {

    var tenantList = ArrayList<TenantUnitData>()

    init {
        tenantList = list
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.tv_unit_location.text = tenantList[position].propertyAddress

        holder.tv_total_amount.text = "Total : \$" + tenantList[position].finalAmount
        holder.tv_invoice_number.text = "#INV" + tenantList[position].invoiceId

        holder.tv_invoice_status.text = "" + tenantList[position].status

        if (tenantList[position].rentAmount != null || tenantList[position].rentAmount != "null") {
            holder.tv_rent_amount.text = "Rent : \$" + tenantList[position].rentAmount
        } else {
            holder.tv_rent_amount.text = "Rent : \$0"
        }

        holder.tv_service_fee.text = "Service fee : \$" + tenantList[position].serviceFee

        holder.tv_invoice_date.text = "Invoice : " + tenantList[position].invoiceDate

        if (tenantList[position].dueDate != null || tenantList[position].dueDate != "null") {
            holder.tv_invoice_due_date.text = "Due : " + tenantList[position].dueDate
        } else {
            holder.tv_invoice_due_date.text = "Due : "
        }
        when (tenantList[position].status) {
            "PAID" -> {
                holder.tv_invoice_status.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorGreenTextNew
                    )
                )

            }

            "UNPAID" -> {
                holder.tv_invoice_status.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorOrangeText
                    )
                )

            }

            else -> {
                holder.tv_invoice_status.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorDeepRed
                    )
                )
            }

        }

        if (tenantList[position].status.equals("paid", true)) {
            holder.tv_pay_rent.text = "view"
        }

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
        val tv_invoice_number = itemView.tv_invoice_number!!
        val tv_invoice_status = itemView.tv_invoice_status!!
        val tv_invoice_date = itemView.tv_invoice_date!!
        val tv_unit_location = itemView.tv_unit_location!!
        val tv_rent_amount = itemView.tv_rent_amount!!
        val tv_service_fee = itemView.tv_service_fee!!
        val tv_total_amount = itemView.tv_total_amount!!
        val tv_invoice_due_date = itemView.tv_invoice_due_date!!
        val tv_pay_rent = itemView.tv_pay_rent!!


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v =
            LayoutInflater.from(context)
                .inflate(R.layout.item_tenant_rent_invoice_list, parent, false)
        return ViewHolder(v)
    }


}