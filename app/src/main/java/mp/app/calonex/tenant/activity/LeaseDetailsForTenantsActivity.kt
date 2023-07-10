package mp.app.calonex.tenant.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_lease_details_for_tenant.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.FetchDocumentCredential
import mp.app.calonex.common.apiCredentials.SignatureLeaseCredentials
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.tenant.TenantLeaseCredentials
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.CxBaseActivity2
import mp.app.calonex.landlord.adapter.ImagePagerAdapter
import mp.app.calonex.landlord.model.*
import mp.app.calonex.landlord.response.FetchDocumentResponse
import mp.app.calonex.landlord.response.SignatureLeaseResponse
import mp.app.calonex.tenant.adapter.TenantListAdapter
import mp.app.calonex.tenant.model.LeaseTenantInfo
import mp.app.calonex.tenant.response.FindApiResponse
import mp.app.calonex.tenant.response.ResponseDtoResponse

class LeaseDetailsForTenantsActivity : CxBaseActivity2() {
    private var isLeaseUndoTerminate=false
    private var isLeaseTerminate=false
    private var txtLrdLeaseTerminate: TextView? = null
    private var txtLrdLeaseTerminateUndo: TextView? = null
    private var listSignature = java.util.ArrayList<LeaseSignature>()
    private var listSecurity = java.util.ArrayList<FetchSecurityInfo>()
    private var tenantRdArrayList = java.util.ArrayList<TenantInfoPayload>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lease_details_for_tenant)
        txtLrdLeaseTerminate=findViewById(R.id.txt_lrd_lease_terminate)
        txtLrdLeaseTerminate!!.setOnClickListener {
            terminateDialog()
        }

        findLease()
        fetchImages()
        getSignatureList()
        iv_back.setOnClickListener {
            onBackPressed()
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
    private fun leaseTerminateDialog() {
        //Inflate the dialog with custom view
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_terminate, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this).setView(mDialogView)


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

                if (isLeaseUndoTerminate)
                {
                    leaseTerminationUndo(paswrd,msgSend)
                }else
                leaseTerminationAction(paswrd, msgSend)

                mdialog.dismiss()
            }
        }


    }
    private fun leaseTerminationAction(valuePaswrd: String, valueMsg: String) {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            //Create retrofit Service
            pb_detail?.visibility = View.VISIBLE
            var obLeaseAction = ObLeaseTenantPayload()
            obLeaseAction.leaseId = findApiResponse.leaseId
            obLeaseAction.landlordId = findApiResponse.landlordId
            obLeaseAction.leaseTerminated = true
            obLeaseAction.modifyByRole = "CX-Landlord"  //Kotpref.userRole
            //obLeaseAction.password = valuePaswrd
            obLeaseAction.terminateReason = valueMsg
            for (item in tenantRdArrayList) {
                item.leaseTerminated = true
            }


            obLeaseAction.tenantBaseInfoDto = mapper(tenantRdArrayList)
            val tenantAddUpdateCall: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<ResponseDtoResponse> =
                tenantAddUpdateCall.leaseTermination(obLeaseAction)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    pb_detail!!.visibility = View.GONE
                    if ((it.responseDto?.responseCode!!.equals(200))) {
                        //LeaseRequestDetailScreen.requestLeaseInfo.leaseStatus = "23"
                        findLease()
                      //  securityDialog()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            it.responseDto?.exceptionDescription,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                    { e ->
                        pb_detail!!.visibility = View.GONE
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
            pb_detail?.visibility = View.VISIBLE
            var obLeaseAction = ObLeaseTenantPayload()
            obLeaseAction.leaseId = findApiResponse.leaseId
            obLeaseAction.landlordId = findApiResponse.landlordId
            obLeaseAction.leaseTerminated = true
            obLeaseAction.modifyByRole = "CX-Landlord"  //Kotpref.userRole
            //obLeaseAction.password = valuePaswrd
            obLeaseAction.terminateReason = valueMsg
            /*for (item in tenantRdArrayList) {
                item.leaseTerminated = true
            }*/


            obLeaseAction.tenantBaseInfoDto = mapper(tenantRdArrayList)
            val tenantAddUpdateCall: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<ResponseDtoResponse> =
                tenantAddUpdateCall.leaseTerminationUndo(obLeaseAction)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    pb_detail!!.visibility = View.GONE
                    if ((it.responseDto?.responseCode!!.equals(200))) {
                        //LeaseRequestDetailScreen.requestLeaseInfo.leaseStatus = "23"
                        findLease()
                      //  securityDialog()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            it.responseDto?.exceptionDescription,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                    { e ->
                        pb_detail!!.visibility = View.GONE
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
    private fun getSignatureList() {
        if (NetworkConnection.isNetworkConnected(this@LeaseDetailsForTenantsActivity)) {
            //Create retrofit Service
            pb_detail!!.visibility = View.VISIBLE
            val credential = SignatureLeaseCredentials()

            credential.leaseId = Kotpref.leaseId
            val signatureApi: ApiInterface =
                ApiClient(this@LeaseDetailsForTenantsActivity!!).provideService(ApiInterface::class.java)
            val apiCall: Observable<SignatureLeaseResponse> =
                signatureApi.getLeaseSignature(credential)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", it.responseDto?.responseDescription.toString())

                    pb_detail!!.visibility = View.GONE

                    if (it.data != null) {
                        signList = it.data!!

                        setSignatureData()
                    }

                },
                    { e ->
                        Log.e("onFailure", e.toString())
                        pb_detail!!.visibility = View.GONE
                        e.message?.let {
                            Toast.makeText(
                                this@LeaseDetailsForTenantsActivity,
                                it,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
        } else {
            Toast.makeText(
                this@LeaseDetailsForTenantsActivity,
                getString(R.string.error_network),
                Toast.LENGTH_SHORT
            )
                .show()

        }
    }

    private fun setSignatureData() {

        try {
            if (signList.size > 0) {
                for (i in 0 until signList.size) {
                    when (i) {
                        0 -> {
                            val decodedString: ByteArray =
                                Base64.decode(signList[i].signatureData, Base64.NO_WRAP)
                            val decodedByte =
                                BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

                            iv_tenant_signature.setImageBitmap(decodedByte)
                        }
                        1 -> {
                            val decodedString: ByteArray =
                                Base64.decode(signList[i].signatureData, Base64.NO_WRAP)
                            val decodedByte =
                                BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

                            iv_landlord_signature.setImageBitmap(decodedByte)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    private fun fetchImages() {
        if (NetworkConnection.isNetworkConnected(this)) {
            pb_detail.visibility = View.VISIBLE
            val credentials = FetchDocumentCredential()
            credentials.propertyId = Kotpref.propertyId

            val signatureApi: ApiInterface =
                ApiClient(this@LeaseDetailsForTenantsActivity).provideService(ApiInterface::class.java)
            val apiCall: Observable<FetchDocumentResponse> =
                signatureApi.fetchDocument(credentials)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", "All File List ====>> " + Gson().toJson(it.data!!))

                    if (it.responseDto!!.responseCode.equals(200)) {
                        //getLeaseList(propertyId)
                        listPropertyImages = it.data!!
                        pb_detail.visibility = View.GONE

                        setPropertyImageSlider()

                    } else {
                        pb_detail.visibility = View.GONE
                        Toast.makeText(
                            this@LeaseDetailsForTenantsActivity,
                            it.responseDto!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                    { e ->
                        Log.e("onFailure", e.toString())
                        pb_detail.visibility = View.GONE
                        e.message?.let {
                            Toast.makeText(
                                this@LeaseDetailsForTenantsActivity,
                                it,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
        } else {
            Toast.makeText(
                this@LeaseDetailsForTenantsActivity,
                getString(R.string.error_network),
                Toast.LENGTH_SHORT
            )
                .show()
        }
    }

    private fun setPropertyImageSlider() {
        val imgList = ArrayList<String>()
        for (item in listPropertyImages) {
            if (item.uploadFileType.equals(getString(R.string.key_img_property))) {
                imgList.add(item.fileName)
            }
        }

        Kotpref.propertyIdNew =
            TenantPropertyDetailViewActivity.propertyDetailResponseLocal.propertyId

        vp_image!!.adapter = ImagePagerAdapter(this, this@LeaseDetailsForTenantsActivity, imgList)

        tl_indicator!!.setupWithViewPager(vp_image)

    }


    private fun findLease() {
        Log.e("TenantListActivity:::::", "findLease Called")
        pb_detail.visibility = View.VISIBLE
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



                    tenantRdArrayList= findApiResponse.tenantBaseInfoDto
                    Kotpref.propertyId = findApiResponse.propertyId
                    Kotpref.leaseId = findApiResponse.leaseId
                    Kotpref.unitNumber = findApiResponse.unitNumber

                    for (item in findApiResponse.tenantBaseInfoDto) {
                        if (item.userId == Kotpref.userId) {
                            Kotpref.exactRole = item.role.toString()
                        }
                    }

                    staticTenantList = findApiResponse.tenantBaseInfoDto
                    tenantListPermanant = findApiResponse.tenantBaseInfoDto
                    tenantListTemporary = findApiResponse.tenantBaseInfoDto
                    updateUi()
                    pb_detail.visibility = View.GONE
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        },
            { e ->
                pb_detail.visibility = View.GONE
                Log.e("onFailure", e.toString())
                e.message?.let {
                    Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun updateUi() {
        txt_unit_id.text = "" + findApiResponse.unitId

        tv_lease_status.text = "" + Util.getLeaseStatus(findApiResponse.leaseStatus)

       if (findApiResponse.leaseStatus.equals("19")) {
           txtLrdLeaseTerminate!!.text= "Termination"
           txtLrdLeaseTerminate!!.visibility=View.VISIBLE
           isLeaseUndoTerminate=false
       }else{
           txtLrdLeaseTerminate!!.visibility=View.GONE
       }

       if (findApiResponse.leaseStatus.equals("33")){
           txtLrdLeaseTerminate!!.text= "Undo Termination"
           isLeaseUndoTerminate=true
           txtLrdLeaseTerminate!!.visibility=View.VISIBLE
       }

        txt_pd_add.text = "" + findApiResponse.propertyAddress + ", " + findApiResponse.propertyCity + ", " + findApiResponse.propertyState + ", " + findApiResponse.propertyZipCode
        txt_rent_amount.text = "" + findApiResponse.rentAmount
        txt_late_fees.text = "" + findApiResponse.lateFee
        txt_months_free.text = "" + findApiResponse.monthsFree
        txt_rent_if_paid_before_due.text = "" + findApiResponse.rentBeforeDueDate
        txt_security_deposit.text = "" + findApiResponse.securityAmount
        txt_lease_start_date.text = "" + Util.convertLongToTime(
            findApiResponse.leaseStartDate!!.toLong(),
            "MMM dd, yyyy"
        )
        txt_lease_creation_date.text = "" + Util.convertLongToTime(
            findApiResponse.createdOn!!.toLong(),
            "MMM dd, yyyy"
        )

        tv_tenant_name.text = "" + findApiResponse.tenantName
        tv_landlord_name.text = "" + findApiResponse.landlordName

        tenantList()
    }

    private fun tenantList() {
        tenant_list_rv!!.layoutManager = LinearLayoutManager(applicationContext)
        var tenantListAdapter = TenantListAdapter(applicationContext, tenantListPermanant, false)
        tenant_list_rv!!.adapter = tenantListAdapter

    }


    companion object {
        var staticTenantList = ArrayList<TenantInfoPayload>()
        var findApiResponse = LeaseTenantInfo()
        var tenantListPermanant = ArrayList<TenantInfoPayload>()
        var tenantListTemporary = ArrayList<TenantInfoPayload>()
        var listPropertyImages = ArrayList<FetchDocumentModel>()
        var signList = ArrayList<LeaseSignature>()

    }

    private fun securityDialog() {
        val mBuilder = AlertDialog.Builder(this)
        mBuilder.setTitle(getString(R.string.security_amt))
        mBuilder.setMessage(getString(R.string.tag_return_security))
        mBuilder.setPositiveButton(getString(R.string.yes)) { dialog, which ->
            //refndDialog()
            dialog.dismiss()
        }
        mBuilder.setNegativeButton(getString(R.string.cancel)) { dialog, which ->
            dialog.dismiss()
            //updateUI()
        }
        mBuilder.show()
    }

  /*  private fun refndDialog() {

        //Inflate the dialog with custom view
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_security_return, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
        var rvRefnd: RecyclerView = mDialogView.findViewById(R.id.rv_refund)
        rvRefnd?.layoutManager = LinearLayoutManager(this)

        LeaseRequestDetailScreen.refundTenantList.clear()
        for (item in tenantRdArrayList) {
            var refundTenantModel = RefundTenantModel()
            refundTenantModel.tenantId = item.tenantId
            LeaseRequestDetailScreen.refundTenantList.add(refundTenantModel)
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
    private fun refundSecurityAction() {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            //Create retrofit Service
            pb_detail?.visibility = View.VISIBLE
            var refundSecurityAmountModel = RefundSecurityAmountModel()
            refundSecurityAmountModel.leaseId = LeaseRequestDetailScreen.requestLeaseInfo.leaseId
            refundSecurityAmountModel.modifyByRole = Kotpref.userRole
            refundSecurityAmountModel.tenantRefundDto = LeaseRequestDetailScreen.refundTenantList

            val securityRefundCall: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<ResponseDtoResponse> =
                securityRefundCall.securityRefund(refundSecurityAmountModel)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    pb_detail!!.visibility = View.GONE
                    //  if ((it.responseDto?.responseCode!!.equals(200)) ) {
                    LeaseRequestDetailScreen.requestLeaseInfo.leaseStatus = "24"
                    fetchSecurityList(LeaseRequestDetailScreen.requestLeaseInfo.leaseId)

                    *//* }
                    else{
                        Toast.makeText(applicationContext, it.responseDto?.exceptionDescription, Toast.LENGTH_SHORT).show()
                    }*//*
                },
                    { e ->
                        pb_detail!!.visibility = View.GONE
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
            pb_detail!!.visibility = View.VISIBLE
            val credential = SignatureLeaseCredentials()

            credential.leaseId = idLease
            val signatureApi: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<SecurityFetchResponse> =
                signatureApi.securityFetchInfo(credential)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", it.responseDto?.responseDescription.toString())

                    pb_detail!!.visibility = View.GONE

                    if (it.data != null) {
                        listSecurity = it.data!!

                    }
                   // updateUI()

                },
                    { e ->
                        Log.e("onFailure", e.toString())
                        pb_detail!!.visibility = View.GONE
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
    }*/


    private fun mapper(from:ArrayList<TenantInfoPayload>):ArrayList<ObTenantPayload>{
       var res= ArrayList<ObTenantPayload> ()

        for(get in from){
           var data=  ObTenantPayload()
            data.leaseId=get.leaseId
            data.accountId= get.accountId
            data. address =get.address
            data. amenities =get.amenities
            data.city= get.city
            data. clientId =get.clientId
            data. coSignerFlag = get.coSignerFlag
            data. coTenantFlag   =get.coTenantFlag
            data. cxId =get.cxId
            data. deleted =get.deleted
            data. emailId =get.emailId
            data. existingTenant =get.existingTenant
            data. landlordApprovalStatus =get.landlordApprovalStatus
            data. leaseAgreementAcceptance= get.leaseAgreementAcceptance
            data. leaseSigningStatus=get.leaseSigningStatus
            data. leaseTerminated =  get.leaseTerminated
            data. licenseNo =get.licenseNo
            data. oldLeaseId =get.oldLeaseId
            data. payDay =get.payDay
            data. phone =get.phone
            data. propertyId =get.propertyId
            data. renewLease = get.renewLease
            data. rentAmount =get.rentAmount
            data. rentAmountBeforeDueDate =get.rentAmountBeforeDueDate
            data. rentPercentage =get.rentPercentage
            data. role =get.role
            data. securityAmount =get.securityAmount
            data. ssn =get.ssn
            data. state =get.state
            data. tenantDataModifed = get.tenantDataModifed
            data. tenantFirstName =get.tenantFirstName
            data. tenantId =get.tenantId
            data. tenantLastName =get.tenantLastName
            data. tenantMiddleName =get.tenantMiddleName
            data. tenantSignature =get.tenantSignature
            data. unitId =get.unitId
            data. unitNumber =get.unitNumber
            data. userId =get.userId
            data. zipcode =get.zipcode
            data. bitmapListPaystub=ArrayList<Bitmap>()
            data. bitmapListW2s=ArrayList<Bitmap>()
            data. bitmapListLicence=ArrayList<Bitmap>()

            res.add(data)


        }

return res


    }
}
