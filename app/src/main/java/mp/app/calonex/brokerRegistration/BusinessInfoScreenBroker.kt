package mp.app.calonex.brokerRegistration

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import mp.app.calonex.R
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.Util
import mp.app.calonex.common.utility.Util.valueMandetory
import mp.app.calonex.landlord.activity.LoginScreen
import mp.app.calonex.landlord.activity.LoginScreen.Companion.registrationPayloadBroker

class BusinessInfoScreenBroker : AppCompatActivity() {

    private var editBusinessName: TextInputEditText? = null
    private var editBusinessGroup: TextInputEditText? = null
    private var editBusinessAddress: TextInputEditText? = null
    private var editBusinessEmail: TextInputEditText? = null
    private var editBusinessPhone: TextInputEditText? = null
    private var editBusinessFax: TextInputEditText? = null
    private var editBusinessEin: TextInputEditText? = null

    private var regBusinessName: String? = null
    private var regBusinessGroup: String? = null
    private var regBusinessAddress: String? = null
    private var regBusinessEmail: String? = null
    private var regBusinessPhone: String? = null
    private var regBusinessFax: String? = null
    private var regBusinessEin: String? = null
    private  var signup:TextView?=null

    private var btnRegNxt: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_business_detail_screen_broker)
        initComponent()
        actionComponent()
    }

    fun initComponent() {
        signup=findViewById(R.id.signup);
        editBusinessName = findViewById(R.id.edit_reg_business_name)
        editBusinessGroup = findViewById(R.id.edit_reg_business_group)
        editBusinessAddress = findViewById(R.id.edit_reg_business_address)
        editBusinessEmail = findViewById(R.id.edit_reg_business_email)
        editBusinessPhone = findViewById(R.id.edit_reg_business_phn)
        editBusinessFax = findViewById(R.id.edit_reg_business_fax)
        editBusinessEin = findViewById(R.id.edit_reg_business_ein)

        btnRegNxt = findViewById(R.id.btn_reg_next)
    }

    override fun onResume() {
        super.onResume()
        if (Kotpref.isRegisterConfirm) {
            editBusinessName!!.setText(registrationPayloadBroker.businessName)
            editBusinessGroup!!.setText(registrationPayloadBroker.userGroupList[0].groupName)
            editBusinessAddress!!.setText(registrationPayloadBroker.businessAddress)
            editBusinessEmail!!.setText(registrationPayloadBroker.businessEmail)
            editBusinessPhone!!.setText(registrationPayloadBroker.businessPhone)
            editBusinessFax!!.setText(registrationPayloadBroker.businessFax)
            editBusinessEin!!.setText(registrationPayloadBroker.brokerLicenseNumber)
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

        btnRegNxt!!.setOnClickListener { view ->
            // Hide the keyboard
            val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            validComponent()
        }
    }

    fun validComponent() {

        regBusinessName = editBusinessName!!.text.toString().trim()
        regBusinessGroup = editBusinessGroup!!.text.toString().trim()
        regBusinessAddress = editBusinessAddress!!.text.toString().trim()
        regBusinessEmail = editBusinessEmail!!.text.toString().trim()
        regBusinessPhone = editBusinessPhone!!.text.toString().trim()
        regBusinessFax = editBusinessFax!!.text.toString().trim()
        //regBusinessEin = editBusinessEin!!.text.toString().trim()

        if (valueMandetory(applicationContext, regBusinessName, editBusinessName!!)) {
            return
        }

        if (valueMandetory(applicationContext, regBusinessAddress, editBusinessAddress!!)) {
            return
        }

       /* if (valueMandetory(applicationContext, regBusinessEmail, editBusinessEmail!!)) {
            return
        }

*/
        if (TextUtils.isEmpty(regBusinessEmail) || (!Util.isEmailValid(editBusinessEmail!!.text.toString().trim()))) {
            editBusinessEmail!!.error = "Valid email Id required"
            editBusinessEmail!!.requestFocus()
            return
        }


        if (valueMandetory(applicationContext, regBusinessPhone, editBusinessPhone!!)) {
            return
        }

        if (valueMandetory(applicationContext, regBusinessFax, editBusinessFax!!)) {
            return
        }

        /*
        if (regPhnNmbr!!.length != 14) {
            editRegPhnNmbr?.error = getString(R.string.error_phn)
            editRegPhnNmbr?.requestFocus()
            return
        }
         */

        val group = UserGroupList()
        group.groupName = regBusinessGroup

        registrationPayloadBroker.businessName = regBusinessName
        registrationPayloadBroker.userGroupList = arrayListOf(group)
        registrationPayloadBroker.businessAddress = regBusinessAddress
        registrationPayloadBroker.businessEmail = regBusinessEmail
        registrationPayloadBroker.businessPhone = regBusinessPhone
        registrationPayloadBroker.businessFax = regBusinessFax
        registrationPayloadBroker.userGroupPath = ""
        //registrationPayloadBroker.brokerLicenseName = regBusinessEin

        if (Kotpref.isRegisterConfirm) {
            onBackPressed()
        } else {
            Util.navigationNext(this, UserConfirmDetailScreenBroker::class.java)
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