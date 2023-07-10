package mp.app.calonex.landlord.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.google.android.material.textfield.TextInputEditText
import mp.app.calonex.R
import mp.app.calonex.common.utility.DigitDecimalInputFilter
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.Util
import mp.app.calonex.common.utility.Util.convertDateToLong
import mp.app.calonex.common.utility.Util.isDateValid
import mp.app.calonex.common.utility.Util.navigationBack
import mp.app.calonex.common.utility.Util.navigationNext
import mp.app.calonex.common.utility.Util.valueMandetory
import mp.app.calonex.landlord.adapter.CustomSpinnerOBLeaseFstScreenAdapter
import mp.app.calonex.landlord.adapter.PropertyListAdapter.Companion.propertyDetailResponse
import mp.app.calonex.landlord.adapter.UnitDescAdapter.Companion.unitLeaseInfo
import mp.app.calonex.landlord.model.ObLeaseTenantPayload
import java.text.SimpleDateFormat
import java.util.*

class ObLeaseSpecificationScreen : CxBaseActivity2() {

    private var txtObProperty:TextView?=null
    private var txtObUnit:TextView?=null
    private var editLsRent:TextInputEditText?=null
    private var editLsSecurity:TextInputEditText?=null
    private var editLsAmenities:TextInputEditText?=null
    private var editLsLateFees: TextInputEditText?=null
    private var editLsStartDate: TextInputEditText?=null
    private var editLsEndDate: TextInputEditText?=null
    private var editLsDuration: TextInputEditText?=null
    private var editLsRentDueDate: TextInputEditText?=null
    private var editLsMonthFree: TextInputEditText?=null
    private var editLsDiscount: TextInputEditText?=null
    private var edit_ls_tenant_service: TextInputEditText?=null
    private var spinner_1: Spinner?=null
    private var switchLateFee:Switch?=null
    private var switchStartEnd:Switch?=null
    private var btnLsNxt: Button?=null
    private var lsRent:String?=null
    private var lsSecurityDeposit:String?=null
    private var lsAmenities:String?=null
    private var lsLateFees:String?=null
    private var lsStartDate:String?=null
    private var lsEndDate:String?=null
    private var lsDuration:String?=null
    private var lsRentDueDate:String?=null
    private var lsMonthFree:String?=null
    private var lsDiscount:String?=null
    private var ratesdata:String?="0"
    private var cal = Calendar.getInstance()
    private var year = cal.get(Calendar.YEAR)
    private var month = cal.get(Calendar.MONTH)
    private var day = cal.get(Calendar.DAY_OF_MONTH)
    val myFormat = "yyyy-MM-dd"
    val sdf = SimpleDateFormat(myFormat, Locale.US)
    var isLateFee:Boolean=false
    private var isRenew:Boolean=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ob_lease_specification_screen)
        initComponent()
        actionComponent()
        startHandler()
    }

    fun initComponent(){
        txtObProperty=findViewById(R.id.txt_ob_property_add)
        txtObUnit=findViewById(R.id.txt_ob_unit)
        editLsRent=findViewById(R.id.edit_ls_rent)
        editLsSecurity=findViewById(R.id.edit_ls_security)
        editLsAmenities=findViewById(R.id.edit_ls_amenities)
        editLsLateFees=findViewById(R.id.edit_ls_late_fees)
        editLsStartDate=findViewById(R.id.edit_ls_start_date)
        editLsEndDate=findViewById(R.id.edit_ls_end_date)
        editLsDuration=findViewById(R.id.edit_ls_duration)
        editLsRentDueDate=findViewById(R.id.edit_ls_rent_due_date)
        editLsMonthFree=findViewById(R.id.edit_ls_month_free)




        editLsDiscount=findViewById(R.id.edit_ls_discount)
        edit_ls_tenant_service=findViewById(R.id.edit_ls_tenant_service)
        spinner_1=findViewById(R.id.spinner_1)

        switchLateFee=findViewById(R.id.switch_late_fee)
        switchStartEnd=findViewById(R.id.switch_start_end)
        btnLsNxt=findViewById(R.id.btn_ls_next)

        val rates = resources.getStringArray(R.array.rates)


        //val adapter1 = ArrayAdapter(this, android.R.layout.simple_spinner_item, rates)
        val adapter1 = CustomSpinnerOBLeaseFstScreenAdapter(this, rates)
        spinner_1!!.adapter = adapter1
        spinner_1!!.setSelection(1)

        spinner_1!!.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                ratesdata = rates[position].toString()

                Log.e("POS SPIN", position.toString() + " - " + ratesdata)
                when (position) {
                    0 -> {
                        edit_ls_tenant_service!!.setText("4%")
                        obLeaseTenantPayload.tenantServiceFee = 4
                        obLeaseTenantPayload.landlordServiceFee = 0
                    }
                    1 -> {
                        edit_ls_tenant_service!!.setText("2%")
                        obLeaseTenantPayload.tenantServiceFee = 2
                        obLeaseTenantPayload.landlordServiceFee = 2
                    }
                    2 -> {
                        edit_ls_tenant_service!!.setText("0%")
                        obLeaseTenantPayload.tenantServiceFee = 0
                        obLeaseTenantPayload.landlordServiceFee = 4
                    }
                    else -> {
                        edit_ls_tenant_service!!.setText("0%")
                        obLeaseTenantPayload.tenantServiceFee = 0
                        obLeaseTenantPayload.landlordServiceFee = 4
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        Util.setEditReadOnly(edit_ls_tenant_service!!, false, InputType.TYPE_NULL)

        txtObProperty!!.text=", "+propertyDetailResponse.address+", "+propertyDetailResponse.city+", "+propertyDetailResponse.state+
                ", "+propertyDetailResponse.zipCode
        isRenew=intent.getBooleanExtra(getString(R.string.is_renew_lease),false)
        editLsRent!!.filters = arrayOf(DigitDecimalInputFilter(8, 2))
        editLsRentDueDate!!.filters = arrayOf(DigitDecimalInputFilter(8, 2))
        editLsSecurity!!.filters = arrayOf(DigitDecimalInputFilter(8, 2))
        editLsLateFees!!.filters = arrayOf(DigitDecimalInputFilter(8, 2))
        Util.setEditReadOnly(editLsEndDate!!, false, InputType.TYPE_NULL)
        Util.setEditReadOnly(editLsStartDate!!, false, InputType.TYPE_NULL)
        editLsLateFees!!.isEnabled=false
        Log.e("FLAG zz", isRenew.toString())
        if(isRenew){
            txtObUnit!!.text="Unit : "+unitLeaseInfo.unitNumber
            editLsRent!!.setText(unitLeaseInfo.rentAmount)
            editLsRentDueDate!!.setText(unitLeaseInfo.rentBeforeDueDate)
            editLsSecurity!!.setText(unitLeaseInfo.securityAmount)
            editLsStartDate!!.setText(Util.convertLongToTime(unitLeaseInfo.leaseEndDate.toLong(),myFormat))
            cal.timeInMillis=unitLeaseInfo.leaseEndDate.toLong()
            editLsDuration!!.setText(unitLeaseInfo.leaseDuration)
            cal.add(Calendar.MONTH, unitLeaseInfo.leaseDuration.toInt())
            editLsEndDate!!.setText(sdf.format(cal.getTime()))
            if(!unitLeaseInfo.lateFee.isNullOrEmpty()){
                editLsLateFees!!.setText(unitLeaseInfo.lateFee)
                switchLateFee!!.isChecked=true
                editLsLateFees!!.isEnabled=true
            }else{
                editLsLateFees!!.setText("")
                switchLateFee!!.isChecked=false
            }
            if (unitLeaseInfo.monthsFree.trim().isNullOrEmpty()) {
                editLsMonthFree!!.setText("0")
            } else {
                editLsMonthFree!!.setText(unitLeaseInfo.monthsFree)
            }
            editLsAmenities!!.setText(unitLeaseInfo.amenities)
            obLeaseTenantPayload.tenantBaseInfoDto= unitLeaseInfo.tenantBaseInfoDto
            obLeaseTenantPayload.oldLeaseId= unitLeaseInfo.leaseId
            obLeaseTenantPayload.renewLease=true
            obLeaseTenantPayload.unitNumber=unitLeaseInfo.unitNumber
            obLeaseTenantPayload.unitId= unitLeaseInfo.unitId
            obLeaseTenantPayload.uploadedBy=""

        }else{
            txtObUnit!!.text="Unit : "+intent.getStringExtra(getString(R.string.intent_unit_no))

            editLsRent!!.setText(intent.getStringExtra(getString(R.string.intent_rent_mnth)))
            editLsRentDueDate!!.setText(intent.getStringExtra(getString(R.string.intent_rent_mnth)))
            editLsSecurity!!.setText(intent.getStringExtra(getString(R.string.intent_security_amt)))

            Log.e("FLAG xx", intent.getStringExtra("DISCOUNT") + " -- " + intent.getStringExtra("LATE FEE"))

            editLsDiscount!!.setText(intent.getStringExtra("DISCOUNT"))
            editLsLateFees!!.setText(intent.getStringExtra("LATE FEE"))
        }


    }

    fun actionComponent(){


        editLsStartDate?.setOnClickListener {


            val dateDialog = DatePickerDialog(
                this,
                { view, year, monthOfYear, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    editLsStartDate!!.setText(sdf.format(cal.getTime()))
                    Kotpref.payDay=Calendar.DAY_OF_MONTH.toString()
                    Kotpref.calMonToAdd=monthOfYear

                },
                year,
                month,
                day
            )
            dateDialog.show()
            dateDialog.getDatePicker().setMinDate(System.currentTimeMillis())
        }

        switchLateFee!!.setOnCheckedChangeListener { buttonView, isChecked ->
            editLsLateFees!!.isEnabled = isChecked
            isLateFee=isChecked
        }

        switchStartEnd!!.setOnCheckedChangeListener { buttonView, isChecked ->

obLeaseTenantPayload.monthFreeStandards=isChecked

        }

        editLsDuration!!.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {


            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                lsDuration=editLsDuration!!.text.toString().trim()
                if(editLsDuration!!.text.toString().trim().length>0){

                    Log.e("DATEZZ", "" + Kotpref.calMonToAdd)

                    var mon:Int=lsDuration!!.toInt() + (Kotpref.calMonToAdd)

                    cal.set(Calendar.MONTH, mon)
                        editLsEndDate!!.setText(sdf.format(cal.getTime()))


                }else{
                    cal.timeInMillis=convertDateToLong(editLsStartDate!!.text.toString().trim()!!, myFormat)
                    editLsEndDate!!.setText("")
                }
            }
        })

        btnLsNxt!!.setOnClickListener { view ->
            // Hide the keyboard
            val imm =getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            try {
                validComponent()
            }catch (e:Exception){
                Log.e("error validate",e.message.toString())
            }


        }


    }



    fun validComponent() {

        lsRent=editLsRent!!.text.toString().trim()
        lsSecurityDeposit=editLsSecurity!!.text.toString().trim()
        lsAmenities=editLsAmenities!!.text.toString().trim()
        if(isLateFee) {
            lsLateFees = editLsLateFees!!.text.toString().trim()
        }

        lsStartDate=editLsStartDate!!.text.toString().trim()
        lsDuration=editLsDuration!!.text.toString().trim()
        lsEndDate=editLsEndDate!!.text.toString().trim()

        lsRentDueDate=editLsRentDueDate!!.text.toString().trim()
        if (editLsMonthFree!!.text.toString().trim().isNullOrEmpty()) {
            lsMonthFree = "0"
        } else {
            lsMonthFree = editLsMonthFree!!.text.toString().trim()
        }





        if(valueMandetory(applicationContext, lsStartDate, editLsStartDate!!)){
            return
        }

        if(!isDateValid(lsStartDate!!, myFormat)){
            editLsStartDate?.error = getString(R.string.error_date_format)
            editLsStartDate?.requestFocus()
            return
        }
        if(valueMandetory(applicationContext, lsDuration, editLsDuration!!)){
            return
        }

        if(valueMandetory(applicationContext, lsRentDueDate, editLsRentDueDate!!)){
            return
        }

        if(valueMandetory(applicationContext, lsRent, editLsRent!!)){
            return
        }

        if(valueMandetory(applicationContext, lsSecurityDeposit, editLsSecurity!!)){
            return
        }
        obLeaseTenantPayload.rentAmount=lsRent
        obLeaseTenantPayload.securityAmount=lsSecurityDeposit
        obLeaseTenantPayload.leaseStartDate=convertDateToLong(lsStartDate!!, myFormat).toString()
        obLeaseTenantPayload.leaseEndDate=convertDateToLong(lsEndDate!!, myFormat).toString()
        obLeaseTenantPayload.leaseDuration=lsDuration
        obLeaseTenantPayload.rentBeforeDueDate=lsRentDueDate
        if (lsMonthFree.isNullOrEmpty()) {
            obLeaseTenantPayload.monthsFree="0"
        } else {

            obLeaseTenantPayload.monthsFree=lsMonthFree
        }

        if (obLeaseTenantPayload.monthsFree!!.toInt()>= obLeaseTenantPayload.leaseDuration!!.toInt()){
            editLsMonthFree!!.setError("lease free month can't be greater then lease duration")
            return
        }
        if (Kotpref.userRole.contains("Landlord", true)) {
            obLeaseTenantPayload.landlordId=Kotpref.userId
        } else {
            obLeaseTenantPayload.landlordId=intent.getStringExtra("LANDLORD ID")
        }
        obLeaseTenantPayload.uploadedBy=""    //Kotpref.userId
        //obLeaseTenantPayload.propertyId=propertyDetailResponse.propertyId.toString()
        val propId:Long = intent.getLongExtra("PROPERTY ID", 0L)
        obLeaseTenantPayload.propertyId=propId.toString()
        Log.e("PPPP", propId.toString())
        if(!isRenew) {
            obLeaseTenantPayload.unitId = intent.getStringExtra(getString(R.string.intent_unit_id))
            obLeaseTenantPayload.unitNumber = intent.getStringExtra(getString(R.string.intent_unit_no))

            //obLeaseTenantPayload.lateFee=intent.getStringExtra("LATE FEE")
            //obLeaseTenantPayload.discount= intent.getStringExtra("DISCOUNT")!!.toInt()

            lsLateFees = editLsLateFees!!.text.toString().trim()
            obLeaseTenantPayload.lateFee=lsLateFees

            val disc: Int? = editLsDiscount!!.text.toString().trim().toFloat().toInt()
            obLeaseTenantPayload.discount= disc ?: 0

            Log.e("FLAG yy", obLeaseTenantPayload.discount.toString() + " -- " + obLeaseTenantPayload.lateFee)
        } else {
            obLeaseTenantPayload.lateFee=lsLateFees
            val disc: Int? = editLsDiscount!!.text.toString().trim().toFloat().toInt()
            obLeaseTenantPayload.discount= disc ?: 0
        }

        obLeaseTenantPayload.securityTransferTo="CX"

        navigationNext(this, ObTenantsScreen::class.java)
    }



    companion object {
        var obLeaseTenantPayload = ObLeaseTenantPayload()
    }

     override fun onBackPressed() {
        super.onBackPressed()
        navigationBack(this)
    }
}