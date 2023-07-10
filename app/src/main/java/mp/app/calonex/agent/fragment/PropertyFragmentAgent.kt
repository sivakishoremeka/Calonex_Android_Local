package mp.app.calonex.agent.fragment


import android.content.Context
import android.content.Intent
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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_property_agent.*
import kotlinx.android.synthetic.main.fragment_property_agent.txt_lp_notify
import mp.app.calonex.R
import mp.app.calonex.agent.activity.AgentOnBoardedPropertyActivity
import mp.app.calonex.agent.activity.NotifyScreenAgent
import mp.app.calonex.agent.adapter.PropertyListAdapterAgent
import mp.app.calonex.agent.model.PropertyAgent
import mp.app.calonex.agent.responce.PropertyListResponseAgent
import mp.app.calonex.common.apiCredentials.GetPropertyListAgentApiCredential
import mp.app.calonex.common.apiCredentials.NotificationCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.model.AppNotifications
import mp.app.calonex.landlord.model.NotificationLinkRequestModel
import mp.app.calonex.landlord.model.NotificationLinkRequestModelNew
import mp.app.calonex.landlord.response.AppNotificationResponse

import mp.app.calonex.service.FeaturesService
import mp.app.calonex.service.FeaturesService.Companion.allModel


class PropertyFragmentAgent : Fragment() {
    val BackgroundIntentServiceAction = "android.intent.action.CUSTOME_ACTION_1"

    private lateinit var appContext: Context
    private lateinit var pb_property: ProgressBar
    private var ldPropertyListAdapter: PropertyListAdapterAgent? = null
    private var searchProperty: SearchView? = null
    private var rvLandlordProperty: RecyclerView? = null
    private var refreshProperties: SwipeRefreshLayout? = null
    private var layoutAddProperty: LinearLayout? = null
    private var btnTryAgain: Button? = null
    private var layoutLpNotify:RelativeLayout?=null
    private var txtLpNotify:TextView?=null
    private var profilePic:CircleImageView?=null
    var linkRequestListt = ArrayList<AppNotifications>()
    var linkRequestListForLdReq = ArrayList<AppNotifications>()
    var alertsListt = ArrayList<AppNotifications>()
    var messageList = ArrayList<AppNotifications>()
    var appNotifications = ArrayList<AppNotifications>()
    var mNotificationLinkRequest = NotificationLinkRequestModel()
    var mNotificationLinkRequestNew = NotificationLinkRequestModelNew()


    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext = context
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_property_agent, container, false)
        initComponents(rootView)

        return rootView
    }

    private fun initComponents(viewRoot: View) {
        appContext= requireContext()
        profilePic=viewRoot.findViewById(R.id.profile_pic)
        Glide.with(appContext)
            .load(Kotpref.profile_image)
            .placeholder(R.drawable.profile_default_new)
            .into(profilePic!!)

        searchProperty = viewRoot.findViewById(R.id.search_ld_property)
        pb_property = viewRoot.findViewById(R.id.pb_property)
        rvLandlordProperty = viewRoot.findViewById(R.id.rv_ld_properties)
        refreshProperties = viewRoot.findViewById(R.id.refresh_ld_properties)
        layoutAddProperty = viewRoot.findViewById(R.id.layout_add_property)
        btnTryAgain = viewRoot.findViewById(R.id.btn_try_again)
        layoutLpNotify=viewRoot.findViewById(R.id.layout_lp_notify)
        txtLpNotify=viewRoot.findViewById(R.id.txt_lp_notify)

        layoutAddProperty!!.visibility = View.VISIBLE

        val searchTextViewId: Int = searchProperty!!.getContext().getResources()
            .getIdentifier("android:id/search_src_text", null, null)
        val searchText = searchProperty!!.findViewById(searchTextViewId) as EditText
        searchText!!.setTextColor(Color.WHITE)

        val dividerItemDecoration =
            DividerItemDecoration(rvLandlordProperty!!.context, DividerItemDecoration.VERTICAL)
        dividerItemDecoration.setDrawable(
            ContextCompat.getDrawable(requireActivity(), R.drawable.item_list_divider)!!
        )

        getPropertyList()

        rvLandlordProperty!!.addItemDecoration(dividerItemDecoration)

        //** Set the colors of the Pull To Refresh View
        refreshProperties?.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                appContext,
                R.color.colorPrimary
            )
        )
        refreshProperties?.setColorSchemeColors(Color.WHITE)

        layoutAddProperty?.setOnClickListener {
           var i=Intent(activity,AgentOnBoardedPropertyActivity::class.java);
            appContext.startActivity(i)

        }
        try {
            getPropertyList()
        }catch (e:Exception){
            Log.e("element not found+",e.toString());

        }

        actionComponent()
    }

    override fun onStart() {
        super.onStart()

        if(Kotpref.notifyCount!=null && !Kotpref.notifyCount.isEmpty() &&  Integer.parseInt(Kotpref.notifyCount)>0) {
            txtLpNotify!!.text = Kotpref.notifyCount
            txtLpNotify!!.visibility = View.VISIBLE
        }
        else
        {
            txtLpNotify!!.visibility = View.GONE
        }


    }

    private fun actionComponent() {
        searchProperty!!.setOnClickListener {
            searchProperty!!.isIconified = false
        }

        searchProperty!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()) {
                    ldPropertyListAdapter!!.filter.filter(newText)
                } else {
                    if (ldPropertyListAdapter != null)
                        ldPropertyListAdapter!!.filter.filter("")
                }
                return true
            }
        })

        refreshProperties?.setOnRefreshListener {
            try {
                getPropertyList()
            }catch (e:Exception){
                System.out.println("element not found+1"+e);

            }
            refresh_ld_properties?.isRefreshing = false
        }



        layoutLpNotify!!.setOnClickListener{
            Util.navigationNext(requireActivity(), NotifyScreenAgent::class.java)
        }
        btnTryAgain?.setOnClickListener {
            btnTryAgain?.visibility = View.GONE
            try {
                getPropertyList()
            }catch (e:Exception){
               Log.e("element not found+2",e.toString());

            }
        }
    }
    override fun onResume() {
        super.onResume()
        layoutLpNotify!!.visibility = View.VISIBLE
        linkRequestListt.clear()
        linkRequestListForLdReq.clear()
        alertsListt.clear()
        messageList.clear()
        getNotificationList()

    }

    private fun getPropertyList() {
        if (NetworkConnection.isNetworkConnected(appContext)) {
            //Create retrofit Service
            pb_property?.visibility = View.VISIBLE
            var credentials = GetPropertyListAgentApiCredential()

            credentials!!.primaryContactId = Kotpref.userId
            var propertyListService: ApiInterface =
                ApiClient(appContext).provideService(ApiInterface::class.java)
            var apiCall: Observable<PropertyListResponseAgent> =
                propertyListService.getPropertyListAgent(credentials)

            RxAPICallHelper().call(apiCall, object : RxAPICallback<PropertyListResponseAgent> {
                override fun onSuccess(propertyListResponse: PropertyListResponseAgent) {
                    Log.e("onSuccessProperty", propertyListResponse.responseCode.toString())
                    if (!propertyListResponse.data?.isEmpty()!!) {
                        setPropertyList(propertyListResponse.data!!)
                        refreshProperties!!.visibility = View.VISIBLE
                    } else {
                        refreshProperties!!.visibility = View.GONE
                        btnTryAgain?.visibility = View.VISIBLE
                    }
                    pb_property?.visibility = View.GONE
                    if (allModel == null) {
                        val intent = Intent(appContext, FeaturesService::class.java).apply {
                            setAction(BackgroundIntentServiceAction)
                        }
                        appContext.startService(intent)
                    }
                }

                override fun onFailed(t: Throwable) {
                    // show error
                    Log.e("onFailure", t.toString())
                    pb_property?.visibility = View.GONE

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
                    activity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    txt_lp_notify!!.text =
                        (AgentDashBoardFragment.linkRequestListt.size + AgentDashBoardFragment.alertsListt.size + AgentDashBoardFragment.linkRequestListForLdReq.size).toString()
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


    private fun setPropertyList(listResponse: ArrayList<PropertyAgent>) {

        rvLandlordProperty?.layoutManager = LinearLayoutManager(appContext)
        if (listResponse.size == 1) {
            ldPropertyListAdapter = PropertyListAdapterAgent(
                appContext,
                activity,
                pb_property!!,
                listResponse as ArrayList<PropertyAgent>
            )
        } else {
            ldPropertyListAdapter = PropertyListAdapterAgent(
                appContext,
                activity,
                pb_property!!,
                listResponse.reversed() as ArrayList<PropertyAgent>
            )
        }

       txt_total_properties.setText("Showing: " + listResponse.size.toString() + " Properties")
        rvLandlordProperty?.adapter = ldPropertyListAdapter
    }
}