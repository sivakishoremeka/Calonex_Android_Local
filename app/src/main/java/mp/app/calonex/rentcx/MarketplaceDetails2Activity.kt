package mp.app.calonex.rentcx

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_marketplace_details2.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.UserDetailCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.CxBaseActivity2
import mp.app.calonex.rentcx.MarketplaceDetails1Activity.Companion.PropertyDetailsData

class MarketplaceDetails2Activity : CxBaseActivity2() {
    var applicationFee: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marketplace_details2)

        startHandler()

        property_id.text = PropertyDetailsData.property!!.PropertyID.toString()
        unit_number.text = PropertyDetailsData.property!!.UnitNumber.toString()
        address_str.text = PropertyDetailsData.property!!.PropertyDetail!!.Address1

        edit_lease_start_date.setText(PropertyDetailsData.property!!.LeaseStartDate)
        edit_lease_end_date.setText(PropertyDetailsData.property!!.LeaseStartDate)  // Change this
        edit_lease_duration.setText("")
        edit_late_fee.setText(PropertyDetailsData.property!!.LateFee)
        edit_discount.setText(PropertyDetailsData.property!!.discount)
        edit_rent_bef_due_date.setText(PropertyDetailsData.property!!.RentBeforeDueDate)
        edit_proposed_rent.setText(PropertyDetailsData.property!!.RentPerMonth)
        edit_proposed_security_rent.setText(PropertyDetailsData.property!!.SecurityAmount)
        edit_months_fee.setText(PropertyDetailsData.property!!.MonthsFree.toString())

        Util.setEditReadOnly(edit_lease_start_date!!, false, InputType.TYPE_NULL)
        Util.setEditReadOnly(edit_lease_end_date!!, false, InputType.TYPE_NULL)
        Util.setEditReadOnly(edit_late_fee!!, false, InputType.TYPE_NULL)
        Util.setEditReadOnly(edit_discount!!, false, InputType.TYPE_NULL)
        Util.setEditReadOnly(edit_rent_bef_due_date!!, false, InputType.TYPE_NULL)
        Util.setEditReadOnly(edit_proposed_rent!!, false, InputType.TYPE_NULL)
        Util.setEditReadOnly(edit_proposed_security_rent!!, false, InputType.TYPE_NULL)
        Util.setEditReadOnly(edit_months_fee!!, false, InputType.TYPE_NULL)

        header_back.setOnClickListener {
            finish()
        }

        property_apply_text.setOnClickListener {
            if (edit_lease_duration.text.toString() != "") {
                addOrUpdate()
            } else {
                Toast.makeText(this@MarketplaceDetails2Activity, "Invalid Lease duration.", Toast.LENGTH_SHORT).show()
            }
        }

        getUserDetails()
        getApplicationFees()
    }

    @SuppressLint("CheckResult")
    private fun getUserDetails() {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

            val credentials = UserDetailCredential()
            credentials.userId = Kotpref.userId
            //credentials.userId = PropertyDetailsData.property!!.PropertyDetail!!.OnBordedBy
            //credentials.userId = PropertyDetailsData.property!!.PropertyDetail!!.PrimaryContactId.toString()

            //Log.e("MCX_LOG-2", Gson().toJson(PropertyDetailsData))

            Log.e("ASDF", Kotpref.userId)

            val validateService: ApiInterface = ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<UserDetailsResponce> = validateService.getMarketplaceUserDetails(credentials)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
                Log.e("onSuccess", it.responseDto!!.responseCode.toString())

                if (it.responseDto!!.responseCode == 200) {
                    UserDetailsResponceData = it
                }

                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            },
                { e ->
                    Log.e("onFailure", e.toString())

                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    e.message?.let {
                        Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            Toast.makeText(applicationContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("CheckResult")
    private fun getApplicationFees() {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

            val credentials = ApplicationFeesCredential()
            credentials.propertyId = PropertyDetailsData.property!!.PropertyUnitID.toString()

            val validateService: ApiInterface = ApiClient2(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<ApplicationFeesResponce> = validateService.getMarketplaceApplicationFees(credentials)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
                Log.e("onSuccess", it.toString())

                if (it.message == null) {
                    ApplicationFeesResponceData = it
                    applicationFee = true
                } else {
                    applicationFee = false
                    //Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT).show()
                    property_apply_text.visibility = View.INVISIBLE
                }

                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            },
                { e ->
                    Log.e("onFailure", e.toString())

                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    e.message?.let {
                        Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            Toast.makeText(applicationContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("CheckResult")
    private fun addOrUpdate() {

        val tenantBaseInfoDto = TenantBaseInfoDto()

        tenantBaseInfoDto.accountId = "01"
        tenantBaseInfoDto.tenantFirstName = Kotpref.userName
        tenantBaseInfoDto.tenantMiddleName = ""
        tenantBaseInfoDto.tenantLastName = ""//this.userDetails.lastName
        tenantBaseInfoDto.rentAmount = PropertyDetailsData.property!!.RentPerMonth
        tenantBaseInfoDto.rentPercentage = "100"
        tenantBaseInfoDto.securityAmount = PropertyDetailsData.property!!.SecurityAmount
        tenantBaseInfoDto.emailId = Kotpref.emailId
        tenantBaseInfoDto.phone = Kotpref.phone
        tenantBaseInfoDto.licenseNo = ""
        tenantBaseInfoDto.ssn = ""
        tenantBaseInfoDto.address = ""
        tenantBaseInfoDto.city = ""
        tenantBaseInfoDto.state = ""
        tenantBaseInfoDto.zipcode = ""
        tenantBaseInfoDto.role = "CX-PrimaryTenant"
        tenantBaseInfoDto.tenantId = ""
        tenantBaseInfoDto.leaseId = ""
        tenantBaseInfoDto.payDay = "11"
        tenantBaseInfoDto.cxId = ""
        tenantBaseInfoDto.existingTenant = false
        tenantBaseInfoDto.clientId = "61414646"

        val credentials = AddorUpdateRequestModel()

        credentials.fromRentCX = true
        if (ApplicationFeesResponceData.applicationfee != null) {
            credentials.applicationFee = ApplicationFeesResponceData.applicationfee
        }
        credentials.leaseEndDate = 0//PropertyDetailsData.property!!.LeaseStartDate.toString().toInt()
        credentials.leaseStartDate = 0//PropertyDetailsData.property!!.LeaseStartDate.toString().toInt()
        credentials.leaseDuration = edit_lease_duration.text.toString().toInt()
        credentials.rentAmount = PropertyDetailsData.property!!.RentPerMonth
        credentials.securityAmount = PropertyDetailsData.property!!.SecurityAmount
        credentials.action = ""
        credentials.comment = ""
        credentials.leaseId = ""
        credentials.agentId = ""
        credentials.brokerId = ""
        credentials.unitId = PropertyDetailsData.property!!.PropertyUnitID
        credentials.landlordId = PropertyDetailsData.property!!.PropertyDetail!!.UserCatalogID
        credentials.propertyId = PropertyDetailsData.property!!.PropertyID
        credentials.uploadedBy = null
        credentials.unitNumber = PropertyDetailsData.property!!.UnitNumber
        credentials.lateFee = PropertyDetailsData.property!!.LateFee
        credentials.lateFeeApplicable = true
        credentials.rentBeforeDueDate = PropertyDetailsData.property!!.RentBeforeDueDate
        credentials.monthsFree = PropertyDetailsData.property!!.MonthsFree
        credentials.monthFreeStandards = true
        credentials.amenities = ""
        credentials.securityTransferTo = "CX"
        credentials.dontShowToLandlord = false
        credentials.leaseDocument = null
        credentials.existingTenant = false
        credentials.landlordServiceFee = 2
        credentials.tenantServiceFee = 2
        credentials.discount = PropertyDetailsData.property!!.discount
        credentials.tenantBaseInfoDto.addAll(listOf(tenantBaseInfoDto))

        val tenantAddUpdateCall: ApiInterface =
            ApiClient(this).provideService(ApiInterface::class.java)
        val apiCall: Observable<MarketplaceAddorUpdateResponse> =
            tenantAddUpdateCall.marketplaceAddUpdate(credentials)

        apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.data != null) {
                    leaseid = it.data!!.leaseId
                    unitid = it.data!!.unitId
                    if (applicationFee) {
                        val intent = Intent(this@MarketplaceDetails2Activity, MarketPlacePaymentActivity::class.java)
                        //intent.putExtra("PropertyId", listPayment[position].PropertyUnitID)
                        startActivity(intent)
                    } else {
                        startActivity(Intent(this@MarketplaceDetails2Activity, MarketplaceSignatureUploadActivity::class.java))
                    }
                }
            },
                { e ->
                    Toast.makeText(applicationContext, "Fail", Toast.LENGTH_SHORT).show()
                    Log.e("error", e.message.toString())
                })
    }

    companion object {
        var UserDetailsResponceData = UserDetailsResponce()
        var ApplicationFeesResponceData = ApplicationFeesResponce()
        var leaseid: String? = null
        var unitid: String? = null
    }
}