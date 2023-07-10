package mp.app.calonex.LdRegBankAdd

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText

import mp.app.calonex.R
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.RSA
import mp.app.calonex.common.utility.Util
import mp.app.calonex.common.utility.Util.valueMandetory
import mp.app.calonex.landlord.activity.LoginScreen.Companion.bankAddModelLL
import mp.app.calonex.registration.adapter.SubscriptionPlanAdapter.Companion.subscriptionPayload

class UserAccountScreenBankAdd : AppCompatActivity() {
    private var editBnkUsrName: TextInputEditText?=null
    private var editRegAccount: TextInputEditText?=null
    private var editRegCnfmAccount: TextInputEditText?=null
    private var editRegRout: TextInputEditText?=null
    private var btnAccountNxt: Button?=null
    private var txtPlan:TextView?=null
    private var regBnkUsrName:String?=null
    private var regAccount:String?=null
    private var regCnfrmAccount:String?=null
    private var regRouting:String?=null

    private var cardPayFlag:Boolean=false

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_account_screen_add_bank)

        initComponent()
        actionComponent()
    }

    fun initComponent() {
        txtPlan=findViewById(R.id.txt_plan)
        txtPlan!!.text= Kotpref.planSelected
        editBnkUsrName=findViewById(R.id.edit_bnk_usr_name)
        editRegAccount=findViewById(R.id.edit_reg_account)
        editRegCnfmAccount=findViewById(R.id.edit_reg_cnfm_account)
        editRegRout=findViewById(R.id.edit_reg_routing)
        btnAccountNxt=findViewById(R.id.btn_account_nxt)
    }

    override fun onResume() {
        super.onResume()
        if(Kotpref.isRegisterConfirm){
            editBnkUsrName!!.setText(RSA.decrypt(bankAddModelLL.bankUserName!!))
            editRegAccount!!.setText(RSA.decrypt(bankAddModelLL.bankAccountNumber!!))
            editRegCnfmAccount!!.setText(RSA.decrypt(bankAddModelLL.bankAccountNumber!!))
            editRegRout!!.setText(RSA.decrypt(bankAddModelLL.routingNumber!!))
            btnAccountNxt!!.setText(getString(R.string.done))
        }
    }

    fun actionComponent(){
        cardPayFlag = false

        btnAccountNxt!!.setOnClickListener {
            validComponent()
        }
    }

    fun validComponent() {
        subscriptionPayload.isCreditCard = false

        regBnkUsrName= editBnkUsrName!!.text.toString().trim()
        regAccount = editRegAccount!!.text.toString().trim()
        regCnfrmAccount= editRegCnfmAccount!!.text.toString().trim()
        regRouting = editRegRout!!.text.toString().trim()

        if(valueMandetory(applicationContext,regBnkUsrName,editBnkUsrName!!)){
            return
        }

        if(valueMandetory(applicationContext,regAccount,editRegAccount!!)){
            return
        }

        if(valueMandetory(applicationContext,regCnfrmAccount,editRegCnfmAccount!!)){
            return
        }

        // if(regAccount!!.length<2 || regAccount!!.length>30)
        if(regAccount!!.length<2 || regAccount!!.length > 16) {
            editRegAccount?.error  = getString(R.string.error_acc_nmbr)
            editRegAccount?.requestFocus()
            return
        }

        if(!regCnfrmAccount!!.equals(regAccount)){
            editRegCnfmAccount?.error  = getString(R.string.error_cnfm_acc_nmbr)
            editRegCnfmAccount?.requestFocus()
            return
        }

        if(valueMandetory(applicationContext,regRouting,editRegRout!!)){
            return
        }

        if(regRouting!!.length!=9){
            editRegRout?.error  = getString(R.string.error_routing)
            editRegRout?.requestFocus()
            return
        }
        bankAddModelLL.bankUserName= RSA.encrypt(regBnkUsrName!!)
        bankAddModelLL.bankAccountNumber= RSA.encrypt(regAccount!!)
        bankAddModelLL.routingNumber=RSA.encrypt(regRouting!!)

       /* subscriptionPayload.bankUserName= RSA.encrypt(regBnkUsrName!!)
        subscriptionPayload.bankAccountNumber= RSA.encrypt(regAccount!!)
        subscriptionPayload.routingNumber=RSA.encrypt(regRouting!!)*/

        Kotpref.isCardUseToPay = false

        if(Kotpref.isRegisterConfirm){
            onBackPressed()
        }else {
            Util.navigationNext(this, UserConfirmDetailScreenBankAdd::class.java)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(Kotpref.isRegisterConfirm){
            Kotpref.isRegisterConfirm=false
        }
        Util.navigationBack(this)
    }
}