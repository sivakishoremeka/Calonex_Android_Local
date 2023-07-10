package mp.app.calonex.agent.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.reactivex.Observable
import mp.app.calonex.R
import mp.app.calonex.agent.adapter.PropertyListAdapterAgent
import mp.app.calonex.agent.model.PropertyAgent
import mp.app.calonex.agent.responce.OnboardPropertyListResponseAgent
import mp.app.calonex.common.apiCredentials.AgentBrokerOnboardPropertyCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.AccountLinkDetailScreen
import mp.app.calonex.landlord.activity.CxBaseActivity2
import mp.app.calonex.service.FeaturesService

class OnboardPropertyActivityAgent : CxBaseActivity2() {
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
    private var totalCount: TextView? = null
    private var iv_back: ImageView? = null
    private var progress: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboard_property_agent)

        appContext = this@OnboardPropertyActivityAgent

        searchProperty = findViewById(R.id.search_ld_property)
        rvLandlordProperty = findViewById(R.id.rv_ld_properties)
        refreshProperties = findViewById(R.id.refresh_ld_properties)
        layoutAddProperty = findViewById(R.id.layout_add_property)
        btnTryAgain = findViewById(R.id.btn_try_again)
        layoutLpNotify = findViewById(R.id.layout_lp_notify)
        txtLpNotify = findViewById(R.id.txt_lp_notify)
        totalCount = findViewById(R.id.txt_total_properties)
        progress = findViewById(R.id.pb_property)
        iv_back = findViewById(R.id.iv_back)

        startHandler()

        val searchTextViewId: Int = searchProperty!!.context.resources
            .getIdentifier("android:id/search_src_text", null, null)
        val searchText = searchProperty!!.findViewById(searchTextViewId) as EditText
        searchText.setTextColor(Color.WHITE)

        val dividerItemDecoration =
            DividerItemDecoration(rvLandlordProperty!!.context, DividerItemDecoration.VERTICAL)
        dividerItemDecoration.setDrawable(
            ContextCompat.getDrawable(appContext, R.drawable.item_list_divider)!!
        )

        iv_back!!.setOnClickListener {
            onBackPressed()
        }

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
            refreshProperties?.isRefreshing = false
        }

        layoutAddProperty!!.setOnClickListener {
            if (!Kotpref.bankAdded) {
                bankAddDialog()
            }else if (!Kotpref.bankAccountVerified) {
                bankVerifyDialog()
            }else {
                Util.navigationNext(this, LinkPropertyAgent::class.java)
            }
        }

        layoutLpNotify!!.setOnClickListener {
            Util.navigationNext(this, NotifyScreenAgent::class.java)
        }
        btnTryAgain?.setOnClickListener {
            btnTryAgain?.visibility = View.GONE
            getPropertyList()
        }
    }

    private fun bankAddDialog() {
        val mBuilder = AlertDialog.Builder(this)
        mBuilder.setTitle("Error")
        mBuilder.setMessage("Add your bank account to get payment directly to your bank account.")
        mBuilder.setPositiveButton("Ok") { dialog, which ->
            dialog.dismiss()
            Util.navigationNext(this, AccountLinkDetailScreen::class.java)
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
            progress?.visibility = View.VISIBLE
            var credentials = AgentBrokerOnboardPropertyCredential()

            credentials.userCatalogID = Kotpref.userId
            var propertyListService: ApiInterface =
                ApiClient(appContext).provideService(ApiInterface::class.java)
            var apiCall: Observable<OnboardPropertyListResponseAgent> =
                propertyListService.getOnboardList(credentials)

            RxAPICallHelper().call(
                apiCall,
                object : RxAPICallback<OnboardPropertyListResponseAgent> {
                    override fun onSuccess(propertyListResponse: OnboardPropertyListResponseAgent) {
                        Log.e(
                            "onSuccessProperty",
                            propertyListResponse.responseDto?.responseCode.toString()
                        )
                        if (!propertyListResponse.data?.isEmpty()!!) {
                            setPropertyList(propertyListResponse.data!!)
                            refreshProperties!!.visibility = View.VISIBLE
                        } else {
                            refreshProperties!!.visibility = View.GONE
                            btnTryAgain?.visibility = View.VISIBLE
                        }
                        progress?.visibility = View.GONE
                        if (FeaturesService.allModel == null) {
                            val intent = Intent(appContext, FeaturesService::class.java).apply {
                                action = BackgroundIntentServiceAction
                            }
                            appContext.startService(intent)
                        }
                    }

                    override fun onFailed(t: Throwable) {
                        // show error
                        Log.e("onFailure", t.toString())
                        progress?.visibility = View.GONE

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

        rvLandlordProperty?.layoutManager = LinearLayoutManager(appContext)
        if (listResponse.size == 1) {
            ldPropertyListAdapter = PropertyListAdapterAgent(
                appContext,
                this,
                progress!!,
                listResponse
            )
        } else {
            ldPropertyListAdapter = PropertyListAdapterAgent(
                appContext,
                this,
                progress!!,
                listResponse.reversed() as ArrayList<PropertyAgent>
            )
        }

        totalCount?.text = "Showing: " + listResponse.size.toString() + " Properties"
        rvLandlordProperty?.adapter = ldPropertyListAdapter
    }
}

/*
{"responseDto":{"responseCode":200,"responseDescription":"success","exceptionCode":0},"data":[{"landlordName":"Calonex Test.Landlord","price":207.24,"currentplan":3,"numberOfUnits":49,"subscriptionStartDate":1635263464000,"subscriptionEndDate":1729957864000,"subscriptionCancel":false,"agentCommission":"18.65","brokerCommission":"62.17","brokerId":"2","agentId":"2851","cxAmount":"207.24"}]}
 */