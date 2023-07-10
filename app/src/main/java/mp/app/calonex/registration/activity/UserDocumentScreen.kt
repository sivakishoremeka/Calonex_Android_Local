package mp.app.calonex.registration.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_signature_on_screen.*
import kotlinx.android.synthetic.main.activity_user_confirm_detail_screen.*
import mp.app.calonex.R
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.Util
import mp.app.calonex.registration.model.DocsPayload
import mp.app.calonex.registration.model.DocumentPayload
import java.io.IOException


class UserDocumentScreen : AppCompatActivity() {
    private val GALLERY = 1
    private val CAMERA = 2
    private var layoutOwnerShip:RelativeLayout?=null
    //private var layoutFrontLicence:RelativeLayout?=null
    //private var layoutBackLicence:RelativeLayout?=null
    private var imgOwnerShip:ImageView?=null
    //private var imgFrontLicence:ImageView?=null
    //private var imgBackLicence:ImageView?=null
    private var btnRegDocNext:Button?=null
    private var txtPlan:TextView?=null
    private var isLayoutOwnerShip:Boolean?=false
    //private var isLayoutFrontLicence:Boolean?=false
    //private var isLayoutBackLicence:Boolean?=false
    private var isImgOwnerShip:Boolean?=false
    //private var isImgFrontLicence:Boolean?=false
    //private var isImgBackLicence:Boolean?=false
    private var bitmapOwnership:Bitmap?=null
    //private var bitmapLicenceFront:Bitmap?=null
    //private var bitmapLicenceBack:Bitmap?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_document_screen)

        initComponent()
        actionComponent()
    }

    fun initComponent(){
        txtPlan=findViewById(R.id.txt_plan)
        txtPlan!!.text= Kotpref.planSelected
        imgOwnerShip=findViewById(R.id.img_ownership)
        //imgFrontLicence=findViewById(R.id.img_front_licence)
        //imgBackLicence=findViewById(R.id.img_back_licence)
        layoutOwnerShip=findViewById(R.id.layout_ownership)
        //layoutFrontLicence=findViewById(R.id.layout_front_licence)
        //layoutBackLicence=findViewById(R.id.layout_back_licence)
        btnRegDocNext=findViewById(R.id.btn_reg_doc_next)
    }

    override fun onResume() {
        super.onResume()
        if (Kotpref.isRegisterConfirm) {

            layoutOwnerShip!!.visibility=View.GONE
            imgOwnerShip!!.visibility=View.VISIBLE
            imgOwnerShip!!.setImageBitmap(documentPayload.regOwnerShip)
            bitmapOwnership=documentPayload.regOwnerShip
//            layoutFrontLicence!!.visibility=View.GONE
//            imgFrontLicence!!.visibility=View.VISIBLE
//            imgFrontLicence!!.setImageBitmap(documentPayload.regLicenceFront)
//            bitmapLicenceFront=documentPayload.regLicenceFront
//            layoutBackLicence!!.visibility=View.GONE
//            imgBackLicence!!.visibility=View.VISIBLE
//            imgBackLicence!!.setImageBitmap(documentPayload.regLicenceBack)
//            bitmapLicenceBack=documentPayload.regLicenceBack
            btnRegDocNext!!.setText(getString(R.string.done))
        }
    }
    fun actionComponent(){

        btnRegDocNext!!.setOnClickListener {
            validBitmap()

        }

        imgOwnerShip!!.setOnClickListener {

            isImgOwnerShip=true
            showPictureDialog()
        }

        /*imgFrontLicence!!.setOnClickListener {
            isImgFrontLicence=true
            showPictureDialog()
        }

        imgBackLicence!!.setOnClickListener {
            isImgBackLicence=true
            showPictureDialog()
        }*/

        layoutOwnerShip!!.setOnClickListener {
            isLayoutOwnerShip=true
            showPictureDialog()
        }

        /*layoutFrontLicence!!.setOnClickListener {
            isLayoutFrontLicence=true
            showPictureDialog()
        }

        layoutBackLicence!!.setOnClickListener {
            isLayoutBackLicence=true
            showPictureDialog()
        }*/

    }


    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems
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
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    public override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        /* if (resultCode == this.RESULT_CANCELED)
         {
         return
         }*/
        if (requestCode == GALLERY)
        {
            if (data != null)
            {
                val contentURI = data!!.data
                try
                {


                    if(Build.VERSION.SDK_INT < 28) {
                        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,contentURI!!)
                        displayImage(bitmap)
                    } else {
                        val source = ImageDecoder.createSource(this.contentResolver, contentURI!!)
                        val bitmap = ImageDecoder.decodeBitmap(source)
                        displayImage(bitmap)
                    }

                }
                catch (e: IOException) {
                    e.printStackTrace()

                }

            }

        }
        else if (requestCode == CAMERA)
        {
            if (data != null) {
                val thumbnail = data!!.extras!!.get("data") as Bitmap
                displayImage(thumbnail)
            }

        }
    }

    fun displayImage(bitmap: Bitmap){

        if(isLayoutOwnerShip!! || isImgOwnerShip!!){
            isLayoutOwnerShip=false
            isImgOwnerShip=false
            layoutOwnerShip!!.visibility=View.GONE
            imgOwnerShip!!.visibility=View.VISIBLE
            bitmapOwnership=bitmap
            imgOwnerShip!!.setImageBitmap(bitmapOwnership)

        }

        /*if(isLayoutFrontLicence!! || isImgFrontLicence!!){
            isLayoutFrontLicence=false
            isImgFrontLicence=false
            layoutFrontLicence!!.visibility=View.GONE
            imgFrontLicence!!.visibility=View.VISIBLE
            bitmapLicenceFront=bitmap
            imgFrontLicence!!.setImageBitmap(bitmapLicenceFront)


        }

        if(isLayoutBackLicence!! || isImgBackLicence!!){
            isLayoutBackLicence=false
            isImgBackLicence=false
            layoutBackLicence!!.visibility=View.GONE
            imgBackLicence!!.visibility=View.VISIBLE
            bitmapLicenceBack=bitmap
            imgBackLicence!!.setImageBitmap(bitmapLicenceBack)
            documentPayload.regLicenceBack=bitmapLicenceBack

        }*/


    }

    private fun validBitmap(){
        documentPayloadList.clear()
        if(bitmapOwnership==null){
            Toast.makeText(applicationContext,"Please upload the proof of ownership", Toast.LENGTH_SHORT).show()
            return
        }else{
            val documentPo=DocsPayload()
            documentPo.imgBitmap=bitmapOwnership
            documentPo.imgKey=getString(R.string.key_img_po)
            documentPayloadList.add(documentPo)
            documentPayload.regOwnerShip=bitmapOwnership
        }

        /*if(bitmapLicenceFront==null){
            Toast.makeText(applicationContext,"Please upload the front pic of licence", Toast.LENGTH_SHORT).show()
            return
        }else{
            val documentLf=DocsPayload()
            documentLf.imgBitmap=bitmapLicenceFront
            documentLf.imgKey=getString(R.string.key_img_lf)
            documentPayloadList.add(documentLf)
            documentPayload.regLicenceFront=bitmapLicenceFront
        }

        if(bitmapLicenceBack==null){
            Toast.makeText(applicationContext,"Please upload the back pic of licence", Toast.LENGTH_SHORT).show()
            return
        }else{
            val documentLb=DocsPayload()
            documentLb.imgBitmap=bitmapLicenceBack
            documentLb.imgKey=getString(R.string.key_img_lb)
            documentPayloadList.add(documentLb)
            documentPayload.regLicenceBack=bitmapLicenceBack
        }*/



        if(Kotpref.isRegisterConfirm){
            onBackPressed()
        }else {

            Util.navigationNext(this, UserAccountScreen::class.java)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(Kotpref.isRegisterConfirm){
            Kotpref.isRegisterConfirm=false

        }
        Util.navigationBack(this)
    }




    companion object {
        var documentPayload= DocumentPayload()
        var documentPayloadList= ArrayList<DocsPayload>()
    }

}