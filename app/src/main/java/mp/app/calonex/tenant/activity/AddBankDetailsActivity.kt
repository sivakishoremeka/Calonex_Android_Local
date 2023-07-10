package mp.app.calonex.tenant.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import com.stripe.android.ApiResultCallback
import com.stripe.android.Stripe
import com.stripe.android.model.Token
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_bank_details.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.CreditCardCredentials
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.Kotpref.userId
import mp.app.calonex.common.utility.RSA
import mp.app.calonex.landlord.activity.CxBaseActivity2
import mp.app.calonex.landlord.activity.HomeActivityCx
import mp.app.calonex.tenant.model.apiCredentials.AddBankInfoCredential
import mp.app.calonex.tenant.model.apiCredentials.VerifyBankInfoCredential
import mp.app.calonex.tenant.response.AddBankInfoResponse
import mp.app.calonex.tenant.response.GetBankInfoResponse
import mp.app.calonex.tenant.response.GetCreditCardInfoResponse
import mp.app.calonex.tenant.response.ResponseDtoResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class AddBankDetailsActivity : CxBaseActivity2() {
    var ll_bank_details_note: LinearLayout? = null

    var bankTab = true
    var stripeKey = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_bank_details)
        ll_bank_details_note = findViewById(R.id.ll_bank_details_note);
        cardInputWidget.postalCodeEnabled = false

        /*if (Kotpref.userRole.equals("tenant", true) || Kotpref.userRole.equals("landlord", true)) {
            ll_bank_details_note?.visibility = View.VISIBLE
        } else {
            ll_bank_details_note?.visibility = View.GONE
        }*/

        getBankInfo()
        getCardInfo()
        actionComponent()
        getStripeKey()
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
                    SweetAlertDialog(this@AddBankDetailsActivity, SweetAlertDialog.ERROR_TYPE)
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
                SweetAlertDialog(this@AddBankDetailsActivity, SweetAlertDialog.ERROR_TYPE)
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

    private fun actionComponent() {

        txt_acc.setOnClickListener {
            bankTab = true
            txt_acc.setBackgroundResource(R.drawable.btn_dk_blue_round)
            bank_part.visibility = View.VISIBLE
            txt_card.setBackgroundResource(R.drawable.btn_dk_grey_round)
            card_part.visibility = View.GONE
            //auto_pay_part.visibility = View.VISIBLE
        }

        txt_card.setOnClickListener {
            bankTab = false
            txt_acc.setBackgroundResource(R.drawable.btn_dk_grey_round)
            bank_part.visibility = View.GONE
            txt_card.setBackgroundResource(R.drawable.btn_dk_blue_round)
            card_part.visibility = View.VISIBLE
            //auto_pay_part.visibility = View.GONE
        }

        payment_info_action_done.setOnClickListener {

            if (bankTab) {
                if (edit_bnk_usr_name.text!!.isEmpty()) {
                    edit_bnk_usr_name?.error = getString(R.string.error_mandetory)
                    edit_bnk_usr_name?.requestFocus()
                    return@setOnClickListener
                }

                if (edit_reg_account.text!!.isEmpty()) {
                    edit_reg_account?.error = getString(R.string.error_acc_nmbr)
                    edit_reg_account?.requestFocus()
                    return@setOnClickListener
                }

                if (!edit_reg_account.text.toString()
                        .equals(edit_reg_cnfm_account.text.toString())
                ) {
                    edit_reg_cnfm_account?.error = getString(R.string.error_cnfm_acc_nmbr)
                    edit_reg_cnfm_account?.requestFocus()
                    return@setOnClickListener
                }

                if (edit_reg_routing.text!!.isEmpty() || edit_reg_routing.text!!.length < 9) {
                    edit_reg_routing?.error = getString(R.string.error_routing)
                    edit_reg_routing?.requestFocus()
                    return@setOnClickListener
                }
            } else {
                cardInputWidget.cardParams
                cardInputWidget.cardParams?.let { card ->
                    // Create a Stripe token from the card details
                    val stripe = Stripe(applicationContext, stripeKey)
                    stripe.createCardToken(card, callback = object : ApiResultCallback<Token> {
                        override fun onSuccess(result: Token) {
                            Kotpref.stripeToken = result.id
                        }

                        override fun onError(e: java.lang.Exception) {
                            Log.e("Token Error", e.toString())
                        }
                    })
                }

                if (edit_name_on_card.text!!.isEmpty()) {
                    edit_name_on_card?.error = getString(R.string.error_mandetory)
                    edit_name_on_card?.requestFocus()
                    return@setOnClickListener
                }
            }

            addPaymentInfo()
        }
    }

    private fun getBankInfo() {

        progressBar_bank.visibility = View.VISIBLE
        val bankInfoService: ApiInterface =
            ApiClient(this).provideService(ApiInterface::class.java)
        val apiCall: Observable<GetBankInfoResponse> =
            bankInfoService.getBankInfo(Kotpref.userId) //Test API Key

        RxAPICallHelper().call(apiCall, object : RxAPICallback<GetBankInfoResponse> {
            override fun onSuccess(response: GetBankInfoResponse) {
                progressBar_bank.visibility = View.GONE

                if (response.data != null && response.data!!.accountNo.isNotEmpty()) {
                    tabs_part.visibility = View.GONE
                    //ll_bank_details_note!!.visibility = View.GONE
                    bank_part.visibility = View.VISIBLE
                    card_part.visibility = View.GONE

                    edit_bnk_usr_name!!.setText(RSA.decrypt(response.data!!.userName))
                    edit_reg_account.setText(RSA.decrypt(response.data!!.accountNo))
                    edit_reg_cnfm_account.setText(RSA.decrypt(response.data!!.accountNo))
                    edit_reg_routing.setText(RSA.decrypt(response.data!!.routingNo))
                    payment_info_switch_auto_pay.isChecked = response.data!!.autoPay
                    payment_info_action_done.visibility = View.GONE

                    pay_type_info!!.text = "Your Preferred Payment Option is Bank Account"
                }
            }

            override fun onFailed(t: Throwable) {
                progressBar_bank.visibility = View.GONE
            }
        })
    }


    private fun getCardInfo() {
        progressBar_bank?.visibility = View.VISIBLE

        var credentials: CreditCardCredentials = CreditCardCredentials()
        credentials.userId = Kotpref.userId

        //  progressBar_bank.visibility = View.VISIBLE
        val bankInfoService: ApiInterface =
            ApiClient(this).provideService(ApiInterface::class.java)
        val apiCall: Observable<GetCreditCardInfoResponse> =
            bankInfoService.getCredit(credentials) //Test API Key

        RxAPICallHelper().call(apiCall, object : RxAPICallback<GetCreditCardInfoResponse> {
            override fun onSuccess(response: GetCreditCardInfoResponse) {
                progressBar_bank?.visibility = View.GONE
                if (!response.responseDescription.equals("No card information available")) {
                    if (response.data != null && !response.data!!.last4.isNullOrEmpty()) {

                        tabs_part.visibility = View.GONE
                        bank_part.visibility = View.GONE
                        card_part.visibility = View.VISIBLE
                        til_card_no.visibility = View.VISIBLE
                        edit_card_no.visibility = View.VISIBLE
                        til_card_valid.visibility = View.VISIBLE
                        edit_card_valid.visibility = View.VISIBLE
                        cardInputWidget.visibility = View.GONE

                        //payment_info_switch_auto_pay.isChecked = response.data!!.autoPay
                        payment_info_action_done.visibility = View.GONE

                        pay_type_info!!.setText("Your preferred payment option is Credit Card")
                        if (response.data!!.name.isNullOrEmpty()) {
                            edit_name_on_card!!.setText(response.data!!.name)
                        }
                        edit_card_no!!.setText("""**** **** ****${response.data!!.last4}""")
                        edit_card_valid!!.setText(response.data!!.exp_month + "/" + response.data!!.exp_year)
                    }
                }
            }

            override fun onFailed(t: Throwable) {
                progressBar_bank?.visibility = View.GONE
                //pay_type_info!!.text = ""
            }
        })
    }


    @SuppressLint("CheckResult")
    private fun addPaymentInfo() {
        progressBar_bank.visibility = View.VISIBLE
        val credential = AddBankInfoCredential()

        if (bankTab) {
            credential.userId = userId
            credential.accountNo = RSA.encrypt(edit_reg_account.text.toString())
            credential.routingNo = RSA.encrypt(edit_reg_routing.text.toString())
            credential.userName = RSA.encrypt(edit_bnk_usr_name.text.toString())
            credential.autoPay = payment_info_switch_auto_pay.isChecked
        } else {
            credential.userId = userId
            credential.token = Kotpref.stripeToken
            credential.userName = RSA.encrypt(edit_name_on_card.text.toString())
            credential.isCreditCard = true
            credential.autoPay = payment_info_switch_auto_pay.isChecked
        }

        val paymentInfoApi: ApiInterface =
            ApiClient(this).provideService(ApiInterface::class.java)
        val apiCall: Observable<ResponseDtoResponse> =
            paymentInfoApi.addBankInfo(credential)

        apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
            Log.e("onSuccess", it.responseDto?.responseDescription.toString())

            /*val intent = Intent(this,   VerifyAccountDetailsActivity::class.java)
                startActivity(intent)*/
            if (bankTab) {
                verifyPaymentInfo()
            } else {
                if (Kotpref.userRole.contains("CX-Tenant", true)) {
                    Kotpref.bankAccountVerified = true
                    if (bankTab) {
                        Toast.makeText(this, "Bank account verified", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(
                            this,
                            "Credit Card added successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    startActivity(Intent(this, HomeActivityTenant::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Bank account verified", Toast.LENGTH_SHORT).show()
                    Kotpref.bankAccountVerified = true
                    startActivity(Intent(this, HomeActivityCx::class.java))
                    finish()
                }
            }


            /*  val intent =  Intent(this, VerifyAccountDetailsActivity::class.java)
              startActivity(intent)*/

        },
            { e ->

                Log.e("onFailure", e.toString())
                e.message?.let {
                    Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                }
            })

    }

    @SuppressLint("CheckResult")
    private fun verifyPaymentInfo() {
        progressBar_bank.visibility = View.VISIBLE
        val credential = VerifyBankInfoCredential()
        credential.userCatelogId = Kotpref.userId
        credential.firstamount = "32"
        credential.secondamount = "45"
        Log.d("Bank_I_LOG_1 user ", "id verify account details activity " + Kotpref.userId)

        Log.e("Bank_I_LOG_1", Gson().toJson(credential))
        val paymentInfoApi: ApiInterface = ApiClient(this).provideService(ApiInterface::class.java)
        val apiCall: Observable<AddBankInfoResponse> = paymentInfoApi.verifyBankInfo(credential)

        apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
            Log.e("Bank_O_LOG_1", Gson().toJson(it.responseDto))
            Log.e("onSuccess", it.responseDto?.responseDescription.toString())
            progressBar_bank.visibility = View.GONE

            if (it.responseDto?.responseCode == 400) {

                val sayWindows = AlertDialog.Builder(this)
                sayWindows.setMessage(it.responseDto?.message)
                sayWindows.setPositiveButton("ok", null)

                val mAlertDialog = sayWindows.create()
                mAlertDialog.setOnShowListener {
                    val b: Button = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    b.setOnClickListener(View.OnClickListener {
                        mAlertDialog.dismiss()
                    })
                }
                mAlertDialog.show()


                /* val intent = Intent(this, SignatureUploadActivity::class.java)
                 startActivity(intent)*/

            } else if (it.responseDto?.responseCode == 201) {
                if (Kotpref.userRole.contains("Landlord", true)) {
                    Toast.makeText(this, "Bank account verified", Toast.LENGTH_SHORT).show()
                    Kotpref.bankAccountVerified = true
                    startActivity(Intent(this, HomeActivityCx::class.java))
                    finish()
                } else if (it.responseDto?.responseCode == 201) {
                    if (Kotpref.userRole.contains("CX-Tenant", true)) {
                        Kotpref.bankAccountVerified = true
                        Toast.makeText(this, "Bank account verified", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, HomeActivityTenant::class.java))
                        finish()
                    }
                } else {
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage(it.responseDto?.message)
                    builder.setPositiveButton("ok",
                        DialogInterface.OnClickListener { dialog, id ->
                            val intent = Intent(this, SignatureUploadActivity::class.java)
                            startActivity(intent)
                        })
                    builder.show()
                }
            }
        },
            { e ->
                progressBar_bank.visibility = View.GONE
                Log.e("onFailure", e.toString())
                e.message?.let {
                    Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                }
            })
    }
}
