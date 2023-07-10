package mp.app.calonex.agent.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_ld_payment.*
import kotlinx.android.synthetic.main.fragment_ld_payment.txt_lp_notify
import mp.app.calonex.R
import mp.app.calonex.agent.activity.NotifyScreenAgent
import mp.app.calonex.agent.adapter.AgentPaymentListAdapter
import mp.app.calonex.agent.model.AgentPaymentHistory
import mp.app.calonex.common.apiCredentials.LandlordPaymentHistoryCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.adapter.CustomSpinnerAdapter
import mp.app.calonex.agent.responce.AgentPaymentHistoryListResponse
import mp.app.calonex.common.apiCredentials.NotificationCredential
import mp.app.calonex.landlord.model.AppNotifications
import mp.app.calonex.landlord.model.NotificationLinkRequestModel
import mp.app.calonex.landlord.model.NotificationLinkRequestModelNew
import mp.app.calonex.landlord.response.AppNotificationResponse

class PaymentHistoryFragmentAgent : Fragment() {
    var linkRequestListt = ArrayList<AppNotifications>()
    var linkRequestListForLdReq = ArrayList<AppNotifications>()
    var alertsListt = ArrayList<AppNotifications>()
    var messageList = ArrayList<AppNotifications>()
    var appNotifications = ArrayList<AppNotifications>()
    var mNotificationLinkRequest = NotificationLinkRequestModel()
    var mNotificationLinkRequestNew = NotificationLinkRequestModelNew()


    private lateinit var appContext: Context
    private var ldPropertyListAdapter: AgentPaymentListAdapter? = null
    private var refreshPayment: SwipeRefreshLayout? = null
    private var spinnerAddress: Spinner? = null
    private var addressList = ArrayList<String>()
    private var paymentList = ArrayList<AgentPaymentHistory>()
    private var btnTryAgain: Button? = null
    private var searchPayment: SearchView? = null
    private var layoutLphNotify: RelativeLayout? = null
    private var txtLphNotify: TextView? = null
    private var profilePic: CircleImageView? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_ld_payment, container, false)
        profilePic = rootView.findViewById(R.id.profile_pic)
        Glide.with(appContext)
            .load(Kotpref.profile_image)
            .placeholder(R.drawable.profile_default_new)
            .into(profilePic!!)
        refreshPayment = rootView.findViewById(R.id.refresh_ld_payment)
        searchPayment = rootView.findViewById(R.id.search_payment)
        spinnerAddress = rootView.findViewById(R.id.spinner_property)
        btnTryAgain = rootView.findViewById(R.id.btn_try_again)
        layoutLphNotify = rootView.findViewById(R.id.layout_lp_notify)
        txtLphNotify = rootView.findViewById(R.id.txt_lp_notify)
        val searchTextViewId: Int = searchPayment!!.context.resources
            .getIdentifier("android:id/search_src_text", null, null)
        val searchText = searchPayment!!.findViewById(searchTextViewId) as EditText
        searchText.setTextColor(Color.WHITE)
        refreshPayment?.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                appContext,
                R.color.colorPrimary
            )
        )
        refreshPayment?.setColorSchemeColors(Color.WHITE)
        refreshPayment?.setOnRefreshListener {

            getPaymentList()
            refreshPayment?.isRefreshing = false

        }

        getPaymentList()
        layoutLphNotify!!.setOnClickListener {
            Util.navigationNext(requireActivity(), NotifyScreenAgent::class.java)
        }
        btnTryAgain?.setOnClickListener {
            btnTryAgain?.visibility = View.GONE
            getPaymentList()
        }
        searchPayment!!.setOnClickListener {
            //  searchPayment!!.isIconified = false
        }

        searchPayment!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (ldPropertyListAdapter != null && !newText.isNullOrEmpty()) {
                    ldPropertyListAdapter!!.filter.filter(newText)
                } else {
                    if (ldPropertyListAdapter != null)
                        ldPropertyListAdapter!!.filter.filter("")
                }
                return true
            }

        })
        return rootView

    }

    override fun onResume() {
        super.onResume()
        layoutLphNotify!!.visibility = View.VISIBLE
        linkRequestListt.clear()
        linkRequestListForLdReq.clear()
        alertsListt.clear()
        messageList.clear()
        getNotificationList()

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
        if (NetworkConnection.isNetworkConnected(appContext)) {
            //Create retrofit Service
            pb_payment?.visibility = View.VISIBLE
            val credentials = LandlordPaymentHistoryCredential()

            credentials.userCatalogId = Kotpref.userId
            val paymentListService: ApiInterface =
                ApiClient(appContext).provideService(ApiInterface::class.java)
            val apiCall: Observable<AgentPaymentHistoryListResponse> =
                paymentListService.agentPaymentHistory(credentials)

            RxAPICallHelper().call(
                apiCall,
                object : RxAPICallback<AgentPaymentHistoryListResponse> {
                    override fun onSuccess(paymentListResponse: AgentPaymentHistoryListResponse) {
                        Log.e(
                            "onSuccessPayment",
                            paymentListResponse.responseDto?.responseDescription.toString()
                        )
                        if (paymentListResponse.data != null && !paymentListResponse.data?.isEmpty()!!) {
                            setPaymentList(paymentListResponse.data!!)
                        } else {
                            btnTryAgain?.visibility = View.VISIBLE
                        }
                        pb_payment?.visibility = View.GONE
                    }

                    override fun onFailed(t: Throwable) {
                        // show error
                        Log.e("onFailure", t.toString())
                        btnTryAgain?.visibility = View.VISIBLE
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
                        AgentDashBoardFragment.linkRequestListt.clear()
                        AgentDashBoardFragment.linkRequestListForLdReq.clear()
                        AgentDashBoardFragment.alertsListt.clear()
                        AgentDashBoardFragment.messageList.clear()
                        for (i in 0..appNotifications.size - 1) {
                            val gson = Gson()

                            if (appNotifications[i].activityType == "1" && appNotifications[i].agentRequest == "true") {
                                val objectList = gson.fromJson(
                                    appNotifications[i].notificationData,
                                    NotificationLinkRequestModel::class.java
                                )
                                mNotificationLinkRequest = objectList
                                AgentDashBoardFragment.linkRequestListt.add(appNotifications[i])
                            } else if (appNotifications[i].activityType == "1" && appNotifications[i].agentRequest == "false") {
                                val objectList = gson.fromJson(
                                    appNotifications[i].notificationData,
                                    NotificationLinkRequestModelNew::class.java
                                )
                                mNotificationLinkRequestNew = objectList
                                AgentDashBoardFragment.linkRequestListForLdReq.add(appNotifications[i])
                            } else if (appNotifications[i].activityType == "2") {
                                AgentDashBoardFragment.alertsListt.add(appNotifications[i])
                            } else if (appNotifications[i].activityType == "3") {
                                AgentDashBoardFragment.messageList.add(appNotifications[i])
                            }
                        }
                    }

                    Kotpref.notifyCount =
                        (AgentDashBoardFragment.linkRequestListt.size + AgentDashBoardFragment.alertsListt.size + AgentDashBoardFragment.linkRequestListForLdReq.size).toString()
                    txt_lp_notify!!.text = Kotpref.notifyCount
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
                    txt_lp_notify!!.text =
                        (AgentDashBoardFragment.linkRequestListt.size + AgentDashBoardFragment.alertsListt.size + AgentDashBoardFragment.linkRequestListForLdReq.size).toString()
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

    private fun setPaymentList(listResponse: ArrayList<AgentPaymentHistory>) {
        //getAddressList(listResponse)
        //paymentList = listResponse
        setList(listResponse)
        var spinnerAddressAdapter = CustomSpinnerAdapter(appContext, addressList)
        /*spinnerAddress?.adapter=spinnerAddressAdapter

        spinnerAddress?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {

                if (position> 0){
                    val value:String= addressList.get(position)
                    applyFilter(value,paymentList)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // another interface callback
            }
        }*/
    }

    /*private fun getAddressList(rootList: ArrayList<AgentPaymentHistory>) {
        addressList.add("Select Property")
        val addList = mutableListOf<String>()
        for (item in rootList) {
            addList.add(item.propertyAddress)
        }
        addressList.addAll(ArrayList<String>(addList.toSet().toList()))
    }*/

    private fun setList(rootList: ArrayList<AgentPaymentHistory>) {
        rv_ld_payments?.layoutManager = LinearLayoutManager(appContext)
        val list: MutableList<AgentPaymentHistory>? = rootList.toMutableList()

        val listPayment = ArrayList(list!!.reversed())
        ldPropertyListAdapter = AgentPaymentListAdapter(
            appContext,
            activity,
            listPayment
        )
        rv_ld_payments?.adapter = ldPropertyListAdapter
    }

    /*private fun applyFilter(address: String, rootList: ArrayList<AgentPaymentHistory>) {
        lateinit var filterList: List<AgentPaymentHistory>
        filterList = rootList.filter { it.propertyAddress == address }
        val array = arrayListOf<AgentPaymentHistory>()
        array.addAll(filterList)
        setList(array)
    }*/
}