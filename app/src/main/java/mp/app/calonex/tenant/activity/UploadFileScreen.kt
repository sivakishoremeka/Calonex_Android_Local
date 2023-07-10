package mp.app.calonex.tenant.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.screen_upload_file.*
import mp.app.calonex.R
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.landlord.activity.CxBaseActivity2
import mp.app.calonex.landlord.model.TenantInfoPayload
import mp.app.calonex.landlord.response.UploadFileMultipartResponse
import mp.app.calonex.tenant.activity.TenantVerifyUpdateDetailsFirstActivity.Companion.tenantDetails
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


class UploadFileScreen : CxBaseActivity2() {

    internal var REQUEST_GALLERY_CODE = 200
    internal var REQUEST_FILE_CODE = 300
    internal lateinit var imagesEncodedList: MutableList<String>
    var mArrayUri = ArrayList<Uri>()
    private lateinit var titleFileType: String
    var tenantDetail = TenantInfoPayload()

    lateinit var filePart: MultipartBody.Part

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screen_upload_file)
        titleFileType = intent.getStringExtra("title")!!
        upload_text_title.text = titleFileType


        btn_upload!!.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_GALLERY_CODE)
        }

        btn_upload_file!!.setOnClickListener {
            val intentFile = Intent()
            intentFile.action = Intent.ACTION_GET_CONTENT
            intentFile.type = "application/pdf"
            startActivityForResult(intentFile, 2)
        }

        btn_cancel.setOnClickListener {
            val intent = Intent()
            setResult(3, intent)
            finish()
        }

        btn_done!!.setOnClickListener {

            val propertyId: RequestBody = tenantDetails.propertyId
                .toRequestBody("text/plain".toMediaTypeOrNull())
            val emailId: RequestBody = tenantDetails.emailId!!
                .toRequestBody("text/plain".toMediaTypeOrNull())
            val uploadFileType: RequestBody = titleFileType
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

                    val intent = Intent()
                    intent.putExtra("message", titleFileType)
                    setResult(2, intent)
                    finish()

                },
                    { e ->
                        //                                        toast("upload image fail")
                        Log.e("error", e.message.toString())

                    })


//            super.onBackPressed()
        }


    }


    @SuppressLint("NewApi")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            // When an Image is picked
            if (requestCode == REQUEST_GALLERY_CODE && resultCode == Activity.RESULT_OK
                && null != data
            ) {
                // Get the Image from data

                val filePathColumn = arrayOf(MediaStore.MediaColumns.DATA)
                var filePath: String?
                var uri: Uri
                imagesEncodedList = ArrayList()
                if (data.data != null) {

                    uri = data.data!!

                    filePart = prepareFilePart("file", uri)

                    upload_screen_image.setImageURI(uri)

                } else {
                    if (data.clipData != null) {
                        val mClipData = data.clipData

                        for (i in 0 until mClipData!!.itemCount) {

                            val item = mClipData.getItemAt(i)
                            val uri = item.uri
                            mArrayUri.add(uri)
                            val cursor =
                                contentResolver.query(uri, filePathColumn, null, null, null)
                            // Move to first row
                            cursor!!.moveToFirst()

                            filePath = uri.path

                            imagesEncodedList.add(filePath!!)

                            var file = File(filePath)

                            Log.d("FilePath", "filePath=" + filePath)
                            upload_screen_image.setImageURI(uri)

                            filePart = prepareFilePart("file", uri)

                            cursor.close()

                        }
                        Log.v("LOG_TAG", "Selected Images" + mArrayUri.size)
                    }
                }
            }
            else if (requestCode == 2) {
                var uri: Uri? = null

                if (data != null && data.data != null) {
                    uri = data.data!!
                    var file = File(uri.toString())
                    var path: String = uri.path!!
                    filePart = prepareFileParts("files", file, uri)
                }

            }
            else {
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

    companion object {
        var multipartFileImage = ArrayList<MultipartBody.Part>()
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

    @NonNull
    private fun prepareFileParts(
        partName: String,
        file: File,
        fileUri: Uri
    ): MultipartBody.Part {


        var requestFile: RequestBody =
            file
                .asRequestBody(contentResolver.getType(fileUri)?.let { it.toMediaTypeOrNull() })

        return MultipartBody.Part.createFormData("file", file.name, requestFile)

    }




}
