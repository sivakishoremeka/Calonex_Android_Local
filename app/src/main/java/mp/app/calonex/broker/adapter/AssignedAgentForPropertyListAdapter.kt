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
import kotlinx.android.synthetic.main.item_broker_agent.view.*
import mp.app.calonex.R
import mp.app.calonex.models.broker.AssignedAgentModel
import java.util.*
import kotlin.collections.ArrayList

class AssignedAgentForPropertyListAdapter(
    var context: Context,
    var activity: FragmentActivity?,
    var list: ArrayList<AssignedAgentModel>,
    var propertyId: String
) : RecyclerView.Adapter<AssignedAgentForPropertyListAdapter.ViewHolder>(),
    Filterable {

    private var listPayment = ArrayList<AssignedAgentModel>()

    init {
        listPayment = list
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var agentData = listPayment[position]



        holder.agent_name.text = agentData.userFullName
        holder.agent_id.text = "Agent Id : " + agentData.agentLicenseNO.toString()
        holder.agent_property_onboarded.text = "Onboarded Properties : $propertyId"
        holder.agent_status.text = "Status : " + agentData.finalStatus


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

        //val type = itemView.landlord_payment_type!!
        val agent_name = itemView.agent_name!!
        val agent_id = itemView.agent_id!!
        val agent_property_onboarded = itemView.property_onboarded!!
        val agent_status = itemView.agent_status!!
        //val ivCopy = itemView.iv_copy!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(context).inflate(R.layout.item_broker_agent, parent, false)

        return ViewHolder(v)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    listPayment = list
                } else {
                    val resultList = ArrayList<AssignedAgentModel>()
                    for (row in list) {
                        if (row.userFullName!!.lowercase(Locale.getDefault())
                                .contains(charSearch) || row.id.toString()
                                .lowercase(Locale.getDefault())
                                .contains(charSearch) ||
                            row.commissionPercentage.toString().lowercase(Locale.getDefault())
                                .contains(charSearch)
                            || row.finalStatus!!.lowercase(Locale.getDefault()).contains(charSearch)
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
                listPayment = results?.values as ArrayList<AssignedAgentModel>
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