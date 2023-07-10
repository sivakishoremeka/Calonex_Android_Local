package mp.app.calonex.agent.activity

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
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_property_detail_screen_agent.img_edit
import kotlinx.android.synthetic.main.activity_property_detail_screen_agent.iv_back
import kotlinx.android.synthetic.main.activity_property_detail_screen_agent.pb_detail
import kotlinx.android.synthetic.main.activity_property_detail_screen_agent.tab_pd
import kotlinx.android.synthetic.main.activity_property_detail_screen_agent.txt_lp_notify
import kotlinx.android.synthetic.main.activity_property_detail_screen_agent.txt_pd_add
import kotlinx.android.synthetic.main.activity_property_detail_screen_agent.txt_pd_added
import kotlinx.android.synthetic.main.activity_property_detail_screen_agent.txt_property_id
import kotlinx.android.synthetic.main.activity_property_detail_screen_agent.vp_property_detail
import kotlinx.android.synthetic.main.fragment_property_description_agent.*
import kotlinx.android.synthetic.main.fragment_unit_dedescription.*
import kotlinx.android.synthetic.main.sub_fragment_property_agent_list.*
import mp.app.calonex.R
import mp.app.calonex.agent.adapter.PropertyListAdapterAgent.Companion.listPropertyImages
import mp.app.calonex.agent.adapter.PropertyListAdapterAgent.Companion.propertyDetailResponse
import mp.app.calonex.common.utility.Util
import mp.app.calonex.agent.adapter.PropertyDetailAdapterAgent
import mp.app.calonex.agent.adapter.UnitDescAdapterAgent
import mp.app.calonex.broker.activity.AgentAssenedActivity
import mp.app.calonex.broker.activity.AssignAgentForPropertyActivity
import mp.app.calonex.broker.adapter.AssignedAgentForPropertyListAdapter
import mp.app.calonex.broker.adapter.DashBoardPropertyListAdapter.Companion.listPropertyImagesHome
import mp.app.calonex.broker.adapter.DashBoardPropertyListAdapter.Companion.propertyDetailResponseHome
import mp.app.calonex.common.apiCredentials.NotificationCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.landlord.activity.CxBaseActivity2
import mp.app.calonex.landlord.adapter.*
import mp.app.calonex.landlord.fragment.FullImageFragment
import mp.app.calonex.landlord.model.*
import mp.app.calonex.landlord.response.AppNotificationResponse
import mp.app.calonex.models.broker.AssignedAgentListData
import mp.app.calonex.models.broker.AssignedAgentModel
import mp.app.calonex.service.FeaturesService
import java.math.RoundingMode

class PropertyDetailScreenAgent : CxBaseActivity2() {
    var linkRequestListt = java.util.ArrayList<AppNotifications>()
    var linkRequestListForLdReq = java.util.ArrayList<AppNotifications>()
    var alertsListt = java.util.ArrayList<AppNotifications>()
    var messageList = java.util.ArrayList<AppNotifications>()
    var appNotifications = java.util.ArrayList<AppNotifications>()
    var mNotificationLinkRequest = NotificationLinkRequestModel()
    var mNotificationLinkRequestNew = NotificationLinkRequestModelNew()
    private var layoutLphNotify: RelativeLayout? = null
    private var agentList: NestedScrollView? = null
    private var txtLphNotify: TextView? = null


    private val myImageList =
        intArrayOf(R.drawable.property_img1, R.drawable.property_img2, R.drawable.property_img3)
    private var vpImage: ViewPager? = null
    private var tlIndicator: TabLayout? = null
    private var brokerAgentListAdapter: AssignedAgentForPropertyListAdapter? = null
    private var profilePic: CircleImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_property_detail_screen_agent)

        initComponent()
        startHandler()
    }



    private fun initComponent() {
        layoutLphNotify = findViewById(R.id.layout_lp_notify)
        agentList = findViewById(R.id.ll_agent_list)
        txtLphNotify = findViewById(R.id.txt_lp_notify)

        profilePic = findViewById(R.id.profile_pic)

        vpImage = findViewById(R.id.vp_image)
        tlIndicator = findViewById(R.id.tl_indicator)


        try {

            var extras=intent.extras


            if (extras!=null)
            {
             var from=extras.getString("from","");
             if (from.equals("dashboard"))  {
                 propertyDetailResponseLocal = propertyDetailResponseHome
                 listPropertyImagesLocal.clear()

                 for (item in listPropertyImagesHome) {
                     if (item.uploadFileType.equals(getString(R.string.key_img_property))) {
                         listPropertyImagesLocal.add(item)
                     }
                 }
             }else{
                 propertyDetailResponseLocal = propertyDetailResponse
                 listPropertyImagesLocal.clear()

                 for (item in listPropertyImages) {
                     if (item.uploadFileType.equals(getString(R.string.key_img_property))) {
                         listPropertyImagesLocal.add(item)
                     }
                 }
             }
            }
           /* if (propertyDetailResponse.propertyId != 0L) {


            } else {


            }*/

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
        Glide.with(this)
            .load(Kotpref.profile_image)
            .placeholder(R.drawable.profile_default_new)
            .into(profilePic!!)


        Kotpref.propertyIdNew = propertyDetailResponseLocal.propertyId

        vpImage!!.adapter = ImagePagerAdapter(this, this@PropertyDetailScreenAgent, imgList)

        tlIndicator!!.setupWithViewPager(vpImage)
        txt_pd_add!!.text = propertyDetailResponseLocal.address + ", " + propertyDetailResponseLocal.city + ", " + propertyDetailResponseLocal.state +
                    ", " + propertyDetailResponseLocal.zipCode
//    txt_pd_id!!.text =getString(R.string.property_id) + propertyDetailResponse!!.propertyId.toString()
        /*txt_lp_total_unit!!.text=propertyDetailResponse!!.propertyUnitDetailsDTO.size.toString()
        val totalUnit=propertyDetailResponse!!.propertyUnitDetailsDTO.size.toString()*/

        txt_pd_added!!.text = Util.getDateTime(propertyDetailResponseLocal.createdOn.toString())
        txt_property_id!!.text = propertyDetailResponseLocal.propertyId.toString()

        // TODO add this section
        img_edit.visibility = View.INVISIBLE
        /*img_edit.setOnClickListener {
            //edit property

            val intent = Intent(this, AddNewPropertyFirstScreen::class.java)

            intent.putExtra("isEdit", true)
            startActivity(intent)
            Util.navigationNext(this)

        }*/

        iv_back.setOnClickListener {
            onBackPressed()
        }
        layoutLphNotify!!.setOnClickListener {
            Util.navigationNext(this@PropertyDetailScreenAgent, NotifyScreenAgent::class.java)
        }

        if (Kotpref.userRole.contains("agent",true)){
            agentList!!.visibility=View.GONE
        }

        tv_assign_agent.setOnClickListener {
            val intent = Intent(this, AssignAgentForPropertyActivity::class.java)
            intent.putExtra("propertyId", "${propertyDetailResponseLocal.propertyId}")
            intent.putExtra("brokerId", Kotpref.userId)
            intent.putExtra("assignAgents", Gson().toJson(assignedAgentsForProperty))

            startActivity(intent)
        }


        tv_view_all_agent.setOnClickListener {
            val intent = Intent(this, AgentAssenedActivity::class.java)
            intent.putExtra("propertyId", "${propertyDetailResponseLocal.propertyId}")
            intent.putExtra("brokerId", Kotpref.userId)

            startActivity(intent)
        }


        loadPropertyDetails()

        loadUnitDetails()
    }

    override fun onResume() {
        super.onResume()
        getPropertyWiseAgentList(propertyDetailResponseLocal.propertyId)
        getNotificationList()

    }

    var assignedAgentsForProperty: List<AssignedAgentModel> = ArrayList()

    private fun getPropertyWiseAgentList(propertyId: Long?) {
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        pb_detail.visibility = View.VISIBLE

        val propertyDetails: ApiInterface =
            ApiClient(this).provideService(ApiInterface::class.java)
        val apiCall: Observable<AssignedAgentListData> =
            propertyDetails.getPropertyWiseAgentList("$propertyId")

        RxAPICallHelper().call(apiCall, object : RxAPICallback<AssignedAgentListData> {
            override fun onSuccess(assignedAgentList: AssignedAgentListData) {
                Log.e("Get", "Assigned Agent List >> " + Gson().toJson(assignedAgentList))
                this@PropertyDetailScreenAgent.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                pb_detail.visibility = View.GONE
                if (assignedAgentList.responseDto.status == 200) {
                    if (assignedAgentList.assignedAgents.isNotEmpty()) {
                        tv_view_all_agent.visibility = View.VISIBLE
                        tv_no_agent.visibility = View.GONE
                        assignedAgentsForProperty = assignedAgentList.assignedAgents
                        loadAssignedAgentList(assignedAgentList.assignedAgents)
                    } else {
                        tv_view_all_agent.visibility = View.GONE
                        tv_no_agent.visibility = View.VISIBLE
                    }
                } else {
                    tv_view_all_agent.visibility = View.GONE
                    tv_no_agent.visibility = View.VISIBLE

                }


            }

            override fun onFailed(t: Throwable) {
                // show error
                this@PropertyDetailScreenAgent!!.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                pb_detail.visibility = View.GONE
                tv_view_all_agent.visibility = View.GONE
                tv_no_agent.visibility = View.VISIBLE
                Log.e("onFailure", t.toString())
                //  progressBarList?.visibility = View.GONE
            }
        })
    }

    private fun loadAssignedAgentList(assignedAgents: List<AssignedAgentModel>) {
        if (assignedAgents.size > 2) {
            tv_view_all_agent.visibility = View.VISIBLE
        } else {
            tv_view_all_agent.visibility = View.GONE
        }
        tv_no_agent.visibility = View.GONE

        rv_assigned_agents?.layoutManager = LinearLayoutManager(this@PropertyDetailScreenAgent)

        val listPayment = ArrayList<AssignedAgentModel>()
        for (i in assignedAgents.indices) {
            if (i <= 1) {
                listPayment.add(assignedAgents[i])
            }
        }
        brokerAgentListAdapter = AssignedAgentForPropertyListAdapter(
            this@PropertyDetailScreenAgent,
            this@PropertyDetailScreenAgent,
            listPayment,
            "${propertyDetailResponseLocal.propertyId}"
        )
        rv_assigned_agents?.adapter = brokerAgentListAdapter

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
            UnitDescAdapterAgent(this, pb_unit!!, this@PropertyDetailScreenAgent, unitDetailsList)
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
                    Log.e("Filter", "Unit==>$selectedUnit")
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
                    Log.e("Filter", "Availability==>$selectedAvailability")
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


    }

    private fun getNotificationList() {
        this@PropertyDetailScreenAgent.window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        val credentials = NotificationCredential()

        credentials.userCatalog = Kotpref.userId
        val notifyListService: ApiInterface =
            ApiClient(this@PropertyDetailScreenAgent).provideService(ApiInterface::class.java)
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
                    this@PropertyDetailScreenAgent!!.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
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
                Log.e("onFailure", throwable.toString())
                try {
                    this@PropertyDetailScreenAgent!!.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
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
        var listPropertyImagesLocal = java.util.ArrayList<FetchDocumentModel>()
        var listPropertyFilesLocal = java.util.ArrayList<FetchDocumentModel>()

    }

}