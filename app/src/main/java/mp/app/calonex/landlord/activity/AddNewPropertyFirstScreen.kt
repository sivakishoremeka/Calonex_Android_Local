package mp.app.calonex.landlord.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_new_property_first_screen.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.BrokerAgentCredentials
import mp.app.calonex.common.apiCredentials.GetAvailableUnitApiCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.common.utility.Util.alertOkMessage
import mp.app.calonex.common.utility.Util.navigationNext
import mp.app.calonex.landlord.activity.UploadImageScreen.Companion.listOfMultipart
import mp.app.calonex.landlord.adapter.*
import mp.app.calonex.landlord.adapter.PropertyListAdapter.Companion.propertyDetailResponse
import mp.app.calonex.landlord.model.*
import mp.app.calonex.landlord.response.AddPropertyPostResponse
import mp.app.calonex.landlord.response.AvailableUnitResponse
import mp.app.calonex.landlord.response.BrokerAgentDetailResponse
import mp.app.calonex.service.FeaturesService.Companion.allModel
import mp.app.calonex.tenant.response.ResponseDtoResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.*

class AddNewPropertyFirstScreen : CxBaseActivity2() {

    private var rvFeature: RecyclerView? = null
    private var rvParking: RecyclerView? = null
    private var spinner_bt: Spinner? = null
    private var addPhoto: TextView? = null
    private var addFile: TextView? = null
    private var selectedState: String? = null
    public var priId: String? = null

    private var pb_edit: ProgressBar? = null

    private var ownerFile: File? = null
    private var zcsList = ArrayList<ZipCityStateModel>()
    var count: Int = 0
    private var isBrokerValidated: Boolean = false

    var propertyId: String? = null

    var originalUnitQuantity: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_property_first_screen)

        result = AddPropertyPostPayload()
        editProperty = EditPropertyAddBroker()

        pb_edit = findViewById(R.id.pb_edit);
        rvFeature = findViewById(R.id.rv_feature)
        rvParking = findViewById(R.id.rv_parking)
        addPhoto = findViewById(R.id.add_photo)
        addFile = findViewById(R.id.add_file)
        spinner_bt = findViewById(R.id.ld_building_type_spinner)
        rvFeature!!.layoutManager = GridLayoutManager(this, 2)
        rvParking!!.layoutManager = GridLayoutManager(this, 2)
        supportActionBar?.hide()

        zcsList = Util.getZipCityStateList(applicationContext)
        Util.setEditReadOnly(edit_city!!, false, InputType.TYPE_NULL)
        Util.setEditReadOnly(edit_state!!, false, InputType.TYPE_NULL)

        edit_total_unit.setText("1")

        isEditing = intent.getBooleanExtra("isEdit", false)
        isLinkRequested = intent.getBooleanExtra(getString(R.string.is_link_request), false)
        linkedProperty =
            intent.getSerializableExtra(getString(R.string.link_request_property)) as NotificationLinkRequestModel?
        allModel?.let { updateUi(it) }
        mArrayUri.clear()
        mArrayFileUri.clear()
        filePropertyImageList.clear()
        filePropertyOwnershipList.clear()

        iv_back.setOnClickListener {
            onBackPressed()
        }

        getAvailableUnit()
        actionComponent()
        startHandler()

        Log.e("LICENCE-ID", Kotpref.linkBrokerLicenceNo.toString())

        if (!Kotpref.linkBrokerLicenceNo.toString().trim().isNullOrEmpty()) {
            link_broker_switch.isChecked = true
            getBrokerDetails(Kotpref.linkBrokerLicenceNo.toString().trim())
        }
    }

    private fun updateValue() {
        result.userCatalogID = Kotpref.userId
        result.property_id = ""
        result.address = edit_bill_add?.text.toString().trim()
        result.city = edit_city?.text.toString().trim()
        result.state = edit_state?.text.toString().trim()
        result.zipCode = edit_zip?.text.toString().trim()
        result.description = edit_description?.text.toString().trim()
        result.numberOfUnits = edit_total_unit?.text.toString().trim()
        result.primaryContactId = priId

        numOfUnits = edit_total_unit?.text.toString().trim()


        if (buildingFeatureIdList.isNotEmpty()) {
            result.buildingFeatureDTO = JsonObject()
            result.buildingFeatureDTO?.add("propertyBuildingFeatureID", jsonElementsFeature)
        } else
            result.buildingFeatureDTO = null

        Log.e("Parking", "parkingIdList>>" + Gson().toJson(parkingIdList))

        if (parkingIdList.isNotEmpty()) {
            result.parkingTypeDTO = JsonObject()
            result.parkingTypeDTO?.add("parkingTypeId", jsonElementsParking)
        } else
            result.parkingTypeDTO = null

        if (isBrokerValidated && link_broker_switch.isChecked) {
            result.brokerOrAgentLiscenceID = edit_broker_id?.text.toString().trim()
            result.primaryContact = edit_primary_contact?.text.toString().trim()
            result.broker = edit_broker_name?.text.toString().trim()
            result.brokerEmailID = edit_broker_email?.text.toString().trim()
            result.brokerPhone = edit_broker_phone?.text.toString().trim()
            result.linkedByBrokerAgent = true

        } else {
            result.linkedByBrokerAgent = false
        }
    }

    private fun actionComponent() {

        link_broker_switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                broker_id_view.visibility = View.VISIBLE
                edit_broker_id.visibility = View.VISIBLE
                if (isBrokerValidated) {
                    broker_view.visibility = View.VISIBLE
                }
            } else {
                //New added on 03-02-2023 due to un availability of broker/agent unlink API
                try {
                    if (propertyDetailResponse.linkedByBrokerAgent) {
                        Toast.makeText(
                            this,
                            "You can't un link the broker/agent after link",
                            Toast.LENGTH_SHORT
                        ).show()
                        link_broker_switch.isChecked = true

                    } else {
                        broker_id_view.visibility = View.GONE
                        edit_broker_id.visibility = View.GONE
                        broker_view.visibility = View.GONE
                    }
                } catch (e: Exception) {
                    e.printStackTrace()

                    broker_id_view.visibility = View.GONE
                    edit_broker_id.visibility = View.GONE
                    broker_view.visibility = View.GONE
                }


            }

        }

        edit_zip?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

                if (edit_zip!!.text.toString().length > 2) {
                    setCityState(edit_zip!!.text.toString().toInt())
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

        btn_next!!.setOnClickListener { view ->

            if (edit_bill_add.text!!.isEmpty()) {
                edit_bill_add?.error = getString(R.string.error_mandetory)
                edit_bill_add?.requestFocus()
                return@setOnClickListener
            }
            if (edit_zip.text!!.isEmpty()) {
                edit_zip?.error = getString(R.string.error_mandetory)
                edit_zip?.requestFocus()
                return@setOnClickListener
            }
            if (edit_city.text!!.isEmpty()) {
                edit_city?.error = getString(R.string.error_mandetory)
                edit_city?.requestFocus()
                return@setOnClickListener
            }
            /* if (selectedState.isNullOrEmpty()) {
                 Toast.makeText(applicationContext, "Please Select State", Toast.LENGTH_SHORT).show()
                 return@setOnClickListener
             }*/

            if (edit_description.text!!.isEmpty()) {
                edit_description?.error = getString(R.string.error_mandetory)
                edit_description?.requestFocus()
                return@setOnClickListener
            }
            if (edit_total_unit.text!!.isEmpty()) {
                edit_total_unit?.error = getString(R.string.error_mandetory)
                edit_total_unit?.requestFocus()
                return@setOnClickListener
            }

            if (spinner_bt?.selectedItemPosition == 0) {

                Toast.makeText(
                    applicationContext,
                    "Please select building type",
                    Toast.LENGTH_SHORT
                ).show()

                spinner_bt?.requestFocus()


                return@setOnClickListener
            }

            if (result.buildingTypeId!!.equals(0) && result.buildingTypeId!!.equals(-1)) {
                Toast.makeText(
                    applicationContext,
                    "Please Select Building Type",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            if (link_broker_switch.isChecked) {
                if (!isBrokerValidated) {
                    Toast.makeText(
                        applicationContext,
                        "Invalid Broker/Agent ID",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
            }
            if (!Kotpref.subscriptionActive) {
                if (edit_total_unit.text!!.toString().toInt() > 0) {
                    alertOkMessage(
                        this@AddNewPropertyFirstScreen,
                        getString(R.string.alert),
                        getString(R.string.tag_subscribe_inactive)
                    )
                    return@setOnClickListener
                }
            }

            if (Kotpref.userRole.equals("landlord") && Integer.parseInt(edit_total_unit.text!!.toString()) > Integer.parseInt(
                    Kotpref.unitNumber
                )
            ) {
                alertOkMessage(
                    this@AddNewPropertyFirstScreen,
                    getString(R.string.alert),
                    "Number of unit excesses" + Kotpref.unitNumber
                )
                return@setOnClickListener
            }


            nextClick()

        }

        /*spinnerState?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {

                if (position > 0) {
                    selectedState = stateList.get(position)

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // another interface callback
            }
        }*/

        addPhoto?.setOnClickListener { view ->
            if ((mArrayUri.size + mArrayLiveUri.size) >= 5) {
                Toast.makeText(this, "Maximum 5 images are allowed.", Toast.LENGTH_LONG).show()

            } else {
                val intent = Intent(this, UploadImageScreen::class.java)
                startActivity(intent)
                navigationNext(this)
            }

        }

        addFile?.setOnClickListener { view ->
            /*val intentFile = Intent()
            intentFile.setAction(Intent.ACTION_GET_CONTENT)
            //intentFile.setType("application/pdf")
            intentFile.setType("image/jpeg")
            startActivityForResult(intentFile, 2)*/

            if ((mArrayFileUri.size + mArrayFileLiveUri.size) >= 1) {
                Toast.makeText(this, "Maximum 1 document is allowed.", Toast.LENGTH_LONG).show()
            } else {
                val intent = Intent(this, UploadDocumentScreen::class.java)
                startActivity(intent)
                navigationNext(this)
            }
        }

        property_skip.setOnClickListener {
            var availableUnits: Int = 0
            var requestedUnits = edit_total_unit?.text.toString().toInt()

            availableUnits = Integer.parseInt(Kotpref.unitNumber)

            if (requestedUnits == 0) {
                alertOkMessage(
                    this@AddNewPropertyFirstScreen,
                    getString(R.string.alert),
                    "Please mention the number units you want to add or edit"
                )
                return@setOnClickListener
            }
            if (requestedUnits > originalUnitQuantity) {
                if ((requestedUnits - originalUnitQuantity) > availableUnits) {
                    /*alertOkMessage(
                        this@AddNewPropertyFirstScreen,
                        getString(R.string.alert),
                        "You can't add units more than you are permitted")*/

                    val builder = AlertDialog.Builder(this)
                    builder.setTitle(getString(R.string.alert))
                    builder.setMessage("You can not add a new unit as your subscription limit exceed please update your plan")
                    builder.setPositiveButton("Ok") { dialog, which ->
                        dialog.dismiss()
                        val intent = Intent(this, SubscribePlanDetailScreen::class.java)
                        intent.putExtra("fromAddUnitPage", true)
                        startActivity(intent)
                        finish()
                    }
                    /*builder.setNegativeButton(getString(R.string.no)) { dialog, which ->
                        dialog.dismiss()
                    }*/
                    val dialog: AlertDialog = builder.create()
                    dialog.show()

                    return@setOnClickListener
                }
            } else if (requestedUnits < originalUnitQuantity) {
                alertOkMessage(
                    this@AddNewPropertyFirstScreen,
                    getString(R.string.alert),
                    "You can't remove units once they are added"
                )
                return@setOnClickListener
            }
            if (edit_bill_add.text!!.isEmpty()) {
                edit_bill_add?.error = getString(R.string.error_mandetory)
                edit_bill_add?.requestFocus()
                return@setOnClickListener
            }
            if (edit_zip.text!!.isEmpty()) {
                edit_zip?.error = getString(R.string.error_mandetory)
                edit_zip?.requestFocus()
                return@setOnClickListener
            }
            if (edit_city.text!!.isEmpty()) {
                edit_city?.error = getString(R.string.error_mandetory)
                edit_city?.requestFocus()
                return@setOnClickListener
            }
            if (spinner_bt?.selectedItemPosition == 0) {

                Toast.makeText(
                    applicationContext,
                    "Please select building type",
                    Toast.LENGTH_SHORT
                ).show()

                spinner_bt?.requestFocus()


                return@setOnClickListener
            }
            if (edit_description.text!!.isEmpty()) {
                edit_description?.error = getString(R.string.error_mandetory)
                edit_description?.requestFocus()
                return@setOnClickListener
            }
            if (edit_total_unit.text!!.isEmpty()) {
                edit_total_unit?.error = getString(R.string.error_mandetory)
                edit_total_unit?.requestFocus()
                return@setOnClickListener
            }
            if (result.buildingTypeId!!.equals(0) && result.buildingTypeId!!.equals(-1)) {
                Toast.makeText(
                    applicationContext,
                    "Please Select Building Type",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            if (link_broker_switch.isChecked) {
                if (!isBrokerValidated) {
                    Toast.makeText(
                        applicationContext,
                        "Invalid Broker/Agent ID",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
            }
            if (!Kotpref.subscriptionActive) {
                if (edit_total_unit.text!!.toString().toInt() > 0) {
                    alertOkMessage(
                        this@AddNewPropertyFirstScreen,
                        getString(R.string.alert),
                        getString(R.string.tag_subscribe_inactive)
                    )
                    return@setOnClickListener
                }
            }

            updateValue()

            val intent = Intent(this, UnitDescriptionDynamicTabActivity::class.java)
            startActivity(intent)
            // navigationNext(this)
        }

        edit_broker_id.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s!!.length == 11) {
                    val imm =
                        this@AddNewPropertyFirstScreen.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                    var credential = BrokerAgentCredentials()
                    credential.licenceId = edit_broker_id?.text.toString().trim()

                    if (credential.licenceId.equals("")) {
                        Toast.makeText(
                            applicationContext,
                            "Please enter Broker or Agent ID",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    pb_edit!!.visibility = View.VISIBLE
                    val brokerAgentDetails: ApiInterface =
                        ApiClient(this@AddNewPropertyFirstScreen).provideService(ApiInterface::class.java)
                    val apiCall: Observable<BrokerAgentDetailResponse> =
                        brokerAgentDetails.getBrokerAgentList(credential) //Test API Key

                    apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            Log.e("onSuccess", it.statusCode.toString())

                            pb_edit!!.visibility = View.GONE

                            if (it.data != null && it.responseDto!!.responseDescription.contains(
                                    "success"
                                )
                            ) {

                                isBrokerValidated = true
                                broker_view.visibility = View.VISIBLE
                                edit_primary_contact.setText(it.data!!.primaryContact)
                                edit_broker_name.setText(it.data!!.brokerName)
                                edit_broker_phone.setText(it.data!!.brokerPhone)
                                edit_broker_email.setText(it.data!!.brokerEmailID)
                                priId = it.data!!.primaryContactId
                                /*TODO GANRAM*/

                            } else if (it.responseDto != null && it.responseDto!!.responseCode == 404) {
                                isBrokerValidated = false
                                Toast.makeText(
                                    applicationContext,
                                    "Invalid Broker/Agent ID",
                                    Toast.LENGTH_SHORT
                                ).show()
                                broker_view.visibility = View.GONE

                            }


                        },
                            { e ->
                                broker_view.visibility = View.GONE
                                isBrokerValidated = false
                                Log.e("onFailure", e.toString())
                                pb_edit!!.visibility = View.GONE
                                e.message?.let {
                                    Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT)
                                        .show()

                                }
                            })
                }
            }


        })


        edit_broker_id?.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_SEARCH ||
                i == EditorInfo.IME_ACTION_DONE ||
                keyEvent != null &&
                keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER
            ) {
                if (keyEvent == null || !keyEvent.isShiftPressed() || keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    // the user is done typing.
                    val imm =
                        this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                    var credential = BrokerAgentCredentials()
                    credential.licenceId = edit_broker_id?.text.toString().trim()

                    if (credential.licenceId.equals("")) {
                        Toast.makeText(
                            applicationContext,
                            "Please enter Broker or Agent ID",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@setOnEditorActionListener false
                    }
                    pb_edit!!.visibility = View.VISIBLE
                    val brokerAgentDetails: ApiInterface =
                        ApiClient(this).provideService(ApiInterface::class.java)
                    val apiCall: Observable<BrokerAgentDetailResponse> =
                        brokerAgentDetails.getBrokerAgentList(credential) //Test API Key

                    apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            Log.e("onSuccess", it.statusCode.toString())

                            pb_edit!!.visibility = View.GONE

                            if (it.data != null && it.responseDto!!.responseDescription.contains(
                                    "success"
                                )
                            ) {

                                isBrokerValidated = true
                                broker_view.visibility = View.VISIBLE
                                edit_primary_contact.setText(it.data!!.primaryContact)
                                edit_broker_name.setText(it.data!!.brokerName)
                                edit_broker_phone.setText(it.data!!.brokerPhone)
                                edit_broker_email.setText(it.data!!.brokerEmailID)

                            } else if (it.responseDto != null && it.responseDto!!.responseCode == 404) {
                                isBrokerValidated = false
                                Toast.makeText(
                                    applicationContext,
                                    "Invalid Broker/Agent ID",
                                    Toast.LENGTH_SHORT
                                ).show()
                                broker_view.visibility = View.GONE

                            }


                        },
                            { e ->
                                broker_view.visibility = View.GONE
                                isBrokerValidated = false
                                Log.e("onFailure", e.toString())
                                pb_edit!!.visibility = View.GONE
                                e.message?.let {
                                    Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT)
                                        .show()

                                }
                            })
                    return@setOnEditorActionListener true // consume.
                }
            }



            false
        }

    }

    private fun getAvailableUnit() {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            //Create retrofit Service
            var credentials = GetAvailableUnitApiCredential()

            credentials!!.userCatalogId = Kotpref.userId
            var propertyListService: ApiInterface =
                ApiClient(applicationContext).provideService(ApiInterface::class.java)
            var apiCall: Observable<AvailableUnitResponse> =
                propertyListService.getAvailableUnit(credentials)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it.data != null) {
                        Kotpref.unitNumber = it.data.toString()
                    }
                }, { e ->
                    Kotpref.unitNumber = "0"
                })

            /*
            RxAPICallHelper().call(apiCall, object : RxAPICallback<AvailableUnitResponse> {
                override fun onSuccess(availableUnitResponse: AvailableUnitResponse) {
                    Kotpref.unitNumber=  availableUnitResponse.data.toString()
                }

                override fun onFailed(t: Throwable) {
                    // show error
                    Log.e("onFailure", t.toString())

                    Kotpref.unitNumber="0"
                    /*
                    try {
                        Util.apiFailure(applicationContext, t)
                    } catch (e: Exception) {
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.error_server),
                            Toast.LENGTH_SHORT
                        ).show()
                    }*/

                }
            }) */
        } else {
            Toast.makeText(
                applicationContext,
                getString(R.string.error_network),
                Toast.LENGTH_SHORT
            ).show()

        }
    }

    private fun setCityState(valueZip: Int?) {
        val filterList: List<ZipCityStateModel>? = zcsList.filter { it.zip == valueZip }
        if (filterList!!.size > 0) {
            val filterPlan: ZipCityStateModel? = filterList!!.get(0)
            edit_city!!.setText(filterPlan!!.city)
            edit_state!!.setText(filterPlan!!.state)
        } else {
            edit_zip?.error = getString(R.string.error_zipcode)
            edit_zip?.requestFocus()
            edit_city!!.setText("")
            edit_state!!.setText("")
            return
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var uri: Uri? = null

        if (data != null && data.data != null) {
            uri = data.data!!
            var file = File(uri.toString())
            var path: String = uri.path!!
            ownerFile = file
            listOfMultipart.add(prepareFilePart("files", file))
        }
    }


    @NonNull
    private fun prepareFilePart(
        partName: String,
        file: File
    ): MultipartBody.Part {


        var requestFile: RequestBody =
            file.asRequestBody("application/pdf".toMediaTypeOrNull())

        var propertyImagePart: MultipartBody.Part? = null
        propertyImagePart = MultipartBody.Part.createFormData("files", file.name, requestFile)
        return propertyImagePart

    }


    private fun updateUi(t: ApiAllModel) {

        if (isEditing) {

            txt_title.setText("Edit Property")
            edit_bill_add.setText(PropertyDetailScreen.propertyDetailResponseLocal.address)
            edit_city.setText(PropertyDetailScreen.propertyDetailResponseLocal.city)
            edit_state.setText(PropertyDetailScreen.propertyDetailResponseLocal.state)
            edit_zip.setText(PropertyDetailScreen.propertyDetailResponseLocal.zipCode)
            edit_description.setText(PropertyDetailScreen.propertyDetailResponseLocal.description)

            if (PropertyDetailScreen.propertyDetailResponseLocal.propertyUnitDetailsDTO.size == 0) {
                edit_total_unit.setText("1")
            } else {
                edit_total_unit.setText(PropertyDetailScreen.propertyDetailResponseLocal.propertyUnitDetailsDTO.size.toString())
            }
            originalUnitQuantity =
                PropertyDetailScreen.propertyDetailResponseLocal.propertyUnitDetailsDTO.size

            property_skip.visibility = View.VISIBLE
            propertyIdFirst = PropertyDetailScreen.propertyDetailResponseLocal.propertyId.toString()
            numOfUnits =
                PropertyDetailScreen.propertyDetailResponseLocal.propertyUnitDetailsDTO.size.toString()

            if (PropertyDetailScreen.propertyDetailResponseLocal.linkedByBrokerAgent) {
                link_broker_switch.isChecked = true
                broker_id_view.visibility = View.VISIBLE
                edit_broker_id.visibility = View.VISIBLE
                broker_view.visibility = View.VISIBLE
                /*edit_primary_contact.setText(PropertyDetailScreen.propertyDetailResponseLocal.primaryContact)
                edit_broker_name.setText(PropertyDetailScreen.propertyDetailResponseLocal.broker)
                edit_broker_phone.setText(
                    PhoneNumberUtils.formatNumber(
                        PropertyDetailScreen.propertyDetailResponseLocal.brokerPhone,
                        "US"
                    )
                )
                edit_broker_email.setText(PropertyDetailScreen.propertyDetailResponseLocal.brokerEmailID)
                edit_broker_id.setText(PropertyDetailScreen.propertyDetailResponseLocal.brokerOrAgentLiscenceID)*/

                getBrokerDetails(PropertyDetailScreen.propertyDetailResponseLocal.brokerOrAgentLiscenceID)
                isBrokerValidated = true
            } else {
                link_broker_switch.isChecked = false
                isBrokerValidated = false
            }
            //spinnerState!!.setSelection(stateList.indexOf(PropertyDetailScreen.propertyDetailResponseLocal.state))

            mArrayLiveUri.clear()
            mArrayFileLiveUri.clear()
            for (i in 0 until PropertyDetailScreen.listPropertyFilesLocal.size) {
                if (PropertyDetailScreen.listPropertyFilesLocal[i].uploadFileType == getString(R.string.key_img_property)) {
                    mArrayLiveUri.add(Uri.parse(getString(R.string.base_url_img2) + PropertyDetailScreen.listPropertyFilesLocal[i].fileName))
                } else if (PropertyDetailScreen.listPropertyFilesLocal[i].uploadFileType == getString(
                        R.string.key_img_po
                    )
                ) {
                    mArrayFileLiveUri.add(Uri.parse(getString(R.string.base_url_img2) + PropertyDetailScreen.listPropertyFilesLocal[i].fileName))
                }
            }

            var adapter: ImgListForLandlordAdapter? = null
            var adapterFile: ImgListForLandlordAdapter? = null
            if (mArrayLiveUri.size > 0) {
                adapter = ImgListForLandlordAdapter(this, mArrayLiveUri)
                rv_photos_from_api?.layoutManager = GridLayoutManager(this, 3)
                rv_photos_from_api?.adapter = adapter
            } else {
                mArrayLiveUri.clear()
                adapter = ImgListForLandlordAdapter(this, mArrayLiveUri)
                rv_photos_from_api?.layoutManager = GridLayoutManager(this, 3)
                rv_photos_from_api?.adapter = adapter
            }

            if (mArrayFileLiveUri.size > 0) {
                adapterFile = ImgListForLandlordAdapter(this, mArrayFileLiveUri)
                rv_files_from_api?.layoutManager = GridLayoutManager(this, 3)
                rv_files_from_api?.adapter = adapterFile
            } else {
                mArrayFileLiveUri.clear()
                adapterFile = ImgListForLandlordAdapter(this, mArrayFileLiveUri)
                rv_files_from_api?.layoutManager = GridLayoutManager(this, 3)
                rv_files_from_api?.adapter = adapterFile
            }

        }

        if (isLinkRequested) {
            txt_title.setText("Add New Property")
            edit_bill_add.setText(linkedProperty!!.propAddress)
            edit_city.setText(linkedProperty!!.city)
            edit_state.setText(linkedProperty!!.state)
            edit_zip.setText(linkedProperty!!.zipCode)
            property_skip.visibility = View.GONE
            //spinnerState!!.setSelection(stateList.indexOf(linkedProperty!!.state))
            result.linkedByBrokerAgent = isLinkRequested
            result.notificationId =
                intent.getStringExtra(getString(R.string.notification_id)).toString()
            result.propertyLinkRequestId = linkedProperty?.propertyLinkRequestId.toString()

        }

        Log.e("checkValue", t.toString())
        allModel = t

        if (allModel?.buildingFeatureListResponse?.data == null) {
            return
        }

        var featureAdapter =
            allModel?.buildingFeatureListResponse?.data?.let { FeatureAdapter(it, Building) }

        var parkingAdapter =
            allModel?.parkingTypeListResponse?.data?.let {
                ParkingAdapter(
                    applicationContext,
                    it,
                    Parking
                )
            }

        rvFeature?.adapter = featureAdapter
        rvParking?.adapter = parkingAdapter

        if (isEditing) {
            Handler().postDelayed({
                for (i in 0 until allModel?.buildingFeatureListResponse!!.data.size) {
                    val viewHolder = rvFeature?.findViewHolderForAdapterPosition(i)
                    val textView = (viewHolder as FeatureAdapter.ViewHolder).txtFeature
                    var textValue = textView.text
                    for (j in 0 until PropertyDetailScreen.propertyDetailResponseLocal.buildingFeatureDTO.size) {
                        if (PropertyDetailScreen.propertyDetailResponseLocal.buildingFeatureDTO[j].buildingFeatureName.equals(
                                textValue
                            )
                        ) {
                            rvFeature?.findViewHolderForAdapterPosition(i)?.itemView?.performClick()
                        }
                    }
                }

                for (i in 0 until allModel?.parkingTypeListResponse!!.data!!.size) {
                    val viewHolder = rvParking?.findViewHolderForAdapterPosition(i)
                    val textView = (viewHolder as ParkingAdapter.ViewHolder).txtParking
                    var textValue = textView.text
                    for (j in 0 until PropertyDetailScreen.propertyDetailResponseLocal.parkingTypeDTO.size) {
                        if (PropertyDetailScreen.propertyDetailResponseLocal.parkingTypeDTO[j].parkingTypeName.equals(
                                textValue
                            )
                        ) {
                            rvParking?.findViewHolderForAdapterPosition(i)?.itemView?.performClick()
                        }
                    }
                }


            }, 1000)
        }

        val buildingTypeList: MutableList<BuildingType> = ArrayList()
        buildingTypeList.add(0, BuildingType(-1, "Select Building Type"))
        for (i in 1..allModel!!.buildingTypeListResponse.data.size) {
            buildingTypeList.add(i, allModel!!.buildingTypeListResponse.data[i - 1])
        }

        if (spinner_bt != null) {

            var spinnerAdapter: CustomDropDownAdapter =
                CustomDropDownAdapter(this, buildingTypeList)
            spinner_bt!!.adapter = spinnerAdapter

            if (isEditing) {

                spinner_bt!!.setSelection(PropertyDetailScreen.propertyDetailResponseLocal.buildingTypeId.toInt())

                for (i in 0 until buildingTypeList.size - 1) {
                    if (buildingTypeList[i].buildingTypeId == PropertyDetailScreen.propertyDetailResponseLocal.buildingTypeId) {
                        spinner_bt!!.setSelection(i + 1)
                        result.buildingTypeId = buildingTypeList[i + 1].buildingTypeId
                    }
                }
            }




            spinner_bt?.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    if (buildingTypeList[position].buildingTypeId.toInt() != -1) {
                        result.buildingTypeId = buildingTypeList[position].buildingTypeId
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            })
        }

    }

    private fun nextClick() {
        var availableUnits: Int = 0
        var requestedUnits = edit_total_unit?.text.toString().toInt()

        availableUnits = Integer.parseInt(Kotpref.unitNumber)

        if (requestedUnits == 0) {
            alertOkMessage(
                this@AddNewPropertyFirstScreen,
                getString(R.string.alert),
                "Please mention the number units you want to add or edit"
            )
            return
        }

        if (requestedUnits > originalUnitQuantity) {
            if ((requestedUnits - originalUnitQuantity) > availableUnits) {
                /*alertOkMessage(
                    this@AddNewPropertyFirstScreen,
                    getString(R.string.alert),
                    "You can't add units more than you are permitted")*/

                val builder = AlertDialog.Builder(this)
                builder.setTitle(getString(R.string.alert))
                builder.setMessage("You can not add a new unit as your subscription limit exceed please update your plan")
                builder.setPositiveButton("Ok") { dialog, which ->
                    dialog.dismiss()
                    val intent = Intent(this, SubscribePlanDetailScreen::class.java)
                    intent.putExtra("fromAddUnitPage", true)
                    startActivity(intent)
                    finish()
                }
                /*builder.setNegativeButton(getString(R.string.no)) { dialog, which ->
                    dialog.dismiss()
                }*/
                val dialog: AlertDialog = builder.create()
                dialog.show()

                return
            }
        } else if (requestedUnits < originalUnitQuantity) {
            alertOkMessage(
                this@AddNewPropertyFirstScreen,
                getString(R.string.alert),
                "You can't remove units once they are added"
            )
            return
        }

        pb_edit!!.visibility = View.VISIBLE

        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )

        updateValue()

        val addPropertyCall: ApiInterface = ApiClient(this).provideService(ApiInterface::class.java)

        if (isEditing) {
            try {
                pb_edit!!.visibility = View.GONE
                result.propertyId = propertyIdFirst

                Kotpref.propertyId = propertyIdFirst as String

                for (item in filePropertyImageList) {
                    uploadPropertyDoc(item, false)
                }

                for (item in filePropertyOwnershipList) {
                    uploadPropertyDoc(item, true)
                }

                try {
                    val resultForAddProperty = AddPropertyPostPayload()
                    resultForAddProperty.address = result.address
                    resultForAddProperty.buildingFeatureDTO = result.buildingFeatureDTO
                    resultForAddProperty.buildingTypeId = result.buildingTypeId
                    resultForAddProperty.city = result.city
                    resultForAddProperty.description = result.description
                    resultForAddProperty.parkingTypeDTO = result.parkingTypeDTO
                    resultForAddProperty.state = result.state
                    resultForAddProperty.userCatalogID = result.userCatalogID
                    resultForAddProperty.zipCode = result.zipCode

                    resultForAddProperty.linkedByBrokerAgent = result.linkedByBrokerAgent
                    resultForAddProperty.linkedByBrokerAgentOnAdd = result.linkedByBrokerAgentOnAdd
                    resultForAddProperty.propertyId = propertyIdFirst

                    val apiCall: Observable<AddPropertyPostResponse> =
                        addPropertyCall.editPropertyDetails(resultForAddProperty)

                    val subscribe =
                        apiCall.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                Log.e("onSuccess ass", it.responseCode.toString())

                                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                                if (it.data?.has("data")!!) {
                                    if (it.data?.get("data") != null) {
                                        propertyIdFirst = it.data!!.get("data").toString()
                                        Kotpref.propertyId = propertyIdFirst as String
                                        count = 0
                                        for (item in filePropertyImageList) {
                                            uploadPropertyDoc(item, false)
                                        }
                                        for (item in filePropertyOwnershipList) {
                                            uploadPropertyDoc(item, true)
                                        }
                                        /*if (ownerFile != null) {
                                            uploadPropertyDoc(ownerFile!!, false)
                                        }*/
                                    }
                                }

                                pb_edit!!.visibility = View.GONE
                                Toast.makeText(
                                    applicationContext,
                                    "Property Edited Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()

                                // Now if broker/agent is requested, add the broker information

                                if (isBrokerValidated) {
                                    addBrokerOrAgent()
                                } else {
                                    val intent =
                                        Intent(
                                            this,
                                            UnitDescriptionDynamicTabActivity::class.java
                                        )
                                    startActivity(intent)
                                }
                            },
                                { e ->
                                    pb_edit!!.visibility = View.GONE
                                    Log.e("error", e.message.toString())
                                    Toast.makeText(
                                        applicationContext,
                                        "Unable to add the property. Please contact Calonex",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                })
                } catch (e: Exception) {
                    e.printStackTrace()
                    Util.alertOkIntentMessage(
                        this@AddNewPropertyFirstScreen,
                        getString(R.string.alert),
                        "Currently property can not be added with image, continue without image.",
                        UnitDescriptionDynamicTabActivity::class.java
                    )
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Util.alertOkIntentMessage(
                    this@AddNewPropertyFirstScreen,
                    getString(R.string.alert),
                    "Currently property can not be added with image, continue without image.",
                    UnitDescriptionDynamicTabActivity::class.java
                )
            }
        } else {
            try {
                val resultForAddProperty = AddPropertyPostPayload()
                resultForAddProperty.address = result.address
                resultForAddProperty.buildingFeatureDTO = result.buildingFeatureDTO
                resultForAddProperty.buildingTypeId = result.buildingTypeId
                resultForAddProperty.city = result.city
                resultForAddProperty.description = result.description
                resultForAddProperty.parkingTypeDTO = result.parkingTypeDTO
                resultForAddProperty.state = result.state
                resultForAddProperty.userCatalogID = result.userCatalogID
                resultForAddProperty.zipCode = result.zipCode

                resultForAddProperty.linkedByBrokerAgent = result.linkedByBrokerAgent
                resultForAddProperty.linkedByBrokerAgentOnAdd = result.linkedByBrokerAgentOnAdd

                /*resultForAddProperty.buildingFeatures = result.buildingFeatures  // ""
                resultForAddProperty.parking = result.parking
                resultForAddProperty.propertyId = result.propertyId
                resultForAddProperty.address2 = result.address2

                resultForAddProperty.propertyImages = result.propertyImages  // null
                resultForAddProperty.propertyLease = result.propertyLease*/

                val apiCall: Observable<AddPropertyPostResponse> =
                    addPropertyCall.addProperty(resultForAddProperty)

                val subscribe =
                    apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            Log.e("onSuccess ass", it.responseCode.toString())
                            if (it.data?.has("data")!!) {
                                if (it.data?.get("data") != null) {
                                    propertyIdFirst = it.data!!.get("data").toString()
                                    Kotpref.propertyId = propertyIdFirst as String
                                    count = 0
                                    for (item in filePropertyImageList) {
                                        uploadPropertyDoc(item, false)
                                    }
                                    for (item in filePropertyOwnershipList) {
                                        uploadPropertyDoc(item, true)
                                    }
                                    /*if (ownerFile != null) {
                                        uploadPropertyDoc(ownerFile!!, false)
                                    }*/
                                }
                            }

                            pb_edit!!.visibility = View.GONE
                            Toast.makeText(
                                applicationContext,
                                "Property Added Successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                            // Now if broker/agent is requested, add the broker information
                            if (isBrokerValidated) {
                                addBrokerOrAgent()
                            } else {
                                val intent =
                                    Intent(this, UnitDescriptionDynamicTabActivity::class.java)
                                startActivity(intent)
                            }
                        },
                            { e ->
                                pb_edit!!.visibility = View.GONE
                                Log.e("error", e.message.toString())
                                Toast.makeText(
                                    applicationContext,
                                    "Unable to add the property. Please contact Calonex",
                                    Toast.LENGTH_SHORT
                                ).show()
                            })
            } catch (e: Exception) {
                e.printStackTrace()
                Util.alertOkIntentMessage(
                    this@AddNewPropertyFirstScreen,
                    getString(R.string.alert),
                    "Currently property can not be added with image, continue without image.",
                    UnitDescriptionDynamicTabActivity::class.java
                )
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun addBrokerOrAgent() {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            propertyId = propertyIdFirst
            editProperty.propertyId = propertyId
            editProperty.linkedByBrokerAgent = true
            editProperty.linkedByBrokerAgentOnAdd = true
            editProperty.brokerOrAgentLiscenceID = edit_broker_id?.text.toString()
            editProperty.primaryContact = edit_primary_contact?.text.toString()
            editProperty.broker = edit_primary_contact?.text.toString()
            editProperty.brokerPhone = edit_broker_phone?.text.toString()
            editProperty.brokerEmailID = edit_broker_email?.text.toString()
            editProperty.primaryContactId = priId

            val editPropertyCall: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)
            val editApiCall: Observable<AddPropertyPostResponse> =
                editPropertyCall.editPropertyAddBroker(editProperty)

            editApiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (isEditing) {
                        Toast.makeText(
                            applicationContext,
                            "Broker/Agent linked to this property",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Broker/Agent added to this property",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    val intent = Intent(this, UnitDescriptionDynamicTabActivity::class.java)
                    startActivity(intent)
                },
                    { e ->
                        Log.e("error", e.message.toString())
                        Toast.makeText(
                            applicationContext,
                            "Property added but unable to add the broker/agent with your property",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Turn on isEditing so that only edits are possible
                        isEditing = true
                        return@subscribe
                    })

        } else {

            Toast.makeText(
                applicationContext,
                getString(R.string.error_network),
                Toast.LENGTH_SHORT
            ).show()

        }
    }

    fun getPathFromInputStreamUri(context: Context, uri: Uri): File? {
        var filePath: File? = null
        //uri.authority?.let {
        try {
            context.contentResolver.openInputStream(uri).use {
                val photoFile: File? = createTemporalFileFrom(it)
                filePath = photoFile
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        //}
        return filePath
    }

    @Throws(IOException::class)
    private fun createTemporalFileFrom(inputStream: InputStream?): File? {
        var targetFile: File? = null
        return if (inputStream == null) targetFile
        else {
            var read: Int
            val buffer = ByteArray(8 * 1024)
            targetFile = createTemporalFile()
            FileOutputStream(targetFile).use { out ->
                while (inputStream.read(buffer).also { read = it } != -1) {
                    out.write(buffer, 0, read)
                }
                out.flush()
            }
            targetFile
        }
    }

    override fun onResume() {
        super.onResume()

        var adapter: ImgListForLandlordAdapter? = null
        var adapterFile: ImgListForLandlordAdapter? = null
        if (mArrayUri.size > 0) {
            adapter = ImgListForLandlordAdapter(this, mArrayUri)
            rv_photos?.layoutManager = GridLayoutManager(this, 3)
            rv_photos?.adapter = adapter
        } else {
            mArrayUri.clear()
            adapter = ImgListForLandlordAdapter(this, mArrayUri)
            rv_photos?.layoutManager = GridLayoutManager(this, 3)
            rv_photos?.adapter = adapter
        }

        if (mArrayFileUri.size > 0) {
            adapterFile = ImgListForLandlordAdapter(this, mArrayFileUri)
            rv_files?.layoutManager = GridLayoutManager(this, 3)
            rv_files?.adapter = adapterFile
        } else {
            mArrayFileUri.clear()
            adapterFile = ImgListForLandlordAdapter(this, mArrayFileUri)
            rv_files?.layoutManager = GridLayoutManager(this, 3)
            rv_files?.adapter = adapterFile
        }
    }

    private fun createTemporalFile(): File = File(Environment.getDataDirectory(), "tempPicture.jpg")

    private fun uploadPropertyDoc(item: File, isOwnProof: Boolean) {
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        builder.addFormDataPart("propertyId", Kotpref.propertyId)
        if (isOwnProof) {
            builder.addFormDataPart("uploadFileType", getString(R.string.key_img_po))
        } else {
            builder.addFormDataPart("uploadFileType", getString(R.string.key_img_property))
        }
        builder.addFormDataPart(
            "file", item!!.name,
            item.asRequestBody(MultipartBody.FORM)
        )
        val requestBody: RequestBody = builder.build()
        val uploadSign: ApiInterface =
            ApiClient(this).provideService(ApiInterface::class.java)

        val apiCallUploadSign: Observable<ResponseDtoResponse> =
            uploadSign.uploadDocument(requestBody)

        apiCallUploadSign.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({

                if (it.responseDto?.responseCode!!.equals(200)) {
                    if (isOwnProof) {
                        pb_edit!!.visibility = View.GONE
                    } else {
                        count = count + 1
                        if (count == mArrayUri.size) {
                            pb_edit!!.visibility = View.GONE
                        }
                    }

                    Log.e("onSuccess", it.responseDto?.responseDescription.toString())

                } else {
                    pb_edit!!.visibility = View.GONE
                    Toast.makeText(
                        this,
                        it.responseDto?.exceptionDescription!!,
                        Toast.LENGTH_SHORT
                    ).show()
                }

            },
                { e ->
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    //   pb_reg_confirm.visibility = View.GONE
                    Toast.makeText(
                        this,
                        e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("error", e.message.toString())

                })
    }

    fun getBrokerDetails(id: String) {
        var credential = BrokerAgentCredentials()
        credential.licenceId = id
        pb_edit!!.visibility = View.VISIBLE
        val brokerAgentDetails: ApiInterface =
            ApiClient(this@AddNewPropertyFirstScreen).provideService(ApiInterface::class.java)
        val apiCall: Observable<BrokerAgentDetailResponse> =
            brokerAgentDetails.getBrokerAgentList(credential) //Test API Key

        apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.e("onSuccess", it.statusCode.toString())
                pb_edit!!.visibility = View.GONE
                if (it.data != null && it.responseDto!!.responseDescription.contains("success")) {

                    isBrokerValidated = true
                    broker_view.visibility = View.VISIBLE
                    edit_broker_id.setText(id)
                    edit_primary_contact.setText(it.data!!.primaryContact)
                    edit_broker_name.setText(it.data!!.brokerName)
                    edit_broker_phone.setText(it.data!!.brokerPhone)
                    edit_broker_email.setText(it.data!!.brokerEmailID)
                    priId = it.data!!.primaryContactId
                } else if (it.responseDto != null && it.responseDto!!.responseCode == 404) {
                    isBrokerValidated = false
                    Toast.makeText(
                        applicationContext,
                        "Invalid Broker/Agent ID",
                        Toast.LENGTH_SHORT
                    ).show()
                    broker_view.visibility = View.GONE
                }
            },
                { e ->
                    broker_view.visibility = View.GONE
                    isBrokerValidated = false
                    Log.e("onFailure", e.toString())
                    pb_edit!!.visibility = View.GONE
                    e.message?.let {
                        Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                    }
                })
    }

    companion object {

        var mArrayUri = ArrayList<Uri>()
        var mArrayFileUri = ArrayList<Uri>()

        //File url from api
        var mArrayLiveUri = ArrayList<Uri>()
        var mArrayFileLiveUri = ArrayList<Uri>()

        var filePropertyImageList = ArrayList<File>()
        var filePropertyOwnershipList = ArrayList<File>()
        var isEditing: Boolean = false
        var isLinkRequested: Boolean = false

        var linkedProperty: NotificationLinkRequestModel? = null

        var propertyIdFirst: String? = null
        var numOfUnits: String? = null

        var buildingFeatureIdList = ArrayList<Long>()
        var parkingIdList = ArrayList<Long>()

        var jsonElementsFeature: JsonElement? = null
        var jsonElementsParking: JsonElement? = null

        //        var allModel: ApiAllModel? = null
        var result = AddPropertyPostPayload()
        var editProperty = EditPropertyAddBroker()

        var Building = object : FeatureCheckboxCallback {
            override fun sendState(
                state: Boolean,
                buildingFeatureId: Long,
                buildingFeature: String
            ) {
                if (state) {
                    if (!buildingFeatureIdList.contains(buildingFeatureId)) {
                        buildingFeatureIdList.add(buildingFeatureId)
                    }
                } else {
                    if (buildingFeatureIdList.contains(buildingFeatureId)) {
                        buildingFeatureIdList.remove(buildingFeatureId)
                    }
                }
                jsonElementsFeature = Gson().toJsonTree(buildingFeatureIdList)

            }

        }
        var Parking = object : ParkingCheckboxCallback {
            override fun sendState(
                state: Boolean,
                parkingTypeId: Long,
                parkingTypeName: String
            ) {

                Log.e("State", "Parking selected state $state")
                Log.e("State", "Parking parkingTypeId $parkingTypeId")
                Log.e("State", "Parking parkingTypeName $parkingTypeName")
                if (state) {
                    if (!parkingIdList.contains(parkingTypeId)) {
                        parkingIdList.add(parkingTypeId)
                    }
                } else {
                    if (parkingIdList.contains(parkingTypeId)) {
                        parkingIdList.remove(parkingTypeId)
                    }
                }

                jsonElementsParking = Gson().toJsonTree(parkingIdList)

                Log.e("Parking", "Current Parking==>" + Gson().toJson(jsonElementsParking))


            }

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Util.navigationBack(this@AddNewPropertyFirstScreen)
    }
}
