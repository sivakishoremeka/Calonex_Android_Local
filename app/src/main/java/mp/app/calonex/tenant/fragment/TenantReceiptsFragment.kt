package mp.app.calonex.tenant.fragment


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.Observable

import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.GeneralDetailsByUserID
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
import mp.app.calonex.landlord.model.NotificationLinkRequestModelNew
import mp.app.calonex.landlord.response.AppNotificationResponse
import mp.app.calonex.tenant.activity.TenantPayRentInvoiceActivity
import mp.app.calonex.tenant.adapter.TenantPaymentInvoiceListAdapter
import mp.app.calonex.tenant.model.TenantDashboardUnitResponse
import mp.app.calonex.tenant.model.TenantUnitData
import mp.app.calonex.tenant.response.invoiceStats.TenantInvoiceStatDao
import java.math.RoundingMode


class TenantReceiptsFragment : Fragment() {
    private var layoutLphNotify: RelativeLayout? = null
    private var txtLphNotify: TextView? = null

    private var tv_total_rent_amount: TextView? = null
    private var tv_total_security_amount: TextView? = null
    private var tv_monthly_rent_amount: TextView? = null
    private var tv_total_security_deposite: TextView? = null


    private var profile_pic: CircleImageView? = null
    var linkRequestListt = java.util.ArrayList<AppNotifications>()
    var linkRequestListForLdReq = java.util.ArrayList<AppNotifications>()
    var alertsListt = java.util.ArrayList<AppNotifications>()
    var messageList = java.util.ArrayList<AppNotifications>()
    var appNotifications = java.util.ArrayList<AppNotifications>()
    var mNotificationLinkRequest = NotificationLinkRequestModel()
    var mNotificationLinkRequestNew = NotificationLinkRequestModelNew()

    lateinit var appContext: Context

    private var headerBack: ImageView? = null
    var tabLayoutReceipts: TabLayout? = null
    var vpNotify: ViewPager? = null

    var propertyList: RecyclerView? = null
    var adapter: TenantPaymentInvoiceListAdapter? = null
    var list: ArrayList<TenantUnitData>? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_tenant_receipts, container, false)
        tabLayoutReceipts = rootView.findViewById(R.id.tabLayout_receipts)
        vpNotify = rootView.findViewById(R.id.vp_receipts)
        propertyList = rootView.findViewById(R.id.propertylist)
        layoutLphNotify = rootView.findViewById(R.id.layout_lp_notify)
        txtLphNotify = rootView.findViewById(R.id.txt_lp_notify)
        profile_pic = rootView.findViewById(R.id.profile_pic)


        tv_total_rent_amount = rootView.findViewById(R.id.tv_total_rent_amount)
        tv_total_security_amount = rootView.findViewById(R.id.tv_total_security_amount)
        tv_monthly_rent_amount = rootView.findViewById(R.id.tv_monthly_rent_amount)
        tv_total_security_deposite = rootView.findViewById(R.id.tv_total_security_deposite)
        setViewPager()

        return rootView
    }

    private fun setViewPager() {

        Glide.with(this)
            .load(Kotpref.profile_image)
            .placeholder(R.drawable.profile_default_new)
            .into(profile_pic!!)

        layoutLphNotify!!.setOnClickListener {
            Util.navigationNext(requireActivity(), NotifyScreen::class.java)
        }

    }

    override fun onResume() {
        super.onResume()
        getUnitList()
        getNotificationList()
        getInvoiceStats()
    }

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
                    setUnitInvoiceList(response.data)

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


    private fun getInvoiceStats() {


        val bookKeepingService: ApiInterface =
            ApiClient(appContext).provideService(ApiInterface::class.java)

        val credentials = GeneralDetailsByUserID()

        credentials.userId = Kotpref.userId
        //credentials.userCatalogId = "4035"

        val apiCall: Observable<TenantInvoiceStatDao> =
            bookKeepingService.getInvoiceStats(credentials) //Test API Key

        RxAPICallHelper().call(apiCall, object : RxAPICallback<TenantInvoiceStatDao> {
            override fun onSuccess(response: TenantInvoiceStatDao) {
                if (response.responseDto.responseCode == 200) {
                    Log.e("Units", "response.data " + Gson().toJson(response.data))

                    tv_total_rent_amount!!.text =
                        "Total Rent Amount : $" + response.data.totalRentAmountPaid.toBigDecimal().setScale(2, RoundingMode.UP)
                            .toDouble()
                    tv_total_security_amount!!.text =
                        "Total Security Amount : $" + response.data.totalSecurityAmountPaid.toBigDecimal().setScale(2, RoundingMode.UP)
                            .toDouble()
                    tv_monthly_rent_amount!!.text =
                        "Monthly  Rent Amount : $" + response.data.activeMonthRentAmount.toBigDecimal().setScale(2, RoundingMode.UP)
                            .toDouble()
                    tv_total_security_deposite!!.text =
                        "Total Security Deposit : $" + response.data.activeMonthSecurityDepositAmount.toBigDecimal().setScale(2, RoundingMode.UP)
                            .toDouble()

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

            adapter = TenantPaymentInvoiceListAdapter(
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
                txtLphNotify!!.text = Kotpref.notifyCount
                Log.e("onSuccess", "Notification Count in Tenant Receipts " + Kotpref.notifyCount)


                activity!!.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                if (Integer.parseInt(Kotpref.notifyCount) < 1) {
                    txtLphNotify!!.visibility = View.GONE

                } else {
                    txtLphNotify!!.visibility = View.VISIBLE

                }
            }

            override fun onFailed(throwable: Throwable) {
                // show error
                Log.e("onFailure", throwable.toString())

                activity!!.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                txtLphNotify!!.text =
                    (linkRequestListt.size + alertsListt.size).toString()
                if (Integer.parseInt(Kotpref.notifyCount) < 1) {
                    txtLphNotify!!.visibility = View.GONE

                } else {
                    txtLphNotify!!.visibility = View.VISIBLE

                }
            }
        })
    }


}