package mp.app.calonex.tenant.activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_invoice_payment_tenant.*
import mp.app.calonex.R
import mp.app.calonex.billpay.CardAndBankInfoActivity
import mp.app.calonex.common.apiCredentials.InvoiceDetailsByIDCred
import mp.app.calonex.common.apiCredentials.PayRentCredential
import mp.app.calonex.common.network.*
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.CxBaseActivity2
import mp.app.calonex.tenant.model.InvoiceDetailsByIDResponse
import mp.app.calonex.tenant.model.InvoiceDetailsDao
import mp.app.calonex.tenant.response.ResponseDtoResponse
import mp.app.calonex.utility.AppUtil
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*


class TenantPayRentInvoiceActivity : CxBaseActivity2() {

    var dateAvailableTimeStamp: String? = System.currentTimeMillis().toString()
    var info: InvoiceDetailsDao? = null

    private var headerBack: ImageView? = null
    var rentAmount: Double = 0.0
    var totalPayableAmount: Double = 0.0
    var lateFee: Double = 0.0
    var discount: Double = 0.0
    var discountedAmount: Double = 0.0
    var serviceFee: Double = 0.0
    var prevDue: Double = 0.0
    var autoPay: Boolean = false
    var amenities: Double = 0.0

    var month: Int = 0
    var year: Int = 0;

    var monthValue: String = ""
    var yearValue: String = ""


    var payloadRent: String? = ""
    var payloadAmenities: String? = ""
    var payloadCalonexFee: String? = ""

    var lateFeePaying: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice_payment_tenant)

        getPaymentInfo()
        actionComponent()
    }

    fun showHideViews() {

        if (autoPay) {
            payment_info_switch_auto_pay.isChecked = true
            auto_pay_text.text =
                "Auto pay is currently enabled, you can always change your preference in Payment Setting"
        } else {
            payment_info_switch_auto_pay.isChecked = false
            auto_pay_text.text =
                "Auto pay is currently disabled, you can always change your preference in Payment Setting"
        }
    }


    private fun actionComponent() {

        header_back.setOnClickListener {
            super.onBackPressed()
        }

        tv_pay_rent?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {

                if (payment_info_switch_auto_pay!!.isChecked) {
                    Toast.makeText(
                        this@TenantPayRentInvoiceActivity,
                        "Your auto pay is ON",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                } else {
                    val intent = Intent(
                        this@TenantPayRentInvoiceActivity,
                        CardAndBankInfoActivity::class.java
                    )

                    intent.putExtra("invoiceId", info!!.invoiceId.toString())
                    intent.putExtra("leaseId", info!!.leaseId.toString())
                    intent.putExtra("tanentId", Kotpref.userId)
                    intent.putExtra("amount", info!!.finalAmount)

                    startActivity(intent)
                    finish()
                }
            }

        })

        img_break_down?.setOnClickListener {
            if (layout_breakdown_view.visibility == View.VISIBLE) {
                layout_breakdown_view.visibility = View.GONE
            } else {
                layout_breakdown_view.visibility = View.VISIBLE
            }
        }

        val cal = Calendar.getInstance()

        edit_date!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val year = cal.get(Calendar.YEAR)
                val month = cal.get(Calendar.MONTH)
                val day = cal.get(Calendar.DAY_OF_MONTH)

                val dpd = DatePickerDialog(
                    this@TenantPayRentInvoiceActivity,
                    { view, year, monthOfYear, dayOfMonth ->
                        cal.set(Calendar.YEAR, year)
                        cal.set(Calendar.MONTH, monthOfYear)
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                        val myFormat = "MMMM dd yyyy" // mention the format you need
                        val sdf = SimpleDateFormat(myFormat, Locale.US)
                        edit_date!!.text = sdf.format(cal.time)
                        dateAvailableTimeStamp = cal.timeInMillis.toString()
                        getPaymentInfo()

                    },
                    year,
                    month,
                    day
                )

//                dpd.datePicker.minDate = System.currentTimeMillis() - 1000

                dpd.show()
            }

        })

    }


    private fun getPaymentInfo() {
        var lId: String = "0"

        pb_payment.visibility = View.VISIBLE

        val credentials = InvoiceDetailsByIDCred()

        if (!Kotpref.invoiceId.isNullOrEmpty()) {
            lId = Kotpref.invoiceId
        }


        credentials.invoiceId = lId


        val paymentService: ApiInterface =
            ApiClient(this).provideService(ApiInterface::class.java)
        val apiCall: Observable<InvoiceDetailsByIDResponse> =
            paymentService.getInvoiceDetailsByID(credentials)

        Log.e("credentials_LOG", Gson().toJson(credentials))

        RxAPICallHelper().call(apiCall, object : RxAPICallback<InvoiceDetailsByIDResponse> {
            override fun onSuccess(response: InvoiceDetailsByIDResponse) {
                response.data?.let {
                    setPaymentInfo(it)

                }
                pb_payment.visibility = View.GONE


            }

            override fun onFailed(t: Throwable) {
                // show error
                pb_payment.visibility = View.GONE
                Log.e("onFailure", t.toString())
                //  progressBarList?.visibility = View.GONE
            }
        })

    }

    @SuppressLint("SetTextI18n")
    private fun setPaymentInfo(info: InvoiceDetailsDao) {
        this.info = info
        try {

            if (info.status.equals("paid", true)) {
                tv_pay_rent.visibility = View.GONE
            }

            tv_payment_for.text = "" + info.invoiceType
            tv_invoice_title.text = "" + info.invoiceType + " INVOICE"
            tv_invoice_breakup_title.text = "" + info.invoiceType + " Details"
            if (info.invoiceType.contains("Rent")) {
                ll_payment_breakup.visibility = View.VISIBLE
                if (info.rentAmount != null || info.rentAmount != "null") {
                    tv_monthly_rent.text =
                        "$" + info.rentAmount.toBigDecimal().setScale(2, RoundingMode.UP)
                            .toDouble()
                } else {
                    tv_monthly_rent.text = "$0"
                }
                if (info.securityDeposit != null && info.securityDeposit != "null") {
                    payment_total_security.text =
                        "$" + info.securityDeposit.toBigDecimal().setScale(2, RoundingMode.UP)
                            .toDouble()
                } else {
                    payment_total_security.text = "$0"
                }
                if (info.serviceFee != null && info.serviceFee != "null") {
                    tv_tenant_service_fees.text =
                        "$" + info.serviceFee.toBigDecimal().setScale(2, RoundingMode.UP)
                            .toDouble()
                } else {
                    tv_tenant_service_fees.text = "$0"
                }
                if (info.discount != null && info.discount != "null") {
                    tv_discount_amount.text = "$" + info.discount
                } else {
                    tv_discount_amount.text = "$0"
                }
                if (info.totalCharge != null && info.totalCharge != "null") {
                    payment_total_unit_rent.text =
                        "$" + info.totalCharge.toBigDecimal().setScale(2, RoundingMode.UP)
                            .toDouble()
                } else {
                    payment_total_unit_rent.text = "$0"
                }
            } else {
                ll_payment_breakup.visibility = View.GONE

                if (info.finalAmount != null && info.finalAmount != "null") {
                    payment_total_unit_rent.text =
                        "$" + info.finalAmount.toBigDecimal().setScale(2, RoundingMode.UP)
                            .toDouble()
                } else {
                    payment_total_unit_rent.text = "$0"
                }
            }

            tv_invoice_from.text = "" + info.fromName
            tv_unit_location.text = " " + info.fromAddress
            tv_phone_number.text = " " + info.fromMobileNo
            tv_email.text = " " + info.fromEmailAddress
            tv_invoice_number.text = "#INV" + info.invoiceId
            tv_invoice_date.text = " " + info.invoiceDate

            if (info.dueDate != "null") {
                tv_invoice_due_date.text = " " + info.dueDate
            } else {
                tv_invoice_due_date.text = " "
            }
            if (info.finalAmount != null || info.finalAmount != "null") {
                payment_my_monthly_rent.text =
                    "$" + info.finalAmount.toBigDecimal().setScale(2, RoundingMode.UP)
                        .toDouble()
            } else {
                payment_my_monthly_rent.text = "$0"
            }

            tv_invoice_to.text = "" + info.name
            tv_tenant_location.text = " " + info.propertyAddress
            tv_tenant_phone_number.text = " " + info.mobileNo

            //if(info.dueDate)

            try {
                val format = SimpleDateFormat("yyyy-MM-dd")
                val date: Date = format.parse(info.dueDate)
//                val date: Date = format.parse("2023-06-22")

                var getAgoTime = AppUtil.getTimeAgo(date)
                when (getAgoTime) {
                    "in the future" -> {
                        tv_expire_invoice.visibility = View.GONE
                    }

                    "Today" -> {
                        tv_expire_invoice.visibility = View.GONE
                    }

                    else -> {
                        tv_expire_invoice.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun payRent(amountPaying: String) {

        pb_payment.visibility = View.VISIBLE

        val credentials = PayRentCredential()

        /* credentials.propertyId = Kotpref.propertyId
         credentials.tenantId = Kotpref.userId
         credentials.leaseId = Kotpref.leaseId
         credentials.unitNumber = Kotpref.unitNumber
         credentials.rent = payloadRent
         credentials.applicableLateFees = payment_breakdown_late_fee.text.toString()
         credentials.previousDue = prevDue.toString()
         credentials.serviceFee = payloadCalonexFee
         credentials.amenities = payloadAmenities
         credentials.payableAmount = amountPaying*/
        credentials.leaseId = Kotpref.leaseId
        credentials.month = monthValue
        credentials.tenantId = Kotpref.userId
        credentials.year = yearValue

        /*
                if (breakUpPayload != null) {

                    if (lateFeePaying) {
                        credentials.breakup = breakUpPayload
                    } else {
                        credentials.breakup!!.previousDue = breakUpPayload!!.previousDue
                        credentials.breakup!!.rentDueMonthWise = breakUpPayload!!.rentDueMonthWise
                        credentials.breakup!!.rentMonthWise = breakUpPayload!!.rentMonthWise
                    }
                }
        */

        val paymentService: ApiInterface =
            ApiClient(this).provideService(ApiInterface::class.java)
        val apiCall: Observable<ResponseDtoResponse> =
            paymentService.payRent(credentials) //Test API Key

        RxAPICallHelper().call(apiCall, object : RxAPICallback<ResponseDtoResponse> {
            override fun onSuccess(response: ResponseDtoResponse) {
                pb_payment.visibility = View.GONE

                getPaymentInfo()

                if (!response.responseDto?.exceptionCode!!.equals("0")) {
                    /*Util.alertOkMessage(
                        this@TenantPayRentActivity,
                        getString(R.string.success),
                        "Thanks, Your payment was successful"
                    )*/

                    /*
                    ios_tenant123@yopmail.com
                    Test@123
                     */

                    //   payment_action_pay.setText("Paid")
                    tv_pay_rent.setText("Paid")

                    val builder = AlertDialog.Builder(this@TenantPayRentInvoiceActivity)
                    builder.setTitle(getString(R.string.success))
                    builder.setMessage("Thanks, Your payment was successful")
                    builder.setCancelable(false)
                    builder.setPositiveButton(getString(R.string.ok)) { dialog, which ->
                        finish()
                        dialog.dismiss()
                    }
                    builder.show()
                } else {
                    Util.alertOkMessage(
                        this@TenantPayRentInvoiceActivity,
                        getString(R.string.payment_failed),
                        "Your Payment was not successful, please try again later"
                    )
                }

            }

            override fun onFailed(t: Throwable) {
                // show error
                pb_payment.visibility = View.GONE
                t.printStackTrace()
                Util.alertOkMessage(
                    this@TenantPayRentInvoiceActivity,
                    getString(R.string.payment_failed),
                    "Your Payment was not successful, please try again later"
                )
                Log.e("onFailure", t.toString())
            }
        })


    }


}
