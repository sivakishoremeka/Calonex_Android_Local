package mp.app.calonex.tenant.activity

import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.Switch
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_property_detail.*
import mp.app.calonex.R
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.CxBaseActivity2
import mp.app.calonex.tenant.activity.LeaseListActivity.Companion.leaseItem
import mp.app.calonex.tenant.model.LeaseTenantInfo

class TenantPropertyDetailActivity : CxBaseActivity2() {

    var leaseDetail = LeaseTenantInfo()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_property_detail)

        leaseDetail = leaseItem
        updateUi(leaseDetail)
        actionComponent()


    }

    private fun actionComponent() {
        header_back.setOnClickListener {
            super.onBackPressed()
        }
    }

    private fun updateUi(leaseDetail: LeaseTenantInfo) {
        val vi: LayoutInflater? = layoutInflater

        property_detail_landlord_name.text = leaseDetail.landlordName
        property_detail_address.text = leaseDetail.propertyAddress + "," + leaseDetail.propertyCity
        property_detail_lease_start_date.text = Util.getDateTime(leaseDetail.leaseStartDate)
        property_detail_lease_end_date.text = Util.getDateTime(leaseDetail.leaseEndDate)
        property_detail_lease_duration.text = leaseDetail.leaseDuration

        for (item in leaseDetail.tenantBaseInfoDto) {
            val view: View? =
                vi?.inflate(R.layout.item_tenant_property_detail, tenant_details_ll, false)

            val tenantName: TextView = view!!.findViewById(R.id.tenant_property_tenant_name)
            val rent: TextView = view.findViewById(R.id.tenant_property_rent)
            val rent_percentage: TextView = view.findViewById(R.id.tenant_property_rent_percent)
            val email: TextView = view.findViewById(R.id.tenant_property_email)
            val phone: TextView = view.findViewById(R.id.tenant_property_phone)
            val co_tenant_flag: Switch = view.findViewById(R.id.tenant_property_is_co_tenant)
            val co_signer_flag: Switch = view.findViewById(R.id.tenant_property_is_co_signer)

            tenant_details_ll.addView(view)

            tenantName.text = item.tenantFirstName + " " + item.tenantLastName
            rent.text = "$"+item.rentAmount
            rent_percentage.text = item.rentPercentage
            email.text = item.emailId
            phone.text = PhoneNumberUtils.formatNumber(item.phone,"US")
            co_signer_flag.isChecked = item.coSignerFlag!!
            co_tenant_flag.isChecked = item.coTenantFlag!!

        }


    }


}
