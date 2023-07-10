package mp.app.calonex.landlord.activity

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_account_detail_screen.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.CreditCardCredentials
import mp.app.calonex.common.apiCredentials.UpdateUserDetailCredentials
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.RSA
import mp.app.calonex.common.utility.Util
import mp.app.calonex.common.utility.Util.navigationBack
import mp.app.calonex.common.utility.Util.setEditReadOnly
import mp.app.calonex.landlord.fragment.ProfileLdFragment.Companion.userDetailResponse
import mp.app.calonex.landlord.response.UpdateUserResponse
import mp.app.calonex.ldBkAddBank.AddBankPersonalInfoActivity
import mp.app.calonex.tenant.response.GetCreditCardInfoResponse


class AccountDetailScreen : CxBaseActivity2() {
    private var editBnkName: TextInputEditText? = null
    private var editAccName: TextInputEditText? = null
    private var editRouting: TextInputEditText? = null
    private var layoutAccBtn: LinearLayout? = null
    private var btnAccCancel: Button? = null
    private var btnAccUpdate: Button? = null
    private var imgAccUpdate: ImageView? = null
    private var pbAccount: ProgressBar? = null
    private var bnkName: String? = null
    private var accNo: String? = null
    private var routNo: String? = null
    private var et_card_number: TextInputEditText? = null
    private var edit_name_on_card: TextInputEditText? = null
    private var edit_valid_upto: TextInputEditText? = null
    private var til_account: TextInputLayout? = null
    private var tv_prefrea: TextView? = null


    private var til_routin: TextInputLayout? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_detail_screen)
        initComponent()
        actionComponent()
        startHandler()
    }

    private fun initComponent() {
        tv_prefrea = findViewById(R.id.tv_prefrea);
        til_routin = findViewById(R.id.til_routin);
        til_account = findViewById(R.id.til_account);
        edit_name_on_card = findViewById(R.id.edit_name_on_card)
        et_card_number = findViewById(R.id.et_card_number);
        edit_valid_upto = findViewById(R.id.edit_valid_upto);

        editBnkName = findViewById(R.id.edit_bnk_name)
        editAccName = findViewById(R.id.edit_acc_no)
        editRouting = findViewById(R.id.edit_routing_no)
        layoutAccBtn = findViewById(R.id.layout_acc_btn)
        btnAccCancel = findViewById(R.id.btn_acc_cancel)
        btnAccUpdate = findViewById(R.id.btn_acc_update)
        imgAccUpdate = findViewById(R.id.iv_update_acc)
        pbAccount = findViewById(R.id.pb_acc)
    }

    private fun actionComponent() {

        getCardInfo()

        disableEdit()
        if (userDetailResponse.bankUserName != null) {
            editBnkName!!.setText(RSA.decrypt(userDetailResponse.bankUserName))
        }

        if (userDetailResponse.bankAccountNo != null) {
            editAccName!!.setText(RSA.decrypt(userDetailResponse.bankAccountNo))
            editAccName!!.setTransformationMethod(PasswordTransformationMethod())
            tv_prefrea!!.text = "Your Preferred Subscription Payment Option is Bank Account"
            layoutAccBtn!!.visibility = View.GONE
        } else {
            layoutAccBtn!!.visibility = View.VISIBLE
            btn_acc_update!!.setText("ADD BANK DETAILS")
        }

        if (userDetailResponse.routingNo != null) {
            // til_routin!!.passwordVisibilityToggleRequested(false)
            editRouting!!.setTransformationMethod(PasswordTransformationMethod())
            editRouting!!.setText(RSA.decrypt(userDetailResponse.routingNo))
        }

        btnAccCancel!!.setOnClickListener {
            disableEdit()
        }
        btnAccUpdate!!.setOnClickListener {
            //Util.navigationNext(this@AccountDetailScreen, AddBankPersonalInfoActivity::class.java)
            Kotpref.isRegisterConfirm = false
            startActivity(Intent(this@AccountDetailScreen, AddBankPersonalInfoActivity::class.java))

            //validateValue()
        }

        imgAccUpdate!!.setOnClickListener {
            enableEdit()
        }

        header_back.setOnClickListener {
            super.onBackPressed()
            // Util.navigationBack(this)
            navigationBack(this)
        }
    }

    private fun disableEdit() {
        setEditReadOnly(editBnkName!!, false, InputType.TYPE_NULL)
        setEditReadOnly(editAccName!!, false, InputType.TYPE_NULL)
        setEditReadOnly(editRouting!!, false, InputType.TYPE_NULL)

        setEditReadOnly(edit_name_on_card!!, false, InputType.TYPE_CLASS_TEXT)
        setEditReadOnly(et_card_number!!, false, InputType.TYPE_CLASS_TEXT)
        setEditReadOnly(edit_valid_upto!!, false, InputType.TYPE_CLASS_TEXT)

        //layoutAccBtn!!.visibility = View.GONE
    }

    private fun enableEdit() {
        setEditReadOnly(editBnkName!!, true, InputType.TYPE_CLASS_TEXT)
        setEditReadOnly(editAccName!!, true, InputType.TYPE_CLASS_TEXT)
        setEditReadOnly(editRouting!!, true, InputType.TYPE_CLASS_TEXT)

        setEditReadOnly(edit_name_on_card!!, true, InputType.TYPE_CLASS_TEXT)
        setEditReadOnly(et_card_number!!, true, InputType.TYPE_CLASS_TEXT)
        setEditReadOnly(edit_valid_upto!!, true, InputType.TYPE_CLASS_TEXT)

        //layoutAccBtn!!.visibility = View.VISIBLE
    }

    private fun validateValue() {
        bnkName = editBnkName!!.text.toString().trim()
        accNo = editAccName!!.text.toString().trim()
        routNo = editRouting!!.text.toString().trim()

        if (Util.valueMandetory(applicationContext, bnkName, editBnkName!!)) {
            return
        }

        if (Util.valueMandetory(applicationContext, accNo, editAccName!!)) {
            return
        }

        if (accNo!!.length < 2 || accNo!!.length > 17) {
            editAccName?.error = getString(R.string.error_acc_nmbr)
            editAccName?.requestFocus()
            return
        }

        if (Util.valueMandetory(applicationContext, routNo, editRouting!!)) {
            return
        }

        if (routNo!!.length != 9) {
            editRouting?.error = getString(R.string.error_routing)
            editRouting?.requestFocus()
            return
        }
        modifyDetail()
    }

    private fun modifyDetail() {
        if (NetworkConnection.isNetworkConnected(this)) {
            pbAccount!!.visibility = View.VISIBLE
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            val credentials = UpdateUserDetailCredentials()
            credentials.userId = Kotpref.userId
            credentials.bankUserName = RSA.encrypt(bnkName!!)!!
            credentials.bankAccountNo = RSA.encrypt(accNo!!)!!
            credentials.routingNo = RSA.encrypt(routNo!!)!!
            val signatureApi: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<UpdateUserResponse> =
                signatureApi.updateUserDetail(credentials)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", it.responseDescription.toString())

                    pbAccount!!.visibility = View.GONE
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                    if (it!!.responseCode.equals(0)) {

                        disableEdit()
                        if (!Kotpref.subscriptionActive) {
                            Util.alertOkMessage(
                                this@AccountDetailScreen,
                                getString(R.string.alert),
                                getString(R.string.tag_acc_subsribe)
                            )

                        }
                    } else {

                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    }
                },
                    { e ->
                        Log.e("onFailure", e.toString())
                        pbAccount!!.visibility = View.GONE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        e.message?.let {
                            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                        }
                    })
        } else {
            Toast.makeText(this, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navigationBack(this)
    }

    private fun getCardInfo() {
        tv_prefrea?.visibility = View.VISIBLE

        var credentials: CreditCardCredentials = CreditCardCredentials()
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
                        if (response.data!!.name.isNullOrEmpty())
                            edit_name_on_card!!.setText(response.data!!.name)

                        tv_prefrea!!.setText("Your preferred Subscription payment option is Credit Card")

                        et_card_number!!.setText("""**** **** ****${response.data!!.last4}""")
                        edit_valid_upto!!.setText(response.data!!.exp_month + "/" + response.data!!.exp_year)
                    }
                }
            }

            override fun onFailed(t: Throwable) {
                tv_prefrea!!.text = "Your Preferred Subscription Payment Option is Bank Account"
            }
        })
    }

}