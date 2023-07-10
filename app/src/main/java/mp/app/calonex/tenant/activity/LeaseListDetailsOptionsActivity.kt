package mp.app.calonex.tenant.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import mp.app.calonex.R
import mp.app.calonex.landlord.activity.CxBaseActivity2

class LeaseListDetailsOptionsActivity : CxBaseActivity2() {

    private var rateBuilding: View? = null
    private var paymentHistory: View? = null
    private var paymentSetting: View? = null
    private var propertyDetail: View? = null
    private var home_welcome: TextView? = null
    private var payRent: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lease_list_details_options_screen)

        home_welcome = findViewById(R.id.home_welcome_title)
        rateBuilding = findViewById(R.id.home_layout_rate_building)
        paymentHistory = findViewById(R.id.home_layout_payment_history)
        paymentSetting = findViewById(R.id.home_layout_payment_setting)
        propertyDetail = findViewById(R.id.home_layout_property_detail)
        payRent = findViewById(R.id.home_layout_pay_rent)

        rateBuilding?.setOnClickListener {
            val intent = Intent(this, TenantRatingActivity::class.java)
            startActivity(intent)

        }
        payRent?.setOnClickListener {
            val intent = Intent(this, TenantPayRentActivity::class.java)
            startActivityForResult(intent, 400)

        }

        paymentHistory?.setOnClickListener {
            val intent = Intent(this, TenantPaymentHistoryActivity::class.java)
            startActivity(intent)

        }
        paymentSetting?.setOnClickListener {
            val intent = Intent(this, TenantPaymentSettingActivity::class.java)
            startActivity(intent)

        }
        propertyDetail?.setOnClickListener {
            val intent = Intent(this, TenantPropertyDetailActivity::class.java)
            startActivity(intent)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == 400) {
                payRent?.visibility = View.GONE
            } else {
                payRent?.visibility = View.VISIBLE

            }

        }
    }
}
