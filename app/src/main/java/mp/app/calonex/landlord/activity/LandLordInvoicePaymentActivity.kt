package mp.app.calonex.landlord.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_land_lord_invoice_payment.*

import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.InvoiceDetailsByIDCred
import mp.app.calonex.common.apiCredentials.NotificationCredential
import mp.app.calonex.common.apiCredentials.PayRentCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.model.AppNotifications
import mp.app.calonex.landlord.model.NotificationLinkRequestModel
import mp.app.calonex.landlord.model.NotificationLinkRequestModelNew
import mp.app.calonex.landlord.response.AppNotificationResponse
import mp.app.calonex.tenant.model.InvoiceDetailsByIDResponse
import mp.app.calonex.tenant.model.InvoiceDetailsDao
import mp.app.calonex.tenant.response.ResponseDtoResponse
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*

class LandLordInvoicePaymentActivity  : CxBaseActivity2() {

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
    var ProfilPic: CircleImageView?=null
    var backButton:ImageView?=null
    var notificationLayout: RelativeLayout?=null
    var txtNotification: TextView?=null
    var linkRequestListt = java.util.ArrayList<AppNotifications>()
    var linkRequestListForLdReq = java.util.ArrayList<AppNotifications>()
    var alertsListt = java.util.ArrayList<AppNotifications>()
    var messageList = java.util.ArrayList<AppNotifications>()
    var appNotifications = java.util.ArrayList<AppNotifications>()
    var mNotificationLinkRequest = NotificationLinkRequestModel()
    var mNotificationLinkRequestNew = NotificationLinkRequestModelNew()


    var lateFeePaying: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_land_lord_invoice_payment)
        init()
        actionComponent()
        getPaymentInfo()

    }

    fun init(){
        notificationLayout=findViewById(R.id.layout_cx_notify)
        txtNotification=findViewById(R.id.txt_cx_notify)
        backButton=findViewById(R.id.iv_back)
        ProfilPic=findViewById(R.id.profile_pic)
        notificationLayout=findViewById(R.id.layout_cx_notify)
        txtNotification=findViewById(R.id.txt_cx_notify)


        backButton?.setOnClickListener {
            finish()
        }
        Glide.with(this)
            .load(Kotpref.profile_image)
            .placeholder(R.drawable.profile_default_new)
            .into(ProfilPic!!)

        notificationLayout!!.setOnClickListener {
            Util.navigationNext(this@LandLordInvoicePaymentActivity, NotifyScreen::class.java)
        }
    }

    override fun onResume() {
        super.onResume()
        getNotificationList()
    }

    private fun getNotificationList() {

        this.getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        val credentials = NotificationCredential()

        credentials.userCatalog = Kotpref.userId
        val notifyListService: ApiInterface =
            ApiClient(this).provideService(ApiInterface::class.java)
        val apiCall: Observable<AppNotificationResponse> =
            notifyListService.getAppNotifications(credentials) //Test API Key 3600248I

        RxAPICallHelper().call(apiCall, object : RxAPICallback<AppNotificationResponse> {
            override fun onSuccess(response: AppNotificationResponse) {
                Log.e("onSuccess", response.responseCode.toString())

                if (!response.data?.isEmpty()!!) {
                    appNotifications = response.data!!
                    linkRequestListt.clear()
                    alertsListt.clear()
                    messageList.clear()
                    for (i in 0..appNotifications.size - 1) {
                        val gson = Gson()

                        if (appNotifications[i].activityType == "1") {
                            val objectList = gson.fromJson(
                                appNotifications[i].notificationData,
                                NotificationLinkRequestModel::class.java
                            )
                            mNotificationLinkRequest = objectList
                            linkRequestListt.add(appNotifications[i])
                        } else if (appNotifications[i].activityType == "2") {
                            alertsListt.add(appNotifications[i])
                        } else if (appNotifications[i].activityType == "3") {
                            messageList.add(appNotifications[i])
                        }
                    }
                }

                Kotpref.notifyCount = (linkRequestListt.size + alertsListt.size).toString()
                txtNotification!!.text = Kotpref.notifyCount
                Log.e("onSuccess", "Notification Count in Tenant Receipts "+Kotpref.notifyCount)


                this@LandLordInvoicePaymentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                if (Integer.parseInt(Kotpref.notifyCount) < 1) {
                    txtNotification!!.visibility = View.GONE

                } else {
                    txtNotification!!.visibility = View.VISIBLE

                }
            }

            override fun onFailed(throwable: Throwable) {
                // show error
                Log.e("onFailure", throwable.toString())

                this@LandLordInvoicePaymentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                txtNotification!!.text =
                    (linkRequestListt.size + alertsListt.size).toString()
                if (Integer.parseInt(Kotpref.notifyCount) < 1) {
                    txtNotification!!.visibility = View.GONE

                } else {
                    txtNotification!!.visibility = View.VISIBLE

                }
            }
        })
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
                    this@LandLordInvoicePaymentActivity,
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
                response.data?.let { setPaymentInfo(it) }
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

        try {
            tv_invoice_from.text = "" + info.fromName
            tv_unit_location.text = " " + info.fromAddress
            tv_phone_number.text = " " + info.fromMobileNo
            tv_email.text = " " + info.fromEmailAddress
            tv_invoice_number.text = "#INV" + info.invoiceId
            tv_invoice_date.text = " " + info.invoiceDate
            tv_invoice_due_date.text = " " + info.dueDate

            if (info.finalAmount != null && info.finalAmount != "null") {
                payment_my_monthly_rent.text = "$" + info.finalAmount.toBigDecimal().setScale(2, RoundingMode.UP)
                    .toDouble()
            } else {
                payment_my_monthly_rent.text = "$0"
            }


            tv_invoice_to.text = "" + info.name
            tv_tenant_location.text = " " + info.fromAddress
            tv_tenant_phone_number.text = " " + info.mobileNo
            if (info.totalCharge != null && info.totalCharge != "null") {
                tv_monthly_rent.text = "$" + info.totalCharge.toBigDecimal().setScale(2, RoundingMode.UP)
                    .toDouble()
            } else {
                tv_monthly_rent.text = "$0"
            }
            if (info.securityDeposit != null && info.securityDeposit != "null") {
                payment_total_security.text = "$" + info.securityDeposit.toBigDecimal().setScale(2, RoundingMode.UP)
                    .toDouble()
            } else {
                payment_total_security.text = "$0"
            }
            if (info.serviceFee != null && info.serviceFee != "null") {
                tv_tenant_service_fees.text = "$" + info.serviceFee.toBigDecimal().setScale(2, RoundingMode.UP)
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
                payment_total_unit_rent.text = "$" + info.totalCharge.toBigDecimal().setScale(2, RoundingMode.UP)
                    .toDouble()
            } else {
                payment_total_unit_rent.text = "$0"
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

                    val builder = AlertDialog.Builder(this@LandLordInvoicePaymentActivity)
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
                        this@LandLordInvoicePaymentActivity,
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
                    this@LandLordInvoicePaymentActivity,
                    getString(R.string.payment_failed),
                    "Your Payment was not successful, please try again later"
                )
                Log.e("onFailure", t.toString())
            }
        })


    }


}