package mp.app.calonex.landlord.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_bookkeeping_agent.*
import mp.app.calonex.R
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.Util
import mp.app.calonex.common.utility.Util.customProgress
import mp.app.calonex.landlord.fragment.FullImageFragment
import mp.app.calonex.landlord.fragment.ProfileLdFragment
import mp.app.calonex.landlord.fragment.ProfileLdFragment.Companion.listUserImages
import java.io.ByteArrayOutputStream
import java.io.IOException

class DocumentDetailScreen : CxBaseActivity2() {
    private val GALLERY = 1
    private val CAMERA = 2
    private var tv_license_number: TextView? = null
    private var layoutLdProfilePic: RelativeLayout? = null
    private var layoutLdFrontLicence: RelativeLayout? = null
    private var layoutLdBackLicence: RelativeLayout? = null
    private var imgLdProfilePic: ImageView? = null
    private var imgLdFrontLicence: ImageView? = null
    private var imgLdBackLicence: ImageView? = null
    private var header_back: ImageView? = null

    private var isImgProfilePic: Boolean? = false
    private var isImgFrontLicence: Boolean? = false
    private var isImgBackLicence: Boolean? = false
    private var bitmapProfilePic: Bitmap? = null
    private var bitmapLicenceFront: Bitmap? = null
    private var bitmapLicenceBack: Bitmap? = null
    private var valLicenceFront: String? = ""
    private var valLicenceBack: String? = ""
    private var imgListFront = ArrayList<String>()
    private var imgListBack = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_detail_screen)

        initComponent()
        actionComponent()
        startHandler()
    }

    fun initComponent() {
        header_back = findViewById(R.id.header_back);
        imgLdProfilePic = findViewById(R.id.img_ownership)
        tv_license_number = findViewById(R.id.tv_license_number)
        imgLdFrontLicence = findViewById(R.id.img_ld_front_licence)
        imgLdBackLicence = findViewById(R.id.img_ld_back_licence)
        layoutLdProfilePic = findViewById(R.id.layout_ownership)
        layoutLdFrontLicence = findViewById(R.id.layout_ld_front_licence)
        layoutLdBackLicence = findViewById(R.id.layout_ld_back_licence)
        val customPb: CircularProgressDrawable = customProgress(applicationContext)


        if (Kotpref.userRole.contains("Landlord", true)) {
            tv_license_number!!.text =
                resources.getString(R.string.driver_licence) + " : " + ProfileLdFragment.userDetailResponse.userDriverLicenseNumber
        } else {
            tv_license_number!!.text =
                resources.getString(R.string.driver_licence) + " : " + ProfileLdFragment.userDetailResponse.userLicenseNumber
        }


        for (item in listUserImages) {

            if (item.uploadFileType.equals(getString(R.string.key_img_lf), true)) {
                valLicenceFront = item.fileName
                imgListFront.add(item.fileName)
            } else if (item.uploadFileType.equals(getString(R.string.key_img_lb))) {
                valLicenceBack = item.fileName
                imgListBack.add(item.fileName)
            }

        }
        if (!valLicenceFront.isNullOrEmpty()) {
            Glide.with(applicationContext)
                .load(getString(R.string.base_url_img2) + valLicenceFront)
                .placeholder(customPb)
                .into(imgLdFrontLicence!!)
            Log.d(
                "TAG",
                "initComponent: url is: " + getString(R.string.base_url_img2) + valLicenceFront
            )
        }
        if (!valLicenceBack.isNullOrEmpty()) {
            Glide.with(applicationContext)
                .load(getString(R.string.base_url_img) + valLicenceBack)
                .placeholder(customPb)
                .into(imgLdBackLicence!!)
        }


    }

    fun actionComponent() {
        header_back?.setOnClickListener {
            super.onBackPressed()
            // Util.navigationBack(this)
            Util.navigationBack(this)

        }


        imgLdFrontLicence!!.setOnClickListener {

            Kotpref.propertyImageIndex = 1
            FullImageFragment(this).customDialog(imgListFront)
        }

        imgLdBackLicence!!.setOnClickListener {
            Kotpref.propertyImageIndex = 0
            FullImageFragment(this).customDialog(imgListBack)
        }
    }

    /* fun actionComponent(){



         imgLdProfilePic!!.setOnClickListener {

             isImgProfilePic=true
             showPictureDialog()
         }

         imgLdFrontLicence!!.setOnClickListener {
             isImgFrontLicence=true
             showPictureDialog()
         }

         imgLdBackLicence!!.setOnClickListener {
             isImgBackLicence=true
             showPictureDialog()
         }

         layoutLdProfilePic!!.setOnClickListener {
             isImgProfilePic=true
             showPictureDialog()
         }

         layoutLdFrontLicence!!.setOnClickListener {
             isImgFrontLicence=true
             showPictureDialog()
         }

         layoutLdBackLicence!!.setOnClickListener {
             isImgBackLicence=true
             showPictureDialog()
         }

     }*/


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


                    if (Build.VERSION.SDK_INT < 28) {
                        val bitmap =
                            MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI!!)
                        displayImage(bitmap)
                    } else {
                        val source = ImageDecoder.createSource(this.contentResolver, contentURI!!)
                        val bitmap = ImageDecoder.decodeBitmap(source)
                        displayImage(bitmap)
                    }

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

    fun displayImage(bitmap: Bitmap) {

        if (isImgProfilePic!!) {
            isImgProfilePic = false
            layoutLdProfilePic!!.visibility = View.GONE
            imgLdProfilePic!!.visibility = View.VISIBLE
            bitmapProfilePic = bitmap
            imgLdProfilePic!!.setImageBitmap(bitmapProfilePic)

        }

        if (isImgFrontLicence!!) {
            isImgFrontLicence = false
            layoutLdFrontLicence!!.visibility = View.GONE
            imgLdFrontLicence!!.visibility = View.VISIBLE
            bitmapLicenceFront = bitmap
            imgLdFrontLicence!!.setImageBitmap(bitmapLicenceFront)


        }

        if (isImgBackLicence!!) {
            isImgBackLicence = false
            layoutLdBackLicence!!.visibility = View.GONE
            imgLdBackLicence!!.visibility = View.VISIBLE

            // SAMIT - 26.05.22
            // Compare images for driver licenses. Do not allow same images
            if (compareBitmaps(bitmapLicenceFront, bitmapLicenceBack)) {
                Toast.makeText(
                    applicationContext,
                    "The front and the back of licence can't be the same",
                    Toast.LENGTH_SHORT
                ).show()
                layoutLdBackLicence!!.visibility = View.VISIBLE
                imgLdBackLicence!!.visibility = View.GONE
                return
            }

            bitmapLicenceBack = bitmap
            imgLdBackLicence!!.setImageBitmap(bitmapLicenceBack)

        }


    }

    // SAMIT - 26.05.22
    // Compare images for licenses
    fun compareBitmaps(bitmap1: Bitmap?, bitmap2: Bitmap?): Boolean {
        if (bitmap1 == null) {
            return false
        }

        if (bitmap2 == null) {
            return false
        }

        // First Image
        val byteArrayOutputStream1 = ByteArrayOutputStream()
        bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream1)
        val byteArray1: ByteArray = byteArrayOutputStream1.toByteArray()
        val encoded1: String = Base64.encodeToString(byteArray1, Base64.DEFAULT)

        // Second Image
        val byteArrayOutputStream2 = ByteArrayOutputStream()
        bitmap2.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream2)
        val byteArray2: ByteArray = byteArrayOutputStream2.toByteArray()
        val encoded2: String = Base64.encodeToString(byteArray2, Base64.DEFAULT)

        if (encoded1 == encoded2) {
            return true
        }

        return false
    }


    override fun onBackPressed() {
        super.onBackPressed()

        Util.navigationBack(this)
    }


}