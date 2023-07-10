package mp.app.calonex.agentBrokerRegLd

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.stripe.android.view.CardInputWidget
import mp.app.calonex.R
import mp.app.calonex.agent.fragment.ProfileFragmentAgent.Companion.registrationPayload
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.RSA
import mp.app.calonex.common.utility.Util
import mp.app.calonex.common.utility.Util.valueMandetory
import mp.app.calonex.agentBrokerRegLd.SubscriptionPlanAdapterNew.Companion.subscriptionPayload
import mp.app.calonex.landlord.activity.CxBaseActivity2

class UserAccountScreenNew : CxBaseActivity2() {
    private var editBnkUsrName: TextInputEditText? = null
    private var editRegAccount: TextInputEditText? = null
    private var editRegCnfmAccount: TextInputEditText? = null
    private var editRegRout: TextInputEditText? = null
    private var btnAccountNxt: Button? = null
    private var txtPlan: TextView? = null
    private var regBnkUsrName: String? = ""
    private var regAccount: String? = ""
    private var regCnfrmAccount: String? = null
    private var regRouting: String? = ""

    private var txtAcc: TextView? = null
    private var txtCard: TextView? = null

    private lateinit var bank_part: LinearLayout
    private lateinit var cardInputWidget: CardInputWidget

    private var skipBankCBox: CheckBox? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_account_screen_new)

        initComponent()
        actionComponent()
        startHandler()
    }

    fun initComponent() {
        txtPlan = findViewById(R.id.txt_plan)
        txtPlan!!.text = Kotpref.planSelected

        editBnkUsrName = findViewById(R.id.edit_bnk_usr_name)
        editRegAccount = findViewById(R.id.edit_reg_account)
        editRegCnfmAccount = findViewById(R.id.edit_reg_cnfm_account)
        editRegRout = findViewById(R.id.edit_reg_routing)
        btnAccountNxt = findViewById(R.id.btn_account_nxt)

        txtAcc = findViewById(R.id.txt_acc)
        txtCard = findViewById(R.id.txt_card)
        bank_part = findViewById(R.id.bank_part)
        cardInputWidget = findViewById(R.id.cardInputWidget)

        skipBankCBox = findViewById(R.id.skip_bank_now)

        if(Kotpref.loginRole.equals("broker",true)||Kotpref.loginRole.equals("agent",true))
            skipBankCBox?.visibility=View.VISIBLE
        subscriptionPayload.isSignupThroughInvite = true

        skipBankCBox!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                bank_part.visibility = View.GONE
                subscriptionPayload.skipBank = true
                Log.e("Flag", subscriptionPayload.skipBank.toString())
            } else {
                bank_part.visibility = View.VISIBLE
                subscriptionPayload.skipBank = false
                Log.e("Flag", subscriptionPayload.skipBank.toString())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (Kotpref.isRegisterConfirm) {
            /*if (subscriptionPayload.isCreditCard) {
                cardPayFlag = true

                editBnkUsrName?.visibility = View.GONE
                editRegAccount?.visibility = View.GONE
                editRegCnfmAccount?.visibility = View.GONE
                editRegRout?.visibility = View.GONE
                bank_part.visibility = View.GONE

                cardInputWidget.visibility = View.VISIBLE

                txtAcc?.setBackgroundResource(R.drawable.btn_dk_grey_round)
                txtCard?.setBackgroundResource(R.drawable.btn_dk_blue_round)
            } else {
                editBnkUsrName!!.setText(RSA.decrypt(registrationPayload.bankUserName!!))
                editRegAccount!!.setText(RSA.decrypt(registrationPayload.bankAccountNumber!!))
                editRegCnfmAccount!!.setText(RSA.decrypt(registrationPayload.bankAccountNumber!!))
                editRegRout!!.setText(RSA.decrypt(registrationPayload.routingNumber!!))
                btnAccountNxt!!.text = getString(R.string.done)

                cardPayFlag = false

                editBnkUsrName?.visibility = View.VISIBLE
                editRegAccount?.visibility = View.VISIBLE
                editRegCnfmAccount?.visibility = View.VISIBLE
                editRegRout?.visibility = View.VISIBLE
                bank_part.visibility = View.VISIBLE

                cardInputWidget.visibility = View.GONE

                txtAcc?.setBackgroundResource(R.drawable.btn_dk_blue_round)
                txtCard?.setBackgroundResource(R.drawable.btn_dk_grey_round)
            }*/



            if (subscriptionPayload.skipBank) {
                skipBankCBox!!.isChecked = true

                bank_part.visibility = View.GONE

            } else {
                editBnkUsrName!!.setText(RSA.decrypt(registrationPayload.bankUserName!!))
                editRegAccount!!.setText(RSA.decrypt(registrationPayload.bankAccountNumber!!))
                editRegCnfmAccount!!.setText(RSA.decrypt(registrationPayload.bankAccountNumber!!))
                editRegRout!!.setText(RSA.decrypt(registrationPayload.routingNumber!!))
                btnAccountNxt!!.text = getString(R.string.done)

                skipBankCBox!!.isChecked = false

                bank_part.visibility = View.VISIBLE

            }
        }
    }

    fun actionComponent() {
        /*txtAcc!!.setOnClickListener {
            cardPayFlag = false

            editBnkUsrName?.visibility = View.VISIBLE
            editRegAccount?.visibility = View.VISIBLE
            editRegCnfmAccount?.visibility = View.VISIBLE
            editRegRout?.visibility = View.VISIBLE
            bank_part.visibility = View.VISIBLE

            cardInputWidget.visibility = View.GONE

            txtAcc?.setBackgroundResource(R.drawable.btn_dk_blue_round)
            txtCard?.setBackgroundResource(R.drawable.btn_dk_grey_round)

            //Log.e("PAYMENT_ACC_LOG", cardPayFlag.toString())
        }*/

        /*txtCard!!.setOnClickListener {
            cardPayFlag = true

            editBnkUsrName?.visibility = View.GONE
            editRegAccount?.visibility = View.GONE
            editRegCnfmAccount?.visibility = View.GONE
            editRegRout?.visibility = View.GONE
            bank_part.visibility = View.GONE

            cardInputWidget.visibility = View.VISIBLE

            txtAcc?.setBackgroundResource(R.drawable.btn_dk_grey_round)
            txtCard?.setBackgroundResource(R.drawable.btn_dk_blue_round)

            //Log.e("PAYMENT_CARD_LOG", cardPayFlag.toString())
        }*/

        btnAccountNxt!!.setOnClickListener {
            validComponent()
        }
    }

    fun validComponent() {
        //subscriptionPayload.isCreditCard = cardPayFlag
        subscriptionPayload.isCreditCard = false

        /*if (cardPayFlag) {
            cardInputWidget.cardParams?.let { card ->
                // Create a Stripe token from the card details
                val stripe = Stripe(
                    applicationContext,
                    "pk_test_51HxhDBKJ1orMwPHntkQXg8gw9lDCsthnwshMLWHQxZuq6qzmYGrjqhTFtXrehaXjt4jb0sqTLF1YMdhIqZCtNiU700y8yovFv3"
                )
                stripe.createCardToken(card, callback = object : ApiResultCallback<Token> {
                    override fun onSuccess(result: Token) {
                        Kotpref.stripeToken = result.id
                    }

                    override fun onError(e: java.lang.Exception) {
                        Log.e("Token Error", e.toString())
                    }
                })
            }

            Kotpref.isCardUseToPay = true
        } else {
            regBnkUsrName = editBnkUsrName!!.text.toString().trim()
            regAccount = editRegAccount!!.text.toString().trim()
            regCnfrmAccount = editRegCnfmAccount!!.text.toString().trim()
            regRouting = editRegRout!!.text.toString().trim()

            if (valueMandetory(applicationContext, regBnkUsrName, editBnkUsrName!!)) {
                return
            }

            if (valueMandetory(applicationContext, regAccount, editRegAccount!!)) {
                return
            }

            if (valueMandetory(applicationContext, regCnfrmAccount, editRegCnfmAccount!!)) {
                return
            }

            // if(regAccount!!.length<2 || regAccount!!.length>30)
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

            if (valueMandetory(applicationContext, regRouting, editRegRout!!)) {
                return
            }

            if (regRouting!!.length != 9) {
                editRegRout?.error = getString(R.string.error_routing)
                editRegRout?.requestFocus()
                return
            }
            registrationPayload.bankUserName = RSA.encrypt(regBnkUsrName!!)
            registrationPayload.bankAccountNumber = RSA.encrypt(regAccount!!)
            registrationPayload.routingNumber = RSA.encrypt(regRouting!!)

            subscriptionPayload.bankUserName = RSA.encrypt(regBnkUsrName!!)
            subscriptionPayload.bankAccountNumber = RSA.encrypt(regAccount!!)
            subscriptionPayload.routingNumber = RSA.encrypt(regRouting!!)

            Kotpref.isCardUseToPay = false
        }*/

        if (subscriptionPayload.skipBank) {
            subscriptionPayload.bankUserName = RSA.encrypt(regBnkUsrName!!)
            subscriptionPayload.bankAccountNumber = RSA.encrypt(regAccount!!)
            subscriptionPayload.routingNumber = RSA.encrypt(regRouting!!)
        } else {
            regBnkUsrName = editBnkUsrName!!.text.toString().trim()
            regAccount = editRegAccount!!.text.toString().trim()
            regCnfrmAccount = editRegCnfmAccount!!.text.toString().trim()
            regRouting = editRegRout!!.text.toString().trim()

            if (valueMandetory(applicationContext, regBnkUsrName, editBnkUsrName!!)) {
                return
            }

            if (valueMandetory(applicationContext, regAccount, editRegAccount!!)) {
                return
            }

            if (valueMandetory(applicationContext, regCnfrmAccount, editRegCnfmAccount!!)) {
                return
            }

            // if(regAccount!!.length<2 || regAccount!!.length>30)
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

            if (valueMandetory(applicationContext, regRouting, editRegRout!!)) {
                return
            }

            if (regRouting!!.length != 9) {
                editRegRout?.error = getString(R.string.error_routing)
                editRegRout?.requestFocus()
                return
            }
            registrationPayload.bankUserName = RSA.encrypt(regBnkUsrName!!)
            registrationPayload.bankAccountNumber = RSA.encrypt(regAccount!!)
            registrationPayload.routingNumber = RSA.encrypt(regRouting!!)

            subscriptionPayload.bankUserName = RSA.encrypt(regBnkUsrName!!)
            subscriptionPayload.bankAccountNumber = RSA.encrypt(regAccount!!)
            subscriptionPayload.routingNumber = RSA.encrypt(regRouting!!)
        }

        Log.e("Flag Final", subscriptionPayload.skipBank.toString())
        //Kotpref.isCardUseToPay = false

        if (Kotpref.isRegisterConfirm) {
            onBackPressed()
        } else {
            Util.navigationNext(this, UserConfirmDetailScreenNew::class.java)
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