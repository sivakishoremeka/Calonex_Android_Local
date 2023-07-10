package mp.app.calonex.agentBrok

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.google.android.material.textfield.TextInputEditText
import mp.app.calonex.R
import mp.app.calonex.agent.fragment.ProfileFragmentAgent.Companion.registrationPayload
import mp.app.calonex.agentBrokerRegLd.UserPropertyDetailScreenNew
import mp.app.calonex.common.utility.*
import mp.app.calonex.common.utility.Util.valueMandetory
import mp.app.calonex.landlord.activity.CxBaseActivity2
import mp.app.calonex.landlord.adapter.CustomSpinnerAdapter
import mp.app.calonex.landlord.model.ZipCityStateModel
import java.text.SimpleDateFormat
import java.util.*

class UserContactDetailScreenNew : CxBaseActivity2() {

    private var editRegFirstName:TextInputEditText?=null
    private var editRegMiddleName:TextInputEditText?=null
    private var editRegLastName:TextInputEditText?=null
    private var editRegBusinessName: TextInputEditText? = null
    private var editRegDob: TextInputEditText? = null
    private var editRegAddress: TextInputEditText? = null
    private var editRegCity: TextInputEditText? = null
    private var editRegState: TextInputEditText? = null
    private var editRegZipCode: TextInputEditText? = null
    private var editRegPhnNmbr: TextInputEditText? = null
    private var editRegAltrnteNmbr: TextInputEditText? = null

    private var editRegDrivingLicence:TextInputEditText?=null
    private var spinnerRegStateIssue: Spinner?=null
    private var txtPlan: TextView? = null
    private var btnRegNxt: TextView? = null
    private var btnRegSignin: TextView? = null

    private var regFirstName:String?=null
    private var regMiddleName:String?=null
    private var regLastName:String?=null
    private var regBusinessName: String? = null
    private var regDob: String? = null
    private var regAddress: String? = null
    private var regCity: String? = null
    private var regState: String? = null
    private var regZipcode: String? = null
    private var regPhnNmbr: String? = ""
    private var regAltrntNmbr: String? = null

    private var ssnNum: String? = null

    private var regDrivingLicence:String?=null
    private var regStateIssue: String? = null
    private var stateIssueList = ArrayList<String>()
    private var zcsList = ArrayList<ZipCityStateModel>()
    private var cal = Calendar.getInstance()
    private var year = cal.get(Calendar.YEAR)
    private var month = cal.get(Calendar.MONTH)
    private var day = cal.get(Calendar.DAY_OF_MONTH)
    val myFormat = "MMM dd, yyyy"

    private lateinit var edit_ut_ssn: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_contact_detail_screen_new)
        initComponent()
        actionComponent()
        startHandler()
    }

    fun initComponent() {
        txtPlan = findViewById(R.id.txt_plan)
        txtPlan!!.text = Kotpref.planSelected
        editRegFirstName=findViewById(R.id.edit_reg_first_name)
        editRegMiddleName=findViewById(R.id.edit_reg_middle_name)
        editRegLastName=findViewById(R.id.edit_reg_last_name)
        editRegBusinessName = findViewById(R.id.edit_reg_business_name)
        editRegDob = findViewById(R.id.edit_reg_dob)
        editRegAddress = findViewById(R.id.edit_reg_address)
        editRegCity = findViewById(R.id.edit_reg_city)
        editRegState = findViewById(R.id.edit_reg_state)
        editRegZipCode = findViewById(R.id.edit_reg_zip)
        editRegPhnNmbr = findViewById(R.id.edit_reg_phn)
        editRegAltrnteNmbr = findViewById(R.id.edit_reg_alt_phn)
        editRegDrivingLicence=findViewById(R.id.edit_reg_driving_licence)
        spinnerRegStateIssue=findViewById(R.id.spinner_reg_state_issue)
        btnRegNxt = findViewById(R.id.btn_reg_next)
        btnRegSignin = findViewById(R.id.txt_signin)
        zcsList = Util.getZipCityStateList(applicationContext)
        val addLineNumberFormatter = UsPhoneNumberFormatter(editRegPhnNmbr!!)
        val addLineNumberFormatterAlter = UsPhoneNumberFormatter(editRegAltrnteNmbr!!)
        editRegPhnNmbr!!.addTextChangedListener(addLineNumberFormatter)
        editRegAltrnteNmbr!!.addTextChangedListener(addLineNumberFormatterAlter)


        edit_ut_ssn = findViewById(R.id.edit_ut_ssn)

        val addSsnFormatter = UsSSNNumberFormatter(edit_ut_ssn!!)
        edit_ut_ssn!!.addTextChangedListener(addSsnFormatter)

        var listState = mutableListOf<String>()

        stateIssueList.add("State Issued")
        for (item in zcsList) {
            listState.add(item.state!!)
        }
        stateIssueList.addAll(ArrayList<String>(listState.toSet().sorted().toList()))
        val spinnerStateIssueAdapter = CustomSpinnerAdapter(applicationContext, stateIssueList)
        System.out.println("state list ==> " + stateIssueList)
        spinnerRegStateIssue?.adapter=spinnerStateIssueAdapter

        Util.setEditReadOnly(editRegDob!!, true, InputType.TYPE_NULL)
        Util.setEditReadOnly(editRegCity!!, false, InputType.TYPE_NULL)
        Util.setEditReadOnly(editRegState!!, false, InputType.TYPE_NULL)
    }

    override fun onResume() {
        super.onResume()
        if (Kotpref.isRegisterConfirm) {
            editRegFirstName!!.setText(registrationPayload.firstName)
            editRegMiddleName!!.setText(registrationPayload.middleName)
            editRegLastName!!.setText(registrationPayload.lastName)
            editRegBusinessName!!.setText(registrationPayload.businessName)
            editRegDob!!.setText(Kotpref.dateRegistration)
            regDob = registrationPayload.dob
            editRegAddress!!.setText(registrationPayload.address1)
            editRegCity!!.setText(registrationPayload.city)
            editRegState!!.setText(registrationPayload.state)
            editRegZipCode!!.setText(registrationPayload.zipCode)
            editRegPhnNmbr!!.setText(
                PhoneNumberUtils.formatNumber(
                    registrationPayload.phoneNumber,
                    "US"
                )
            )

            editRegAltrnteNmbr!!.setText(
                PhoneNumberUtils.formatNumber(
                    registrationPayload.alternatePhoneNumber,
                    "US"
                )
            )


           // editRegAltrnteNmbr!!.setText(registrationPayload.alternatePhoneNumber)
            editRegDrivingLicence!!.setText(registrationPayload.drivingLicenseNumber)
            spinnerRegStateIssue!!.setSelection(stateIssueList.indexOf(registrationPayload.stateIssued))
            btnRegNxt!!.text = getString(R.string.done)

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
            val minDate: Long = cal1.time.time
            dateDialog.datePicker.maxDate = minDate
            editRegDob!!.error = null
        }


        btnRegNxt!!.setOnClickListener { view ->
            // Hide the keyboard
            val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            validComponent()
        }

        btnRegSignin!!.setOnClickListener { view ->
        }

        spinnerRegStateIssue?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {

                if (position> 0){
                    regStateIssue= stateIssueList.get(position)

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // another interface callback
            }
        }

    }

    @SuppressLint("NewApi")
    private fun updateDateInView() {
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        val sdf1 = SimpleDateFormat("YYYY-MM-dd", Locale.US)
        editRegDob!!.setText(sdf.format(cal.time))
        Kotpref.dateRegistration = editRegDob!!.text.toString()
        regDob = sdf1.format(cal.time)
    }

    private fun setCityState(valueZip: Int?) {
        val filterList: List<ZipCityStateModel>? = zcsList.filter { it.zip == valueZip }
        if (filterList!!.size > 0) {
            val filterPlan: ZipCityStateModel? = filterList.get(0)
            editRegCity!!.setText(filterPlan!!.city)
            editRegState!!.setText(filterPlan.state)
        } else {
            editRegZipCode?.error = getString(R.string.error_zipcode)
            editRegZipCode?.requestFocus()
            editRegCity!!.setText("")
            editRegState!!.setText("")
            return
        }
    }

    fun validComponent() {
        regFirstName=editRegFirstName!!.text.toString().trim()
        regMiddleName=editRegMiddleName!!.text.toString().trim()
        regLastName=editRegLastName!!.text.toString().trim()
        regBusinessName = editRegBusinessName!!.text.toString().trim()
        regAddress = editRegAddress!!.text.toString().trim()
        regCity = editRegCity!!.text.toString().trim()
        regState = editRegState!!.text.toString().trim()
        regZipcode = editRegZipCode!!.text.toString().trim()
        regPhnNmbr = editRegPhnNmbr!!.text.toString().trim()
        regAltrntNmbr = editRegAltrnteNmbr!!.text.toString().trim()
        regDrivingLicence=RSA.encrypt(editRegDrivingLicence!!.text.toString().trim())

        try {
            ssnNum= RSA.encrypt(edit_ut_ssn.text.toString().trim())
        } catch (e: Exception) {
        }

        if (TextUtils.isEmpty(ssnNum)) {
            edit_ut_ssn.error = "Valid SSN number required."
            edit_ut_ssn.requestFocus()
            return
        }






        if(valueMandetory(applicationContext,regFirstName,edit_ut_ssn!!)){
            return
        }

        if(valueMandetory(applicationContext,regFirstName,editRegFirstName!!)){
            return
        }
        if(valueMandetory(applicationContext,regLastName,editRegLastName!!)){
            return
        }
        if (valueMandetory(applicationContext, regBusinessName, editRegBusinessName!!)) {
            return
        }
        if (valueMandetory(applicationContext, regDob, editRegDob!!)) {

            return
        }
        if (valueMandetory(applicationContext, regAddress, editRegAddress!!)) {
            return
        }
        if (valueMandetory(applicationContext, regCity, editRegCity!!)) {
            return
        }
        if (valueMandetory(applicationContext, regState, editRegState!!)) {
            return
        }

        if (valueMandetory(applicationContext, regZipcode, editRegZipCode!!)) {
            return
        }

        if (valueMandetory(applicationContext, regPhnNmbr, editRegPhnNmbr!!)) {
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

        if(valueMandetory(applicationContext,regDrivingLicence,editRegDrivingLicence!!)){
            return
        }
        if(editRegDrivingLicence!!.text.toString().length<9){
            editRegDrivingLicence?.error  = getString(R.string.error_driving_licence)
            editRegDrivingLicence?.requestFocus()
            return
        }
        if(edit_ut_ssn!!.text.toString().length<11){
            edit_ut_ssn?.error  = getString(R.string.error_ssn)
            edit_ut_ssn?.requestFocus()
            return
        }

        if (regStateIssue.isNullOrEmpty()) {
            Toast.makeText(applicationContext,"Please Select State Issued", Toast.LENGTH_SHORT).show()
            return
        }

        registrationPayload.firstName=regFirstName
        registrationPayload.middleName=regMiddleName
        registrationPayload.lastName=regLastName
        registrationPayload.businessName = regBusinessName
        registrationPayload.dob = regDob
        registrationPayload.address1 = regAddress
        registrationPayload.city = regCity
        registrationPayload.state = regState
        registrationPayload.zipCode = regZipcode
        registrationPayload.phoneNumber = regPhnNmbr!!.filter { it.isDigit() }
        registrationPayload.alternatePhoneNumber = regAltrntNmbr
        registrationPayload.drivingLicenseNumber=regDrivingLicence
        registrationPayload.stateIssued=regStateIssue
        registrationPayload.ssn=ssnNum

        if (Kotpref.isRegisterConfirm) {
            onBackPressed()
        } else {
            Util.navigationNext(this, UserPropertyDetailScreenNew::class.java)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (Kotpref.isRegisterConfirm) {
            Kotpref.isRegisterConfirm = false

        }
        Util.navigationBack(this)
    }
}