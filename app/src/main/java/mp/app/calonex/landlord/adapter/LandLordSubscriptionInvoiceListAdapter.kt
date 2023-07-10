package mp.app.calonex.landlord.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.landlord_subscription_invoice_list_item.view.*
import mp.app.calonex.R
import mp.app.calonex.tenant.model.TenantUnitData
import java.sql.Date
import java.text.SimpleDateFormat

class LandLordSubscriptionInvoiceListAdapter (
    var context: Context,
    val list: ArrayList<TenantUnitData>,
    val onClick: (TenantUnitData) -> Unit

) :
    RecyclerView.Adapter<LandLordSubscriptionInvoiceListAdapter.ViewHolder>() {

    var tenantList = ArrayList<TenantUnitData>()

    init {
        tenantList = list
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.tv_unit_location.text = tenantList[position].propertyAddress

        holder.tv_total_amount.text = "Total : \$" + FormatedValue(tenantList[position].finalAmount)
        holder.tv_invoice_number.text = "#INV" + FormatedValue(tenantList[position].invoiceId)

        holder.tv_invoice_status.text = "" + FormatedValue(tenantList[position].status)

        holder.tv_subscription_plan.text = "Subscription Plan : \$" + FormatedValue(tenantList[position].planName)
        holder.plan_amount.text = "Amount : \$" + FormatedValue(tenantList[position].planAmount)

        holder.tv_invoice_date.text = "Invoice : " + FormatedValue(tenantList[position].invoiceDate)
        holder.tv_invoice_due_date.text = "Due : " + FormatedValue(tenantList[position].dueDate)

        if (!TextUtils.isEmpty(tenantList[position].status)) {

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
        val tv_subscription_plan = itemView.subscription_plan!!
        val plan_amount = itemView.plan_amount!!
        val tv_total_amount = itemView.tv_total_amount!!
        val tv_invoice_due_date = itemView.tv_invoice_due_date!!
        val tv_pay_rent = itemView.tv_pay_invoice!!


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v =
            LayoutInflater.from(context)
                .inflate(R.layout.landlord_subscription_invoice_list_item, parent, false)
        return ViewHolder(v)
    }

    fun FormatedValue(s:String?): String? {
     if (TextUtils.isEmpty(s)) {
         return "NA"
     }
        else{
            return s
        }



    }


}