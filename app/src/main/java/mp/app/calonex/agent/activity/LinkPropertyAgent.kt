package mp.app.calonex.agent.activity

//import mp.app.calonex.agentBrokerRegLd.UserContactDetailScreenNew

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_new_property_first_screen.*
import kotlinx.android.synthetic.main.activity_link_property_agent.*
import kotlinx.android.synthetic.main.activity_link_property_agent_new.*
import kotlinx.android.synthetic.main.activity_link_request_action.*
import mp.app.calonex.R
import mp.app.calonex.agent.adapter.AutoCompleteAdapter
import mp.app.calonex.agent.model.FinalAgentLinkProperty
import mp.app.calonex.agent.responce.FinalPropertyLinkResponse
import mp.app.calonex.agent.responce.PropertyLinkResponseAgent
import mp.app.calonex.broker.activity.HomeActivityBroker
import mp.app.calonex.common.apiCredentials.*
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.UsPhoneNumberFormatter
import mp.app.calonex.common.utility.Util
import mp.app.calonex.common.utility.Util.valueMandetory
import mp.app.calonex.landlord.activity.CxBaseActivity2
import mp.app.calonex.landlord.adapter.CustomSpinnerAdapter
import mp.app.calonex.landlord.model.Property
import mp.app.calonex.landlord.model.ZipCityStateModel
import mp.app.calonex.landlord.response.LandlordDetailsByEmail
import mp.app.calonex.landlord.response.PropertyListResponse
import java.util.*


class LinkPropertyAgent : CxBaseActivity2() {

    private var editRegFirstName: TextInputEditText? = null
    private var editRegMiddleName: TextInputEditText? = null
    private var editRegAddress: AutoCompleteTextView? = null
    private var editRegCity: TextInputEditText? = null
    private var editRegState: TextInputEditText? = null
    private var editRegZipCode: TextInputEditText? = null
    private var editRegPhnNmbr: TextInputEditText? = null

    private var pb_login: ProgressBar? = null

    private var btnRegNxt: TextView? = null
    private var iv_back: ImageView? = null

    private var regFirstName: String? = null
    private var regMiddleName: String? = null
    private var regAddress: String? = null
    private var regCity: String? = null
    private var regState: String? = null
    private var regZipcode: String? = null
    private var regPhnNmbr: String? = ""
    private var regEmail: String? = ""


    private var regStateIssue: String? = null
    private var stateIssueList = ArrayList<String>()
    private var zcsList = ArrayList<ZipCityStateModel>()
    private var cal = Calendar.getInstance()
    private var year = cal.get(Calendar.YEAR)
    private var month = cal.get(Calendar.MONTH)
    private var day = cal.get(Calendar.DAY_OF_MONTH)
    val myFormat = "MMM dd, yyyy"
    var propertyListWithoutBrokerAgent = ArrayList<Property>()
    var userDetailsDto: LandlordDetailsByEmail? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_link_property_agent_new)
        initComponent()
        actionComponent()
        startHandler()
    }

    fun initComponent() {

        var extras = intent.extras

        if (extras != null) {

            var pageTitle = extras.getString("fromRegisterLandlord", "")
            if (!pageTitle.isEmpty()) {
                txt_msg_from.text = pageTitle
            }

        }



        editRegFirstName = findViewById(R.id.edit_reg_first_name)
        editRegMiddleName = findViewById(R.id.edit_reg_middle_name)
        editRegAddress = findViewById(R.id.edit_reg_address)
        editRegCity = findViewById(R.id.edit_reg_city)
        editRegState = findViewById(R.id.edit_reg_state)
        editRegZipCode = findViewById(R.id.edit_reg_zip)
        editRegPhnNmbr = findViewById(R.id.edit_reg_phn)
        pb_login = findViewById(R.id.pb_login)

        btnRegNxt = findViewById(R.id.btn_reg_next)
        iv_back = findViewById(R.id.iv_back)

        zcsList = Util.getZipCityStateList(applicationContext)
        val addLineNumberFormatter = UsPhoneNumberFormatter(editRegPhnNmbr!!)
        editRegPhnNmbr!!.addTextChangedListener(addLineNumberFormatter)

        var listState = mutableListOf<String>()

        stateIssueList.add("State Issued")
        for (item in zcsList) {
            listState.add(item.state!!)
        }
        stateIssueList.addAll(ArrayList<String>(listState.toSet().sorted().toList()))
        val spinnerStateIssueAdapter = CustomSpinnerAdapter(applicationContext, stateIssueList)
        System.out.println("state list ==> " + stateIssueList)
        //spinnerRegStateIssue?.adapter=spinnerStateIssueAdapter

        Util.setEditReadOnly(editRegCity!!, false, InputType.TYPE_NULL)
        Util.setEditReadOnly(editRegState!!, false, InputType.TYPE_NULL)


        editRegMiddleName?.setOnFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange(view: View, hasFocus: Boolean) {
                if (!hasFocus) {
                    if (Util.isEmailValid(editRegMiddleName!!.text.toString())) {
                        if (NetworkConnection.isNetworkConnected(this@LinkPropertyAgent)) {
                            var credentials = GetPropertyListByEmailApiCredential()
                            pb_login!!.visibility = View.VISIBLE

                            credentials!!.landLordEmailId = editRegMiddleName!!.text.toString()
                            var propertyListService: ApiInterface =
                                ApiClient(this@LinkPropertyAgent).provideService(ApiInterface::class.java)
                            var apiCall: Observable<PropertyListResponse> =
                                propertyListService.getPropertyByLandlordId(credentials)

                            RxAPICallHelper().call(
                                apiCall,
                                object : RxAPICallback<PropertyListResponse> {
                                    override fun onSuccess(propertyListResponse: PropertyListResponse) {
                                        Log.e(
                                            "onSuccess",
                                            propertyListResponse.responseDto!!.responseCode.toString()
                                        )
                                        pb_login!!.visibility = View.GONE
                                        if (propertyListResponse.responseDto!!.responseCode == 200 || propertyListResponse.responseDto!!.responseCode == 0) {
                                            try {
                                                if (propertyListResponse.userDetailsDto != null) {
                                                    userDetailsDto =
                                                        propertyListResponse.userDetailsDto
                                                    updateUi()
                                                }
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }

                                            try {
                                                if (!propertyListResponse.data?.isEmpty()!!) {
                                                    propertyListWithoutBrokerAgent =
                                                        propertyListResponse.data!!

                                                    setAutocompleteAdapter()
                                                }
                                            }catch (e:Exception){
                                                e.printStackTrace()
                                            }
                                        }


                                    }

                                    override fun onFailed(t: Throwable) {
                                        // show error
                                        pb_login!!.visibility = View.GONE
                                        //updateUi()

                                        Log.e("onFailure", t.toString())
                                        try {
                                            Util.apiFailure(this@LinkPropertyAgent, t)
                                        } catch (e: Exception) {
                                        }
                                    }
                                })
                        } else {
                            Toast.makeText(
                                this@LinkPropertyAgent,
                                getString(R.string.error_network),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        })
    }

    private fun setAutocompleteAdapter() {

        val searchArrayList = ArrayList<String>()
        for (i in 0 until propertyListWithoutBrokerAgent.size) {
            var fullAddress =
                propertyListWithoutBrokerAgent[i].address + ", " + propertyListWithoutBrokerAgent[i].city + ", " + propertyListWithoutBrokerAgent[i].state + ", " + propertyListWithoutBrokerAgent[i].zipCode
            searchArrayList.add(fullAddress)
        }
        val adapter = AutoCompleteAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            android.R.id.text1,
            searchArrayList
        )

        editRegAddress!!.setAdapter(adapter)

        editRegAddress!!.setOnItemClickListener { parent, view, position, id ->
            var mainListPosition = searchArrayList.indexOf(adapter.getItem(position).toString())

            /*Toast.makeText(
                this,
                "($mainListPosition) == >" + adapter.getItem(position).toString(),
                Toast.LENGTH_SHORT
            ).show()*/

            try {
                editRegAddress!!.setText(propertyListWithoutBrokerAgent[mainListPosition].address)
                editRegCity!!.setText(propertyListWithoutBrokerAgent[mainListPosition].city)
                editRegState!!.setText(propertyListWithoutBrokerAgent[mainListPosition].state)
                editRegZipCode!!.setText(propertyListWithoutBrokerAgent[mainListPosition].zipCode)
            } catch (e: Exception) {
                e.printStackTrace()
            }


        }
    }

    private fun updateUi() {
        //Update UI here
        try {
            Log.e("Landlord", "Details Dto==> " + Gson().toJson(userDetailsDto))
            editRegFirstName!!.setText(userDetailsDto!!.userFullName)
            editRegPhnNmbr!!.setText(PhoneNumberUtils.formatNumber(userDetailsDto!!.phoneNumber, "US"))
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    fun actionComponent() {

        editRegZipCode?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

                if (editRegZipCode!!.text.toString().length > 0) {
                    setCityState(editRegZipCode!!.text.toString().toInt())
                }
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {

            }
        })

        btnRegNxt!!.setOnClickListener { view ->
            // Hide the keyboard
            val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            validComponent()
        }

        /*spinnerRegStateIssue?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {

                if (position> 0){
                    regStateIssue= stateIssueList.get(position)

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // another interface callback
            }
        }*/

        iv_back!!.setOnClickListener {
            onBackPressed()
        }

    }

    private fun setCityState(valueZip: Int?) {
        val filterList: List<ZipCityStateModel>? = zcsList.filter { it.zip == valueZip }
        if (filterList!!.size > 0) {
            val filterPlan: ZipCityStateModel? = filterList.get(0)
            editRegCity!!.setText(filterPlan!!.city)
            editRegState!!.setText(filterPlan.state)
        } else {
            editRegZipCode?.error = getString(R.string.error_zipcode)
            editRegZipCode?.requestFocus()
            editRegCity!!.setText("")
            editRegState!!.setText("")
            return
        }
    }

    fun validComponent() {
        regFirstName = editRegFirstName!!.text.toString().trim()
        regMiddleName = editRegMiddleName!!.text.toString().trim()

        regAddress = editRegAddress!!.text.toString().trim()
        regCity = editRegCity!!.text.toString().trim()
        regState = editRegState!!.text.toString().trim()
        regZipcode = editRegZipCode!!.text.toString().trim()
        regPhnNmbr = editRegPhnNmbr!!.text.toString().trim()

        // Check the email string is empty or not
        if (regMiddleName!!.isNullOrEmpty()) {
            editRegMiddleName?.error = getString(R.string.error_email)
            editRegMiddleName?.requestFocus()
            return
        }
        if ((!Util.isEmailValid(regMiddleName!!))) {
            editRegMiddleName?.error = getString(R.string.error_valid_email)
            editRegMiddleName?.requestFocus()
            return
        }

        if (valueMandetory(applicationContext, regFirstName, editRegFirstName!!)) {
            return
        }

        if (valueMandetory(applicationContext, regAddress, editRegAddress!!)) {
            return
        }
        if (valueMandetory(applicationContext, regCity, editRegCity!!)) {
            return
        }
        if (valueMandetory(applicationContext, regState, editRegState!!)) {
            return
        }

        if (valueMandetory(applicationContext, regZipcode, editRegZipCode!!)) {
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

        /*if (regStateIssue.isNullOrEmpty()) {
            Toast.makeText(applicationContext,"Please Select State Issued", Toast.LENGTH_SHORT).show()
            return
        }*/

        //registrationPayload.stateIssued=regStateIssue

        validateApi()
    }

    @SuppressLint("CheckResult")
    private fun validateApi() {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            //Create retrofit Service
            pb_login!!.visibility = View.VISIBLE
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            val credentials = EmailCredentialAgentLink()
            credentials.landlordEmailId = regMiddleName
            val validateService: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<PropertyLinkResponseAgent> =
                validateService.checkLandlord(credentials)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e(
                        "onSuccess",
                        "checkLandlord response Dto==> " + Gson().toJson(it.responseDto)
                    )

                    pb_login!!.visibility = View.GONE
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                    if (it!!.responseDto!!.responseCode.equals(200) && it.data != null) {

                        if (it.data!!.landlordFound!! != "0") {
                            linkProperty()
                        } else {
                            try {
                                Toast.makeText(
                                    applicationContext,
                                    it.responseDto!!.responseDescription,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            finish()
                        }
                    }
                    //New added (02-02-2023) as response has been changed
                    else if (it!!.responseDto!!.responseCode == 404 && it.data != null) {
                        //Log.e("Getting","responseCode ==>"+it!!.responseDto!!.responseCode)
                        //Log.e("Getting","response Data ==> "+Gson().toJson(it.data))

                        if (it.data!!.landlordFound!! == "0") {
                            linkProperty()
                        } else {
                            try {
                                Toast.makeText(
                                    applicationContext,
                                    it.responseDto!!.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            finish()
                        }
                    } else {
                        try {
                            Toast.makeText(
                                applicationContext,
                                it.responseDto!!.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                },
                    { e ->
                        Log.e("onFailure", e.toString())
                        pb_login!!.visibility = View.GONE

                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        try {
                            e.message?.let {
                                Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
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

    @SuppressLint("CheckResult")
    private fun linkProperty() {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            //Create retrofit Service
            pb_login!!.visibility = View.VISIBLE
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )

            val credentials = FinalAgentLinkProperty()

            credentials.landlordName = regFirstName.toString()
            credentials.landlordEmailId = regMiddleName.toString()
            credentials.landlordPhoneNumber = regPhnNmbr.toString()
            credentials.propAddress1 = regAddress.toString()
            credentials.city = regCity.toString()
            credentials.state = regState.toString()
            credentials.zipCode = regZipcode.toString()
            credentials.requestedById = Kotpref.userId

            val validateService: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<FinalPropertyLinkResponse> =
                validateService.linkProperty(credentials)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    // Log.e("onSuccess", it.responseDto?.responseDescription.toString())
                    Log.e(
                        "onSuccess",
                        "linkProperty response Dto==> " + Gson().toJson(it.responseDto)
                    )

                    pb_login!!.visibility = View.GONE
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                    if (it!!.responseDto!!.responseCode.equals(200)) {
                        if (Kotpref.userRole.contains("Agent", true)) {
                            Toast.makeText(
                                this@LinkPropertyAgent,
                                it.responseDto!!.responseDescription,
                                Toast.LENGTH_SHORT
                            ).show()

                            startActivity(
                                Intent(
                                    this@LinkPropertyAgent,
                                    HomeActivityAgent::class.java
                                )
                            )
                            finish()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                it.responseDto!!.responseDescription,
                                Toast.LENGTH_SHORT
                            ).show()

                            startActivity(
                                Intent(
                                    applicationContext,
                                    HomeActivityBroker::class.java
                                )
                            )
                            finish()
                        }
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
                        pb_login!!.visibility = View.GONE

                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
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

    override fun onBackPressed() {
        super.onBackPressed()
        Util.navigationBack(this)
    }
}