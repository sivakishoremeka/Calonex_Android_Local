package mp.app.calonex.agentRegistration

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import mp.app.calonex.R
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.LoginScreen

class NewBrokerInfoScreenAgent : AppCompatActivity() {
    private var editRegBrokerName: TextInputEditText? = null
    private var editRegLicenceNo: TextInputEditText? = null

    private var regBrokerName: String? = null
    private var regLicenceNo: String? = null

    private var btnRegNxt: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_broker_info_screen_agent)
        initComponent()
        actionComponent()
    }

    fun initComponent() {
        editRegBrokerName = findViewById(R.id.edit_reg_broker_name)
        editRegLicenceNo = findViewById(R.id.edit_reg_broker_licence)

        btnRegNxt = findViewById(R.id.btn_reg_next)
    }

    override fun onResume() {
        super.onResume()
        if (Kotpref.isRegisterConfirm) {
            editRegBrokerName!!.setText(LoginScreen.registrationPayloadAgent.brokerLicenseName)
            editRegLicenceNo!!.setText(LoginScreen.registrationPayloadAgent.brokerLicenseNumber)
            btnRegNxt!!.text = getString(R.string.done)

        }
    }

    fun actionComponent() {
        btnRegNxt!!.setOnClickListener { view ->
            // Hide the keyboard
            val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            validComponent()
        }
    }

    fun validComponent() {
        regBrokerName = editRegBrokerName!!.text.toString().trim()
        regLicenceNo = editRegLicenceNo!!.text.toString().trim()

        if (Util.valueMandetory(applicationContext, regBrokerName, editRegBrokerName!!)) {
            return
        }

        if (Util.valueMandetory(applicationContext, regLicenceNo, editRegLicenceNo!!)) {
            return
        }

        LoginScreen.registrationPayloadAgent.brokerLicenseName = regBrokerName
        LoginScreen.registrationPayloadAgent.brokerLicenseNumber = regLicenceNo

        if (Kotpref.isRegisterConfirm) {
            onBackPressed()
        } else {
            Util.navigationNext(this, NewUserDocumentScreenAgent::class.java)
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