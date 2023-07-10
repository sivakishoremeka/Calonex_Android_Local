package mp.app.calonex.broker.activity

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.widget.SearchView

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_agent_broker.*
import mp.app.calonex.R

import mp.app.calonex.agent.activity.NotifyScreenAgent

import mp.app.calonex.broker.adapter.BrokerAgentListAdapter

import mp.app.calonex.broker.responce.BrokerAgentListResponse
import mp.app.calonex.broker.responce.Data
import mp.app.calonex.common.apiCredentials.BrokerAgentsCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.CxBaseActivity2

class AgentsActivityBroker : CxBaseActivity2() {
    private lateinit var appContext: Context
    private var ldPropertyListAdapter: BrokerAgentListAdapter? = null
    private var refreshPayment: SwipeRefreshLayout? = null
    private var layoutToolbar: RelativeLayout? = null
    private var searchImage: ImageView? = null
    private var iv_header_logo: ImageView? = null
    private var profile_pic: CircleImageView? = null
    private var addressList = ArrayList<String>()

    //private var paymentList = ArrayList<AgentPaymentHistory>()
    private var btnTryAgain: Button? = null
    private var searchPayment: SearchView? = null
    private var layoutLphNotify: RelativeLayout? = null
    private var txtLphNotify: TextView? = null
    private var layoutAddProperty: LinearLayout? = null
    private var spinner_status: Spinner? = null
    var listResponseData: ArrayList<Data> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agent_broker)

        appContext = this@AgentsActivityBroker

        refreshPayment = findViewById(R.id.refresh_ld_payment)
        layoutToolbar = findViewById(R.id.layout_toolbar)
        searchImage = findViewById(R.id.search_imageview)
        searchPayment = findViewById(R.id.search_ld_property)
        btnTryAgain = findViewById(R.id.btn_try_again)
        layoutLphNotify = findViewById(R.id.layout_lp_notify)
        txtLphNotify = findViewById(R.id.txt_lp_notify)
        spinner_status = findViewById(R.id.spinner_status)

        layoutAddProperty = findViewById(R.id.layout_add_property)

        startHandler()

        /*val searchTextViewId: Int = searchPayment!!.context.resources
            .getIdentifier("android:id/search_src_text", null, null)
        val searchText = searchPayment!!.findViewById(searchTextViewId) as EditText
        searchText.setTextColor(Color.WHITE)*/
        iv_header_logo = findViewById(R.id.iv_back);
        profile_pic = findViewById(R.id.profile_pic);
        Glide.with(appContext)
            .load(Kotpref.profile_image)
            .placeholder(R.drawable.profile_default_new)
            .into(profile_pic!!)

        iv_header_logo!!.setOnClickListener {
            onBackPressed()
        }

        searchImage!!.setOnClickListener {
            layoutToolbar!!.visibility = View.GONE
            searchPayment!!.visibility = View.VISIBLE
        }




        refreshPayment?.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                appContext,
                R.color.colorPrimary
            )
        )
        refreshPayment?.setColorSchemeColors(Color.WHITE)
        refreshPayment?.setOnRefreshListener {

            getSubscriptionList()
            refreshPayment?.isRefreshing = false

        }

        getSubscriptionList()
        layoutLphNotify!!.setOnClickListener {
            Util.navigationNext(this@AgentsActivityBroker, NotifyScreenAgent::class.java)
        }
        btnTryAgain?.setOnClickListener {
            btnTryAgain?.visibility = View.GONE
            getSubscriptionList()
        }
        searchPayment!!.setOnClickListener {
            //  searchPayment!!.isIconified = false
        }

        layoutAddProperty!!.setOnClickListener {
            Util.navigationNext(this, InviteAgent::class.java)
        }

        searchPayment!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()) {
                    ldPropertyListAdapter!!.filter.filter(newText)
                } else {
                    ldPropertyListAdapter!!.filter.filter("")
                }
                return true
            }

        })

        searchPayment!!.setOnCloseListener(object : SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                layoutToolbar!!.visibility = View.VISIBLE
                searchPayment!!.visibility = View.GONE
                return true
            };
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

    private fun getSubscriptionList() {
        if (NetworkConnection.isNetworkConnected(appContext)) {
            //Create retrofit Service
            pb_payment?.visibility = View.VISIBLE
            val credentials = BrokerAgentsCredential()

            credentials.userCatalogID = Kotpref.userId
            val subscriptionListService: ApiInterface =
                ApiClient(appContext).provideService(ApiInterface::class.java)
            val apiCall: Observable<BrokerAgentListResponse> =
                subscriptionListService.getAgentBrokerList(credentials)

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
                        pb_payment?.visibility = View.GONE
                    }

                    override fun onFailed(t: Throwable) {
                        // show error
                        Log.e("onFailure", t.toString())
                        btnTryAgain?.visibility = View.VISIBLE
                        pb_payment?.visibility = View.GONE
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
        //getAddressList(listResponse)
        //paymentList = listResponse
        listResponseData = listResponse


        var unitStatusList =
            arrayOf("")
        var setStatus: HashSet<String> = HashSet()
        setStatus.add("Select Status")
        for (i in 0 until listResponse.size - 1) {
            setStatus.add(listResponse[i].status.toString())
        }
        unitStatusList = setStatus.toList().toTypedArray()

        if (spinner_status != null) {
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item, unitStatusList
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner_status!!.adapter = adapter
        }

        spinner_status?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != -1 && position != 0) {
                    statusFilter(unitStatusList.get(position))
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }

        }


        setList(listResponse)
        Log.e("Data", "Agent response==> " + Gson().toJson(listResponse))

    }

    private fun statusFilter(status: String) {
        lateinit var filterList: List<Data>
        if (!status.isNullOrEmpty()) {
            filterList =
                listResponseData.filter { it.status!!.trim() == status.trim() }

        }
        val array = arrayListOf<Data>()
        array.addAll(filterList)
        setAdapter(array)
    }

    private fun setAdapter(listPayment: ArrayList<Data>) {
        ldPropertyListAdapter = BrokerAgentListAdapter(
            appContext,
            this,
            listPayment,
            "NA"
        )
        rv_ld_payments?.adapter = ldPropertyListAdapter
    }

    private fun setList(rootList: ArrayList<Data>) {
        rv_ld_payments?.layoutManager = LinearLayoutManager(appContext)
        val list: MutableList<Data>? = rootList.toMutableList()

        val listPayment = ArrayList(list!!.reversed())
        setAdapter(listPayment)

    }


    override fun onResume() {
        super.onResume()
        getSubscriptionList()
    }


    /*private fun applyFilter(address: String, rootList: ArrayList<AgentPaymentHistory>) {
        lateinit var filterList: List<AgentPaymentHistory>
        filterList = rootList.filter { it.propertyAddress == address }
        val array = arrayListOf<AgentPaymentHistory>()
        array.addAll(filterList)
        setList(array)
    }*/
}

/*
{"responseDto":{"responseCode":200,"responseDescription":"success","exceptionCode":0},"data":[{"landlordName":"Calonex Test.Landlord","price":207.24,"currentplan":3,"numberOfUnits":49,"subscriptionStartDate":1635263464000,"subscriptionEndDate":1729957864000,"subscriptionCancel":false,"agentCommission":"18.65","brokerCommission":"62.17","brokerId":"2","agentId":"2851","cxAmount":"207.24"}]}
 */