package mp.app.calonex.tenant.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.telephony.PhoneNumberUtils
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_tenant_verify_update_details_first.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.UserDetailCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.*
import mp.app.calonex.landlord.activity.CxBaseActivity2
import mp.app.calonex.landlord.model.TenantInfoPayload
import mp.app.calonex.landlord.model.ZipCityStateModel
import mp.app.calonex.landlord.response.UploadFileMultipartResponse
import mp.app.calonex.tenant.activity.TenantPropertyHistoryScreen.Companion.leaseListApiResponse
import mp.app.calonex.tenant.response.LeaseByUserIdResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class TenantVerifyUpdateDetailsFirstActivity : CxBaseActivity2() {

    private val GALLERY = 1
    private val CAMERA = 2
    lateinit var filePart: MultipartBody.Part
    private var stateList = ArrayList<String>()
    private var selectedState: String? = null
    private var zcsList = ArrayList<ZipCityStateModel>()
    private var tenantPhoneNumber: String? = null
    private var tenantSSNNumber: String? = null

    var leasePosition: Int = 0

    private var tenant = TenantInfoPayload()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tenant_verify_update_details_first)

        leasePosition = intent.getIntExtra("leasePosition", 0)

        zcsList = Util.getZipCityStateList(applicationContext)
        val listState = mutableListOf<String>()
       // val addLineNumberFormatter = UsPhoneNumberFormatter(tenant_phn!!)
       // tenant_phn!!.addTextChangedListener(addLineNumberFormatter)

        val addSsnFormatter = UsSSNNumberFormatter(txt_tenant_ssn!!)
        txt_tenant_ssn!!.addTextChangedListener(addSsnFormatter)

//        stateIssueList.add("State Issued")
        for (item in zcsList) {
            listState.add(item.state!!)
        }

        Util.setEditReadOnly(txt_tenant_city!!, false, InputType.TYPE_NULL)
        Util.setEditReadOnly(txt_tenant_state!!, false, InputType.TYPE_NULL)


        if (intent.getSerializableExtra("tenantDetails") != null) {
            tenant = intent.getSerializableExtra("tenantDetails") as TenantInfoPayload
            updateUi(tenant)
        }

        for (item in leaseListApiResponse[leasePosition].tenantBaseInfoDto) {
            if (item.userId == Kotpref.userId) {
                tenantResponse = item
            }
        }

        header_back.setOnClickListener {
            super.onBackPressed()
            Util.navigationBack(this)
        }


        verify_tenant_profile_change_icon.setOnClickListener {
            showPictureDialog()
        }


        txt_next.setOnClickListener {

            if (txt_tenant_address.text!!.isEmpty()) {
                txt_tenant_address?.error = getString(R.string.error_mandetory)
                txt_tenant_address?.requestFocus()
                return@setOnClickListener
            }
            if (txt_tenant_zipcode.text!!.isEmpty()) {
                txt_tenant_zipcode?.error = getString(R.string.error_mandetory)
                txt_tenant_zipcode?.requestFocus()
                return@setOnClickListener
            }
            if (txt_tenant_city.text!!.isEmpty()) {
                txt_tenant_city?.error = getString(R.string.error_mandetory)
                txt_tenant_city?.requestFocus()
                return@setOnClickListener
            }
            if (txt_tenant_state.text!!.isEmpty()) {
                txt_tenant_state?.error = getString(R.string.error_mandetory)
                txt_tenant_state?.requestFocus()
                return@setOnClickListener
            }


            tenantPhoneNumber = tenant_phn!!.text.toString().trim()

            if (Util.valueMandetory(applicationContext, tenantPhoneNumber, tenant_phn!!)) {
                return@setOnClickListener
            }

            if (tenantPhoneNumber!!.length != 10) {
                tenant_phn?.error = getString(R.string.error_phn)
                tenant_phn?.requestFocus()
                return@setOnClickListener
            }

            tenantSSNNumber = txt_tenant_ssn.text.toString().trim()

            if (Util.valueMandetory(applicationContext, tenantSSNNumber, txt_tenant_ssn!!)) {
                return@setOnClickListener
            }

            if (tenantSSNNumber!!.length != 11) {
                txt_tenant_ssn?.error = getString(R.string.error_ssn)
                txt_tenant_ssn?.requestFocus()
                return@setOnClickListener
            }


            if (txt_tenant_first_name.text!!.isEmpty()) {
                txt_tenant_first_name?.error = getString(R.string.error_mandetory)
                txt_tenant_first_name?.requestFocus()
                return@setOnClickListener
            }

            if (txt_tenant_license_id.text!!.isEmpty()) {
                txt_tenant_license_id?.error = getString(R.string.error_mandetory)
                txt_tenant_license_id?.requestFocus()
                return@setOnClickListener
            }

            tenantDetails.tenantId = tenantResponse.tenantId
            Kotpref.tenantId = tenantResponse.tenantId!!
            tenantDetails.cxId = tenantResponse.cxId
            tenantDetails.propertyId = ""
            tenantDetails.unitId = ""
            tenantDetails.leaseId = tenantResponse.leaseId
            tenantDetails.emailId = tenantResponse.emailId


            tenantDetails.address = txt_tenant_address.text.toString()
            tenantDetails.zipcode = txt_tenant_zipcode.text.toString()
            tenantDetails.city = txt_tenant_city.text.toString()
            tenantDetails.state = txt_tenant_state.text.toString()
            tenantDetails.tenantFirstName = txt_tenant_first_name.text.toString()
            tenantDetails.tenantLastName = txt_tenant_last_name.text.toString()
            tenantDetails.licenseNo = txt_tenant_license_id.text.toString()
            tenantDetails.ssn = tenantSSNNumber!!.filter { it.isDigit() }
            tenantDetails.phone = tenantPhoneNumber!!.filter { it.isDigit() }


            val intent =
                Intent(
                    this@TenantVerifyUpdateDetailsFirstActivity,
                    TenantVerifyUpdateDetailsSecondActivity::class.java
                )
            intent.putExtra("leasePosition", leasePosition)
            startActivity(intent)
        }

        txt_tenant_zipcode?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

                if (txt_tenant_zipcode!!.text.toString().length > 0) {
                    setCityState(txt_tenant_zipcode!!.text.toString().toInt())
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
       // getTenantDetailsAddedByLandlord();
    }

    private fun setCityState(valueZip: Int?) {
        val filterList: List<ZipCityStateModel>? = zcsList.filter { it.zip == valueZip }
        if (filterList!!.size > 0) {
            val filterPlan: ZipCityStateModel? = filterList.get(0)
            txt_tenant_city!!.setText(filterPlan!!.city)
            txt_tenant_state!!.setText(filterPlan.state)
        } else {
            txt_tenant_zipcode?.error = getString(R.string.error_zipcode)
            txt_tenant_zipcode?.requestFocus()
            txt_tenant_city!!.setText("")
            txt_tenant_state!!.setText("")
            return
        }


    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(
            pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    fun choosePhotoFromGallary() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
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

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        /* if (resultCode == this.RESULT_CANCELED)
         {
         return
         }*/
        if (requestCode == GALLERY) {
            if (data != null) {
                val contentURI = data.data
                try {
                    filePart = contentURI?.let { prepareFilePart("file", it) }!!
                    val bitmap =
                        MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    displayImage(bitmap)
                    sendToServer()

                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }

        } else if (requestCode == CAMERA) {
            if (data != null) {
                val thumbnail = data.extras!!.get("data") as Bitmap
                displayImage(thumbnail)
            }

        }
    }

    private fun sendToServer() {

        val propertyId: RequestBody = tenantDetails.propertyId
            .toRequestBody("text/plain".toMediaTypeOrNull())
        val emailId: RequestBody = tenantDetails.emailId!!
            .toRequestBody("text/plain".toMediaTypeOrNull())
        val uploadFileType: RequestBody = "Profile Pic"
            .toRequestBody("text/plain".toMediaTypeOrNull())
        val tenantId: RequestBody = tenantDetails.tenantId!!
            .toRequestBody("text/plain".toMediaTypeOrNull())
        val unitId: RequestBody = tenantDetails.unitId!!
            .toRequestBody("text/plain".toMediaTypeOrNull())
        val userId: RequestBody = tenantDetails.userId!!
            .toRequestBody("text/plain".toMediaTypeOrNull())


        val map: HashMap<String, RequestBody> = HashMap<String, RequestBody>()
        map.put("emailId", emailId)
        map.put("propertyId", propertyId)
        map.put("uploadFileType", uploadFileType)
        map.put("tenantId", tenantId)
        map.put("unitId", unitId)

        val addPropertyCall: ApiInterface =
            ApiClient(this).provideService(ApiInterface::class.java)

        val apiCallImagesUpload: Observable<UploadFileMultipartResponse> =
            addPropertyCall.uploadFile(
                map,
                filePart
            )

        apiCallImagesUpload.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({
                Log.e(
                    "onSuccess",
                    it.responseDto?.message.toString()
                )

            },
                { e ->
                    //                                        toast("upload image fail")
                    Log.e("error", e.message.toString())

                })

    }

    fun displayImage(bitmap: Bitmap) {
        Glide.with(this)
            .load(bitmap)
            .transform(CircleTransform())
            .into(verify_tenant_profile_pic!!)
    }


    private fun getTenantDetailsAddedByLandlord() {

        val credentials = UserDetailCredential()
        credentials.userId = Kotpref.userId

        val getLeaseByUserId: ApiInterface =
            ApiClient(this).provideService(ApiInterface::class.java)
        val apiCall: Observable<LeaseByUserIdResponse> =
            getLeaseByUserId.getLeaseByUserId(credentials)

        apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.data != null) {
                    tenantResponse = it.data!![0]
                    updateUi(it.data!![0])
                }
            },
                { e ->
                    Toast.makeText(applicationContext, "Fail", Toast.LENGTH_SHORT).show()
                    Log.e("error", e.message.toString())
                })

    }

    private fun updateUi(tenant: TenantInfoPayload) {
        txt_tenant_city.setText(tenant.city)
        txt_tenant_state.setText(tenant.state)
        txt_tenant_first_name.setText(tenant.tenantFirstName)
        txt_tenant_last_name.setText(tenant.tenantLastName)
        txt_tenant_license_id.setText(tenant.licenseNo)
//        txt_tenant_ssn.setText(String.format("000-00-0000", 123456789))

        if (!tenant.ssn.isNullOrEmpty()) {
            val ssnNumber = StringBuilder(tenant.ssn).insert(5, "-").insert(3, "-")
            txt_tenant_ssn.setText(ssnNumber)
        }
        if (!tenant.phone.isNullOrBlank())
            tenant_phn.setText(PhoneNumberUtils.formatNumber(tenant.phone, "US"))
        txt_tenant_address.setText(tenant.address)
        txt_tenant_zipcode.setText(tenant.zipcode)
        Kotpref.exactRole = tenant.role.toString()

    }

    companion object {
        var tenantDetails = TenantInfoPayload()
        var tenantResponse = TenantInfoPayload()

        //image pick code
        private val IMAGE_PICK_CODE = 1000

        //Permission code
        private val PERMISSION_CODE = 1001


    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    //handle requested permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    //permission from popup granted
                    pickImageFromGallery()
                } else {
                    //permission from popup denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    fun bitmapToFile(bitmap: Bitmap, fileNameToSave: String): File? { // File name like "image.png"
        //create a file to write bitmap data
        var file: File? = null
        return try {
            file = File(filesDir, fileNameToSave + ".png")
            file.createNewFile()

            //Convert bitmap to byte array
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 10, bos) // YOU can also save it in JPEG
            val bitmapdata = bos.toByteArray()

            //write the bytes in file
            val fos = FileOutputStream(file)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            file // it will return null
        }
    }


}

