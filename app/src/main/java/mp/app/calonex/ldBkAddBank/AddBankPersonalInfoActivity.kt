package mp.app.calonex.ldBkAddBank

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.telephony.PhoneNumberUtils
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputEditText
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_account_detail_screen.*
import kotlinx.android.synthetic.main.activity_add_bank_personal_info_for_stripe.*
import kotlinx.android.synthetic.main.activity_add_bank_personal_info_for_stripe.header_back
import mp.app.calonex.R
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.*
import mp.app.calonex.landlord.activity.CxBaseActivity2
import mp.app.calonex.landlord.activity.LoginScreen.Companion.registrationPayload3
import mp.app.calonex.landlord.adapter.CustomSpinnerAdapter
import mp.app.calonex.landlord.model.ZipCityStateModel
import mp.app.calonex.tenant.response.ResponseDtoResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class AddBankPersonalInfoActivity : CxBaseActivity2() {
    private var piissn:String=""
    private  var isFrontUploaded:Boolean?=false
    private var isBackUploaded:Boolean?=false
    private var editRegDob: TextInputEditText? = null
    private var editRegAddress: TextInputEditText? = null
    private var editRegCity: TextInputEditText? = null
    private var editRegState: TextInputEditText? = null
    private var editRegZipCode: TextInputEditText? = null
    private var editRegPhnNmbr: TextInputEditText? = null
    private var editRegAltrnteNmbr: TextInputEditText? = null
    private var btnRegNxt: TextView? = null
    private var regAddress: String? = null
    private var regCity: String? = null
    private var regState: String? = null
    private var regZipcode: String? = null
    private var regPhnNmbr: String? = ""
    private var regAltrntNmbr: String? = null
    private var regFirstName: String? = null
    private var regLastName: String? = null
    private var regAddressLine2: String? = null
    private var regSecurityNumber: String? = null
    private var stateIssueList = ArrayList<String>()
    private var zcsList = ArrayList<ZipCityStateModel>()

    private var regDob: String? = null
    private var cal = Calendar.getInstance()
    private var year = cal.get(Calendar.YEAR)
    private var month = cal.get(Calendar.MONTH)
    private var day = cal.get(Calendar.DAY_OF_MONTH)
    val myFormat = "MMM dd, yyyy"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_bank_personal_info_for_stripe)

        initComponent()
        actionComponent()
        startHandler()
    }

    fun initComponent() {
        editRegAddress = findViewById(R.id.edit_reg_address)
        editRegCity = findViewById(R.id.edit_reg_city)
        editRegState = findViewById(R.id.edit_reg_state)
        editRegZipCode = findViewById(R.id.edit_reg_zip)
        editRegPhnNmbr = findViewById(R.id.edit_reg_phn)
        editRegAltrnteNmbr = findViewById(R.id.edit_reg_alt_phn)
        btnRegNxt = findViewById(R.id.btn_reg_next)

        editRegDob = findViewById(R.id.edit_reg_dob)
        editRegDob?.setOnFocusChangeListener(OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                val dateDialog = DatePickerDialog(
                    this,
                    { view, year, monthOfYear, dayOfMonth ->
                        cal.set(Calendar.YEAR, year)
                        cal.set(Calendar.MONTH, monthOfYear)
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                        updateDateInView()
                    },
                    year,
                    month,
                    day
                )
                dateDialog.show()
                var cal1 = Calendar.getInstance()
                Log.e("Time==", Calendar.MONTH.toString())
                cal1.add(Calendar.YEAR, -18)
                val minDate: Long = cal1.getTime().getTime();
                dateDialog.getDatePicker().setMaxDate(minDate)
                editRegDob!!.setError(null)
            } else {

            }
        })


        zcsList = Util.getZipCityStateList(applicationContext)
        val addLineNumberFormatter = UsPhoneNumberFormatter(editRegPhnNmbr!!)
        editRegPhnNmbr!!.addTextChangedListener(addLineNumberFormatter)
        val addLineNumberFormatterAlter = UsPhoneNumberFormatter(editRegAltrnteNmbr!!)
        editRegAltrnteNmbr!!.addTextChangedListener(addLineNumberFormatterAlter)


        var listState = mutableListOf<String>()

        stateIssueList.add("State Issued")
        for (item in zcsList) {
            listState.add(item.state!!)
        }
        stateIssueList.addAll(ArrayList<String>(listState.toSet().sorted().toList()))
        val spinnerStateIssueAdapter = CustomSpinnerAdapter(applicationContext, stateIssueList)
        System.out.println("state list ==> $stateIssueList")

        Util.setEditReadOnly(editRegDob!!, true, InputType.TYPE_NULL)
        Util.setEditReadOnly(editRegCity!!, false, InputType.TYPE_NULL)
        Util.setEditReadOnly(editRegState!!, false, InputType.TYPE_NULL)

        add_front_img.setOnClickListener {
            showPictureDialog("front")
        }

        add_back_img.setOnClickListener {
            showPictureDialog("back")
        }

        header_back.setOnClickListener {
            super.onBackPressed()
            // Util.navigationBack(this)
            Util.navigationBack(this)
        }


    }


    private val GALLERY = 1
    private val GALLERY_BACK = 3
    private val CAMERA = 2
    private val CAMERA_BACK = 4
    private var fileFront: File? = null
    private var fileBack: File? = null
    private fun showPictureDialog(type: String) {
        val pictureDialog = AlertDialog.Builder(this@AddBankPersonalInfoActivity)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(
            pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary(type)
                1 -> takePhotoFromCamera(type)
            }
        }
        pictureDialog.show()
    }

    fun choosePhotoFromGallary(type: String) {
        when (type) {
            "front" -> {
                val galleryIntent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )

                startActivityForResult(galleryIntent, GALLERY)
            }
            "back" -> {
                val galleryIntent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )

                startActivityForResult(galleryIntent, GALLERY_BACK)
            }
        }
    }

    private fun takePhotoFromCamera(type: String) {
        when (type) {
            "front" -> {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, CAMERA)
            }
            "back" -> {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, CAMERA_BACK)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_CANCELED) {
            return
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                val contentURI = data.data
                fileFront =
                    Util.getRealPathFromUri(this@AddBankPersonalInfoActivity, contentURI!!)
                profileUploadApi("front")
                img_front_licence!!.visibility = View.VISIBLE
                img_front_licence!!.setImageBitmap(BitmapFactory.decodeFile(fileFront!!.absolutePath))
            }
        } else if (requestCode == CAMERA) {
            if (data != null) {
                val thumbnail = data.extras!!.get("data") as Bitmap
                fileFront =
                    Util.bitmapToFile(
                        this@AddBankPersonalInfoActivity,
                        thumbnail,
                        getString(R.string.profile_pic)
                    )
                img_front_licence!!.visibility = View.VISIBLE

                img_front_licence!!.setImageBitmap(BitmapFactory.decodeFile(fileFront!!.absolutePath))
                profileUploadApi("front")
            }
        } else if (requestCode == CAMERA_BACK) {
            if (data != null) {
                val thumbnail = data.extras!!.get("data") as Bitmap
                fileBack =
                    Util.bitmapToFile(
                        this@AddBankPersonalInfoActivity,
                        thumbnail,
                        getString(R.string.profile_pic)
                    )
                img_back_licence!!.visibility = View.VISIBLE

                img_back_licence!!.setImageBitmap(BitmapFactory.decodeFile(fileBack!!.absolutePath))
                profileUploadApi("back")
            }
        } else if (requestCode == GALLERY_BACK) {
            if (data != null) {
                val contentURI = data.data
                fileBack =
                    Util.getRealPathFromUri(this@AddBankPersonalInfoActivity, contentURI!!)
                profileUploadApi("back")
                img_back_licence!!.visibility = View.VISIBLE
                img_back_licence!!.setImageBitmap(BitmapFactory.decodeFile(fileBack!!.absolutePath))
            }
        }
    }


    private fun profileUploadApi(type: String) {
        if (NetworkConnection.isNetworkConnected(this@AddBankPersonalInfoActivity)) {
            //pbProfile!!.visibility = View.VISIBLE
            this@AddBankPersonalInfoActivity.window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
            builder.addFormDataPart("userId", Kotpref.userId)

            when (type) {
                "front" -> {
                    builder.addFormDataPart(
                        "uploadFileType",
                        this@AddBankPersonalInfoActivity.getString(R.string.key_front_license)
                    )

                    builder.addFormDataPart(
                        "file", fileFront!!.name,
                        fileFront!!.asRequestBody(MultipartBody.FORM)
                    )
                }
                "back" -> {
                    builder.addFormDataPart(
                        "uploadFileType",
                        this@AddBankPersonalInfoActivity.getString(R.string.key_back_license)
                    )

                    builder.addFormDataPart(
                        "file", fileBack!!.name,
                        fileBack!!.asRequestBody(MultipartBody.FORM)
                    )
                }
            }

            val requestBody: RequestBody = builder.build()
            val uploadSign: ApiInterface =
                ApiClient(this@AddBankPersonalInfoActivity).provideService(ApiInterface::class.java)

            val apiCallUploadSign: Observable<ResponseDtoResponse> =
                uploadSign.uploadDocument(requestBody)

            apiCallUploadSign.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({

                    if (it.responseDto?.responseCode!!.equals(200)) {
                        if (type.equals("front")){
                            isFrontUploaded=true
                        }else{
                            isBackUploaded=true
                        }

                        // pbProfile!!.visibility = View.GONE
                        this@AddBankPersonalInfoActivity.window
                            .clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        pb_ut_confirm!!.visibility = View.GONE


                    }
                },
                    { e ->
                        this@AddBankPersonalInfoActivity.window
                            .clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        //pbProfile!!.visibility = View.GONE

                        Log.e("error", e.message.toString())
                    })
        } else {
            Toast.makeText(
                this@AddBankPersonalInfoActivity,
                getString(R.string.error_network),
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    override fun onResume() {
        super.onResume()
        if (Kotpref.isRegisterConfirm) {
            editRegDob!!.setText(Kotpref.dateRegistration)
            regDob = registrationPayload3.dob
            editRegAddress!!.setText(registrationPayload3.address1)
            editRegCity!!.setText(registrationPayload3.city)
            editRegState!!.setText(registrationPayload3.state)
            editRegZipCode!!.setText(registrationPayload3.zipCode)
            editRegPhnNmbr!!.setText(
                PhoneNumberUtils.formatNumber(
                    registrationPayload3.phoneNumber,
                    "US"
                )
            )
            editRegAltrnteNmbr!!.setText(registrationPayload3.alternatePhoneNumber)

            btnRegNxt!!.setText(getString(R.string.next))

        }
    }

    fun actionComponent() {

        editRegZipCode?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

                if (editRegZipCode!!.text.toString().length > 0) {
                    setCityState(editRegZipCode!!.text.toString().toInt())
                }
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {

            }
        })

        editRegDob?.setOnClickListener {

            val dateDialog = DatePickerDialog(
                this,
                { view, year, monthOfYear, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    updateDateInView()
                },
                year,
                month,
                day
            )
            dateDialog.show()
            var cal1 = Calendar.getInstance()
            Log.e("Time==", Calendar.MONTH.toString())
            cal1.add(Calendar.YEAR, -18)
            val minDate: Long = cal1.getTime().getTime();
            dateDialog.getDatePicker().setMaxDate(minDate)
            editRegDob!!.setError(null)
        }

        btnRegNxt!!.setOnClickListener { view ->
            // Hide the keyboard
            val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            validComponent()
        }
    }

    @SuppressLint("NewApi")
    private fun updateDateInView() {

        val sdf = SimpleDateFormat(myFormat, Locale.US)
        val sdf1 = SimpleDateFormat("YYYY-MM-dd", Locale.US)
        editRegDob!!.setText(sdf.format(cal.getTime()))
        Kotpref.dateRegistration = editRegDob!!.getText().toString()
        regDob = sdf1.format(cal.getTime())
    }

    private fun setCityState(valueZip: Int?) {
        val filterList: List<ZipCityStateModel>? = zcsList.filter { it.zip == valueZip }
        if (filterList!!.size > 0) {
            val filterPlan: ZipCityStateModel? = filterList!!.get(0)
            editRegCity!!.setText(filterPlan!!.city)
            editRegState!!.setText(filterPlan!!.state)
        } else {
            editRegZipCode?.error = getString(R.string.error_zipcode)
            editRegZipCode?.requestFocus()
            editRegCity!!.setText("")
            editRegState!!.setText("")
            return
        }
    }


    fun validComponent() {
        regAddress = editRegAddress!!.text.toString().trim()
        regCity = editRegCity!!.text.toString().trim()
        regState = editRegState!!.text.toString().trim()
        regZipcode = editRegZipCode!!.text.toString().trim()
        regPhnNmbr = editRegPhnNmbr!!.text.toString().trim()
        regAltrntNmbr = editRegAltrnteNmbr!!.text.toString().trim()

        regFirstName = edit_first_name.text.toString().trim()
        regLastName = edit_last_name.text.toString().trim()
        regAddressLine2 = edit_reg_address_line.text.toString().trim()
        regSecurityNumber = edit_security_number.text.toString().trim()

        if (!isFrontUploaded!!){
            Toast.makeText(
                this@AddBankPersonalInfoActivity,
                "license front image is not uploaded",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (!isBackUploaded!!){
            Toast.makeText(
                this@AddBankPersonalInfoActivity,
                "license back image is not uploaded",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (Util.valueMandetory(applicationContext, regFirstName, edit_first_name)) {
            return
        }

        if (Util.valueMandetory(applicationContext, regLastName, edit_last_name)) {
            return
        }

        if (Util.valueMandetory(applicationContext, regSecurityNumber, edit_security_number)) {

            return
        }
        if (regSecurityNumber!!.length<9){
            return
        }

        if (Util.valueMandetory(applicationContext, regDob, editRegDob!!)) {
            return
        }

        if (Util.valueMandetory(applicationContext, regAddress, editRegAddress!!)) {
            return
        }

        if (Util.valueMandetory(applicationContext, regCity, editRegCity!!)) {
            return
        }

        if (Util.valueMandetory(applicationContext, regState, editRegState!!)) {
            return
        }

        if (Util.valueMandetory(applicationContext, regZipcode, editRegZipCode!!)) {
            return
        }

        if (Util.valueMandetory(applicationContext, regPhnNmbr, editRegPhnNmbr!!)) {
            return
        }

        if (regPhnNmbr!!.length != 14) {
            editRegPhnNmbr?.error = getString(R.string.error_phn)
            editRegPhnNmbr?.requestFocus()
            return
        }

        if (regPhnNmbr.equals(regAltrntNmbr)) {
            editRegAltrnteNmbr?.error = getString(R.string.error_alternate_no)
            editRegAltrnteNmbr?.requestFocus()
            return
        }

        Log.e("DOBOOOOO", regDob!!)


        registrationPayload3.firstName = regFirstName!!
        registrationPayload3.lastName = regLastName!!
        if (regSecurityNumber!!.length>4){
            registrationPayload3.ssn_last_4 = regSecurityNumber!!.substring(regSecurityNumber!!.length-4)
        }
        registrationPayload3.ssn_token=regSecurityNumber



        registrationPayload3.address2 = regAddressLine2!!

        registrationPayload3.dob = regDob!!
        registrationPayload3.address1 = regAddress
        registrationPayload3.city = regCity
        registrationPayload3.state = regState
        registrationPayload3.zipCode = regZipcode
        registrationPayload3.phoneNumber = regPhnNmbr!!.filter { it.isDigit() }
        registrationPayload3.alternatePhoneNumber = regAltrntNmbr

        Util.navigationNext(this, AddBankAccountInfoActivity::class.java)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        /* if(Kotpref.isRegisterConfirm){
             Kotpref.isRegisterConfirm=false

         }*/
        Util.navigationBack(this)
    }



}
