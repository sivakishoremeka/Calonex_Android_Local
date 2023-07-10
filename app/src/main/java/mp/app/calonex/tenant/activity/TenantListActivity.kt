package mp.app.calonex.tenant.activity

import android.content.Intent
import android.os.Bundle
import android.text.Html
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
import kotlinx.android.synthetic.main.activity_tenant_list.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.TenantFindApiCredentials
import mp.app.calonex.common.constant.StatusConstant
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
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

class TenantListActivity : CxBaseActivity2() {

    var lease: Lease? = null
    var leaseTenant: LeaseTenantInfo? = null
    var tenant_list_rv: RecyclerView? = null
    var isTenantReviewed: Boolean = false
    var isLandlordReviewed: Boolean = false
    var isPrimaryTenant: Boolean = false
    var isCoTenant: Boolean = false
    var isCoSigner: Boolean = false

    var leaseStatus: String = ""
    var leaseSigningStatus: String = ""

    var showConfirm: Boolean = false
    var showEdit: Boolean = false
    var otherTenantsSigningStatus: Boolean = true
    var primaryTenantSigningStatus: Boolean = true
    var isLandlordApproved: Boolean = true

    var leasePosition : Int = 0

    private val fragmentProfile: Fragment = TenantListProfileFragment()
    private val fm: FragmentManager = supportFragmentManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tenant_list)
        Log.e("TenantListActivity:::::", "onCreate Called")
        tenant_list_rv = findViewById(R.id.tenant_list_rv)

        leasePosition = intent.getIntExtra("leasePosition",0)

        tenantListPermanant.clear()
        tenantListTemporary.clear()
        findApiResponse = LeaseTenantInfo()

        findLease()

        actionComponent()

//        leaseTenant = findApiResponse
//        staticTenantList = findApiResponse.tenantBaseInfoDto
//        tenantListPermanant = findApiResponse.tenantBaseInfoDto
//        tenantListTemporary = findApiResponse.tenantBaseInfoDto

    }

    private fun actionComponent() {
        Log.e("TenantListActivity:::::", "actionComponent Called")

        tenant_list_action_edit.setOnClickListener {
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
        }

        tenant_list_user_profile.setOnClickListener {
            fm.beginTransaction().add(R.id.container, fragmentProfile).addToBackStack(null).commit()
        }

        btn_confirm.setOnClickListener {

//            if (!leaseStatus.contains("Approved")) {
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
        }
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
            val intent =
                Intent(this, HomeActivityTenant::class.java)
            startActivity(intent)
            this.finish()
        }

    }

    private fun tenantList() {
        Log.e("tenantList", "Called")

        tenant_list_rv!!.layoutManager = LinearLayoutManager(applicationContext)
        var tenantListAdapter = TenantListAdapter(applicationContext, tenantListPermanant, false)
        tenant_list_rv!!.adapter = tenantListAdapter

    }

    private fun showDialog() {
        leaseTenant?.let { ViewLeaseDialogFragment(this).customDialog(it) }
    }

    private fun updateUi() {
        Log.e("TenantListActivity:::::", "updateUi Called")


        /*if (!findApiResponse.deadLine.isNullOrEmpty()) {
            tenant_list_lease_deadline.setText("Deadline: " + Util.getDateTimeStamp(findApiResponse.deadLine))
        }*/


        if (Kotpref.exactRole.contains("Primary")) {
            isPrimaryTenant = true
        } else if (Kotpref.exactRole.contains("CoTenant")) {
            isCoTenant = true
        } else if (Kotpref.exactRole.contains("CoSigner")) {
            isCoSigner = true
        }

        leaseSigningStatus = StatusConstant.globalStatus(findApiResponse.leaseSigningStatus)

        isLandlordReviewed = findApiResponse.reviewByLandlord

        leaseStatus = findApiResponse.leaseStatus
//        leaseSigningStatus = findApiResponse.leaseSigningStatus

        tenant_list_note.setText(Html.fromHtml("<b>Note:</b> <i>All updates made here will be sent to the landlord for approval</i>"))
        if (!isPrimaryTenant) {
            tenant_list_note.visibility = View.GONE
            tenant_list_action_edit.visibility = View.GONE
            tenant_list_action_add_tenant.visibility = View.GONE
        }

        tenant_list_lease_landlord_approval_status.setText(
            "Lease Status : " + StatusConstant.leaseStatus(
                leaseStatus
            )
        )

        if (leaseStatus == StatusConstant.Rejected_By_Landlord) {
            tenant_list_bottom_View.visibility = View.GONE
        } else {
            tenant_list_bottom_View.visibility = View.VISIBLE
        }


        if (leaseStatus == StatusConstant.Refund_In_progress) {
            tenant_list_bottom_View.visibility = View.GONE
        } else {
            tenant_list_bottom_View.visibility = View.VISIBLE
        }





        if (!isPrimaryTenant) {
            for (item in findApiResponse.tenantBaseInfoDto) {
                if (Kotpref.userId == item.userId) {
//                    if (!item.deleted!! && item.leaseStatus?.contains("Approve")!!) {
                    if (!item.deleted!! && item.landlordApprovalStatus == StatusConstant.APPROVED) {
                        otherTenantsSigningStatus = item.leaseSigningStatus == StatusConstant.SIGNED
                    } else if (!item.deleted!! && item.landlordApprovalStatus != StatusConstant.APPROVED) {
                        isLandlordApproved = false
                    }
                } else {
                    if (item.role?.contains("Primary")!!) {
//                        if (item.leaseSigningStatus?.contains("Signed")!!) {
                        if (item.leaseSigningStatus!! == StatusConstant.SIGNED) {
                            primaryTenantSigningStatus = true
                            otherTenantsSigningStatus = true
//                            tenant_list_lease_landlord_approval_status.visibility = View.VISIBLE
//                            tenant_list_lease_landlord_approval_status.setText(
//                                "Lease Status : " + StatusConstant.leaseStatus(
//                                    leaseStatus
//                                )
//                            )
                        }

                    }
                }
            }
        } else {
            for (item in findApiResponse.tenantBaseInfoDto) {

                if (Kotpref.userId != item.userId) {
                    // other than primary tenant item
//                    if (!item.deleted!! && item.landlordApprovalStatus?.contains("Approve")!!) {
                    if (!item.deleted!! && item.landlordApprovalStatus == StatusConstant.APPROVED) {
//                        if (!item.leaseSigningStatus?.contains("Signed")!!) {
                        if (item.leaseSigningStatus != StatusConstant.SIGNED) {
                            otherTenantsSigningStatus = false
                            break
                        }
//                    } else if (!item.deleted!! && !item.landlordApprovalStatus?.contains("Approve")!!) {
                    } else if (!item.deleted!! && item.landlordApprovalStatus != StatusConstant.APPROVED) {
                        otherTenantsSigningStatus = false
                        break
                    }
                } else {
                    // primary tenant item
                    if (item.payDay != null) {
                        primaryTpayDay = item.payDay.toString()
                    }
//                    if (!item.leaseSigningStatus?.contains("Signed")!!) {
                    if (item.leaseSigningStatus != StatusConstant.SIGNED) {
                        primaryTenantSigningStatus = false
//                        tenant_list_lease_landlord_approval_status.setText(
//                            "Lease Status : " + StatusConstant.leaseStatus(
//                                leaseStatus
//                            )
//                        )
                    }
//                    if (item.leaseSigningStatus?.contains("Signed")!!) {
                    if (item.leaseSigningStatus == StatusConstant.SIGNED) {
//                        tenant_list_lease_landlord_approval_status.setText(
//                            "Lease Status : " + StatusConstant.leaseStatus(
//                                leaseStatus
//                            )
//                        )
                    }
                }
            }
        }

        if (isPrimaryTenant) {
            if (primaryTenantSigningStatus) {
//                tenant_list_lease_landlord_approval_status.visibility = View.VISIBLE
                showConfirm = false
                showEdit = false
                tenant_list_note.visibility = View.GONE
            } else if (!primaryTenantSigningStatus && otherTenantsSigningStatus) {
                showConfirm = true
                showEdit = true
//                tenant_list_lease_landlord_approval_status.visibility = View.GONE
                tenant_list_note.visibility = View.VISIBLE
                tenant_list_note.setText(Html.fromHtml("<b>Note:</b> <i>All updates made here will be sent to the landlord for approval.</i>"))

            } else if (primaryTenantSigningStatus && !otherTenantsSigningStatus) {
                showConfirm = false
                showEdit = false
//                tenant_list_lease_landlord_approval_status.visibility = View.GONE
            } else if (!primaryTenantSigningStatus && !otherTenantsSigningStatus) {
                showConfirm = false
                showEdit = true
//                tenant_list_lease_landlord_approval_status.visibility = View.GONE
                tenant_list_note.visibility = View.VISIBLE
                tenant_list_note.setText(
                    Html.fromHtml(
                        "<b>Note:</b> <i>All updates made here will be sent to the landlord for approval.</i>  </n> " +
                                "You will be able to sign the lease when all the rest of the tenants have signed the lease."
                    )
                )

            }
        } else {
            if (isLandlordApproved && !otherTenantsSigningStatus) {
                showConfirm = true
            } else if (!isLandlordApproved) {
                showConfirm = false
            }

        }



        if (showConfirm) {
            btn_confirm.visibility = View.VISIBLE
        } else {
            btn_confirm.visibility = View.GONE
        }

        if (showEdit) {
            tenant_list_note.visibility = View.VISIBLE
            if (leaseStatus == StatusConstant.Tenant_Signature_Pending) {
                tenant_list_action_edit.visibility = View.GONE
                tenant_list_action_add_tenant.visibility = View.GONE
            } else {
                tenant_list_action_edit.visibility = View.VISIBLE
                tenant_list_action_add_tenant.visibility = View.VISIBLE
            }
        } else {
            tenant_list_action_edit.visibility = View.GONE
            tenant_list_action_add_tenant.visibility = View.GONE
            tenant_list_note.visibility = View.GONE
        }

        tenantList()
    }

    private fun findLease() {
        Log.e("TenantListActivity:::::", "findLease Called")

        tenant_list_progress.visibility = View.VISIBLE
        val credential = TenantFindApiCredentials()
        credential.userId = Kotpref.userId
        credential.userRole = "CX-Tenant"

        val findApi: ApiInterface = ApiClient(this).provideService(ApiInterface::class.java)
        val apiCall: Observable<FindApiResponse> =
            findApi.find(credential)

        apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
            Log.e("onSuccess", it.responseDto?.responseDescription.toString())


            if (it.data != null) {
                findApiResponse = it.data!![leasePosition]
//                txt_tenant_address.text = it.data!![0].propertyAddress
//                txt_tenant_unit_no.text = it.data!![0].unitNumber
//                txt_landlord_name.text = it.data!![0].landlordName

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
