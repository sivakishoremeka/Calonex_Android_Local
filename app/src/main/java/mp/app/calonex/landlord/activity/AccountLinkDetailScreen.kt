package mp.app.calonex.landlord.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_link_account_detail_screen.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.CreditCardCredentials
import mp.app.calonex.common.apiCredentials.UserDetailCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.*
import mp.app.calonex.common.utility.Util.navigationBack
import mp.app.calonex.ldBkAddBank.AddBankPersonalInfoActivity
import mp.app.calonex.rentcx.UserDetailsResponce
import mp.app.calonex.tenant.model.apiCredentials.VerifyBankInfoCredential
import mp.app.calonex.tenant.response.AddBankInfoResponse
import mp.app.calonex.tenant.response.GetCreditCardInfoResponse


class AccountLinkDetailScreen : CxBaseActivity2() {
    var isBroker = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_link_account_detail_screen)
        initComponent()
        actionComponent()
        startHandler()
    }

    private fun initComponent() {
        isBroker = Kotpref.userRole.contains("Broker", true)
    }

    private fun actionComponent() {
        header_back.setOnClickListener {
            super.onBackPressed()
            // Util.navigationBack(this)
            navigationBack(this)
        }

        tv_payable_configure.setOnClickListener {
            Kotpref.isRegisterConfirm = false
            startActivity(
                Intent(
                    this@AccountLinkDetailScreen,
                    AddBankPersonalInfoActivity::class.java
                )
            )
        }

        tv_receivable_configure.setOnClickListener {
            if (tv_receivable_configure.text.toString() == "View Details") {
                /*startActivity(
                    Intent(
                        this@AccountLinkDetailScreen,
                        AddBankPersonalInfoActivity::class.java
                    )
                )*/
            } else {
                Kotpref.isRegisterConfirm = false
                startActivity(
                    Intent(
                        this@AccountLinkDetailScreen,
                        AddBankPersonalInfoActivity::class.java
                    )
                )
            }
        }

        tv_submit.setOnClickListener {
            if (validPaymentEntry()) {
                verifyPaymentInfo()
            }
        }

    }

    private fun validPaymentEntry(): Boolean {
        if (Util.valueMandetory(
                this,
                edit_first_amount.text.toString().trim(),
                edit_first_amount
            )
        ) {
            return false
        }

        if (Util.valueMandetory(
                this,
                edit_second_amount.text.toString().trim(),
                edit_second_amount
            )
        ) {
            return false
        }

        return true
    }


    override fun onBackPressed() {
        super.onBackPressed()
        navigationBack(this)
    }

    private fun getCardInfo() {
        var credentials = CreditCardCredentials()
        credentials.userId = Kotpref.userId

        //  progressBar_bank.visibility = View.VISIBLE
        val bankInfoService: ApiInterface =
            ApiClient(this).provideService(ApiInterface::class.java)
        val apiCall: Observable<GetCreditCardInfoResponse> =
            bankInfoService.getCredit(credentials) //Test API Key

        RxAPICallHelper().call(apiCall, object : RxAPICallback<GetCreditCardInfoResponse> {
            override fun onSuccess(response: GetCreditCardInfoResponse) {

                if (!response.responseDescription.equals("No card information available")) {
                    if (response.data != null && !response.data!!.last4.isNullOrEmpty()) {


                    }
                }
            }

            override fun onFailed(t: Throwable) {

            }
        })
    }

    override fun onResume() {
        super.onResume()
        getUserDetails()
        getCardInfo()
    }


    @SuppressLint("CheckResult")
    private fun getUserDetails() {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            pb_acc!!.visibility = View.VISIBLE

            val credentials = UserDetailCredential()
            credentials.userId = Kotpref.userId
            //credentials.userId = PropertyDetailsData.property!!.PropertyDetail!!.OnBordedBy
            //credentials.userId = PropertyDetailsData.property!!.PropertyDetail!!.PrimaryContactId.toString()

            //Log.e("MCX_LOG-2", Gson().toJson(PropertyDetailsData))

            Log.e("ASDF", Kotpref.userId)

            val validateService: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<UserDetailsResponce> =
                validateService.getMarketplaceUserDetails(credentials)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    pb_acc!!.visibility = View.GONE

                    Log.e(
                        "onSuccess",
                        "User Details ==>" + Gson().toJson(it)
                    )
                    if (it.responseDto!!.responseCode == 200) {
                        UserDetailsResponceData = it

                        checkAccountPref(it.data!!.stripAccountVerified!!)

                        it.data!!.stripAccountVerified?.let { sa->
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
            pb_acc!!.visibility = View.GONE
            Toast.makeText(
                applicationContext,
                getString(R.string.error_network),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showAmountVerificationAlert(b: Boolean) {
        if (b) {
            Log.e("Show Amount", "Show Amount Info==>$b")

            ll_account_info.visibility = View.GONE
            ll_amount_verify.visibility = View.VISIBLE
        } else {
            Log.e("Show Account", "Show Account Info==>$b")

            ll_account_info.visibility = View.VISIBLE
            ll_amount_verify.visibility = View.GONE
        }
    }

    private fun checkAccountPref(stripAccountVerified: Boolean) {
        try {
            if (!UserDetailsResponceData.data!!.bankAccountNo.equals("") && UserDetailsResponceData.data?.bankAccountNo!! != null ) {
                tv_account_number.text =
                    "Ac. No. : " + UserDetailsResponceData.data?.bankAccountNo?.let {
                        RSA.decrypt(
                            it
                        )
                    }

                tv_account_holder_name.text =
                    "Account Holder : " + UserDetailsResponceData.data?.bankUserName?.let {
                        it
                    }

                tv_routing_number.text =
                    "Routing No. : " + UserDetailsResponceData.data?.routingNo?.let {
                        RSA.decrypt(
                            it
                        )
                    }


                tv_receivable_configure.visibility = View.GONE
                tv_view_account.visibility = View.GONE
                tv_account_number.visibility = View.VISIBLE
                tv_routing_number.visibility = View.VISIBLE

                if (!stripAccountVerified) {
                    showAmountVerificationAlert(true)
                } else {
                    showAmountVerificationAlert(false)
                }

            } else {

                tv_account_holder_name.text = resources.getString(R.string.text_not_yet_configured)
                tv_receivable_configure.visibility = View.VISIBLE
                tv_view_account.visibility = View.GONE
                tv_account_number.visibility = View.GONE
                tv_routing_number.visibility = View.GONE
                ll_account_info.visibility = View.VISIBLE
            }


        } catch (e: Exception) {
            e.printStackTrace()
            tv_account_holder_name.text = resources.getString(R.string.text_not_yet_configured)
            tv_receivable_configure.visibility = View.VISIBLE
            tv_view_account.visibility = View.GONE
            tv_account_number.visibility = View.GONE
            tv_routing_number.visibility = View.GONE

            ll_account_info.visibility = View.VISIBLE
            ll_amount_verify.visibility = View.GONE
        }

    }

    @SuppressLint("CheckResult")
    private fun verifyPaymentInfo() {
        pb_acc!!.visibility = View.VISIBLE
        val credential = VerifyBankInfoCredential()
        credential.userCatelogId = Kotpref.userId
        credential.firstamount = "" + edit_first_amount.text.toString().trim()
        credential.secondamount = "" + edit_second_amount.text.toString().trim()
        Log.d("Bank_I_LOG_1 user ", "id verify account details activity " + Kotpref.userId)

        Log.e("Bank_I_LOG_1", Gson().toJson(credential))
        val paymentInfoApi: ApiInterface = ApiClient(this).provideService(ApiInterface::class.java)
        val apiCall: Observable<AddBankInfoResponse> = paymentInfoApi.verifyBankInfo(credential)

        apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
            Log.e("Bank_O_LOG_1", Gson().toJson(it.responseDto))
            Log.e("onSuccess", it.responseDto?.responseDescription.toString())
            pb_acc!!.visibility = View.GONE

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

            } else if (it.responseDto?.responseCode == 201) {

                val sayWindows = AlertDialog.Builder(this)
                sayWindows.setMessage("Your payment information has been verified.")
                sayWindows.setPositiveButton("ok", null)

                val mAlertDialog = sayWindows.create()
                mAlertDialog.setOnShowListener {
                    val b: Button = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    b.setOnClickListener(View.OnClickListener {
                        getUserDetails()

                        mAlertDialog.dismiss()
                    })
                }
                mAlertDialog.show()

                /* if (isBroker) {
                     Kotpref.bankAdded = true
                     Kotpref.bankAccountVerified = true
                     Util.navigationNext(
                         this@AccountLinkDetailScreen,
                         HomeActivityBroker::class.java
                     )
                     finish()
                 } else {
                     Kotpref.bankAdded = true
                     Kotpref.bankAccountVerified = true
                     Util.navigationNext(
                         this@AccountLinkDetailScreen,
                         HomeActivityCx::class.java
                     )
                     finish()
                 }*/
            }
        },
            { e ->
                pb_acc!!.visibility = View.GONE
                Log.e("onFailure", e.toString())
                e.message?.let {
                    Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                }
            })
    }

    companion object {
        var UserDetailsResponceData = UserDetailsResponce()
    }

}