package mp.app.calonex.tenant.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.github.gcacace.signaturepad.views.SignaturePad
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_signature_on_screen.*
import mp.app.calonex.R
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.landlord.activity.CxBaseActivity
import mp.app.calonex.landlord.activity.LandlordSignUploadScreen
import mp.app.calonex.landlord.response.UploadSignatureResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class SignatureUploadActivity : CxBaseActivity() {
    private var leaseId: String = ""
    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE =
        arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signature_on_screen)
        verifyStoragePermissions(this)
        leaseId = intent.getStringExtra("leaseId").toString()

        signature_pad.setOnSignedListener(
            object : SignaturePad.OnSignedListener {
                override fun onStartSigning() {
                }

                override fun onSigned() {
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

        iv_upload_sign.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    SignUploadScreenTenant::class.java
                ).putExtra(getString(R.string.intent_lease_id), leaseId)
            )
            finish()
            overridePendingTransition(0, 0)

        }

        save_button.setOnClickListener {

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
        sendToServer(contentUri)
        this.sendBroadcast(mediaScanIntent)
    }

    @SuppressLint("CheckResult")
    private fun sendToServer(fileUri: Uri) {

        signature_pad_progress.visibility = View.VISIBLE
        val file: File = mp.app.calonex.utility.FileUtils.getFile(this, fileUri)
        val requestFile: RequestBody = file
            .asRequestBody(contentResolver.getType(fileUri)?.let { it.toMediaTypeOrNull() })

        val filePart: MultipartBody.Part =
            MultipartBody.Part.createFormData("signature", file.name, requestFile)

        val leaseId: RequestBody = leaseId
            .toRequestBody("text/plain".toMediaTypeOrNull())
        Log.e("leaseid",Kotpref.leaseId)
        val userId: RequestBody = Kotpref.userId.toRequestBody("text/plain".toMediaTypeOrNull())
        val role: RequestBody = Kotpref.userRole
            .toRequestBody("text/plain".toMediaTypeOrNull())

        val map: HashMap<String, RequestBody> = HashMap<String, RequestBody>()
        map.put("leaseId", leaseId)
        map.put("userId", userId)
        map.put("role", role)
          Log.e("rq",""+map.toString())
        val uploadSign: ApiInterface =
            ApiClient(this).provideService(ApiInterface::class.java)

        val apiCallUploadSign: Observable<UploadSignatureResponse> =
            uploadSign.uploadSignature(
                map,
                filePart
            )

        apiCallUploadSign.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({
                signature_pad_progress.visibility = View.GONE
                val builder = AlertDialog.Builder(this)
                if (it.responseDto!!.responseCode == 201 || it.responseDto!!.responseCode == 200 || it.responseDto!!.responseCode == 204){
                    builder.setTitle("Success")

                    // Display a message on alert dialog
                    builder.setMessage("Lease Signed Successfully")
                    Kotpref.leaseSigned = true
                    builder.setPositiveButton("Ok") { dialog, which ->
                        val intent =
                            Intent(this, HomeActivityTenant::class.java)
                        intent.putExtra("isUpdated", true)
                       // intent.flags =
                         //   Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                }

                else if (it.responseDto!!.responseCode == 502){
                    builder.setTitle("Failed")

                    builder.setMessage(it.responseDto!!.responseDescription)
                    builder.setPositiveButton("Ok") { dialog, which ->
                        super.onBackPressed()
                    }
                }

                else if (it.responseDto?.responseDescription.toString() != null) {
                    builder.setTitle("Failed")
                    builder.setMessage(it.responseDto!!.responseDescription)
                    builder.setPositiveButton("Ok") { dialog, which ->
                        val intent =
                            Intent(this, TenantListActivity::class.java)
                        intent.putExtra("isUpdated", true)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }

                }
                else {
                    builder.setTitle("Failed")
                    builder.setMessage("Internal Server Error, Please try again after some time")
                    builder.setPositiveButton("Ok") { dialog, which ->
                        val intent =
                            Intent(this, TenantListActivity::class.java)
                        intent.putExtra("isUpdated", true)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }

                }

                val dialog: AlertDialog = builder.create()
                dialog.show()
            },
                { e ->
                    signature_pad_progress.visibility = View.GONE

                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Failed")
                    builder.setMessage(e.message.toString())
                    builder.setPositiveButton("Try Again") { dialog, which ->
                        dialog.dismiss()
                    }
                    val dialog: AlertDialog = builder.create()
                    dialog.show()

                })
    }


    fun verifyStoragePermissions(activity: Activity?) {
        // Check if we have write permission
        val permission =
            ActivityCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        }
    }
    override fun onSaveInstanceState(oldInstanceState: Bundle) {
        super.onSaveInstanceState(oldInstanceState)
        oldInstanceState.clear()
    }

}
