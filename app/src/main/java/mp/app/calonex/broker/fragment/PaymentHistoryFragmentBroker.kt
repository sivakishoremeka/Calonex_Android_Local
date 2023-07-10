package mp.app.calonex.broker.fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.Observable
import kotlinx.android.synthetic.main.dialog_filter.view.*
import kotlinx.android.synthetic.main.dialog_filter_for_payment.view.*
import kotlinx.android.synthetic.main.fragment_broker_payment.*
import kotlinx.android.synthetic.main.fragment_broker_payment.name_title
import kotlinx.android.synthetic.main.fragment_broker_payment.profile_pic

import mp.app.calonex.R
import mp.app.calonex.agent.activity.NotifyScreenAgent
import mp.app.calonex.agent.fragment.ProfileFragmentAgent
import mp.app.calonex.common.apiCredentials.LandlordPaymentHistoryCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.adapter.CustomSpinnerAdapter
import mp.app.calonex.broker.adapter.BrokerPaymentListAdapter
import mp.app.calonex.broker.model.BrokerPaymentHistory
import mp.app.calonex.broker.responce.BrokerFranchiseInfoResponse
import mp.app.calonex.broker.responce.BrokerPaymentHistoryListResponse
import mp.app.calonex.common.apiCredentials.CxMsgUsersCredentials
import mp.app.calonex.common.apiCredentials.NotificationCredential
import mp.app.calonex.common.apiCredentials.UserDetailCredential
import mp.app.calonex.landlord.model.*
import mp.app.calonex.landlord.response.AppNotificationResponse
import mp.app.calonex.landlord.response.CxMsgUsersResponse
import mp.app.calonex.landlord.response.UserDetailResponse

import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PaymentHistoryFragmentBroker : Fragment() {
    var linkRequestListt = ArrayList<AppNotifications>()
    var linkRequestListForLdReq = ArrayList<AppNotifications>()
    var alertsListt = ArrayList<AppNotifications>()
    var messageList = ArrayList<AppNotifications>()
    var appNotifications = ArrayList<AppNotifications>()
    var mNotificationLinkRequest = NotificationLinkRequestModel()
    var mNotificationLinkRequestNew = NotificationLinkRequestModelNew()

    private lateinit var appContext: Context

    private var ldPropertyListAdapter: BrokerPaymentListAdapter? = null
    private var refreshPayment: SwipeRefreshLayout? = null
    private var refreshPayment2: SwipeRefreshLayout? = null


    private var btnTryAgain: Button? = null
    private var profilePic: CircleImageView? = null

    private var layoutLphNotify: RelativeLayout? = null
    private var rl_filter_by: RelativeLayout? = null
    private var ll_filter_lay: LinearLayout? = null
    private var txtLphNotify: TextView? = null

    private var total_ammount: TextView? = null
    private var total_payout: TextView? = null
    private var total_payout_agent: TextView? = null
    private var total_profit: TextView? = null
    private var tv_filter_by: TextView? = null
    private var tv_transaction_filter: TextView? = null

    var amount1: Double = 0.00
    var amount2: Double = 0.00
    var amount3: Double = 0.00

    var bDataFound: Boolean = false;

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_broker_payment, container, false)
        refreshPayment = rootView.findViewById(R.id.refresh_ld_payment)
        refreshPayment2 = rootView.findViewById(R.id.refresh_ld_payment_2)

        profilePic=rootView.findViewById(R.id.profile_pic)

        btnTryAgain = rootView.findViewById(R.id.btn_try_again)

        rl_filter_by = rootView.findViewById(R.id.rl_filter_by)
        ll_filter_lay = rootView.findViewById(R.id.ll_filter_lay)
        txtLphNotify = rootView.findViewById(R.id.txt_lp_notify)
        layoutLphNotify = rootView.findViewById(R.id.layout_lp_notify)

        total_ammount = rootView.findViewById(R.id.total_ammount)
        total_payout = rootView.findViewById(R.id.total_payout)
        total_payout_agent = rootView.findViewById(R.id.total_payout_agent)
        total_profit = rootView.findViewById(R.id.total_profit)
        tv_filter_by = rootView.findViewById(R.id.tv_filter_by)
        tv_transaction_filter = rootView.findViewById(R.id.tv_transaction_filter)

        val txt_agent_com: TextView = rootView.findViewById(R.id.txt_agent_com)
        val txt_recived: TextView = rootView.findViewById(R.id.txt_recived)

        txt_agent_com.setBackgroundResource(R.drawable.btn_dk_blue_round)
        txt_recived.setBackgroundResource(R.drawable.btn_dk_grey_round)

        txt_agent_com.setOnClickListener {
            txt_agent_com.setBackgroundResource(R.drawable.btn_dk_blue_round)
            txt_recived.setBackgroundResource(R.drawable.btn_dk_grey_round)
            refresh_ld_payment.visibility = View.VISIBLE
            refresh_ld_payment_2.visibility = View.GONE
        }

        txt_recived.setOnClickListener {
            txt_agent_com.setBackgroundResource(R.drawable.btn_dk_grey_round)
            txt_recived.setBackgroundResource(R.drawable.btn_dk_blue_round)
            refresh_ld_payment.visibility = View.GONE
            refresh_ld_payment_2.visibility = View.VISIBLE
        }




        refreshPayment2?.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                appContext,
                R.color.colorPrimary
            )
        )
        refreshPayment?.setColorSchemeColors(Color.WHITE)
        refreshPayment2?.setColorSchemeColors(Color.WHITE)

        refreshPayment?.setOnRefreshListener {

            getPaymentList()
            refreshPayment?.isRefreshing = false

        }

        refreshPayment2?.setOnRefreshListener {

            getPaymentList()
            //  getPaymentList2()
            refreshPayment2?.isRefreshing = false

        }

        rl_filter_by?.setOnClickListener {
            dialogFilter()
        }

        tv_transaction_filter?.setOnClickListener {
            dialogFilterTransaction()
        }

        Glide.with(appContext)
            .load(Kotpref.profile_image)
            .placeholder(R.drawable.profile_default_new)
            .into(profilePic!!)


        getPaymentList()
        getUserInfo()
        priorityFilter()
        getUserList()
        getNotificationList()


        layoutLphNotify!!.setOnClickListener {
            Util.navigationNext(requireActivity(), NotifyScreenAgent::class.java)
        }
        btnTryAgain?.setOnClickListener {
            btnTryAgain?.visibility = View.GONE
            getPaymentList()

        }



        return rootView
    }


    private var cal = Calendar.getInstance()
    private var year = cal.get(Calendar.YEAR)
    private var month = cal.get(Calendar.MONTH)
    private var day = cal.get(Calendar.DAY_OF_MONTH)
    val myFormat = "MMM dd, yyyy"

    var startDate = "2020-01-01"
    var endDate = ""

    var selectedStartDate = ""
    var selectedEndDate = ""

    var startDateFlag: Boolean = false


    private fun dialogFilterTransaction() {
        var spinnerUnitAdapter: CustomSpinnerAdapter? = null
        var spinnerPropertyAdapter = CustomSpinnerAdapter(appContext, propertyList)
        var quarterUnit = ArrayList<String>()
        var listUnit = ArrayList<String>()
        listUnit = unitList
        quarterUnit.add("Q1")
        quarterUnit.add("Q2")
        quarterUnit.add("Q3")
        quarterUnit.add("Q4")
        var spinnerQuarterAdapter = CustomSpinnerAdapter(appContext, quarterUnit)


        val dialog = Dialog(appContext, R.style.DialogSlideBottomChooseTeam)
        val bottomSheetDialog = layoutInflater.inflate(R.layout.dialog_filter_for_payment, null)
        //login button click of custom layout
        dialog.setContentView(bottomSheetDialog)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.show()

        bottomSheetDialog.edit_start_date?.setOnClickListener {
            val dateDialog = DatePickerDialog(
                requireContext(),
                { view, year, monthOfYear, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    bottomSheetDialog.edit_start_date!!.setText(sdf.format(cal.time))
                    startDateFlag = true

                    val formatter2 = SimpleDateFormat("yyyy-MM-dd")
                    val todayString2 = formatter2.format(cal.time)
                    startDate = todayString2
                    selectedStartDate = startDate
                },
                year,
                month,
                day
            )
            dateDialog.show()
            val cal1 = Calendar.getInstance()
            cal1.add(Calendar.DAY_OF_MONTH, 0)
            dateDialog.datePicker.maxDate = cal1.timeInMillis
            bottomSheetDialog.edit_start_date!!.error = null
        }

        bottomSheetDialog.edit_end_date?.setOnClickListener {
            if (startDateFlag) {
                val dateDialog = DatePickerDialog(
                    requireContext(),
                    { view, year, monthOfYear, dayOfMonth ->
                        cal.set(Calendar.YEAR, year)
                        cal.set(Calendar.MONTH, monthOfYear)
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                        val sdf = SimpleDateFormat(myFormat, Locale.US)
                        bottomSheetDialog.edit_end_date!!.setText(sdf.format(cal.time))

                        val formatter3 = SimpleDateFormat("yyyy-MM-dd")
                        val todayString3 = formatter3.format(cal.time)
                        Log.e("DateNow 3", todayString3)
                        endDate = todayString3
                        selectedEndDate = endDate
                    },
                    year,
                    month,
                    day
                )
                dateDialog.show()
                val cal1 = Calendar.getInstance()
                cal1.add(Calendar.DAY_OF_MONTH, 0)
                dateDialog.datePicker.maxDate = cal1.timeInMillis
                bottomSheetDialog.edit_end_date!!.error = null
            } else {
                Toast.makeText(appContext, "Select Start date first.", Toast.LENGTH_SHORT).show()
            }
        }

        bottomSheetDialog.spn_quarter!!.adapter = spinnerQuarterAdapter
        bottomSheetDialog.spn_property!!.adapter = spinnerPropertyAdapter
        var propertyValue = ""
        var unitValue = ""
        bottomSheetDialog.spn_property.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    if (position > 0) {
                        propertyValue = propertyList.get(position)
                        listUnit = getUnitList(propertyValue!!)
                        spinnerUnitAdapter = CustomSpinnerAdapter(appContext, listUnit)
                        bottomSheetDialog.spn_unit!!.adapter = spinnerUnitAdapter

                    } else {
                        spinnerUnitAdapter = CustomSpinnerAdapter(appContext, listUnit)
                        bottomSheetDialog.spn_unit!!.adapter = spinnerUnitAdapter
                    }

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // another interface callback
                }
            }

        bottomSheetDialog.btn_cancel?.setOnClickListener {

            dialog.dismiss()
        }

        bottomSheetDialog.btn_apply?.setOnClickListener {
            getPaymentList()
            dialog.dismiss()

        }

    }

    private fun getUnitList(value: String): ArrayList<String> {
        lateinit var filterList: List<CxMsgUsers>
        var filterUnit = ArrayList<String>()
        filterList = userResponseList.filter { it.propertyAddress == value }

        var listUnit = mutableListOf<String>()
        for (item in filterList) {
            if (!item.unitNumber.isNullOrEmpty()) {
                listUnit.add(item.unitNumber)
            }
        }
        filterUnit.add("Select Unit")
        filterUnit.addAll(ArrayList<String>(listUnit.sorted().toSet().toList()))

        return filterUnit
    }

    private var propertyList = ArrayList<String>()
    private var unitList = ArrayList<String>()
    private var userResponseList = ArrayList<CxMsgUsers>()

    private fun getUserList() {
        val msgCredentials = CxMsgUsersCredentials()

        msgCredentials.userCatalogId = Kotpref.userId
        msgCredentials.role = Kotpref.userRole
        val userListService: ApiInterface =
            ApiClient(appContext).provideService(ApiInterface::class.java)
        val apiCall: Observable<CxMsgUsersResponse> =
            userListService.getMsgUsers(msgCredentials)

        RxAPICallHelper().call(apiCall, object : RxAPICallback<CxMsgUsersResponse> {
            override fun onSuccess(response: CxMsgUsersResponse) {
                if (!response.data?.isEmpty()!!) {
                    userResponseList = response.data as ArrayList<CxMsgUsers>
                    propertyList.clear()
                    unitList.clear()
                    propertyList.add("Select Property")
                    val addList = mutableListOf<String>()
                    val usersList = mutableListOf<String>()
                    for (item in userResponseList) {
                        if (!item.userName.isNullOrEmpty()) {
                            usersList.add(item.userName + item.role.replace("CX", ""))
                            addList.add(item.propertyAddress)
                        }
                    }

                    propertyList.addAll(ArrayList<String>(addList.toSet().toList()))
                }
            }

            override fun onFailed(t: Throwable) {
                // show error
                Log.e("onFailure", t.toString())
            }
        })
    }

    override fun onStart() {
        super.onStart()

        if (Kotpref.notifyCount != null && !Kotpref.notifyCount.isEmpty() && Integer.parseInt(
                Kotpref.notifyCount
            ) > 0
        ) {
            txtLphNotify!!.text = Kotpref.notifyCount
            txtLphNotify!!.visibility = View.VISIBLE
        } else {
            txtLphNotify!!.visibility = View.GONE
        }
    }

    private fun getPaymentList() {
        amount1 = 0.00
        amount2 = 0.00
        amount3 = 0.00
        if (NetworkConnection.isNetworkConnected(appContext)) {
            //Create retrofit Service
            pb_payment?.visibility = View.VISIBLE
            val credentials = LandlordPaymentHistoryCredential()


            //To get the data we are hard coding the user id here, as per the discussion with Sumit 17-01-2023
            credentials.userCatalogId = Kotpref.userId
            //credentials.userCatalogId = "3104"


            val paymentListService: ApiInterface =
                ApiClient(appContext).provideService(ApiInterface::class.java)
            val apiCall: Observable<BrokerPaymentHistoryListResponse> =
                paymentListService.brokerPaymentHistory(
                    credentials,
                    selectedStartDate,
                    selectedEndDate
                )

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
                            setPaymentList(paymentListResponse.data!!)
                            bDataFound = true
                        } else {
                            //btnTryAgain?.visibility = View.VISIBLE
                        }

                        getPaymentList2()

                        pb_payment?.visibility = View.GONE
                    }

                    override fun onFailed(t: Throwable) {
                        // show error
                        Log.e("onFailure", t.toString())
                        btnTryAgain?.visibility = View.VISIBLE
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

    private fun getPaymentList2() {
        if (NetworkConnection.isNetworkConnected(appContext)) {
            //Create retrofit Service
            pb_payment?.visibility = View.VISIBLE
            val credentials = LandlordPaymentHistoryCredential()

            //To get the data we are hard coding the user id here, as per the discussion with Sumit 17-01-2023
            credentials.userCatalogId = Kotpref.userId
            //credentials.userCatalogId = "3104"

            val paymentListService: ApiInterface =
                ApiClient(appContext).provideService(ApiInterface::class.java)
            val apiCall: Observable<BrokerPaymentHistoryListResponse> =
                paymentListService.getBrokerPaymentHistory(
                    credentials,
                    selectedStartDate,
                    selectedEndDate
                )

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
                            getFranchise()

                            setPaymentList2(paymentListResponse.data!!)
                            bDataFound = true
                        } else {
                            //btnTryAgain?.visibility = View.VISIBLE
                        }
                        pb_payment?.visibility = View.GONE

                        if (bDataFound) {
                            btnTryAgain?.visibility = View.GONE
                            ll_filter_lay?.visibility = View.VISIBLE
                        } else {
                            btnTryAgain?.visibility = View.VISIBLE
                            ll_filter_lay?.visibility = View.GONE

                        }
                    }

                    override fun onFailed(t: Throwable) {
                        // show error
                        Log.e("onFailure", t.toString())
                        btnTryAgain?.visibility = View.VISIBLE
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

    private fun setPaymentList(listResponse: ArrayList<BrokerPaymentHistory>) {
        setList(listResponse)
    }

    private fun setList(rootList: ArrayList<BrokerPaymentHistory>) {
        rv_ld_payments?.layoutManager = LinearLayoutManager(appContext)
        val list: MutableList<BrokerPaymentHistory>? = rootList.toMutableList()

        val listPayment = ArrayList(list!!.reversed())
        ldPropertyListAdapter = BrokerPaymentListAdapter(
            appContext,
            activity,
            listPayment,
            false
        )
        rv_ld_payments?.adapter = ldPropertyListAdapter
    }

    private fun setPaymentList2(listResponse: ArrayList<BrokerPaymentHistory>) {
        setList2(listResponse)
    }

    private fun setList2(rootList: ArrayList<BrokerPaymentHistory>) {
        rv_ld_payments_2?.layoutManager = LinearLayoutManager(appContext)
        val list: MutableList<BrokerPaymentHistory>? = rootList.toMutableList()

        val listPayment = ArrayList(list!!.reversed())
        ldPropertyListAdapter = BrokerPaymentListAdapter(
            appContext,
            activity,
            listPayment,
            true
        )
        rv_ld_payments_2?.adapter = ldPropertyListAdapter
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
                                amount3 =
                                    ((amount2 - amount1) * paymentListResponse.percentage!!.toDouble() / 100.00)
                                total_payout!!.text =
                                    "$" + amount3.toBigDecimal().setScale(2, RoundingMode.UP)
                                        .toDouble()

                            }
                            total_profit!!.text =
                                "$" + (amount2 - (amount1 + amount3)).toBigDecimal()
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
                        btnTryAgain?.visibility = View.VISIBLE
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
                ProfileFragmentAgent.userDetailResponse = userDetail.data!!
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
        name_title?.text =
            ProfileFragmentAgent.userDetailResponse.firstName + " " + ProfileFragmentAgent.userDetailResponse.lastName

        var profileImg: String = ""
        val customPb: CircularProgressDrawable = Util.customProgress(requireContext())
        for (item in ProfileFragmentAgent.listUserImages) {

            if (item.uploadFileType.equals(getString(R.string.key_profile_pic))) {
                profileImg = item.fileName
                break
            }
        }

        if (!profileImg.isNullOrEmpty()) {
            Glide.with(appContext)
                .load(getString(R.string.base_url_img2) + profileImg)
                .placeholder(customPb)
                .into(profile_pic!!)
        }
    }

    var txnPriority = "1"
    private fun dialogFilter() {
        val dialog = Dialog(appContext, R.style.DialogSlideBottomChooseTeam)
        val bottomSheetDialog = layoutInflater.inflate(R.layout.dialog_transaction_filter, null)
        //login button click of custom layout
        dialog.setContentView(bottomSheetDialog)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.show()

        if (txnPriority.equals("1")) {
            bottomSheetDialog.btn_normal?.setTextColor(resources.getColor(R.color.colorGreenText))
            bottomSheetDialog.btn_attnsn?.setTextColor(resources.getColor(R.color.colorPrimary))
            tv_filter_by!!.text = "" + resources.getString(R.string.text_quarterly)
        } else if (txnPriority.equals("2")) {
            bottomSheetDialog.btn_normal?.setTextColor(resources.getColor(R.color.colorPrimary))
            bottomSheetDialog.btn_attnsn?.setTextColor(resources.getColor(R.color.colorGreenText))
            tv_filter_by!!.text = "" + resources.getString(R.string.text_received)

        }

        bottomSheetDialog.btn_normal?.setOnClickListener {
            //dismiss dialog
            txnPriority = "1"
            priorityFilter()
            dialog.dismiss()
        }

        bottomSheetDialog.btn_attnsn?.setOnClickListener {
            //dismiss dialog
            dialog.dismiss()
            txnPriority = "2"
            priorityFilter()
        }

    }

    private fun priorityFilter() {

        when (txnPriority) {
            "1" -> {
                refreshPayment!!.visibility = View.VISIBLE
                refreshPayment2!!.visibility = View.GONE
                tv_filter_by!!.text = "" + resources.getString(R.string.text_quarterly)
            }
            "2" -> {
                refreshPayment!!.visibility = View.GONE
                refreshPayment2!!.visibility = View.VISIBLE
                tv_filter_by!!.text = "" + resources.getString(R.string.text_received)
            }

        }
    }



    companion object {
        var userDetailResponse = UserDetail()
        var listUserImages = ArrayList<FetchDocumentModel>()

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
                        (linkRequestListt.size +alertsListt.size +linkRequestListForLdReq.size).toString()
                    txtLphNotify!!.text = Kotpref.notifyCount
                    //(activity as HomeActivityBroker?)!!.addBadgeNew(messageList.size.toString())
                    activity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
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
                    activity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    txtLphNotify!!.text =
                        (linkRequestListt.size +alertsListt.size +linkRequestListForLdReq.size).toString()
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
}