package mp.app.calonex.broker.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_show_garph.*
import kotlinx.android.synthetic.main.fragment_broker_payment.*
import mp.app.calonex.R
import mp.app.calonex.agent.responce.AgentBookKeepingResponse
import mp.app.calonex.broker.responce.BrokerFranchiseInfoResponse
import mp.app.calonex.broker.responce.BrokerPaymentHistoryListResponse
import mp.app.calonex.common.apiCredentials.LandlordPaymentHistoryCredential
import mp.app.calonex.common.apiCredentials.UserDetailCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs


class BrokerDashboardChartFragment : Fragment() {
    lateinit var appContext: Context
    private var cal = Calendar.getInstance()
    val myFormat = "MMM dd, yyyy"
    var startDate = "2020-01-01"
    var endDate = ""
    var set1: BarDataSet? = null
    var barChart: BarChart? = null
    var barDataSet: BarDataSet? = null
    var barEntriesArrayList = ArrayList<BarEntry>()

    var credit_point: TextView? = null
    var cash_value: TextView? = null


    private var total_ammount: TextView? = null
    private var total_payout_franchise: TextView? = null
    private var total_payout_agent: TextView? = null
    private var total_profit: TextView? = null
    private var ll_points: LinearLayout?=null
    private var rl_points:  RelativeLayout?=null

    var amount1: Double = 0.0
    var amount2: Double = 0.0
    var amount3: Double = 0.0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext = context
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.tenant_bar_graph_fragment, container, false)

        credit_point = rootView.findViewById(R.id.credit_point)
        cash_value = rootView.findViewById(R.id.cash_value)

        barChart = rootView.findViewById(R.id.barchart)



        total_ammount = rootView.findViewById(R.id.total_ammount)
        total_payout_franchise = rootView.findViewById(R.id.total_payout_franchise)
        total_payout_agent = rootView.findViewById(R.id.total_payout_agent)
        total_profit = rootView.findViewById(R.id.total_profit)
        rl_points=rootView.findViewById(R.id.rl_points);
        ll_points=rootView.findViewById(R.id.ll_points);
        val todayDate = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val todayString = formatter.format(todayDate)
        ll_points?.visibility = View.VISIBLE
        rl_points?.visibility=View.GONE
        startDate = "2020-01-01"
        endDate = todayString
        val text1 = "<font color=#000000>Credit Point: </font> <font color=#17AE66>$0.00</font>"
        credit_point!!.text = Html.fromHtml(text1)
        val text2 = "<font color=#000000>Cash Value: </font><font color=#17AE66>$0.00</font>"
        cash_value!!.text = Html.fromHtml(text2)

      // getTenantCreditPoints()
     //  getDashboardDataList()

      //  getPaymentList2()(

        getPaymentList();
        getBookKeepingList()



        return rootView
    }



    companion object {
        @JvmStatic
        fun newInstance() = BrokerDashboardChartFragment()
    /*.apply {
                arguments = Bundle().apply {
                    putSerializable("data", "data")
                }
            }*/
    }


    private fun getPaymentList2() {
        if (NetworkConnection.isNetworkConnected(appContext)) {
            //Create retrofit Service
            pb_payment?.visibility = View.VISIBLE
            val credentials = LandlordPaymentHistoryCredential()

            credentials.userCatalogId = Kotpref.userId
            val paymentListService: ApiInterface =
                ApiClient(appContext).provideService(ApiInterface::class.java)
            val apiCall: Observable<BrokerPaymentHistoryListResponse> =
                paymentListService.getBrokerPaymentHistory(credentials,"","")

            RxAPICallHelper().call(
                apiCall,
                object : RxAPICallback<BrokerPaymentHistoryListResponse> {
                    override fun onSuccess(paymentListResponse: BrokerPaymentHistoryListResponse) {
                        Log.e(
                            "onSuccessPayment",
                            paymentListResponse.responseDto?.responseDescription.toString()
                        )
                        Log.d("TAG", "onSuccessganram: ")
                        if (paymentListResponse.data != null && !paymentListResponse.data?.isEmpty()!!) {

                            for (data in paymentListResponse.data!!) {
                                if (data.amount.toDouble() != 0.00) {
                                    amount2 += data.amount.toDouble()
                                } else {
                                    amount2 = 0.0
                                }
                            }

                            total_ammount!!.text = "$" + amount2


                         //   setPaymentList(paymentListResponse.data!!)
                        } else {
                         //   btnTryAgain?.visibility = View.VISIBLE
                        }
                        pb_payment?.visibility = View.GONE
                        getFranchise();

                    }

                    override fun onFailed(t: Throwable) {
                        // show error
                        Log.e("onFailure", t.toString())
                      //  btnTryAgain?.visibility = View.VISIBLE
                        pb_payment?.visibility = View.GONE
                        t.printStackTrace()
/*
                        try {
                            Util.apiFailure(appContext, t)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(
                                appContext,
                                getString(R.string.error_something),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
*/
                    }
                })
        } else {
            Toast.makeText(appContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
        }
    }


    @SuppressLint("ResourceType")
    private fun char(amoun1: Float,amount2:Float)
    {
        val labels = ArrayList<String>()
        labels.add("Earning")
        labels.add("Expense")

        barEntriesArrayList = ArrayList()
        barEntriesArrayList.add(BarEntry(0f, abs(amoun1)))
        barEntriesArrayList.add(BarEntry(1f, abs(amount2)))

        set1 = BarDataSet(barEntriesArrayList, "")
        set1!!.setColors(
            Color.parseColor(getString(R.color.colorLightRed)),
            Color.parseColor(getString(R.color.colorLightRed))
        )


        set1!!.valueTextSize = 10f

        val data = BarData(set1)
        barChart!!.data = data
        barChart!!.axisLeft.axisMinimum = 0f
        barChart!!.description.isEnabled = false
        barChart!!.axisRight.axisMinimum = 0f
        barChart!!.setDrawBarShadow(false)
        barChart!!.setDrawValueAboveBar(true)
        barChart!!.setDrawGridBackground(false)
        barChart!!.legend.isEnabled = false

        val xAxis = barChart!!.xAxis
        xAxis.granularity = 1f
        xAxis.labelRotationAngle = 0f
        xAxis.labelCount = labels.size
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.labelRotationAngle = -20f
        barChart!!.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        barChart!!.extraBottomOffset = 10f

        val leftAxis = barChart!!.axisLeft
        leftAxis.removeAllLimitLines()
        leftAxis.typeface = Typeface.DEFAULT
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        leftAxis.textColor = Color.BLACK
        leftAxis.setDrawGridLines(true)
        barChart!!.axisRight.isEnabled = false
        barChart!!.isDragEnabled = false
        barChart!!.setTouchEnabled(false)

        data.barWidth = 0.3f
        xAxis.axisMinimum = data.xMin - .5f
        xAxis.axisMaximum = data.xMax + .5f
        barChart!!.xAxis.setDrawGridLines(false)
        barChart!!.invalidate()
    }

    private fun getFranchise() {
        if (NetworkConnection.isNetworkConnected(appContext)) {
            //Create retrofit Service
            pb_payment?.visibility = View.VISIBLE
            val credentials = UserDetailCredential()

            credentials.userId = Kotpref.userId
            val paymentListService: ApiInterface =
                ApiClient(appContext).provideService(ApiInterface::class.java)
            val apiCall: Observable<BrokerFranchiseInfoResponse> =
                paymentListService.getFranchise(credentials)

            RxAPICallHelper().call(
                apiCall,
                object : RxAPICallback<BrokerFranchiseInfoResponse> {
                    override fun onSuccess(paymentListResponse: BrokerFranchiseInfoResponse) {
                        Log.e(
                            "onSuccessPayment",
                            paymentListResponse.percentage.toString()
                        )
                        try {
                            if(paymentListResponse.percentage.toString().isNotEmpty() && !paymentListResponse.percentage.toString().equals("0.00"))
                            {
                                val amt: Double = paymentListResponse.percentage?.toDouble()?:0.0
                                amount3 =((amount2 - amount1) * amt / 100.00)
                                total_payout_franchise!!.text = "$" + amount3.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
                            }
                            total_profit!!.text="$" +(amount2-(amount1+amount3)).toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Log.d("TAG", "onSuccess: "+e.message
                            )                          }
                    }

                    override fun onFailed(t: Throwable) {
                        // show error
                        Log.e("onFailure", t.toString())
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


    private fun getPaymentList() {
        amount1=0.00
        amount2=0.00
        amount3=0.00
        if (NetworkConnection.isNetworkConnected(appContext)) {
            //Create retrofit Service
            pb_payment?.visibility = View.VISIBLE
            val credentials = LandlordPaymentHistoryCredential()

            credentials.userCatalogId = Kotpref.userId
            val paymentListService: ApiInterface =
                ApiClient(appContext).provideService(ApiInterface::class.java)
            val apiCall: Observable<BrokerPaymentHistoryListResponse> =
                paymentListService.brokerPaymentHistory(credentials,"","")
            RxAPICallHelper().call(
                apiCall,
                object : RxAPICallback<BrokerPaymentHistoryListResponse> {
                    override fun onSuccess(paymentListResponse: BrokerPaymentHistoryListResponse) {
                        Log.e(
                            "onSuccessPayment",
                            paymentListResponse.responseDto?.responseDescription.toString()
                        )
                        if (paymentListResponse.data != null && !paymentListResponse.data?.isEmpty()!!) {


                            for (data in paymentListResponse.data!!) {
                                if (data.amount.toDouble() != 0.00) {
                                    amount1 += data.amount.toDouble()
                                } else {
                                    amount1 = 0.0
                                }
                            }


                          //  total_ammount!!.text = "$" + amount2
                            total_payout_agent!!.text = "$" + amount1


                            //   setPaymentList2(paymentListResponse.data!!)
                        } else {
                         //   btnTryAgain?.visibility = View.VISIBLE
                        }
                        pb_payment?.visibility = View.GONE
                        getPaymentList2();


                    }

                    override fun onFailed(t: Throwable) {
                        // show error
                        Log.e("onFailure", t.toString())
                      //  btnTryAgain?.visibility = View.VISIBLE
                      //  pb_payment?.visibility = View.GONE
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

    private fun getBookKeepingList() {
        val bookKeepingService: ApiInterface =
            ApiClient(appContext).provideService(ApiInterface::class.java)
        val apiCall: Observable<AgentBookKeepingResponse> =
            bookKeepingService.getBookKeepingInfo(startDate, endDate) //Test API Key

        RxAPICallHelper().call(apiCall, object : RxAPICallback<AgentBookKeepingResponse> {
            override fun onSuccess(response: AgentBookKeepingResponse) {
                if (response.responseCode == "200") {
                   /* txtTotalEarning!!.text = "$" + response.amount!!.totalEarnings
                    txtTotalExpance!!.text = "$" + response.amount!!.totalExpenses
                    txtTotalBalance!!.text = "$" + response.amount!!.balance
*/
                  /*  var platformEarn = 0.0f
                    var platformExpance = 0.0f

                    for (item in response.fromSystem!!) {
                        if (item.type == "expense") {
                            platformExpance += item.amount
                        } else {
                            platformEarn += item.amount
                        }
                    }*/
                  var totalEarnings:  Float = 0.0F
                    var totalExpenses:Float=0.0f
                    if(!response.amount!!.totalEarnings.toString().isNullOrEmpty())
                        totalEarnings=response.amount!!.totalEarnings


                    if(!response.amount!!.totalExpenses.toString().isNullOrEmpty())
                        totalExpenses=response.amount!!.totalExpenses
                    char(totalEarnings,totalExpenses)

                }
            }

            override fun onFailed(t: Throwable) {
                // show error
                Log.e("onFailure", t.toString())
                //
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
    }

}
