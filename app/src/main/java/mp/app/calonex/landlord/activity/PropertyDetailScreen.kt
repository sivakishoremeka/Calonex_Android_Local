package mp.app.calonex.landlord.activity

import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.RelativeLayout
import android.widget.TextView
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
import kotlinx.android.synthetic.main.activity_property_detail_screen.*
import kotlinx.android.synthetic.main.activity_property_detail_screen.iv_back
import kotlinx.android.synthetic.main.activity_property_detail_screen.txt_lp_notify
import kotlinx.android.synthetic.main.fragment_property_description.*
import kotlinx.android.synthetic.main.fragment_unit_dedescription.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.NotificationCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.adapter.*
import mp.app.calonex.landlord.adapter.DashBoardPropertyListAdapter.Companion.listPropertyImagesHome
import mp.app.calonex.landlord.adapter.DashBoardPropertyListAdapter.Companion.propertyDetailResponseHome
import mp.app.calonex.landlord.adapter.PropertyListAdapter.Companion.listPropertyImages
import mp.app.calonex.landlord.adapter.PropertyListAdapter.Companion.propertyDetailResponse
import mp.app.calonex.landlord.fragment.FullImageFragment
import mp.app.calonex.landlord.model.*
import mp.app.calonex.landlord.response.AppNotificationResponse
import mp.app.calonex.service.FeaturesService
import java.math.RoundingMode
import java.util.*

class PropertyDetailScreen : CxBaseActivity2() {

    private val myImageList =
        intArrayOf(R.drawable.property_img1, R.drawable.property_img2, R.drawable.property_img3)
    private var vpImage: ViewPager? = null
    private var tlIndicator: TabLayout? = null
    private var ytd_revenue: TextView? = null
    private var txt_lp_total_unit: TextView? = null
    var profile_pic: CircleImageView? = null
    private var layoutLphNotify: RelativeLayout? = null
    private var txtLphNotify: TextView? = null
    var linkRequestListt = ArrayList<AppNotifications>()
    var linkRequestListForLdReq = ArrayList<AppNotifications>()
    var alertsListt = ArrayList<AppNotifications>()
    var messageList = ArrayList<AppNotifications>()
    var appNotifications = ArrayList<AppNotifications>()
    var mNotificationLinkRequest = NotificationLinkRequestModel()
    var mNotificationLinkRequestNew = NotificationLinkRequestModelNew()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_property_detail_screen)

        initComponent()
        startHandler()
    }

    private fun initComponent() {
        layoutLphNotify = findViewById(R.id.layout_lp_notify)
        txtLphNotify = findViewById(R.id.txt_lph_notify)

        profile_pic = findViewById(R.id.profile_pic)

        Glide.with(this)
            .load(Kotpref.profile_image)
            .placeholder(R.drawable.profile_default_new)
            .into(profile_pic!!)
        txt_lp_total_unit = findViewById(R.id.txt_lp_total_unit);
        ytd_revenue = findViewById(R.id.ytd_revenue);
        vpImage = findViewById(R.id.vp_image)
        tlIndicator = findViewById(R.id.tl_indicator)

        val fragmentAdapter = PropertyDetailAdapter(supportFragmentManager)

        vp_property_detail!!.adapter = fragmentAdapter
        tab_pd?.setupWithViewPager(vp_property_detail!!)

        try {
            if (propertyDetailResponse.propertyId != 0L) {
                propertyDetailResponseLocal = propertyDetailResponse
                listPropertyFilesLocal = listPropertyImages
                listPropertyImagesLocal.clear()
                for (item in listPropertyImages) {
                    if (item.uploadFileType.equals(getString(R.string.key_img_property))) {
                        listPropertyImagesLocal.add(item)
                    }
                }

            } else {
                propertyDetailResponseLocal = propertyDetailResponseHome
                listPropertyFilesLocal = listPropertyImagesHome

                listPropertyImagesLocal.clear()
                for (item in listPropertyImagesHome) {
                    if (item.uploadFileType.equals(getString(R.string.key_img_property))) {
                        listPropertyImagesLocal.add(item)
                        Log.e("From Dashboard", "Image item==>> ${Gson().toJson(item)}")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


        // Log.e("propertyDetailResponse","propertyDetailResponseLocal==>"+Gson().toJson(propertyDetailResponseLocal))
        Log.e(
            "listPropertyFiles",
            "listPropertyFilesLocal==>" + Gson().toJson(listPropertyFilesLocal)
        )
        Log.e(
            "listPropertyImages",
            "listPropertyImagesLocal==>" + Gson().toJson(listPropertyImagesLocal)
        )

        Log.e(
            "From",
            "propertyDetailResponseLocal==>> " + Gson().toJson(propertyDetailResponseLocal)
        )
        Log.e("From", "listPropertyFilesLocal==>> " + Gson().toJson(listPropertyFilesLocal))

        val imgList = ArrayList<String>()
        for (item in listPropertyImagesLocal) {
            if (item.uploadFileType.equals(getString(R.string.key_img_property))) {
                imgList.add(item.fileName)
            }
        }
        Kotpref.propertyIdNew = propertyDetailResponseLocal.propertyId

        ytd_revenue!!.setText("$" + propertyDetailResponseLocal.totalRevenue)

        if (intent.hasExtra("tenantName"))
            txt_lp_total_unit!!.setText(
                intent.getStringExtra("tenantName")
                    .toString() + "\n" + intent.getStringExtra("tenantEmail").toString()
            )

        Log.e("Property", "Image List ==>" + Gson().toJson(imgList))

        vpImage!!.adapter = ImagePagerAdapter(this, this@PropertyDetailScreen, imgList)

        tlIndicator!!.setupWithViewPager(vpImage)
        txt_pd_name.text = propertyDetailResponseLocal.address
        txt_pd_add!!.text =
            propertyDetailResponseLocal.city + ", " + propertyDetailResponseLocal.state +
                    ", " + propertyDetailResponseLocal.zipCode
//    txt_pd_id!!.text =getString(R.string.property_id) + propertyDetailResponseLocal!!.propertyId.toString()
        if (propertyDetailResponseLocal!!.propertyUnitDetailsDTO.size > 0) {
            if (!propertyDetailResponseLocal!!.propertyUnitDetailsDTO.get(0).tenantFirstName.isNullOrEmpty())
                txt_lp_total_unit!!.text =
                    propertyDetailResponseLocal!!.propertyUnitDetailsDTO.get(0).tenantFirstName + " " + propertyDetailResponseLocal!!.propertyUnitDetailsDTO.get(
                        0
                    ).tenantLastName
        }
        txt_pd_added!!.text = Util.getDateTime(propertyDetailResponseLocal.createdOn.toString())

        txt_property_id.text = "" + propertyDetailResponseLocal!!.propertyId

        img_edit.setOnClickListener {
            //edit property

            val intent = Intent(this, AddNewPropertyFirstScreen::class.java)

            intent.putExtra("isEdit", true)
            startActivity(intent)
            Util.navigationNext(this)

        }
        layoutLphNotify!!.setOnClickListener {
            Util.navigationNext(this@PropertyDetailScreen, NotifyScreen::class.java)
        }

        iv_back.setOnClickListener {
            onBackPressed()
        }

        loadPropertyDetails()

        loadUnitDetails()
    }


    private fun loadUnitDetails() {
        // Log.e("Check", "Unit Loaded")
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

        txt_available_units.text = "Available Units (${unitDetailsList.size})"

        var unitNameList: ArrayList<String> = ArrayList()
        var availability: ArrayList<String> = ArrayList()

        availability.add("Select")
        availability.add("Vacant")
        availability.add("CX-Tenant")
        availability.add("Non CX-Tenant")

        unitNameList.add("Select")
        for (i in unitDetailsList.indices) {
            unitNameList.add("Unit #" + unitDetailsList[i].unitNumber)
        }

        val spinnerUnitAdapter = CustomSpinnerAdapter(applicationContext, unitNameList)
        spn_unit.adapter = spinnerUnitAdapter

        val spinnerAvailabilityAdapter = CustomSpinnerAdapter(applicationContext, availability)
        spn_availability.adapter = spinnerAvailabilityAdapter

        val unitDescAdapter =
            UnitDescAdapter(this, pb_unit!!, this@PropertyDetailScreen, unitDetailsList)
        rv_units?.layoutManager = LinearLayoutManager(this)

        rv_units?.adapter = unitDescAdapter
        unitDescAdapter.notifyDataSetChanged()

        spn_unit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position > 0) {
                    var selectedUnit = unitNameList.get(position)
                    // Log.e("Filter", "Unit==>$selectedUnit")
                    unitDescAdapter!!.filter.filter(selectedUnit)
                } else {
                    unitDescAdapter!!.filter.filter("")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // another interface callback
            }
        }

        spn_availability.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position > 0) {
                    var selectedAvailabilityText = availability.get(position)
                    var selectedAvailability = "1"
                    when (selectedAvailabilityText) {
                        "Vacant" -> {
                            selectedAvailability = "1"
                        }

                        "Non CX-Tenant" -> {
                            selectedAvailability = "2"
                        }

                        "CX-Tenant" -> {
                            selectedAvailability = "3"
                        }
                    }
                    // Log.e("Filter", "Availability==>$selectedAvailability")
                    unitDescAdapter!!.filter.filter(selectedAvailability)

                } else {
                    unitDescAdapter!!.filter.filter("")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // another interface callback
            }
        }

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
        featureListPD.addAll(propertyDetailResponseLocal!!.buildingFeatureDTO)
        parkingListPD.addAll(propertyDetailResponseLocal!!.parkingTypeDTO)
        expenseListPD.addAll(propertyDetailResponseLocal!!.propertyExpensesDetailsDTO)


        /* Log.e(
             "Property Details",
             "propertyDetailResponseLocal>> " + Gson().toJson(propertyDetailResponseLocal)
         )*/

        var imageDocList = java.util.ArrayList<String>()

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
            this@PropertyDetailScreen,
            java.util.ArrayList(hashMapFeature.keys),
            java.util.ArrayList(hashMapFeature.values)
        )
        rv_desc_feature?.layoutManager = GridLayoutManager(this@PropertyDetailScreen, 3)
        rv_desc_feature?.adapter = featureAdapter

        val parkingAdapter = DescriptionParkingAdapter(
            this@PropertyDetailScreen,
            java.util.ArrayList(hashMapParking.keys),
            java.util.ArrayList(hashMapParking.values)
        )
        rv_desc_parking?.layoutManager = GridLayoutManager(this@PropertyDetailScreen, 2)
        rv_desc_parking?.adapter = parkingAdapter

        val expenseAdapter = DescriptionExpenseAdapter(this@PropertyDetailScreen, expenseListPD)
        rv_desc_expense?.layoutManager = GridLayoutManager(this@PropertyDetailScreen, 3)
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
            Glide.with(this@PropertyDetailScreen)
                .load(getString(R.string.base_url_img) + docValue)
                .placeholder(Util.customProgress(this@PropertyDetailScreen))
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

        var totalExpenses = 0.0
        for (item in propertyDetailResponseLocal!!.propertyExpensesDetailsDTO) {
            if (item.expensesAmount != null) {
                totalExpenses += item.expensesAmount.toBigDecimal().setScale(2, RoundingMode.UP)
                    .toDouble()
            }
        }

        txt_revinew!!.text = "$" + propertyDetailResponseLocal.totalRevenue

        if (totalExpenses == 0.0) {
            txt_expense!!.text = "$0.00"
        } else {
            txt_expense!!.text = "$" + totalExpenses.toString()
        }

        img_proof_own!!.setOnClickListener {
            if (imageDocList.size > 0) {
                Kotpref.propertyImageIndex = 0
                FullImageFragment(this@PropertyDetailScreen).customDialog(imageDocList)
            }
        }


    }


    private fun populateList(): ArrayList<ImageModel> {
        val list = ArrayList<ImageModel>()
        for (i in 0..2) {
            val imageModel = ImageModel()
            imageModel.setImage_drawables(myImageList[i])
            list.add(imageModel)
        }
        return list
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Util.navigationBack(this)
    }

    override fun onResume() {
        super.onResume()
        getNotificationList()
    }

    private fun getNotificationList() {
        this@PropertyDetailScreen.window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        val credentials = NotificationCredential()

        credentials.userCatalog = Kotpref.userId
        val notifyListService: ApiInterface =
            ApiClient(this@PropertyDetailScreen).provideService(ApiInterface::class.java)
        val apiCall: Observable<AppNotificationResponse> =
            notifyListService.getAppNotifications(credentials) //Test API Key 3600248I

        RxAPICallHelper().call(apiCall, object : RxAPICallback<AppNotificationResponse> {
            override fun onSuccess(response: AppNotificationResponse) {
                try {
                    if (!response.data?.isEmpty()!!) {
                        appNotifications = response.data!!
                        linkRequestListt.clear()
                        alertsListt.clear()
                        messageList.clear()

                        for (i in 0..appNotifications.size - 1) {
                            val gson = Gson()

                            if (appNotifications[i].activityType == "1" && appNotifications[i].agentRequest == "true") {
                                val objectList = gson.fromJson(
                                    appNotifications[i].notificationData,
                                    NotificationLinkRequestModel::class.java
                                )
                                mNotificationLinkRequest = objectList
                                linkRequestListt.add(appNotifications[i])
                            } else if (appNotifications[i].activityType == "1" && appNotifications[i].agentRequest == "false") {
                                val objectList = gson.fromJson(
                                    appNotifications[i].notificationData,
                                    NotificationLinkRequestModelNew::class.java
                                )
                                mNotificationLinkRequestNew = objectList
                                linkRequestListForLdReq.add(appNotifications[i])
                            } else if (appNotifications[i].activityType == "2") {
                                alertsListt.add(appNotifications[i])
                            } else if (appNotifications[i].activityType == "3") {
                                messageList.add(appNotifications[i])
                            }
                        }
                    }

                    Kotpref.notifyCount =
                        (linkRequestListt.size + alertsListt.size + linkRequestListForLdReq.size).toString()
                    txt_lp_notify!!.text = Kotpref.notifyCount
                    //(activity as HomeActivityBroker?)!!.addBadgeNew(messageList.size.toString())
                    this@PropertyDetailScreen!!.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    if (Integer.parseInt(Kotpref.notifyCount) < 1) {
                        txtLphNotify!!.visibility = View.GONE

                    } else {
                        txtLphNotify!!.visibility = View.VISIBLE

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailed(throwable: Throwable) {
                // show error
                // Log.e("onFailure", throwable.toString())
                try {
                    this@PropertyDetailScreen!!.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    txt_lp_notify!!.text =
                        (linkRequestListt.size + alertsListt.size + linkRequestListForLdReq.size).toString()
                    if (Integer.parseInt(Kotpref.notifyCount) < 1) {
                        txtLphNotify!!.visibility = View.GONE

                    } else {
                        txtLphNotify!!.visibility = View.VISIBLE

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    companion object {
        var propertyDetailResponseLocal = PropertyDetail()
        var listPropertyImagesLocal = ArrayList<FetchDocumentModel>()
        var listPropertyFilesLocal = ArrayList<FetchDocumentModel>()

    }

}