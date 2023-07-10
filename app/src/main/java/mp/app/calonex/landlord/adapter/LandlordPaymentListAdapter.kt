package mp.app.calonex.landlord.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_landlord_payment.view.*
import mp.app.calonex.R
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.model.LandlordPaymentHistory
import java.util.*
import kotlin.collections.ArrayList

class LandlordPaymentListAdapter(
    var context: Context,
    var activity: FragmentActivity?,
    var list: ArrayList<LandlordPaymentHistory>
) : RecyclerView.Adapter<LandlordPaymentListAdapter.ViewHolder>(),
    Filterable {

    private var listPayment = ArrayList<LandlordPaymentHistory>()

    init {
        listPayment = list
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //holder.transactionId.text = listPayment[position].transactionId
        holder.propertyAdd.text = listPayment[position].propertyAddress
        holder.date.text =
            Util.convertLongToTime(listPayment[position].paymentDate.toLong(), "MMM dd, yyyy")
        holder.type.text = listPayment[position].paymentType
        holder.unit.text = listPayment[position].unitNumber
        holder.tenant_name.text = listPayment[position].tenantName
        holder.amount.text = listPayment[position].amount
        holder.status.text = listPayment[position].status

        //holder.ivCopy.setOnClickListener {
        //    copyTextToClipboard(listPayment[holder.adapterPosition].transactionId)
        //}
    }

    override fun getItemCount(): Int {
        return listPayment.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val propertyAdd = itemView.landlord_payment_prop_address!!
        //val transactionId = itemView.landlord_payment_id!!
        val date = itemView.landlord_payment_date!!
        val type = itemView.landlord_payment_type!!
        val unit = itemView.landlord_payment_unit!!
        val tenant_name = itemView.landlord_payment_tenant_name!!
        val amount = itemView.landlord_payment_amount!!
        val status = itemView.txt_payment_status!!
        //val ivCopy = itemView.iv_copy!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(context).inflate(R.layout.item_landlord_payment, parent, false)

        return ViewHolder(v)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    listPayment = list
                } else {
                    val resultList = ArrayList<LandlordPaymentHistory>()
                    for (row in list) {
                        if (row.tenantName.lowercase(Locale.getDefault())
                                .contains(charSearch) || row.propertyAddress.lowercase(Locale.getDefault())
                                .contains(charSearch) ||
                            row.unitNumber.lowercase(Locale.getDefault()).contains(charSearch)
                            || row.transactionId.lowercase(Locale.getDefault()).contains(charSearch)
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
                listPayment = results?.values as ArrayList<LandlordPaymentHistory>
                notifyDataSetChanged()
            }
        }
    }

    private fun copyTextToClipboard(value: String) {

        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", value)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(
            context,
           "TransactionId Copied",
            Toast.LENGTH_SHORT
        ).show()

    }
}