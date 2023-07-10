package mp.app.calonex.agentRegistration

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import mp.app.calonex.R
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.Util
import mp.app.calonex.registration.model.DocsPayload
import mp.app.calonex.registration.model.DocumentPayload
import java.io.ByteArrayOutputStream
import java.io.IOException


class UserDocumentScreenAgent : AppCompatActivity() {
    private val GALLERY = 1
    private val CAMERA = 2
    private var layoutOwnerShip: RelativeLayout? = null
    //private var layoutFrontLicence: RelativeLayout? = null
    //private var layoutBackLicence: RelativeLayout? = null
    private var imgOwnerShip: ImageView? = null
    //private var imgFrontLicence: ImageView? = null
    //private var imgBackLicence: ImageView? = null
    private var btnRegDocNext: Button? = null
    private var isLayoutOwnerShip: Boolean? = false
    //private var isLayoutFrontLicence: Boolean? = false
    //private var isLayoutBackLicence: Boolean? = false
    private var isImgOwnerShip: Boolean? = false
    //private var isImgFrontLicence: Boolean? = false
    //private var isImgBackLicence: Boolean? = false
    private var bitmapOwnership: Bitmap? = null
    //private var bitmapLicenceFront: Bitmap? = null
    //private var bitmapLicenceBack: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_document_screen_agent)

        initComponent()
        actionComponent()
    }

    fun initComponent() {
        imgOwnerShip = findViewById(R.id.img_ownership)
        //imgFrontLicence = findViewById(R.id.img_front_licence)
        //imgBackLicence = findViewById(R.id.img_back_licence)
        layoutOwnerShip = findViewById(R.id.layout_ownership)
        //layoutFrontLicence = findViewById(R.id.layout_front_licence)
        //layoutBackLicence = findViewById(R.id.layout_back_licence)
        btnRegDocNext = findViewById(R.id.btn_reg_doc_next)
    }

    override fun onResume() {
        super.onResume()
        if (Kotpref.isRegisterConfirm) {

            layoutOwnerShip!!.visibility = View.GONE
            imgOwnerShip!!.visibility = View.VISIBLE
            imgOwnerShip!!.setImageBitmap(documentPayload.regOwnerShip)
            bitmapOwnership = documentPayload.regOwnerShip
            /*layoutFrontLicence!!.visibility = View.GONE
            imgFrontLicence!!.visibility = View.VISIBLE
            imgFrontLicence!!.setImageBitmap(documentPayload.regLicenceFront)
            bitmapLicenceFront = documentPayload.regLicenceFront
            layoutBackLicence!!.visibility = View.GONE
            imgBackLicence!!.visibility = View.VISIBLE
            imgBackLicence!!.setImageBitmap(documentPayload.regLicenceBack)
            bitmapLicenceBack = documentPayload.regLicenceBack*/
            btnRegDocNext!!.text = getString(R.string.done)
        }
    }

    fun actionComponent() {

        btnRegDocNext!!.setOnClickListener {
            validBitmap()
        }

        imgOwnerShip!!.setOnClickListener {

            isImgOwnerShip = true
            showPictureDialog()
        }

        /*imgFrontLicence!!.setOnClickListener {
            isImgFrontLicence = true
            showPictureDialog()
        }

        imgBackLicence!!.setOnClickListener {
            isImgBackLicence = true
            showPictureDialog()
        }*/

        layoutOwnerShip!!.setOnClickListener {
            isLayoutOwnerShip = true
            showPictureDialog()
        }

       /* layoutFrontLicence!!.setOnClickListener {
            isLayoutFrontLicence = true
            showPictureDialog()
        }

        layoutBackLicence!!.setOnClickListener {
            isLayoutBackLicence = true
            showPictureDialog()
        }*/
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

        if (isLayoutOwnerShip!! || isImgOwnerShip!!) {
            isLayoutOwnerShip = false
            isImgOwnerShip = false
            layoutOwnerShip!!.visibility = View.GONE
            imgOwnerShip!!.visibility = View.VISIBLE
            bitmapOwnership = bitmap
            imgOwnerShip!!.setImageBitmap(bitmapOwnership)

        }

        /*if (isLayoutFrontLicence!! || isImgFrontLicence!!) {
            isLayoutFrontLicence = false
            isImgFrontLicence = false
            layoutFrontLicence!!.visibility = View.GONE
            imgFrontLicence!!.visibility = View.VISIBLE
            bitmapLicenceFront = bitmap
            imgFrontLicence!!.setImageBitmap(bitmapLicenceFront)


        }

        if (isLayoutBackLicence!! || isImgBackLicence!!) {
            isLayoutBackLicence = false
            isImgBackLicence = false
            layoutBackLicence!!.visibility = View.GONE
            imgBackLicence!!.visibility = View.VISIBLE
            bitmapLicenceBack = bitmap

            // SAMIT - 26.05.22
            // Compare images for driver licenses. Do not allow same images
            if (compareBitmaps(bitmapLicenceFront, bitmapLicenceBack)) {
                Toast.makeText(applicationContext,"The front and the back of licence can't be the same", Toast.LENGTH_SHORT).show()
                layoutBackLicence!!.visibility = View.VISIBLE
                imgBackLicence!!.visibility = View.GONE
                return
            }

            imgBackLicence!!.setImageBitmap(bitmapLicenceBack)
            documentPayload.regLicenceBack = bitmapLicenceBack
        }*/
    }

    // SAMIT - 26.05.22
    // Compare images for licenses
    fun compareBitmaps(bitmap1: Bitmap?, bitmap2: Bitmap?) : Boolean {
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

    private fun validBitmap() {
        documentPayloadList.clear()
        if (bitmapOwnership == null) {
            Toast.makeText(
                applicationContext,
                "Please upload the proof of ownership",
                Toast.LENGTH_SHORT
            ).show()
            return
        } else {
            val documentPo = DocsPayload()
            documentPo.imgBitmap = bitmapOwnership
            documentPo.imgKey = "agent_license"
            documentPayloadList.add(documentPo)
            documentPayload.regOwnerShip = bitmapOwnership
        }

        /*if (bitmapLicenceFront == null) {
            Toast.makeText(
                applicationContext,
                "Please upload the front pic of licence",
                Toast.LENGTH_SHORT
            ).show()
            return
        } else {
            val documentLf = DocsPayload()
            documentLf.imgBitmap = bitmapLicenceFront
            documentLf.imgKey = getString(R.string.key_img_lf)
            documentPayloadList.add(documentLf)
            documentPayload.regLicenceFront = bitmapLicenceFront
        }*/

        /*if (bitmapLicenceBack == null) {
            Toast.makeText(
                applicationContext,
                "Please upload the back pic of licence",
                Toast.LENGTH_SHORT
            ).show()
            return
        } else {
            val documentLb = DocsPayload()
            documentLb.imgBitmap = bitmapLicenceBack
            documentLb.imgKey = getString(R.string.key_img_lb)
            documentPayloadList.add(documentLb)
            documentPayload.regLicenceBack = bitmapLicenceBack
        }*/



        if (Kotpref.isRegisterConfirm) {
            onBackPressed()
        } else {

            Util.navigationNext(this, UserConfirmDetailScreenAgent::class.java)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (Kotpref.isRegisterConfirm) {
            Kotpref.isRegisterConfirm = false

        }
        Util.navigationBack(this)
    }

    companion object {
        var documentPayload = DocumentPayload()
        var documentPayloadList = ArrayList<DocsPayload>()
    }

}