package mp.app.calonex.tenant.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_tenant_property_unit_detail.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.TenantFindApiCredentials
import mp.app.calonex.common.constant.StatusConstant
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.landlord.activity.CxBaseActivity
import mp.app.calonex.landlord.activity.LoginScreen
import mp.app.calonex.landlord.model.TenantInfoPayload
import mp.app.calonex.tenant.model.LeaseTenantInfo
import mp.app.calonex.tenant.response.FindApiResponse

class TenantPropertyUnitDetailActivityCx : CxBaseActivity() {

    private var isUpdated: Boolean = false
    var leasePosition : Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tenant_property_unit_detail)
        leasePosition = intent.getIntExtra("leasePosition",0)


        findLease()
        findApiResponse = LeaseTenantInfo()
        tenantListPermanant.clear()
        tenantListTemporary.clear()
        isUpdated = intent.getBooleanExtra("isUpdated", false)

        if (isUpdated) {
            Handler().postDelayed({
                goToList()
            }, 1000)
        }

        txt_next.setOnClickListener {


            if (findApiResponse.leaseSigningStatus.equals(
                    "Signed",
                    true
                ) && findApiResponse.leaseStatus == StatusConstant.FINALIZED
            ) {
                goToHome()
            } else {
                goToList()
            }

        }

        action_logout.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.logout))
            builder.setMessage(getString(R.string.tag_logout))
            builder.setPositiveButton(getString(R.string.yes)) { dialog, which ->
                Kotpref.clear()
                Kotpref.isLogin = false

                val intent = Intent(this, LoginScreen::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            builder.setNegativeButton(getString(R.string.no)) { dialog, which ->
                dialog.dismiss()
            }
            builder.show()
        }
    }

    private fun goToHome() {

        val intent = Intent(this, HomeActivityTenant::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun goToList() {
        val intent = Intent(this, TenantListActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra("leasePosition", leasePosition)
        startActivity(intent)
    }

    private fun findLease() {
        propertyDetailsProgressBar.visibility = View.VISIBLE
        val credential = TenantFindApiCredentials()
        credential.userId = Kotpref.userId
        credential.userRole = "CX-Tenant"

        val findApi: ApiInterface = ApiClient(this).provideService(ApiInterface::class.java)
        val apiCall: Observable<FindApiResponse> =
            findApi.find(credential)

        apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
            Log.e("onSuccess", it.responseDto?.responseDescription.toString())
            propertyDetailsProgressBar.visibility = View.GONE


            if (it.data != null) {
                findApiResponse = it.data!![leasePosition]
                txt_tenant_address.text = findApiResponse.propertyAddress
                txt_tenant_unit_no.text = findApiResponse.unitNumber
                txt_landlord_name.text = findApiResponse.landlordName

                Kotpref.propertyId = findApiResponse.propertyId
                Kotpref.leaseId = findApiResponse.leaseId
                Kotpref.unitNumber = findApiResponse.unitNumber

                for (item in findApiResponse.tenantBaseInfoDto) {
                    if (item.userId == Kotpref.userId) {
                        Kotpref.exactRole = item.role.toString()
                    }
                }

                if (findApiResponse.leaseStatus == StatusConstant.LEASE_TERMINATED){
                    txt_next.visibility = View.GONE
                    lease_bottom_view.visibility = View.VISIBLE
                    text_lease_status.setText("Lease Terminated")
                }

                if (findApiResponse.leaseStatus == StatusConstant.Refund_In_progress){
                    txt_next.visibility = View.GONE
                    lease_bottom_view.visibility = View.VISIBLE
                    text_lease_status.setText("Refund In Progress")
                }

            }

        },
            { e ->
                propertyDetailsProgressBar.visibility = View.GONE
                Log.e("onFailure", e.toString())
                e.message?.let {
                    Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                }
            })

    }

    companion object {
        var findApiResponse = LeaseTenantInfo()
        var tenantListPermanant = ArrayList<TenantInfoPayload>()
        var tenantListTemporary = ArrayList<TenantInfoPayload>()

    }
}
