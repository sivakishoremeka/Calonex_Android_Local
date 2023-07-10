package mp.app.calonex.brokerRegistration

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import mp.app.calonex.R
import mp.app.calonex.landlord.activity.LoginScreen.Companion.registrationPayloadBroker
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.UsPhoneNumberFormatter
import mp.app.calonex.common.utility.Util
import mp.app.calonex.common.utility.Util.valueMandetory
import mp.app.calonex.landlord.activity.LoginScreen
import mp.app.calonex.landlord.model.ZipCityStateModel
import java.text.SimpleDateFormat
import java.util.*

class UserContactDetailScreenBroker : AppCompatActivity() {

    private var editRegAddress: TextInputEditText? = null
    private var editRegZipCode: TextInputEditText? = null
    private var editRegCity: TextInputEditText? = null
    private var editRegState: TextInputEditText? = null
    private var editRegPhnNmbr: TextInputEditText? = null
    private var editRegAltrnteNmbr: TextInputEditText? = null
    private var editRegDob: TextInputEditText? = null
    private var editRegAgentId:TextInputEditText?=null

    private var regAddress: String? = null
    private var regZipcode: String? = null
    private var regCity: String? = null
    private var regState: String? = null
    private var regPhnNmbr: String? = ""
    private var regAltrntNmbr: String? = null
    private var regDob: String? = null
    private var regAgentId:String?=null
    private var signup:TextView?=null

    private var btnRegNxt: TextView? = null

    private var zcsList = ArrayList<ZipCityStateModel>()

    private var cal = Calendar.getInstance()
    private var year = cal.get(Calendar.YEAR)
    private var month = cal.get(Calendar.MONTH)
    private var day = cal.get(Calendar.DAY_OF_MONTH)
    val myFormat = "MMM dd, yyyy"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_contact_detail_screen_broker)
        initComponent()
        actionComponent()
    }

    fun initComponent() {
        signup=findViewById(R.id.signup);
        editRegAddress = findViewById(R.id.edit_reg_address)
        editRegZipCode = findViewById(R.id.edit_reg_zip)
        editRegCity = findViewById(R.id.edit_reg_city)
        editRegState = findViewById(R.id.edit_reg_state)
        editRegPhnNmbr = findViewById(R.id.edit_reg_phn)
        editRegAltrnteNmbr = findViewById(R.id.edit_reg_alt_phn)
        editRegDob = findViewById(R.id.edit_reg_dob)
        editRegAgentId=findViewById(R.id.edit_reg_agent_id)

        btnRegNxt = findViewById(R.id.btn_reg_next)

        zcsList = Util.getZipCityStateList(applicationContext)
        val addLineNumberFormatter = UsPhoneNumberFormatter(editRegPhnNmbr!!)
        editRegPhnNmbr!!.addTextChangedListener(addLineNumberFormatter)
        val addLineNumberFormatterAlter = UsPhoneNumberFormatter(editRegAltrnteNmbr!!)

        editRegAltrnteNmbr!!.addTextChangedListener(addLineNumberFormatterAlter)



        Util.setEditReadOnly(editRegDob!!, true, InputType.TYPE_NULL)
        Util.setEditReadOnly(editRegCity!!, false, InputType.TYPE_NULL)
        Util.setEditReadOnly(editRegState!!, false, InputType.TYPE_NULL)

        registrationPayloadBroker.address2 = ""
        registrationPayloadBroker.userId = Kotpref.userId
        registrationPayloadBroker.userRoleName = "CX-Broker"
    }

    override fun onResume() {
        super.onResume()
        if (Kotpref.isRegisterConfirm) {
            editRegAddress!!.setText(registrationPayloadBroker.address1)
            editRegZipCode!!.setText(registrationPayloadBroker.zipCode)
            editRegCity!!.setText(registrationPayloadBroker.city)
            editRegState!!.setText(registrationPayloadBroker.state)
            editRegPhnNmbr!!.setText(
                PhoneNumberUtils.formatNumber(
                    registrationPayloadBroker.phoneNumber,
                    "US"
                )
            )


            editRegAltrnteNmbr!!.setText(
                PhoneNumberUtils.formatNumber(
                    LoginScreen.registrationPayloadBroker.alternatePhoneNumber,
                    "US"
                )
            )




            editRegAltrnteNmbr!!.setText(registrationPayloadBroker.alternatePhoneNumber)
            editRegDob!!.setText(Kotpref.dateRegistration)
            regDob = registrationPayloadBroker.dob
            editRegAgentId!!.setText(registrationPayloadBroker.brokerLicenseNumber)
            btnRegNxt!!.text = getString(R.string.done)

        }
    }

    fun actionComponent() {


        signup!!.setOnClickListener {
            // startActivity(Intent(this@QuickRegistrationActivity, LoginScreen::class.java))
            val intent = Intent(this, LoginScreen::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }


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
        regAddress = editRegAddress!!.text.toString().trim()
        regZipcode = editRegZipCode!!.text.toString().trim()
        regCity = editRegCity!!.text.toString().trim()
        regState = editRegState!!.text.toString().trim()
        regPhnNmbr = editRegPhnNmbr!!.text.toString().trim()
        regAltrntNmbr = editRegAltrnteNmbr!!.text.toString().trim()
        regAgentId=editRegAgentId!!.text.toString().trim()

        if (valueMandetory(applicationContext, regAddress, editRegAddress!!)) {
            return
        }

        if (valueMandetory(applicationContext, regDob, editRegDob!!)) {

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





        if(valueMandetory(applicationContext,regAgentId,editRegAgentId!!)){
            return
        }
        if(regAgentId!!.length!=11){
            editRegAgentId?.error  = getString(R.string.error_broker_id)
            editRegAgentId?.requestFocus()
            return
        }

        registrationPayloadBroker.address1 = regAddress
        registrationPayloadBroker.zipCode = regZipcode
        registrationPayloadBroker.city = regCity
        registrationPayloadBroker.state = regState
        registrationPayloadBroker.phoneNumber = regPhnNmbr
        registrationPayloadBroker.alternatePhoneNumber = regAltrntNmbr
        registrationPayloadBroker.dob = regDob
        registrationPayloadBroker.brokerLicenseNumber=regAgentId

        if (Kotpref.isRegisterConfirm) {
            onBackPressed()
        } else {
            Util.navigationNext(this, BusinessInfoScreenBroker::class.java)
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