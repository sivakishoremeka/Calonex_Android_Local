package mp.app.calonex.agent.activity

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.*
import com.google.android.material.textfield.TextInputEditText
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_personal_information_screen.header_back
import kotlinx.android.synthetic.main.fragment_broker_payment.*
import mp.app.calonex.R
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.common.utility.Util.navigationBack
import mp.app.calonex.common.utility.Util.setEditReadOnly
import mp.app.calonex.broker.responce.BrokerFranchiseInfoResponse
import mp.app.calonex.common.apiCredentials.FranchiseDetailCredential
import mp.app.calonex.common.apiCredentials.UserDetailCredential
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.landlord.activity.CxBaseActivity2

class FranchiseSettingsAgent : CxBaseActivity2() {
    private var et_f_name: TextInputEditText? = null
    private var et_percentage: TextInputEditText? = null
    private var iv_update_acc: ImageView?=null
    private var layout_acc_btn: LinearLayout?=null
    private  var btn_acc_cancel: Button?=null
    private var btn_acc_update: Button?=null

    var name:String=""
    var percentage: String=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_franchise_setting)
        try {


            initComponent()
            actionComponent()
            getFranchise()
            startHandler()
        } catch (e: Exception) {
            Log.e("Plan Detail", e.message.toString())
        }
    }

    private fun initComponent() {
        iv_update_acc=findViewById(R.id.iv_update_acc);
        layout_acc_btn=findViewById(R.id.layout_acc_btn);
        et_f_name = findViewById(R.id.et_f_name)
        et_percentage = findViewById(R.id.et_percentage)
        btn_acc_update=findViewById(R.id.btn_acc_update);
        btn_acc_cancel=findViewById(R.id.btn_acc_cancel);


        disableEdit()
    }

    private fun actionComponent() {

        btn_acc_cancel!!.setOnClickListener {
            finish()
        }


        enableEdit()
        header_back.setOnClickListener {
            super.onBackPressed()
         //   Util.navigationBack(this)
            navigationBack(this)

        }


        btn_acc_update!!.setOnClickListener {
            validateValue()
        }

/*
        iv_update_acc!!.setOnClickListener {

        }
*/


    }

    private fun disableEdit() {
        setEditReadOnly(et_f_name!!, false, InputType.TYPE_NULL)
        setEditReadOnly(et_percentage!!, false, InputType.TYPE_NULL)
        btn_acc_update!!.visibility = View.GONE
    }

    private fun enableEdit() {
        setEditReadOnly(et_percentage!!, true, InputType.TYPE_CLASS_TEXT)
        setEditReadOnly(et_f_name!!, true, InputType.TYPE_CLASS_TEXT)
        btn_acc_update!!.visibility = View.VISIBLE
    }

    private fun validateValue() {
        name = et_f_name?.text.toString().trim()
        percentage = et_percentage!!.text.toString().trim()


        if(name.isNullOrEmpty())
        {
            et_f_name?.setError(R.string.error_mandetory.toString())
            et_f_name?.requestFocus()
        }
        else if(percentage.isNullOrEmpty())
        {
            et_percentage?.setError(R.string.error_mandetory.toString())
            et_percentage?.requestFocus()
        }

        else
        {
            saveFranchise()
        }


    }

    private fun getFranchise() {
        if (NetworkConnection.isNetworkConnected(this)) {
            //Create retrofit Service
            pb_payment?.visibility = View.VISIBLE
            val credentials = UserDetailCredential()

            credentials.userId = Kotpref.userId
            val paymentListService: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<BrokerFranchiseInfoResponse> =
                paymentListService.getFranchise(credentials)

            RxAPICallHelper().call(
                apiCall,
                object : RxAPICallback<BrokerFranchiseInfoResponse> {
                    override fun onSuccess(paymentListResponse: BrokerFranchiseInfoResponse) {
                        Log.e(
                            "onSuccessPayment",
                            paymentListResponse.percentage.toString()
                        )

                        if(paymentListResponse.percentage!=null)
                        {
                            et_percentage!!.setText(paymentListResponse.percentage.toString())
                        }

                        if (paymentListResponse.franchiseName!=null)
                               et_f_name!!.setText(paymentListResponse.franchiseName)

                    }

                    override fun onFailed(t: Throwable) {
                        // show error
                        Log.e("onFailure", t.toString())
                        try {
                            Util.apiFailure(this@FranchiseSettingsAgent, t)
                        } catch (e: Exception) {
                            Toast.makeText(
                                this@FranchiseSettingsAgent,
                                getString(R.string.error_something),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
        } else {
            Toast.makeText(this@FranchiseSettingsAgent, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navigationBack(this)
    }

    private fun saveFranchise() {
        if (NetworkConnection.isNetworkConnected(this)) {
            //Create retrofit Service
            pb_payment?.visibility = View.VISIBLE
            val credentials = FranchiseDetailCredential()

            credentials.userId = Kotpref.userId
            credentials.franchiseName = et_f_name!!.text.toString()
            credentials.percentage=et_percentage!!.text.toString()
            val paymentListService: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<Boolean> =
                paymentListService.AddDetailsFranchise(credentials)

            RxAPICallHelper().call(
                apiCall,
                object : RxAPICallback<Boolean> {
                    override fun onSuccess(paymentListResponse: Boolean) {


if(paymentListResponse)
                        Toast.makeText(
                            this@FranchiseSettingsAgent,"Information updated successfully.",
                            Toast.LENGTH_SHORT
                        ).show()
                        else
    Toast.makeText(
        this@FranchiseSettingsAgent,"Something went wrong.",
        Toast.LENGTH_SHORT
    ).show()





                    }

                    override fun onFailed(t: Throwable) {
                        // show error
                        Log.e("onFailure", t.toString())
                        try {
                            Util.apiFailure(this@FranchiseSettingsAgent, t)
                        } catch (e: Exception) {
                            Toast.makeText(
                                this@FranchiseSettingsAgent,
                                getString(R.string.error_something),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
        } else {
            Toast.makeText(this@FranchiseSettingsAgent, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
        }
    }

}