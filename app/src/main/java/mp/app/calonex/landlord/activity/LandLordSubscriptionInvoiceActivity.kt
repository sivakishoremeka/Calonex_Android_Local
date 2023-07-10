package mp.app.calonex.landlord.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.Observable
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.LandlordPaymentHistoryCredential
import mp.app.calonex.common.apiCredentials.NotificationCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.adapter.LandLordSubscriptionInvoiceListAdapter
import mp.app.calonex.landlord.model.AppNotifications
import mp.app.calonex.landlord.model.NotificationLinkRequestModel
import mp.app.calonex.landlord.model.NotificationLinkRequestModelNew
import mp.app.calonex.landlord.response.AppNotificationResponse
import mp.app.calonex.tenant.model.TenantDashboardUnitResponse
import mp.app.calonex.tenant.model.TenantUnitData

class LandLordSubscriptionInvoiceActivity : AppCompatActivity() {

    var ProfilPic:CircleImageView?=null
    var backButton:ImageView?=null

    var propertylist:RecyclerView?=null
    var adapter :LandLordSubscriptionInvoiceListAdapter?=null

    var notificationLayout:RelativeLayout?=null
    var txtNotification:TextView?=null
    var linkRequestListt = java.util.ArrayList<AppNotifications>()
    var linkRequestListForLdReq = java.util.ArrayList<AppNotifications>()
    var alertsListt = java.util.ArrayList<AppNotifications>()
    var messageList = java.util.ArrayList<AppNotifications>()
    var appNotifications = java.util.ArrayList<AppNotifications>()
    var mNotificationLinkRequest = NotificationLinkRequestModel()
    var mNotificationLinkRequestNew = NotificationLinkRequestModelNew()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_land_lord_subscription_invoice)
      init()
    }

    override fun onResume() {
        super.onResume()
        getUnitList()
        getNotificationList()
    }


    private fun  init(){
        propertylist=findViewById(R.id.propertylist)
        backButton=findViewById(R.id.iv_back)
        ProfilPic=findViewById(R.id.profile_pic)
        notificationLayout=findViewById(R.id.layout_cx_notify)
        txtNotification=findViewById(R.id.txt_cx_notify)

        backButton?.setOnClickListener {
            finish()
        }
        Glide.with(this)
            .load(Kotpref.profile_image)
            .placeholder(R.drawable.profile_default_new)
            .into(ProfilPic!!)

        notificationLayout!!.setOnClickListener {
            Util.navigationNext(this@LandLordSubscriptionInvoiceActivity, NotifyScreen::class.java)
        }
    }

    private fun getUnitList() {


        val bookKeepingService: ApiInterface =
            ApiClient(this).provideService(ApiInterface::class.java)

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
                        this@LandLordSubscriptionInvoiceActivity,
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
                    Util.apiFailure(this@LandLordSubscriptionInvoiceActivity, throwable)
                } catch (e: Exception) {
                    Toast.makeText(
                        this@LandLordSubscriptionInvoiceActivity,
                        getString(R.string.error_something),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }

    private fun setUnitInvoiceList(listResponse: ArrayList<TenantUnitData>) {
        try {
            propertylist?.layoutManager = LinearLayoutManager(this)

            adapter = LandLordSubscriptionInvoiceListAdapter(
                this@LandLordSubscriptionInvoiceActivity,
                listResponse
            ) {
                if (it.invoiceId != null) {
                    Kotpref.invoiceId = it.invoiceId
                }

                val intent = Intent(this@LandLordSubscriptionInvoiceActivity, LandLordInvoicePaymentActivity::class.java)
                startActivity(intent)
            }

            propertylist!!.adapter = adapter
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun getNotificationList() {

        this.getWindow().setFlags(
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
                txtNotification!!.text = Kotpref.notifyCount
                Log.e("onSuccess", "Notification Count in Tenant Receipts "+Kotpref.notifyCount)


                this@LandLordSubscriptionInvoiceActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                if (Integer.parseInt(Kotpref.notifyCount) < 1) {
                    txtNotification!!.visibility = View.GONE

                } else {
                    txtNotification!!.visibility = View.VISIBLE

                }
            }

            override fun onFailed(throwable: Throwable) {
                // show error
                Log.e("onFailure", throwable.toString())

                this@LandLordSubscriptionInvoiceActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                txtNotification!!.text =
                    (linkRequestListt.size + alertsListt.size).toString()
                if (Integer.parseInt(Kotpref.notifyCount) < 1) {
                    txtNotification!!.visibility = View.GONE

                } else {
                    txtNotification!!.visibility = View.VISIBLE

                }
            }
        })
    }
}