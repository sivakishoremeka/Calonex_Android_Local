package mp.app.calonex.agent.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dashboard_agent_new.*

import mp.app.calonex.R
import mp.app.calonex.agent.AgentMyBookOfBusinesResponse
import mp.app.calonex.agent.AgentPropertiesAndTenantResponse
import mp.app.calonex.agent.activity.HomeActivityAgent
import mp.app.calonex.agent.activity.NotifyScreenAgent
import mp.app.calonex.agent.adapter.AgentDashboardPropertyAdepter
import mp.app.calonex.agent.model.PropertyAgent
import mp.app.calonex.agent.responce.OnboardPropertyListResponseAgent
import mp.app.calonex.broker.responce.BrokerPaymentHistoryListResponse
import mp.app.calonex.common.apiCredentials.*
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.model.*
import mp.app.calonex.landlord.response.AppNotificationResponse
import mp.app.calonex.landlord.response.FetchDocumentResponse
import mp.app.calonex.landlord.response.UserDetailResponse
import java.math.RoundingMode

class AgentDashBoardFragment : Fragment() {
    var userDetailResponse = UserDetail()
    var listUserImages = ArrayList<FetchDocumentModel>()
    private var progressBar_notification: ProgressBar? = null
    lateinit var appContext: Context

    var propertyList: RecyclerView? = null
    var adapter: AgentDashboardPropertyAdepter? = null
    var list: ArrayList<PropertyAgent>? = null
    var name_title: TextView? = null
    var tv_visit_our_website: TextView? = null
    var viewMoreProperty: TextView? = null


    private var profile_pic: CircleImageView? = null
    private var layoutNotify: RelativeLayout? = null
    var appNotifications = ArrayList<AppNotifications>()
    var mNotificationLinkRequest = NotificationLinkRequestModel()
    var mNotificationLinkRequestNew = NotificationLinkRequestModelNew()

    private var total_ammount: TextView? = null
    private var total_payout_fransise: TextView? = null
    private var total_payout_agent: TextView? = null
    private var total_profit: TextView? = null
    var amount1: Double = 0.00
    var amount2: Double = 0.00
    var amount3: Double = 0.00


    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.dashboard_agent_new, container, false)
        initComponents(rootView)
        return rootView
    }

    private fun initComponents(viewRoot: View) {
        progressBar_notification = viewRoot.findViewById(R.id.progressBar_notification)
        viewMoreProperty = viewRoot.findViewById(R.id.view_more_property) as TextView
        propertyList = viewRoot.findViewById(R.id.propertylist)
        propertyList!!.setHasFixedSize(true);
        name_title = viewRoot.findViewById<View>(R.id.name_title) as TextView
        layoutNotify = viewRoot.findViewById(R.id.layout_lp_notify)
        tv_visit_our_website = viewRoot.findViewById(R.id.tv_visit_our_website)

        total_ammount = viewRoot.findViewById(R.id.amount_received_value)
        total_payout_fransise = viewRoot.findViewById(R.id.amount_payout_to_franchise_value)
        total_payout_agent = viewRoot.findViewById(R.id.amount_payout_to_agent_value)
        total_profit = viewRoot.findViewById(R.id.total_profit_value)
        profile_pic = viewRoot.findViewById(R.id.profile_pic)
        total_payout_agent!!.text = "$ 0"
        total_ammount!!.text = "$ 0"
        total_payout_fransise!!.text = "$ 0"
        total_profit!!.text = "$ 0"

        try {
            tv_visit_our_website!!.setOnClickListener {
                val viewIntent = Intent(
                    "android.intent.action.VIEW",
                    Uri.parse("https://rentcx.calonex.com/")
                )
                startActivity(viewIntent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


        viewMoreProperty!!.setOnClickListener {
            try {
                (requireActivity() as HomeActivityAgent).navigateTo("property")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        setData()
        fetchImages()
        getUserInfo()
        getPropertyList()
        getMyBookOfBusiness()
        getSumOfAgentPropertiesAndApprovedTenant()
      //  getPaymentList()


    }

    private fun setData() {

        list = ArrayList<PropertyAgent>()

        //sub_header_layout.visibility = View.VISIBLE
        adapter = AgentDashboardPropertyAdepter(appContext, activity, list!!,progressBar_notification!!)

        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        propertyList?.layoutManager = layoutManager
        propertyList?.adapter = adapter

        layoutNotify!!.setOnClickListener {
            Util.navigationNext(requireActivity(), NotifyScreenAgent::class.java)
        }
    }


    override fun onResume() {
        super.onResume()
        layoutNotify!!.visibility = View.VISIBLE
        linkRequestListt.clear()
        linkRequestListForLdReq.clear()
        alertsListt.clear()
        messageList.clear()
        getNotificationList()

    }

    private fun getNotificationList() {
        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        val credentials = NotificationCredential()

        credentials.userCatalog = Kotpref.userId
        val notifyListService: ApiInterface =
            ApiClient(appContext).provideService(ApiInterface::class.java)
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
                    activity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    if (Integer.parseInt(Kotpref.notifyCount) < 1) {
                        txt_lp_notify!!.visibility = View.GONE

                    } else {
                        txt_lp_notify!!.visibility = View.VISIBLE

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailed(throwable: Throwable) {
                // show error
                Log.e("onFailure", throwable.toString())
                try {
                    activity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    txt_lp_notify!!.text =
                        (linkRequestListt.size + alertsListt.size + linkRequestListForLdReq.size).toString()
                    if (Integer.parseInt(Kotpref.notifyCount) < 1) {
                        txt_lp_notify!!.visibility = View.GONE

                    } else {
                        txt_lp_notify!!.visibility = View.VISIBLE

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    private fun getPropertyList() {

        if (NetworkConnection.isNetworkConnected(appContext)) {
            val credential = AgentBrokerOnboardPropertyCredential()
            credential.userCatalogID = Kotpref.userId
            var propertyListService: ApiInterface =
                ApiClient(appContext).provideService(ApiInterface::class.java);
            val apiCall: Observable<OnboardPropertyListResponseAgent> =
                propertyListService.getOnboardList(credential)
            RxAPICallHelper().call(
                apiCall,
                object : RxAPICallback<OnboardPropertyListResponseAgent> {
                    override fun onSuccess(response: OnboardPropertyListResponseAgent) {
                        Log.d(
                            "onSuccess notification",
                            "onSuccess notification" + (response.responseDto?.responseCode)
                        )
                        setPropertyList(response.data!!)


                    }

                    override fun onFailed(throwable: Throwable) {
                        // show error
                        Log.e("onFailure", throwable.toString())

                    }
                })

        } else {
            Log.e("network", "Please check the network for get property list")
            Toast.makeText(appContext, "please check your Internet connection", Toast.LENGTH_SHORT)
                .show();
        }
    }


    private fun getStaticData(): ArrayList<BrokerAllProperties> {
        var response = ArrayList<BrokerAllProperties>()

        for (i in 1..5) {
            var data = BrokerAllProperties(
                "tharhi andharatharhi madhubani bihar, " + i,
                "1",
                "vivektharhi@gmail.com",
                "7501643766",
                "vivek kumar"
            )
            response.add(data);
        }


        return response;

    }

    private fun setPropertyList(listResponse: ArrayList<PropertyAgent>) {

        propertyList?.layoutManager = LinearLayoutManager(appContext)
        if (listResponse.size == 1) {
            adapter = AgentDashboardPropertyAdepter(
                appContext,
                activity,

                listResponse as ArrayList<PropertyAgent>
            ,progressBar_notification!!)
        } else {

            adapter = AgentDashboardPropertyAdepter(
                appContext,
                activity,

                listResponse as ArrayList<PropertyAgent>
            ,progressBar_notification!!)
        }
        propertyList!!.adapter = adapter

    }

    companion object {
        var linkRequestListt = ArrayList<AppNotifications>()
        var linkRequestListForLdReq = ArrayList<AppNotifications>()
        var alertsListt = ArrayList<AppNotifications>()
        var messageList = ArrayList<AppNotifications>()
    }

    private fun getPaymentList() {

        total_payout_agent!!.text = "$ 0"
        total_ammount!!.text = "$ 0"
        total_payout_fransise!!.text = "$ 0"
        total_profit!!.text = "$ 0"

        amount1 = 0.00
        amount2 = 0.00
        amount3 = 0.00
        if (NetworkConnection.isNetworkConnected(appContext)) {
            //Create retrofit Service

            val credentials = LandlordPaymentHistoryCredential()


            //To get the data we are hard coding the user id here, as per the discussion with Sumit 17-01-2023
            credentials.userCatalogId = Kotpref.userId
//            credentials.userCatalogId = "3104"


            val paymentListService: ApiInterface =
                ApiClient(appContext).provideService(ApiInterface::class.java)
            val apiCall: Observable<BrokerPaymentHistoryListResponse> =
                paymentListService.brokerPaymentHistory(credentials, "", "")

            RxAPICallHelper().call(
                apiCall,
                object : RxAPICallback<BrokerPaymentHistoryListResponse> {
                    override fun onSuccess(paymentListResponse: BrokerPaymentHistoryListResponse) {
                        Log.e(
                            "onSuccessPayment",
                            paymentListResponse.responseDto?.responseDescription.toString()
                        )
                        if (paymentListResponse.data != null && !paymentListResponse.data?.isEmpty()!!) {
                            for (data in paymentListResponse.data!!) {
                                if (!data.amount.isNullOrEmpty() && data.amount.toDouble() != 0.00) {
                                    amount1 += data.amount.toDouble()
                                } else {
                                    amount1 = 0.00
                                }
                            }

                            total_payout_agent!!.text =
                                "$" + amount1.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()

                        } else {
                            //btnTryAgain?.visibility = View.VISIBLE
                        }

                        getPaymentList2()


                    }

                    override fun onFailed(t: Throwable) {
                        // show error
                        Log.e("onFailure", t.toString())

                        try {
                            Util.apiFailure(appContext, t)
                        } catch (e: Exception) {
                            Toast.makeText(
                                appContext,
                                getString(R.string.error_something),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
        } else {
            Toast.makeText(appContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
        }
    }

    private fun getPaymentList2() {
        if (NetworkConnection.isNetworkConnected(appContext)) {
            //Create retrofit Service
            val credentials = LandlordPaymentHistoryCredential()

            //To get the data we are hard coding the user id here, as per the discussion with Sumit 17-01-2023
            credentials.userCatalogId = Kotpref.userId
//            credentials.userCatalogId = "3104"
            val paymentListService: ApiInterface =
                ApiClient(appContext).provideService(ApiInterface::class.java)
            val apiCall: Observable<BrokerPaymentHistoryListResponse> =
                paymentListService.getBrokerPaymentHistory(credentials, "", "")

            RxAPICallHelper().call(
                apiCall,
                object : RxAPICallback<BrokerPaymentHistoryListResponse> {
                    override fun onSuccess(paymentListResponse: BrokerPaymentHistoryListResponse) {
                        Log.e(
                            "onSuccessPayment",
                            paymentListResponse.responseDto?.responseDescription.toString()
                        )
                        if (paymentListResponse.data != null && !paymentListResponse.data?.isEmpty()!!) {

                            for (data in paymentListResponse.data!!) {
                                if (data.amount.toDouble() != 0.00) {

                                    amount2 += data.amount.toDouble()

                                } else {
                                    amount2 = 0.00
                                }
                            }
                            total_ammount!!.text =
                                "$" + amount2.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()


                        } else {
                            //btnTryAgain?.visibility = View.VISIBLE
                        }


                    }

                    override fun onFailed(t: Throwable) {
                        // show error
                        Log.e("onFailure", t.toString())

                        try {
                            Util.apiFailure(appContext, t)
                        } catch (e: Exception) {
                            Toast.makeText(
                                appContext,
                                getString(R.string.error_something),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
        } else {
            Toast.makeText(appContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
        }
    }

    private fun getUserInfo() {
        //Create retrofit Service

        val credentials = UserDetailCredential()

        credentials.userId = Kotpref.userId
        val userDetailService: ApiInterface =
            ApiClient(appContext).provideService(ApiInterface::class.java)
        val apiCall: Observable<UserDetailResponse> =
            userDetailService.getUserDetail(credentials) //Test API Key

        RxAPICallHelper().call(apiCall, object : RxAPICallback<UserDetailResponse> {
            override fun onSuccess(userDetail: UserDetailResponse) {
                userDetailResponse = userDetail.data!!
                setUserInfo()
            }

            override fun onFailed(t: Throwable) {
                // show error
                Log.e("onFailure", t.toString())
                //  progressBarList?.visibility = View.GONE
            }
        })
    }

    private fun setUserInfo() {
        name_title?.text = userDetailResponse.firstName + " " + userDetailResponse.lastName
        var profileImg: String = ""
        val customPb: CircularProgressDrawable = Util.customProgress(requireContext())
        for (item in listUserImages) {

            if (item.uploadFileType.equals(getString(R.string.key_profile_pic))) {
                profileImg = item.fileName

                break
            }
        }
        Log.e("imageUrl", "" + getString(R.string.base_url_img2) + profileImg)

        if (!profileImg.isNullOrEmpty()) {

            Kotpref.profile_image = getString(R.string.base_url_img2) + profileImg

            Log.e("imageUrl", "" + R.string.base_url_img2 + profileImg)
            Glide.with(appContext)
                .load(Kotpref.profile_image)
                .placeholder(customPb)
                .into(profile_pic!!)
        }else{
            Kotpref.profile_image=R.drawable.profile_default_new.toString()
            Glide.with(appContext)
                .load(Kotpref.profile_image)
                .placeholder(R.drawable.profile_default_new)
                .into(profile_pic!!)
        }
    }


    private fun fetchImages() {
        if (NetworkConnection.isNetworkConnected(appContext)) {

            val credentials = FetchDocumentCredential()
            credentials.userId = Kotpref.userId

            val signatureApi: ApiInterface =
                ApiClient(appContext).provideService(ApiInterface::class.java)
            val apiCall: Observable<FetchDocumentResponse> =
                signatureApi.fetchDocument(credentials)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", it.responseDto!!.responseDescription.toString())

                    if (it.responseDto!!.responseCode.equals(200) && it.data != null) {
                        listUserImages = it.data!!

                        setUserInfo()

                    } else {
                        Toast.makeText(appContext, it.responseDto!!.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                },
                    { e ->
                        Log.e("onFailure", e.toString())

                        e.message?.let {
                            Toast.makeText(appContext, it, Toast.LENGTH_SHORT).show()
                        }
                    })
        } else {
            Toast.makeText(appContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
        }
    }
    private fun getSumOfAgentPropertiesAndApprovedTenant() {

        if (NetworkConnection.isNetworkConnected(appContext)) {
            //Create retrofit Service
            val credentials = LandlordRevenueDetailCredential()

            credentials.userCatalogId = Kotpref.userId
            val service: ApiInterface =
                ApiClient(appContext).provideService(ApiInterface::class.java)
            val apiCall: Observable<AgentPropertiesAndTenantResponse> =
                service.getSumOfAgentPropertiesAndApprovedTenant(credentials)

            RxAPICallHelper().call(
                apiCall,
                object : RxAPICallback<AgentPropertiesAndTenantResponse> {
                    override fun onSuccess(response: AgentPropertiesAndTenantResponse) {

                        if (response.data != null) {
                            amount_payout_to_franchise_value.text= response.data!!.assignedProperties
                            amount_payout_to_agent_value.text= response.data!!.leaseConvertedProperties
                            total_profit_value.text= response.data!!.onboardedProperties
                        }


                    }

                    override fun onFailed(t: Throwable) {

                    }
                })
        } else {
            Toast.makeText(appContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
        }
    }
    private fun getMyBookOfBusiness() {

        if (NetworkConnection.isNetworkConnected(appContext)) {
            //Create retrofit Service
            val credentials = LandlordRevenueDetailCredential()

            credentials.userCatalogId = Kotpref.userId
            val service: ApiInterface =
                ApiClient(appContext).provideService(ApiInterface::class.java)
            val apiCall: Observable<AgentMyBookOfBusinesResponse> =
                service.getAgentBookOfBusiness(credentials)

            RxAPICallHelper().call(
                apiCall,
                object : RxAPICallback<AgentMyBookOfBusinesResponse> {
                    override fun onSuccess(response: AgentMyBookOfBusinesResponse) {

                        if (response.data != null) {
                            amount_received_value.text="$ "+response.data!!.myBookOfBusiness
                        }


                    }

                    override fun onFailed(t: Throwable) {

                    }
                })
        } else {
            Toast.makeText(appContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
        }
    }
}