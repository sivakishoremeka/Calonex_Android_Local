package mp.app.calonex.broker.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import mp.app.calonex.R
//import mp.app.calonex.agentBrokerRegLd.UserContactDetailScreenNew
import mp.app.calonex.broker.model.InviteAgentRequestModel
import mp.app.calonex.broker.responce.BrokerInviteAgentResponse
import mp.app.calonex.common.apiCredentials.BrokerAgentCredentials
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.UsPhoneNumberFormatter
import mp.app.calonex.common.utility.Util
import mp.app.calonex.common.utility.Util.valueMandetory
import mp.app.calonex.landlord.activity.CxBaseActivity2
import mp.app.calonex.landlord.response.BrokerAgentDetailResponse
import java.util.*

class InviteAgent : CxBaseActivity2() {

    private var editRegName: TextInputEditText? = null
    private var editRegEmail: TextInputEditText? = null
    private var editRegLicenceNo: TextInputEditText? = null
    private var editCommision: TextInputEditText? = null
    private var editRegPhnNmbr: TextInputEditText? = null

    private var btnRegNxt: TextView? = null
    private var iv_back: ImageView? = null


    private var regName: String? = null
    private var regEmail: String? = null
    private var regLicenceNo: String? = null
    private var regCommision: String? = null
    private var regPhnNmbr: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite_agent)
        initComponent()
        actionComponent()
        startHandler()
    }

    fun initComponent() {
        editRegName = findViewById(R.id.edit_reg_first_name)
        editRegEmail = findViewById(R.id.edit_reg_middle_name)
        editRegLicenceNo = findViewById(R.id.edit_reg_address)
        editCommision = findViewById(R.id.edit_reg_commision)
        editRegPhnNmbr = findViewById(R.id.edit_reg_phn)

        btnRegNxt = findViewById(R.id.btn_reg_next)
        iv_back = findViewById(R.id.iv_back)

        val addLineNumberFormatter = UsPhoneNumberFormatter(editRegPhnNmbr!!)
        editRegPhnNmbr!!.addTextChangedListener(addLineNumberFormatter)

        //Util.setEditReadOnly(editRegCity!!, false, InputType.TYPE_NULL)
    }

    fun actionComponent() {

        iv_back!!.setOnClickListener {
            onBackPressed()
        }

        editRegLicenceNo?.onFocusChangeListener =
            View.OnFocusChangeListener { view, hasFocus ->
                if (!hasFocus) {
                    if (editRegLicenceNo!!.text.toString().trim().length==11) {
                        if (NetworkConnection.isNetworkConnected(this@InviteAgent)) {

                            var credential = BrokerAgentCredentials()
                            credential.licenceId = editRegLicenceNo!!.text.toString()

                            val brokerAgentDetails: ApiInterface =
                                                                                                                                                                                ApiClient(this@InviteAgent).provideService(ApiInterface::class.java)
                            val apiCall: Observable<BrokerAgentDetailResponse> =
                                brokerAgentDetails.getBrokerAgentList(credential) //Test API Key

                            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    Log.e("onSuccess", "Agent Data==>"+Gson().toJson(it.data))
                                    if (it.data != null && it.responseDto!!.responseCode==200) {

                                        if (Kotpref.userRole.contains("broker",true) && !it.data!!.primaryContactType.contains("broker",true)){
                                            editRegName!!.setText(it.data!!.agentName)
                                            editRegEmail!!.setText(it.data!!.agentEmailID)
                                            editRegPhnNmbr!!.setText(PhoneNumberUtils.formatNumber(it.data!!.agentPhone, "US"))
                                        }else{
                                            var alert=SweetAlertDialog(this@InviteAgent,SweetAlertDialog.ERROR_TYPE)
                                              alert.contentText="Broker can't invite a broker, please provide valid agent licence No"

                                            alert.confirmText="Ok"
                                            alert.setConfirmClickListener {
                                                editRegLicenceNo!!.setText("")
                                                alert.dismiss()
                                            }
                                            alert.show()
                                        }




                                    }
                                },
                                    { e ->

                                        e.message?.let {
                                            Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                                        }
                                    })
                        } else {
                            Toast.makeText(
                                this@InviteAgent,
                                getString(R.string.error_network),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

        btnRegNxt!!.setOnClickListener { view ->
            // Hide the keyboard
            val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            validComponent()
        }
    }

    fun validComponent() {
        regName = editRegName!!.text.toString().trim()
        regEmail = editRegEmail!!.text.toString().trim()
        regLicenceNo = editRegLicenceNo!!.text.toString().trim()
        regCommision = editCommision!!.text.toString().trim()
        regPhnNmbr = editRegPhnNmbr!!.text.toString().trim()

        // Check the email string is empty or not
        if (regEmail!!.isNullOrEmpty()) {
            editRegEmail?.error = getString(R.string.error_email)
            editRegEmail?.requestFocus()
            return
        }
        if ((!Util.isEmailValid(regEmail!!))) {
            editRegEmail?.error = getString(R.string.error_valid_email)
            editRegEmail?.requestFocus()
            return
        }

        if (valueMandetory(applicationContext, regName, editRegName!!)) {
            return
        }

        if (valueMandetory(applicationContext, regEmail, editRegEmail!!)) {
            return
        }
        if (valueMandetory(applicationContext, regLicenceNo, editRegLicenceNo!!)) {
            return
        }

        if (valueMandetory(applicationContext, regPhnNmbr, editRegPhnNmbr!!)) {
            return
        }

        if (regPhnNmbr!!.length != 14) {
            editRegPhnNmbr?.error = getString(R.string.error_phn)
            editRegPhnNmbr?.requestFocus()
            return
        }

        if (regCommision!!.isNotEmpty() && Integer.parseInt(regCommision) > 100) {
            editCommision?.error = getString(R.string.error_prcnt_agent)
            editCommision?.requestFocus()
            return
        }


        inviteAgent()
    }


    @SuppressLint("CheckResult")
    private fun inviteAgent() {
        var des: String = "";
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            //Create retrofit Service
            //pb_validate!!.visibility = View.VISIBLE
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )

            val credentials = InviteAgentRequestModel()


            credentials.agentName = regName.toString()
            credentials.agentEmailId = regEmail.toString()
            credentials.agentPhoneNo = regPhnNmbr.toString()
            credentials.location = ""
            credentials.agentLicenseNo = regLicenceNo.toString()
            credentials.commissionPercentage = regCommision.toString()
            credentials.brokerFullName = Kotpref.userName
            credentials.brokerUserId = Kotpref.userId

            val validateService: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<BrokerInviteAgentResponse> =
                validateService.inviteAgent(credentials)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Log.e("onSuccess", it.responseDto?.responseDescription.toString())
                        des = it.responseDto?.responseDescription.toString()
                        //pb_validate!!.visibility = View.GONE
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                        if (it!!.responseDto!!.responseCode.equals(200)) {
                            finish()
                        }

                        if (it!!.responseDto!!.responseCode.equals(409)) {
                            Toast.makeText(
                                applicationContext,
                                "Email already exists and the agent is notified",
                                Toast.LENGTH_SHORT
                            ).show()

                            //   finish()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                it!!.responseDto!!.responseDescription,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    { e ->
                        Log.e("onFailure", e.toString())
                        //pb_validate!!.visibility = View.GONE
                        e.printStackTrace()

                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        e.message?.let {
                            Toast.makeText(
                                applicationContext,
                                "Email already exists and the agent is notified",
                                Toast.LENGTH_SHORT
                            ).show()
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

    override fun onBackPressed() {
        super.onBackPressed()
        Util.navigationBack(this)
    }
}