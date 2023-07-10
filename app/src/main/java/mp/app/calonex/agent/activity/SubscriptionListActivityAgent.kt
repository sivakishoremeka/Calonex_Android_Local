package mp.app.calonex.agent.activity

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_subscription_list_agent.*
import mp.app.calonex.R
import mp.app.calonex.agent.adapter.AgentSubscriptionListAdapter
import mp.app.calonex.agent.model.AgentSubscriptionList
import mp.app.calonex.agent.responce.AgentSubscriptionListResponse
import mp.app.calonex.common.apiCredentials.LandlordPaymentHistoryCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.CxBaseActivity2
import mp.app.calonex.landlord.adapter.CustomSpinnerAdapter

class SubscriptionListActivityAgent : CxBaseActivity2() {
    private lateinit var appContext: Context
    private var ldPropertyListAdapter: AgentSubscriptionListAdapter? = null
    private var refreshPayment: SwipeRefreshLayout? = null
    private var spinnerAddress: Spinner? = null
    private var addressList = ArrayList<String>()
    //private var paymentList = ArrayList<AgentPaymentHistory>()
    private var btnTryAgain: Button? = null
    private var searchPayment: SearchView? = null
    private var layoutLphNotify: RelativeLayout? = null
    private var txtLphNotify: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscription_list_agent)

        appContext = this@SubscriptionListActivityAgent

        refreshPayment = findViewById(R.id.refresh_ld_payment)
        searchPayment = findViewById(R.id.search_payment)
        spinnerAddress = findViewById(R.id.spinner_property)
        btnTryAgain = findViewById(R.id.btn_try_again)
        layoutLphNotify = findViewById(R.id.layout_lph_notify)
        txtLphNotify = findViewById(R.id.txt_lph_notify)

        startHandler()

        val searchTextViewId: Int = searchPayment!!.context.resources
            .getIdentifier("android:id/search_src_text", null, null)
        val searchText = searchPayment!!.findViewById(searchTextViewId) as EditText
        searchText.setTextColor(Color.WHITE)
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
            Util.navigationNext(this@SubscriptionListActivityAgent, NotifyScreenAgent::class.java)
        }
        btnTryAgain?.setOnClickListener {
            btnTryAgain?.visibility = View.GONE
            getSubscriptionList()
        }
        searchPayment!!.setOnClickListener {
            //  searchPayment!!.isIconified = false
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
    }

    override fun onStart() {
        super.onStart()
        if(Kotpref.notifyCount!=null && !Kotpref.notifyCount.isEmpty() && Integer.parseInt(Kotpref.notifyCount)>0) {
            txtLphNotify!!.text = Kotpref.notifyCount
            txtLphNotify!!.visibility = View.VISIBLE
        }
        else
        {
            txtLphNotify!!.visibility = View.GONE
        }




    }

    private fun getSubscriptionList() {
        if (NetworkConnection.isNetworkConnected(appContext)) {
            //Create retrofit Service
            pb_payment?.visibility = View.VISIBLE
            val credentials = LandlordPaymentHistoryCredential()

            credentials.userCatalogId = Kotpref.userId
            val subscriptionListService: ApiInterface =
                ApiClient(appContext).provideService(ApiInterface::class.java)
            val apiCall: Observable<AgentSubscriptionListResponse> =
                subscriptionListService.getSubscriptionList(credentials)

            RxAPICallHelper().call(
                apiCall,
                object : RxAPICallback<AgentSubscriptionListResponse> {
                    override fun onSuccess(paymentListResponse: AgentSubscriptionListResponse) {
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

    private fun setSubscriptionList(listResponse: ArrayList<AgentSubscriptionList>) {
        //getAddressList(listResponse)
        //paymentList = listResponse
        setList(listResponse)
        var spinnerAddressAdapter = CustomSpinnerAdapter(appContext, addressList)
        /*spinnerAddress?.adapter=spinnerAddressAdapter

        spinnerAddress?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {

                if (position> 0){
                    val value:String= addressList.get(position)
                    applyFilter(value,paymentList)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // another interface callback
            }
        }*/
    }

    /*private fun getAddressList(rootList: ArrayList<AgentPaymentHistory>) {
        addressList.add("Select Property")
        val addList = mutableListOf<String>()
        for (item in rootList) {
            addList.add(item.propertyAddress)
        }
        addressList.addAll(ArrayList<String>(addList.toSet().toList()))
    }*/

    private fun setList(rootList: ArrayList<AgentSubscriptionList>) {
        rv_ld_payments?.layoutManager = LinearLayoutManager(appContext)
        val list: MutableList<AgentSubscriptionList>? = rootList.toMutableList()

        val listPayment = ArrayList(list!!.reversed())
        ldPropertyListAdapter = AgentSubscriptionListAdapter(
            appContext,
            this,
            listPayment
        )
        rv_ld_payments?.adapter = ldPropertyListAdapter
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