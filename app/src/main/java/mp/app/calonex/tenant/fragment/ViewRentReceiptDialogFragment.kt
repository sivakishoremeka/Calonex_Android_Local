package mp.app.calonex.tenant.fragment

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.TextView
import mp.app.calonex.R
import mp.app.calonex.common.utility.Util
import mp.app.calonex.tenant.model.PaymentHistory


class ViewRentReceiptDialogFragment(context: Context) : Dialog(context) {

    fun customDialog(lease: PaymentHistory) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.fragment_view_rent_history_receipt_dialog)
        dialog.setTitle("Lease Specification")
        dialog.setCancelable(false)

        val btn_dialog = dialog.findViewById<View>(R.id.btDialog) as Button
        val history_invoaice_no = dialog.findViewById<View>(R.id.history_invoice_no) as TextView
        val history_invoice_date = dialog.findViewById<View>(R.id.history_invoice_date) as TextView
        val history_amount_paid = dialog.findViewById<View>(R.id.history_amount_paid) as TextView
        val history_payment_status = dialog.findViewById<View>(R.id.history_payment_status) as TextView

        history_invoice_date.text = Util.getDateTime(lease.invoiceDate.toString())
        history_amount_paid.text = lease.amount
        history_payment_status.text = lease.paymentStatus


        btn_dialog.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })

        dialog.show()
    }
}