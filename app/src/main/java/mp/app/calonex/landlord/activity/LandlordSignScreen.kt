package mp.app.calonex.landlord.activity

import android.app.Activity
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
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.github.gcacace.signaturepad.views.SignaturePad
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_signature_on_screen.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.FindLeaseCredentials
import mp.app.calonex.common.apiCredentials.SignatureLeaseCredentials
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.model.LeaseRequestInfo
import mp.app.calonex.landlord.model.LeaseSignature
import mp.app.calonex.landlord.response.FindLeaseResponse
import mp.app.calonex.landlord.response.SignatureLeaseResponse
import mp.app.calonex.tenant.response.ResponseDtoResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class LandlordSignScreen : CxBaseActivity2() {

    private var leaseId: String? = ""
    var uploaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signature_on_screen)
        startHandler()
        leaseId = intent.getStringExtra(getString(R.string.intent_lease_id))

        signature_pad.setOnSignedListener(
            object : SignaturePad.OnSignedListener {
                override fun onStartSigning() {
                }

                override fun onSigned() {
                    uploaded = false

                    save_button?.setEnabled(true)
                    clear_button?.setEnabled(true)
                }

                override fun onClear() {
                    save_button?.setEnabled(false)
                    clear_button?.setEnabled(false)
                }
            }
        )

        clear_button?.setOnClickListener {
            signature_pad!!.clear()
        }

        save_button.setOnClickListener {
            try {
                if (uploaded) {
                    fileProfile?.let { it1 -> registerApi(it1) }

                } else {
                    val signatureBitmap = signature_pad!!.signatureBitmap
                    if (addJpgSignatureToGallery(signatureBitmap)) {

                    } else {
                        Toast.makeText(
                            this,
                            "Unable to store the signature",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        iv_upload_sign.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    LandlordSignUploadScreen::class.java
                ).putExtra(getString(R.string.intent_lease_id), leaseId)
            )
            finish()
            overridePendingTransition(0, 0)

        }
        clear_button_upload.setOnClickListener {
            save_button?.setEnabled(false)
            clear_button_upload?.visibility = View.GONE
            uploaded = false
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

    fun addJpgSignatureToGallery(signature: Bitmap): Boolean {
        var result = false
        try {
            val photo = File(
                getAlbumStorageDir("SignaturePad"),
                String.format("Signature_%d.jpg", System.currentTimeMillis())
            )
            saveBitmapToJPG(signature, photo)
            scanMediaFile(photo)
            result = true
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }

    private fun scanMediaFile(photo: File) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val contentUri: Uri = Uri.fromFile(photo)
        mediaScanIntent.data = contentUri
        registerApi(contentUri)
        this.sendBroadcast(mediaScanIntent)
    }

    private val GALLERY = 1
    private val CAMERA = 2
    private var fileProfile: File? = null

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY && resultCode != Activity.RESULT_CANCELED) {
            if (data != null) {
                val contentURI = data.data
                fileProfile = Util.getRealPathFromUri(this, contentURI!!)
                save_button?.setEnabled(true)
                clear_button_upload?.visibility = View.VISIBLE
                uploaded = true
            }
        } else if (requestCode == CAMERA && resultCode != Activity.RESULT_CANCELED) {
            if (data != null) {
                val thumbnail = data.extras!!.get("data") as Bitmap
                fileProfile =
                    Util.bitmapToFile(this, thumbnail, getString(R.string.profile_pic))
                save_button?.setEnabled(true)
                clear_button_upload?.visibility = View.VISIBLE
                uploaded = true
            }
        }
    }

    fun registerApi(fileUri: Uri) {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            signature_pad_progress!!.visibility = View.VISIBLE
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
            builder.addFormDataPart("leaseId", leaseId!!)
            builder.addFormDataPart("userId", Kotpref.userId)
            builder.addFormDataPart("role", Kotpref.userRole)
            val file: File = mp.app.calonex.utility.FileUtils.getFile(this, fileUri)
            builder.addFormDataPart(
                "signature", file!!.name,
                file.asRequestBody(MultipartBody.FORM)
            )
            val requestBody: RequestBody = builder.build()
            val uploadSign: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)

            val apiCallUploadSign: Observable<ResponseDtoResponse> =
                uploadSign.uploadSignDoc(requestBody)

            apiCallUploadSign.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({

                    if (it.responseDto?.responseCode!!.equals(200)) {
                        getLeaseList(leaseId!!)


                    } else {
                        signature_pad_progress.visibility = View.GONE
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        Toast.makeText(
                            this,
                            it.responseDto?.exceptionDescription!!,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                },
                    { e ->
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        signature_pad_progress.visibility = View.GONE
                        Toast.makeText(
                            this,
                            e.message,
                            Toast.LENGTH_SHORT
                        ).show()
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


    fun registerApi(file: File) {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            signature_pad_progress!!.visibility = View.VISIBLE
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
            builder.addFormDataPart("leaseId", leaseId!!)
            builder.addFormDataPart("userId", Kotpref.userId)
            builder.addFormDataPart("role", Kotpref.userRole)
            builder.addFormDataPart(
                "signature", file!!.name,
                file.asRequestBody(MultipartBody.FORM)
            )
            val requestBody: RequestBody = builder.build()
            val uploadSign: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)

            val apiCallUploadSign: Observable<ResponseDtoResponse> =
                uploadSign.uploadSignDoc(requestBody)

            apiCallUploadSign.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({

                    if (it.responseDto?.responseCode!!.equals(200)) {
                        getLeaseList(leaseId!!)


                    } else {
                        signature_pad_progress.visibility = View.GONE
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        Toast.makeText(
                            this,
                            it.responseDto?.exceptionDescription!!,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                },
                    { e ->
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        signature_pad_progress.visibility = View.GONE
                        Toast.makeText(
                            this,
                            e.message,
                            Toast.LENGTH_SHORT
                        ).show()
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

    private fun getLeaseList(idLease: String) {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            //Create retrofit Service
            signature_pad_progress!!.visibility = View.VISIBLE
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            val credential = FindLeaseCredentials()

            credential.leaseId = idLease
            val findApi: ApiInterface = ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<FindLeaseResponse> =
                findApi.findLease(credential)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", it.responseDto?.responseDescription.toString())



                    if (it.data != null) {
                        signLeaseInfo = it.data!![0]
                        getSignatureList(signLeaseInfo.leaseId)
                    } else {
                        signature_pad_progress.visibility = View.GONE
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    }

                },
                    { e ->
                        Log.e("onFailure", e.toString())
                        signature_pad_progress!!.visibility = View.GONE
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

    private fun getSignatureList(idLease: String) {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            //Create retrofit Service
            signature_pad_progress!!.visibility = View.VISIBLE
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            val credential = SignatureLeaseCredentials()

            credential.leaseId = idLease
            val signatureApi: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<SignatureLeaseResponse> =
                signatureApi.getLeaseSignature(credential)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", it.responseDto?.responseDescription.toString())


                    signature_pad_progress.visibility = View.GONE
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)


                    if (it.data != null) {
                        signListSignature = it.data!!

                    }


                    val builder = AlertDialog.Builder(this)

                    builder.setTitle(getString(R.string.success))

                    // Display a message on alert dialog
                    builder.setMessage(getString(R.string.lease_sign_success))

                    // Set a positive button and its click listener on alert dialog
                    builder.setPositiveButton(getString(R.string.ok)) { dialog, which ->
                        val intent =
                            Intent(this, LeaseRequestDetailScreen::class.java)
                        intent.putExtra(getString(R.string.intent_sign_lease), true)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        finish()
                    }

                    val dialog: AlertDialog = builder.create()
                    dialog.show()

                },
                    { e ->
                        Log.e("onFailure", e.toString())
                        signature_pad_progress!!.visibility = View.GONE
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
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
        var signLeaseInfo = LeaseRequestInfo()
        var signListSignature = ArrayList<LeaseSignature>()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Util.navigationBack(this@LandlordSignScreen)
    }
}