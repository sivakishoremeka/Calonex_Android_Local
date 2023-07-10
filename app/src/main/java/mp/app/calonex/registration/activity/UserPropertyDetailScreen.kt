package mp.app.calonex.registration.activity

import android.content.Context
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_new_property_first_screen.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.BrokerAgentCredentials
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.UsPhoneNumberFormatter
import mp.app.calonex.common.utility.Util
import mp.app.calonex.common.utility.Util.setEditReadOnly
import mp.app.calonex.common.utility.Util.valueMandetory
import mp.app.calonex.landlord.model.ZipCityStateModel
import mp.app.calonex.landlord.activity.LoginScreen.Companion.registrationPayload3
import mp.app.calonex.landlord.response.BrokerAgentDetailResponse
import mp.app.calonex.registration.adapter.SubscriptionPlanAdapter.Companion.subscriptionPayload
import mp.app.calonex.registration.model.RegistrationPropertyPayload


class UserPropertyDetailScreen : AppCompatActivity() {
    private var editRegPropertyAddress: TextInputEditText?=null
    private var editRegPropertyCity: TextInputEditText?=null
    private var editRegPropertyState: TextInputEditText?=null
    private var editRegPropertyZipCode: TextInputEditText?=null
    private var editRegBrokerAgent: TextInputEditText?=null
    private var cbRegPrimaryAdd:CheckBox?=null
    private var editRegPropertyPhnNmbr:TextInputEditText?=null
    private var editRegPropertyUnit: TextInputEditText?=null
    private var btnRegPropertyNext:Button?=null
    private var txtPlan:TextView?=null
    private var stateList=ArrayList<String>()
    private var regPropertyAddress:String?=null
    private var regPropertyCity:String?=null
    private var regPropertyState:String?=null
    private var regPropertyZipcode:String?=null
    private var regPropertyBrkrAgent:String?=null
    private var regPropertyPhn:String?=null
    private var regPropertyUnit:String?=null
    private var zcsList=ArrayList<ZipCityStateModel>()
    private var addressCheckFlag = false
    private var isBrokerValidated : Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_property_detail_screen)
        initComponent()
        actionComponent()
    }

    fun initComponent(){
        txtPlan=findViewById(R.id.txt_plan)
        txtPlan!!.text= Kotpref.planSelected
            editRegPropertyAddress=findViewById(R.id.edit_reg_property_address)
        editRegPropertyCity=findViewById(R.id.edit_reg_property_city)
        editRegPropertyState=findViewById(R.id.edit_reg_property_state)
        editRegPropertyZipCode=findViewById(R.id.edit_reg_property_zip)
        editRegPropertyUnit=findViewById(R.id.edit_reg_property_unit)
        cbRegPrimaryAdd=findViewById(R.id.cb_reg_primary_add)
        editRegPropertyPhnNmbr=findViewById(R.id.edit_reg_property_phn)
        editRegBrokerAgent=findViewById(R.id.edit_reg_brkr_agent)
        btnRegPropertyNext=findViewById(R.id.btn_reg_property_next)
        zcsList=Util.getZipCityStateList(applicationContext)
        val addLineNumberFormatter= UsPhoneNumberFormatter(editRegPropertyPhnNmbr!!)
        editRegPropertyPhnNmbr!!.addTextChangedListener(addLineNumberFormatter)

        setEditReadOnly(editRegPropertyCity!!,false,InputType.TYPE_NULL)
        setEditReadOnly(editRegPropertyState!!,false,InputType.TYPE_NULL)
    }


    override fun onResume() {
        super.onResume()
        if(Kotpref.isRegisterConfirm){
            val gson = Gson()
            val jsonTutMap: String = gson.toJson(registrationPayload3.userPropertyRegisterDto)
            val mapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            val propertyPayload:RegistrationPropertyPayload=mapper.readValue(jsonTutMap)
            editRegPropertyAddress!!.setText(propertyPayload.address1)
              editRegPropertyCity!!.setText(propertyPayload.city)
            editRegPropertyState!!.setText(propertyPayload.state)
              editRegPropertyZipCode!!.setText(propertyPayload.zipCode)
            editRegPropertyPhnNmbr!!.setText(PhoneNumberUtils.formatNumber(propertyPayload.phoneNumber,"US"))
              editRegBrokerAgent!!.setText(propertyPayload.brokerId)
              editRegPropertyUnit!!.setText(propertyPayload.numberOfUnits)

            btnRegPropertyNext!!.setText(getString(R.string.done))

            cbRegPrimaryAdd!!.isChecked=Kotpref.isPrimaryAddress

        }
    }

    fun actionComponent(){
        cbRegPrimaryAdd!!.setOnCheckedChangeListener { compoundButton, b ->

            if(b){
                addressCheckFlag = true
                editRegPropertyAddress!!.setText(registrationPayload3.address1)
                editRegPropertyCity!!.setText(registrationPayload3.city)
                editRegPropertyState!!.setText(registrationPayload3.state)
                editRegPropertyZipCode!!.setText(registrationPayload3.zipCode)
                editRegPropertyPhnNmbr!!.setText(PhoneNumberUtils.formatNumber(registrationPayload3.phoneNumber,"US"))
                setEditReadOnly(editRegPropertyAddress!!,false,InputType.TYPE_NULL)
                setEditReadOnly(editRegPropertyZipCode!!,false,InputType.TYPE_NULL)
                setEditReadOnly(editRegPropertyPhnNmbr!!,false,InputType.TYPE_NULL)
            }else{
                addressCheckFlag = false
                    editRegPropertyAddress!!.setText("")
                editRegPropertyCity!!.setText("")
                editRegPropertyState!!.setText("")
                editRegPropertyZipCode!!.setText("")
                editRegPropertyPhnNmbr!!.setText("")
                setEditReadOnly(editRegPropertyAddress!!,true,InputType.TYPE_CLASS_TEXT)
                setEditReadOnly(editRegPropertyZipCode!!,true,InputType.TYPE_CLASS_NUMBER)
                setEditReadOnly(editRegPropertyPhnNmbr!!,true,InputType.TYPE_CLASS_NUMBER)
            }


        }
        editRegPropertyZipCode?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (editRegPropertyZipCode!!.text.toString().length > 0) {
                    setCityState(editRegPropertyZipCode!!.text.toString().toInt())
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
        btnRegPropertyNext!!.setOnClickListener {
            validComponent()

        }

        // SAMIT
        editRegBrokerAgent?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s!!.length == 11) {
                    val imm = this@UserPropertyDetailScreen.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                    var credential = BrokerAgentCredentials()
                    credential.licenceId = editRegBrokerAgent?.text.toString().trim()

                    if (credential.licenceId.equals("")) {
                        Toast.makeText(
                            applicationContext,
                            "Please enter Broker or Agent ID",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    pb_edit.visibility = View.VISIBLE
                    val brokerAgentDetails: ApiInterface =
                        ApiClient(this@UserPropertyDetailScreen).provideService(ApiInterface::class.java)
                    val apiCall: Observable<BrokerAgentDetailResponse> =
                        brokerAgentDetails.getBrokerAgentList(credential) //Test API Key

                    apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            Log.e("onSuccess", it.statusCode.toString())

                            pb_edit.visibility = View.GONE

                            if (it.data != null && it.responseDto!!.responseDescription.contains(
                                    "success"
                                )
                            ) {
                                isBrokerValidated = true
                            } else if (it.responseDto != null && it.responseDto!!.responseCode == 404) {
                                isBrokerValidated = false
                                Toast.makeText(
                                    applicationContext,
                                    "Invalid Broker/Agent IDD",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                            { e ->
                                isBrokerValidated = false
                                Log.e("onFailure", e.toString())
                                pb_edit.visibility = View.GONE
                                e.message?.let {
                                    Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT)
                                        .show()
                                }
                            })
                }
            }
        })

        // SAMIT

    }

    private fun  setCityState(valueZip:Int?) {
        val filterList:List<ZipCityStateModel>?= zcsList.filter { it.zip==valueZip}
        if(filterList!!.size>0){
            val filterPlan: ZipCityStateModel?=filterList!!.get(0)
            editRegPropertyCity!!.setText(filterPlan!!.city)
            editRegPropertyState!!.setText(filterPlan!!.state)
        }else{
            editRegPropertyZipCode?.error  = getString(R.string.error_zipcode)
            editRegPropertyZipCode?.requestFocus()
            editRegPropertyCity!!.setText("")
            editRegPropertyState!!.setText("")
            return
        }



    }


    fun validComponent() {
        regPropertyAddress=editRegPropertyAddress!!.text.toString().trim()
        regPropertyCity=editRegPropertyCity!!.text.toString().trim()
        regPropertyState=editRegPropertyState!!.text.toString().trim()
        regPropertyZipcode=editRegPropertyZipCode!!.text.toString().trim()
        regPropertyBrkrAgent=editRegBrokerAgent!!.text.toString().trim()
        regPropertyUnit=editRegPropertyUnit!!.text.toString().trim()
        regPropertyPhn=editRegPropertyPhnNmbr!!.text.toString().trim()

        if(valueMandetory(applicationContext,regPropertyAddress,editRegPropertyAddress!!)){
            return
        }
        if(valueMandetory(applicationContext,regPropertyZipcode,editRegPropertyZipCode!!)){
            return
        }
        if(valueMandetory(applicationContext,regPropertyCity,editRegPropertyCity!!)){
            return
        }

        if(valueMandetory(applicationContext,regPropertyState,editRegPropertyState!!)){
            return
        }

        // TODO fix number length issue
        /*if(valueMandetory(applicationContext,regPropertyPhn,editRegPropertyPhnNmbr!!)){
            return
        }

        if(regPropertyPhn!!.length!=14){
            editRegPropertyPhnNmbr?.error  = getString(R.string.error_phn)
            editRegPropertyPhnNmbr?.requestFocus()
            return
        }*/

        if (regPropertyBrkrAgent!!.length > 0) {
            if (!isBrokerValidated) {
                editRegBrokerAgent?.error = getString(R.string.error_brkr)
                editRegBrokerAgent?.requestFocus()
                return
            }
        }

        if(regPropertyBrkrAgent!!.length>0 && regPropertyBrkrAgent!!.length!=11){
            editRegBrokerAgent?.error  = getString(R.string.error_brkr)
            editRegBrokerAgent?.requestFocus()
            return
        }

        if(valueMandetory(applicationContext,regPropertyUnit,editRegPropertyUnit!!)){
            return
        }

        if (isBrokerValidated && regPropertyBrkrAgent!!.length==11) {
            registrationPayload3.signupThroughInvite = true
        }

        if(!subscriptionPayload.numberOfUnits!!.equals("100+")){

            if(regPropertyUnit!!.toInt() > subscriptionPayload.numberOfUnits!!.toInt()){
                editRegPropertyUnit?.error  = getString(R.string.error_no_unit)
                editRegPropertyUnit?.requestFocus()
                return
            }
        }



        var registrationPropertyPayload=RegistrationPropertyPayload()
        registrationPropertyPayload.address1=regPropertyAddress
        registrationPropertyPayload.city=regPropertyCity
        registrationPropertyPayload.state=regPropertyState
        registrationPropertyPayload.zipCode=regPropertyZipcode
        registrationPropertyPayload.phoneNumber=regPropertyPhn!!.filter { it.isDigit() }
        registrationPropertyPayload.brokerId=regPropertyBrkrAgent
        registrationPropertyPayload.numberOfUnits=regPropertyUnit
        if (addressCheckFlag) {
            registrationPropertyPayload.isPrimaryAddress = true
        }

        val gson = Gson()
        val valueString=gson.toJson(registrationPropertyPayload)
        val jsonObject: JsonObject = JsonParser().parse(valueString).getAsJsonObject()
        registrationPayload3.userPropertyRegisterDto=jsonObject
        Kotpref.isPrimaryAddress=cbRegPrimaryAdd!!.isChecked
        if(Kotpref.isRegisterConfirm){
            onBackPressed()
        }else {
            Util.navigationNext(this, UserDocumentScreen::class.java)
        }

    }

    

    override fun onBackPressed() {
        super.onBackPressed()
        if(Kotpref.isRegisterConfirm){
            Kotpref.isRegisterConfirm=false

        }
        Util.navigationBack(this)
    }
}