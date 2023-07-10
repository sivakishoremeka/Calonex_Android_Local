package mp.app.calonex.broker.fragment


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
import kotlinx.android.synthetic.main.fragment_broker_payment.*
import kotlinx.android.synthetic.main.fragment_home_broker.txt_lp_notify
import mp.app.calonex.R
import mp.app.calonex.agent.activity.NotifyScreenAgent
import mp.app.calonex.broker.activity.HomeActivityBroker
import mp.app.calonex.broker.adapter.DashBoardPropertyListAdapter
import mp.app.calonex.broker.responce.BrokerFranchiseInfoResponse
import mp.app.calonex.broker.responce.BrokerPaymentHistoryListResponse
import mp.app.calonex.common.apiCredentials.FetchDocumentCredential
import mp.app.calonex.common.apiCredentials.LandlordPaymentHistoryCredential
import mp.app.calonex.common.apiCredentials.NotificationCredential
import mp.app.calonex.common.apiCredentials.UserDetailCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.model.*
import mp.app.calonex.landlord.response.AppNotificationResponse
import mp.app.calonex.landlord.response.BrokerDashboardAllPropertiesResponse
import mp.app.calonex.landlord.response.FetchDocumentResponse
import mp.app.calonex.landlord.response.UserDetailResponse
import java.math.RoundingMode


class HomeFragmentBroker : Fragment() {
    var userDetailResponse = UserDetail()
    var listUserImages = ArrayList<FetchDocumentModel>()

    lateinit var appContext: Context

    var propertyList: RecyclerView? = null
    var adapter: DashBoardPropertyListAdapter? = null
    var list: ArrayList<BrokerAllProperties>? = null
    var name_title: TextView? = null
    var tv_visit_our_website: TextView? = null
    var viewMoreProperty: TextView? = null


    private var profile_pic: CircleImageView? = null
    private var layoutNotify: RelativeLayout? = null
    var appNotifications = ArrayList<AppNotifications>()
    var mNotificationLinkRequest = NotificationLinkRequestModel()
    var mNotificationLinkRequestNew = NotificationLinkRequestModelNew()

    private var total_amount_received: Double = 0.0
    private var total_payout_fransise: Double = 0.00
    private var total_payout_agent: Double = 0.00
    private var total_profit: Double = 0.00

    private var totalAmountReceivedTextView: TextView? = null
    private var totalPayoutFransiseTextView: TextView? = null
    private var totalPayoutAgentTextView: TextView? = null
    private var totalProfitTextView: TextView? = null



    private var progressBar_notification: ProgressBar? = null
   /* var amount1: Double = 0.00
    var amount2: Double = 0.00
    var amount3: Double = 0.00
*/

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_home_broker, container, false)
        initComponents(rootView)
        setDefault()
        return rootView
    }

    private fun initComponents(viewRoot: View) {
        viewMoreProperty = viewRoot.findViewById(R.id.view_more_property) as TextView
        propertyList = viewRoot.findViewById(R.id.propertylist)
        propertyList!!.setHasFixedSize(true);
        name_title = viewRoot.findViewById<View>(R.id.name_title) as TextView
        layoutNotify = viewRoot.findViewById(R.id.layout_lp_notify)
        tv_visit_our_website = viewRoot.findViewById(R.id.tv_visit_our_website)

        totalAmountReceivedTextView = viewRoot.findViewById(R.id.amount_received_value)
        totalPayoutFransiseTextView = viewRoot.findViewById(R.id.amount_payout_to_franchise_value)
        totalPayoutAgentTextView = viewRoot.findViewById(R.id.amount_payout_to_agent_value)
        totalProfitTextView = viewRoot.findViewById(R.id.total_profit_value)
        profile_pic = viewRoot.findViewById(R.id.profile_pic)
        progressBar_notification = viewRoot.findViewById(R.id.progressBar_notification)

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
                (requireActivity() as HomeActivityBroker).navigateTo("property")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        setData()

        getUserInfo()
        fetchImages()
        getPropertyList()
        getReceivedBrokerPaymentHistory()




    }

    private fun setData() {

        list = ArrayList<BrokerAllProperties>()

        //sub_header_layout.visibility = View.VISIBLE
        adapter =
            DashBoardPropertyListAdapter(appContext, activity, list!!, progressBar_notification!!)

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
                        linkRequestListForLdReq.clear()
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
            val credential = LandlordPaymentHistoryCredential()
            credential.userCatalogId = Kotpref.userId
            var propertyListService: ApiInterface =
                ApiClient(appContext).provideService(ApiInterface::class.java);
            val apiCall: Observable<BrokerDashboardAllPropertiesResponse> =
                propertyListService.getAllProperties(credential)
            RxAPICallHelper().call(
                apiCall,
                object : RxAPICallback<BrokerDashboardAllPropertiesResponse> {
                    override fun onSuccess(response: BrokerDashboardAllPropertiesResponse) {
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

    private fun setPropertyList(listResponse: ArrayList<BrokerAllProperties>) {

        propertyList?.layoutManager = LinearLayoutManager(appContext)
        if (listResponse.size == 1) {
            adapter = DashBoardPropertyListAdapter(
                appContext,
                activity,

                listResponse as ArrayList<BrokerAllProperties>,
                progressBar_notification!!
            )
        } else {

            adapter = DashBoardPropertyListAdapter(
                appContext,
                activity,

                listResponse as ArrayList<BrokerAllProperties>, progressBar_notification!!
            )
        }
        propertyList!!.adapter = adapter

    }

    companion object {
        var linkRequestListt = ArrayList<AppNotifications>()
        var linkRequestListForLdReq = ArrayList<AppNotifications>()
        var alertsListt = ArrayList<AppNotifications>()
        var messageList = ArrayList<AppNotifications>()
    }

    private fun getToBePaidBrokerPaymentHistory() {



       /* total_amount_received = 0.00
        total_payout_agent = 0.00
        total_payout_fransise = 0.00
        total_profit=0.0*/
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
                            "PaidToBrokerRes", Gson().toJson(paymentListResponse)
                        )
                        if (paymentListResponse.data != null && !paymentListResponse.data?.isEmpty()!!) {
                            for (data in paymentListResponse.data!!) {
                                if (!data.amount.isNullOrEmpty() && data.amount.toDouble() != 0.00) {
                                    total_payout_agent = total_payout_agent!! + data.amount.toDouble()
                                } else {
                                    total_payout_agent = 0.00
                                }
                            }

                            totalPayoutAgentTextView!!.text = "$" + total_payout_agent!!.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()

                        } else {
                            //btnTryAgain?.visibility = View.VISIBLE
                        }


                        getFranchise()

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

    private fun getReceivedBrokerPaymentHistory() {
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
                            "ReceivedResponse",Gson().toJson( paymentListResponse)
                        )
                        if (paymentListResponse.data != null && !paymentListResponse.data?.isEmpty()!!) {

                            for (data in paymentListResponse.data!!) {
                                if (data.amount.toDouble() != 0.00) {

                                    total_amount_received = total_amount_received.plus(data.amount.toDouble())

                                } else {
                                    total_amount_received = 0.00
                                }
                            }
                            totalAmountReceivedTextView!!.text = "$" + total_amount_received!!.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()


                        } else {
                            //btnTryAgain?.visibility = View.VISIBLE
                        }
                        getToBePaidBrokerPaymentHistory()

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

    private fun getFranchise() {
        if (NetworkConnection.isNetworkConnected(appContext)) {
            //Create retrofit Service
            pb_payment?.visibility = View.VISIBLE
            val credentials = UserDetailCredential()

            credentials.userId = Kotpref.userId
            val paymentListService: ApiInterface =
                ApiClient(appContext).provideService(ApiInterface::class.java)
            val apiCall: Observable<BrokerFranchiseInfoResponse> =
                paymentListService.getFranchise(credentials)

            RxAPICallHelper().call(
                apiCall,
                object : RxAPICallback<BrokerFranchiseInfoResponse> {
                    override fun onSuccess(paymentListResponse: BrokerFranchiseInfoResponse) {
                        Log.e(
                            "onSuccessPayment",
                            paymentListResponse.percentage.toString()
                        )
                        try {
                            if (!paymentListResponse.percentage.toString()
                                    .isNullOrEmpty() && !paymentListResponse.percentage.toString()
                                    .equals("0.00")
                            ) {
                                total_payout_fransise =
                                    ((total_amount_received.minus(total_payout_agent)) * paymentListResponse.percentage!!.toDouble() / 100.00)
                                totalPayoutFransiseTextView!!.text =
                                    "$" + total_payout_fransise.toBigDecimal().setScale(2, RoundingMode.UP)
                                        .toDouble()
                            }
                            totalProfitTextView!!.text =
                                "$" + (total_amount_received - (total_payout_agent + total_payout_fransise)).toBigDecimal()
                                    .setScale(2, RoundingMode.UP).toDouble()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Log.d(
                                "TAG", "onSuccess: " + e.message
                            )
                        }


                        pb_payment?.visibility = View.GONE
                    }

                    override fun onFailed(t: Throwable) {
                        // show error
                        Log.e("onFailure", t.toString())
                       // btnTryAgain?.visibility = View.VISIBLE
                        ll_filter_lay?.visibility = View.GONE
                        pb_payment?.visibility = View.GONE
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
                .load(getString(R.string.base_url_img2) + profileImg)
                .placeholder(customPb)
                .into(profile_pic!!)
        }else{
            Kotpref.profile_image=R.drawable.profile_default_new.toString()
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

    fun setDefault(){
        totalPayoutAgentTextView!!.text = "$ 0"
        totalAmountReceivedTextView!!.text = "$ 0"
        totalPayoutFransiseTextView!!.text = "$ 0"
        totalProfitTextView!!.text = "$ 0"
    }


}