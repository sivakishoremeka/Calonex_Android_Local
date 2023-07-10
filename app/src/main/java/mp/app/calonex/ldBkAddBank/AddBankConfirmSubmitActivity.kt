package mp.app.calonex.ldBkAddBank

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_bank_confirm_submit.*
import mp.app.calonex.R
import mp.app.calonex.agent.fragment.ProfileFragmentAgent
import mp.app.calonex.broker.activity.HomeActivityBroker
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.CxBaseActivity2
import mp.app.calonex.landlord.activity.HomeActivityCx
import mp.app.calonex.landlord.activity.LoginScreen.Companion.registrationPayload3
import mp.app.calonex.landlord.fragment.ProfileLdFragment
import mp.app.calonex.registration.adapter.SubscriptionPlanAdapter
import mp.app.calonex.registration.model.RegistrationPropertyPayload
import mp.app.calonex.registration.response.UserRegistrationResponse3
import mp.app.calonex.tenant.activity.HomeActivityTenant
import mp.app.calonex.tenant.model.apiCredentials.VerifyBankInfoCredential
import mp.app.calonex.tenant.response.AddBankInfoResponse
import mp.app.calonex.utility.StripePiiResponse
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class AddBankConfirmSubmitActivity : CxBaseActivity2() {
    private var layoutContactInfo: LinearLayout? = null
    private var layoutAccountInfo: LinearLayout? = null
    private var btnConfirmSign: Button? = null
    private var isBtnCnfrm: Boolean? = false

    private var isBroker: Boolean = false

    private var count: Int = 0
    private var pb_reg_confirm: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_bank_confirm_submit)

        if (Kotpref.userRole.contains("Broker", true)) {
            //registrationPayload3.userRoleName = "CX-Broker"
            //
            registrationPayload3.userRoleName = "CX-Broker"
            registrationPayload3.userId = Kotpref.userId
            //
            registrationPayload3.firstName = ProfileFragmentAgent.userDetailResponse.firstName
            registrationPayload3.lastName = ProfileFragmentAgent.userDetailResponse.lastName
            registrationPayload3.signupThroughInvite = true
            isBroker = true
        } else {
            registrationPayload3.userRoleName = "CX-Landlord"
            registrationPayload3.firstName = ProfileLdFragment.userDetailResponse.firstName
            registrationPayload3.lastName = ProfileLdFragment.userDetailResponse.lastName
            isBroker = false
        }

        registrationPayload3.emailId = Kotpref.emailId

        if (Kotpref.isCardUseToPay) {
            SubscriptionPlanAdapter.subscriptionPayload.token = Kotpref.stripeToken
        }
        //Log.e("PAYMENT_LOG", Gson().toJson(subscriptionPayload))
        initComponent()
        actionComponent()
        startHandler()
    }

    fun initComponent() {
        pb_reg_confirm = findViewById(R.id.pb_reg_confirm);
        layoutContactInfo = findViewById(R.id.layout_contact_info)
        layoutAccountInfo = findViewById(R.id.layout_acc_info)
        btnConfirmSign = findViewById(R.id.btn_cnfirm_sign)
    }

    fun actionComponent() {
        layoutContactInfo!!.setOnClickListener {
            Kotpref.isRegisterConfirm = true
            Util.navigationNext(this, AddBankPersonalInfoActivity::class.java)
        }

        layoutAccountInfo!!.setOnClickListener {
            Kotpref.isRegisterConfirm = true
            Util.navigationNext(this, AddBankAccountInfoActivity::class.java)
        }

        btnConfirmSign!!.setOnClickListener {
            isBtnCnfrm = true
            StripeKey()

        }

        header_back.setOnClickListener {
            super.onBackPressed()
            // Util.navigationBack(this)
            Util.navigationBack(this)
        }
    }

    fun StripePiiCreation(key: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.stripe.com/")
            // as we are sending data in json format so
            // we have to add Gson converter factory
            .addConverterFactory(GsonConverterFactory.create())
            // at last we are building our retrofit builder.
            .build();
        val apiCall = retrofit.create(ApiInterface::class.java)


        val call = apiCall.piiToken("Bearer " + key, registrationPayload3.ssn_token!!)
        call.enqueue(object : Callback<StripePiiResponse> {
            override fun onResponse(
                call: Call<StripePiiResponse>,
                response: Response<StripePiiResponse>
            ) {
                Log.e("response stripe", response.body().toString())
                try {
                    registrationPayload3.ssn_token = response.body()!!.id
                    socialLoginUserRegisterApi()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            override fun onFailure(call: Call<StripePiiResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })

    }

    fun StripeKey() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.calonex.com/api/")
            // as we are sending data in json format so
            // we have to add Gson converter factory
            .addConverterFactory(ScalarsConverterFactory.create())
            // at last we are building our retrofit builder.
            .build();
        val apiCall = retrofit.create(ApiInterface::class.java)


        val call = apiCall.stripeKey()
        call.enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                Log.e("Stripe", "Token-=> " + Gson().toJson(response.body()!!))

                StripePiiCreation(response.body()!!)
                //   socialLoginUserRegisterApi()

            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })

    }

    /* private fun createPlanSubcription() {

         try {
             if (NetworkConnection.isNetworkConnected(applicationContext)) {
                 pb_reg_confirm!!.visibility = View.VISIBLE
                 getWindow().setFlags(
                     WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                     WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                 )

                 //Log.e("Final_I_LOG_1", Gson().toJson(subscriptionPayload))
                 SubscriptionPlanAdapter.subscriptionPayload.userCatalogId = Kotpref.userId


                 val signatureApi: ApiInterface =
                     ApiClient(this).provideService(ApiInterface::class.java)
                 val apiCall: Observable<SelectPlanResponse2> =
                     signatureApi.createSubscription(SubscriptionPlanAdapter.subscriptionPayload)

                 apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                     .subscribe({
                         //Log.e("Final_O_LOG_1", Gson().toJson(it.responseDto))
                         Log.e("onSuccess", it.responseDto?.responseDescription.toString())

                         if (it!!.responseDto!!.responseCode.equals(201)) {

                             if (it.data != null) {
                                 LoginScreen.registrationPayload3.subscriptionDetailsId =
                                     it.data!!.subscriptionDetailsId
                                 LoginScreen.registrationPayload3.stripeCustomerId = it.data!!.stripeCustomerId
                                 //  registerApi()
                                 socialLoginUserRegisterApi()


 *//*
                                try {
                                    Kotpref.loginRole = "landlord"
                                    Kotpref.isLogin = true
                                    Kotpref.bankAccountVerified = true
                                    Kotpref.subscriptionActive = true
                                    Util.navigationNext(this, HomeActivityCx::class.java)
                                    finish()
                                } catch (e: Exception) {
                                    e.printStackTrace()

                                    Toast.makeText(
                                        applicationContext,
                                        " Something went wrong",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }
*//*



                            }
                        } else {
                            pb_reg_confirm!!.visibility = View.GONE
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            Toast.makeText(
                                applicationContext,
                                it!!.responseDto!!.responseDescription,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                        { e ->
                            Log.e("onFailure", e.toString())
                            pb_reg_confirm!!.visibility = View.GONE
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            val exception: HttpException = e as HttpException
                            if (exception.code().equals(409)) {
                                Toast.makeText(
                                    applicationContext,
                                    getString(R.string.error_bank_detail),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                e.message?.let {
                                    Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        })
            } else {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.error_network),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }*/

    override fun onBackPressed() {
        super.onBackPressed()
        if (!isBtnCnfrm!!) {
            Util.navigationBack(this)
        }
    }


    @SuppressLint("CheckResult")
    private fun socialLoginUserRegisterApi() {
        try {
            if (NetworkConnection.isNetworkConnected(applicationContext)) {
                //Create retrofit Service
                pb_reg_confirm!!.visibility = View.VISIBLE
                getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )

                registrationPayload3.userId = Kotpref.userId

                var registrationPropertyPayload = RegistrationPropertyPayload()
                val gson = Gson()
                val valueString = gson.toJson(registrationPropertyPayload)
                val jsonObject: JsonObject = JsonParser().parse(valueString).getAsJsonObject()
                registrationPayload3.userPropertyRegisterDto = jsonObject

                //Log.e("Final_I_LOG_2", Gson().toJson(registrationPayload3))
                val registerService: ApiInterface =
                    ApiClient(this).provideService(ApiInterface::class.java)
                val apiCall: Observable<UserRegistrationResponse3> =
                    registerService.setupUserSocial(registrationPayload3)

                apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        //Log.e("Final_O_LOG_2", Gson().toJson(it))
                        Log.e("onSuccess", it.responseCode.toString())

                        if (it!!.status == 200) {
                            Toast.makeText(
                                applicationContext,
                                it.message + "",
                                Toast.LENGTH_SHORT
                            ).show()

                            navigateScreens()
                            // verifyPaymentInfo()

                        } else {
                            pb_reg_confirm!!.visibility = View.GONE
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            Toast.makeText(
                                applicationContext,
                                it.responseDescription + "",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                        { e ->
                            Log.e("onFailure", e.toString())
                            pb_reg_confirm!!.visibility = View.GONE
                            isBtnCnfrm = false
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            val exception: HttpException = e as HttpException
                            if (exception.code().equals(400)) {
                                Toast.makeText(
                                    applicationContext,
                                    e.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()

                            } else {
                                e.message?.let {
                                    Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        })
            } else {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.error_network),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun navigateScreens() {
        try {
            if (isBroker) {
                Kotpref.bankAdded = true
                //Kotpref.bankAccountVerified = true
                Util.navigationNext(
                    this@AddBankConfirmSubmitActivity,
                    HomeActivityBroker::class.java
                )
                finish()
            } else if (Kotpref.userRole.equals("tanent", true)) {
                Kotpref.bankAdded = true
                //Kotpref.bankAccountVerified = true
                Util.navigationNext(
                    this@AddBankConfirmSubmitActivity,
                    HomeActivityTenant::class.java
                )
                finish()
            } else {
                Kotpref.bankAdded = true
                //Kotpref.bankAccountVerified = true
                Util.navigationNext(this@AddBankConfirmSubmitActivity, HomeActivityCx::class.java)
                finish()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    @SuppressLint("CheckResult")
    private fun verifyPaymentInfo() {
        pb_reg_confirm!!.visibility = View.VISIBLE
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
            pb_reg_confirm!!.visibility = View.GONE

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
                /*if (Kotpref.userRole.contains("Landlord", true)) {
                    Toast.makeText(this, "Bank account verified", Toast.LENGTH_SHORT).show()
                    Kotpref.bankAccountVerified=true
                    startActivity(Intent(this, HomeActivityCx::class.java))
                    finish()
                }
                else if (it.responseDto?.responseCode == 201) {
                    if (Kotpref.userRole.contains("CX-Tenant", true)) {
                        Kotpref.bankAccountVerified=true
                        Toast.makeText(this, "Bank account verified", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, HomeActivityTenant::class.java))
                        finish()
                    }
                }*/

                if (isBroker) {
                    Kotpref.bankAdded = true
                    Kotpref.bankAccountVerified = true
                    Util.navigationNext(
                        this@AddBankConfirmSubmitActivity,
                        HomeActivityBroker::class.java
                    )
                    finish()
                } else {
                    Kotpref.bankAdded = true
                    Kotpref.bankAccountVerified = true
                    Util.navigationNext(
                        this@AddBankConfirmSubmitActivity,
                        HomeActivityCx::class.java
                    )
                    finish()
                }
            }
        },
            { e ->
                pb_reg_confirm!!.visibility = View.GONE
                Log.e("onFailure", e.toString())
                e.message?.let {
                    Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                }
            })
    }
}