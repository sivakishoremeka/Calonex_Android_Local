package mp.app.calonex.registration.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckedTextView
import android.widget.LinearLayout
import android.widget.TextView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.stripe.android.ApiResultCallback
import com.stripe.android.Stripe
import com.stripe.android.model.Token
import com.stripe.android.view.CardInputWidget
import io.reactivex.Observable
import mp.app.calonex.R
import mp.app.calonex.common.constant.StatusConstant
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.RSA
import mp.app.calonex.common.utility.Util
import mp.app.calonex.common.utility.Util.valueMandetory
import mp.app.calonex.landlord.activity.LoginScreen.Companion.registrationPayload3
import mp.app.calonex.registration.adapter.SubscriptionPlanAdapter.Companion.subscriptionPayload
import mp.app.calonex.tenant.model.stripe.StripeKeyResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class UserAccountScreen : AppCompatActivity() {
    private var editBnkUsrName: TextInputEditText? = null
    private var editRegAccount: TextInputEditText? = null
    private var editRegCnfmAccount: TextInputEditText? = null
    private var editRegRout: TextInputEditText? = null
    private var btnAccountNxt: Button? = null
    private var txtPlan: TextView? = null
    private var regBnkUsrName: String? = null
    private var regAccount: String? = null
    private var regCnfrmAccount: String? = null
    private var regRouting: String? = null
    private var nameOnCard: String? = null
    private var stripeKey: String? = null
    private lateinit var ll_charge_apply: LinearLayout
    private lateinit var edit_name_on_card: TextInputEditText
    private lateinit var til_name: TextInputLayout

    private var txtAcc: CheckedTextView? = null
    private var txtCard: CheckedTextView? = null
    private var cardPayFlag: Boolean = false
    private lateinit var bank_part: LinearLayout
    private lateinit var cardInputWidget: CardInputWidget

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_account_screen)

        initComponent()
        actionComponent()

        getStripeKey()
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
        edit_name_on_card = findViewById(R.id.edit_name_on_card);
        ll_charge_apply = findViewById(R.id.ll_charge_apply);
        til_name = findViewById(R.id.til_name);
        cardInputWidget.postalCodeEnabled = false
    }

    override fun onResume() {
        super.onResume()
        if (Kotpref.isRegisterConfirm) {
            if (subscriptionPayload.isCreditCard) {
                cardPayFlag = true

                /*  editBnkUsrName?.visibility = View.GONE
                  editRegAccount?.visibility = View.GONE
                  editRegCnfmAccount?.visibility = View.GONE
                  editRegRout?.visibility = View.GONE
                  bank_part.visibility = View.GONE*/

                editBnkUsrName!!.setText(RSA.decrypt(registrationPayload3.bankUserName!!))
                editRegAccount!!.setText(RSA.decrypt(registrationPayload3.bankAccountNumber!!))
                editRegCnfmAccount!!.setText(RSA.decrypt(registrationPayload3.bankAccountNumber!!))
                editRegRout!!.setText(RSA.decrypt(registrationPayload3.routingNumber!!))
                cardInputWidget.setCardNumber(registrationPayload3.cardNumber)


                cardInputWidget.visibility = View.VISIBLE
                edit_name_on_card.visibility = View.VISIBLE
                ll_charge_apply.visibility = View.VISIBLE
                til_name.visibility = View.VISIBLE

                txtAcc?.setBackgroundResource(R.drawable.btn_dk_grey_round)
                txtCard?.setBackgroundResource(R.drawable.btn_dk_blue_round)


            } else {
                editBnkUsrName!!.setText(RSA.decrypt(registrationPayload3.bankUserName!!))
                editRegAccount!!.setText(RSA.decrypt(registrationPayload3.bankAccountNumber!!))
                editRegCnfmAccount!!.setText(RSA.decrypt(registrationPayload3.bankAccountNumber!!))
                editRegRout!!.setText(RSA.decrypt(registrationPayload3.routingNumber!!))
                btnAccountNxt!!.setText(getString(R.string.done))

                cardPayFlag = false

                editBnkUsrName?.visibility = View.VISIBLE
                editRegAccount?.visibility = View.VISIBLE
                editRegCnfmAccount?.visibility = View.VISIBLE
                editRegRout?.visibility = View.VISIBLE
                bank_part.visibility = View.VISIBLE

                cardInputWidget.visibility = View.GONE
                edit_name_on_card.visibility = View.GONE
                ll_charge_apply.visibility = View.GONE
                til_name.visibility = View.GONE




                txtAcc?.setBackgroundResource(R.drawable.btn_dk_blue_round)
                txtCard?.setBackgroundResource(R.drawable.btn_dk_grey_round)
            }
        }
    }

    fun actionComponent() {

        /*
                if(Kotpref.userRole.equals("broker",true)||Kotpref.userRole.equals("agent",true))
                    skipBankCBox?.visibility=View.VISIBLE
        */


        txtAcc!!.setOnClickListener {
            cardPayFlag = false


            /*  editBnkUsrName?.visibility = View.VISIBLE
              editRegAccount?.visibility = View.VISIBLE
              editRegCnfmAccount?.visibility = View.VISIBLE
              editRegRout?.visibility = View.VISIBLE
              bank_part.visibility = View.VISIBLE
  */
            cardInputWidget.visibility = View.GONE
            edit_name_on_card.visibility = View.GONE
            ll_charge_apply.visibility = View.GONE
            til_name.visibility = View.GONE




            txtAcc?.setBackgroundResource(R.drawable.btn_dk_blue_round)
            txtCard?.setBackgroundResource(R.drawable.btn_dk_grey_round)

            //Log.e("PAYMENT_ACC_LOG", cardPayFlag.toString())
        }

        txtCard!!.setOnClickListener {
            cardPayFlag = true

            /* editBnkUsrName?.visibility = View.GONE
             editRegAccount?.visibility = View.GONE
             editRegCnfmAccount?.visibility = View.GONE
             editRegRout?.visibility = View.GONE
             bank_part.visibility = View.GONE*/

            cardInputWidget.visibility = View.VISIBLE
            edit_name_on_card.visibility = View.VISIBLE
            ll_charge_apply.visibility = View.VISIBLE
            til_name.visibility = View.VISIBLE




            txtAcc?.setBackgroundResource(R.drawable.btn_dk_grey_round)
            txtCard?.setBackgroundResource(R.drawable.btn_dk_blue_round)

            //Log.e("PAYMENT_CARD_LOG", cardPayFlag.toString())
        }

        btnAccountNxt!!.setOnClickListener {
            validComponent()
        }
    }

    fun validComponent() {
        subscriptionPayload.isCreditCard = cardPayFlag

        if (cardPayFlag) {
            cardInputWidget.cardParams
            cardInputWidget.cardParams?.let { card ->
                // Create a Stripe token from the card details
                val stripe = stripeKey?.let { Stripe(applicationContext, it) }
                stripe?.createCardToken(card, callback = object : ApiResultCallback<Token> {
                    override fun onSuccess(result: Token) {
                        Kotpref.stripeToken = result.id
                    }

                    override fun onError(e: java.lang.Exception) {
                        Log.e("Token Error", e.toString())
                    }
                })
            }


            regBnkUsrName = editBnkUsrName!!.text.toString().trim()
            regAccount = editRegAccount!!.text.toString().trim()
            regCnfrmAccount = editRegCnfmAccount!!.text.toString().trim()
            regRouting = editRegRout!!.text.toString().trim()
            nameOnCard = edit_name_on_card!!.text.toString().trim()




            if (valueMandetory(applicationContext, regBnkUsrName, editBnkUsrName!!)) {
                return
            }

            if (valueMandetory(applicationContext, regAccount, editRegAccount!!)) {
                return
            }

            if (valueMandetory(applicationContext, regCnfrmAccount, editRegCnfmAccount!!)) {
                return
            }

            if (valueMandetory(applicationContext, nameOnCard, edit_name_on_card!!)) {
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
            if (nameOnCard.toString().trim().isEmpty()) {
                edit_name_on_card?.error = getString(R.string.error_routing)
                edit_name_on_card?.requestFocus()
                return
            }
            registrationPayload3.bankUserName = RSA.encrypt(regBnkUsrName!!)
            registrationPayload3.bankAccountNumber = RSA.encrypt(regAccount!!)
            registrationPayload3.routingNumber = RSA.encrypt(regRouting!!)

            subscriptionPayload.bankUserName = RSA.encrypt(regBnkUsrName!!)
            subscriptionPayload.bankAccountNumber = RSA.encrypt(regAccount!!)
            subscriptionPayload.routingNumber = RSA.encrypt(regRouting!!)


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
            registrationPayload3.bankUserName = RSA.encrypt(regBnkUsrName!!)
            registrationPayload3.bankAccountNumber = RSA.encrypt(regAccount!!)
            registrationPayload3.routingNumber = RSA.encrypt(regRouting!!)

            subscriptionPayload.bankUserName = RSA.encrypt(regBnkUsrName!!)
            subscriptionPayload.bankAccountNumber = RSA.encrypt(regAccount!!)
            subscriptionPayload.routingNumber = RSA.encrypt(regRouting!!)

            Kotpref.isCardUseToPay = false
        }

        if (Kotpref.isRegisterConfirm) {
            onBackPressed()
        } else {
            Util.navigationNext(this, UserConfirmDetailScreen::class.java)
        }
    }

    fun getStripeKey() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.calonex.com/api/")
            // as we are sending data in json format so
            // we have to add Gson converter factory
            .addConverterFactory(ScalarsConverterFactory.create())
            // at last we are building our retrofit builder.
            .build();
        val apiCall = retrofit.create(ApiInterface::class.java)


        val call = apiCall.stripePublicKey()
        call.enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                try {
                    Log.e("Stripe", "Token-=> " + Gson().toJson(response.body()!!))
                    stripeKey = response.body()!!
                    //   socialLoginUserRegisterApi()
                } catch (e: Exception) {
                    e.printStackTrace()
                    SweetAlertDialog(this@UserAccountScreen, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Calonex")
                        .setContentText("Stripe token is not valid")
                        .setConfirmText("Ok")
                        .setConfirmClickListener {
                            finish()
                        }
                        .show()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                SweetAlertDialog(this@UserAccountScreen, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Calonex")
                    .setContentText("Stripe token is not valid")
                    .setConfirmText("Ok")
                    .setConfirmClickListener {
                        finish()
                    }
                    .show()
            }
        })

    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (Kotpref.isRegisterConfirm) {
            Kotpref.isRegisterConfirm = false
        }
        Util.navigationBack(this)
    }
}