package mp.app.calonex.tenant.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.stripe.android.ApiResultCallback
import com.stripe.android.Stripe
import com.stripe.android.model.Token
import com.stripe.android.view.CardInputWidget
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_tenant_payment_setting.*
import mp.app.calonex.R
import mp.app.calonex.common.network.*
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.RSA
import mp.app.calonex.landlord.activity.CxBaseActivity2
import mp.app.calonex.tenant.model.apiCredentials.AddBankInfoCredential
import mp.app.calonex.tenant.response.GetBankInfoResponse
import mp.app.calonex.tenant.response.ResponseDtoResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class TenantPaymentSettingActivity : CxBaseActivity2() {

    private var headerBack: ImageView? = null

    private var txtAcc: TextView? = null
    private var txtCard: TextView? = null
    private var cardPayFlag: Boolean = false
    private lateinit var bank_part: LinearLayout
    private var edit_name_on_card: TextInputEditText? = null
    private lateinit var cardInputWidget: CardInputWidget

    var stripeKey = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tenant_payment_setting)

        getBankInfo()
        headerBack = findViewById(R.id.header_back)
        edit_name_on_card = findViewById(R.id.edit_name_on_card);
        txtAcc = findViewById(R.id.txt_acc)
        txtCard = findViewById(R.id.txt_card)
        bank_part = findViewById(R.id.bank_part)
        cardInputWidget = findViewById(R.id.cardInputWidget)
        cardInputWidget.postalCodeEnabled = false

        actionComponent()

    }

    private fun getBankInfo() {

        pb_payment_setting.visibility = View.VISIBLE
        val bankInfoService: ApiInterface =
            ApiClient(this).provideService(ApiInterface::class.java)
        val apiCall: Observable<GetBankInfoResponse> =
            bankInfoService.getBankInfo(Kotpref.userId) //Test API Key

        RxAPICallHelper().call(apiCall, object : RxAPICallback<GetBankInfoResponse> {
            override fun onSuccess(response: GetBankInfoResponse) {
                pb_payment_setting.visibility = View.GONE

                payment_setting_name!!.setText(RSA.decrypt(response.data!!.userName))

//                payment_setting_name.setText(response.data!!.userName)
                payment_setting_account_no.setText(RSA.decrypt(response.data!!.accountNo))
                payment_setting_routing_no.setText(RSA.decrypt(response.data!!.routingNo))
                payment_setting_switch_auto_pay.isChecked = response.data!!.autoPay

            }

            override fun onFailed(t: Throwable) {
                pb_payment_setting.visibility = View.GONE
            }
        })


    }

    private fun actionComponent() {
        headerBack?.setOnClickListener {
            super.onBackPressed()
        }

        txtAcc!!.setOnClickListener {
            cardPayFlag = false

            bank_part.visibility = View.VISIBLE

            cardInputWidget.visibility = View.GONE
            til_name.visibility = View.GONE

            txtAcc?.setBackgroundResource(R.drawable.btn_dk_blue_round)
            txtCard?.setBackgroundResource(R.drawable.btn_dk_grey_round)

            //Log.e("PAYMENT_ACC_LOG", cardPayFlag.toString())
        }
        getStripeKey()

        txtCard!!.setOnClickListener {
            cardPayFlag = true

            bank_part.visibility = View.GONE

            cardInputWidget.visibility = View.VISIBLE
            til_name.visibility = View.VISIBLE


            txtAcc?.setBackgroundResource(R.drawable.btn_dk_grey_round)
            txtCard?.setBackgroundResource(R.drawable.btn_dk_blue_round)

            //Log.e("PAYMENT_CARD_LOG", cardPayFlag.toString())
        }

        payment_setting_update.setOnClickListener {
            validComponent()
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
                    SweetAlertDialog(this@TenantPaymentSettingActivity, SweetAlertDialog.ERROR_TYPE)
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
                SweetAlertDialog(this@TenantPaymentSettingActivity, SweetAlertDialog.ERROR_TYPE)
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

    fun validComponent() {
        val credential = AddBankInfoCredential()
        credential.userId = Kotpref.userId
        credential.autoPay = payment_setting_switch_auto_pay.isChecked
        credential.userName = RSA.encrypt(payment_setting_name.text.toString())


        if (cardPayFlag) {

            if (edit_name_on_card?.text.isNullOrEmpty()) {
                edit_name_on_card?.error = getString(R.string.error_mandetory)
                return
            }


            cardInputWidget.card?.let { card ->
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

            // Kotpref.isCardUseToPay = true
            credential.token = Kotpref.stripeToken
            credential.isCreditCard = true
            credential.userName = RSA.encrypt(edit_name_on_card?.text.toString())
        } else {
            credential.isCreditCard = false
            if (payment_setting_name.text.isNullOrEmpty()) {
                payment_setting_name.error = getString(R.string.error_mandetory)
                return
            }

            if (payment_setting_account_no.text.isNullOrEmpty()) {
                payment_setting_account_no.error = getString(R.string.error_acc_nmbr)
                return
            }

            if (payment_setting_routing_no.text.isNullOrEmpty() && payment_setting_routing_no.text!!.length < 8) {
                payment_setting_routing_no.error = getString(R.string.error_routing)
                return
            }
            pb_payment_setting.visibility = View.VISIBLE

            credential.accountNo = RSA.encrypt(payment_setting_account_no.text.toString())
            credential.routingNo = RSA.encrypt(payment_setting_routing_no.text.toString())
            credential.userName = RSA.encrypt(payment_setting_name.text.toString())
            credential.isCreditCard = false
        }

        val bankInfoService: ApiInterface =
            ApiClient(this).provideService(ApiInterface::class.java)
        val apiCall: Observable<ResponseDtoResponse> =
            bankInfoService.addBankInfo(credential) //Test API Key

        RxAPICallHelper().call(apiCall, object : RxAPICallback<ResponseDtoResponse> {
            override fun onSuccess(response: ResponseDtoResponse) {
                pb_payment_setting.visibility = View.GONE

                if (response.responseDto?.responseCode == 200 || response.responseDto?.responseCode == 201) {

                    val builder = AlertDialog.Builder(this@TenantPaymentSettingActivity)

                    builder.setTitle("Success")

                    // Display a message on alert dialog
                    builder.setMessage(response.responseDto?.responseDescription)

                    // Set a positive button and its click listener on alert dialog
                    builder.setPositiveButton("Ok") { dialog, which ->


                        Kotpref.bankAdded = true
                        Kotpref.bankAccountVerified = true
                        val intent = Intent(
                            this@TenantPaymentSettingActivity,
                            HomeActivityTenant::class.java

                        )
                        startActivity(intent)
                        dialog.dismiss()
                    }

                    val dialog: AlertDialog = builder.create()
                    dialog.show()

                }
            }

            override fun onFailed(t: Throwable) {
                pb_payment_setting.visibility = View.GONE
            }
        })
    }
}
