package mp.app.calonex.broker.fragment


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
import androidx.appcompat.app.AlertDialog
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
import mp.app.calonex.R
import mp.app.calonex.agent.activity.LinkPropertyAgent
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
import mp.app.calonex.landlord.activity.AccountLinkDetailScreen
import mp.app.calonex.landlord.model.AppNotifications
import mp.app.calonex.landlord.model.NotificationLinkRequestModel
import mp.app.calonex.landlord.model.NotificationLinkRequestModelNew
import mp.app.calonex.landlord.response.AppNotificationResponse

import mp.app.calonex.service.FeaturesService
import mp.app.calonex.service.FeaturesService.Companion.allModel


class PropertyFragmentBroker : Fragment() {
    val BackgroundIntentServiceAction = "android.intent.action.CUSTOME_ACTION_1"

    private lateinit var appContext: Context
    private var ldPropertyListAdapter: PropertyListAdapterAgent? = null
    private var searchProperty: SearchView? = null
    private var rvLandlordProperty: RecyclerView? = null
    private var refreshProperties: SwipeRefreshLayout? = null
    private var layoutAddProperty: LinearLayout? = null
    private var btnTryAgain: Button? = null
    private var layoutLpNotify: RelativeLayout? = null
    private var txtLpNotify: TextView? = null
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
        val rootView = inflater.inflate(R.layout.fragment_property_broker, container, false)
        initComponents(rootView)

        return rootView
    }

    private fun initComponents(viewRoot: View) {
        searchProperty = viewRoot.findViewById(R.id.search_ld_property)
        rvLandlordProperty = viewRoot.findViewById(R.id.rv_ld_properties)
        refreshProperties = viewRoot.findViewById(R.id.refresh_ld_properties)
        layoutAddProperty = viewRoot.findViewById(R.id.layout_add_property)
        btnTryAgain = viewRoot.findViewById(R.id.btn_try_again)
        layoutLpNotify = viewRoot.findViewById(R.id.layout_lp_notify)
        txtLpNotify = viewRoot.findViewById(R.id.txt_lp_notify)
        profilePic=viewRoot.findViewById(R.id.profile_pic)

        //layoutAddProperty!!.visibility = View.INVISIBLE

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
        getNotificationList()
        Glide.with(appContext)
            .load(Kotpref.profile_image)
            .placeholder(R.drawable.profile_default_new)
            .into(profilePic!!)



        rvLandlordProperty!!.addItemDecoration(dividerItemDecoration)

        //** Set the colors of the Pull To Refresh View
        refreshProperties?.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                appContext,
                R.color.colorPrimary
            )
        )
        refreshProperties?.setColorSchemeColors(Color.WHITE)

        actionComponent()
    }

    override fun onStart() {
        super.onStart()


        if (Kotpref.notifyCount != null && !Kotpref.notifyCount.isEmpty() && Integer.parseInt(
                Kotpref.notifyCount
            ) > 0
        ) {
            txtLpNotify!!.text = Kotpref.notifyCount
            txtLpNotify!!.visibility = View.VISIBLE
        } else {
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
            getPropertyList()
            refresh_ld_properties?.isRefreshing = false
        }

        layoutAddProperty!!.setOnClickListener {
            //Util.navigationNext(requireActivity(), AddNewPropertyFirstScreen::class.java)

            if (!Kotpref.bankAdded) {
                bankAddDialog()
            }else if (!Kotpref.bankAccountVerified) {
                bankVerifyDialog()
            }else {
                Util.navigationNext(requireActivity(), LinkPropertyAgent::class.java)
            }

        }

        layoutLpNotify!!.setOnClickListener {
            Util.navigationNext(requireActivity(), NotifyScreenAgent::class.java)
        }
        btnTryAgain?.setOnClickListener {
            btnTryAgain?.visibility = View.GONE
            getPropertyList()
        }
    }
    private fun bankAddDialog() {
        val mBuilder = AlertDialog.Builder(requireContext())
        mBuilder.setTitle("Error")
        mBuilder.setMessage("Add your bank account to get payment directly to your bank account.")
        mBuilder.setPositiveButton("Ok") { dialog, which ->
            dialog.dismiss()
            Util.navigationNext(requireActivity(), AccountLinkDetailScreen::class.java)
        }

        mBuilder.show()
    }
    private fun bankVerifyDialog() {
        val mBuilder = AlertDialog.Builder(requireContext())
        mBuilder.setTitle("Error")
        mBuilder.setMessage("Add your bank account to get payment directly to your bank account.")
        mBuilder.setPositiveButton("Ok") { dialog, which ->
            dialog.dismiss()
            Util.navigationNext(requireActivity(), AccountLinkDetailScreen::class.java)
        }

        mBuilder.show()
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


    private fun setPropertyList(listResponse: ArrayList<PropertyAgent>) {

        try {
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
                    //listResponse.reversed() as ArrayList<PropertyAgent>
                    listResponse as ArrayList<PropertyAgent>
                )
            }

            txt_total_properties.setText("Showing: " + listResponse.size.toString() + " Properties")
            rvLandlordProperty?.adapter = ldPropertyListAdapter
        } catch (e: Exception) {
            e.printStackTrace()

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
                    txtLpNotify!!.text = Kotpref.notifyCount
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
                    txtLpNotify!!.text =
                        (linkRequestListt.size +alertsListt.size +linkRequestListForLdReq.size).toString()
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