package mp.app.calonex.ldBkAddBank

import android.os.Bundle
import android.widget.Button
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_add_bank_account_info.*
import mp.app.calonex.R
import mp.app.calonex.common.utility.RSA
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.CxBaseActivity2
import mp.app.calonex.landlord.activity.LoginScreen.Companion.registrationPayload3

class AddBankAccountInfoActivity : CxBaseActivity2() {
    private var editBnkUsrName: TextInputEditText? = null
    private var editRegAccount: TextInputEditText? = null
    private var editRegCnfmAccount: TextInputEditText? = null
    private var editRegRout: TextInputEditText? = null
    private var btnAccountNxt: Button? = null

    private var regBnkUsrName: String? = null
    private var regAccount: String? = null
    private var regCnfrmAccount: String? = null
    private var regRouting: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_bank_account_info)

        initComponent()
        actionComponent()
        startHandler()
    }

    fun initComponent() {
        editBnkUsrName = findViewById(R.id.edit_bnk_usr_name)
        editRegAccount = findViewById(R.id.edit_reg_account)
        editRegCnfmAccount = findViewById(R.id.edit_reg_cnfm_account)
        editRegRout = findViewById(R.id.edit_reg_routing)
        btnAccountNxt = findViewById(R.id.btn_account_nxt)

        editBnkUsrName!!.setText(registrationPayload3.firstName!! + " " + registrationPayload3.lastName!!)
    }

    override fun onResume() {
        super.onResume()

        //editBnkUsrName!!.setText(RSA.decrypt(registrationPayload3.bankUserName!!))
        editBnkUsrName!!.setText(registrationPayload3.firstName!! + " " + registrationPayload3.lastName!!)

        editRegAccount!!.setText(RSA.decrypt(registrationPayload3.bankAccountNumber!!))
        editRegCnfmAccount!!.setText(RSA.decrypt(registrationPayload3.bankAccountNumber!!))
        editRegRout!!.setText(RSA.decrypt(registrationPayload3.routingNumber!!))
    }

    fun actionComponent() {
        btnAccountNxt!!.setOnClickListener {
            validComponent()
        }

        header_back.setOnClickListener {
            super.onBackPressed()
            // Util.navigationBack(this)
            Util.navigationBack(this)
        }

    }

    fun validComponent() {
        regBnkUsrName = editBnkUsrName!!.text.toString().trim()
        regAccount = editRegAccount!!.text.toString().trim()
        regCnfrmAccount = editRegCnfmAccount!!.text.toString().trim()
        regRouting = editRegRout!!.text.toString().trim()

        if (Util.valueMandetory(applicationContext, regBnkUsrName, editBnkUsrName!!)) {
            return
        }

        if (Util.valueMandetory(applicationContext, regAccount, editRegAccount!!)) {
            return
        }

        if (Util.valueMandetory(applicationContext, regCnfrmAccount, editRegCnfmAccount!!)) {
            return
        }

        if (regAccount!!.length < 2 || regAccount!!.length > 16) {
            editRegAccount?.error = getString(R.string.error_acc_nmbr)
            editRegAccount?.requestFocus()
            return
        }

        if (!regCnfrmAccount!!.equals(regAccount)) {
            editRegCnfmAccount?.error = getString(R.string.error_cnfm_acc_nmbr)
            editRegCnfmAccount?.requestFocus()
            return
        }

        if (Util.valueMandetory(applicationContext, regRouting, editRegRout!!)) {
            return
        }

        if (regRouting!!.length != 9) {
            editRegRout?.error = getString(R.string.error_routing)
            editRegRout?.requestFocus()
            return
        }

        registrationPayload3.bankUserName = RSA.encrypt(regBnkUsrName!!)
        registrationPayload3.bankAccountNumber = RSA.encrypt(regAccount!!)
        registrationPayload3.routingNumber = RSA.encrypt(regRouting!!)

        /*SubscriptionPlanAdapter.subscriptionPayload.bankUserName= RSA.encrypt(regBnkUsrName!!)
        SubscriptionPlanAdapter.subscriptionPayload.bankAccountNumber= RSA.encrypt(regAccount!!)
        SubscriptionPlanAdapter.subscriptionPayload.routingNumber= RSA.encrypt(regRouting!!)*/

        Util.navigationNext(this, AddBankConfirmSubmitActivity::class.java)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Util.navigationBack(this)
    }
}