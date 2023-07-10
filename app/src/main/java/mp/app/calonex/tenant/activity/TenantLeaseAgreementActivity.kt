package mp.app.calonex.tenant.activity

import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_tenant_lease_agreement.*
import mp.app.calonex.R
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.Kotpref.userId
import mp.app.calonex.landlord.activity.AccountLinkDetailScreen
import mp.app.calonex.landlord.activity.CxBaseActivity2
import mp.app.calonex.tenant.activity.TenantListActivity.Companion.findApiResponse
import mp.app.calonex.tenant.model.apiCredentials.LeaseAgreementActionCredential
import mp.app.calonex.tenant.response.ModifyTenantDataResponse

class TenantLeaseAgreementActivity : CxBaseActivity2() {

    private var leaseId: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tenant_lease_agreement)

        leaseId = intent.getStringExtra("leaseId").toString()

        val vi: LayoutInflater? = layoutInflater
        for (item in findApiResponse.tenantBaseInfoDto) {
            val view: View? =
                vi?.inflate(
                    R.layout.item_tenant_property_detail,
                    lease_agreement_tenant_details_ll,
                    false
                )

            val tenantName: TextView = view!!.findViewById(R.id.tenant_property_tenant_name)
            val rent: TextView = view.findViewById(R.id.tenant_property_rent)
            val rent_percentage: TextView = view.findViewById(R.id.tenant_property_rent_percent)
            val email: TextView = view.findViewById(R.id.tenant_property_email)
            val phone: TextView = view.findViewById(R.id.tenant_property_phone)
            val co_tenant_flag: Switch = view.findViewById(R.id.tenant_property_is_co_tenant)
            val co_signer_flag: Switch = view.findViewById(R.id.tenant_property_is_co_signer)

            lease_agreement_tenant_details_ll.addView(view)

            tenantName.text = item.tenantFirstName + " " + item.tenantLastName
            rent.text = "$" + item.rentAmount
            rent_percentage.text = item.rentPercentage
            email.text = item.emailId
            phone.text = PhoneNumberUtils.formatNumber(item.phone, "US")
            co_signer_flag.isChecked = item.coSignerFlag!!
            co_tenant_flag.isChecked = item.coTenantFlag!!

        }

        //Log.e("DATA_LOG", Gson().toJson(findApiResponse))

        lease_agreement_landlord_name.text = findApiResponse.landlordName
        lease_agreement_rent_per_month.text = "$" + findApiResponse.rentAmount
        lease_agreement_security_deposit.text = "$" + findApiResponse.securityAmount
        //lease_agreement_service_fee.text = "$" + findApiResponse.securityAmount
        lease_agreement_lease_duration.text = findApiResponse.leaseDuration + "Months"

        if (findApiResponse.discount != null)
            lease_agreement_discount.text = findApiResponse.discount + "%"
        else
            lease_agreement_discount.text = "0%"

        if (findApiResponse.lateFee != null)
            lease_agreement_late_fee.text = "$" + findApiResponse.lateFee
        else
            lease_agreement_late_fee.text = "$ 0"

        lease_agreement_months_free.text = findApiResponse.monthsFree

        try {
            lease_agreement_lease_start_date.text =
                mp.app.calonex.common.utility.Util.convertLongToTime(
                    findApiResponse.leaseStartDate.toLong(), "MMM dd, yyyy"
                )

        } catch (e: Exception) {
            // e.printStackTrace()
            lease_agreement_lease_start_date.text = ""
        }

        actionComponent()
    }

    private fun actionComponent() {
        lease_agree_action_accept.setOnClickListener {
            leaseAction("Accept")
        }

        lease_agree_action_reject.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.alert))
            builder.setMessage(getString(R.string.tag_reject_lease))
            builder.setPositiveButton(getString(R.string.yes)) { dialog, which ->
                leaseAction("Reject")
                dialog.dismiss()
            }
            builder.setNegativeButton(getString(R.string.no)) { dialog, which ->
                dialog.dismiss()
            }
            builder.show()
        }
    }

    private fun leaseAction(action: String) {

        lease_agreement_progress.visibility = View.VISIBLE

        val credential = LeaseAgreementActionCredential()
        credential.userId = userId
        credential.leaseId = findApiResponse.leaseId
        credential.action = action

        val leaseActionApi: ApiInterface =
            ApiClient(this).provideService(ApiInterface::class.java)
        val apiCall: Observable<ModifyTenantDataResponse> =
            leaseActionApi.leaseAgreementAction(credential)

        apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
            Log.e("onSuccess", it.responseDto?.responseDescription.toString())

            /* TODO
            // Check response, if lease was rejected then navigate to TenantPropertyUnitDetailActivityCx Activity by
            // by clearing the stack
            // if lease accepted then navigate to SignatureUploadActivity without clearing the stack
             */



            if (it.data != null) {
                lease_agreement_progress.visibility = View.GONE
                if (it.data.leaseAgreementAcceptance.equals("Reject", true)) {
                    val intent =
                        Intent(this, TenantListActivity::class.java)
                    intent.putExtra("isUpdated", true)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else if (Kotpref.bankAccountVerified) {
                    val intent =
                        Intent(this, SignatureUploadActivity::class.java)
                    startActivity(intent)
                } else if (!Kotpref.bankAccountVerified) {
                    /*  val intent =
                          Intent(this, AddBankDetailsActivity::class.java)*/
                    val intent =
                        Intent(this, AccountLinkDetailScreen::class.java)
                    startActivity(intent)
                }


            }

        },
            { e ->
                lease_agreement_progress.visibility = View.GONE
                Log.e("onFailure", e.toString())
                e.message?.let {
                    Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                }
            })

    }

    override fun onSaveInstanceState(oldInstanceState: Bundle) {
        super.onSaveInstanceState(oldInstanceState)
        oldInstanceState.clear()
    }
}
