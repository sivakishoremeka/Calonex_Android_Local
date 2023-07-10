package mp.app.calonex.landlord.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mp.app.calonex.R
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.AddNewPropertyFirstScreen.Companion.mArrayFileUri
import mp.app.calonex.landlord.adapter.ImgListAdapter
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class UploadDocumentScreen : CxBaseActivity2() {
    private var btn: TextView? = null
    private var done: Button? = null
    internal var PICK_IMAGE_MULTIPLE = 1
    internal var REQUEST_GALLERY_CODE = 200
    internal lateinit var imageEncoded: String
    internal lateinit var imagesEncodedList: MutableList<String>
    private var rvImg: RecyclerView? = null
    private var imgSelect: ImageView? = null
    private var txtTagPic: TextView? = null
    private var adapter: ImgListAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_image_screen)

        btn = findViewById(R.id.btn_upload) as TextView
        done = findViewById(R.id.btn_done) as Button
        txtTagPic = findViewById(R.id.txt_tag_pic) as TextView
        rvImg = findViewById(R.id.rv_photos) as RecyclerView
        imgSelect = findViewById(R.id.img_select) as ImageView
        rvImg?.layoutManager = GridLayoutManager(this, 3)

        startHandler()

       /* if (checkForFilePermission(101)) {
            Util.navigationBack(this)
        }*/

        if (mArrayFileUri != null && mArrayFileUri.size != 0) {
            adapter = ImgListAdapter(this, mArrayFileUri, imgSelect, txtTagPic)
            rvImg?.adapter = adapter
        }



        btn!!.setOnClickListener {
            if (mArrayFileUri.size >= 1) {
                Toast.makeText(this, "Maximum 1 document is allowed.", Toast.LENGTH_LONG).show()
            } else {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, REQUEST_GALLERY_CODE)
            }
        }

        done!!.setOnClickListener {
            super.onBackPressed()

            try {
                for (item in mArrayFileUri) {
                    /*grantUriPermission(getPackageName(), item, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    val path: String? = Util.getRealPathFromUri(this@UploadImageScreen, item)*/
                    val file: File? = Util.getRealPathFromUri(this@UploadDocumentScreen, item)
                    AddNewPropertyFirstScreen.filePropertyOwnershipList.add(file!!)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Util.navigationBack(this)
        }
    }

    private fun askPermission() {


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
                    filePath = getRealPathFromUri(uri)
                    var file = File(filePath)

                    Log.d("Upload Image Screen", "filePath=" + filePath)

                    listOfMultipart.add(prepareFilePart("files", file))

                    val mImageUri = data.data

                    if (mImageUri != null) {
                        mArrayFileUri.add(mImageUri)
                    }
                    val set = HashSet<Uri>(mArrayFileUri)
                    mArrayFileUri.clear()
                    mArrayFileUri.addAll(set)
                    adapter = ImgListAdapter(this, mArrayFileUri, imgSelect, txtTagPic)

                    rvImg?.adapter = adapter
                } else {
                    if (data.clipData != null) {
                        val mClipData = data.clipData

                        for (i in 0 until mClipData!!.itemCount) {

                            val item = mClipData.getItemAt(i)
                            val uri = item.uri
                            mArrayFileUri.add(uri)
                            val cursor =
                                contentResolver.query(uri, filePathColumn, null, null, null)
                            // Move to first row
                            cursor!!.moveToFirst()

                            filePath = uri.path

                            imagesEncodedList.add(filePath!!)

                            var file = File(filePath)

                            Log.d("FilePath", "filePath=" + filePath);

                            listOfMultipart.add(prepareFilePart("files", file))

                            cursor.close()
                            val set = HashSet<Uri>(mArrayFileUri)
                            mArrayFileUri.clear()
                            mArrayFileUri.addAll(set)
                            adapter = ImgListAdapter(this, mArrayFileUri, imgSelect, txtTagPic)

                            rvImg?.adapter = adapter

                        }
                        Log.v("LOG_TAG", "Selected Images" + mArrayFileUri.size)
                    }
                }
                txtTagPic?.visibility = View.GONE
            } else {
                if (mArrayFileUri.isEmpty()) {
                    txtTagPic?.visibility = View.VISIBLE
                }
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
        var listOfMultipart = ArrayList<MultipartBody.Part>()
    }


    @NonNull
    private fun prepareFilePart(
        partName: String,
        file: File
    ): MultipartBody.Part {


        var requestFile: RequestBody =
            file.asRequestBody("image/*".toMediaTypeOrNull())

        var propertyImagePart: MultipartBody.Part? = null
        propertyImagePart = MultipartBody.Part.createFormData("files", file.name, requestFile)
        return propertyImagePart
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
}