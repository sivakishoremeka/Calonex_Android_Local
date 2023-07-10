package mp.app.calonex.rentcx

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import com.stripe.android.ApiResultCallback
import com.stripe.android.Stripe
import com.stripe.android.model.Token
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import mp.app.calonex.R
import kotlinx.android.synthetic.main.activity_market_place_payment.*
import mp.app.calonex.common.constant.StatusConstant
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.landlord.activity.CxBaseActivity2
import mp.app.calonex.rentcx.MarketplaceDetails2Activity.Companion.ApplicationFeesResponceData
import mp.app.calonex.rentcx.MarketplaceDetails2Activity.Companion.leaseid
import mp.app.calonex.rentcx.MarketplaceDetails2Activity.Companion.unitid
import mp.app.calonex.tenant.model.stripe.StripeKeyResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class MarketPlacePaymentActivity : CxBaseActivity2() {

    var stripeKey = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_market_place_payment)

        startHandler()

        val service_fee: Float = ApplicationFeesResponceData.total!!.toFloat() - ApplicationFeesResponceData.applicationfee!!.toFloat()
        application_fee_text.text = "$" + ApplicationFeesResponceData.applicationfee
        service_fee_text.text = "$" + service_fee
        total_fee_text.text = "$" + ApplicationFeesResponceData.total

        cardInputWidget.postalCodeEnabled=false

        getStripeKey()

        /*
        cardInputWidget.card?.let { card ->
            // Create a Stripe token from the card details
            val stripe = Stripe(applicationContext, "pk_test_51HxhDBKJ1orMwPHntkQXg8gw9lDCsthnwshMLWHQxZuq6qzmYGrjqhTFtXrehaXjt4jb0sqTLF1YMdhIqZCtNiU700y8yovFv3")
            stripe.createCardToken(card, callback = object: ApiResultCallback<Token> {
                override fun onSuccess(result: Token) {
                    Kotpref.rentcxStripeToken = result.id
                }

                override fun onError(e: java.lang.Exception) {
                    Log.e("Token Error", e.toString())
                }
            })
        }*/

        header_back.setOnClickListener {
            finish()
        }

        property_pay_now_text.setOnClickListener {

            cardInputWidget.card?.let { card ->
                // Create a Stripe token from the card details
                val stripe = Stripe(applicationContext, stripeKey)
                stripe.createCardToken(card, callback = object: ApiResultCallback<Token> {
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
                    SweetAlertDialog(this@MarketPlacePaymentActivity, SweetAlertDialog.ERROR_TYPE)
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
                SweetAlertDialog(this@MarketPlacePaymentActivity, SweetAlertDialog.ERROR_TYPE)
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
    @SuppressLint("CheckResult")
    private fun makePayment() {
        val credentials = MarketplaceMakePayRequestModel()

        credentials.token = Kotpref.rentcxStripeToken
        credentials.total = ApplicationFeesResponceData.total
        credentials.leaseId = leaseid
        credentials.unitId = unitid
        credentials.email = Kotpref.emailId
        credentials.userId = Kotpref.userId

        val tenantAddUpdateCall: ApiInterface =
            ApiClient2(this).provideService(ApiInterface::class.java)
        val apiCall: Observable<MarketplaceMakePayResponce> =
            tenantAddUpdateCall.makePaymentMarketplace(credentials)

        apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.message != null) {
                    startActivity(Intent(this@MarketPlacePaymentActivity, MarketplaceSignatureUploadActivity::class.java))
                }


            },
                { e ->
                    Toast.makeText(applicationContext, "Fail", Toast.LENGTH_SHORT).show()
                    Log.e("error", e.message.toString())
                })
    }

}