package mp.app.calonex.landlord.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_pament_history_list.view.*
import mp.app.calonex.R
import mp.app.calonex.common.utility.Util
import mp.app.calonex.tenant.fragment.ViewRentReceiptDialogFragment
import mp.app.calonex.tenant.model.PaymentHistory

class PaymentHistoryAdapter(var context: Context, val paymentList: ArrayList<PaymentHistory>) :
    RecyclerView.Adapter<PaymentHistoryAdapter.ViewHolder>() {


    var listPH = ArrayList<PaymentHistory>()

    init {
        listPH = paymentList
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.address.text = paymentList[position].property
        holder.amount.text = "$" + paymentList[position].amount
        holder.status.text = paymentList[position].paymentStatus
        holder.invoiceNumber.text="#"+paymentList[position].transactionId
        holder.invoiceDate.text = Util.getDateTimeStamp(paymentList[position].invoiceDate.toString())

//        holder.rent_view.setOnClickListener {
//            showDialog(paymentList[position])
//        }


    }

    override fun getItemCount(): Int {
        return paymentList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val address = itemView.payment_history_address
        val amount = itemView.payment_history_amount
        val invoiceDate = itemView.payment_history_invoice_date
        val status = itemView.payment_history_payment_status
        val invoiceNumber = itemView.payment_invoice_number
        val rent_view = itemView.rent_view

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(context).inflate(R.layout.item_pament_history_list, parent, false)
        return ViewHolder(v)
    }

    private fun showDialog(history: PaymentHistory) {

        ViewRentReceiptDialogFragment(context).customDialog(history)

    }
}