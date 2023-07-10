package mp.app.calonex.tenant.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_tenant_list_edit.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.TenantOnboardAddUpdateCredentials
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.landlord.activity.CxBaseActivity2
import mp.app.calonex.landlord.response.UploadFileMultipartResponse
import mp.app.calonex.tenant.activity.TenantListActivity.Companion.findApiResponse
import mp.app.calonex.tenant.activity.TenantListActivity.Companion.tenantListPermanant
import mp.app.calonex.tenant.activity.TenantListActivity.Companion.tenantListTemporary
import mp.app.calonex.tenant.adapter.TenantListEditAdapter
import mp.app.calonex.tenant.model.Lease
import mp.app.calonex.tenant.model.LeaseTenantInfo
import mp.app.calonex.tenant.response.TenantPOnboardAddUpdateResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class
TenantListEditActivity : CxBaseActivity2() {

    var lease: Lease? = null
    var leaseTenant: LeaseTenantInfo? = null
    var tenant_list_rv: RecyclerView? = null
    var isUpdated: Boolean = false

    lateinit var filePart: MultipartBody.Part


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tenant_list_edit)
        tenant_list_rv = findViewById(R.id.tenant_list_edit_rv)


        actionComponent()
        tenantList()

        tenant_list_edit_add_tenant.setOnClickListener {
            val intent = Intent(this, AddTenantActivity::class.java)
            startActivity(intent)
        }

        layout_add_tenant.setOnClickListener {
            val intent = Intent(this, AddTenantActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
//        if(isEdited) {
//            btn_save.visibility = View.VISIBLE
//            isEdited = false
//        } else {
//            btn_save.visibility = View.GONE
//            isEdited = false
//        }
    }

    override fun onBackPressed() {

        if (isEdited) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Quit Editing ?")
            // Display a message on alert dialog
            builder.setMessage("Do you want to discard the changes you have made ?")

            // Set a positive button and its click listener on alert dialog
            builder.setPositiveButton("Yes") { dialog, which ->
                dialog.dismiss()
                super.onBackPressed()
                tenantListTemporary.clear()
                tenantListTemporary=tenantListPermanant
            }
            builder.setNegativeButton(getString(R.string.no)) { dialog, which ->
                dialog.dismiss()
            }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        } else {
            super.onBackPressed()
        }
    }

    private fun actionComponent() {

        btn_rent_distribute.setOnClickListener {
            val intent = Intent(this, RentDistributionTenantActivity::class.java)
            startActivity(intent)
        }


        btn_save.setOnClickListener {

            var totalRentPercent = 0
            for (item in tenantListTemporary) {
                if (!item.rentPercentage.isNullOrEmpty())
                    totalRentPercent += item.rentPercentage!!.toDouble().toInt()
            }

            if (totalRentPercent != 100) {

                val builder = AlertDialog.Builder(this)
                builder.setTitle("Distribute Rent")
                builder.setMessage("Please distribute the rent before sending to landlord.")

                // Set a positive button and its click listener on alert dialog
                builder.setPositiveButton("OK") { dialog, which ->
                    val intent =
                        Intent(this, RentDistributionTenantActivity::class.java)
                    startActivity(intent)

                }

                val dialog: AlertDialog = builder.create()
                dialog.show()
            }

//            if (isRemoved) {
//                val intent =
//                    Intent(this, RentDistributionTenantActivity::class.java)
//                startActivity(intent)
//            }
            else {
                tenant_list_progress.visibility = View.VISIBLE
                val credentials = TenantOnboardAddUpdateCredentials()
                credentials.dontShowToLandlord = false
                credentials.landlordId = findApiResponse.landlordId
                credentials.lateFee = findApiResponse.lateFee
                credentials.leaseDuration = findApiResponse.leaseDuration
                credentials.leaseEndDate = findApiResponse.leaseEndDate
                credentials.leaseId = findApiResponse.leaseId
                credentials.leaseStartDate = findApiResponse.leaseStartDate
                credentials.monthsFree = findApiResponse.monthsFree
                credentials.propertyId = findApiResponse.propertyId
                credentials.rentAmount = findApiResponse.rentAmount
                credentials.rentBeforeDueDate = findApiResponse.rentBeforeDueDate
                credentials.reviewByLandlord = findApiResponse.reviewByLandlord
                credentials.securityAmount = findApiResponse.securityAmount
                credentials.unitId = findApiResponse.unitId
                credentials.unitNumber = findApiResponse.unitNumber
                credentials.uploadedBy = findApiResponse.uploadedBy
                credentials.modifyByRole = Kotpref.userRole
                credentials.editorId = Kotpref.userId
                credentials.securityTransferTo = "CX"

                credentials.tenantBaseInfoDto.addAll(tenantListTemporary)


                val tenantAddUpdateCall: ApiInterface =
                    ApiClient(this).provideService(ApiInterface::class.java)
                val apiCall: Observable<TenantPOnboardAddUpdateResponse> =
                    tenantAddUpdateCall.tenantAddUpdate(credentials)

                apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        if (it.data != null) {

                            for (i in 0 until it.data!!.tenantBaseInfoDto.size) {
                                for (j in 0 until tenantListTemporary.size) {
                                    if (it.data!!.tenantBaseInfoDto[i].emailId ==  tenantListTemporary[j].emailId) {
                                        tenantListTemporary[j].tenantId = it.data!!.tenantBaseInfoDto[i].tenantId
                                        tenantListTemporary[j].userId = it.data!!.tenantBaseInfoDto[i].userId
                                        break
                                    }
                                }
                            }

                            for (item in tenantListTemporary) {

                                if (item.drivingLicenseImagesArray.isNotEmpty() && item.coSignerFlag!!) {
                                    sendMultipleCoSigner(
                                        item.emailId,
                                        item.tenantId,
                                        item.paystubImagesArray,
                                        item.drivingLicenseImagesArray,
                                        item.w2sImagesArray
                                    )
                                } else if (item.voidCheckImagesArray.isNotEmpty() && !item.coSignerFlag!!) {
                                    sendMultipleCoTenant(
                                        item.emailId,
                                        item.tenantId,
                                        item.voidCheckImagesArray
                                    )
                                } else {
                                    val builder = AlertDialog.Builder(this)
                                    builder.setTitle("Success")
                                    // Display a message on alert dialog
                                    builder.setMessage("All Information has been sent to landlord, Please wait until landlord take action on the changes you have made.")

                                    // Set a positive button and its click listener on alert dialog
                                    builder.setPositiveButton("OK") { dialog, which ->
                                        val intent =
                                            Intent(this, TenantListActivity::class.java)
                                        intent.putExtra("isUpdated", true)
                                        intent.flags =
                                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        startActivity(intent)

                                    }

                                    val dialog: AlertDialog = builder.create()
                                    dialog.show()
                                }
                            }

                        }
                        tenant_list_progress.visibility = View.GONE
                    },
                        { e ->
                            tenant_list_progress.visibility = View.GONE
                            Toast.makeText(applicationContext, "Fail", Toast.LENGTH_SHORT).show()
                            Log.e("error", e.message.toString())
                        })

            }
        }
    }

    @SuppressLint("CheckResult")
    private fun sendMultipleCoSigner(
        email: String?,
        newTenantId: String?,
        mArrayPaystubUri: ArrayList<Uri>?,
        mArrayLicenseUri: ArrayList<Uri>?,
        mArrayw2sUri: ArrayList<Uri>?
    ) {

        val propertyId: RequestBody = findApiResponse.propertyId
            .toRequestBody("text/plain".toMediaTypeOrNull())
        val emailId: RequestBody = email!!
            .toRequestBody("text/plain".toMediaTypeOrNull())
        val tenantId: RequestBody = newTenantId!!
            .toRequestBody("text/plain".toMediaTypeOrNull())
        val unitId: RequestBody = findApiResponse.unitId
            .toRequestBody("text/plain".toMediaTypeOrNull())


        val map: HashMap<String, RequestBody> = HashMap<String, RequestBody>()
        map.put("emailId", emailId)
        map.put("propertyId", propertyId)
        map.put("tenantId", tenantId)
        map.put("unitId", unitId)

        val uploadCall: ApiInterface =
            ApiClient(this).provideService(ApiInterface::class.java)
        val listOfObservable = ArrayList<Observable<UploadFileMultipartResponse>>()

        for (i in 0 until mArrayPaystubUri!!.size) {

            filePart = prepareFilePart("file", mArrayPaystubUri[i])

            val uploadFileType: RequestBody = "paystub"
                .toRequestBody("text/plain".toMediaTypeOrNull())
            map.put("uploadFileType", uploadFileType)

            listOfObservable.add(
                uploadCall.uploadFile(
                    map,
                    filePart
                )
            )

        }

        for (i in 0 until mArrayLicenseUri!!.size) {


            filePart = prepareFilePart("file", mArrayLicenseUri!![i])

            val uploadFileType: RequestBody = "license"
                .toRequestBody("text/plain".toMediaTypeOrNull())
            map.put("uploadFileType", uploadFileType)


            listOfObservable.add(
                uploadCall.uploadFile(
                    map,
                    filePart
                )
            )

        }

        for (i in 0 until mArrayw2sUri!!.size) {

            filePart = prepareFilePart("file", mArrayw2sUri!![i])
            val uploadFileType: RequestBody = "w2s"
                .toRequestBody("text/plain".toMediaTypeOrNull())
            map.put("uploadFileType", uploadFileType)

            listOfObservable.add(
                uploadCall.uploadFile(
                    map,
                    filePart
                )
            )

        }
        Observable.zip(listOfObservable) { args -> Arrays.asList(args) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val response = it[0]
                Log.d("response", response[0].toString())
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Success")
                // Display a message on alert dialog
                builder.setMessage("All Information has been sent to landlord, Please wait until landlord take action on the changes you have made.")

                // Set a positive button and its click listener on alert dialog
                builder.setPositiveButton("OK") { dialog, which ->
                    val intent =
                        Intent(this, TenantListActivity::class.java)
                    intent.putExtra("isUpdated", true)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)

                }

                val dialog: AlertDialog = builder.create()
                dialog.show()
            },
                {
                    tenant_list_progress.visibility = View.GONE
                    Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT).show()


                })


    }

    @SuppressLint("CheckResult")
    private fun sendMultipleCoTenant(
        email: String?,
        newTenantId: String?,
        mArrayVoidCheckUri: ArrayList<Uri>?
    ) {

        val propertyId: RequestBody = findApiResponse.propertyId
            .toRequestBody("text/plain".toMediaTypeOrNull())
        val emailId: RequestBody = email!!
            .toRequestBody("text/plain".toMediaTypeOrNull())
        val tenantId: RequestBody = newTenantId!!
            .toRequestBody("text/plain".toMediaTypeOrNull())
        val unitId: RequestBody = findApiResponse.unitId
            .toRequestBody("text/plain".toMediaTypeOrNull())


        val map: HashMap<String, RequestBody> = HashMap<String, RequestBody>()
        map.put("emailId", emailId)
        map.put("propertyId", propertyId)
        map.put("tenantId", tenantId)
        map.put("unitId", unitId)

        val uploadCall: ApiInterface =
            ApiClient(this).provideService(ApiInterface::class.java)
        val listOfObservable = ArrayList<Observable<UploadFileMultipartResponse>>()

        for (i in 0 until mArrayVoidCheckUri!!.size) {

            filePart = prepareFilePart("file", mArrayVoidCheckUri[i])

            val uploadFileType: RequestBody = "voidCheck"
                .toRequestBody("text/plain".toMediaTypeOrNull())
            map.put("uploadFileType", uploadFileType)

            listOfObservable.add(
                uploadCall.uploadFile(
                    map,
                    filePart
                )
            )

        }
        Observable.zip(listOfObservable) { args -> Arrays.asList(args) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val response = it[0]
                Log.d("response", response[0].toString())
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Success")
                // Display a message on alert dialog
                builder.setMessage("All Information has been sent to landlord, Please wait until landlord take action on the changes you have made.")

                // Set a positive button and its click listener on alert dialog
                builder.setPositiveButton("OK") { dialog, which ->
                    val intent =
                        Intent(this, TenantListActivity::class.java)
                    intent.putExtra("isUpdated", true)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)

                }

                val dialog: AlertDialog = builder.create()
                dialog.show()
            },
                {
                    tenant_list_progress.visibility = View.GONE
                    Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT).show()

                })


    }


    @NonNull
    private fun prepareFilePart(
        partName: String,
        fileUri: Uri
    ): MultipartBody.Part {

        val file: File = mp.app.calonex.utility.FileUtils.getFile(this, fileUri)

        val requestFile: RequestBody = file
            .asRequestBody(contentResolver.getType(fileUri)?.let { it.toMediaTypeOrNull() })
        return MultipartBody.Part.createFormData("file", file.name, requestFile)
    }

    // handle back click, empty static tenant list, finish this activity and start TenantListActivity as a new activity
    // OR
    // On back click open "TenantPropertyUnitDetailActivity" but clean the previos stack and no need to do anything else because
    // staticTenantList will be cleaned with this step automatically


    private fun tenantList() {

        val vi: LayoutInflater? = layoutInflater

//        if (isUpdated) {
//
//            tenant_list_rv!!.layoutManager = LinearLayoutManager(this)
//            val tenantListAdapter = TenantListEditAdapter(this, staticTenantList, true)
//
//            tenant_list_rv!!.adapter = tenantListAdapter
//
//        } else {

        tenant_list_rv!!.layoutManager = LinearLayoutManager(this)
        val tenantListAdapter =
            TenantListEditAdapter(this, tenantListTemporary, false)
        tenant_list_rv!!.adapter = tenantListAdapter
//        }
    }

    companion object {
        var isRemoved: Boolean = false
        var isEdited: Boolean = false

        var tenantImagesPaystub = ArrayList<Uri>()
        var tenantImagesLicense = ArrayList<Uri>()
        var tenantImagesW2s = ArrayList<Uri>()
        var tenantImagesVoidCheck = ArrayList<Uri>()

    }
}
