package mp.app.calonex.agent.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_agent_payment.view.*
import mp.app.calonex.R
import mp.app.calonex.agent.model.AgentPaymentHistory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class AgentPaymentListAdapter(
    var context: Context,
    var activity: FragmentActivity?,
    var list: ArrayList<AgentPaymentHistory>
) : RecyclerView.Adapter<AgentPaymentListAdapter.ViewHolder>(),
    Filterable {

    private var listPayment = ArrayList<AgentPaymentHistory>()

    init {
        listPayment = list
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.propertyAdd.text = listPayment[position].propertyAddress
        if(!listPayment[position].paymentDate.isNullOrEmpty())
        {

           // val dateTemp = listPayment[position].paymentDate.split("T").toTypedArray()
            try {
                val localDateTime: LocalDateTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    LocalDateTime.parse(listPayment[position].paymentDate)
                } else {
                    TODO("VERSION.SDK_INT < O")
                }
                val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM/dd/yyyy")
                val output: String = formatter.format(localDateTime)

                holder.date.text=output
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }




        }
        holder.tenant_name.text = listPayment[position].brokerName
        holder.amount.text = "$" + listPayment[position].amount
        holder.unit.text = listPayment[position].unitNumber
        holder.status.text = listPayment[position].commission
        //holder.transactionId.text = listPayment[position].transactionId
        //holder.type.text = listPayment[position].paymentType
        holder.ivCopy.setOnClickListener {
            //copyTextToClipboard(listPayment[holder.adapterPosition].transactionId)
        }
    }

    override fun getItemCount(): Int {
        return listPayment.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val propertyAdd = itemView.landlord_payment_prop_address!!
        val transactionId = itemView.landlord_payment_id!!
        val date = itemView.landlord_payment_date!!
        val type = itemView.landlord_payment_type!!
        val unit = itemView.landlord_payment_unit!!
        val tenant_name = itemView.landlord_payment_tenant_name!!
        val amount = itemView.landlord_payment_amount!!
        val status = itemView.txt_payment_status!!
        val ivCopy = itemView.iv_copy!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(context).inflate(R.layout.item_agent_payment, parent, false)

        return ViewHolder(v)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    listPayment = list
                } else {
                    val resultList = ArrayList<AgentPaymentHistory>()
                    for (row in list) {
                        if (row.brokerName.lowercase(Locale.getDefault())
                                .contains(charSearch) || row.propertyAddress.lowercase(Locale.getDefault())
                                .contains(charSearch) ||
                            row.unitNumber.lowercase(Locale.getDefault()).contains(charSearch)
                            || row.commission.lowercase(Locale.getDefault()).contains(charSearch)
                        ) {
                            resultList.add(row)
                        }
                    }
                    listPayment = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = listPayment
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                listPayment = results?.values as ArrayList<AgentPaymentHistory>
                notifyDataSetChanged()
            }
        }
    }

    private fun copyTextToClipboard(value: String) {
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", value)
        clipboardManager.setPrimaryClip(clipData)
    }
}