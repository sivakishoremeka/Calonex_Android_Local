package mp.app.calonex.tenant.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_payment_tenant.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.PayRentCredential
import mp.app.calonex.common.apiCredentials.PaymentRentInfoCredential
import mp.app.calonex.common.network.*
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.CxBaseActivity2
import mp.app.calonex.tenant.model.RentBreakup
import mp.app.calonex.tenant.model.TenantRentInfo
import mp.app.calonex.tenant.response.ResponseDtoResponse
import mp.app.calonex.tenant.response.TenantRentInfoResponse
import java.text.SimpleDateFormat
import java.util.*


class TenantPayRentActivity : CxBaseActivity2() {

    var dateAvailableTimeStamp: String? = System.currentTimeMillis().toString()

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

    var rentInfo: TenantRentInfo? = TenantRentInfo()
    var breakUpPayload: RentBreakup? = RentBreakup()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_tenant)
        actionComponent()
        getPaymentInfo()

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

        payment_action_pay?.setOnClickListener {

            if (payment_info_switch_auto_pay!!.isChecked) {
                Toast.makeText(
                    this,
                    "Your auto pay is ON",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            } else {


                if (payment_rent_paying.text.isNullOrEmpty()) {
                    Toast.makeText(
                        this,
                        "Please enter valid amount to pay rent",
                        Toast.LENGTH_SHORT
                    ).show()
                    val resultIntent = Intent()
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish()
                    return@setOnClickListener
                }

                val rentPaying = payment_rent_paying?.text.toString().toDouble()

                if (rentPaying < (totalPayableAmount - lateFee)) {
                    Toast.makeText(
                        this,
                        "Please pay the full rent",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                if (rentPaying > totalPayableAmount) {
                    Toast.makeText(
                        this,
                        "You are paying more than the total payable amount",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                if (rentPaying == totalPayableAmount) {
                    lateFeePaying = true
                } else {
                    lateFeePaying = false
                }
                payRent(rentPaying.toString())
            }

        }

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
                    this@TenantPayRentActivity,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
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
        var lId: Int = 0
        var uId: Int = 0
        var uDate: Long = 0

        pb_payment.visibility = View.VISIBLE

        val credentials = PaymentRentInfoCredential()

        if (!Kotpref.leaseId.isNullOrEmpty()) {
            lId = Integer.parseInt(Kotpref.leaseId)
        }
        if (!Kotpref.userId.isNullOrEmpty()) {
            uId = Integer.parseInt(Kotpref.userId)
        }

        if (!dateAvailableTimeStamp.isNullOrEmpty()) {
            uDate = dateAvailableTimeStamp!!.toLong()


        }



        credentials.leaseId = lId
        credentials.tenantId = uId
        credentials.deviceTime = uDate


        val paymentService: ApiInterface =
            ApiClient(this).provideService(ApiInterface::class.java)
        val apiCall: Observable<TenantRentInfoResponse> =
            paymentService.getRentInfo(credentials) //Test API Key

        Log.e("credentials_LOG", Gson().toJson(credentials))

        RxAPICallHelper().call(apiCall, object : RxAPICallback<TenantRentInfoResponse> {
            override fun onSuccess(response: TenantRentInfoResponse) {
                response.data?.let { setPaymentInfo(it) }
                rentInfo = response.data!!
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
    private fun setPaymentInfo(info: TenantRentInfo) {

        try {
            rentAmount = 0.0

            if (!info.paybleRent.isNullOrEmpty()) {
                payment_total_unit_rent.text = "$" + info.paybleRent
                rentAmount = info.paybleRent.toDouble()
            }
            if (!info.totalSecurityAmount.isNullOrEmpty())
                payment_total_security.text = "$" + info.totalSecurityAmount
            autoPay = info.autoPay

            showHideViews()
            if (!info.paybleRent.isNullOrEmpty())
                payment_my_monthly_rent.text = "$" + info.paybleRent
            if (!info.rentPercentage.isNullOrEmpty())
                payment_my_rent_share.text = info.rentPercentage + "%"

            //rent_date.text = "Rent for date" + info.nextRentDate


            if (info.breakup.rentPaid) {
                bottom_pay_view.visibility = View.GONE
                no_due_text.visibility = View.VISIBLE
            } else {

                bottom_pay_view.visibility = View.VISIBLE
                no_due_text.visibility = View.GONE

                breakUpPayload = info.breakup

                lateFee = 0.0
                /*for (item in info.breakup.lateFeeMonthWise) {
                    if (!item.amount.isNullOrEmpty()) {
                        lateFee += item.amount!!.toDouble()
                    }
                }*/

                payment_breakdown_late_fee.setText("$" + info.lateFee)

                discount = info.discount.toDouble()

                if (!info.breakup.rentMonthWise.discount.isNullOrEmpty()) {
                    discount = info.breakup.rentMonthWise.discount!!.toDouble()
                }

                //            if (lateFee == 0.0) {
                //                payment_breakdown_late_fee_view.visibility = View.GONE
                //            } else {
                //                payment_breakdown_late_fee_view.visibility = View.VISIBLE
                //            }

                /*if (discount == 0.0) {
                    payment_breakdown_discount_view.visibility = View.GONE
                } else {
                    payment_breakdown_discount_view.visibility = View.VISIBLE
                    if(!info.breakup.rentMonthWise.discount.isNullOrEmpty())
                    payment_breakdown_discount.text = "- $" + info.breakup.rentMonthWise.discount
                }*/

                payment_breakdown_discount_text.setText("Discount (" + info.discount + "%)")
                discountedAmount = (rentAmount * discount) / 100

                payment_breakdown_discount.setText("$" + discountedAmount.toString())

                prevDue = 0.0
                if (!info.breakup.previousDue.isNullOrEmpty()) {
                    prevDue = info.breakup.previousDue.toDouble()
                }

                if (prevDue == 0.0) {
                    payment_breakdown_previous_due_view.visibility = View.GONE
                } else {
                    payment_breakdown_previous_due_view.visibility = View.VISIBLE

                    payment_breakdown_previous_due_breakdown.removeAllViews()

                    val vi: LayoutInflater? = layoutInflater

                    if (info.breakup.rentDueMonthWise.size != 0) {
                        for (item in info.breakup.rentDueMonthWise) {
                            if (!item.amount.isNullOrEmpty()) {

                                val view: View? = vi?.inflate(
                                    R.layout.item_previous_rent_breakup,
                                    payment_breakdown_previous_due_breakdown,
                                    false
                                )

                                val title: TextView? =
                                    view?.findViewById(R.id.item_previos_due_title)
                                val value: TextView? =
                                    view?.findViewById(R.id.item_previos_due_value)
                                payment_breakdown_previous_due_breakdown.addView(view)
                                title!!.text = "Rent " + "(" + item.monthName + ")"
                                value!!.text = "$" + item.amount

                            }
                        }
                    }
                    if (info.breakup.lateFeeMonthWise.size != 0) {

                        for (item in info.breakup.lateFeeMonthWise) {
                            if (!item.amount.isNullOrEmpty()) {
                                val view: View? = vi?.inflate(
                                    R.layout.item_previous_rent_breakup,
                                    payment_breakdown_previous_due_breakdown,
                                    false
                                )
                                val title: TextView? =
                                    view?.findViewById(R.id.item_previos_due_title)
                                val value: TextView? =
                                    view?.findViewById(R.id.item_previos_due_value)
                                payment_breakdown_previous_due_breakdown.addView(view)
                                title!!.text = "Late Fee " + "(" + item.monthName + ")"
                                value!!.text = "$" + item.amount
                            }
                        }
                    }


                }




                var dueDate : String = info.rentDueMonth + "-" + info.rentDueDay + "-" + info.rentDueYear;

                monthValue = info.rentDueMonth
                yearValue = info.rentDueYear

                payment_payble_rent_due_date.setText(dueDate)

                month = Util.getMonthStamp(info.rentDueDate)
                year = Util.getYearStamp(info.rentDueDate)

                Log.d("TAG", "year is $year")

                //payment_breakdown_monthly_rent.text = "$" + info.paybleRent
                payment_breakdown_monthly_rent.setText("$" + info.paybleRent)
                payloadRent = info.breakup.rentMonthWise.amount

                amenities = 0.0
                if (!info.breakup.rentMonthWise.amenities.isNullOrEmpty()) {
                    amenities = info.breakup.rentMonthWise.amenities!!.toDouble()
                }

                if (amenities == 0.0) {
                    payment_breakdown_amenities_view.visibility = View.GONE
                } else {
                    payment_breakdown_amenities_view.visibility = View.VISIBLE
                    payment_breakdown_amenities.text = "$" + info.breakup.rentMonthWise.amenities
                }
                payloadAmenities = info.breakup.rentMonthWise.amenities


                // As no need to shoe late fee ,it should be in previous due break up, so comment below line
                //            payment_breakdown_late_fee.text = lateFee.toString()

                payment_breakdown_previous_due.text = "$" + info.breakup.previousDue
                //payment_breakdown_calonex_fee.text = "$" + info.tenantServiceFee.toInt().toString()

                //payment_breakdown_calonex_fee.setText("$" + info.tenantServiceFee.toDouble().toString())
                //payloadCalonexFee = info.breakup.serviceFee

                serviceFee = info.tenantServiceFee.toDouble()
                payloadCalonexFee = String.format("%.2f", serviceFee)
                payment_breakdown_calonex_fee.setText("$" + payloadCalonexFee)

                payment_rent_paying?.setText(info.totalPayableAmount)

                if (info.breakup.freeMonth) {
                    bottom_pay_view.visibility = View.GONE
                    no_due_text.visibility = View.VISIBLE

                    no_due_text!!.text = "You are entitled for a free monthly rent."
                } else
                    bottom_pay_view.visibility = View.VISIBLE


                totalPayableAmount = rentAmount + lateFee - discountedAmount + serviceFee

                if (!info.paybleRent.isNullOrEmpty()) {
                    payment_breakdown_amount_payable.text = "$" + totalPayableAmount
                    //totalPayableAmount = info.paybleRent.toDouble()
                }
                else
                    totalPayableAmount = 0.0

                payment_rent_paying?.setText(totalPayableAmount.toString())
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

                    payment_action_pay.setText("Paid")

                    val builder = AlertDialog.Builder(this@TenantPayRentActivity)
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
                        this@TenantPayRentActivity,
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
                    this@TenantPayRentActivity,
                    getString(R.string.payment_failed),
                    "Your Payment was not successful, please try again later"
                )
                Log.e("onFailure", t.toString())
            }
        })


    }


}
