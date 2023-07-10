package mp.app.calonex.broker.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_broker_payment.view.*
import mp.app.calonex.R
import mp.app.calonex.broker.model.BrokerPaymentHistory
import mp.app.calonex.common.utility.Util
import java.util.*
import kotlin.collections.ArrayList

class BrokerPaymentListAdapter(
    var context: Context,
    var activity: FragmentActivity?,
    var list: ArrayList<BrokerPaymentHistory>,
    var flag: Boolean
) : RecyclerView.Adapter<BrokerPaymentListAdapter.ViewHolder>(),
    Filterable {

    private var listPayment = ArrayList<BrokerPaymentHistory>()

    init {
        listPayment = list
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (flag) {
            holder.nameText.text = "Agent Name : "
            holder.tenant_name.text =
                listPayment[position].agentName
            holder.landlord_payment_broker_name.text = listPayment[position].landlordName
        } else {
            holder.nameText.text = "Agent Name : "
            holder.tenant_name.text = listPayment[position].agentName
        }
        holder.unit.text = listPayment[position].unitNumber
        holder.amount.text = "$" + listPayment[position].amount
        holder.date.text =
            Util.convertLongToTime(listPayment[position].paymentDate.toLong(), "MMM dd, yyyy")
        holder.propertyAdd.text = listPayment[position].propertyAddress
        holder.status.text = listPayment[position].status
        //holder.transactionId.text = listPayment[position].transactionId
        holder.type.text = listPayment[position].paymentCategory
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
        val nameText = itemView.agent_broker_name_txt!!
        val landlord_payment_broker_name = itemView.landlord_payment_broker_name!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(context).inflate(R.layout.item_broker_payment, parent, false)

        return ViewHolder(v)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    listPayment = list
                } else {
                    val resultList = ArrayList<BrokerPaymentHistory>()
                    for (row in list) {
                        if (row.agentName.lowercase(Locale.getDefault())
                                .contains(charSearch) || row.propertyAddress.lowercase(Locale.getDefault())
                                .contains(charSearch) ||
                            row.unitNumber.lowercase(Locale.getDefault()).contains(charSearch)
                            || row.amount.lowercase(Locale.getDefault()).contains(charSearch)
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
                listPayment = results?.values as ArrayList<BrokerPaymentHistory>
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