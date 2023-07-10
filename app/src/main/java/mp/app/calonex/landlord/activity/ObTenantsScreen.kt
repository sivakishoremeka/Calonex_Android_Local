package mp.app.calonex.landlord.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_new_property_first_screen.*
import kotlinx.android.synthetic.main.activity_tenant_verify_update_details_first.*
import kotlinx.android.synthetic.main.layout_update_mobile.view.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.TenantSeachh
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.*
import mp.app.calonex.common.utility.Util.valueMandetory
import mp.app.calonex.landlord.activity.ObLeaseSpecificationScreen.Companion.obLeaseTenantPayload
import mp.app.calonex.landlord.adapter.ImageBitmapAdapter
import mp.app.calonex.landlord.adapter.ObItemTenantAdapter
import mp.app.calonex.landlord.adapter.PropertyListAdapter
import mp.app.calonex.landlord.model.ObTenantPayload
import mp.app.calonex.tenant.response.SeachTenantResponse
import java.io.IOException
import kotlin.math.roundToInt

class ObTenantsScreen : CxBaseActivity2() {

    private val GALLERY = 1
    private val CAMERA = 2
    private var editObTenantEmail: TextInputEditText? = null
    private var editObTenantPhn: TextInputEditText? = null
    private var editObTenantFirstName: TextInputEditText? = null
    private var editObTenantMiddleName: TextInputEditText? = null
    private var editObTenantLastName: TextInputEditText? = null
    private var editObTenantRentPercentage: TextInputEditText? = null
    private var editObTenantAddress: TextInputEditText? = null
    private var editObTenantDrivingLicence: TextInputEditText? = null
    private var editObTenantSsn: TextInputEditText? = null
    private var rvObTenant: RecyclerView? = null
    private var layoutListTenant: LinearLayout? = null
    private var layoutObTenantForm: LinearLayout? = null
    private var btnObAddTenant: Button? = null
    private var btnObTenantSave: Button? = null
    private var txtObTenantSkip: TextView? = null
    private var txtFormTitle: TextView? = null
    private var layoutObCo: LinearLayout? = null
    private var txtObCoSigner: TextView? = null
    private var txtObCoTenant: TextView? = null
    private var txtObPaystub: TextView? = null
    private var txtObW2s: TextView? = null
    private var txtObLicence: TextView? = null
    private var rvObPaystub: RecyclerView? = null
    private var rvObW2s: RecyclerView? = null
    private var rvObLicence: RecyclerView? = null
    private var isPaystubClick: Boolean = false
    private var isW2sClick: Boolean = false
    private var isLicence: Boolean = false
    private var txtObProperty: TextView? = null
    private var txtObUnit: TextView? = null
    private var tenantArrayList = ArrayList<ObTenantPayload>()
    private var obTenantEmail: String? = null
    private var obTenantPhn: String? = null
    private var obTenantFirstName: String? = null
    private var obTenantMiddleName: String? = null
    private var obTenantLastName: String? = null
    private var obTenantRentPercentage: String? = null
    private var obTenantAddress: String? = null
    private var obTenantDrivingLicence: String? = null
    private var obTenantSsn: String? = null
    private var isObCoSigner: Boolean? = false
    private var isObCoTenant: Boolean? = false
    private var imagePaystubBitmapAdapter: ImageBitmapAdapter? = null
    private var imageW2sBitmapAdapter: ImageBitmapAdapter? = null
    private var imageLicenceBitmapAdapter: ImageBitmapAdapter? = null

    private var obItemTenantAdapter: ObItemTenantAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ob_tenants_screen)

        initComponent()
        actionComponent()
        startHandler()
    }

    fun initComponent() {
        txtObProperty = findViewById(R.id.txt_ob_property_add)
        txtObUnit = findViewById(R.id.txt_ob_unit)
        layoutListTenant = findViewById(R.id.layout_list_tenant)
        layoutObTenantForm = findViewById(R.id.layout_ob_tenant_form)
        layoutObCo = findViewById(R.id.layout_ob_co)
        rvObTenant = findViewById(R.id.rv_ob_tenant)
        btnObAddTenant = findViewById(R.id.btn_ob_add_tenant)
        txtObCoSigner = findViewById(R.id.txt_ob_co_signer)
        txtObCoTenant = findViewById(R.id.txt_ob_co_tenant)
        txtFormTitle = findViewById(R.id.txt_form_title)
        editObTenantEmail = findViewById(R.id.edit_ob_tenant_email)
        editObTenantPhn = findViewById(R.id.edit_ob_tenant_phn)
        editObTenantFirstName = findViewById(R.id.edit_ob_tenant_first_name)
        editObTenantMiddleName = findViewById(R.id.edit_ob_tenant_middle_name)
        editObTenantLastName = findViewById(R.id.edit_ob_tenant_last_name)
        editObTenantRentPercentage = findViewById(R.id.edit_ob_tenant_rent_percentage)
        editObTenantAddress = findViewById(R.id.edit_ob_tenant_address)
        editObTenantDrivingLicence = findViewById(R.id.edit_ob_tenant_driving_licence)
        editObTenantSsn = findViewById(R.id.edit_ob_tenant_ssn)
        btnObTenantSave = findViewById(R.id.btn_ob_tenant_save)
        txtObTenantSkip = findViewById(R.id.txt_ob_tenant_skip)
        txtObPaystub = findViewById(R.id.txt_ob_paystub)
        txtObW2s = findViewById(R.id.txt_ob_w2s)
        txtObLicence = findViewById(R.id.txt_ob_licence)
        rvObPaystub = findViewById(R.id.rv_ob_paystub)
        rvObW2s = findViewById(R.id.rv_ob_w2s)
        rvObLicence = findViewById(R.id.rv_ob_licence)
        rvObPaystub?.layoutManager = GridLayoutManager(this, 3)
        rvObW2s?.layoutManager = GridLayoutManager(this, 3)
        rvObLicence?.layoutManager = GridLayoutManager(this, 3)
        imagePaystubBitmapAdapter = ImageBitmapAdapter(this, listPaystub)
        rvObPaystub!!.adapter = imagePaystubBitmapAdapter
        imageW2sBitmapAdapter = ImageBitmapAdapter(this, listW2s)
        rvObW2s!!.adapter = imageW2sBitmapAdapter
        imageLicenceBitmapAdapter = ImageBitmapAdapter(this, listLicence)
        rvObLicence!!.adapter = imageLicenceBitmapAdapter
        listPaystub.clear()
        listW2s.clear()
        listLicence.clear()


        //   val addLineNumberFormatter= UsPhoneNumberFormatter(editObTenantPhn!!)
        //   editObTenantPhn!!.addTextChangedListener(addLineNumberFormatter)
        val addSsnFormatter = UsSSNNumberFormatter(editObTenantSsn!!)
        editObTenantSsn!!.addTextChangedListener(addSsnFormatter)
        txtObProperty!!.text =
            ", ${PropertyListAdapter.propertyDetailResponse.address}, ${PropertyListAdapter.propertyDetailResponse.city}, ${PropertyListAdapter.propertyDetailResponse.state}, ${PropertyListAdapter.propertyDetailResponse.zipCode}"
        txtObUnit!!.text = "Unit : ${obLeaseTenantPayload.unitNumber}"
        rvObTenant?.layoutManager = LinearLayoutManager(applicationContext)
        if (obLeaseTenantPayload.renewLease!!) {
            tenantArrayList = obLeaseTenantPayload.tenantBaseInfoDto
            for (item in tenantArrayList) {
                item.renewLease = true
                item.leaseId = ""
                item.tenantId = ""
                item.oldLeaseId = obLeaseTenantPayload.oldLeaseId
                item.rentAmount = ((item.rentPercentage!!.toFloat()
                    .roundToInt() * obLeaseTenantPayload.rentAmount!!.toFloat()
                    .roundToInt()) / 100).toString()
                item.securityAmount = ((item.rentPercentage!!.toFloat()
                    .roundToInt() * obLeaseTenantPayload.securityAmount!!.toFloat()
                    .roundToInt()) / 100).toString()
                if (!obLeaseTenantPayload.amenities!!.isNullOrEmpty()) {
                    item.amenities = ((item.rentPercentage!!.toFloat()
                        .roundToInt() * obLeaseTenantPayload.amenities!!.toFloat()
                        .roundToInt()) / 100).toString()

                }
            }
        }
        obItemTenantAdapter = ObItemTenantAdapter(applicationContext, tenantArrayList)
        rvObTenant?.adapter = obItemTenantAdapter
        if (tenantArrayList.size == 0) {
            layoutListTenant!!.visibility = View.GONE
            txtObTenantSkip!!.visibility = View.GONE
            layoutObCo!!.visibility = View.GONE
            layoutObTenantForm!!.visibility = View.VISIBLE
        } else {
            layoutListTenant!!.visibility = View.VISIBLE
            txtObTenantSkip!!.visibility = View.VISIBLE
            layoutObCo!!.visibility = View.GONE
            btnObTenantSave!!.visibility = View.GONE
            layoutObTenantForm!!.visibility = View.GONE
        }

    }

    fun actionComponent() {
        editObTenantEmail?.setOnFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange(view: View, hasFocus: Boolean) {
                if (!hasFocus) {
                    val credential = TenantSeachh()

                    credential.emailId = editObTenantEmail?.text.toString().trim()
                    credential.phoneNumber = editObTenantPhn?.text.toString().toString()
                    credential.userCatalogId = Kotpref.userId

                    credential.userCatalogId = Kotpref.userId

                    if (credential.emailId.equals("")) {
                        Toast.makeText(
                            applicationContext,
                            "Please enter valid E-mail id",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    val brokerAgentDetails: ApiInterface =
                        ApiClient(this@ObTenantsScreen).provideService(ApiInterface::class.java)
                    val apiCall: Observable<SeachTenantResponse> =
                        brokerAgentDetails.searchTenant(credential) //Test API Key

                    apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            {
                                Log.e("onSuccess", it.statusCode.toString())


                                if (it.data != null && it.responseDto!!.responseCode == 200) {
                                    // Existing Tenant
                                    editObTenantFirstName?.setText(it.data!!.firstName)
                                    editObTenantLastName?.setText(it.data!!.lastName)
                                    editObTenantMiddleName?.setText(it.data!!.middleName)
                                    editObTenantPhn?.setText(it.data!!.phoneNumber)

                                    //Set 100% rent amount by default for Primary Tenant
                                    editObTenantRentPercentage?.setText("100")


                                    /*TODO GANRAM*/

                                } else if (it.responseDto != null && it.responseDto!!.responseCode == 404) {
                                    Toast.makeText(
                                        applicationContext,
                                        "Invalid Tenant Mail",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    broker_view.visibility = View.GONE

                                }
                            },
                            { e ->
                                //  broker_view.visibility = View.GONE
                                Log.e("onFailure", e.toString())
                                //  pb_edit.visibility = View.GONE
                                e.message?.let {
                                    Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT)
                                        .show()

                                }
                            })

                }
            }
        })



        editObTenantEmail?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

            }


        })


        btnObTenantSave!!.setOnClickListener { view ->
            // Hide the keyboard
            val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            validComponent()

        }

        btnObAddTenant!!.setOnClickListener {
            layoutObTenantForm!!.visibility = View.VISIBLE
            layoutObCo!!.visibility = View.VISIBLE
            txtFormTitle!!.text = getString(R.string.tenants)
            btnObAddTenant!!.visibility = View.GONE
            btnObTenantSave!!.visibility = View.VISIBLE
            listPaystub.clear()
            listW2s.clear()
            listLicence.clear()
            resetComponent()

        }

        txtObCoSigner!!.setOnClickListener {
            if (!isObCoSigner!!) {
                isObCoSigner = true
                txtObCoSigner!!.setBackgroundResource(R.drawable.btn_dk_blue_round)
                txtObCoSigner!!.setTextColor(Color.WHITE)

            } else {
                isObCoSigner = true
                txtObCoSigner!!.setBackgroundResource(R.drawable.btn_dk_blue_round)
                txtObCoSigner!!.setTextColor(Color.WHITE)
                /*isObCoSigner = false
                txtObCoSigner!!.setBackgroundResource(R.drawable.btn_lt_blue_grey_round)
                txtObCoSigner!!.setTextColor(Color.BLACK)*/
            }

        }

        txtObCoTenant!!.setOnClickListener {

            if (!isObCoTenant!!) {
                isObCoTenant = true
                txtObCoTenant!!.setBackgroundResource(R.drawable.btn_dk_blue_round)
                txtObCoTenant!!.setTextColor(Color.WHITE)

            } else {
                isObCoTenant = false
                txtObCoTenant!!.setBackgroundResource(R.drawable.btn_lt_blue_grey_round)
                txtObCoTenant!!.setTextColor(Color.BLACK)
            }

        }

        txtObTenantSkip!!.setOnClickListener {
            var totalPercentage = 0
            if (tenantArrayList.size>0)
            {
            for (i in 0 until tenantArrayList.size) {
                totalPercentage += tenantArrayList[i].rentPercentage!!.toInt()
            }
            }

            if (totalPercentage == 100) {
                obLeaseTenantPayload.tenantBaseInfoDto = tenantArrayList
                //Util.navigationNext(this, ObRentDistributionScreen::class.java)
                obLeaseTenantPayload.tenantBaseInfoDto[0].rentAmount =
                    obLeaseTenantPayload.rentAmount
                obLeaseTenantPayload.tenantBaseInfoDto[0].securityAmount =
                    obLeaseTenantPayload.securityAmount
                obLeaseTenantPayload.tenantBaseInfoDto[0].amenities = obLeaseTenantPayload.amenities
                obLeaseTenantPayload.tenantBaseInfoDto[0].rentPercentage =
                    tenantArrayList[0].rentPercentage

                Util.navigationNext(this, ObLeaseConfirmScreen::class.java)
            } else {
                Toast.makeText(
                    this,
                    "The sum of rent percentage should be equal 100",
                    Toast.LENGTH_LONG
                )
                    .show()
            }

        }

        txtObPaystub!!.setOnClickListener {
            isPaystubClick = true
            showPictureDialog()
        }

        txtObW2s!!.setOnClickListener {
            isW2sClick = true
            showPictureDialog()
        }

        txtObLicence!!.setOnClickListener {
            isLicence = true
            showPictureDialog()
        }


    }

    fun validComponent() {

        obTenantEmail = editObTenantEmail!!.text.toString().trim()
        obTenantPhn = editObTenantPhn!!.text.toString().trim()
        obTenantFirstName = editObTenantFirstName!!.text.toString().trim()
        obTenantMiddleName = editObTenantMiddleName!!.text.toString().trim()
        obTenantLastName = editObTenantLastName!!.text.toString().trim()
        obTenantRentPercentage = editObTenantRentPercentage!!.text.toString().trim()
        obTenantAddress = editObTenantAddress!!.text.toString().trim()
        obTenantDrivingLicence = RSA.encrypt(editObTenantDrivingLicence!!.text.toString().trim())
        obTenantSsn = editObTenantSsn!!.text.toString().trim()


        if (valueMandetory(applicationContext, obTenantEmail, editObTenantEmail!!)) {
            return
        }
        if (valueMandetory(applicationContext,obTenantRentPercentage, editObTenantRentPercentage!!))
        {
            return
        }

        /* if (valueMandetory(applicationContext, obTenantPhn, editObTenantPhn!!)) {
             return
         }*/
        /*if (obTenantPhn!!.length != 10) {
            editObTenantPhn?.error = getString(R.string.error_phn)
            editObTenantPhn?.requestFocus()
            return
        }*/


/*
        if (tenantArrayList.size > 0) {
            for (items in tenantArrayList) {
                if (items.equals(obTenantEmail)) {
                    editObTenantEmail?.error="Tenant already added"
                    editObTenantDrivingLicence?.requestFocus()
                    return
                }
            }


        }
*/
        /* if (obTenantSsn!!.length>1 && obTenantSsn!!.length != 11) {
             editObTenantSsn?.error = getString(R.string.error_ssn)
             editObTenantSsn?.requestFocus()
             return
         }*/

        val obTenantPayload = ObTenantPayload()
        obTenantPayload.emailId = obTenantEmail
        obTenantPayload.phone = obTenantPhn!!.filter { it.isDigit() }
        obTenantPayload.tenantFirstName = obTenantFirstName
        obTenantPayload.tenantMiddleName = obTenantMiddleName
        obTenantPayload.tenantLastName = obTenantLastName
        obTenantPayload.address = obTenantAddress
        obTenantPayload.licenseNo = obTenantDrivingLicence
        obTenantPayload.ssn = obTenantSsn!!.filter { it.isDigit() }
        obTenantPayload.coSignerFlag = isObCoSigner
        obTenantPayload.coTenantFlag = isObCoTenant
        //obTenantPayload.payDay= Kotpref.payDay

        obTenantPayload.rentAmount = obLeaseTenantPayload.rentAmount
        obTenantPayload.securityAmount = obLeaseTenantPayload.securityAmount


        if (tenantArrayList.size == 0) {
            obTenantPayload.role = "CX-PrimaryTenant"
            obTenantPayload.coSignerFlag = true
            obTenantPayload.coTenantFlag = true
            obTenantPayload.rentPercentage = obTenantRentPercentage
        } else {
            obTenantPayload.rentPercentage = obTenantRentPercentage

            if (isObCoSigner!! && isObCoTenant!!) {
                obTenantPayload.role = "CX-CoSigner-CoTenant"
            } else {
                when {
                    isObCoSigner!! -> {
                        obTenantPayload.role = "CX-CoSigner"
                    }
                    isObCoTenant!! -> {
                        obTenantPayload.role = "CX-CoTenant"
                    }
                    else -> {
                        Toast.makeText(
                            applicationContext,
                            "Please select the role for the added Tenant",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }
                }
            }
        }

        obTenantPayload.bitmapListPaystub.addAll(listPaystub)
        obTenantPayload.bitmapListW2s.addAll(listW2s)
        obTenantPayload.bitmapListLicence.addAll(listLicence)

        //Validate Rent Share Percentage
        try {
            var rentPercentage = obTenantPayload.rentPercentage!!.toInt()
            try {
                Log.e("List", "Current %==>$rentPercentage")
                if (editObTenantRentPercentage!!.text.toString().trim().isNotEmpty()) {
                    if (tenantArrayList.size > 0) {
                        //rentPercentage = 0
                        for (i in 0 until tenantArrayList.size) {
                            rentPercentage += tenantArrayList[i].rentPercentage!!.toInt()
                            Log.e(
                                "Rent Share",
                                "Percentage Sum Value $i =${tenantArrayList[i].emailId}==${tenantArrayList[i].rentPercentage}> " + rentPercentage
                            )
                        }
                    } else {
                        rentPercentage = obTenantRentPercentage!!.toInt()
                    }
                } else {
                    Toast.makeText(
                        this,
                        "The rent percentage should be less or equal to 100",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    return
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            Log.e("Check Rent", "rentPercentage>>>$rentPercentage")
            if (rentPercentage > 100) {
                Toast.makeText(
                    this,
                    "The rent percentage should be less or equal to 100",
                    Toast.LENGTH_SHORT
                )
                    .show()
                return
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }



        tenantArrayList.add(obTenantPayload)

        btnObAddTenant!!.visibility = View.VISIBLE
        btnObTenantSave!!.visibility = View.GONE
        if (tenantArrayList.size > 0) {
            layoutListTenant!!.visibility = View.VISIBLE
            txtObTenantSkip!!.visibility = View.VISIBLE
            layoutObTenantForm!!.visibility = View.GONE
            layoutObCo!!.visibility = View.GONE
            obItemTenantAdapter!!.notifyDataSetChanged()
            //obItemTenantAdapter!!.setData(tenantArrayList)
        }

    }

    private fun resetComponent() {
        editObTenantEmail!!.setText("")
        editObTenantPhn!!.setText("")
        editObTenantFirstName!!.setText("")
        editObTenantMiddleName!!.setText("")
        editObTenantLastName!!.setText("")
        editObTenantAddress!!.setText("")
        editObTenantDrivingLicence!!.setText("")
        editObTenantSsn!!.setText("")
        isObCoSigner = true
        isObCoTenant = false
        txtObCoTenant!!.setBackgroundResource(R.drawable.btn_dk_blue_round)
        txtObCoTenant!!.setTextColor(Color.WHITE)
        txtObCoSigner!!.setBackgroundResource(R.drawable.btn_dk_blue_round)
        txtObCoSigner!!.setTextColor(Color.WHITE)
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
                val contentURI = data!!.data
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
                val thumbnail = data!!.extras!!.get("data") as Bitmap
                displayImage(thumbnail)
            }

        }
    }

    fun displayImage(bitmap: Bitmap) {
        if (isPaystubClick) {
            isPaystubClick = false
            listPaystub.add(bitmap)
            imagePaystubBitmapAdapter = ImageBitmapAdapter(this, listPaystub)
            rvObPaystub!!.adapter = imagePaystubBitmapAdapter
        }
        if (isW2sClick) {
            isW2sClick = false
            listW2s.add(bitmap)
            imageW2sBitmapAdapter = ImageBitmapAdapter(this, listW2s)
            rvObW2s!!.adapter = imageW2sBitmapAdapter
        }
        if (isLicence) {
            isLicence = false
            listLicence.add(bitmap)
            imageLicenceBitmapAdapter = ImageBitmapAdapter(this, listLicence)
            rvObLicence!!.adapter = imageLicenceBitmapAdapter
        }

    }


    override fun onBackPressed() {
        super.onBackPressed()
        Util.navigationBack(this)
    }

    companion object {
        var listPaystub = ArrayList<Bitmap>()
        var listW2s = ArrayList<Bitmap>()
        var listLicence = ArrayList<Bitmap>()


    }
}