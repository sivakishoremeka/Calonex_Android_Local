package mp.app.calonex.billpay

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import com.stripe.android.ApiResultCallback
import com.stripe.android.Stripe
import com.stripe.android.model.Token
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_card_and_bank_info.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.UserDetailCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.RSA
import mp.app.calonex.landlord.activity.AccountLinkDetailScreen
import mp.app.calonex.rentcx.MarketplaceMakePayResponceNewDto
import mp.app.calonex.rentcx.UserDetailsResponce
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class CardAndBankInfoActivity : AppCompatActivity() {
    var leaseId: String? = null
    var invoiceId: String? = null
    var amountTobepaid: Double? = null
    var tanentId: String? = null
    var stripeKey = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_and_bank_info)

        var extras = intent.extras

        if (extras != null) {
            invoiceId = extras.getString("invoiceId", "")
            leaseId = extras.getString("leaseId", "")
            tanentId = extras.getString("tanentId", "")
            amountTobepaid = extras.getString("amount", "").toDouble()

            tobepaid.text = "Amount to be paid:" + amountTobepaid.toString()
            payingfor.text = "Paying for :" + invoiceId
            selector(0)
        }
        iv_back.setOnClickListener {
            finish()
        }

        getStripeKey()
        getUserDetails()

        lease_selector.setOnClickListener {
            selector(0)
            ccview.visibility = View.VISIBLE
            bankview.visibility = View.GONE
        }
        application_selector.setOnClickListener {
            selector(1)
            ccview.visibility = View.GONE
            bankview.visibility = View.VISIBLE
        }

        cardInputWidget.postalCodeEnabled = false


        property_pay_now_text.setOnClickListener {
            cardInputWidget.card?.let { card ->
                // Create a Stripe token from the card details
                val stripe = Stripe(applicationContext, stripeKey)
                stripe.createCardToken(card, callback = object : ApiResultCallback<Token> {
                    override fun onSuccess(result: Token) {
                        Kotpref.rentcxStripeToken = result.id
                        makePayment()
                    }

                    override fun onError(e: java.lang.Exception) {
                        Log.e("Token Error", e.toString())
                    }
                })
            }

        }

        property_pay_now_text1.setOnClickListener {
            var accNo = acc_number.text.toString().trim()
            var reAccNo = re_acc_number.text.toString().trim()
            var routingNo = routing_number.text.toString().trim()
            var name = name1.text.toString().trim()

            if (name.isEmpty()) {
                name1.error = "Write your name"
            } else if (accNo.isEmpty()) {
                acc_number.error = "Invalid account number"
            } else if (reAccNo.isEmpty()) {
                re_acc_number.error = "Invalid re-account number"
            } else if (reAccNo != accNo) {
                re_acc_number.error = "Invalid re-account number"
            } else if (routingNo.isEmpty()) {
                routing_number.error = "Routing number is empty"
            } else {
                makePaymentByBank()
            }
        }

    }

    @SuppressLint("CheckResult")
    private fun getUserDetails() {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            val credentials = UserDetailCredential()
            credentials.userId = Kotpref.userId
            Log.e("ASDF", Kotpref.userId)
            val validateService: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<UserDetailsResponce> =
                validateService.getMarketplaceUserDetails(credentials)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e(
                        "onSuccess",
                        "User Details ==>" + Gson().toJson(it)
                    )
                    if (it.responseDto!!.responseCode == 200) {
                        UserDetailsResponceData = it
                        checkAccountPref(it.data!!.stripAccountVerified!!)
                        it.data!!.stripAccountVerified?.let { sa ->
                            Kotpref.bankAccountVerified = sa
                        }
                    }
                },
                    { e ->
                        Log.e("onFailure", e.toString())
                        e.message?.let {
                            Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                        }
                    })
        } else {
            Toast.makeText(
                applicationContext,
                getString(R.string.error_network),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        var UserDetailsResponceData = UserDetailsResponce()
    }

    private fun checkAccountPref(stripAccountVerified: Boolean) {
        try {
            if (!UserDetailsResponceData.data!!.bankAccountNo.equals("") && UserDetailsResponceData.data?.bankAccountNo!! != null) {
                acc_number.setText(UserDetailsResponceData.data?.bankAccountNo?.let {
                    RSA.decrypt(
                        it
                    )
                })
                re_acc_number.setText(UserDetailsResponceData.data?.bankAccountNo?.let {
                    RSA.decrypt(
                        it
                    )
                })
                name1.setText(UserDetailsResponceData.data?.bankUserName?.let {
                    it
                })
                routing_number.setText(UserDetailsResponceData.data?.routingNo?.let {
                    RSA.decrypt(
                        it
                    )
                })

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun makePaymentByBank() {
        val credentials = PayInvoiceByBank()
        credentials.amount = amountTobepaid.toString()
        credentials.isCreditCard = false
        credentials.invoiceId = invoiceId
        credentials.tenantId = tanentId
        credentials.bankAccountNumber = RSA.encrypt(
            acc_number.text.toString().trim()
        )
        credentials.routingNumber = RSA.encrypt(
            routing_number.text.toString().trim()
        )
        credentials.bankUserName = name1.text.toString()

        payment_progress.visibility = View.VISIBLE
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        val tenantAddUpdateCall: ApiInterface =
            ApiClient(this).provideService(ApiInterface::class.java)
        val apiCall: Observable<MarketplaceMakePayResponceNewDto> =
            tenantAddUpdateCall.payRentByInvoice(credentials)

        apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                this.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                payment_progress.visibility = View.GONE

                if (it.responseDto!!.responseCode == 200 || it.responseDto!!.responseCode == 201) {
                    SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Calonex")
                        .setContentText("" + it.responseDto!!.responseDescription)
                        .setConfirmText("Ok")
                        .setConfirmClickListener {
                            finish()
                        }
                        .show()
                } else {
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Calonex")
                        .setContentText("" + it.responseDto!!.responseDescription)
                        .setConfirmText("Ok")
                        .setConfirmClickListener {
                            finish()
                        }
                        .show()
                }
            },
                { e ->
                    this.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    payment_progress.visibility = View.GONE
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Calonex")
                        .setContentText("Something went wrong!")
                        .setConfirmText("Ok")
                        .setConfirmClickListener {
                            finish()
                        }
                        .show()
                    // Toast.makeText(applicationContext, "Fail", Toast.LENGTH_SHORT).show()
                    Log.e("error", e.message.toString())
                })

    }

    var isBank = false
    private fun selector(status: Int) {
        if (status == 0) {
            isBank = false
            cardSelected()
            bankUnSelected()
        } else {
            isBank = true
            bankSelected()
            cardUnSelected()
        }

    }

    private fun cardSelected() {
        lease_selector?.setBackgroundResource(R.drawable.bg_card_white)
        lease_text!!.setTextColor((Color.parseColor("#0874EA")))

    }

    private fun cardUnSelected() {
        lease_selector?.setBackgroundResource(R.drawable.white_border)
        lease_text!!.setTextColor((Color.parseColor("#FFFFFF")))
    }

    private fun bankSelected() {
        application_selector?.setBackgroundResource(R.drawable.bg_card_white)
        application_text!!.setTextColor((Color.parseColor("#0874EA")))
    }

    private fun bankUnSelected() {
        application_selector?.setBackgroundResource(R.drawable.white_border)
        application_text!!.setTextColor((Color.parseColor("#FFFFFF")))
    }

    private fun makePayment() {
        val credentials = PayInvoiceBo()

        credentials.token = Kotpref.rentcxStripeToken
        credentials.amount = amountTobepaid.toString()
        //credentials.leaseId = leaseId
        credentials.isCreditCard = true
        credentials.tenantId = tanentId
        credentials.invoiceId = invoiceId

        payment_progress.visibility = View.VISIBLE
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        val tenantAddUpdateCall: ApiInterface =
            ApiClient(this).provideService(ApiInterface::class.java)
        val apiCall: Observable<MarketplaceMakePayResponceNewDto> =
            tenantAddUpdateCall.payRentByInvoice(credentials)

        apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                this.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                payment_progress.visibility = View.GONE

                if (it.responseDto!!.responseCode == 200 || it.responseDto!!.responseCode == 201) {
                    SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Calonex")
                        .setContentText("" + it.responseDto!!.responseDescription)
                        .setConfirmText("Ok")
                        .setConfirmClickListener {
                            finish()
                        }
                        .show()
                } else {
                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Calonex")
                        .setContentText("" + it.responseDto!!.responseDescription)
                        .setConfirmText("Ok")
                        .setConfirmClickListener {
                            finish()
                        }
                        .show()
                }
            },
                { e ->

                    this.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                    payment_progress.visibility = View.GONE

                    SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Calonex")
                        .setContentText("Something went wrong!")
                        .setConfirmText("Ok")
                        .setConfirmClickListener {
                            finish()
                        }
                        .show()
                    // Toast.makeText(applicationContext, "Fail", Toast.LENGTH_SHORT).show()
                    Log.e("error", e.message.toString())
                })
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
                    SweetAlertDialog(this@CardAndBankInfoActivity, SweetAlertDialog.ERROR_TYPE)
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
                SweetAlertDialog(this@CardAndBankInfoActivity, SweetAlertDialog.ERROR_TYPE)
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
}