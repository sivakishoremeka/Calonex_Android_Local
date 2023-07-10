package mp.app.calonex.tenant.fragment


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_home_agent.badge_notification
import mp.app.calonex.R
import mp.app.calonex.agent.responce.AgentBookKeepingResponse
import mp.app.calonex.common.apiCredentials.LandlordPaymentHistoryCredential
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
import mp.app.calonex.landlord.response.AppNotificationResponse
import mp.app.calonex.tenant.activity.TenantPayRentInvoiceActivity
import mp.app.calonex.tenant.adapter.TenantDashboardUnitRentListAdapter
import mp.app.calonex.tenant.model.TenantDashboardUnitResponse
import mp.app.calonex.tenant.model.TenantUnitData
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment() {

    lateinit var appContext: Context
    // var viewPager: ViewPager? = null
    //var tabLayout: TabLayout? = null
    var tv_visit_our_website: TextView? = null
    private var imgNotify: ImageView? = null
    private var profile_pic: CircleImageView? = null
    private var pbNotification: ProgressBar? = null
    private var layoutNotify: RelativeLayout? = null
    var appNotifications = ArrayList<AppNotifications>()
    var mNotificationLinkRequest = NotificationLinkRequestModel()
    var messageList = ArrayList<AppNotifications>()
    var startDate = "2020-01-01"
    var endDate = ""

    lateinit var data_0: TextView
    lateinit var data_1: TextView
    lateinit var data_2: TextView
    lateinit var data_5: TextView
    lateinit var data_3: TextView
    lateinit var data_4: TextView
    lateinit var data_6: TextView


    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_home_tenant, container, false)
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



        tv_visit_our_website = viewRoot.findViewById(R.id.tv_visit_our_website)

        imgNotify = viewRoot.findViewById(R.id.img_notify)
        pbNotification = viewRoot.findViewById(R.id.progressBar_notification)
        layoutNotify = viewRoot.findViewById(R.id.layout_notify)
        propertyList = viewRoot.findViewById(R.id.propertylist)

        data_0 = viewRoot.findViewById(R.id.data_0)
        data_1 = viewRoot.findViewById(R.id.data_1)
        data_2 = viewRoot.findViewById(R.id.data_2)
        data_5 = viewRoot.findViewById(R.id.data_5)
        data_3 = viewRoot.findViewById(R.id.data_3)
        data_4 = viewRoot.findViewById(R.id.data_4)
        data_6 = viewRoot.findViewById(R.id.data_6)

        val todayDate = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val todayString = formatter.format(todayDate)

        startDate = "2020-01-01"
        endDate = todayString
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

        setData()
    }

    private fun setData() {
        /* val adapter = TenantChartAdapter(appContext, requireFragmentManager())
         viewPager!!.offscreenPageLimit = 4
         viewPager!!.adapter = adapter
         tabLayout!!.setupWithViewPager(viewPager)*/

        layoutNotify!!.setOnClickListener {
            Util.navigationNext(requireActivity(), NotifyScreen::class.java)
        }
    }

    override fun onResume() {
        super.onResume()
        layoutNotify!!.visibility = View.VISIBLE
        linkRequestList.clear()
        alertsListTenant.clear()
        messageList.clear()
        getNotificationList()
        getUnitList()
        getBookKeepingList()

    }


    private fun getBookKeepingList() {


        val bookKeepingService: ApiInterface =
            ApiClient(appContext).provideService(ApiInterface::class.java)
        val apiCall: Observable<AgentBookKeepingResponse> =
            bookKeepingService.getBookKeepingInfo(startDate, endDate) //Test API Key

        RxAPICallHelper().call(apiCall, object : RxAPICallback<AgentBookKeepingResponse> {
            override fun onSuccess(response: AgentBookKeepingResponse) {
                if (response.responseCode == "200") {

                    val text0 =
                        "$" + 0.toBigDecimal()
                            .setScale(2, RoundingMode.UP).toDouble() + ""
                    data_0!!.text = Html.fromHtml(text0)

                    val text1 =
                        "$" + response.amount!!.rentPaid.toBigDecimal()
                            .setScale(2, RoundingMode.UP).toDouble() + ""
                    data_1!!.text = Html.fromHtml(text1)

                    val text2 =
                        "$" + response.amount!!.otherEarnings.toBigDecimal()
                            .setScale(2, RoundingMode.UP).toDouble() + ""
                    data_2!!.text = Html.fromHtml(text2)
                    val text5 =
                        "$" + response.amount!!.totalExpenses.toBigDecimal()
                            .setScale(2, RoundingMode.UP).toDouble() + ""
                    data_5!!.text = Html.fromHtml(text5)

                    val text3 =
                        "$" + response.amount!!.otherExpenses.toBigDecimal()
                            .setScale(2, RoundingMode.UP).toDouble() + ""
                    data_3!!.text = Html.fromHtml(text3)

                    val text4 =
                        "$" + response.amount!!.totalEarnings.toBigDecimal()
                            .setScale(2, RoundingMode.UP).toDouble() + ""
                    data_4!!.text = Html.fromHtml(text4)

                    if (response.amount!!.balance >= 0) {
                        val text6 =
                            "$" + response.amount!!.balance.toBigDecimal()
                                .setScale(2, RoundingMode.UP).toDouble() + ""
                        data_6!!.text = Html.fromHtml(text6)
                    } else {
                        val text6 =
                            "$" + response.amount!!.balance.toBigDecimal()
                                .setScale(2, RoundingMode.UP).toDouble() + ""
                        data_6!!.text = Html.fromHtml(text6)
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


    var propertyList: RecyclerView? = null
    var adapter: TenantDashboardUnitRentListAdapter? = null
    var list: ArrayList<TenantUnitData> = arrayListOf()
    private fun getUnitList() {


        val bookKeepingService: ApiInterface =
            ApiClient(appContext).provideService(ApiInterface::class.java)

        val credentials = LandlordPaymentHistoryCredential()

        credentials.userCatalogId = Kotpref.userId
        //credentials.userCatalogId = "4035"

        val apiCall: Observable<TenantDashboardUnitResponse> =
            bookKeepingService.getAllUnitsByTenant(credentials) //Test API Key

        RxAPICallHelper().call(apiCall, object : RxAPICallback<TenantDashboardUnitResponse> {
            override fun onSuccess(response: TenantDashboardUnitResponse) {
                if (response.responseDto.responseCode == 200) {
                    Log.e("Units", "Unit List Size==> " + response.data.size)

                    list.clear()
                    for (i in 0 until response.data.size) {
                        if (response.data[i].status == "UNPAID") {
                            list.add(response.data[i])
                        }
                    }

                    setUnitInvoiceList(list)

                } else if (response.responseDto.responseCode == 202) {
                    Toast.makeText(
                        requireContext(),
                        response.responseDto.responseDescription,
                        Toast.LENGTH_LONG
                    ).show()
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

    private fun setUnitInvoiceList(listResponse: ArrayList<TenantUnitData>) {
        try {
            propertyList?.layoutManager = LinearLayoutManager(appContext)

            adapter = TenantDashboardUnitRentListAdapter(
                appContext,
                listResponse
            ) {

                if (it.invoiceId != null) {
                    Kotpref.invoiceId = it.invoiceId
                }

                val intent = Intent(requireContext(), TenantPayRentInvoiceActivity::class.java)
                startActivity(intent)
            }

            propertyList!!.adapter = adapter
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
                badge_notification!!.text = Kotpref.notifyCount

                Log.e("onSuccess", "Notification Count in Home "+Kotpref.notifyCount)

                //todo change class name
                //(activity as HomeActivityAgent?)!!.addBadgeNew(messageList.size.toString())
                pbNotification!!.visibility = View.GONE
                activity!!.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                if (Integer.parseInt(Kotpref.notifyCount) < 1) {
                    badge_notification!!.visibility = View.GONE

                } else {
                    badge_notification!!.visibility = View.VISIBLE

                }
            }

            override fun onFailed(throwable: Throwable) {
                // show error
                Log.e("onFailure", throwable.toString())
                pbNotification!!.visibility = View.GONE
                activity!!.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                badge_notification!!.text =
                    (linkRequestList.size + alertsListTenant.size).toString()
                if (Integer.parseInt(Kotpref.notifyCount) < 1) {
                    badge_notification!!.visibility = View.GONE

                } else {
                    badge_notification!!.visibility = View.VISIBLE

                }
            }
        })
    }

    companion object {
        var linkRequestList = ArrayList<AppNotifications>()
        var alertsListTenant = ArrayList<AppNotifications>()
    }
}