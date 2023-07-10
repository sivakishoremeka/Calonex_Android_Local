package mp.app.calonex.tenant.activity

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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_add_tenant.*
import mp.app.calonex.R
import mp.app.calonex.common.constant.StatusConstant
import mp.app.calonex.common.utility.UsPhoneNumberFormatter
import mp.app.calonex.common.utility.UsSSNNumberFormatter
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.CxBaseActivity2
import mp.app.calonex.landlord.adapter.ImgListAdapter
import mp.app.calonex.landlord.model.TenantInfoPayload
import mp.app.calonex.tenant.activity.TenantListActivity.Companion.staticTenantList
import mp.app.calonex.tenant.activity.TenantListActivity.Companion.tenantListTemporary
import mp.app.calonex.tenant.activity.TenantListEditActivity.Companion.tenantImagesLicense
import mp.app.calonex.tenant.activity.TenantListEditActivity.Companion.tenantImagesPaystub
import mp.app.calonex.tenant.activity.TenantListEditActivity.Companion.tenantImagesVoidCheck
import mp.app.calonex.tenant.activity.TenantListEditActivity.Companion.tenantImagesW2s
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*
import kotlin.collections.ArrayList

class AddTenantActivity : CxBaseActivity2(), AdapterView.OnItemSelectedListener {

    private val GALLERY = 1
    private val CAMERA = 2


    var mArrayPaystubUri = ArrayList<Uri>()
    var mArrayLicenseUri = ArrayList<Uri>()
    var mArrayw2sUri = ArrayList<Uri>()
    var mArrayVoidCheckUri = ArrayList<Uri>()
    private var tenantSSNNumber:String?=null


    private var adapter: ImgListAdapter? = null

    var payStubClickActive: Boolean = false
    var licenseClickActive: Boolean = false
    var w2sClickActive: Boolean = false
    var voidCheckClickActive: Boolean = false


    var list_of_items =
        arrayOf("Select Tenant Role", "CX-CoTenant", "CX-CoSigner", "CX-CoSigner-CoTenant")
    var tenantAddDetail = TenantInfoPayload()
    var isEdit: Boolean = false
    var tenantDetail = TenantInfoPayload()
    var editTenantposition: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_tenant)

        isEdit = intent.getBooleanExtra("editTenant", false)


        val addSsnFormatter= UsSSNNumberFormatter(add_tenant_ssn!!)
        add_tenant_ssn!!.addTextChangedListener(addSsnFormatter)


        rv_paystubs?.layoutManager = GridLayoutManager(this, 3)

        if (mArrayPaystubUri.size != 0) {
            adapter = ImgListAdapter(this, mArrayPaystubUri, null, null)
            rv_paystubs?.adapter = adapter
        }

        rv_license?.layoutManager = GridLayoutManager(this, 3)

        if (mArrayLicenseUri.size != 0) {
            adapter = ImgListAdapter(this, mArrayLicenseUri, null, null)
            rv_license?.adapter = adapter
        }

        rv_w2s?.layoutManager = GridLayoutManager(this, 3)

        if (mArrayw2sUri.size != 0) {
            adapter = ImgListAdapter(this, mArrayw2sUri, null, null)
            rv_w2s?.adapter = adapter
        }

        rv_voidCheck?.layoutManager = GridLayoutManager(this, 3)

        if (mArrayVoidCheckUri.size != 0) {
            adapter = ImgListAdapter(this, mArrayVoidCheckUri, null, null)
            rv_voidCheck?.adapter = adapter
        }

        val addLineNumberFormatter= UsPhoneNumberFormatter(add_tenant_phone!!)
        add_tenant_phone!!.addTextChangedListener(addLineNumberFormatter)
        actionComponent()

        add_tenant_role_spinner!!.setOnItemSelectedListener(this)
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, list_of_items)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        add_tenant_role_spinner!!.setAdapter(aa)

        if (isEdit) {
            tenantDetail = intent.getSerializableExtra("tenantInfo") as TenantInfoPayload
            editTenantposition = intent.getIntExtra("position", 0)

            if (tenantDetail != null) {
                updateUi(tenantDetail)
                tenantAddDetail = tenantDetail
            }
        }

    }

    private fun actionComponent() {

        upload_void_check_view.setOnClickListener {
            payStubClickActive = false
            licenseClickActive = false
            w2sClickActive = false
            voidCheckClickActive = true
            showPictureDialog()
        }

        add_tenant_upload_licence.setOnClickListener {
            payStubClickActive = false
            licenseClickActive = true
            w2sClickActive = false
            voidCheckClickActive = false
            showPictureDialog()

        }

        add_tenant_upload_w2s.setOnClickListener {
            payStubClickActive = false
            licenseClickActive = false
            w2sClickActive = true
            voidCheckClickActive = false
            showPictureDialog()
        }


        add_tenant_upload_paystab.setOnClickListener {
            payStubClickActive = true
            licenseClickActive = false
            w2sClickActive = false
            voidCheckClickActive = false
            showPictureDialog()

        }



        add_tenant_btn_add!!.setOnClickListener {

            if (add_tenant_first_name.text!!.isEmpty()) {
                add_tenant_first_name?.error = getString(R.string.error_mandetory)
                add_tenant_first_name?.requestFocus()
                return@setOnClickListener
            }

            if (add_tenant_last_name.text!!.isEmpty()) {
                add_tenant_last_name?.error = getString(R.string.error_mandetory)
                add_tenant_last_name?.requestFocus()
                return@setOnClickListener
            }

            tenantSSNNumber = add_tenant_ssn.text.toString().trim()

            if(Util.valueMandetory(applicationContext, tenantSSNNumber, add_tenant_ssn!!)){
                return@setOnClickListener
            }

            if(tenantSSNNumber!!.length!=11){
                add_tenant_ssn?.error  = getString(R.string.error_ssn)
                add_tenant_ssn?.requestFocus()
                return@setOnClickListener
            }

            if (add_tenant_email.text!!.isEmpty()) {
                add_tenant_email?.error = getString(R.string.error_mandetory)
                add_tenant_email?.requestFocus()
                return@setOnClickListener
            }



            if (isEdit) {
                staticTenantList.remove(tenantDetail)
                if (editTenantposition != 0) {
                    tenantListTemporary.removeAt(editTenantposition)
                }
            }

            if (tenantAddDetail.role.isNullOrEmpty()) {
                Toast.makeText(applicationContext, "Please select tenant role", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

           /* if (tenantAddDetail.role.equals("CX-CoTenant",true)){
                if (mArrayVoidCheckUri.size == 0) {
                    Toast.makeText(applicationContext, "Please upload void check", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }*/

            if (tenantAddDetail.role!!.contains("CoSigner",true) || tenantAddDetail.role.equals("CX-CoTenant",true)) {
                if (mArrayw2sUri.size == 0) {
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
                }
            }

            tenantImagesPaystub = mArrayPaystubUri
            tenantImagesLicense = mArrayLicenseUri
            tenantImagesW2s = mArrayw2sUri
            tenantImagesVoidCheck = mArrayVoidCheckUri


            tenantAddDetail.tenantFirstName = add_tenant_first_name.text.toString()
            tenantAddDetail.tenantLastName = add_tenant_last_name.text.toString()
            tenantAddDetail.ssn = add_tenant_ssn.text.toString().filter { it.isDigit() }
            tenantAddDetail.emailId = add_tenant_email.text.toString()
            tenantAddDetail.payDay = TenantListActivity.primaryTpayDay
            tenantAddDetail.leaseSigningStatus = StatusConstant.NOT_STARTED
            tenantAddDetail.landlordApprovalStatus =  StatusConstant.PENDING

            if (tenantAddDetail.role?.equals("CX-CoTenant", true)!!) {
                tenantAddDetail.coTenantFlag = true
                tenantAddDetail.coSignerFlag = false
                tenantAddDetail.phone = add_tenant_phone.text.toString().filter { it.isDigit() }
                tenantAddDetail.address = add_tenant_address.text.toString()
                tenantAddDetail.licenseNo = add_tenant_license.text.toString()
                tenantAddDetail.voidCheckImagesArray = tenantImagesVoidCheck
                tenantAddDetail.w2sImagesArray.removeAll(tenantImagesW2s)
                tenantAddDetail.drivingLicenseImagesArray.removeAll(tenantImagesLicense)
                tenantAddDetail.paystubImagesArray.removeAll(tenantImagesPaystub)
            } else if (tenantAddDetail.role?.equals("CX-CoSigner", true)!!) {
                tenantAddDetail.coSignerFlag = true
                tenantAddDetail.coTenantFlag = false
                tenantAddDetail.phone = add_tenant_phone.text.toString().filter { it.isDigit() }
                tenantAddDetail.address = add_tenant_address.text.toString()
                tenantAddDetail.licenseNo = add_tenant_license.text.toString()

                tenantAddDetail.voidCheckImagesArray = tenantImagesVoidCheck
                tenantAddDetail.w2sImagesArray = tenantImagesW2s
                tenantAddDetail.paystubImagesArray = tenantImagesPaystub
                tenantAddDetail.voidCheckImagesArray.removeAll(tenantImagesVoidCheck)


            } else if (tenantAddDetail.role?.equals("CX-CoSigner-CoTenant", true)!!) {
                tenantAddDetail.coSignerFlag = true
                tenantAddDetail.coTenantFlag = true
                tenantAddDetail.phone = add_tenant_phone.text.toString().filter { it.isDigit() }
                tenantAddDetail.address = add_tenant_address.text.toString()
                tenantAddDetail.licenseNo = add_tenant_license.text.toString()
                tenantAddDetail.voidCheckImagesArray = tenantImagesVoidCheck
                tenantAddDetail.w2sImagesArray = tenantImagesW2s
                tenantAddDetail.paystubImagesArray = tenantImagesPaystub
                tenantAddDetail.voidCheckImagesArray.removeAll(tenantImagesVoidCheck)
            }

            tenantListTemporary.add(tenantAddDetail)

            val intent =
                Intent(this, RentDistributionTenantActivity::class.java)
            startActivity(intent)
            finish()


        }


    }

    private fun updateUi(tenant: TenantInfoPayload) {
        add_tenant_first_name.setText(tenant.tenantFirstName)
        add_tenant_last_name.setText(tenant.tenantLastName)

        if (!tenant.ssn.isNullOrEmpty()) {
            val ssnNumber = StringBuilder(tenant.ssn).insert(5, "-").insert(3, "-")
            add_tenant_ssn.setText(ssnNumber)
        }
        add_tenant_email.setText(tenant.emailId)
        add_tenant_email.isEnabled = false
        if (tenant.role.equals("CX-CoTenant", true)) {
            add_tenant_role_spinner.setSelection(1)
        } else if (tenant.role.equals("CX-CoSigner", true)) {
            add_tenant_role_spinner.setSelection(2)
        } else {
            add_tenant_role_spinner.setSelection(3)
        }

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
        if (position > 0) {
            tenantAddDetail.role = list_of_items[position]


                coSigner_view.visibility = View.VISIBLE
                coTenant_view_upload_void_check.visibility = View.GONE


        }

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
                if (contentURI != null) {

                    if (voidCheckClickActive) {
                        mArrayVoidCheckUri.add(contentURI)
                        val set = HashSet<Uri>(mArrayVoidCheckUri)
                        mArrayVoidCheckUri.clear()
                        mArrayVoidCheckUri.addAll(set)
                        adapter = ImgListAdapter(
                            this,
                            mArrayVoidCheckUri, null, null
                        )

                        rv_voidCheck?.adapter = adapter

                    }

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

        if (voidCheckClickActive) {
            mArrayVoidCheckUri.add(contentURI)
            val set = HashSet<Uri>(mArrayVoidCheckUri)
            mArrayVoidCheckUri.clear()
            mArrayVoidCheckUri.addAll(set)
            adapter = ImgListAdapter(
                this,
                mArrayVoidCheckUri, null, null
            )

            rv_voidCheck?.adapter = adapter
        }


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


}
