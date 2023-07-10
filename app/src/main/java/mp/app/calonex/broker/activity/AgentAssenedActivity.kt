package mp.app.calonex.broker.activity

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide

import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_agent_assened.*

import mp.app.calonex.R
import mp.app.calonex.agent.activity.NotifyScreenAgent
import mp.app.calonex.broker.adapter.AssignedAgentForPropertyListAdapter
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.CxBaseActivity2
import mp.app.calonex.models.broker.AssignedAgentListData
import mp.app.calonex.models.broker.AssignedAgentModel

class AgentAssenedActivity : CxBaseActivity2() {
    private lateinit var appContext: Context
    private lateinit var brokerId: String
    private lateinit var propertyId: String
    private var brokerAgentListAdapter: AssignedAgentForPropertyListAdapter? = null
    private var refresh: SwipeRefreshLayout? = null
    private var btnTryAgain: Button? = null
    private var layoutLphNotify: RelativeLayout? = null
    private var txtLphNotify: TextView? = null
    private var backButton: ImageView? = null
    private var profile_pic: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agent_assened)

        appContext = this@AgentAssenedActivity

        refresh = findViewById(R.id.refresh)
        backButton = findViewById(R.id.iv_back)
        btnTryAgain = findViewById(R.id.btn_try_again)
        layoutLphNotify = findViewById(R.id.layout_lp_notify)
        txtLphNotify = findViewById(R.id.txt_lp_notify)
        profile_pic = findViewById(R.id.profile_pic)
        propertyId = intent.getStringExtra("propertyId").toString()
        brokerId = intent.getStringExtra("brokerId").toString()
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

       // getAgentList()
        layoutLphNotify!!.setOnClickListener {
            Util.navigationNext(this@AgentAssenedActivity, NotifyScreenAgent::class.java)
        }
        btnTryAgain?.setOnClickListener {
            btnTryAgain?.visibility = View.GONE
            getAgentList()
        }


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
            /*val credentials = BrokerAgentsCredential()

            credentials.userCatalogID = Kotpref.userId*/
            val subscriptionListService: ApiInterface =
                ApiClient(appContext).provideService(ApiInterface::class.java)
            val apiCall: Observable<AssignedAgentListData> =
                subscriptionListService.getPropertyWiseAgentList("$propertyId")

            RxAPICallHelper().call(
                apiCall,
                object : RxAPICallback<AssignedAgentListData> {
                    override fun onSuccess(assignedAgentList: AssignedAgentListData) {
                        Log.e(
                            "onSuccess",
                            "Agent List"+assignedAgentList.responseDto?.responseDescription.toString()
                        )

                        if (assignedAgentList.responseDto.status == 200) {
                            if (assignedAgentList.assignedAgents.isNotEmpty()) {
                                setSubscriptionList(assignedAgentList.assignedAgents)
                            } else {
                                btnTryAgain?.visibility = View.VISIBLE
                            }
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

    private fun setSubscriptionList(listResponse: ArrayList<AssignedAgentModel>) {

        setList(listResponse)

    }


    private fun setList(rootList: ArrayList<AssignedAgentModel>) {
        agent_list?.layoutManager = LinearLayoutManager(appContext)
        val list: MutableList<AssignedAgentModel>? = rootList.toMutableList()

        val listPayment = ArrayList(list!!.reversed())
        brokerAgentListAdapter = AssignedAgentForPropertyListAdapter(
            appContext,
            this,
            listPayment,
            propertyId
        )
        agent_list?.adapter = brokerAgentListAdapter
    }


    override fun onResume() {
        super.onResume()
        getAgentList()
    }


}