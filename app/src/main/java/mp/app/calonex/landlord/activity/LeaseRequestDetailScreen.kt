package mp.app.calonex.landlord.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_lease_details_for_tenant.*
import kotlinx.android.synthetic.main.activity_lease_request_detail_screen.*
import kotlinx.android.synthetic.main.activity_lease_request_detail_screen.txt_interested_for_lease
import kotlinx.android.synthetic.main.activity_tenant_lease_details.*
import kotlinx.android.synthetic.main.dialog_reject.view.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.SignatureLeaseCredentials
import mp.app.calonex.common.constant.StatusConstant
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.common.utility.Util.getLeaseStatus
import mp.app.calonex.landlord.activity.LandlordSignScreen.Companion.signLeaseInfo
import mp.app.calonex.landlord.activity.LandlordSignScreen.Companion.signListSignature
import mp.app.calonex.landlord.adapter.LeaseRequestDetailAdapter
import mp.app.calonex.landlord.adapter.NotificationAlertsAdapter.Companion.alertLeaseInfo
import mp.app.calonex.landlord.adapter.NotificationAlertsAdapter.Companion.alertListSignature
import mp.app.calonex.landlord.adapter.NotificationAlertsAdapter.Companion.alertSecurityList
import mp.app.calonex.landlord.adapter.RefundSecurityAdapter
import mp.app.calonex.landlord.adapter.SignatureLeaseAdapter
import mp.app.calonex.landlord.adapter.TenantLeaseRequestAdapter.Companion.leaseRequestInfo
import mp.app.calonex.landlord.adapter.TenantLeaseRequestAdapter.Companion.leaseSecurityList
import mp.app.calonex.landlord.adapter.TenantLeaseRequestAdapter.Companion.signList
import mp.app.calonex.landlord.adapter.UnitDescAdapter.Companion.unitLeaseInfo
import mp.app.calonex.landlord.adapter.UnitDescAdapter.Companion.unitListSignature
import mp.app.calonex.landlord.adapter.UnitDescAdapter.Companion.unitSecurityList
import mp.app.calonex.landlord.model.*
import mp.app.calonex.landlord.response.BrkrTenantLeaseActionResponse
import mp.app.calonex.landlord.response.SecurityFetchResponse
import mp.app.calonex.tenant.activity.TenantLeaseAgreementDetailsActivity
import mp.app.calonex.tenant.response.ResponseDtoResponse
import mp.app.calonex.tenant.response.TenantPOnboardAddUpdateResponse
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class LeaseRequestDetailScreen : CxBaseActivity2() {
    private var isLeaseUndoTerminate=false
    private var txtLrdProperty: TextView? = null
    private var txtLrdUnit: TextView? = null
    private var txtLrdLeaseStatus: TextView? = null
    private var txtLrdLeaseTerminate: TextView? = null
    private var cvLease: CardView? = null
    private var layoutEditLease: LinearLayout? = null
    private var rvLrdTenant: RecyclerView? = null
    private var rvLrdSignature: RecyclerView? = null
    private var txtLrdRentMnth: TextView? = null
    private var txtLrdSecurityAmt: TextView? = null
    private var txtLrdLateFee: TextView? = null
    private var txtLrdLeaseStartDate: TextView? = null
    private var txtLrdCreationDate: TextView? = null
    private var txtLrdLeaseDuration: TextView? = null
    private var txtLrdMonthFree: TextView? = null
    private var txtLrdRentBeforDueDate: TextView? = null
    private var tenantRdArrayList = ArrayList<ObTenantPayload>()
    private var layoutBrkr: LinearLayout? = null
    private var layoutAgent: LinearLayout? = null
    private var txtLrdBrkrName: TextView? = null
    private var txtLrdBrkrLicence: TextView? = null
    private var txtLrdBrkrEmail: TextView? = null
    private var txtLrdAgntName: TextView? = null
    private var txtLrdAgntLicence: TextView? = null
    private var txtLrdAgntEmail: TextView? = null
    private var leaseListAdapter: LeaseRequestDetailAdapter? = null
    private var leaseSignAdapter: SignatureLeaseAdapter? = null

    private var listSignature = ArrayList<LeaseSignature>()
    private var listSecurity = ArrayList<FetchSecurityInfo>()

    private var editLrdRent: TextInputEditText? = null
    private var editLrdSecurity: TextInputEditText? = null
    private var editLrdAmenities: TextInputEditText? = null
    private var editLrdLateFees: TextInputEditText? = null
    private var editLrdStartDate: TextInputEditText? = null
    private var editLrdEndDate: TextInputEditText? = null
    private var editLrdDuration: TextInputEditText? = null
    private var editLrdRentDueDate: TextInputEditText? = null
    private var editLrdMonthFree: TextInputEditText? = null
    private var pbLrdLease: ProgressBar? = null
    private var layoutBrkrLeaseBtn: LinearLayout? = null
    private var btnBrkrLeaseUpdate: Button? = null
    private var btnBrkrLeaseReject: Button? = null
    private var btnBrkrLeaseApprove: Button? = null
    private var layoutTenantLeaseBtn: LinearLayout? = null
    private var btnTenantLeaseReject: Button? = null
    private var btnTenantLmr: Button? = null
    private var btnTenantLma: Button? = null
    private var btnLdSign: Button? = null
    private var btnRfndRtrn: Button? = null
    private var btnLrdLeaseUpdate: TextView? = null

    private var lrdRent: String? = null
    private var lrdSecurityDeposit: String? = null
    private var lrdAmenities: String? = null
    private var lrdLateFees: String? = null
    private var lrdStartDate: String? = null
    private var lrdEndDate: String? = null
    private var lrdDuration: String? = null
    private var lrdRentDueDate: String? = null
    private var lrdMonthFree: String? = null
    private var cal = Calendar.getInstance()
    private var year = cal.get(Calendar.YEAR)
    private var month = cal.get(Calendar.MONTH)
    private var day = cal.get(Calendar.DAY_OF_MONTH)
    val myFormat = "MMM dd, yyyy"
    val sdf = SimpleDateFormat(myFormat, Locale.US)
    private var obLeaseModify = ObLeaseTenantPayload()

    private var txtLrdData1: TextView? = null
    private var txtLrdData2: TextView? = null
    private var txtLrdData3: TextView? = null
    private var txtLrdData4: TextView? = null
    private var txtLrdData5: TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lease_request_detail_screen)
        try {
            initComponent()
            actionComponent()
            startHandler()
        } catch (e: Exception) {
            Log.e("LeaseDeatil = ", e.message.toString())
            e.printStackTrace()
        }
    }

    private fun initComponent() {
        layoutBrkr = findViewById(R.id.layout_broker)
        layoutAgent = findViewById(R.id.layout_agent)
        txtLrdProperty = findViewById(R.id.txt_lrd_property_add)
        txtLrdUnit = findViewById(R.id.txt_lrd_unit)
        txtLrdLeaseStatus = findViewById(R.id.txt_lrd_lease_status)
        txtLrdLeaseTerminate = findViewById(R.id.txt_lrd_lease_terminate)
        cvLease = findViewById(R.id.cv_lease)
        layoutEditLease = findViewById(R.id.layout_edit_lease)
        rvLrdTenant = findViewById(R.id.rv_lrd_tenant)
        rvLrdSignature = findViewById(R.id.rv_lrd_signature)
        txtLrdRentMnth = findViewById(R.id.txt_lrd_rent_mnth)
        txtLrdSecurityAmt = findViewById(R.id.txt_lrd_security_amt)
        txtLrdLateFee = findViewById(R.id.txt_lrd_late_fee)
        txtLrdLeaseStartDate = findViewById(R.id.txt_lrd_lease_start_date)
        txtLrdCreationDate = findViewById(R.id.txt_lrd_lease_creation_date)
        txtLrdLeaseDuration = findViewById(R.id.txt_lrd_lease_duration)
        txtLrdMonthFree = findViewById(R.id.txt_lrd_lease_free_mnth)
        txtLrdRentBeforDueDate = findViewById(R.id.txt_lc_rent_bfor_due_date)
        txtLrdBrkrName = findViewById(R.id.txt_lrd_brkr_name)
        txtLrdBrkrLicence = findViewById(R.id.txt_lrd_brkr_licence)
        txtLrdBrkrEmail = findViewById(R.id.txt_lrd_brkr_email)
        txtLrdAgntName = findViewById(R.id.txt_lrd_agnt_name)
        txtLrdAgntLicence = findViewById(R.id.txt_lrd_agnt_licence)
        txtLrdAgntEmail = findViewById(R.id.txt_lrd_agnt_email)
        editLrdRent = findViewById(R.id.edit_lrd_rent)
        editLrdSecurity = findViewById(R.id.edit_lrd_security)
        editLrdAmenities = findViewById(R.id.edit_lrd_amenities)
        editLrdLateFees = findViewById(R.id.edit_lrd_late_fees)
        editLrdStartDate = findViewById(R.id.edit_lrd_start_date)
        editLrdEndDate = findViewById(R.id.edit_lrd_end_date)
        editLrdDuration = findViewById(R.id.edit_lrd_duration)
        editLrdRentDueDate = findViewById(R.id.edit_lrd_rent_due_date)
        editLrdMonthFree = findViewById(R.id.edit_lrd_month_free)
        pbLrdLease = findViewById(R.id.pb_lrd_lease)
        layoutBrkrLeaseBtn = findViewById(R.id.layout_brkr_lease_btn)
        btnBrkrLeaseUpdate = findViewById(R.id.btn_lrd_brkr_lease_update)
        btnBrkrLeaseReject = findViewById(R.id.btn_lrd_brkr_lease_reject)
        btnBrkrLeaseApprove = findViewById(R.id.btn_lrd_brkr_lease_approve)
        btnLrdLeaseUpdate = findViewById(R.id.btn_lrd_lease_update)

        layoutTenantLeaseBtn = findViewById(R.id.layout_tenant_lease_btn)
        btnTenantLmr = findViewById(R.id.btn_lrd_tenant_lmr)
        btnTenantLeaseReject = findViewById(R.id.btn_lrd_tenant_lease_rejct)
        btnTenantLma = findViewById(R.id.btn_lrd_tenant_lma)
        btnLdSign = findViewById(R.id.btn_lrd_ld_sign)
        btnRfndRtrn = findViewById(R.id.btn_rfnd_rtrn)

        // txtLrdData1 = findViewById(R.id.txt_lrd_lease_create_date)
        //  txtLrdData2 = findViewById(R.id.txt_lrd_monthly_ret)
        txtLrdData3 = findViewById(R.id.txt_lrd_lease_discount_befor_5th)
        txtLrdData4 = findViewById(R.id.txt_lrd_rent_gross)
        txtLrdData5 = findViewById(R.id.txt_lrd_lease_rent_net)

        Util.setEditReadOnly(editLrdEndDate!!, false, InputType.TYPE_NULL)

        when {
            intent.getBooleanExtra(getString(R.string.intent_tenant_req_adap), false) -> {
                requestLeaseInfo = leaseRequestInfo
                listSignature = signList
                listSecurity = leaseSecurityList
            }
            intent.getBooleanExtra(getString(R.string.intent_alert_lease), false) -> {
                requestLeaseInfo = alertLeaseInfo
                listSignature = alertListSignature
                listSecurity = alertSecurityList
            }
            intent.getBooleanExtra(getString(R.string.intent_unit_lease), false) -> {
                requestLeaseInfo = unitLeaseInfo
                listSignature = unitListSignature
                listSecurity = unitSecurityList
            }
            intent.getBooleanExtra(getString(R.string.intent_sign_lease), false) -> {
                requestLeaseInfo = signLeaseInfo
                listSignature = signListSignature

            }

        }

        txtLrdProperty!!.text =
            requestLeaseInfo.propertyAddress + ", " + requestLeaseInfo.propertyCity + ", " + requestLeaseInfo.propertyState + ", " + requestLeaseInfo.propertyZipCode
        txtLrdUnit!!.text = getString(R.string.unit_num) + requestLeaseInfo.unitNumber

        updateUI()

    }

    private fun actionComponent() {
        txt_interested_for_lease.setOnClickListener {
            if (requestLeaseInfo?.leaseStatus == StatusConstant.Modified_Landlord_Approval_Pending) {
                Util.alertOkMessage(
                    this,
                    getString(R.string.alert),
                    "Landlord status is not approved yet."
                )
                return@setOnClickListener
            }

            val intent =
                Intent(this, TenantLeaseAgreementDetailsActivity::class.java)

            intent.putExtra("leaseId", requestLeaseInfo!!.leaseId)
            intent.putExtra("from", "LeaseRequestDetailScreen")
            startActivity(intent)
        }

        btnBrkrLeaseUpdate!!.setOnClickListener {
            updateLeaseDialog()
        }
        btnLrdLeaseUpdate!!.setOnClickListener {
            updateLeaseDialog()
        }

        editLrdStartDate!!.setOnTouchListener { view, motionEvent ->

            val dateDialog = DatePickerDialog(
                this,
                { view, year, monthOfYear, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    editLrdStartDate!!.setText(sdf.format(cal.getTime()))

                },
                year,
                month,
                day
            )
            dateDialog.show()

            if (!Kotpref.userRole.contains("landlord",true)){
                dateDialog.dismiss()
            }
           // dateDialog.dismiss()
            dateDialog.getDatePicker().setMinDate(System.currentTimeMillis())
            true
        }



        editLrdDuration!!.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (!s.toString().isEmpty())
                {
                lrdDuration = editLrdDuration!!.text.toString().trim()
                cal.add(Calendar.MONTH + 1, lrdDuration!!.toInt())
                editLrdEndDate!!.setText(sdf.format(cal.getTime()))
                }
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {

            }
        })

        btnBrkrLeaseApprove!!.setOnClickListener {
            leaseBrkrTenantAction("Approved", "")
        }
        btnBrkrLeaseReject!!.setOnClickListener {
            rejctDialog()
        }

        btnTenantLma!!.setOnClickListener {
            leaseTenantAction("Approved")
        }
        btnTenantLmr!!.setOnClickListener {
            leaseTenantAction("Rejected")
        }

        btnTenantLeaseReject!!.setOnClickListener {
            rejctDialog()
        }

        txtLrdLeaseTerminate!!.setOnClickListener {
            terminateDialog()
        }

        btnLdSign!!.setOnClickListener {
         if (!Kotpref.bankAdded) {
            bankAddDialog()
            } else {
                val intent = Intent(this, LandlordSignScreen::class.java)
                intent.putExtra(getString(R.string.intent_lease_id), requestLeaseInfo.leaseId)
                startActivity(intent)
            }
        }
        btnRfndRtrn!!.setOnClickListener {
            refndDialog()
        }

    }

    private fun updateUI() {

        // non exiting tenant requested by broker or agent

        Log.e("lease details","="+Gson().toJson(requestLeaseInfo))
        if (requestLeaseInfo.leaseStatus==null &&(requestLeaseInfo.brokerId!=null ||requestLeaseInfo.agentId!=null) ){

            cvLease!!.visibility = View.GONE
            layoutEditLease!!.visibility = View.VISIBLE

            layoutBrkrLeaseBtn!!.visibility = View.VISIBLE
            txt_interested_for_lease.visibility=View.GONE
            btnLrdLeaseUpdate!!.visibility = View.GONE

            txtLrdRentMnth!!.text = "$ " + requestLeaseInfo.rentAmount
            txtLrdSecurityAmt!!.text = "$ " + requestLeaseInfo.securityAmount

            if (!requestLeaseInfo.lateFee.isNullOrEmpty())
                txtLrdLateFee!!.text = "$ " + requestLeaseInfo.lateFee
            txtLrdLeaseStartDate!!.text = Util.convertLongToTime(
                requestLeaseInfo.leaseStartDate.toLong(),
                "MMM dd, yyyy"
            )
            txtLrdLeaseDuration!!.text = requestLeaseInfo.leaseDuration
            txtLrdMonthFree!!.text = requestLeaseInfo.monthsFree
            if (requestLeaseInfo.rentBeforeDueDate != null)
                txtLrdRentBeforDueDate!!.text = "$ " + requestLeaseInfo.rentBeforeDueDate

            txtLrdCreationDate!!.text = Util.convertLongToTime(
                requestLeaseInfo.createdOn.toLong(),
                "MMM dd, yyyy"
            )

            if (listSecurity.size > 0) {

/*
    txtLrdData1!!.text = Util.convertLongToTime(
        listSecurity[0].securityInitiatedOn!!.toLong(),
        "MMM dd, yyyy"
    )
*/


            }

            if (requestLeaseInfo.monthsFree == null) {
                requestLeaseInfo.monthsFree = "0";
            }

            val netAmount =
                ((requestLeaseInfo.leaseDuration.toBigDecimal().setScale(2, RoundingMode.UP)
                    .toDouble() -
                        requestLeaseInfo.monthsFree.toBigDecimal().setScale(2, RoundingMode.UP)
                            .toDouble()) *
                        requestLeaseInfo.rentAmount.toBigDecimal().setScale(2, RoundingMode.UP)
                            .toDouble()).toBigDecimal().setScale(2, RoundingMode.UP).toDouble()

            /*   txtLrdData2!!.text= "$ " + (netAmount / requestLeaseInfo.leaseDuration.toBigDecimal().setScale(2, RoundingMode.UP).toDouble())
                   .toBigDecimal().setScale(2, RoundingMode.UP).toDouble().toBigDecimal().setScale(2, RoundingMode.UP).toDouble().toString()
   */
            txtLrdData3!!.text = "$ " + requestLeaseInfo.rentBeforeDueDate.toBigDecimal()
                .setScale(2, RoundingMode.UP).toDouble()
            txtLrdData4!!.text = "$ " + (requestLeaseInfo.rentAmount.toBigDecimal().setScale(2, RoundingMode.UP)
                .toDouble() *
                    requestLeaseInfo.leaseDuration.toBigDecimal().setScale(2, RoundingMode.UP)
                        .toDouble()).toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
                .toString()
            txtLrdData5!!.text = "$ " + netAmount.toBigDecimal().setScale(2, RoundingMode.UP).toDouble().toString()
            txtLrdLeaseStatus!!.text = getLeaseStatus("DND")
            if (requestLeaseInfo.brokerId.isNullOrEmpty()) {
                layoutBrkr!!.visibility = View.GONE
            } else {
                txtLrdBrkrName!!.text = requestLeaseInfo.brokerName
                //txtLrdBrkrLicence!!.text=requestLeaseInfo.brokerLicenseNumber
                txtLrdBrkrLicence!!.text = requestLeaseInfo.brokerId
                txtLrdBrkrEmail!!.text = requestLeaseInfo.brokerEmailId
            }
            if (requestLeaseInfo.agentId.isNullOrEmpty()) {
                layoutAgent!!.visibility = View.GONE
            } else {
                txtLrdAgntName!!.text = requestLeaseInfo.agentName
                //txtLrdAgntLicence!!.text=requestLeaseInfo.agentLicenseNumber
                txtLrdAgntLicence!!.text = requestLeaseInfo.agentId
                txtLrdAgntEmail!!.text = requestLeaseInfo.agentEmailId
            }

            editLrdRent!!.setText(requestLeaseInfo.rentAmount)
            editLrdSecurity!!.setText(requestLeaseInfo.securityAmount)

            editLrdLateFees!!.setText(requestLeaseInfo.lateFee)
            editLrdStartDate!!.setText(
                Util.convertLongToTime(
                    requestLeaseInfo.leaseStartDate.toLong(),
                    "MMM dd, yyyy"
                )
            )
            editLrdDuration!!.setText(requestLeaseInfo.leaseDuration)
            editLrdMonthFree!!.setText(requestLeaseInfo.monthsFree)
            editLrdRentDueDate!!.setText(requestLeaseInfo.rentBeforeDueDate)
            editLrdAmenities!!.setText(requestLeaseInfo.amenities)
            editLrdEndDate!!.setText(
                Util.convertLongToTime(
                    requestLeaseInfo.leaseEndDate.toLong(),
                    "MMM dd, yyyy"
                )
            )
            tenantRdArrayList = requestLeaseInfo.tenantBaseInfoDto


            return
        }
        // non exiting tenant requested by landlord
        if (requestLeaseInfo.leaseStatus==null && requestLeaseInfo.brokerId==null &&requestLeaseInfo.agentId==null ){

            cvLease!!.visibility = View.GONE
            layoutEditLease!!.visibility = View.VISIBLE

            layoutBrkrLeaseBtn!!.visibility = View.GONE
            txt_interested_for_lease.visibility=View.VISIBLE
            btnLrdLeaseUpdate!!.visibility = View.VISIBLE

            txtLrdRentMnth!!.text = "$ " + requestLeaseInfo.rentAmount
            txtLrdSecurityAmt!!.text = "$ " + requestLeaseInfo.securityAmount

            if (!requestLeaseInfo.lateFee.isNullOrEmpty())
                txtLrdLateFee!!.text = "$ " + requestLeaseInfo.lateFee
            txtLrdLeaseStartDate!!.text = Util.convertLongToTime(
                requestLeaseInfo.leaseStartDate.toLong(),
                "MMM dd, yyyy"
            )
            txtLrdLeaseDuration!!.text = requestLeaseInfo.leaseDuration
            txtLrdMonthFree!!.text = requestLeaseInfo.monthsFree
            if (requestLeaseInfo.rentBeforeDueDate != null)
                txtLrdRentBeforDueDate!!.text = "$ " + requestLeaseInfo.rentBeforeDueDate

            txtLrdCreationDate!!.text = Util.convertLongToTime(
                requestLeaseInfo.createdOn.toLong(),
                "MMM dd, yyyy"
            )

            if (listSecurity.size > 0) {

/*
    txtLrdData1!!.text = Util.convertLongToTime(
        listSecurity[0].securityInitiatedOn!!.toLong(),
        "MMM dd, yyyy"
    )
*/


            }

            if (requestLeaseInfo.monthsFree == null) {
                requestLeaseInfo.monthsFree = "0";
            }

            val netAmount =
                ((requestLeaseInfo.leaseDuration.toBigDecimal().setScale(2, RoundingMode.UP)
                    .toDouble() -
                        requestLeaseInfo.monthsFree.toBigDecimal().setScale(2, RoundingMode.UP)
                            .toDouble()) *
                        requestLeaseInfo.rentAmount.toBigDecimal().setScale(2, RoundingMode.UP)
                            .toDouble()).toBigDecimal().setScale(2, RoundingMode.UP).toDouble()

            /*   txtLrdData2!!.text= "$ " + (netAmount / requestLeaseInfo.leaseDuration.toBigDecimal().setScale(2, RoundingMode.UP).toDouble())
                   .toBigDecimal().setScale(2, RoundingMode.UP).toDouble().toBigDecimal().setScale(2, RoundingMode.UP).toDouble().toString()
   */
            txtLrdData3!!.text = "$ " + requestLeaseInfo.rentBeforeDueDate.toBigDecimal()
                .setScale(2, RoundingMode.UP).toDouble()
            txtLrdData4!!.text = "$ " + (requestLeaseInfo.rentAmount.toBigDecimal().setScale(2, RoundingMode.UP)
                .toDouble() *
                    requestLeaseInfo.leaseDuration.toBigDecimal().setScale(2, RoundingMode.UP)
                        .toDouble()).toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
                .toString()
            txtLrdData5!!.text = "$ " + netAmount.toBigDecimal().setScale(2, RoundingMode.UP).toDouble().toString()
            txtLrdLeaseStatus!!.text = getLeaseStatus("DND")
            if (requestLeaseInfo.brokerId.isNullOrEmpty()) {
                layoutBrkr!!.visibility = View.GONE
            } else {
                txtLrdBrkrName!!.text = requestLeaseInfo.brokerName
                //txtLrdBrkrLicence!!.text=requestLeaseInfo.brokerLicenseNumber
                txtLrdBrkrLicence!!.text = requestLeaseInfo.brokerId
                txtLrdBrkrEmail!!.text = requestLeaseInfo.brokerEmailId
            }
            if (requestLeaseInfo.agentId.isNullOrEmpty()) {
                layoutAgent!!.visibility = View.GONE
            } else {
                txtLrdAgntName!!.text = requestLeaseInfo.agentName
                //txtLrdAgntLicence!!.text=requestLeaseInfo.agentLicenseNumber
                txtLrdAgntLicence!!.text = requestLeaseInfo.agentId
                txtLrdAgntEmail!!.text = requestLeaseInfo.agentEmailId
            }

            editLrdRent!!.setText(requestLeaseInfo.rentAmount)
            editLrdSecurity!!.setText(requestLeaseInfo.securityAmount)

            editLrdLateFees!!.setText(requestLeaseInfo.lateFee)
            editLrdStartDate!!.setText(
                Util.convertLongToTime(
                    requestLeaseInfo.leaseStartDate.toLong(),
                    "MMM dd, yyyy"
                )
            )
            editLrdDuration!!.setText(requestLeaseInfo.leaseDuration)
            editLrdMonthFree!!.setText(requestLeaseInfo.monthsFree)
            editLrdRentDueDate!!.setText(requestLeaseInfo.rentBeforeDueDate)
            editLrdAmenities!!.setText(requestLeaseInfo.amenities)
            editLrdEndDate!!.setText(
                Util.convertLongToTime(
                    requestLeaseInfo.leaseEndDate.toLong(),
                    "MMM dd, yyyy"
                )
            )
            tenantRdArrayList = requestLeaseInfo.tenantBaseInfoDto


            return
        }

        if ((requestLeaseInfo.leaseStatus.equals("11") || requestLeaseInfo.leaseStatus.equals("12") || requestLeaseInfo.leaseStatus.equals("15"))) {
            cvLease!!.visibility = View.GONE
            layoutEditLease!!.visibility = View.VISIBLE
            if (requestLeaseInfo.leaseStatus.equals("11") || requestLeaseInfo.leaseStatus.equals("12")) {
                layoutBrkrLeaseBtn!!.visibility = View.VISIBLE
            } else {
                btnLrdLeaseUpdate!!.visibility = View.VISIBLE
                if (Kotpref.userRole.contains("landlord",true))
                {
                    txt_interested_for_lease.visibility=View.GONE
                }else{
                    txt_interested_for_lease.visibility=View.VISIBLE
                }

            }
            editLrdRent!!.setText(requestLeaseInfo.rentAmount)
            editLrdSecurity!!.setText(requestLeaseInfo.securityAmount)

            editLrdLateFees!!.setText(requestLeaseInfo.lateFee)
            editLrdStartDate!!.setText(
                Util.convertLongToTime(
                    requestLeaseInfo.leaseStartDate.toLong(),
                    "MMM dd, yyyy"
                )
            )
            editLrdDuration!!.setText(requestLeaseInfo.leaseDuration)
            editLrdMonthFree!!.setText(requestLeaseInfo.monthsFree)
            editLrdRentDueDate!!.setText(requestLeaseInfo.rentBeforeDueDate)
            editLrdAmenities!!.setText(requestLeaseInfo.amenities)
            editLrdEndDate!!.setText(
                Util.convertLongToTime(
                    requestLeaseInfo.leaseEndDate.toLong(),
                    "MMM dd, yyyy"
                )
            )
        } else {

            if (requestLeaseInfo.leaseStatus.equals("33")){
                txtLrdLeaseTerminate!!.text= "Undo Termination"
                isLeaseUndoTerminate=true
                txtLrdLeaseTerminate!!.visibility=View.VISIBLE
            }else{
                txtLrdLeaseTerminate!!.visibility = View.GONE
            }

            cvLease!!.visibility = View.VISIBLE
            layoutEditLease!!.visibility = View.GONE
            layoutBrkrLeaseBtn!!.visibility = View.GONE

            layoutTenantLeaseBtn!!.visibility = View.GONE
            btnLdSign!!.visibility = View.GONE
            btnRfndRtrn!!.visibility = View.GONE
            btnLrdLeaseUpdate!!.visibility = View.GONE
            txt_interested_for_lease.visibility=View.GONE
            txtLrdRentMnth!!.text = "$ " + requestLeaseInfo.rentAmount
            txtLrdSecurityAmt!!.text = "$ " + requestLeaseInfo.securityAmount

            if (!requestLeaseInfo.lateFee.isNullOrEmpty())
                txtLrdLateFee!!.text = "$ " + requestLeaseInfo.lateFee
            txtLrdLeaseStartDate!!.text = Util.convertLongToTime(
                requestLeaseInfo.leaseStartDate.toLong(),
                "MMM dd, yyyy"
            )
            txtLrdLeaseDuration!!.text = requestLeaseInfo.leaseDuration
            txtLrdMonthFree!!.text = requestLeaseInfo.monthsFree
            if (requestLeaseInfo.rentBeforeDueDate != null)
                txtLrdRentBeforDueDate!!.text = "$ " + requestLeaseInfo.rentBeforeDueDate

            txtLrdCreationDate!!.text = Util.convertLongToTime(
                requestLeaseInfo.createdOn.toLong(),
                "MMM dd, yyyy"
            )

            if (listSecurity.size > 0) {

/*
    txtLrdData1!!.text = Util.convertLongToTime(
        listSecurity[0].securityInitiatedOn!!.toLong(),
        "MMM dd, yyyy"
    )
*/

            }

            if (requestLeaseInfo.monthsFree == null) {
                requestLeaseInfo.monthsFree = "0";
            }

            val netAmount = ((requestLeaseInfo.leaseDuration.toBigDecimal().setScale(2, RoundingMode.UP)
                    .toDouble() -
                        requestLeaseInfo.monthsFree.toBigDecimal().setScale(2, RoundingMode.UP)
                            .toDouble()) *
                        requestLeaseInfo.rentAmount.toBigDecimal().setScale(2, RoundingMode.UP)
                            .toDouble()).toBigDecimal().setScale(2, RoundingMode.UP).toDouble()

            /*   txtLrdData2!!.text= "$ " + (netAmount / requestLeaseInfo.leaseDuration.toBigDecimal().setScale(2, RoundingMode.UP).toDouble())
                   .toBigDecimal().setScale(2, RoundingMode.UP).toDouble().toBigDecimal().setScale(2, RoundingMode.UP).toDouble().toString()
   */
            try {
                txtLrdData3!!.text = "$ " + requestLeaseInfo.rentBeforeDueDate.toBigDecimal()
                    .setScale(2, RoundingMode.UP).toDouble()
                txtLrdData4!!.text = "$ " + (requestLeaseInfo.rentAmount.toBigDecimal().setScale(2, RoundingMode.UP)
                    .toDouble() *
                        requestLeaseInfo.leaseDuration.toBigDecimal().setScale(2, RoundingMode.UP)
                            .toDouble()).toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
                    .toString()
                txtLrdData5!!.text = "$ " + netAmount.toBigDecimal().setScale(2, RoundingMode.UP).toDouble().toString()

            }catch (E:Exception){
              Log.e("error",E.toString())
            }


            if (requestLeaseInfo.leaseStatus.equals("19")) {
                txtLrdLeaseStatus!!.setBackgroundResource(R.drawable.btn_green_round)
                txtLrdLeaseTerminate!!.visibility = View.VISIBLE
            }
            if (requestLeaseInfo.leaseStatus.equals("17")) {
                if (Kotpref.userRole.contains("CX-Landlord", true)) {
                    layoutTenantLeaseBtn!!.visibility = View.VISIBLE
                }
            }else{
                layoutTenantLeaseBtn!!.visibility = View.GONE
            }
            if (requestLeaseInfo.leaseStatus.equals("18")) {

                if (Kotpref.userRole.contains("tenant",true)){
                    btnLdSign!!.visibility = View.GONE

                }else{
                    btnLdSign!!.visibility = View.VISIBLE
                }

            }
            if (requestLeaseInfo.leaseStatus.toInt() > 22 && requestLeaseInfo.leaseStatus.toInt() < 31) {
                txtLrdLeaseStatus!!.setBackgroundResource(R.drawable.btn_red_round)
            }
            if (requestLeaseInfo.leaseStatus.equals("23") || requestLeaseInfo.leaseStatus.equals("31")) {
                btnRfndRtrn!!.visibility = View.VISIBLE
            }


        }

        txtLrdLeaseStatus!!.text = getLeaseStatus(requestLeaseInfo.leaseStatus)
        if (requestLeaseInfo.brokerId.isNullOrEmpty()) {
            layoutBrkr!!.visibility = View.GONE
        } else {
            txtLrdBrkrName!!.text = requestLeaseInfo.brokerName
            //txtLrdBrkrLicence!!.text=requestLeaseInfo.brokerLicenseNumber
            txtLrdBrkrLicence!!.text = requestLeaseInfo.brokerId
            txtLrdBrkrEmail!!.text = requestLeaseInfo.brokerEmailId
        }
        if (requestLeaseInfo.agentId.isNullOrEmpty()) {
            layoutAgent!!.visibility = View.GONE
        } else {
            txtLrdAgntName!!.text = requestLeaseInfo.agentName
            //txtLrdAgntLicence!!.text=requestLeaseInfo.agentLicenseNumber
            txtLrdAgntLicence!!.text = requestLeaseInfo.agentId
            txtLrdAgntEmail!!.text = requestLeaseInfo.agentEmailId
        }
        /*txtLrdLeaseTerminate!!.visibility = View.VISIBLE

        if (Kotpref.userRole.contains("Tenant", true)) {
            txtLrdLeaseTerminate!!.visibility = View.GONE
        }*/

        rvLrdTenant?.layoutManager = LinearLayoutManager(applicationContext)
        rvLrdSignature?.layoutManager = LinearLayoutManager(this)
        tenantRdArrayList = requestLeaseInfo.tenantBaseInfoDto
        if (requestLeaseInfo.leaseStatus.toInt() > 23) {
            layout_security_info!!.visibility = View.VISIBLE
        } else {
            layout_security_info!!.visibility = View.GONE
        }
        leaseListAdapter = LeaseRequestDetailAdapter(
            applicationContext,
            this@LeaseRequestDetailScreen,
            requestLeaseInfo.leaseStatus!!.toInt(),
            tenantRdArrayList,
            listSecurity
        )
        rvLrdTenant?.adapter = leaseListAdapter
        leaseSignAdapter = SignatureLeaseAdapter(
            applicationContext,
            requestLeaseInfo.landlordName!!,
            listSignature,
            tenantRdArrayList
        )
        rvLrdSignature?.adapter = leaseSignAdapter
    }

    private fun validComponent() {

        lrdRent = editLrdRent!!.text.toString().trim()
        lrdSecurityDeposit = editLrdSecurity!!.text.toString().trim()
        lrdAmenities = editLrdAmenities!!.text.toString().trim()
        lrdLateFees = editLrdLateFees!!.text.toString().trim()
        lrdStartDate = editLrdStartDate!!.text.toString().trim()
        lrdDuration = editLrdDuration!!.text.toString().trim()
        lrdEndDate = editLrdEndDate!!.text.toString().trim()

        lrdRentDueDate = editLrdRentDueDate!!.text.toString().trim().removeRange(0, 1)
        lrdMonthFree = editLrdMonthFree!!.text.toString().trim()


        if (Util.valueMandetory(applicationContext, lrdLateFees, editLrdLateFees!!)) {
            return
        }

        if (Util.valueMandetory(applicationContext, lrdStartDate, editLrdStartDate!!)) {
            return
        }

        if (!Util.isDateValid(lrdStartDate!!, myFormat)) {
            editLrdStartDate?.error = getString(R.string.error_date_format)
            editLrdStartDate?.requestFocus()
            return
        }
        if (Util.valueMandetory(applicationContext, lrdDuration, editLrdDuration!!)) {
            return
        }

        if (Util.valueMandetory(applicationContext, lrdRentDueDate, editLrdRentDueDate!!)) {
            return
        }

        if (Util.valueMandetory(applicationContext, lrdMonthFree, editLrdMonthFree!!)) {
            return
        }

        if (Util.valueMandetory(applicationContext, lrdRent, editLrdRent!!)) {
            return
        }

        if (Util.valueMandetory(applicationContext, lrdSecurityDeposit, editLrdSecurity!!)) {
            return
        }
        obLeaseModify.modifyByRole=Kotpref.userRole
        obLeaseModify.leaseId = requestLeaseInfo.leaseId
        obLeaseModify.rentAmount = lrdRent
        obLeaseModify.securityAmount = lrdSecurityDeposit
        obLeaseModify.lateFee = lrdLateFees
        obLeaseModify.leaseStartDate =
            Util.convertDateToLong(lrdStartDate!!, myFormat).toString()
        obLeaseModify.leaseDuration = lrdDuration
        obLeaseModify.rentBeforeDueDate = lrdRentDueDate
        obLeaseModify.monthsFree = lrdMonthFree

        obLeaseModify.landlordId = requestLeaseInfo.landlordId
        obLeaseModify.propertyId = requestLeaseInfo.propertyId


        obLeaseModify.brokerId = requestLeaseInfo.brokerId




        obLeaseModify.unitId = requestLeaseInfo.unitId
        obLeaseModify.unitNumber = requestLeaseInfo.unitNumber
        obLeaseModify.securityTransferTo = "CX"

        try {
            for (item in tenantRdArrayList) {
                if (!item.rentPercentage.isNullOrEmpty() && !item.rentPercentage.equals("0") && !item.rentPercentage.equals(
                        "0.00"
                    )
                ) {
                    item.rentAmount = ((item.rentPercentage!!.toDouble()
                        .roundToInt() * lrdRent!!.toDouble()) / 100).toString()
                    item.securityAmount =
                        ((item.rentPercentage!!.toDouble() * lrdSecurityDeposit!!.toDouble()) / 100).toString()
                }
            }
        } catch (e: Exception) {
        }

        obLeaseModify.tenantBaseInfoDto = tenantRdArrayList
        requestLeaseInfo.tenantBaseInfoDto = tenantRdArrayList
        submitLease()

    }

    private fun rejctDialog() {
        //Inflate the dialog with custom view
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_reject, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)

        mBuilder.setPositiveButton(getString(R.string.confirm)) { dialog, which ->

            val msgSend: String = mDialogView.edit_reject!!.text.toString().trim()
            leaseBrkrTenantAction("Rejected", msgSend)

            dialog.dismiss()
        }

        mBuilder.setNegativeButton(getString(R.string.cancel)) { dialog, which ->
            dialog.dismiss()
        }
        mBuilder.show()

    }


    private fun updateLeaseDialog() {
        val mBuilder = AlertDialog.Builder(this)
        mBuilder.setTitle(getString(R.string.confirm))
        mBuilder.setMessage(getString(R.string.tag_lease_update))

        mBuilder.setPositiveButton(getString(R.string.yes)) { dialog, which ->
            validComponent()
            dialog.dismiss()
        }

        mBuilder.setNegativeButton(getString(R.string.no)) { dialog, which ->
            dialog.dismiss()
        }

        mBuilder.show()
    }

    private fun leaseTerminateDialog() {
        //Inflate the dialog with custom view
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_terminate, null)

        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)


        var editTerminate: TextInputEditText = mDialogView.findViewById(R.id.edit_terminate)
        var editTerminatePaswrd: TextInputEditText =
            mDialogView.findViewById(R.id.edit_reject_password)
        val layoutTerminate: TextInputLayout =
            mDialogView.findViewById(R.id.layout_terminate_paswrd)
        editTerminatePaswrd!!.visibility = View.GONE
        editTerminatePaswrd!!.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
                layoutTerminate!!.isPasswordVisibilityToggleEnabled = true
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {

            }
        })
        mBuilder.setPositiveButton(getString(R.string.confirm), null)
        mBuilder.setNegativeButton(getString(R.string.cancel)) { dialog, which ->
            dialog.dismiss()
        }
        val mdialog: AlertDialog = mBuilder.create()
        mdialog.show()

        val positiveButton: Button = mdialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            val paswrd: String = editTerminatePaswrd!!.text.toString().trim()
            val msgSend: String = editTerminate!!.text.toString().trim()
            /*if (paswrd!!.isNullOrEmpty()) {
                layoutTerminate!!.isPasswordVisibilityToggleEnabled = false
                editTerminatePaswrd!!.error = getString(R.string.error_pwd)
                editTerminatePaswrd!!.requestFocus()

            } else if (!paswrd!!.equals(Kotpref.password)) {
                layoutTerminate!!.isPasswordVisibilityToggleEnabled = false
                editTerminatePaswrd!!.error = getString(R.string.error_valid_pwd)
                editTerminatePaswrd!!.requestFocus()
            } else*/
            if (msgSend!!.isNullOrEmpty()) {
                editTerminate!!.error = getString(R.string.error_note)
                editTerminate!!.requestFocus()
            } else {
                //leaseTerminationAction(paswrd, msgSend)
                if (isLeaseUndoTerminate)
                {
                    leaseTerminationUndo(paswrd,msgSend)
                }else
                    leaseTerminationAction(paswrd, msgSend)

                mdialog.dismiss()
            }
        }


    }

    private fun terminateDialog() {
        val mBuilder = AlertDialog.Builder(this)
        mBuilder.setTitle(getString(R.string.terminate_lease))

        if (!isLeaseUndoTerminate)
            mBuilder.setMessage(getString(R.string.tag_terminate))
        else{
            mBuilder.setMessage(getString(R.string.tag_terminate_undo))
        }



        mBuilder.setPositiveButton(getString(R.string.confirm)) { dialog, which ->
            leaseTerminateDialog()
            dialog.dismiss()
        }

        mBuilder.setNegativeButton(getString(R.string.cancel)) { dialog, which ->
            dialog.dismiss()
        }

        mBuilder.show()

    }

    private fun bankAddDialog() {
        val mBuilder = AlertDialog.Builder(this)
        mBuilder.setTitle("Error")
        mBuilder.setMessage("Add your bank account to get payment directly to your bank account.")
        mBuilder.setPositiveButton("Ok") { dialog, which ->
          dialog.dismiss()
            Util.navigationNext(this@LeaseRequestDetailScreen, AccountLinkDetailScreen::class.java)
        }

        mBuilder.show()
    }

    private fun securityDialog() {
        val mBuilder = AlertDialog.Builder(this)
        mBuilder.setTitle(getString(R.string.security_amt))
        mBuilder.setMessage(getString(R.string.tag_return_security))
        mBuilder.setPositiveButton(getString(R.string.yes)) { dialog, which ->
            refndDialog()
            dialog.dismiss()
        }
        mBuilder.setNegativeButton(getString(R.string.cancel)) { dialog, which ->
            dialog.dismiss()
            updateUI()
        }
        mBuilder.show()
    }

    private fun refndDialog() {

        //Inflate the dialog with custom view
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_security_return, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
        var rvRefnd: RecyclerView = mDialogView.findViewById(R.id.rv_refund)
        rvRefnd?.layoutManager = LinearLayoutManager(this)



        refundTenantList.clear()
        for (item in tenantRdArrayList) {
            var refundTenantModel = RefundTenantModel()
            refundTenantModel.tenantId = item.tenantId
            refundTenantList.add(refundTenantModel)
        }

        var refundSecurityAdapter: RefundSecurityAdapter? = RefundSecurityAdapter(
            applicationContext,
            this,
            tenantRdArrayList
        )
        rvRefnd?.adapter = refundSecurityAdapter
        mBuilder.setPositiveButton(getString(R.string.confirm)) { dialog, which ->

            refundSecurityAction()

            dialog.dismiss()
        }

        mBuilder.setNegativeButton(getString(R.string.cancel)) { dialog, which ->
            dialog.dismiss()
        }

        mBuilder.show()
    }

    private fun submitLease() {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            //Create retrofit Service
            pbLrdLease?.visibility = View.VISIBLE


            val tenantAddUpdateCall: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<TenantPOnboardAddUpdateResponse> =
                tenantAddUpdateCall.addOrUpdateLease(obLeaseModify)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    pbLrdLease!!.visibility = View.GONE
                    if ((it.responseDto?.responseCode!!.equals(200)) && it.data != null) {

                        var alertDialog=SweetAlertDialog(this,SweetAlertDialog.SUCCESS_TYPE)
                         alertDialog.setContentText(it.responseDto!!.responseDescription)
                         alertDialog.setConfirmText("ok")
                         alertDialog.setConfirmClickListener {
                             alertDialog.dismiss()
                           //  updateUI()
                              finish()
                         }
                        alertDialog.show()




                    } else {
                        Toast.makeText(
                            applicationContext,
                            it.responseDto?.exceptionDescription,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                    { e ->
                        pbLrdLease!!.visibility = View.GONE
                        Toast.makeText(applicationContext, "Fail", Toast.LENGTH_SHORT).show()
                        Log.e("error", e.message.toString())
                    })

        } else {
            Toast.makeText(
                applicationContext,
                getString(R.string.error_network),
                Toast.LENGTH_SHORT
            ).show()

        }
    }

    private fun leaseBrkrTenantAction(valueAction: String, valueMsg: String) {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            //Create retrofit Service
            pbLrdLease?.visibility = View.VISIBLE
            var obLeaseAction = ObLeaseTenantPayload()
            obLeaseAction.modifyByRole=Kotpref.userRole
            obLeaseAction.leaseId = requestLeaseInfo.leaseId
            obLeaseAction.landlordId = requestLeaseInfo.landlordId
            obLeaseAction.action = valueAction
            obLeaseAction.comment = valueMsg
            val tenantAddUpdateCall: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<BrkrTenantLeaseActionResponse> =
                tenantAddUpdateCall.brkrLeaseAction(obLeaseAction)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    pbLrdLease!!.visibility = View.GONE
                    if ((it.responseDto?.responseCode!!.equals(200)) && it.data != null) {
                        requestLeaseInfo = it.data!!
                        var alertDialog=SweetAlertDialog(this,SweetAlertDialog.SUCCESS_TYPE)
                        alertDialog.setContentText(it.responseDto!!.responseDescription)
                        alertDialog.setConfirmText("ok")
                        alertDialog.setConfirmClickListener {
                            alertDialog.dismiss()
                            //  updateUI()
                            finish()
                        }
                        alertDialog.show()

                    } else {
                        Toast.makeText(
                            applicationContext,
                            it.responseDto?.exceptionDescription,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                    { e ->
                        pbLrdLease!!.visibility = View.GONE
                        Toast.makeText(applicationContext, "Fail", Toast.LENGTH_SHORT).show()
                        Log.e("error", e.message.toString())
                    })

        } else {
            Toast.makeText(
                applicationContext,
                getString(R.string.error_network),
                Toast.LENGTH_SHORT
            ).show()

        }
    }

    private fun leaseTerminationAction(valuePaswrd: String, valueMsg: String) {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            //Create retrofit Service
            pbLrdLease?.visibility = View.VISIBLE
            var obLeaseAction = ObLeaseTenantPayload()
            obLeaseAction.leaseId = requestLeaseInfo.leaseId
            obLeaseAction.landlordId = requestLeaseInfo.landlordId
            obLeaseAction.leaseTerminated = true
            obLeaseAction.modifyByRole ="CX-Landlord" // Kotpref.userRole
            //obLeaseAction.password = valuePaswrd
            obLeaseAction.terminateReason = valueMsg
            for (item in tenantRdArrayList) {
                item.leaseTerminated = true
            }
            obLeaseAction.tenantBaseInfoDto = tenantRdArrayList
            val tenantAddUpdateCall: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<ResponseDtoResponse> =
                tenantAddUpdateCall.leaseTermination(obLeaseAction)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    pbLrdLease!!.visibility = View.GONE
                    if ((it.responseDto?.responseCode!!.equals(200))) {
                        //requestLeaseInfo.leaseStatus = "23"
                      //  securityDialog()
                        finish()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            it.responseDto?.exceptionDescription,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                    { e ->
                        pbLrdLease!!.visibility = View.GONE
                        Toast.makeText(applicationContext, "Fail", Toast.LENGTH_SHORT).show()
                        Log.e("error", e.message.toString())
                    })

        } else {
            Toast.makeText(
                applicationContext,
                getString(R.string.error_network),
                Toast.LENGTH_SHORT
            ).show()

        }
    }
    private fun leaseTerminationUndo(valuePaswrd: String, valueMsg: String) {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            //Create retrofit Service
            pbLrdLease?.visibility = View.VISIBLE
            var obLeaseAction = ObLeaseTenantPayload()
            obLeaseAction.leaseId = requestLeaseInfo.leaseId
            obLeaseAction.landlordId = requestLeaseInfo.landlordId
            obLeaseAction.leaseTerminated = true
            obLeaseAction.modifyByRole = "CX-Landlord" //Kotpref.userRole
            //obLeaseAction.password = valuePaswrd
            obLeaseAction.terminateReason = valueMsg
            for (item in tenantRdArrayList) {
                item.leaseTerminated = true
            }
            obLeaseAction.tenantBaseInfoDto = tenantRdArrayList
            val tenantAddUpdateCall: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<ResponseDtoResponse> =
                tenantAddUpdateCall.leaseTerminationUndo(obLeaseAction)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    pbLrdLease!!.visibility = View.GONE
                    if ((it.responseDto?.responseCode!!.equals(200))) {
                       finish()

                        //securityDialog()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            it.responseDto?.exceptionDescription,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                    { e ->
                        pbLrdLease!!.visibility = View.GONE
                        Toast.makeText(applicationContext, "Fail", Toast.LENGTH_SHORT).show()
                        Log.e("error", e.message.toString())
                    })

        } else {
            Toast.makeText(
                applicationContext,
                getString(R.string.error_network),
                Toast.LENGTH_SHORT
            ).show()

        }
    }


    private fun leaseTenantAction(valueAction: String) {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            //Create retrofit Service
            pbLrdLease?.visibility = View.VISIBLE
            var obLeaseAction = ObLeaseTenantPayload()
            obLeaseAction.leaseId = requestLeaseInfo.leaseId
            obLeaseAction.landlordId = requestLeaseInfo.landlordId
            obLeaseAction.action = valueAction
            //obLeaseAction.modifyByRole=Kotpref.userRole
            val tenantAddUpdateCall: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<BrkrTenantLeaseActionResponse> =
                tenantAddUpdateCall.tenantLeaseAction(obLeaseAction)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    pbLrdLease!!.visibility = View.GONE
                    if ((it.responseDto?.responseCode!!.equals(200)) && it.data != null) {
                        requestLeaseInfo = it.data!!
                        var alertDialog=SweetAlertDialog(this,SweetAlertDialog.SUCCESS_TYPE)
                        alertDialog.setContentText(it.responseDto!!.responseDescription)
                        alertDialog.setConfirmText("ok")
                        alertDialog.setConfirmClickListener {
                            alertDialog.dismiss()
                            //  updateUI()
                            finish()
                        }
                        alertDialog.show()


                    } else {
                        Toast.makeText(
                            applicationContext,
                            it.responseDto?.exceptionDescription,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                    { e ->
                        pbLrdLease!!.visibility = View.GONE
                        Toast.makeText(applicationContext, "Fail", Toast.LENGTH_SHORT).show()
                        Log.e("error", e.message.toString())
                    })

        } else {
            Toast.makeText(
                applicationContext,
                getString(R.string.error_network),
                Toast.LENGTH_SHORT
            ).show()

        }
    }

    private fun refundSecurityAction() {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            //Create retrofit Service
            pbLrdLease?.visibility = View.VISIBLE
            var refundSecurityAmountModel = RefundSecurityAmountModel()
            refundSecurityAmountModel.leaseId = requestLeaseInfo.leaseId
            refundSecurityAmountModel.modifyByRole = Kotpref.userRole
            refundSecurityAmountModel.tenantRefundDto = refundTenantList

            val securityRefundCall: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<ResponseDtoResponse> =
                securityRefundCall.securityRefund(refundSecurityAmountModel)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    pbLrdLease!!.visibility = View.GONE
                    //  if ((it.responseDto?.responseCode!!.equals(200)) ) {
                    requestLeaseInfo.leaseStatus = "24"
                    fetchSecurityList(requestLeaseInfo.leaseId)

                    /* }
                    else{
                        Toast.makeText(applicationContext, it.responseDto?.exceptionDescription, Toast.LENGTH_SHORT).show()
                    }*/
                },
                    { e ->
                        pbLrdLease!!.visibility = View.GONE
                        Toast.makeText(applicationContext, "Fail", Toast.LENGTH_SHORT).show()
                        Log.e("error", e.message.toString())
                    })

        } else {
            Toast.makeText(
                applicationContext,
                getString(R.string.error_network),
                Toast.LENGTH_SHORT
            ).show()

        }
    }

    private fun fetchSecurityList(idLease: String) {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            //Create retrofit Service
            pbLrdLease!!.visibility = View.VISIBLE
            val credential = SignatureLeaseCredentials()

            credential.leaseId = idLease
            val signatureApi: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<SecurityFetchResponse> =
                signatureApi.securityFetchInfo(credential)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", it.responseDto?.responseDescription.toString())

                    pbLrdLease!!.visibility = View.GONE

                    if (it.data != null) {
                        listSecurity = it.data!!

                    }
                    updateUI()

                },
                    { e ->
                        Log.e("onFailure", e.toString())
                        pbLrdLease!!.visibility = View.GONE
                        e.message?.let {
                            Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                        }
                    })
        } else {
            Toast.makeText(
                applicationContext,
                getString(R.string.error_network),
                Toast.LENGTH_SHORT
            ).show()

        }
    }

    companion object {
        var refundTenantList = ArrayList<RefundTenantModel>()
         var requestLeaseInfo = LeaseRequestInfo()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Util.navigationBack(this@LeaseRequestDetailScreen)
    }



}