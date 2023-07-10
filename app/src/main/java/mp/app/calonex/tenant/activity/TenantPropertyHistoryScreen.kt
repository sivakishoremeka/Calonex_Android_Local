package mp.app.calonex.tenant.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_tenant_property_history.*
import kotlinx.android.synthetic.main.activity_tenant_property_history.txt_lp_notify
import kotlinx.android.synthetic.main.fragment_cx_report_tenant.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.NotificationCredential
import mp.app.calonex.common.apiCredentials.TenantFindApiCredentials
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.CxBaseActivity2
import mp.app.calonex.landlord.activity.NotifyScreen
import mp.app.calonex.landlord.model.AppNotifications
import mp.app.calonex.landlord.model.NotificationLinkRequestModel
import mp.app.calonex.landlord.model.NotificationLinkRequestModelNew
import mp.app.calonex.landlord.response.AppNotificationResponse
import mp.app.calonex.service.FeaturesService
import mp.app.calonex.tenant.adapter.TenantApplicationHistoryAdapter
import mp.app.calonex.tenant.adapter.TenantLeaseRequestHistoryAdapter
import mp.app.calonex.tenant.model.LeaseTenantInfo
import mp.app.calonex.tenant.response.FindApiResponse

class TenantPropertyHistoryScreen : CxBaseActivity2() {
    val BackgroundIntentServiceAction = "android.intent.action.CUSTOME_ACTION_1"

    private var rvLeaseReq: RecyclerView? = null
    private var pbLr: ProgressBar? = null
    private var srLeaseReq: SwipeRefreshLayout? = null
    private var tenantLeaseRequestAdapter: TenantLeaseRequestHistoryAdapter? = null
    private var propertyId: String? = ""
    private var isHistory: Boolean? = false
    private var isTenant: Boolean? = false
    private var unitId: String? = ""
    private var txtLeaseTryAgain: TextView? = null
    private var iv_back: ImageView? = null
    private var layoutLeaseEmpty: RelativeLayout? = null

    private var leaseListAdapter: TenantApplicationHistoryAdapter? = null
    private var profile_pic: CircleImageView? = null
    private var layoutLphNotify: RelativeLayout? = null
    private var txtLphNotify: TextView? = null
    var linkRequestListt = java.util.ArrayList<AppNotifications>()
    var linkRequestListForLdReq = java.util.ArrayList<AppNotifications>()
    var alertsListt = java.util.ArrayList<AppNotifications>()
    var messageList = java.util.ArrayList<AppNotifications>()
    var appNotifications = java.util.ArrayList<AppNotifications>()
    var mNotificationLinkRequest = NotificationLinkRequestModel()
    var mNotificationLinkRequestNew = NotificationLinkRequestModelNew()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tenant_property_history)
        initComponent()
        actionComponent()
        startHandler()
    }

    private fun initComponent() {
        layoutLphNotify = findViewById(R.id.layout_lp_notify)
        layoutLphNotify!!.setOnClickListener {
            Util.navigationNext(this@TenantPropertyHistoryScreen, NotifyScreen::class.java)
        }

        txtLphNotify = findViewById(R.id.txt_lp_notify)
        profile_pic=findViewById(R.id.profile_pic)
        Glide.with(this)
            .load(Kotpref.profile_image)
            .placeholder(R.drawable.profile_default_new)
            .into(profile_pic!!)

        rvLeaseReq = findViewById(R.id.rv_lease)
        srLeaseReq = findViewById(R.id.sr_lease_req)
        iv_back = findViewById(R.id.iv_back)
        pbLr = findViewById(R.id.pb_lr)
        txtLeaseTryAgain = findViewById(R.id.txt_lease_try_again)
        layoutLeaseEmpty = findViewById(R.id.layout_lease_empty)
        propertyId = intent.getStringExtra(getString(R.string.intent_property_id))
        isHistory = intent.getBooleanExtra(getString(R.string.is_lease_history), false)
        isTenant = intent.getBooleanExtra("isTenant", false)

        if (isHistory!!) {
            unitId = intent.getStringExtra(getString(R.string.intent_unit_id))
        }


        //** Set the colors of the Pull To Refresh View
        srLeaseReq?.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                applicationContext,
                R.color.colorPrimary
            )
        )
        srLeaseReq?.setColorSchemeColors(Color.WHITE)


        val dividerItemDecoration =
            DividerItemDecoration(rv_lease_list!!.context, DividerItemDecoration.VERTICAL)
        dividerItemDecoration.setDrawable(
            ContextCompat.getDrawable(this, R.drawable.item_list_divider)!!
        )
        iv_back!!.setOnClickListener {
            onBackPressed()
        }

        rv_lease_list!!.addItemDecoration(dividerItemDecoration)

        leaseSelected()
        applicationUnSelected()


        lease_selector.setOnClickListener {
            selector(0)
            rl_lease_history.visibility = View.VISIBLE
            rl_property_history.visibility = View.GONE
        }
        application_selector.setOnClickListener {
            selector(1)
            rl_lease_history.visibility = View.GONE
            rl_property_history.visibility = View.VISIBLE
        }

    }

    override fun onResume() {
        super.onResume()
        getPropertyLeaseList()
        getPropertyRequestList()
        layoutLphNotify!!.visibility = View.VISIBLE
        linkRequestListt.clear()
        alertsListt.clear()
        messageList.clear()
        getNotificationList()
    }
    private fun getNotificationList() {
        pb_lr!!.visibility = View.VISIBLE
        this@TenantPropertyHistoryScreen.getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        val credentials = NotificationCredential()

        credentials.userCatalog = Kotpref.userId
        val notifyListService: ApiInterface =
            ApiClient(this).provideService(ApiInterface::class.java)
        val apiCall: Observable<AppNotificationResponse> =
            notifyListService.getAppNotifications(credentials) //Test API Key 3600248I

        RxAPICallHelper().call(apiCall, object : RxAPICallback<AppNotificationResponse> {
            override fun onSuccess(response: AppNotificationResponse) {
                Log.e("onSuccess", response.responseCode.toString())

                if (!response.data?.isEmpty()!!) {
                    appNotifications = response.data!!
                    linkRequestListt.clear()
                    alertsListt.clear()
                    messageList.clear()
                    for (i in 0..appNotifications.size - 1) {
                        val gson = Gson()

                        if (appNotifications[i].activityType == "1") {
                            val objectList = gson.fromJson(
                                appNotifications[i].notificationData,
                                NotificationLinkRequestModel::class.java
                            )
                            mNotificationLinkRequest = objectList
                            linkRequestListt.add(appNotifications[i])
                        } else if (appNotifications[i].activityType == "2") {
                            alertsListt.add(appNotifications[i])
                        } else if (appNotifications[i].activityType == "3") {
                            messageList.add(appNotifications[i])
                        }
                    }
                }

                Kotpref.notifyCount = (linkRequestListt.size + alertsListt.size).toString()
                txt_lp_notify!!.text = Kotpref.notifyCount
                //todo change class name
                //(activity as HomeActivityAgent?)!!.addBadgeNew(messageList.size.toString())
                pb_lr!!.visibility = View.GONE
                this@TenantPropertyHistoryScreen!!.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                if (Integer.parseInt(Kotpref.notifyCount) < 1) {
                    txt_lp_notify!!.visibility = View.GONE

                } else {
                    txt_lp_notify!!.visibility = View.VISIBLE

                }
            }

            override fun onFailed(throwable: Throwable) {
                // show error
                Log.e("onFailure", throwable.toString())
                pb_lr!!.visibility = View.GONE
                this@TenantPropertyHistoryScreen.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                txt_lp_notify!!.text =
                    (linkRequestListt.size + alertsListt.size).toString()
                if (Integer.parseInt(Kotpref.notifyCount) < 1) {
                    txt_lp_notify!!.visibility = View.GONE

                } else {
                    txt_lp_notify!!.visibility = View.VISIBLE

                }
            }
        })
    }


    private fun actionComponent() {
        srLeaseReq?.setOnRefreshListener {

            getPropertyLeaseList()
            srLeaseReq?.isRefreshing = false

        }
        txtLeaseTryAgain!!.setOnClickListener {
            layoutLeaseEmpty!!.visibility = View.GONE
            srLeaseReq!!.visibility = View.GONE
            getPropertyLeaseList()

        }

        sr_ld_lease_req?.setOnRefreshListener {

            getPropertyLeaseList()
            sr_ld_lease_req?.isRefreshing = false

        }
        /* txtLeaseTryAgain!!.setOnClickListener {
             no_lease_tenant!!.visibility = View.GONE
             sr_ld_lease_req!!.visibility = View.GONE
             getPropertyLeaseList()

         }
 */
    }

    private fun getPropertyLeaseList() {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            //Create retrofit Service
            pbLr!!.visibility = View.VISIBLE
            /* val credential = FindLeaseCredentials()
             credential.userId = Kotpref.userId
             credential.userRole = Kotpref.userRole
             credential.propertyId = propertyId
             if (isHistory!!) {
                 credential.leaseHistory = isHistory!!
                 if (!isTenant!!) {
                     credential.unitId = unitId
                 }
             }

             */


            val credential = TenantFindApiCredentials()
            credential.userId = Kotpref.userId
            credential.userRole = "CX-Tenant"

            val findApi: ApiInterface = ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<FindApiResponse> =
                findApi.find(credential)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", it.responseDto?.responseDescription.toString())


                    pbLr!!.visibility = View.GONE

                    if (it.data != null && it.data!!.isNotEmpty()) {
                        propertyListData.clear()

                        for (i in 0 until it.data!!.size) {
                            if (it.data!!.get(i).leaseStatus.equals("19", true)) {
                                propertyListData.add(it.data!!.get(i))
                            }
                        }
                        setLeasePropertyList(propertyListData)

                        if (FeaturesService.allModel == null) {
                            val intent = Intent(this@TenantPropertyHistoryScreen, FeaturesService::class.java).apply {
                                action = BackgroundIntentServiceAction
                            }
                            startService(intent)
                        }

                        srLeaseReq!!.visibility = View.VISIBLE
                        layoutLeaseEmpty!!.visibility = View.GONE
                    } else {
                        layoutLeaseEmpty!!.visibility = View.VISIBLE
                        srLeaseReq!!.visibility = View.GONE
                    }

                },
                    { e ->
                        Log.e("onFailure", e.toString())
                        pbLr!!.visibility = View.GONE
                        e.message?.let {
                            Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                        }
                    })
        } else {
            Toast.makeText(
                applicationContext,
                getString(R.string.error_network),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setLeasePropertyList(listResponse: ArrayList<LeaseTenantInfo>) {
        rvLeaseReq?.layoutManager = LinearLayoutManager(applicationContext)

        tenantLeaseRequestAdapter =
            TenantLeaseRequestHistoryAdapter(
                this,
                pbLr!!,
                this@TenantPropertyHistoryScreen,
                listResponse
            ) {

            }
        rvLeaseReq?.adapter = tenantLeaseRequestAdapter
    }

    var leaseListData: ArrayList<LeaseTenantInfo> = arrayListOf()
    var propertyListData: ArrayList<LeaseTenantInfo> = arrayListOf()

    @SuppressLint("CheckResult")
    private fun getPropertyRequestList() {

        val credential = TenantFindApiCredentials()
        credential.userId = Kotpref.userId
        credential.userRole = "CX-Tenant"

        val findApi: ApiInterface = ApiClient(this).provideService(ApiInterface::class.java)
        val apiCall: Observable<FindApiResponse> =
            findApi.find(credential)

        apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
            Log.e("onSuccess", "Lease List Data" + Gson().toJson(it.data))
            if (it.data != null && it.data!!.isNotEmpty()) {
                leaseListData.clear()

                for (i in 0 until it.data!!.size) {
                    if (!it.data!!.get(i).leaseStatus.equals("19", true)) {
                        leaseListData.add(it.data!!.get(i))
                    }
                }

                setPropertyReqList(leaseListData)
                leaseListApiResponse = leaseListData

                sr_ld_lease_req.visibility = View.VISIBLE
                no_lease_tenant.visibility = View.GONE
            } else {
                no_lease_tenant.visibility = View.VISIBLE
                sr_ld_lease_req.visibility = View.GONE
            }
            pbLr!!.visibility = View.GONE


        },
            { e ->
                Log.e("onFailure", e.toString())
                pbLr!!.visibility = View.GONE
                e.message?.let {
                    Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun setPropertyReqList(listResponse: ArrayList<LeaseTenantInfo>) {
        rv_lease_list?.layoutManager = LinearLayoutManager(this)
        leaseListAdapter = TenantApplicationHistoryAdapter(
            this,
            pbLr!!,
            this@TenantPropertyHistoryScreen,
            listResponse as ArrayList<LeaseTenantInfo>
        ) {

        }
        rv_lease_list?.adapter = leaseListAdapter
    }

    companion object {
        var leaseListApiResponse = ArrayList<LeaseTenantInfo>()
        var leaseItem = LeaseTenantInfo()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Util.navigationBack(this)
    }

    private fun selector(status: Int) {

        if (status == 0) {
            leaseSelected()
            applicationUnSelected()
        } else {
            applicationSelected()
            leaseUnSelected()
        }

    }

    private fun leaseSelected() {
        lease_selector?.setBackgroundResource(R.drawable.bg_card_white)
        lease_text!!.setTextColor((Color.parseColor("#0874EA")))

    }

    private fun leaseUnSelected() {
        lease_selector?.setBackgroundResource(R.drawable.white_border)
        lease_text!!.setTextColor((Color.parseColor("#FFFFFF")))
    }

    private fun applicationSelected() {
        application_selector?.setBackgroundResource(R.drawable.bg_card_white)
        application_text!!.setTextColor((Color.parseColor("#0874EA")))
    }

    private fun applicationUnSelected() {
        application_selector?.setBackgroundResource(R.drawable.white_border)
        application_text!!.setTextColor((Color.parseColor("#FFFFFF")))
    }
}