package mp.app.calonex.landlord.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonElement
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_unit_description_screen.*
import mp.app.calonex.R
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.landlord.adapter.*
import mp.app.calonex.landlord.model.AddUnitCredentials
import mp.app.calonex.landlord.model.BathroomType
import mp.app.calonex.landlord.model.UnitType
import mp.app.calonex.landlord.response.AddUnitDetailResponse
import mp.app.calonex.service.FeaturesService.Companion.allModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NewUnitDescriptionScreen : CxBaseActivity2() {

    private var rvFeature: RecyclerView? = null
    private var rvUtilities: RecyclerView? = null

    var unitTypeId: Long = 0
    var bathroomTypeId: Long = 0
    var unitNumber: Int? = null
    var unitStatus: String? = ""

    var cal = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unit_description_screen)
        rvFeature = findViewById(R.id.rv_unit_feature)
        rvUtilities = findViewById(R.id.rv_utilities_included)

        rvFeature!!.layoutManager = GridLayoutManager(this, 2)
        rvUtilities!!.layoutManager = GridLayoutManager(this, 2)
        var featureAdapter =
            allModel?.unitFeatureListResponse?.data?.let { UnitFeatureAdapter(it, Unit) }

        var utilitiesAdapter =
            allModel?.unitUtilitiesResponse?.data?.let { UnitUtilitiesAdapter(it, UnitUtilities) }

        rvFeature?.adapter = featureAdapter
        rvUtilities?.adapter = utilitiesAdapter


        unitNumber = intent.getIntExtra("unitNumber", 0)

        edit_unit_no.setText("Unit " + unitNumber)

        val unitTypeList: MutableList<UnitType> = ArrayList()
        unitTypeList.add(0, UnitType(-1, "Select Unit Type"))

        val bathroomTypeList: MutableList<BathroomType> = ArrayList()
        bathroomTypeList.add(0, BathroomType(-1, "Select Bathroom Type"))


        for (i in 1..allModel!!.unitTypeListResponse.data!!.size) {
            unitTypeList.add(i, allModel!!.unitTypeListResponse.data!![i - 1])
        }

        for (i in 1..allModel!!.bathroomTypeListResponse.data!!.size) {
            bathroomTypeList.add(i, allModel!!.bathroomTypeListResponse.data!![i - 1])
        }

        if (spinner_unit_type != null) {
            var spinnerAdapter: CustomUnitTypeSpinnerAdapter =
                CustomUnitTypeSpinnerAdapter(this, unitTypeList)
            spinner_unit_type!!.adapter = spinnerAdapter
        }

        if (spinner_bathroom_type != null) {
            var spinnerAdapter: CustomBathroomTypeSpinnerAdapter =
                CustomBathroomTypeSpinnerAdapter(this, bathroomTypeList)
            spinner_bathroom_type!!.adapter = spinnerAdapter
        }


        val unitStatusList =
            arrayOf("Select Unit Type", "Vacant", "Occupied")

        if (spinner_unit_status != null) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, unitStatusList
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner_unit_status.adapter = adapter
        }

        spinner_unit_status?.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != -1 && position != 0) {
                    unitStatus = unitStatusList[position]

                    if (unitStatus.equals("Vacant", true)) {
                        view_unit_vacant.visibility = View.VISIBLE
                        view_unit_occupied.visibility = View.GONE
                    } else if (unitStatus!!.contains("Occupied", true)) {
                        view_unit_occupied.visibility = View.VISIBLE
                        view_unit_vacant.visibility = View.GONE
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }

        })


        spinner_unit_type?.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {

                if (unitTypeList[position].unitTypeId.toInt() != -1) {
                    unitTypeId = unitTypeList[position].unitTypeId
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }

        })


        spinner_bathroom_type?.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {

                if (bathroomTypeList[position].bathroomTypeId.toInt() != -1) {
                    bathroomTypeId = bathroomTypeList[position].bathroomTypeId
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }

        })

        actionComponent()
        startHandler()
    }

    private fun actionComponent() {

        edit_date!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)

                val dpd = DatePickerDialog(
                    this@NewUnitDescriptionScreen,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->


                        val myFormat = "MM/dd/yyyy" // mention the format you need
                        val sdf = SimpleDateFormat(myFormat, Locale.US)
                        edit_date!!.text = sdf.format(cal.getTime())

                    },
                    year,
                    month,
                    day
                )

                dpd.datePicker.minDate = System.currentTimeMillis() - 1000

                dpd.show()
            }

        })

        lease_start_date!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val c = Calendar.getInstance()
                val year = c.get(Calendar.YEAR)
                val month = c.get(Calendar.MONTH)
                val day = c.get(Calendar.DAY_OF_MONTH)

                val dpd = DatePickerDialog(
                    this@NewUnitDescriptionScreen,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->


                        val myFormat = "MM/dd/yyyy" // mention the format you need
                        val sdf = SimpleDateFormat(myFormat, Locale.US)
                        lease_start_date!!.text = sdf.format(cal.getTime())

                    },
                    year,
                    month,
                    day
                )

                dpd.datePicker.minDate = System.currentTimeMillis() - 1000

                dpd.show()
            }

        })


        btn_save_unit.setOnClickListener { view ->

            var credentials = AddUnitCredentials()
            credentials.propertyId = Kotpref.propertyId

            add__progress.visibility = View.VISIBLE

            credentials.propertyUnitDetails.addProperty("unitTypeId", unitTypeId)
            credentials.propertyUnitDetails.addProperty("unitNumber", unitNumber?.toLong())
            credentials.propertyUnitDetails.addProperty(
                "rentPerMonth",
                edit_rent_month.text.toString().toLong()
            )

            credentials.propertyUnitDetails.addProperty("increaseYearlyPercentage", edit_increase_yearly.text.toString().toLong());
            credentials.propertyUnitDetails.addProperty("applicationFees", edit_application_fee.text.toString().toLong());

            credentials.propertyUnitDetails.addProperty("dateAvailable", edit_date.text.toString())
            credentials.propertyUnitDetails.add("unitFeatureIds", jsonElementsFeature)
            credentials.propertyUnitDetails.add("unitUtilitiesIds", jsonElementsUtility)
            credentials.propertyUnitDetails.addProperty("bathroomTypeID", bathroomTypeId)
            credentials.propertyUnitDetails.addProperty(
                "squareFootArea",
                edit_square_foot.text.toString().toLong()
            )
            credentials.propertyUnitDetails.addProperty("isActive", true)

            if (unitStatus!!.contains("Vacant", true)) {
                // Status Code for Vacant is 1
                credentials.propertyUnitDetails.addProperty("unitStatus", "1")
                credentials.propertyUnitDetails.addProperty(
                    "leaseStartDate",
                    lease_start_date.text.toString()
                )
                credentials.propertyUnitDetails.addProperty(
                    "monthsFree",
                    edit_months_free.text.toString()
                )
                credentials.propertyUnitDetails.addProperty(
                    "rentBeforeDueDate",
                    edit_rent_before_due_date.text.toString()
                )
                credentials.propertyUnitDetails.addProperty(
                    "lateFee",
                    edit_rent_before_due_date.text.toString()
                )
            }

            if (unitStatus!!.contains("occupied", true)) {
                // Status Code for OCCUPIED_OUTSIDE_CX is 2
                // Status Code for OCCUPIED_INSIDE_CX is 3

                credentials.propertyUnitDetails.addProperty("unitStatus", "2")
                credentials.propertyUnitDetails.addProperty(
                    "tenantFirstName",
                    edit_tenant_first_name.text.toString()
                )
                credentials.propertyUnitDetails.addProperty(
                    "tenantMiddleName",
                    edit_tenant_middle_name.text.toString()
                )
                credentials.propertyUnitDetails.addProperty(
                    "tenantLastName",
                    edit_tenant_last_name.text.toString()
                )
                credentials.propertyUnitDetails.addProperty(
                    "tenantEmailId",
                    edit_tenant_email.text.toString()
                )
                credentials.propertyUnitDetails.addProperty(
                    "tenantPhoneNumber",
                    edit_tenant_phone.text.toString()
                )
            }


            val addPropertyCall: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<AddUnitDetailResponse> =
                addPropertyCall.addUnit(credentials)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", it.responseCode.toString())

                    add__progress.visibility = View.GONE
                    if (it.responseDto != null && it.responseDto!!.responseCode == 200) {
                        Toast.makeText(applicationContext,"Unit Added Successfully",Toast.LENGTH_SHORT).show()
                    }
                    val intent = Intent(this, UnitAddActivity::class.java)
                    intent.putExtra("unitNumber",unitNumber)
                    setResult(Activity.RESULT_OK,intent)
                    finish()

                },
                    { e ->
                        Log.e("onFailure", e.toString())
                        add__progress.visibility = View.GONE
                    })


        }

        btn_cancel.setOnClickListener {
            this.finish()
        }

    }

    val dateSetListener = object : DatePickerDialog.OnDateSetListener {
        override fun onDateSet(
            view: DatePicker, year: Int, monthOfYear: Int,
            dayOfMonth: Int
        ) {
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear + 1)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }
    }

    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        edit_date!!.text = sdf.format(cal.getTime())
    }


    companion object {

        var unitFeatureIds = ArrayList<Long>()
        var unitUtilitiesIds = ArrayList<Long>()

        var jsonElementsFeature: JsonElement? = null
        var jsonElementsUtility: JsonElement? = null


        var Unit = object : FeatureCheckboxCallback {
            override fun sendState(
                state: Boolean,
                buildingFeatureId: Long,
                buildingFeature: String
            ) {
                if (state) {
                    unitFeatureIds.add(buildingFeatureId)
                } else {
                    if (unitFeatureIds.contains(buildingFeatureId)) {
                        unitFeatureIds.remove(buildingFeatureId)
                    }
                }
                jsonElementsFeature = Gson().toJsonTree(unitFeatureIds)
            }
        }
        var UnitUtilities = object : FeatureCheckboxCallback {
            override fun sendState(
                state: Boolean,
                buildingFeatureId: Long,
                buildingFeature: String
            ) {
                if (state) {
                    unitUtilitiesIds.add(buildingFeatureId)
                } else {
                    if (unitUtilitiesIds.contains(buildingFeatureId)) {
                        unitUtilitiesIds.remove(buildingFeatureId)
                    }
                }
                jsonElementsUtility = Gson().toJsonTree(unitUtilitiesIds)
            }

        }

    }

}


/*

Vacant,Occupied inside Cx,Occupied outside Cx


leaseStartDate : yyyy-mm-dd
monthsFree:No of free months
rentBeforeDueDate: rent amount before Due date
unitStatus: the availability of unit is present: this has fixed the case sensitive values as :(Vacant,Occupied inside Cx,Occupied outside Cx)
if unitStatus is "Occupied outside Cx" then send out the below fields as well
tenantFirstName
tenantMiddleName
tenantLastName
tenantEmailId
tenantPhoneNumber

 */
