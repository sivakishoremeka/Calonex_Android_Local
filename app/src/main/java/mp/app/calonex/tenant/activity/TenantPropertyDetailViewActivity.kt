package mp.app.calonex.tenant.activity

import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_property_detail_view.*
import kotlinx.android.synthetic.main.fragment_property_description_agent.*
import mp.app.calonex.R
import mp.app.calonex.agent.adapter.PropertyDetailAdapterAgent
import mp.app.calonex.common.apiCredentials.FindLeaseCredentials
import mp.app.calonex.common.apiCredentials.SignatureLeaseCredentials
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.CxBaseActivity2
import mp.app.calonex.landlord.activity.LeaseRequestDetailScreen
import mp.app.calonex.landlord.adapter.*
import mp.app.calonex.landlord.fragment.FullImageFragment
import mp.app.calonex.landlord.model.*
import mp.app.calonex.landlord.response.FindLeaseResponse
import mp.app.calonex.landlord.response.SecurityFetchResponse
import mp.app.calonex.landlord.response.SignatureLeaseResponse
import mp.app.calonex.service.FeaturesService
import mp.app.calonex.tenant.adapter.TenantApplicationHistoryAdapter.Companion.listPropertyViewImages
import mp.app.calonex.tenant.adapter.TenantApplicationHistoryAdapter.Companion.propertyDetailViewResponse
import mp.app.calonex.tenant.adapter.UnitDescAdapterTenant
import java.math.RoundingMode

class TenantPropertyDetailViewActivity : CxBaseActivity2() {

    private var vpImage: ViewPager? = null
    private var tlIndicator: TabLayout? = null

    private var profilePic: CircleImageView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_property_detail_view)


        initComponent()
        startHandler()


    }

    private fun initComponent() {
        profilePic = findViewById(R.id.profile_pic)

        vpImage = findViewById(R.id.vp_image)
        tlIndicator = findViewById(R.id.tl_indicator)


        try {
            if (propertyDetailViewResponse.propertyId != 0L) {
                propertyDetailResponseLocal = propertyDetailViewResponse
                propertyFileResponseLocal = listPropertyViewImages
                listPropertyImagesLocal.clear()

                for (item in listPropertyViewImages) {
                    if (item.uploadFileType.equals(getString(R.string.key_img_property))) {
                        listPropertyImagesLocal.add(item)
                    }
                }

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        val fragmentAdapter = PropertyDetailAdapterAgent(supportFragmentManager)

        vp_property_detail!!.adapter = fragmentAdapter
        tab_pd?.setupWithViewPager(vp_property_detail!!)

        val imgList = ArrayList<String>()
        for (item in listPropertyImagesLocal) {
            if (item.uploadFileType.equals(getString(R.string.key_img_property))) {
                imgList.add(item.fileName)
            }
        }

        Log.e(
            "property Details",
            "property Details Response>> " + Gson().toJson(propertyDetailResponseLocal)
        )
        try {
            Glide.with(this)
                .load(Kotpref.profile_image)
                .placeholder(R.drawable.profile_default_new)
                .into(profilePic!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }


        Kotpref.propertyIdNew = propertyDetailResponseLocal.propertyId

        vpImage!!.adapter = ImagePagerAdapter(this, this@TenantPropertyDetailViewActivity, imgList)

        tlIndicator!!.setupWithViewPager(vpImage)
        txt_pd_add!!.text =
            propertyDetailResponseLocal.address + ", " + propertyDetailResponseLocal.city + ", " + propertyDetailResponseLocal.state +
                    ", " + propertyDetailResponseLocal.zipCode

        txt_pd_added!!.text = Util.getDateTime(propertyDetailResponseLocal.createdOn.toString())
        txt_property_id!!.text = propertyDetailResponseLocal.propertyId.toString()

        iv_back.setOnClickListener {
            onBackPressed()
        }


        loadPropertyDetails()

        loadUnitDetails()
    }


    private fun loadUnitDetails() {
        Log.e("Check", "Unit Loaded")
        val dividerItemDecoration =
            DividerItemDecoration(rv_units!!.context, DividerItemDecoration.VERTICAL)
        dividerItemDecoration.setDrawable(
            ContextCompat.getDrawable(
                applicationContext,
                R.drawable.item_list_divider
            )!!
        )
        var unitDetailsList = java.util.ArrayList<UnitDetailsPD>()
        unitDetailsList.addAll(propertyDetailResponseLocal!!.propertyUnitDetailsDTO)

        Log.e("List", "unitDetails==>" + Gson().toJson(unitDetailsList))
        val unitDescAdapter =
            UnitDescAdapterTenant(
                this,
                pb_unit!!,
                this@TenantPropertyDetailViewActivity,
                unitDetailsList
            )
        rv_units?.layoutManager = LinearLayoutManager(this)

        rv_units?.adapter = unitDescAdapter
        unitDescAdapter.notifyDataSetChanged()

    }

    private fun loadPropertyDetails() {
        var featureListPD = java.util.ArrayList<BuildingFeaturePD>()
        var parkingListPD = java.util.ArrayList<ParkingTypePD>()
        var expenseListPD = java.util.ArrayList<PropertyExpensePD>()
        var defaultFeatureList = java.util.ArrayList<BuildingFeature>()
        var defaultParkingList = java.util.ArrayList<ParkingType>()
        var hashMapFeature: HashMap<String, Boolean> = HashMap<String, Boolean>()
        var hashMapParking: HashMap<String, Boolean> = HashMap<String, Boolean>()
        var listDefaultFeature = java.util.ArrayList<BuildingFeature>()
        var listDefaultParking = java.util.ArrayList<ParkingType>()
        var imageDocList = java.util.ArrayList<String>()
        featureListPD.addAll(propertyDetailResponseLocal!!.buildingFeatureDTO)
        parkingListPD.addAll(propertyDetailResponseLocal!!.parkingTypeDTO)
        expenseListPD.addAll(propertyDetailResponseLocal!!.propertyExpensesDetailsDTO)
        if (FeaturesService.allModel != null) {
            listDefaultFeature =
                FeaturesService.allModel?.buildingFeatureListResponse?.data as java.util.ArrayList<BuildingFeature>
            listDefaultParking =
                FeaturesService.allModel?.parkingTypeListResponse?.data as java.util.ArrayList<ParkingType>
        }
        defaultFeatureList.addAll(listDefaultFeature)

        for (item in defaultFeatureList) {

            hashMapFeature.put(item.buildingFeature, false)
        }
        for (item in featureListPD) {

            if (hashMapFeature.containsKey(item.buildingFeatureName)) {
                hashMapFeature.put(item.buildingFeatureName, true)
            }
        }

        defaultParkingList.addAll(listDefaultParking)
        for (item in defaultParkingList) {

            hashMapParking.put(item.parkingType, false)
        }
        for (item in parkingListPD) {

            if (hashMapParking.containsKey(item.parkingTypeName)) {
                hashMapParking.put(item.parkingTypeName, true)
            }
        }


        val featureAdapter = DescriptionFeatureAdapter(
            applicationContext,
            java.util.ArrayList(hashMapFeature.keys),
            java.util.ArrayList(hashMapFeature.values)
        )
        rv_desc_feature?.layoutManager = GridLayoutManager(applicationContext, 2)
        rv_desc_feature?.adapter = featureAdapter

        val parkingAdapter = DescriptionParkingAdapter(
            applicationContext,
            java.util.ArrayList(hashMapParking.keys),
            java.util.ArrayList(hashMapParking.values)
        )
        rv_desc_parking?.layoutManager = GridLayoutManager(applicationContext, 2)
        rv_desc_parking?.adapter = parkingAdapter

        val expenseAdapter = DescriptionExpenseAdapter(applicationContext, expenseListPD)
        rv_desc_expense?.layoutManager = GridLayoutManager(applicationContext, 3)
        rv_desc_expense?.adapter = expenseAdapter
        var docValue: String? = ""
        for (item in listPropertyImagesLocal) {
            if (item.uploadFileType.equals(getString(R.string.key_img_po))) {
                docValue = item.fileName
                break
            }
        }
        if (docValue.isNullOrEmpty()) {
            img_proof_own!!.visibility = View.GONE
        } else {
            imageDocList.add(docValue)
            Glide.with(applicationContext)
                .load(getString(R.string.base_url_img) + docValue)
                .placeholder(Util.customProgress(applicationContext))
                .into(img_proof_own!!)
        }
        txt_pd_about!!.text = propertyDetailResponseLocal!!.description
        if (propertyDetailResponseLocal!!.linkedByBrokerAgent) {
            layout_brokr!!.visibility = View.VISIBLE
            txt_broker_name!!.text = propertyDetailResponseLocal!!.broker
            txt_broker_email!!.text = propertyDetailResponseLocal!!.brokerEmailID
            txt_broker_contact!!.text =
                PhoneNumberUtils.formatNumber(propertyDetailResponseLocal!!.brokerPhone, "US")
            txt_licence_id!!.text = propertyDetailResponseLocal!!.brokerOrAgentLiscenceID
        } else {
            layout_brokr!!.visibility = View.GONE
        }

        var totalExpenses = 0.00
        for (item in propertyDetailResponseLocal!!.propertyExpensesDetailsDTO) {
            if (item.expensesAmount != null) {
                totalExpenses += item.expensesAmount.toBigDecimal().setScale(2, RoundingMode.UP)
                    .toDouble()
            }
        }

        try {
            if (Kotpref.loginRole == "agent") {
                ld_title!!.visibility = View.VISIBLE
                layout_landlord!!.visibility = View.VISIBLE
                txt_landlord_name!!.text = propertyDetailResponseLocal.landlordFullName
                txt_landlord_email!!.text = propertyDetailResponseLocal.landlordEmailId
            } else {
                txt_landlord_name!!.text = propertyDetailResponseLocal.landlordFullName
                txt_landlord_email!!.text = propertyDetailResponseLocal.landlordEmailId
                ld_title!!.visibility = View.VISIBLE
                layout_landlord!!.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        txt_revinew!!.text = "$" + propertyDetailResponseLocal.totalRevenue

        if (totalExpenses == 0.00) {
            txt_expense!!.text = "$0.00"
        } else {
            txt_expense!!.text = "$" + totalExpenses.toString()
        }

        img_proof_own!!.setOnClickListener {
            if (imageDocList.size > 0) {
                Kotpref.propertyImageIndex = 0
                FullImageFragment(applicationContext).customDialog(imageDocList)
            }

        }

        txt_interested_for_lease.setOnClickListener {
            var extras = intent.extras
            if (extras != null) {
                getLeaseList(Kotpref.leaseId)
               /* val intent = Intent(this, TenantLeaseDetailsActivity::class.java)
                intent.putExtra("leasePosition", extras.getString("position", "-1"))
                startActivity(intent)*/
            }
        }


    }

    private fun getLeaseList(idLease: String) {
        if (NetworkConnection.isNetworkConnected(this)) {
            //Create retrofit Service
          //  pbAlert!!.visibility = View.VISIBLE
            val credential = FindLeaseCredentials()

            credential.leaseId = idLease
            val findApi: ApiInterface =
                ApiClient(this!!).provideService(ApiInterface::class.java)
            val apiCall: Observable<FindLeaseResponse> =
                findApi.findLease(credential)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", it.responseDto?.responseDescription.toString())



                    if (it.data != null) {
                        NotificationAlertsAdapter.alertLeaseInfo = it.data!![0]
                        getSignatureList(idLease)
                    }

                },
                    { e ->
                        Log.e("onFailure", e.toString())
            //pbAlert!!.visibility = View.GONE
                        e.message?.let {
                            Toast.makeText(this@TenantPropertyDetailViewActivity, it, Toast.LENGTH_SHORT).show()
                        }
                    })
        } else {
            Toast.makeText(this, this.getString(R.string.error_network), Toast.LENGTH_SHORT)
                .show()

        }
    }

    private fun getSignatureList(idLease: String) {
        if (NetworkConnection.isNetworkConnected(this)) {
            //Create retrofit Service
        //    pbAlert!!.visibility = View.VISIBLE
            val credential = SignatureLeaseCredentials()

            credential.leaseId = idLease
            val signatureApi: ApiInterface =
                ApiClient(this!!).provideService(ApiInterface::class.java)
            val apiCall: Observable<SignatureLeaseResponse> =
                signatureApi.getLeaseSignature(credential)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", it.responseDto?.responseDescription.toString())

                        // pbAlert!!.visibility = View.GONE

                    if (it.data != null) {
                        NotificationAlertsAdapter.alertListSignature = it.data!!

                    } else {
                        NotificationAlertsAdapter.alertListSignature = ArrayList<LeaseSignature>()
                    }
                    fetchSecurityList(idLease)

                },
                    { e ->
                        Log.e("onFailure", e.toString())
                      //  pbAlert!!.visibility = View.GONE
                        e.message?.let {
                            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                        }
                    })
        } else {
            Toast.makeText(this, this.getString(R.string.error_network), Toast.LENGTH_SHORT)
                .show()

        }
    }

    private fun fetchSecurityList(idLease: String) {
        if (NetworkConnection.isNetworkConnected(this)) {
            //Create retrofit Service
           // pbAlert!!.visibility = View.VISIBLE
            val credential = SignatureLeaseCredentials()

            credential.leaseId = idLease
            val signatureApi: ApiInterface =
                ApiClient(this!!).provideService(ApiInterface::class.java)
            val apiCall: Observable<SecurityFetchResponse> =
                signatureApi.securityFetchInfo(credential)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", it.responseDto?.responseDescription.toString())

                  //  pbAlert!!.visibility = View.GONE

                    if (it.data != null) {
                        NotificationAlertsAdapter.alertSecurityList = it.data!!

                    }
                    val intent = Intent(this@TenantPropertyDetailViewActivity!!, LeaseRequestDetailScreen::class.java)
                    intent.putExtra(this.getString(R.string.intent_alert_lease), true)
                    this!!.startActivity(intent)
                    Util.navigationNext(this!!)

                },
                    { e ->
                        Log.e("onFailure", e.toString())
                       // pbAlert!!.visibility = View.GONE
                        e.message?.let {
                            Toast.makeText(this@TenantPropertyDetailViewActivity, it, Toast.LENGTH_SHORT).show()
                        }
                    })
        } else {
            Toast.makeText(this@TenantPropertyDetailViewActivity, this.getString(R.string.error_network), Toast.LENGTH_SHORT)
                .show()

        }
    }

    /*private fun populateList(): ArrayList<ImageModel> {
        val list = ArrayList<ImageModel>()
        for (i in 0..2) {
            val imageModel = ImageModel()
            imageModel.setImage_drawables(myImageList[i])
            list.add(imageModel)
        }
        return list
    }*/

    override fun onBackPressed() {
        super.onBackPressed()
        Util.navigationBack(this)
    }

    companion object {
        var propertyDetailResponseLocal = PropertyDetail()
        var propertyFileResponseLocal = ArrayList<FetchDocumentModel>()
        var listPropertyImagesLocal = ArrayList<FetchDocumentModel>()

    }

}
