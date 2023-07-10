package mp.app.calonex.agent.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.item_agent_bookkeeping.view.*
import mp.app.calonex.R
import mp.app.calonex.agent.activity.BookKeepingItemEditActivityAgent
import mp.app.calonex.agent.model.AgentBookKeepingUser
import mp.app.calonex.common.utility.Util
import kotlin.collections.ArrayList

class AgentBookKeepingAdapter(
    var context: Context,
    var activity: FragmentActivity?,
    var list: ArrayList<AgentBookKeepingUser>
) : RecyclerView.Adapter<AgentBookKeepingAdapter.ViewHolder>(),
    Filterable {

    private var listPayment = ArrayList<AgentBookKeepingUser>()

    init {
        listPayment = list
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //holder.date.text = Util.convertLongToTime(listPayment[position].paymentDate.toLong(), "MMM dd, yyyy")

        Log.e("Item","Book Keeping >> "+ Gson().toJson(listPayment[position]))
        if (listPayment[position].type == "earnings") {
            holder.amount.text = "+$" + listPayment[position].amount
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.colorDeepGreen))
        } else {
            holder.amount.text = "-$" + listPayment[position].amount
            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.colorDeepRed))
        }

        holder.description.text = listPayment[position].description
        holder.tv_date.text = Util.convertLongToTime(listPayment[position].date, "MMM/dd/yyyy")
        //holder.description.text = listPayment[position].description
        //holder.description.text = listPayment[position].description

        /*
        tv_date
tv_balance_amount
iv_edit


        + " on " +
                Util.convertLongToTime(listPayment[position].date, "MMM/dd/yyyy")
*/
        //holder.type.text = listPayment[position].paymentType

        holder.iv_edit.setOnClickListener {
            //navigationNext(activity!!, BookKeepingItemEditActivityAgent::class.java)
            val intent = Intent(activity!!, BookKeepingItemEditActivityAgent::class.java)
            intent.putExtra("ForEdit", listPayment[position].id)
            intent.putExtra("amount", listPayment[position].amount)
            intent.putExtra("des", listPayment[position].description)
            intent.putExtra("date", listPayment[position].date)
            intent.putExtra("type", listPayment[position].type)
            intent.putExtra("category", listPayment[position].category)
            activity!!.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listPayment.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val amount = itemView.itm_ammount!!
        val description = itemView.itm_description!!
        val tv_date = itemView.tv_date!!
        val tv_balance_amount = itemView.tv_balance_amount!!
        val iv_edit = itemView.iv_edit!!
        val main_layout = itemView.main_block!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(context).inflate(R.layout.item_agent_bookkeeping, parent, false)

        return ViewHolder(v)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    listPayment = list
                } /*else {
                    val resultList = ArrayList<AgentBookKeepingUser>()
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
                }*/
                val filterResults = FilterResults()
                filterResults.values = listPayment
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                listPayment = results?.values as ArrayList<AgentBookKeepingUser>
                notifyDataSetChanged()
            }
        }
    }
}