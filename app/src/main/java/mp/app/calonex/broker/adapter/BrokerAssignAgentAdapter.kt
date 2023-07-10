package mp.app.calonex.broker.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_selected_agent.view.*
import mp.app.calonex.R
import mp.app.calonex.broker.responce.Data
import java.util.*

class BrokerAssignAgentAdapter(
    var context: Context,
    var activity: FragmentActivity?,
    var list: ArrayList<Data>,
    var assignedAgentsForProperty: List<String>,
    var propertyId: String,
    val onClick: (Data, String) -> Unit
) : RecyclerView.Adapter<BrokerAssignAgentAdapter.ViewHolder>(),
    Filterable {

    private var listPayment = ArrayList<Data>()

    init {
        listPayment = list
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var agentData = listPayment[position]

        holder.agent_name.text = agentData.agentName
        holder.agent_id.text = "Agent Id : " + agentData.agentLicenseNo.toString()

        holder.agent_property_onboarded.text = "Onboarded Properties / Units : $propertyId"

        holder.agent_status.text = "Status : " + agentData.status

        if (assignedAgentsForProperty.contains(agentData.agentLicenseNo.toString())) {
            holder.iv_agent_selected.visibility = View.VISIBLE
        } else {
            holder.iv_agent_selected.visibility = View.GONE
        }

        holder.rootView.setOnClickListener {
            if (!holder.iv_agent_selected.isVisible) {
                onClick.invoke(agentData, "" + agentData.agentLicenseNo)
            }
        }

        /* if (listPayment[position].status == "APPROVED")
         {
             holder.status.setTextColor(ContextCompat.getColor(context, R.color.colorDeepGreen))
         } else {
             holder.status.setTextColor(ContextCompat.getColor(context, R.color.colorDeepRed))
         }
         holder.status.text = listPayment[position].status


                 //holder.transactionId.text = listPayment[position].transactionId
         //holder.type.text = listPayment[position].paymentType*/

    }

    override fun getItemCount(): Int {
        return listPayment.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val agent_profile_image = itemView.agent_image!!

        val rootView = itemView.ll_root!!
        val agent_name = itemView.agent_name!!
        val agent_id = itemView.agent_id!!
        val agent_property_onboarded = itemView.property_onboarded!!
        val agent_status = itemView.agent_status!!
        val iv_agent_selected = itemView.iv_agent_selected!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(context).inflate(R.layout.item_selected_agent, parent, false)

        return ViewHolder(v)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    listPayment = list
                } else {
                    val resultList = ArrayList<Data>()
                    for (row in list) {
                        if (row.agentName!!.lowercase(Locale.getDefault())
                                .contains(charSearch) || row.agentId.toString()
                                .lowercase(Locale.getDefault())
                                .contains(charSearch) ||
                            row.commissionPercentage.toString().lowercase(Locale.getDefault())
                                .contains(charSearch)
                            || row.status!!.lowercase(Locale.getDefault()).contains(charSearch)
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
                listPayment = results?.values as ArrayList<Data>
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