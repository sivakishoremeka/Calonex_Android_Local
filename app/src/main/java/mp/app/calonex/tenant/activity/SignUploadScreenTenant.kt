package mp.app.calonex.tenant.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_signature_upload_screen.*
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

class SignUploadScreenTenant : AppCompatActivity() {

    private var leaseId: String? = ""
    var uploaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signature_upload_screen)

        leaseId = intent.getStringExtra(getString(R.string.intent_lease_id))
        showPictureDialog()
        signature_image.visibility = View.VISIBLE

        clear_button?.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    SignatureUploadActivity::class.java
                ).putExtra(getString(R.string.intent_lease_id), leaseId)
            )
            finish()
            overridePendingTransition(0, 0)
        }

        save_button.setOnClickListener {
            try {
                if (uploaded) {
                    fileProfile?.let { it1 -> registerApi(it1) }

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        iv_upload_sign.setOnClickListener {
            showPictureDialog()
        }
        clear_button_upload.setOnClickListener {
            save_button?.setEnabled(false)
            clear_button_upload?.visibility = View.GONE
            uploaded = false
        }
    }


    private val GALLERY = 1
    private val CAMERA = 2
    private var fileProfile: File? = null


    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems =
            arrayOf("Select photo from gallery", /*"Capture photo from camera",*/ "Cancel")
        pictureDialog.setItems(
            pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                //1 -> takePhotoFromCamera()
                2 -> {
                    startActivity(
                        Intent(
                            this,
                            SignatureUploadActivity::class.java
                        ).putExtra(getString(R.string.intent_lease_id), leaseId)
                    )
                    finish()
                    overridePendingTransition(0, 0)
                }
            }
        }
        pictureDialog.show()
    }

    fun choosePhotoFromGallary() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    /* override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
         super.onActivityResult(requestCode, resultCode, data)
         *//* if (resultCode == this.RESULT_CANCELED)
         {
         return
         }*//*
        if (requestCode == GALLERY && resultCode != Activity.RESULT_CANCELED) {
            if (data != null) {
                val contentURI = data.data
                fileProfile = Util.getRealPathFromUri(this, contentURI!!)
                save_button?.setEnabled(true)
                clear_button?.setEnabled(true)
                clear_button?.visibility = View.VISIBLE
                uploaded = true

                Glide.with(this)
                    .load(fileProfile)
                    .into(signature_image!!)

            }
        } else if (requestCode == CAMERA && resultCode != Activity.RESULT_CANCELED) {
            if (data != null) {
                val thumbnail = data.extras!!.get("data") as Bitmap
                fileProfile =
                    Util.bitmapToFile(this, thumbnail, getString(R.string.profile_pic))
                save_button?.setEnabled(true)
                clear_button?.setEnabled(true)
                clear_button?.visibility = View.VISIBLE
                uploaded = true

                Glide.with(this)
                    .load(fileProfile)
                    .into(signature_image!!)

            }
        }
    }
*/

    @SuppressLint("NewApi")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            // When an Image is picked
            if (requestCode == GALLERY && resultCode == Activity.RESULT_OK
                && null != data
            ) {
                // Get the Image from data
                val filePathColumn = arrayOf(MediaStore.MediaColumns.DATA)
                var filePath: String?
                var uri: Uri
                if (data.data != null) {

                    uri = data.data!!
                    filePath = getRealPathFromUri(uri)
                    fileProfile = File(filePath)

                    Log.d("Upload Image Screen", "filePath=> $filePath")

                    val mImageUri = data.data

                    save_button?.setEnabled(true)
                    clear_button?.setEnabled(true)
                    clear_button?.visibility = View.VISIBLE
                    uploaded = true

                    Glide.with(this)
                        .load(mImageUri)
                        .into(signature_image!!)

                } else {
                    if (data.clipData != null) {
                        val mClipData = data.clipData

                        for (i in 0 until mClipData!!.itemCount) {

                            val item = mClipData.getItemAt(i)
                            val uri = item.uri
                            val cursor =
                                contentResolver.query(uri, filePathColumn, null, null, null)
                            // Move to first row
                            cursor!!.moveToFirst()

                            filePath = uri.path


                            fileProfile = File(filePath)

                            Log.d("FilePath", "filePath=" + filePath);

                            Glide.with(this)
                                .load(filePath)
                                .into(signature_image!!)

                            cursor.close()


                        }

                    }
                }
            } else {

                Toast.makeText(
                    this, "You haven't picked Image",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                .show()
        }

        super.onActivityResult(requestCode, resultCode, data)
    }


    private fun getRealPathFromUri(
        contentUri: Uri
    ): String {
        val cursor =
            contentResolver.query(contentUri, null, null, null, null)

        if (cursor == null) {
            return contentUri.path.toString()
        } else {
            cursor.moveToFirst()
            var index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            return cursor.getString(index)
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
                            Intent(this, HomeActivityTenant::class.java)
                        intent.putExtra("isUpdated", true)
                        // intent.flags =
                        //   Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
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
        Util.navigationBack(this@SignUploadScreenTenant)
    }
}