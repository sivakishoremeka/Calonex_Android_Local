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
import kotlinx.android.synthetic.main.item_agent_subscription.view.*
import mp.app.calonex.R
import mp.app.calonex.agent.model.AgentSubscriptionList
import java.util.*
import kotlin.collections.ArrayList

class BrokerSubscriptionListAdapter(
    var context: Context,
    var activity: FragmentActivity?,
    var list: ArrayList<AgentSubscriptionList>
) : RecyclerView.Adapter<BrokerSubscriptionListAdapter.ViewHolder>(),
    Filterable {

    private var listPayment = ArrayList<AgentSubscriptionList>()

    init {
        listPayment = list
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //holder.propertyAdd.text = listPayment[position].propertyAddress
        //holder.date.text = Util.convertLongToTime(listPayment[position].paymentDate.toLong(), "MMM dd, yyyy")
        holder.tenant_name.text = listPayment[position].landlordName
        holder.amount.text = "$" + listPayment[position].price
        holder.unit.text = listPayment[position].numberOfUnits.toString()
        holder.status.text = "$" + listPayment[position].brokerCommission
        holder.date.text = listPayment[position].currentplan.toString() + " Years"
        holder.type.text = "$" + listPayment[position].agentCommission
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
        var v = LayoutInflater.from(context).inflate(R.layout.item_broker_subscription, parent, false)

        return ViewHolder(v)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    listPayment = list
                } else {
                    val resultList = ArrayList<AgentSubscriptionList>()
                    for (row in list) {
                        if (row.landlordName.lowercase(Locale.getDefault())
                                .contains(charSearch) || row.currentplan.toString().lowercase(Locale.getDefault())
                                .contains(charSearch) ||
                            row.numberOfUnits.toString().lowercase(Locale.getDefault()).contains(charSearch)
                            || row.agentCommission.lowercase(Locale.getDefault()).contains(charSearch)
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
                listPayment = results?.values as ArrayList<AgentSubscriptionList>
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