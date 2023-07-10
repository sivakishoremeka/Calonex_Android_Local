package mp.app.calonex.agent.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_agent_on_boarded_property.*
import mp.app.calonex.R
import mp.app.calonex.agent.adapter.AgentOnBoardedPropertyAdapter

import mp.app.calonex.agent.model.PropertyAgent
import mp.app.calonex.agent.responce.OnboardPropertyListResponseAgent


import mp.app.calonex.common.apiCredentials.AgentBrokerOnboardPropertyCredential
import mp.app.calonex.common.apiCredentials.NotificationCredential

import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.AccountLinkDetailScreen
import mp.app.calonex.landlord.model.AppNotifications
import mp.app.calonex.landlord.model.NotificationLinkRequestModel
import mp.app.calonex.landlord.model.NotificationLinkRequestModelNew
import mp.app.calonex.landlord.response.AppNotificationResponse


class AgentOnBoardedPropertyActivity : AppCompatActivity() {

    private lateinit var appContext: Context
    private var ldPropertyListAdapter: AgentOnBoardedPropertyAdapter? = null
    private var propertyListView: RecyclerView? = null
    private var refreshProperties: SwipeRefreshLayout? = null
    private var layoutAddProperty: LinearLayout? = null
    private var btnTryAgain: Button? = null
    private var layoutLpNotify: RelativeLayout? = null
    private var txtLpNotify: TextView? = null
    private var profile_pic: CircleImageView? = null
    private var progressBar: ProgressBar? = null
    var linkRequestListt = ArrayList<AppNotifications>()
    var linkRequestListForLdReq = ArrayList<AppNotifications>()
    var alertsListt = ArrayList<AppNotifications>()
    var messageList = ArrayList<AppNotifications>()
    var appNotifications = ArrayList<AppNotifications>()
    var mNotificationLinkRequest = NotificationLinkRequestModel()
    var mNotificationLinkRequestNew = NotificationLinkRequestModelNew()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agent_on_boarded_property)
        init()
    }

    private fun init() {
        appContext = this
        profile_pic=findViewById(R.id.profile_pic_onboarded_agent)

        Glide.with(this)
            .load(Kotpref.profile_image)
            .placeholder(R.drawable.profile_default_new)
            .into(profile_pic!!)
        progressBar = findViewById(R.id.pb_property_on_boarded_agent);
        propertyListView = findViewById(R.id.property_list_on_boarded_agent)
        refreshProperties = findViewById(R.id.refresh_property_list_on_boarded_agent)
        layoutAddProperty = findViewById(R.id.layout_add_property_on_boarded_agent)
        btnTryAgain = findViewById(R.id.btn_try_again_on_boarded_agent)
        layoutLpNotify = findViewById(R.id.layout_lp_notify_on_boarded_agent)
        txtLpNotify = findViewById(R.id.txt_lp_notify_on_boarded_agent)

        iv_back_onboarded_agent.setOnClickListener {
            finish()
        }
        try {
            getPropertyList()
            getNotificationList()

        } catch (E: Exception) {

            Log.e("something went wrong", E.toString());
        }

        layoutLpNotify!!.setOnClickListener {
            var i=Intent(this@AgentOnBoardedPropertyActivity,NotifyScreenAgent::class.java);
            startActivity(i)

        }

        layoutAddProperty!!.setOnClickListener {
            //Util.navigationNext(requireActivity(), AddNewPropertyFirstScreen::class.java)

            if(Kotpref.userRole.contains("Agent")){
                Util.navigationNext(this, LinkPropertyAgent::class.java)
            }else {
                if (!Kotpref.bankAdded) {
                    bankAddDialog()
                } else if (!Kotpref.bankAccountVerified) {
                    bankVerifyDialog()
                } else {
                    Util.navigationNext(this, LinkPropertyAgent::class.java)
                }
            }
            //Util.navigationNext(this, LinkPropertyAgent::class.java)


        /* if(Kotpref.bankAccountVerified) {


            }else{
                *//*Util.alertOkIntentMessage(
                    appContext as Activity,
                    "Units Added",
                    "All the units have been added successfully",
                    HomeActivityCx::class.java
                )*//*
                Util.alertOkIntentMessage(
                    this,
                    "Bank Account Not Verified",
                    "Please verify your bank account details to register a landlord.",
                    AccountLinkDetailScreen::class.java
                )
                *//*val intent =
                    Intent(this, AccountLinkDetailScreen::class.java)
                startActivity(intent)*//*
            }*/
            //Util.navigationNext(this, LinkPropertyAgent::class.java)
        }

    }

    private fun bankAddDialog() {
        val mBuilder = AlertDialog.Builder(this)
        mBuilder.setTitle("Error")
        mBuilder.setMessage("Add your bank account to get payment directly to your bank account.")
        mBuilder.setPositiveButton("Ok") { dialog, which ->
            dialog.dismiss()
            Util.navigationNext(this@AgentOnBoardedPropertyActivity, AccountLinkDetailScreen::class.java)
        }

        mBuilder.show()
    }

    private fun bankVerifyDialog() {
        val mBuilder = AlertDialog.Builder(this)
        mBuilder.setTitle("Error")
        mBuilder.setMessage("Your bank account is still pending for verification, We're not able to charge you until you verify your bank account, Please check your bank account to get verification amount.")
        mBuilder.setPositiveButton("Ok") { dialog, which ->
            dialog.dismiss()
            Util.navigationNext(this, AccountLinkDetailScreen::class.java)
        }

        mBuilder.show()
    }

    private fun getPropertyList() {
        if (NetworkConnection.isNetworkConnected(appContext)) {
            //Create retrofit Service
            progressBar?.visibility = View.VISIBLE
            var credentials = AgentBrokerOnboardPropertyCredential()

            credentials!!.userCatalogID = Kotpref.userId
            var propertyListService: ApiInterface =
                ApiClient(appContext).provideService(ApiInterface::class.java)
            var apiCall: Observable<OnboardPropertyListResponseAgent> =
                propertyListService.getOnboardList(credentials)

            RxAPICallHelper().call(
                apiCall,
                object : RxAPICallback<OnboardPropertyListResponseAgent> {
                    override fun onSuccess(propertyListResponse: OnboardPropertyListResponseAgent) {
                        Log.e("onSuccessProperty", propertyListResponse.data!!.size.toString())
                        try {
                            if (!propertyListResponse.data?.isEmpty()!!) {
                                progressBar?.visibility = View.GONE
                                setPropertyList(propertyListResponse.data!!)
                                refreshProperties!!.visibility = View.VISIBLE
                            } else {
                                refreshProperties!!.visibility = View.GONE
                                btnTryAgain?.visibility = View.VISIBLE
                            }
                        } catch (E: Exception) {
                            Log.e("error occur", "please check property api")
                        }


                    }

                    override fun onFailed(t: Throwable) {
                        // show error
                        Log.e("onFailure", t.toString())
                        progressBar?.visibility = View.GONE

                        try {
                            Util.apiFailure(appContext, t)
                        } catch (e: Exception) {
                            Toast.makeText(
                                appContext,
                                getString(R.string.error_server),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        refreshProperties!!.visibility = View.GONE
                        btnTryAgain!!.visibility = View.VISIBLE
                    }
                })
        } else {
            Toast.makeText(appContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show()

        }
    }


    private fun setPropertyList(listResponse: ArrayList<PropertyAgent>) {

        propertyListView?.layoutManager = LinearLayoutManager(appContext)
        if (listResponse.size == 1) {
            ldPropertyListAdapter = AgentOnBoardedPropertyAdapter(
                appContext,

                listResponse as ArrayList<PropertyAgent>
            )
        } else {
            ldPropertyListAdapter = AgentOnBoardedPropertyAdapter(
                appContext,

                listResponse.reversed() as ArrayList<PropertyAgent>
            )
        }

        txt_total_properties.setText("Showing: " + listResponse.size.toString() + " Properties")
        propertyListView?.adapter = ldPropertyListAdapter
    }

    private fun getNotificationList() {
        this.window.setFlags(
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
                    txtLpNotify!!.text = Kotpref.notifyCount
                    //(activity as HomeActivityBroker?)!!.addBadgeNew(messageList.size.toString())
                    this@AgentOnBoardedPropertyActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    if (Integer.parseInt(Kotpref.notifyCount) < 1) {
                        txtLpNotify!!.visibility = View.GONE

                    } else {
                        txtLpNotify!!.visibility = View.VISIBLE

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailed(throwable: Throwable) {
                // show error
                Log.e("onFailure", throwable.toString())
                try {
                    this@AgentOnBoardedPropertyActivity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    txtLpNotify!!.text =
                        (linkRequestListt.size + alertsListt.size + linkRequestListForLdReq.size).toString()
                    if (Integer.parseInt(Kotpref.notifyCount) < 1) {
                        txtLpNotify!!.visibility = View.GONE

                    } else {
                        txtLpNotify!!.visibility = View.VISIBLE

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

}