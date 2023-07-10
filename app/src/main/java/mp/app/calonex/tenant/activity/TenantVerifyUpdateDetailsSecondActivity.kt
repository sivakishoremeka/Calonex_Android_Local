package mp.app.calonex.tenant.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.recyclerview.widget.GridLayoutManager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_tenant_verify_update_details_second.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.FetchDocumentCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.CxBaseActivity2
import mp.app.calonex.landlord.adapter.ImgListAdapter
import mp.app.calonex.landlord.response.FetchDocumentResponse
import mp.app.calonex.landlord.response.UploadFileMultipartResponse
import mp.app.calonex.tenant.activity.TenantVerifyUpdateDetailsFirstActivity.Companion.tenantDetails
import mp.app.calonex.tenant.response.ModifyTenantDataResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class TenantVerifyUpdateDetailsSecondActivity : CxBaseActivity2() {

    private val GALLERY = 1
    private val CAMERA = 2
    lateinit var filePart: MultipartBody.Part

    var mArrayPaystubUri = ArrayList<Uri>()
    var mArrayLicenseUri = ArrayList<Uri>()
    var mArrayw2sUri = ArrayList<Uri>()
    var leasePosition : Int = 0


    private var adapter: ImgListAdapter? = null

    var payStubClickActive: Boolean = false
    var licenseClickActive: Boolean = false
    var w2sClickActive: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tenant_verify_update_details_second)
        leasePosition = intent.getIntExtra("leasePosition",0)

        fetchImages()
        actionComponent()

    }

    fun initView() {
        rv_paystubs?.layoutManager = GridLayoutManager(this, 3)

        rv_license?.layoutManager = GridLayoutManager(this, 3)

        rv_w2s?.layoutManager = GridLayoutManager(this, 3)

        if (mArrayPaystubUri.size != 0) {
            adapter = ImgListAdapter(this, mArrayPaystubUri, null, null)
            rv_paystubs?.adapter = adapter
        }

        if (mArrayLicenseUri.size != 0) {
            adapter = ImgListAdapter(this, mArrayLicenseUri, null, null)
            rv_license?.adapter = adapter
        }

        if (mArrayw2sUri.size != 0) {
            adapter = ImgListAdapter(this, mArrayw2sUri, null, null)
            rv_w2s?.adapter = adapter
        }
    }


    fun choosePhotoFromGallary() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        galleryIntent.putExtra("type", "paystub")
        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
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

    private fun actionComponent() {

        header_back.setOnClickListener {
            super.onBackPressed()
            Util.navigationBack(this)
        }


        upload_paystab_view.setOnClickListener {
            payStubClickActive = true
            licenseClickActive = false
            w2sClickActive = false
            showPictureDialog()
        }

        upload_license_view.setOnClickListener {
            payStubClickActive = false
            licenseClickActive = true
            w2sClickActive = false
            showPictureDialog()
        }

        upload_w2s_view.setOnClickListener {
            payStubClickActive = false
            licenseClickActive = false
            w2sClickActive = true

            showPictureDialog()
        }

        tenant_details_upload_action.setOnClickListener {

            /*if (mArrayw2sUri.size == 0) {
                Toast.makeText(applicationContext, "Please upload w2s", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (mArrayPaystubUri.size == 0) {
                Toast.makeText(applicationContext, "Please upload PayStub", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (mArrayLicenseUri.size == 0) {
                Toast.makeText(applicationContext, "Please upload License", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }*/

            sendMultiple()
        }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY) {
            if (data != null) {
                val contentURI = data.data
                if (contentURI != null) {
                    if (payStubClickActive) {
                        mArrayPaystubUri.add(contentURI)
                        val set = HashSet<Uri>(mArrayPaystubUri)
                        mArrayPaystubUri.clear()
                        mArrayPaystubUri.addAll(set)
                        adapter = ImgListAdapter(
                            this,
                            mArrayPaystubUri, null, null
                        )

                        rv_paystubs?.adapter = adapter

                    }

                    if (w2sClickActive) {
                        mArrayw2sUri.add(contentURI)
                        val set = HashSet<Uri>(mArrayw2sUri)
                        mArrayw2sUri.clear()
                        mArrayw2sUri.addAll(set)
                        adapter = ImgListAdapter(
                            this,
                            mArrayw2sUri, null, null
                        )

                        rv_w2s?.adapter = adapter

                    }

                    if (licenseClickActive) {
                        mArrayLicenseUri.add(contentURI)
                        val set = HashSet<Uri>(mArrayLicenseUri)
                        mArrayLicenseUri.clear()
                        mArrayLicenseUri.addAll(set)
                        adapter = ImgListAdapter(
                            this,
                            mArrayLicenseUri, null, null
                        )

                        rv_license?.adapter = adapter
                    }
                }
            }

        } else if (requestCode == CAMERA) {
            if (data != null) {
                val bitmap = data!!.extras!!.get("data") as Bitmap
                addJpgSignatureToGallery(bitmap)
            }

        }
    }


    fun getAlbumStorageDir(albumName: String?): File? {
        // Get the directory for the user's public pictures directory.
        val file = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ), albumName
        )
        if (!file.mkdirs()) {
            Log.e("SignaturePad", "Directory not created")
        }
        return file
    }

    @Throws(IOException::class)
    fun saveBitmapToJPG(bitmap: Bitmap, photo: File?) {
        val newBitmap =
            Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(newBitmap)
        canvas.drawColor(Color.WHITE)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        val stream: OutputStream = FileOutputStream(photo)
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        stream.close()
    }

    private fun scanMediaFile(photo: File) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val contentURI: Uri = Uri.fromFile(photo)
        mediaScanIntent.data = contentURI

        if (payStubClickActive) {
            mArrayPaystubUri.add(contentURI)
            val set = HashSet<Uri>(mArrayPaystubUri)
            mArrayPaystubUri.clear()
            mArrayPaystubUri.addAll(set)
            adapter = ImgListAdapter(
                this,
                mArrayPaystubUri, null, null
            )

            rv_paystubs?.adapter = adapter

        }

        if (w2sClickActive) {
            mArrayw2sUri.add(contentURI)
            val set = HashSet<Uri>(mArrayw2sUri)
            mArrayw2sUri.clear()
            mArrayw2sUri.addAll(set)
            adapter = ImgListAdapter(
                this,
                mArrayw2sUri, null, null
            )

            rv_w2s?.adapter = adapter

        }

        if (licenseClickActive) {
            mArrayLicenseUri.add(contentURI)
            val set = HashSet<Uri>(mArrayLicenseUri)
            mArrayLicenseUri.clear()
            mArrayLicenseUri.addAll(set)
            adapter = ImgListAdapter(
                this,
                mArrayLicenseUri, null, null
            )

            rv_license?.adapter = adapter
        }


        this.sendBroadcast(mediaScanIntent)
    }

    fun addJpgSignatureToGallery(image: Bitmap): Boolean {
        var result = false
        try {
            val photo = File(
                getAlbumStorageDir("Image"),
                String.format("Image_%d.jpg", System.currentTimeMillis())
            )
            saveBitmapToJPG(image, photo)
            scanMediaFile(photo)
            result = true
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }


    private fun sendTenantData() {
        val modifyTenantData: ApiInterface =
            ApiClient(this).provideService(ApiInterface::class.java)
        val apiCall: Observable<ModifyTenantDataResponse> =
            modifyTenantData.modifyTenantData(tenantDetails)

        apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                progressBar.visibility = View.GONE

                val intent =
                    Intent(
                        this@TenantVerifyUpdateDetailsSecondActivity,
                        TenantPropertyUnitDetailActivityCx::class.java
                    )
                intent.putExtra("leasePosition", leasePosition)
                startActivity(intent)
            },
                { e ->
                    progressBar.visibility = View.GONE
                    Toast.makeText(applicationContext, R.string.error_something, Toast.LENGTH_SHORT).show()
                    Log.e("error", e.message.toString())
                })
    }


    @SuppressLint("CheckResult")
    private fun sendMultiple() {

        progressBar.visibility = View.VISIBLE

        val propertyId: RequestBody = tenantDetails.propertyId
            .toRequestBody("text/plain".toMediaTypeOrNull())
        val emailId: RequestBody = tenantDetails.emailId!!
            .toRequestBody("text/plain".toMediaTypeOrNull())
//        val uploadFileType: RequestBody = "paystub"
//            .toRequestBody("text/plain".toMediaTypeOrNull())
        val tenantId: RequestBody = tenantDetails.tenantId!!
            .toRequestBody("text/plain".toMediaTypeOrNull())
        val unitId: RequestBody = tenantDetails.unitId!!
            .toRequestBody("text/plain".toMediaTypeOrNull())
        val userId: RequestBody = tenantDetails.userId!!
            .toRequestBody("text/plain".toMediaTypeOrNull())


        val map: HashMap<String, RequestBody> = HashMap<String, RequestBody>()
        map.put("emailId", emailId)
        map.put("propertyId", propertyId)
        map.put("tenantId", tenantId)
        map.put("unitId", unitId)

        val uploadCall: ApiInterface =
            ApiClient(this).provideService(ApiInterface::class.java)
        val listOfObservable = ArrayList<Observable<UploadFileMultipartResponse>>()

        var uploadApiHit: Boolean = false

        for (i in 0 until mArrayPaystubUri.size) {

            if (!mArrayPaystubUri[i].toString().contains("s3Bucket", true)) {
                filePart = prepareFilePart("file", mArrayPaystubUri[i])

                uploadApiHit = true
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
        }

        for (i in 0 until mArrayLicenseUri.size) {

            if (!mArrayLicenseUri[i].toString().contains("s3Bucket", true)) {

                filePart = prepareFilePart("file", mArrayLicenseUri[i])

                uploadApiHit = true
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
        }

        for (i in 0 until mArrayw2sUri.size) {

            if (!mArrayw2sUri[i].toString().contains("s3Bucket", true)) {
                filePart = prepareFilePart("file", mArrayw2sUri[i])
                val uploadFileType: RequestBody = "w2s"
                    .toRequestBody("text/plain".toMediaTypeOrNull())
                map.put("uploadFileType", uploadFileType)
                uploadApiHit = true

                listOfObservable.add(
                    uploadCall.uploadFile(
                        map,
                        filePart
                    )
                )
            }
        }
        Observable.zip(listOfObservable) { args -> Arrays.asList(args) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val response = it[0]
                Log.d("response", response[0].toString())
                sendTenantData()
            },
                {
                    progressBar.visibility = View.GONE
                    Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT).show()

                })

        if (!uploadApiHit) {
            sendTenantData()
        }

    }

    @SuppressLint("CheckResult")
    private fun fetchImages() {
        if (NetworkConnection.isNetworkConnected(this)) {
//            this!!.getWindow().setFlags(
//                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
//            )
            progressBar.visibility = View.VISIBLE
            val credentials = FetchDocumentCredential()
            credentials.propertyId = tenantDetails.propertyId
            credentials.leaseId = tenantDetails.leaseId
            credentials.userId = tenantDetails.userId
            credentials.tenantId = tenantDetails.tenantId
            credentials.unitId = tenantDetails.unitId

            val signatureApi: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<FetchDocumentResponse> =
                signatureApi.fetchDocument(credentials)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", it.responseDto!!.responseDescription.toString())

                    progressBar.visibility = View.GONE
                    if (it.responseDto!!.responseCode.equals(200)) {

                        for (item in it.data!!) {
                            when {
                                item.uploadFileType.contains("paystub", true) -> {

                                    val file = File(item.fileName)
                                    val uri = Uri.fromFile(file)

                                    var url: String = ""
                                    for (i in 0 until uri.pathSegments.size) {
                                        if (i == 0) {
                                            url = url + uri.pathSegments[i]
                                        } else if (i == 1) {
                                            url = url + "//" + uri.pathSegments[i]
                                        } else {
                                            url = url + "/" + uri.pathSegments[i]
                                        }
                                    }
                                    var finalUri = resources.getString(R.string.base_url_img) + url
                                    mArrayPaystubUri.add(finalUri.toUri())
                                }
                                item.uploadFileType.contains("w2s", true) -> {
                                    val file = File(item.fileName)
                                    val uri = Uri.fromFile(file)

                                    var url: String = ""
                                    for (i in 0 until uri.pathSegments.size) {
                                        if (i == 0) {
                                            url = url + uri.pathSegments[i]
                                        } else if (i == 1) {
                                            url = url + "//" + uri.pathSegments[i]
                                        } else {
                                            url = url + "/" + uri.pathSegments[i]
                                        }
                                    }
                                    val finalUri = resources.getString(R.string.base_url_img) + url
                                    mArrayw2sUri.add(finalUri.toUri())
                                }
                                item.uploadFileType.contains("license", true) -> {
                                    val file = File(item.fileName)
                                    val uri = Uri.fromFile(file)

                                    var url: String = ""
                                    for (i in 0 until uri.pathSegments.size) {
                                        if (i == 0) {
                                            url = url + uri.pathSegments[i]
                                        } else if (i == 1) {
                                            url = url + "//" + uri.pathSegments[i]
                                        } else {
                                            url = url + "/" + uri.pathSegments[i]
                                        }
                                    }
                                    var finalUri = resources.getString(R.string.base_url_img) + url
                                    mArrayLicenseUri.add(finalUri.toUri())
                                }
                            }
                        }
                    }
                    initView()

                },
                    { e ->
                        Log.e("onFailure", e.toString())
                        progressBar.visibility = View.GONE
//                        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        e.message?.let {
                            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                        }
                        initView()
                    })
        } else {
            Toast.makeText(this, this.getString(R.string.error_network), Toast.LENGTH_SHORT).show()

        }
    }


}
