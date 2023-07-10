package mp.app.calonex.tenant.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_tenant_lease_details.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.TenantOnboardAddUpdateCredentials
import mp.app.calonex.common.constant.StatusConstant.Companion.Landlord_Signature_Pending
import mp.app.calonex.common.constant.StatusConstant.Companion.Modified_Landlord_Approval_Pending
import mp.app.calonex.common.constant.StatusConstant.Companion.Tenant_Signature_In_Progress
import mp.app.calonex.common.constant.StatusConstant.Companion.Tenant_Signature_Pending
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.tenant.TenantLeaseCredentials
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.CxBaseActivity2
import mp.app.calonex.landlord.model.TenantInfoPayload
import mp.app.calonex.tenant.adapter.TenantListAdapter
import mp.app.calonex.tenant.fragment.TenantListProfileFragment
import mp.app.calonex.tenant.fragment.ViewLeaseDialogFragment
import mp.app.calonex.tenant.model.Lease
import mp.app.calonex.tenant.model.LeaseTenantInfo
import mp.app.calonex.tenant.response.FindApiResponse
import mp.app.calonex.tenant.response.TenantPOnboardAddUpdateResponse
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TenantLeaseDetailsActivity : CxBaseActivity2() {
    var lease: Lease? = null
    var leaseTenant: LeaseTenantInfo? = null
    var tenant_list_rv: RecyclerView? = null
    var leaseStatus: String = ""
    var leaseSigningStatus: String = ""

    var leasePosition: Int = 0

    private var cal = Calendar.getInstance()
    private var year = cal.get(Calendar.YEAR)
    private var month = cal.get(Calendar.MONTH)
    private var day = cal.get(Calendar.DAY_OF_MONTH)
    val myFormat = "yyyy-MM-dd"
    val sdf = SimpleDateFormat(myFormat, Locale.US)
    private var lsDuration: String? = null

    private val fragmentProfile: Fragment = TenantListProfileFragment()
    private val fm: FragmentManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tenant_lease_details)
        Log.e("TenantListActivity:::::", "onCreate Called")
        tenant_list_rv = findViewById(R.id.tenant_list_rv)
        leasePosition = intent.getIntExtra("leasePosition", 0)
        if (leasePosition == -1) {
            onBackPressed()
        }
        tenantListPermanant.clear()
        tenantListTemporary.clear()
        findApiResponse = LeaseTenantInfo()
        findLease()
        actionComponent()

    }

    private fun actionComponent() {
        Log.e("TenantListActivity:::::", "actionComponent Called")
        /*tenant_list_action_edit.setOnClickListener {
            val intent =
                Intent(this, TenantListEditActivity::class.java)
            startActivity(intent)
        }

        tenant_list_action_add_tenant.setOnClickListener {
            val intent =
                Intent(this, AddTenantActivity::class.java)
            startActivity(intent)
        }

        tenant_list_view_lease.setOnClickListener {
            showDialog()
        }*/
        iv_back.setOnClickListener {
            onBackPressed()
        }

        txt_update_lease.setOnClickListener {
            if (validateAll()) {
                updateLeaseAmountRequest()
            }
        }

        tv_lease_start_date.setOnClickListener {
            val dateDialog = DatePickerDialog(
                this,
                { view, year, monthOfYear, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    tv_lease_start_date!!.setText(sdf.format(cal.getTime()))
                    Kotpref.payDay = Calendar.DAY_OF_MONTH.toString()
                    Kotpref.calMonToAdd = monthOfYear

                    lsDuration = tv_lease_duration!!.text.toString().trim()
                    if (tv_lease_duration!!.text.toString().trim().length > 0) {
                        Log.e("DATEZZ", "" + Kotpref.calMonToAdd)
                        var mon: Int = lsDuration!!.toInt() + (Kotpref.calMonToAdd)
                        cal.set(Calendar.MONTH, mon)
                        tv_lease_end_date!!.setText(sdf.format(cal.getTime()))
                    } else {
                        cal.timeInMillis =
                            Util.convertDateToLong(
                                tv_lease_start_date!!.text.toString().trim()!!,
                                myFormat
                            )
                        tv_lease_end_date!!.setText("")
                    }
                },
                year,
                month,
                day
            )
            dateDialog.show()
            dateDialog.getDatePicker().setMinDate(System.currentTimeMillis())
        }

        tv_lease_duration!!.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

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
                try {
                    lsDuration = tv_lease_duration!!.text.toString().trim()
                    if (tv_lease_duration!!.text.toString().trim().length > 0) {
                        Log.e("DATEZZ", "" + Kotpref.calMonToAdd)
                        var mon: Int = lsDuration!!.toInt() + (Kotpref.calMonToAdd)
                        cal.set(Calendar.MONTH, mon)
                        tv_lease_end_date!!.setText(sdf.format(cal.getTime()))
                    } else {
                        cal.timeInMillis =
                            Util.convertDateToLong(
                                tv_lease_start_date!!.text.toString().trim()!!,
                                myFormat
                            )
                        tv_lease_end_date!!.setText("")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })

        txt_interested_for_lease.setOnClickListener {
            if (leaseTenant?.leaseStatus == Modified_Landlord_Approval_Pending) {
                Util.alertOkMessage(
                    this,
                    getString(R.string.alert),
                    "Landlord status is not approved yet."
                )
                return@setOnClickListener
            }

            val intent =
                Intent(this, TenantLeaseAgreementDetailsActivity::class.java)
            intent.putExtra("leaseId", leaseTenant!!.leaseId)
            intent.putExtra("from","")
            startActivity(intent)
        }

        /* tenant_list_user_profile.setOnClickListener {
             fm.beginTransaction().add(R.id.container, fragmentProfile).addToBackStack(null).commit()
         }

         btn_confirm.setOnClickListener {
             if (leaseStatus == StatusConstant.Modified_Landlord_Approval_Pending) {
                 Util.alertOkMessage(
                     this,
                     getString(R.string.alert),
                     "Landlord status is not approved yet."
                 )
                 return@setOnClickListener
             }

             val intent =
                 Intent(this, TenantLeaseAgreementActivity::class.java)
             intent.putExtra("leaseId", leaseTenant!!.leaseId)
             startActivity(intent)
         }*/
    }

    private fun validateAll(): Boolean {
        if (Util.valueMandetory(
                this,
                tv_lease_duration.text.toString().toString(),
                tv_lease_duration
            )
        ) {
            return false
        }
        if (Util.valueMandetory(
                this,
                tv_rent_per_month.text.toString().toString(),
                tv_rent_per_month
            )
        ) {
            return false

        }
        return true
    }

    private fun updateLeaseAmountRequest() {


        /*================Need to check with below request=======================
   *
   *
   * {


 "uploadedBy": null,
 "securityAmount": 677,
 "freezedRecord": false,
 "dontShowToLandlord": false,
 "tenantBaseInfoDto": [

 ],
 "action": null,
 "comment": null,
 "deadLine": "1676419200000",
 "lateFee": 120,
 "rentBeforeDueDate": 96,
 "monthsFree": 2,
 "propertyAddress": "Debjit Test",
 "propertyCity": "Floral Park",
 "propertyState": "New York",
 "propertyZipCode": "11005",
 "leaseSigningStatus": "20",
 "leaseStatus": "15",
 "oldLeaseData": null,
 "terminateReason": null,
 "leaseTerminateNotice": false,
 "leaseTerminateNoticeDate": null,
 "brokerPhoneNumber": null,
 "agentPhoneNumber": null,
 "brokerLicenseNumber": null,
 "agentLicenseNumber": null,
 "amenities": 0,
 "deleted": false,
 "monthFreeStandards": true,
 "renewLease": false,
 "landlordServiceFee": 2,
 "tenantServiceFee": 2,
 "discount": 4,
 "discountedRent": 96,
 "tenantName": null,
 "applicantName": "Patricia Smith",
 "activeLink": true,
 "modifyByRole": "CX-Tenant",
 "editorId": "5564",
 "securityTransferTo": null
}
   *
   * */
        val credentials = TenantOnboardAddUpdateCredentials()
        credentials.leaseId = leaseTenant!!.leaseId
        credentials.propertyId = leaseTenant!!.propertyId
        credentials.unitId = leaseTenant!!.unitId
        credentials.unitNumber = leaseTenant!!.unitNumber
        credentials.leaseEndDate = Util.getDateTimeStamp(tv_lease_end_date.text.toString(),"yyyy-MM-dd")
        credentials.leaseStartDate = Util.getDateTimeStamp(tv_lease_start_date.text.toString(),"yyyy-MM-dd")
        credentials.landlordId = leaseTenant!!.landlordId
        credentials.rentAmount = tv_rent_per_month.text.toString()
        credentials.leaseDuration = tv_lease_duration.text.toString()
        credentials.brokerId = leaseTenant!!.brokerId
        credentials.agentId = leaseTenant!!.agentId
        credentials.securityAmount = leaseTenant!!.securityAmount
        credentials.freezedRecord = leaseTenant!!.freezedRecord
        credentials.dontShowToLandlord = false
        credentials.deadLine = leaseTenant!!.deadLine
        credentials.lateFee = leaseTenant!!.lateFee
        credentials.monthsFree = leaseTenant!!.monthsFree
        credentials.rentBeforeDueDate = leaseTenant!!.rentBeforeDueDate
        credentials.applicantName = leaseTenant!!.applicantName


        credentials.discount = leaseTenant!!.discount
        //credentials.discountedRent = leaseTenant!!.discountedRent
        credentials.applicantName = leaseTenant!!.applicantName
        credentials.activeLink = true

        credentials.editorId = Kotpref.userId
        credentials.modifyByRole = "CX-Tenant"
        //credentials.reviewByLandlord = true


        credentials.tenantBaseInfoDto.addAll(leaseTenant!!.tenantBaseInfoDto)


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

                } else {
                    Toast.makeText(
                        applicationContext,
                        it.responseDto!!.exceptionDescription,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
                { e ->
                    Toast.makeText(applicationContext, "Fail", Toast.LENGTH_SHORT).show()
                    Log.e("error", e.message.toString())
                })


    }

    override fun onResume() {
        Log.e("TenantListActivity:::::", "onResume Called")
        super.onResume()
        tenantListTemporary = findApiResponse.tenantBaseInfoDto
    }

    override fun onBackPressed() {
        if (fm.backStackEntryCount != 0)
            fm.popBackStack()
        else {
            Log.e("TenantListActivity:::::", "onBackPressed Called")
            super.onBackPressed()
            this.finish()
        }

    }

    private fun tenantList() {
        tenant_list_rv!!.layoutManager = LinearLayoutManager(applicationContext)
        var tenantListAdapter = TenantListAdapter(applicationContext, tenantListPermanant, false)
        tenant_list_rv!!.adapter = tenantListAdapter

    }

    private fun showDialog() {
        leaseTenant?.let { ViewLeaseDialogFragment(this).customDialog(it) }
    }

    private fun updateUi() {

        tv_landlord_name.text =
            getString(R.string.text_land_lord_name) + " " + leaseTenant?.landlordName

        tv_property_address.text =
            leaseTenant?.propertyAddress + ", " + leaseTenant?.propertyCity + ", " + leaseTenant?.propertyState + ", " + leaseTenant?.propertyZipCode


        when (leaseTenant?.leaseStatus) {
            Tenant_Signature_Pending -> {
                tv_lease_ld_sign_status.text = "TENANT SIGNATURE PENDING"
                tv_lease_ld_sign_status.setTextColor(Color.RED)
            }
            Tenant_Signature_In_Progress -> {
                tv_lease_ld_sign_status.text = "TENANT SIGNATURE IN PROGRESS"
                tv_lease_ld_sign_status.setTextColor(Color.YELLOW)
            }
            Modified_Landlord_Approval_Pending -> {
                tv_lease_ld_sign_status.text = "LANDLORD APPROVAL PENDING"
                tv_lease_ld_sign_status.setTextColor(Color.RED)
            }
            Landlord_Signature_Pending -> {
                tv_lease_ld_sign_status.text = "LANDLORD SIGNATURE PENDING"
                tv_lease_ld_sign_status.setTextColor(Color.RED)
            }
        }


        tv_lease_duration.setText("" + leaseTenant?.leaseDuration)
        tv_late_fee_after_due.text =
            getString(R.string.text_late_fee_after_due) + " " + leaseTenant?.lateFee
        tv_free_months.text = getString(R.string.text_free_months) + " " + leaseTenant?.monthsFree
        tv_security_deposite.text =
            getString(R.string.text_security_deposit_title) + " " + leaseTenant?.securityAmount
        tv_amenities.text = ""

        tv_lease_start_date.text = Util.convertLongToTime(
            leaseTenant?.leaseStartDate!!.toLong(),
            "yyyy-MM-dd"
        )
        tv_rent_per_month.setText("" + leaseTenant?.rentAmount)
        tv_rent_discount.text = getString(R.string.text_rent_discount) + " " + leaseTenant?.discount
        tenantList()
    }

    private fun findLease() {
        Log.e("TenantListActivity:::::", "findLease Called")
        tenant_list_progress.visibility = View.VISIBLE
        val credential = TenantLeaseCredentials()
        credential.userId = Kotpref.userId
        credential.userRole = "CX-Tenant"
        credential.leaseId = Kotpref.leaseId
        credential.unitId = ""
        credential.leaseHistory = true
        val findApi: ApiInterface = ApiClient(this).provideService(ApiInterface::class.java)
        val apiCall: Observable<FindApiResponse> =
            findApi.find(credential)
        apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
            Log.e("onSuccess", it.responseDto?.responseDescription.toString())
            try {
                if (it.data!!.size > 0) {
                    findApiResponse = it.data!![0]
                    Kotpref.propertyId = findApiResponse.propertyId
                    Kotpref.leaseId = findApiResponse.leaseId
                    Kotpref.unitNumber = findApiResponse.unitNumber

                    for (item in findApiResponse.tenantBaseInfoDto) {
                        if (item.userId == Kotpref.userId) {
                            Kotpref.exactRole = item.role.toString()
                        }
                    }
                    leaseTenant = findApiResponse
                    staticTenantList = findApiResponse.tenantBaseInfoDto
                    tenantListPermanant = findApiResponse.tenantBaseInfoDto
                    tenantListTemporary = findApiResponse.tenantBaseInfoDto
                    updateUi()
                    tenant_list_progress.visibility = View.GONE
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        },
            { e ->
                tenant_list_progress.visibility = View.GONE
                Log.e("onFailure", e.toString())
                e.message?.let {
                    Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                }
            })
    }


    companion object {
        var staticTenantList = ArrayList<TenantInfoPayload>()
        var primaryTpayDay = String()
        var findApiResponse = LeaseTenantInfo()
        var tenantListPermanant = ArrayList<TenantInfoPayload>()
        var tenantListTemporary = ArrayList<TenantInfoPayload>()
    }
}
