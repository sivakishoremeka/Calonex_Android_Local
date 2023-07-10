package mp.app.calonex.tenant.fragment

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_view_lease_dialog.*
import mp.app.calonex.R
import mp.app.calonex.common.utility.Util
import mp.app.calonex.tenant.model.LeaseTenantInfo


class ViewLeaseDialogFragment(context: Context) : Dialog(context) {

    fun customDialog(lease: LeaseTenantInfo) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.fragment_view_lease_dialog)
        dialog.setTitle("Lease Specification")
        dialog.setCancelable(false)

        val textView = dialog.findViewById<View>(R.id.lease_text_view) as TextView
        val leaseDuration = dialog.findViewById<View>(R.id.lease_lease_duration) as TextView
        val lease_landlord_name = dialog.findViewById<View>(R.id.lease_landlord_name) as TextView
        val lease_property_address = dialog.findViewById<View>(R.id.lease_property_address) as TextView
        val rentAmount = dialog.findViewById<View>(R.id.lease_rent_amount) as TextView
        val securityDeposit = dialog.findViewById<View>(R.id.lease_security_deposit) as TextView
        val startDate = dialog.findViewById<View>(R.id.lease_start_date) as TextView
        val late_fee = dialog.findViewById<View>(R.id.lease_late_fee) as TextView
        val months_free = dialog.findViewById<View>(R.id.lease_months_free) as TextView
        val btn_dialog = dialog.findViewById<View>(R.id.btDialog) as Button

        textView.setText("Lease Specification")
        lease_landlord_name.setText(lease.landlordName)
        lease_property_address.setText(lease.propertyAddress)
        leaseDuration.setText(lease.leaseDuration)
        rentAmount.setText("$"+lease.rentAmount)
        securityDeposit.setText("$"+lease.securityAmount)
        late_fee.setText("$"+lease.lateFee)
        months_free.setText(lease.monthsFree)
        startDate.setText(Util.getDateTime(lease.leaseStartDate.toString()))


        btn_dialog.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })

        dialog.show()
    }
}