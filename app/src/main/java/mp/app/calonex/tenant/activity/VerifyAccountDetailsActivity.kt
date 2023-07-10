package mp.app.calonex.tenant.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_tenant_list.*
import kotlinx.android.synthetic.main.activity_verify_account_details.*
import mp.app.calonex.R
import mp.app.calonex.broker.activity.HomeActivityBroker
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.landlord.activity.CxBaseActivity
import mp.app.calonex.landlord.activity.HomeActivityCx
import mp.app.calonex.landlord.activity.LoginScreen
import mp.app.calonex.tenant.fragment.TenantListProfileFragment
import mp.app.calonex.tenant.model.apiCredentials.VerifyBankInfoCredential
import mp.app.calonex.tenant.response.AddBankInfoResponse


class VerifyAccountDetailsActivity : CxBaseActivity() {
    private val fragmentProfile: Fragment = TenantListProfileFragment()
    private val fm: FragmentManager = supportFragmentManager
    private lateinit var  verify_account_profile:ImageView
    private lateinit var message: String
    private var FromLandlord = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_account_details)
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            FromLandlord = bundle.getBoolean("FromLandlord")
        }
        actionComponent()

        verify_account_profile=findViewById(R.id.verify_account_profile)
        if (FromLandlord) {
            verify_account_profile.visibility = View.INVISIBLE
        }
        verify_account_profile.setOnClickListener {

            fm.beginTransaction().add(R.id.container, fragmentProfile).addToBackStack(null).commit()
        }
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
            Log.e("Bank_Flag", FromLandlord.toString())
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
        Log.d("Bank_I_LOG_1 user ","id verify account details activity "+ Kotpref.userId)

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
                }
                else if (it.responseDto?.responseCode == 201) {
                    if (Kotpref.userRole.contains("CX-Broker", true)) {
                        Kotpref.bankAccountVerified=true
                        Toast.makeText(this, "Bank account verified", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, HomeActivityBroker::class.java))
                        finish()
                    }
                }

                else {
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

    override fun onBackPressed() {
        if (FromLandlord) {
           // startActivity(Intent(this, LoginScreen::class.java))
            val intent = Intent(this, LoginScreen::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
