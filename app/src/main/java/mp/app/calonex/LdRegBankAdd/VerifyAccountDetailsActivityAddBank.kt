package mp.app.calonex.LdRegBankAdd

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_verify_account_details_add_bank.*
import mp.app.calonex.R
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.landlord.activity.CxBaseActivity
import mp.app.calonex.landlord.activity.HomeActivityCx
import mp.app.calonex.tenant.model.apiCredentials.VerifyBankInfoCredential
import mp.app.calonex.tenant.response.AddBankInfoResponse


class VerifyAccountDetailsActivityAddBank : CxBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_account_details_add_bank)

        actionComponent()
    }

    private fun actionComponent() {
        tv_verify_account.setOnClickListener {

            if (tv_first_deposit.text!!.isEmpty()) {
                tv_first_deposit?.error = getString(R.string.error_mandetory)
                tv_first_deposit?.requestFocus()
                return@setOnClickListener
            }

            if (tv_second_deposit.text!!.isEmpty()) {
                tv_second_deposit?.error = getString(R.string.error_mandetory)
                tv_second_deposit?.requestFocus()
                return@setOnClickListener
            }

            verifyPaymentInfo()
        }
    }

    @SuppressLint("CheckResult")
    private fun verifyPaymentInfo() {
        progressBar_bank.visibility = View.VISIBLE
        val credential = VerifyBankInfoCredential()
        credential.userCatelogId = Kotpref.userId
        credential.firstamount = tv_first_deposit.text.toString()
        credential.secondamount = tv_second_deposit.text.toString()

        Log.d("Bank_I_LOG_1 user ","id verify account details activity Add bank "+ Kotpref.userId)


        val paymentInfoApi: ApiInterface = ApiClient(this).provideService(ApiInterface::class.java)
        val apiCall: Observable<AddBankInfoResponse> = paymentInfoApi.verifyBankInfo(credential)

        apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
            progressBar_bank.visibility = View.GONE

            if (it.responseDto?.responseCode == 400) {

                val sayWindows = AlertDialog.Builder(this)
                sayWindows.setMessage(it.responseDto?.message)
                sayWindows.setPositiveButton("ok", null)

                val mAlertDialog = sayWindows.create()
                mAlertDialog.setOnShowListener {
                    val b: Button = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    b.setOnClickListener {
                        mAlertDialog.dismiss()
                    }
                }
                mAlertDialog.show()


                /* val intent = Intent(this, SignatureUploadActivity::class.java)
                 startActivity(intent)*/

            } else if (it.responseDto?.responseCode == 201) {
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage(it.responseDto?.message)
                    builder.setPositiveButton("ok"
                    ) { dialog, id ->
                        startActivity(Intent(this, HomeActivityCx::class.java))
                        finish()
                    }
                builder.show()
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

    override fun onBackPressed() {
    }
}
