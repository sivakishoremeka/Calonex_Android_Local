package mp.app.calonex.landlord.activity

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_tenant_onboard.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.TenantOnboardAddUpdateCredentials
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.model.TenantInfoPayload
import mp.app.calonex.tenant.response.TenantPOnboardAddUpdateResponse
import java.text.SimpleDateFormat
import java.util.*

class TenantOnboardActivity : CxBaseActivity2() {

    var unitNumber: String = ""
    var unitId: String = ""
    var rentPerMonth: String = ""
    var propertyId: String = ""
    var cal = Calendar.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tenant_onboard)

        unitNumber = intent.getStringExtra("unitNumber").toString()
        unitId = intent.getStringExtra("unitId").toString()
        rentPerMonth = intent.getStringExtra("rentPerMonth").toString()
        propertyId = intent.getStringExtra("propertyId").toString()

        onbrd_tenant_rent_amount.setText(rentPerMonth)
        onbrd_tenant_unit_id.setText(unitId)
        onbrd_tenant_property_id.setText(propertyId)

        actionComponent()
        startHandler()
    }

    private fun actionComponent() {

        onbrd_tenant_lease_end_date!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)

                val dpd = DatePickerDialog(
                    this@TenantOnboardActivity,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->


                        val myFormat = "yyyy/MM/dd" // mention the format you need
                        val sdf = SimpleDateFormat(myFormat, Locale.US)
                        onbrd_tenant_lease_end_date!!.text = sdf.format(cal.getTime())

                    },
                    year,
                    month,
                    day
                )

                dpd.datePicker.minDate = System.currentTimeMillis() - 1000

                dpd.show()
            }

        })

        onbrd_tenant_lease_start_date!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)

                val dpd = DatePickerDialog(
                    this@TenantOnboardActivity,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->


                        val myFormat = "yyyy/MM/dd" // mention the format you need
                        val sdf = SimpleDateFormat(myFormat, Locale.US)
                        onbrd_tenant_lease_start_date!!.text = sdf.format(cal.getTime())

                    },
                    year,
                    month,
                    day
                )

                dpd.datePicker.minDate = System.currentTimeMillis() - 1000

                dpd.show()
            }

        })


        onbrd_tenant_btn_done!!.setOnClickListener {
            val credentials = TenantOnboardAddUpdateCredentials()
            credentials.dontShowToLandlord = false
            //credentials.landlordId = Kotpref.userId
            credentials.landlordId = Kotpref.userId
            credentials.lateFee = onbrd_tenant_late_fee.text.toString()
            credentials.leaseDuration = onbrd_tenant_lease_duration.text.toString()
            credentials.leaseEndDate = onbrd_tenant_lease_end_date.text.toString()
            credentials.leaseId = ""
            credentials.leaseStartDate = onbrd_tenant_lease_start_date.text.toString()
            credentials.monthsFree = onbrd_tenant_months_free.text.toString()
            credentials.propertyId = propertyId
            credentials.rentAmount = onbrd_tenant_rent_amount.text.toString()
            credentials.rentBeforeDueDate = onbrd_tenant_rentBeforeDueDate.text.toString()
            credentials.reviewByLandlord = true
            credentials.securityAmount = onbrd_tenant_securityAmount.text.toString()

            val tenantInfo = TenantInfoPayload()
            tenantInfo.address = onbrd_tenant_address.text.toString()
            tenantInfo.city = onbrd_tenant_city.text.toString()
            tenantInfo.emailId = onbrd_tenant_email.text.toString()
            tenantInfo.leaseId = ""
            tenantInfo.licenseNo = onbrd_tenant_license_number.text.toString()
            tenantInfo.payDay = onbrd_tenant_pay_day.text.toString()
            tenantInfo.phone = onbrd_tenant_phone.text.toString()
            tenantInfo.rentAmount = onbrd_tenant_rent_amount_tenant.text.toString()
            tenantInfo.rentPercentage = onbrd_tenant_rent_percentage_tenant.text.toString()
            tenantInfo.role = onbrd_tenant_role.text.toString()
            tenantInfo.securityAmount = onbrd_tenant_securityAmount_tenant.text.toString()
            tenantInfo.ssn = onbrd_tenant_ssn.text.toString().filter { it.isDigit() }
            tenantInfo.state = onbrd_tenant_state.text.toString()
            tenantInfo.tenantFirstName = onbrd_tenant_first_name.text.toString()
            tenantInfo.tenantId = ""
            tenantInfo.tenantLastName = onbrd_tenant_last_name.text.toString()
            tenantInfo.tenantMiddleName = onbrd_tenant_middle_name.text.toString()
            tenantInfo.zipcode = onbrd_tenant_zip.text.toString()

            credentials.tenantBaseInfoDto.addAll(listOf(tenantInfo))

            credentials.unitId = unitId
            credentials.unitNumber = unitNumber
            credentials.uploadedBy = Kotpref.userId

            val tenantAddUpdateCall: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<TenantPOnboardAddUpdateResponse> =
                tenantAddUpdateCall.tenantAddUpdate(credentials)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it.data != null) {

                        Toast.makeText(
                            applicationContext,
                            it.responseDto!!.responseDescription,
                            Toast.LENGTH_SHORT
                        ).show()
                        onBackPressed()

                    }
                },
                    { e ->
                        Toast.makeText(applicationContext, "Fail", Toast.LENGTH_SHORT).show()
                        Log.e("error", e.message.toString())
                    })


        }

    }
    override fun onBackPressed() {
        super.onBackPressed()
        Util.navigationBack(this)
    }
}
