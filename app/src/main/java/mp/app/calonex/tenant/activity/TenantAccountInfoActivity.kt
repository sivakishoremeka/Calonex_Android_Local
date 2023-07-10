package mp.app.calonex.tenant.activity

import android.os.Bundle
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_tenant_account_info.*
import mp.app.calonex.R
import mp.app.calonex.landlord.activity.CxBaseActivity2
import mp.app.calonex.tenant.model.CxScoreLeaseDetail

class TenantAccountInfoActivity : CxBaseActivity2() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tenant_account_info)

        try {
            var extras = intent.extras

            if (extras != null) {

                var cxScoreLeaseDetail: CxScoreLeaseDetail? = null
                var accountDetails = extras.getString("account_details", "")

                if (accountDetails != "") {
                    cxScoreLeaseDetail =
                        Gson().fromJson(accountDetails, CxScoreLeaseDetail::class.java)
                    tv_mdaa.text = ""
                    tv_paid_on_time.text = "" + cxScoreLeaseDetail.paidOnTimeCount
                    tv_late_payment.text = "" + cxScoreLeaseDetail.latePaymentCount
                    if (cxScoreLeaseDetail.depositPaid)
                        tv_deposit_returned.text = "Yes"
                    else
                        tv_deposit_returned.text = "No"

                    tv_judgment.text = "" + cxScoreLeaseDetail.judgeMentCount
                    tv_amount_owed.text = "" + cxScoreLeaseDetail.amountOwed
                    tv_date_collected.text = "" + cxScoreLeaseDetail.collectedDate
                    tv_payment_withheld.text = "" + cxScoreLeaseDetail.paymentWithHeldCount
                    tv_deposit.text = "" + cxScoreLeaseDetail.depositPaidCount
                    tv_default.text = "" + cxScoreLeaseDetail.defaulterCount
                    tv_eviction.text = "" + cxScoreLeaseDetail.evictionCount
                    tv_collected.text = "" + cxScoreLeaseDetail.amountCollected
                    tv_release_type.text = "" + cxScoreLeaseDetail.releaseType
                }


            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        iv_back.setOnClickListener {
            onBackPressed()
        }
    }
}