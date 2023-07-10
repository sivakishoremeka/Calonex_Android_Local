package mp.app.calonex.broker.activity

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_assign_agent_for_property.*
import mp.app.calonex.R
import mp.app.calonex.agent.activity.NotifyScreenAgent
import mp.app.calonex.broker.adapter.BrokerAssignAgentAdapter
import mp.app.calonex.broker.responce.BrokerAgentListResponse
import mp.app.calonex.broker.responce.Data
import mp.app.calonex.common.apiCredentials.AssignPropertyWiseAgents
import mp.app.calonex.common.apiCredentials.BrokerAgentsCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.CxBaseActivity2
import mp.app.calonex.models.broker.AssignAgentResponseDao
import mp.app.calonex.models.broker.AssignedAgentModel


class AssignAgentForPropertyActivity : CxBaseActivity2() {
    private lateinit var appContext: Context
    private lateinit var brokerId: String
    private lateinit var propertyId: String
    private var brokerAgentListAdapter: BrokerAssignAgentAdapter? = null
    private var refresh: SwipeRefreshLayout? = null
    private var btnTryAgain: Button? = null
    private var layoutLphNotify: RelativeLayout? = null
    private var txtLphNotify: TextView? = null
    private var btnSubmit: TextView? = null
    private var backButton: ImageView? = null
    private var profile_pic: ImageView? = null
    var assignedAgentsForProperty: ArrayList<AssignedAgentModel> = ArrayList()
    var selectedAgentIDs: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assign_agent_for_property)

        appContext = this@AssignAgentForPropertyActivity

        refresh = findViewById(R.id.refresh)
        backButton = findViewById(R.id.iv_back)
        btnTryAgain = findViewById(R.id.btn_try_again)
        btnSubmit = findViewById(R.id.btn_submit)
        layoutLphNotify = findViewById(R.id.layout_lp_notify)
        txtLphNotify = findViewById(R.id.txt_lp_notify)
        profile_pic = findViewById(R.id.profile_pic)
        propertyId = intent.getStringExtra("propertyId").toString()
        brokerId = intent.getStringExtra("brokerId").toString()
        try {
            val assignedAgentObj = intent.getStringExtra("assignAgents").toString()
            val type = object : TypeToken<List<AssignedAgentModel?>?>() {}.type
            assignedAgentsForProperty = Gson().fromJson(assignedAgentObj, type)
            for (i in assignedAgentsForProperty.indices) {
                selectedAgentIDs.add("" + assignedAgentsForProperty[i].agentLicenseNO)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        Glide.with(appContext)
            .load(Kotpref.profile_image)
            .placeholder(R.drawable.profile_default_new)
            .into(profile_pic!!)


        backButton!!.setOnClickListener { onBackPressed() }

        startHandler()


        refresh?.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                appContext,
                R.color.colorPrimary
            )
        )
        refresh?.setColorSchemeColors(Color.WHITE)
        refresh?.setOnRefreshListener {

            getAgentList()
            refresh?.isRefreshing = false

        }

        getAgentList()
        layoutLphNotify!!.setOnClickListener {
            Util.navigationNext(this@AssignAgentForPropertyActivity, NotifyScreenAgent::class.java)
        }

        btnTryAgain?.setOnClickListener {
            btnTryAgain?.visibility = View.GONE
            getAgentList()
        }


        btnSubmit?.setOnClickListener {

        }

    }

    private fun getPropertyWiseAgentList(agentID: String, status: String, agentLicense: String) {
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        pb.visibility = View.VISIBLE

        var requestData = AssignPropertyWiseAgents()
        requestData.propertyId = propertyId
        requestData.agentId = agentID
        requestData.status = status


        val propertyDetails: ApiInterface =
            ApiClient(this).provideService(ApiInterface::class.java)
        val apiCall: Observable<AssignAgentResponseDao> =
            propertyDetails.assignPropertyWiseAgent(requestData)

        RxAPICallHelper().call(apiCall, object : RxAPICallback<AssignAgentResponseDao> {
            override fun onSuccess(assignedAgentList: AssignAgentResponseDao) {
                Log.e("Get", "Assigned Agent List >> " + Gson().toJson(assignedAgentList))
                this@AssignAgentForPropertyActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                pb.visibility = View.GONE
                if (assignedAgentList.responseDto.status == 200) {
                    if (selectedAgentIDs.contains(agentLicense)) {
                        selectedAgentIDs.remove(agentLicense)
                    } else {
                        selectedAgentIDs.add(agentLicense)
                    }
                    brokerAgentListAdapter?.notifyDataSetChanged()
                } else {

                }


            }

            override fun onFailed(t: Throwable) {
                // show error
                this@AssignAgentForPropertyActivity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                pb.visibility = View.GONE

                Log.e("onFailure", t.toString())
                //  progressBarList?.visibility = View.GONE
            }
        })
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

    private fun getAgentList() {
        if (NetworkConnection.isNetworkConnected(appContext)) {
            //Create retrofit Service
            pb?.visibility = View.VISIBLE
            val credentials = BrokerAgentsCredential()

            credentials.userCatalogID = Kotpref.userId
            val subscriptionListService: ApiInterface =
                ApiClient(appContext).provideService(ApiInterface::class.java)
            val apiCall: Observable<BrokerAgentListResponse> =
                subscriptionListService.getAssignableAgentList(credentials)

            RxAPICallHelper().call(
                apiCall,
                object : RxAPICallback<BrokerAgentListResponse> {
                    override fun onSuccess(paymentListResponse: BrokerAgentListResponse) {
                        Log.e(
                            "onSuccessPayment",
                            paymentListResponse.responseDto?.responseDescription.toString()
                        )
                        if (paymentListResponse.data != null && !paymentListResponse.data?.isEmpty()!!) {

                            setSubscriptionList(paymentListResponse.data!!)
                        } else {
                            btnTryAgain?.visibility = View.VISIBLE
                        }
                        pb?.visibility = View.GONE
                    }

                    override fun onFailed(t: Throwable) {
                        // show error
                        Log.e("onFailure", t.toString())
                        btnTryAgain?.visibility = View.VISIBLE
                        pb?.visibility = View.GONE
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

    private fun setSubscriptionList(listResponse: ArrayList<Data>) {

        setList(listResponse)

    }


    private fun setList(rootList: ArrayList<Data>) {
        agent_list?.layoutManager = LinearLayoutManager(appContext)
        val list: MutableList<Data>? = rootList.toMutableList()

        val listPayment = ArrayList(list!!.reversed())
        brokerAgentListAdapter = BrokerAssignAgentAdapter(
            appContext,
            this,
            listPayment,
            selectedAgentIDs,
            propertyId
        ) { data: Data, agentLicense: String ->

            if (selectedAgentIDs.contains(agentLicense)) {
                //selectedAgentIDs.remove(agentLicense)
                //getPropertyWiseAgentList("" + data.agentId, ""+data.status, agentLicense)
            } else {
                //selectedAgentIDs.add(agentLicense)
                getPropertyWiseAgentList("" + data.agentId, "" + data.status, agentLicense)
            }

        }
        agent_list?.adapter = brokerAgentListAdapter
    }


    override fun onResume() {
        super.onResume()
        getAgentList()
    }


}