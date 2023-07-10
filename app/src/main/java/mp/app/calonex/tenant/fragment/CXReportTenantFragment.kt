package mp.app.calonex.tenant.fragment


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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.anastr.speedviewlib.SpeedView
import com.github.anastr.speedviewlib.components.Section
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_cx_report_tenant.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.GeneralReportDetailsByID
import mp.app.calonex.common.apiCredentials.NotificationCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.NotifyScreen
import mp.app.calonex.landlord.model.AppNotifications
import mp.app.calonex.landlord.model.NotificationLinkRequestModel
import mp.app.calonex.landlord.model.NotificationLinkRequestModelNew
import mp.app.calonex.landlord.response.AppNotificationResponse
import mp.app.calonex.tenant.activity.TenantAccountInfoActivity
import mp.app.calonex.tenant.adapter.TenantAccountInfoAdapter
import mp.app.calonex.tenant.model.CxScoreLeaseDetail
import mp.app.calonex.tenant.model.GeneralReportResponse
import kotlin.collections.ArrayList

class CXReportTenantFragment : Fragment() {
    lateinit var appContext: Context
    // var viewPager: ViewPager? = null
    //var tabLayout: TabLayout? = nullt
    private var profile_pic: CircleImageView? = null
    private var pbNotification: ProgressBar? = null
    private var layoutLphNotify: RelativeLayout? = null
    private var txtLphNotify: TextView? = null
    var linkRequestListt = java.util.ArrayList<AppNotifications>()
    var linkRequestListForLdReq = java.util.ArrayList<AppNotifications>()
    var alertsListt = java.util.ArrayList<AppNotifications>()
    var messageList = java.util.ArrayList<AppNotifications>()
    var appNotifications = java.util.ArrayList<AppNotifications>()
    var mNotificationLinkRequest = NotificationLinkRequestModel()
    var mNotificationLinkRequestNew = NotificationLinkRequestModelNew()
    var startDate = "2020-01-01"
    var endDate = ""
    lateinit var sv_calonex_score: SpeedView
    lateinit var tv_report_prepared_for: TextView
    lateinit var tv_report_number: TextView
    lateinit var tv_date_report_prepared: TextView
    lateinit var tv_status: TextView
    lateinit var tv_present_status: TextView
    lateinit var tv_csn_number: TextView
    lateinit var tv_tenant_name: TextView
    lateinit var tv_tenant_address: TextView
    lateinit var tv_tenant_csn: TextView
    lateinit var tv_email_address: TextView
    lateinit var tv_tenant_employer: TextView
    lateinit var tv_tenant_employer_address: TextView
    lateinit var tv_tenant_year_employment: TextView
    lateinit var tv_late_payment_count: TextView
    lateinit var tv_paid_on_time: TextView
    lateinit var tv_address_change_within_a_year: TextView
    lateinit var tv_job_changes: TextView
    lateinit var tv_judgment: TextView
    lateinit var tv_evictions: TextView
    lateinit var tv_deposits_paid: TextView
    lateinit var tv_deposits_refunded: TextView
    lateinit var tv_collections: TextView
    lateinit var rv_account_info: RecyclerView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_cx_report_tenant, container, false)
        initComponents(rootView)
        return rootView
    }

    private fun initComponents(viewRoot: View) {
        //viewPager = viewRoot.findViewById<View>(R.id.view_pager) as ViewPager
        //tabLayout = viewRoot.findViewById<View>(R.id.slidingTabs) as TabLayout
        profile_pic=viewRoot.findViewById(R.id.profile_pic)
        Glide.with(this)
            .load(Kotpref.profile_image)
            .placeholder(R.drawable.profile_default_new)
            .into(profile_pic!!)

        pbNotification = viewRoot.findViewById(R.id.progressBar_notification)
        layoutLphNotify = viewRoot.findViewById(R.id.layout_lp_notify)
        txtLphNotify = viewRoot.findViewById(R.id.txt_lp_notify)
        sv_calonex_score = viewRoot.findViewById(R.id.sv_calonex_score)
        tv_report_prepared_for = viewRoot.findViewById(R.id.tv_report_prepared_for)
        tv_report_number = viewRoot.findViewById(R.id.tv_report_number)
        tv_date_report_prepared = viewRoot.findViewById(R.id.tv_date_report_prepared)
        tv_status = viewRoot.findViewById(R.id.tv_status)
        tv_present_status = viewRoot.findViewById(R.id.tv_present_status)
        tv_csn_number = viewRoot.findViewById(R.id.tv_csn_number)
        tv_tenant_name = viewRoot.findViewById(R.id.tv_tenant_name)
        tv_tenant_address = viewRoot.findViewById(R.id.tv_tenant_address)
        tv_tenant_csn = viewRoot.findViewById(R.id.tv_tenant_csn)
        tv_email_address = viewRoot.findViewById(R.id.tv_email_address)
        tv_tenant_employer = viewRoot.findViewById(R.id.tv_tenant_employer)
        tv_tenant_employer_address = viewRoot.findViewById(R.id.tv_tenant_employer_address)
        tv_tenant_year_employment = viewRoot.findViewById(R.id.tv_tenant_year_employment)
        tv_late_payment_count = viewRoot.findViewById(R.id.tv_late_payment_count)
        tv_paid_on_time = viewRoot.findViewById(R.id.tv_paid_on_time)
        tv_address_change_within_a_year =
            viewRoot.findViewById(R.id.tv_address_change_within_a_year)
        tv_job_changes = viewRoot.findViewById(R.id.tv_job_changes)
        tv_judgment = viewRoot.findViewById(R.id.tv_judgment)
        tv_evictions = viewRoot.findViewById(R.id.tv_evictions)
        tv_deposits_paid = viewRoot.findViewById(R.id.tv_deposits_paid)
        tv_deposits_refunded = viewRoot.findViewById(R.id.tv_deposits_refunded)
        tv_collections = viewRoot.findViewById(R.id.tv_collections)
        rv_account_info = viewRoot.findViewById(R.id.rv_account_info)

        setData()
    }

    private fun setData() {
        /* val adapter = TenantChartAdapter(appContext, requireFragmentManager())
         viewPager!!.offscreenPageLimit = 4
         viewPager!!.adapter = adapter
         tabLayout!!.setupWithViewPager(viewPager)*/

        layoutLphNotify!!.setOnClickListener {
            Util.navigationNext(requireActivity(), NotifyScreen::class.java)
        }

        // sv_calonex_score.sections
        sv_calonex_score.clearSections()
        //sv_calonex_score.minSpeed = 160f
        //sv_calonex_score.maxSpeed = 333f

        sv_calonex_score.addSections(
            Section(
                0f,
                .32f,
                appContext.resources.getColor(R.color.colorReportRed),
                sv_calonex_score.dpTOpx(30f)
            ),
            Section(
                .32f,
                .65f,
                appContext.resources.getColor(R.color.colorReportYellow),
                sv_calonex_score.dpTOpx(30f)
            ),
            Section(
                .65f,
                .80f,
                appContext.resources.getColor(R.color.colorReportBlue),
                sv_calonex_score.dpTOpx(30f)
            ),
            Section(
                .80f,
                1.0f,
                appContext.resources.getColor(R.color.colorReportGreen),
                sv_calonex_score.dpTOpx(30f)
            )
        )


    }


    override fun onResume() {
        super.onResume()
        layoutLphNotify!!.visibility = View.VISIBLE
        linkRequestList.clear()
        alertsListTenant.clear()
        messageList.clear()
        getNotificationList()
        //getUnitList()
        getGeneralReport()

    }

    var adapter: TenantAccountInfoAdapter? = null
    var list: ArrayList<CxScoreLeaseDetail>? = null

    private fun getGeneralReport() {

        val credentials = GeneralReportDetailsByID()

        credentials.userCatalogId = Kotpref.userId

        val bookKeepingService: ApiInterface =
            ApiClient(appContext).provideService(ApiInterface::class.java)
        val apiCall: Observable<GeneralReportResponse> =
            bookKeepingService.generateReport(credentials) //Test API Key

        RxAPICallHelper().call(apiCall, object : RxAPICallback<GeneralReportResponse> {
            override fun onSuccess(response: GeneralReportResponse) {
                if (response.responseDto.responseCode == 200) {

                    try {
                        sv_calonex_score.setSpeedAt(response.data.cx_Score.toFloat())

                        tv_report_prepared_for.text = "" + response.data.tenantName
                        tv_report_number.text = "" + response.data.reportNo
                        tv_date_report_prepared.text = "" + response.data.reportDate
                        tv_status.text = "" + response.data.tenantStatus
                        tv_present_status.text = "" + response.data.presentStatus
                        tv_csn_number.text = "" + response.data.csn_No
                        tv_tenant_name.text = "" + response.data.tenantName
                        tv_tenant_address.text = "" + response.data.tenantAddress
                        tv_tenant_csn.text = "" + response.data.csn_No
                        tv_email_address.text = "" + response.data.tenantEmail
                        tv_tenant_employer.text = "" + response.data.employer
                        tv_tenant_employer_address.text = "" + response.data.employerAddress
                        tv_tenant_year_employment.text = "" + response.data.yearsEmployed
                        tv_late_payment_count.text = "" + response.data.totalLatePaymentCount
                        tv_paid_on_time.text = "" + response.data.totalPaidOnTimeCount
                        tv_address_change_within_a_year.text =
                            "" + response.data.totalAddressChangeInYear
                        tv_job_changes.text = "" + response.data.noOfJobChangeInYear
                        tv_judgment.text = "" + response.data.totalJudgeMentCount
                        tv_evictions.text = "" + response.data.totalEvictionCount
                        tv_deposits_paid.text = "" + response.data.totalDepositPaidCount
                        tv_deposits_refunded.text = "" + response.data.totalDepositRefunded
                        tv_collections.text = "$" + response.data.totalAmountCollected

                        setAccountInfo(response.data.cxScoreLeaseDetails)


                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }

            override fun onFailed(throwable: Throwable) {
                // show error
                Log.e("onFailure", throwable.toString())
                //
                try {
                    Util.apiFailure(appContext, throwable)
                } catch (e: Exception) {
                    Toast.makeText(
                        appContext,
                        getString(R.string.error_something),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    private fun setAccountInfo(cxScoreLeaseDetails: ArrayList<CxScoreLeaseDetail>) {
        try {
            rv_account_info?.layoutManager = LinearLayoutManager(appContext)

            adapter = TenantAccountInfoAdapter(
                appContext,
                cxScoreLeaseDetails
            ) {

                val intent = Intent(requireContext(), TenantAccountInfoActivity::class.java)
                intent.putExtra("account_details", Gson().toJson(it))
                startActivity(intent)
            }

            rv_account_info!!.adapter = adapter
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun getNotificationList() {
        pbNotification!!.visibility = View.VISIBLE
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

                if (!response.data?.isEmpty()!!) {
                    appNotifications = response.data!!
                    linkRequestList.clear()
                    alertsListTenant.clear()
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
                        } else if (appNotifications[i].activityType == "2") {
                            alertsListTenant.add(appNotifications[i])
                        } else if (appNotifications[i].activityType == "3") {
                            messageList.add(appNotifications[i])
                        }
                    }
                }

                Kotpref.notifyCount = (linkRequestList.size + alertsListTenant.size).toString()
                txt_lp_notify!!.text = Kotpref.notifyCount
                //todo change class name
                //(activity as HomeActivityAgent?)!!.addBadgeNew(messageList.size.toString())
                pbNotification!!.visibility = View.GONE
                activity!!.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                if (Integer.parseInt(Kotpref.notifyCount) < 1) {
                    txt_lp_notify!!.visibility = View.GONE

                } else {
                    txt_lp_notify!!.visibility = View.VISIBLE

                }
            }

            override fun onFailed(throwable: Throwable) {
                // show error
                Log.e("onFailure", throwable.toString())
                pbNotification!!.visibility = View.GONE
                activity!!.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                txt_lp_notify!!.text =
                    (linkRequestList.size + alertsListTenant.size).toString()
                if (Integer.parseInt(Kotpref.notifyCount) < 1) {
                    txt_lp_notify!!.visibility = View.GONE

                } else {
                    txt_lp_notify!!.visibility = View.VISIBLE

                }
            }
        })
    }

    companion object {
        var linkRequestList = ArrayList<AppNotifications>()
        var alertsListTenant = ArrayList<AppNotifications>()
    }
}