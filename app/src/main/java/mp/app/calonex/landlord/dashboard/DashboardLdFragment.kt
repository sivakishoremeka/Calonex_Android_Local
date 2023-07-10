package mp.app.calonex.landlord.dashboard

import android.content.Context
import android.content.Intent
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
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_landlord_dashboard.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.*
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.AllApplicantDetailsActivity
import mp.app.calonex.landlord.activity.HomeActivityCx
import mp.app.calonex.landlord.activity.NotifyScreen
import mp.app.calonex.landlord.adapter.DashBoardPropertyListAdapter
import mp.app.calonex.landlord.model.*
import mp.app.calonex.landlord.response.*
import mp.app.calonex.service.FeaturesService
import org.json.JSONException
import java.util.*
import kotlin.collections.ArrayList

class DashboardLdFragment : Fragment() {
    val BackgroundIntentServiceAction = "android.intent.action.CUSTOME_ACTION_1"
    var userDetailResponse = UserDetail()
    var listUserImages = ArrayList<FetchDocumentModel>()
    lateinit var appContext: Context
    var propertyList: RecyclerView? = null
    var pb_loading: ProgressBar? = null
    var list: ArrayList<Property>? = null
    var adapter: DashBoardPropertyListAdapter? = null
    var viewMoreProperty: TextView? = null
    private var layout_lp_notify: RelativeLayout? = null
    private var cv_total_applicant: CardView? = null
    var appNotifications = ArrayList<AppNotifications>()
    var mNotificationLinkRequest = NotificationLinkRequestModel()
    //var linkRequestList = ArrayList<AppNotifications>()
    //var alertsList = ArrayList<AppNotifications>()
    var messageList = ArrayList<AppNotifications>()
    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_landlord_dashboard, container, false)
        layout_lp_notify = rootView.findViewById(R.id.layout_lp_notify)
        cv_total_applicant = rootView.findViewById(R.id.cv_total_applicant)
        viewMoreProperty = rootView.findViewById(R.id.view_more_property) as TextView
        propertyList = rootView.findViewById(R.id.propertylist)
        pb_loading = rootView.findViewById(R.id.pb_loading)
        propertyList!!.setHasFixedSize(true);
        actionCompnent()

        return rootView
    }

    override fun onResume() {
        super.onResume()
        layout_lp_notify!!.visibility = View.VISIBLE
        linkRequestList.clear()
        alertsList.clear()
        messageList.clear()
        getNotificationList()

        fetchImages()
        getUserInfo()
        getPropertyList()
        findLease()


        val calendar = Calendar.getInstance(TimeZone.getDefault())
        val year: Int = calendar.get(Calendar.YEAR)
        getRevenueDetails("" + year)
        getOccupancy()


    }

    private fun actionCompnent() {
        layout_lp_notify!!.setOnClickListener {
            Util.navigationNext(requireActivity(), NotifyScreen::class.java)

        }

        viewMoreProperty!!.setOnClickListener {
            try {
                (requireActivity() as HomeActivityCx).navigateTo("property")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        cv_total_applicant!!.setOnClickListener {
            Util.navigationNext(requireActivity(), AllApplicantDetailsActivity::class.java)
        }

        setData()

        /*getPaymentList()*/

    }

    var applicantCount = 0
    private fun findLease() {
        if (NetworkConnection.isNetworkConnected(requireContext())) {
            //Create retrofit Service
            val credential = FindLeaseCredentials()
            credential.userId = Kotpref.userId
            credential.userRole = Kotpref.userRole

            val findApi: ApiInterface =
                ApiClient(requireContext()).provideService(ApiInterface::class.java)
            val apiCall: Observable<FindLeaseResponse> =
                findApi.findLease(credential)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", it.responseDto?.responseDescription.toString())
                    try {
                        if (it.data != null) {
                            if (it!!.data!!.size > 0) {
                                applicantCount = 0
                                for (i in 0 until it!!.data!!.size) {
                                    if (it!!.data!![i].leaseStatus != "19") {
                                        applicantCount += 1
                                    }

                                }
                            }

                            tv_total_applicants.text = "$applicantCount"
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                },
                    { e ->
                        Log.e("onFailure", e.toString())

                    })
        }

    }

    private fun getNotificationList() {
        layout_lp_notify!!.visibility = View.VISIBLE
        requireActivity().getWindow().setFlags(
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
                Log.e("onSuccess", response.responseCode.toString())
                try {
                    if (!response.data?.isEmpty()!!) {
                        appNotifications = response.data!!
                        linkRequestList.clear()
                        alertsList.clear()
                        messageList.clear()
                        for (i in 0..appNotifications.size - 1) {
                            val gson = Gson()

                            if (appNotifications[i].activityType == "1") {
                                val objectList = gson.fromJson(
                                    appNotifications[i].notificationData,
                                    NotificationLinkRequestModel::class.java
                                )
                                mNotificationLinkRequest = objectList
                                linkRequestList.add(appNotifications[i])

                                Log.e("NOTI DATA 1", linkRequestList.size.toString())
                            } else if (appNotifications[i].activityType == "2") {
                                alertsList.add(appNotifications[i])
                            } else if (appNotifications[i].activityType == "3") {
                                messageList.add(appNotifications[i])
                            }
                        }
                    }
                    Kotpref.notifyCount = (linkRequestList.size + alertsList.size).toString()
                    txt_lp_notify!!.text = Kotpref.notifyCount
                    //(activity as HomeActivityCx?)!!.addBadgeNew(messageList.size.toString())

                    layout_lp_notify!!.visibility = View.VISIBLE
                    activity!!.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    if (Integer.parseInt(Kotpref.notifyCount) < 1) {
                        txt_lp_notify!!.visibility = View.VISIBLE

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
                layout_lp_notify!!.visibility = View.VISIBLE
                activity!!.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                txt_lp_notify!!.text = (linkRequestList.size + alertsList.size).toString()

                if (Integer.parseInt(Kotpref.notifyCount) < 1) {
                    txt_lp_notify!!.visibility = View.VISIBLE

                } else {
                    txt_lp_notify!!.visibility = View.VISIBLE

                }
            }
        })

    }

    private fun setData() {

        list = ArrayList<Property>()
        // list!!.addAll(getStaticData())

        //sub_header_layout.visibility = View.VISIBLE
        adapter =
            DashBoardPropertyListAdapter(appContext, activity, list!!, pb_loading!!)

        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        propertyList?.layoutManager = layoutManager
        propertyList?.adapter = adapter

        layout_lp_notify!!.setOnClickListener {
            Util.navigationNext(requireActivity(), NotifyScreen::class.java)
        }
    }

    private fun getPropertyList() {

        if (NetworkConnection.isNetworkConnected(appContext)) {
            val credential = GetPropertyListApiCredential()
            credential.userCatalogID = Kotpref.userId
            pb_loading!!.visibility = View.VISIBLE

            var propertyListService: ApiInterface =
                ApiClient(appContext).provideService(ApiInterface::class.java);
            val apiCall: Observable<PropertyListResponse> =
                propertyListService.getPropertyList(credential)
            RxAPICallHelper().call(
                apiCall,
                object : RxAPICallback<PropertyListResponse> {
                    override fun onSuccess(response: PropertyListResponse) {
                        Log.d(
                            "onSuccess notification",
                            "onSuccess notification" + (response.responseCode)
                        )
                        setPropertyList(response.data!!)
                        if (FeaturesService.allModel == null) {
                            val intent = Intent(appContext, FeaturesService::class.java).apply {
                                setAction(BackgroundIntentServiceAction)
                            }
                            appContext.startService(intent)

                        }


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

    private fun setPropertyList(listResponse: ArrayList<Property>) {
        pb_loading!!.visibility = View.GONE

        propertyList?.layoutManager = LinearLayoutManager(appContext)
        if (listResponse.size == 1) {
            adapter = DashBoardPropertyListAdapter(
                appContext,
                activity,

                listResponse as ArrayList<Property>, pb_loading!!
            )
        } else {

            adapter = DashBoardPropertyListAdapter(
                appContext,
                activity,

                listResponse as ArrayList<Property>, pb_loading!!
            )
        }
        propertyList!!.adapter = adapter

    }

    var revenueResponseData = LandlordRevenueDetail()

    private fun getRevenueDetails(year: String) {
        if (NetworkConnection.isNetworkConnected(appContext)) {
            //Create retrofit Service
            val credentials = LandlordRevenueDetailCredential()

            credentials.userCatalogId = Kotpref.userId
            credentials.year = year
            val service: ApiInterface =
                ApiClient(appContext).provideService(ApiInterface::class.java)
            val apiCall: Observable<LandlordRevenueDetailResponse> =
                service.getRevenueDetail(credentials)

            RxAPICallHelper().call(
                apiCall,
                object : RxAPICallback<LandlordRevenueDetailResponse> {
                    override fun onSuccess(response: LandlordRevenueDetailResponse) {
                        Log.e("DDDD", response.toString())
                        if (response.data != null) {
                            try {
                                revenueResponseData = response.data!!
                                tv_total_profit.text = "$ " + String.format(
                                    "%.2f",
                                    revenueResponseData.profit.toDouble()
                                )
                            }catch (e:Exception){
                                e.printStackTrace()
                            }
                        }
                    }

                    override fun onFailed(t: Throwable) {

                    }
                })
        } else {
            Toast.makeText(appContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
        }
    }

    private var vacant: String = "0"
    private var inactive: String = "0"
    private var occupiedOutsideCx: String = "0"
    private var occupiedInsideCx: String = "0"
    private fun getOccupancy() {

        if (NetworkConnection.isNetworkConnected(appContext)) {
            //Create retrofit Service
            val credentials = LandlordPaymentHistoryCredential()

            credentials.userCatalogId = Kotpref.userId
            val service: ApiInterface =
                ApiClient(appContext).provideService(ApiInterface::class.java)
            val apiCall: Observable<LandlordOccupencyResponse> =
                service.getOccupancy(credentials)

            RxAPICallHelper().call(
                apiCall,
                object : RxAPICallback<LandlordOccupencyResponse> {
                    override fun onSuccess(response: LandlordOccupencyResponse) {

                        if (response.data != null) {
                            if (response.data!!.vacant.isNotEmpty())
                                vacant = response.data!!.vacant

                            if (response.data!!.inactive.isNotEmpty())
                                inactive = response.data!!.inactive

                            if (response.data!!.occupiedInsideCx.isNotEmpty())
                                occupiedInsideCx = response.data!!.occupiedInsideCx

                            if (response.data!!.occupiedOutsideCx.isNotEmpty())
                                occupiedOutsideCx = response.data!!.occupiedOutsideCx

                            try {
                                var totalOccupancy =
                                    vacant.toInt() + occupiedOutsideCx.toInt() + occupiedInsideCx.toInt()
                                if (vacant.equals(0) && inactive.equals("0") && occupiedInsideCx.equals(
                                        "0"
                                    ) && occupiedOutsideCx.equals("0")
                                ) {
                                    //do nothing
                                } else {
                                    tv_total_occupancy.text = "" + totalOccupancy
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }


                    }

                    override fun onFailed(t: Throwable) {

                    }
                })
        } else {
            Toast.makeText(appContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
        }
    }

    /* private fun getPaymentList() {

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
     }*/

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

    companion object {
        var linkRequestList = ArrayList<AppNotifications>()
        var alertsList = ArrayList<AppNotifications>()
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


}