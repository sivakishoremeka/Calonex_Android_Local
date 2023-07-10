package mp.app.calonex.landlord.activity

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_new_property_first_screen.*
import kotlinx.android.synthetic.main.activity_ob_lease_confirm_screen.*
import kotlinx.android.synthetic.main.activity_tenant_list_edit.*
import kotlinx.android.synthetic.main.activity_user_confirm_detail_screen.*
import kotlinx.android.synthetic.main.fragment_ld_property.*
import mp.app.calonex.R
import mp.app.calonex.agent.activity.PropertyDetailScreenAgent
import mp.app.calonex.common.apiCredentials.BrokerAgentCredentials
import mp.app.calonex.common.network.*
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.ObLeaseSpecificationScreen.Companion.obLeaseTenantPayload
import mp.app.calonex.landlord.adapter.ObItemLeaseConfirmAdapter
import mp.app.calonex.landlord.adapter.PropertyListAdapter
//import mp.app.calonex.landlord.adapter.PropertyListAdapter.Companion.propertyDetailResponse
import mp.app.calonex.landlord.model.BrokerAgentDetails
import mp.app.calonex.landlord.model.ObTenantPayload
import mp.app.calonex.landlord.response.BrokerAgentDetailResponse
import mp.app.calonex.landlord.response.RenewLeaseRequestResponse
import mp.app.calonex.tenant.response.ResponseDtoResponse
import mp.app.calonex.tenant.response.TenantPOnboardAddUpdateResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ObLeaseConfirmScreen : CxBaseActivity2() {
    private var txtObProperty: TextView? = null
    private var txtObUnit: TextView? = null
    private var rvObLcTenant: RecyclerView? = null
    private var btnLcSubmit: Button? = null
    private var txtLcRentMnth: TextView? = null
    private var txtLcSecurityAmt: TextView? = null
    private var txtLcLateFee: TextView? = null
    private var txtLcLeaseStartDate: TextView? = null
    private var txtLcLeaseDuration: TextView? = null
    private var txtLcMonthFree: TextView? = null
    private var txtLcRentBeforDueDate: TextView? = null
    private var pbObLeaseConfirm: ProgressBar? = null
    private var tenantRdArrayList = ArrayList<ObTenantPayload>()
    private var obItemLeaseConfirmAdapter: ObItemLeaseConfirmAdapter? = null
    private var count = 0
    private var size = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ob_lease_confirm_screen)
        brokerAgentInfo()
        initComponent()
        actionComponent()
        startHandler()
    }

    private fun initComponent() {
        txtObProperty = findViewById(R.id.txt_ob_property_add)
        txtObUnit = findViewById(R.id.txt_ob_unit)
        rvObLcTenant = findViewById(R.id.rv_ob_lc_tenant)
        txtLcRentMnth = findViewById(R.id.txt_lc_rent_mnth)
        txtLcSecurityAmt = findViewById(R.id.txt_lc_security_amt)
        txtLcLateFee = findViewById(R.id.txt_lc_late_fee)
        txtLcLeaseStartDate = findViewById(R.id.txt_lc_lease_start_date)
        txtLcLeaseDuration = findViewById(R.id.txt_lc_lease_duration)
        txtLcMonthFree = findViewById(R.id.txt_lc_lease_free_mnth)
        txtLcRentBeforDueDate = findViewById(R.id.txt_lc_rent_bfor_due_date)
        pbObLeaseConfirm = findViewById(R.id.pb_ob_lease_confirm)
        txtLcRentMnth!!.text = obLeaseTenantPayload.rentAmount
        txtLcSecurityAmt!!.text = obLeaseTenantPayload.securityAmount
        txtLcLateFee!!.text = obLeaseTenantPayload.lateFee

        try {
            txtLcLeaseStartDate!!.text = Util.convertLongToTime(
                obLeaseTenantPayload.leaseStartDate!!.toLong(),
                "MMM dd, yyyy"
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

        txtLcLeaseDuration!!.text = obLeaseTenantPayload.leaseDuration
        txtLcMonthFree!!.text = obLeaseTenantPayload.monthsFree
        if (obLeaseTenantPayload.rentBeforeDueDate != null)
            txtLcRentBeforDueDate!!.text = obLeaseTenantPayload.rentBeforeDueDate
        txtObProperty!!.text =
            PropertyListAdapter.propertyDetailResponse.address + ", " + PropertyListAdapter.propertyDetailResponse.city + ", " + PropertyListAdapter.propertyDetailResponse.state +
                    ", " + PropertyListAdapter.propertyDetailResponse.zipCode
        txtObUnit!!.text = "Unit : " + obLeaseTenantPayload.unitNumber
        btnLcSubmit = findViewById(R.id.btn_lc_submit)
        rvObLcTenant?.layoutManager = LinearLayoutManager(applicationContext)

        tenantRdArrayList = obLeaseTenantPayload.tenantBaseInfoDto

        obItemLeaseConfirmAdapter = ObItemLeaseConfirmAdapter(applicationContext, tenantRdArrayList)
        rvObLcTenant?.adapter = obItemLeaseConfirmAdapter
    }

    private fun actionComponent() {

        btnLcSubmit!!.setOnClickListener {
            if (obLeaseTenantPayload.renewLease!!) {
                submitRenewLease()
            } else {
                submitLease()
            }
        }

    }

    private fun submitRenewLease() {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            //Create retrofit Service
            pbObLeaseConfirm?.visibility = View.VISIBLE
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            val AddUpdateCall: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<RenewLeaseRequestResponse> =
                AddUpdateCall.renewLeaseRequest(obLeaseTenantPayload)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    pbObLeaseConfirm!!.visibility = View.GONE
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    if ((it.responseDto?.responseCode!!.equals(200)) && it.data != null) {
                        /*Util.alertOkIntentMessage(
                            this@ObLeaseConfirmScreen,
                            "Lease Submitted Successfully!",
                            "We have sent a request to the added tenant(s) to review the lease.",
                            PropertyDetailScreen::class.java
                        )*/

                        if (Kotpref.loginRole == "agent" || Kotpref.loginRole == "broker") {
                            Util.alertOkIntentMessage(
                                this@ObLeaseConfirmScreen,
                                "Lease Submitted Successfully!",
                                "We have sent a request to the added tenant(s) to review the lease.",
                                PropertyDetailScreenAgent::class.java
                            )
                        } else {
                            Util.alertOkIntentMessage(
                                this@ObLeaseConfirmScreen,
                                "Lease Submitted Successfully!",
                                "We have sent a request to the added tenant(s) to review the lease.",
                                PropertyDetailScreen::class.java
                            )
                        }
                    } else {
                        Toast.makeText(
                            applicationContext,
                            it.responseDto?.exceptionDescription,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                    { e ->
                        pbObLeaseConfirm!!.visibility = View.GONE
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
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

    fun brokerAgentInfo() {
        var alertDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        alertDialog.setContentText("loading")
        alertDialog.show()

        val credential = BrokerAgentCredentials()
        if (Kotpref.userRole.contains("Landlord", true)) {
            credential.licenceId =
                PropertyDetailScreen.propertyDetailResponseLocal.brokerOrAgentLiscenceID
        } else if (Kotpref.userRole.contains("agent", true) || Kotpref.userRole.contains(
                "broker",
                true
            )
        ) {
            credential.licenceId =
                PropertyDetailScreenAgent.propertyDetailResponseLocal.brokerOrAgentLiscenceID
        } else {
            credential.licenceId =
                PropertyDetailScreen.propertyDetailResponseLocal.brokerOrAgentLiscenceID
        }

        if (credential.licenceId.equals("")) {
            agentBrokerData = null
            return
        }

        val brokerAgentDetails: ApiInterface =
            ApiClient(this@ObLeaseConfirmScreen).provideService(ApiInterface::class.java)
        val apiCall: Observable<BrokerAgentDetailResponse> =
            brokerAgentDetails.getBrokerAgentList(credential) //Test API Key

        apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e("onSuccess", it.statusCode.toString())
                if (it.data != null && it.responseDto!!.responseDescription.contains(
                        "success"
                    )
                ) {
                    alertDialog.dismiss()
                    agentBrokerData = it.data
                } else {
                    agentBrokerData = null
                }
            },
                { e ->
                    Log.e("onFailure", e.toString())
                    pb_edit.visibility = View.GONE
                    e.message?.let {
                        Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT)
                            .show()
                    }
                })
    }

    private fun submitLease() {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            if (Kotpref.userRole.contains("Landlord", true)) {

                /*if (agentBrokerData != null) {
                    if (agentBrokerData!!.primaryContactType == "CX-Agent") {
                        obLeaseTenantPayload.agentId = agentBrokerData!!.agentId
                    } else {
                        obLeaseTenantPayload.brokerId = agentBrokerData!!.brokerId
                    }
                }*/
            }

            if (Kotpref.userRole.contains("Broker", true)) {
                if (agentBrokerData != null) {
                    obLeaseTenantPayload.brokerId = agentBrokerData!!.brokerId

                }
            }

            if (Kotpref.userRole.contains("Agent", true)) {
                if (agentBrokerData!!.primaryContactType == "CX-Agent") {
                    if (agentBrokerData != null) {
                        obLeaseTenantPayload.agentId = agentBrokerData!!.agentId

                    }
                }

            }

            //Create retrofit Service
            pbObLeaseConfirm?.visibility = View.VISIBLE
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            val AddUpdateCall: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<TenantPOnboardAddUpdateResponse> =
                AddUpdateCall.addOrUpdateLease(obLeaseTenantPayload)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    pbObLeaseConfirm!!.visibility = View.GONE
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    if ((it.responseDto?.responseCode!!.equals(200)) && it.data != null) {
                        var tenantBaseInfoDto = it.data!!.tenantBaseInfoDto
                        var tenantBaseDto = obLeaseTenantPayload.tenantBaseInfoDto
                        var leaseeId = it.data!!.leaseId

                        for (item in tenantBaseInfoDto) {

                            for (itemValue in tenantBaseDto) {
                                if (itemValue.emailId.equals(item.emailId)) {
                                    size =
                                        size + itemValue.bitmapListPaystub.size + itemValue.bitmapListW2s.size + itemValue.bitmapListLicence.size

                                    if (itemValue.bitmapListPaystub.size > 0) {
                                        for (doc in itemValue.bitmapListPaystub) {
                                            docUploadApi(
                                                doc,
                                                getString(R.string.key_img_paystub),
                                                item.tenantId!!,
                                                leaseeId!!
                                            )
                                        }

                                    }
                                    if (itemValue.bitmapListW2s.size > 0) {
                                        for (doc in itemValue.bitmapListW2s) {
                                            docUploadApi(
                                                doc,
                                                getString(R.string.key_img_w2s),
                                                item.tenantId!!,
                                                leaseeId!!
                                            )
                                        }

                                    }
                                    if (itemValue.bitmapListLicence.size > 0) {
                                        for (doc in itemValue.bitmapListLicence) {
                                            docUploadApi(
                                                doc,
                                                getString(R.string.key_img_license),
                                                item.tenantId!!,
                                                leaseeId!!
                                            )
                                        }

                                    }
                                }
                            }
                            if (size == 0) {
                                /*Util.alertOkIntentMessage(
                                    this@ObLeaseConfirmScreen,
                                    "Lease Submitted Successfully!",
                                    "We have sent a request to the added tenant(s) to review the lease.",
                                    PropertyDetailScreen::class.java
                                )*/

                                if (Kotpref.loginRole == "agent" || Kotpref.loginRole == "broker") {
                                    Util.alertOkIntentMessage(
                                        this@ObLeaseConfirmScreen,
                                        "Lease Submitted Successfully!",
                                        "We have sent a request to the added tenant(s) to review the lease.",
                                        PropertyDetailScreenAgent::class.java
                                    )
                                } else {
                                    Util.alertOkIntentMessage(
                                        this@ObLeaseConfirmScreen,
                                        "Lease Submitted Successfully!",
                                        "We have sent a request to the added tenant(s) to review the lease.",
                                        PropertyDetailScreen::class.java
                                    )
                                }
                            }
                        }


                    } else {
                        Toast.makeText(
                            applicationContext,
                            it.responseDto?.exceptionDescription,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                    { e ->
                        pbObLeaseConfirm!!.visibility = View.GONE
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
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


    fun docUploadApi(bitmapUpload: Bitmap, fileType: String, idTenant: String, idLease: String) {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            pbObLeaseConfirm!!.visibility = View.VISIBLE
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
            builder.addFormDataPart("tenantId", idTenant)
            builder.addFormDataPart("leaseId", idLease)
            builder.addFormDataPart("uploadFileType", fileType)
            var file: File? =
                Util.bitmapToFile(this@ObLeaseConfirmScreen, bitmapUpload, fileType)
            builder.addFormDataPart(
                "file", file!!.name,
                file.asRequestBody(MultipartBody.FORM)
            )
            val requestBody: RequestBody = builder.build()
            val uploadSign: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)

            val apiCallUploadSign: Observable<ResponseDtoResponse> =
                uploadSign.uploadDocument(requestBody)

            apiCallUploadSign.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    pbObLeaseConfirm!!.visibility = View.GONE
                    count = count + 1
                    if (count == size) {
                        /*Util.alertOkIntentMessage(
                            this@ObLeaseConfirmScreen,
                            "Lease Submitted Successfully!",
                            "We have sent a request to the added tenant(s) to review the lease.",
                            PropertyDetailScreen::class.java
                        )*/

                        if (Kotpref.loginRole == "agent" || Kotpref.loginRole == "broker") {
                            Util.alertOkIntentMessage(
                                this@ObLeaseConfirmScreen,
                                "Lease Submitted Successfully!",
                                "We have sent a request to the added tenant(s) to review the lease.",
                                PropertyDetailScreenAgent::class.java
                            )
                        } else {
                            Util.alertOkIntentMessage(
                                this@ObLeaseConfirmScreen,
                                "Lease Submitted Successfully!",
                                "We have sent a request to the added tenant(s) to review the lease.",
                                PropertyDetailScreen::class.java
                            )
                        }
                    }

                },
                    { e ->

                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        pbObLeaseConfirm!!.visibility = View.GONE

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

    companion object {
        var agentBrokerData: BrokerAgentDetails? = null
    }


    override fun onBackPressed() {
        super.onBackPressed()
        Util.navigationBack(this@ObLeaseConfirmScreen)
    }
}