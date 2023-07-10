package mp.app.calonex.agent

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import io.reactivex.Observable
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.LandlordRevenueDetailCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import org.json.JSONException
import java.util.*
import kotlin.collections.ArrayList


class AgentDistributionRankingfragment : Fragment() {
    var barChart: BarChart? = null
    var dataSets = ArrayList<IBarDataSet>()
    var defaultBarWidth = -1f
    var xAxisValues: MutableList<String>? = null
    var set1: MyBarDataSet? = null
    var set2: BarDataSet? = null
    var json_string: String? = null
    var yearSelected = ArrayList<String>()
    var spinnerYearAdapter: ArrayAdapter<String>? = null
    var expenseEntries: ArrayList<BarEntry>? = null
    var expenseAmount: MutableList<Double>? = null
    lateinit var appContext: Context
    var yearInSpinner: String = ""
    var isFirstTime: Boolean = true

    var apiResponse = AgentCommisionDetail()

    var monthValues = arrayOf(
        "Jan",
        "Feb",
        "Mar",
        "Apr",
        "May",
        "Jun",
        "Jul",
        "Aug",
        "Sep",
        "Oct",
        "Nov",
        "Dec"
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext = context
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.agent_distribution_fragment, container, false)
        barChart = rootView.findViewById(R.id.barchart)


        val calendar = Calendar.getInstance(TimeZone.getDefault())
        val yearr: Int = calendar.get(Calendar.YEAR)

        getRevenueDetails(yearr.toString(), true)
        return rootView
    }

    private fun setSpinner() {

        yearSelected = ArrayList<String>()

        for (item in apiResponse.year!!) {
            yearSelected.add(item)
        }
    }


    @Throws(JSONException::class)
    private fun setChart(response: AgentCommisionDetail) {
        val labels = java.util.ArrayList<String>()
        for (element in monthValues) {
            labels.add(element)
        }
        val size: Int = response.monthlyIncome!!.size

        dataSets = ArrayList()
        set2 = BarDataSet(getExpenseEntries(size), "")
        set2!!.setColors(
            Color.parseColor("#395096"),
            Color.parseColor("#395096"),
            Color.parseColor("#395096")
        )
        set2!!.valueTextSize = 10f
        dataSets.add(set2!!)
        val data = BarData(dataSets)
        barChart!!.data = data
        barChart!!.axisLeft.axisMinimum = 0f
        barChart!!.description.isEnabled = false
        barChart!!.axisRight.axisMinimum = 0f
        barChart!!.setDrawBarShadow(false)
        barChart!!.setDrawValueAboveBar(true)
        //   barChart!!.setMaxVisibleValueCount(10)
        //   barChart.setPinchZoom(false);
        barChart!!.setDrawGridBackground(false)
        barChart!!.legend.isEnabled = false

        val xAxis = barChart!!.xAxis
        xAxis.granularity = 1f
        xAxis.labelRotationAngle = 0f
        xAxis.labelCount = labels.size
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.labelRotationAngle = -20f
        /*xAxis.axisMaximum = getExpenseEntries(size)!!.size.toFloat()
        xAxis.axisMinimum = data.xMin -.5f;
        xAxis.axisMaximum = data.xMax +.5f;*/
        //xAxis.setCenterAxisLabels(false)
        //  xAxis.setAvoidFirstLastClipping(false);

        // barChart!!.setFitBars(true);

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
        data.barWidth = 0.5f
        xAxis.axisMinimum = data.xMin - .5f
        xAxis.axisMaximum = data.xMax + .5f
        barChart!!.xAxis.setDrawGridLines(false)
        /* val barChartRender =
             VerticalBarChartRender(
                 barChart,
                 barChart!!.getAnimator(),
                 barChart!!.getViewPortHandler()
             )
         barChartRender.setRadius(10)
         barChartRender.initBuffers()
         barChart!!.setRenderer(barChartRender)*/
        barChart!!.invalidate()

    }

    @Throws(JSONException::class)
    private fun getExpenseEntries(size: Int): MutableList<BarEntry> {
        val expenseEntries = ArrayList<BarEntry>()
        expenseAmount = ArrayList()
        xAxisValues = ArrayList()
        val yaxisEntryMap = LinkedHashMap<String, String>()
        for (i in 0 until apiResponse.monthlyIncome!!.size) {
//            val json_objectdetail = jsonarray_info.getJSONObject(i)
            expenseAmount!!.add(apiResponse.monthlyIncome!![i].bookOfBusiness.toDouble())
            yaxisEntryMap.put(
                apiResponse.monthlyIncome!![i].months.substring(0, 3),
                apiResponse.monthlyIncome!![i].bookOfBusiness
            )

        }
        for ((key, value) in yaxisEntryMap) {
            if (key == monthValues[0]) {
                expenseEntries.add(BarEntry(0f, value.toFloat()))
            } else {
                expenseEntries.add(BarEntry(0f, -1f))
            }
            if (key == monthValues[1]) {
                expenseEntries.add(BarEntry(1f, value.toFloat()))
            }
            if (key == monthValues[2]) {
                expenseEntries.add(BarEntry(2f, value.toFloat()))
            }
            if (key == monthValues[3]) {
                expenseEntries.add(BarEntry(3f, value.toFloat()))
            }
            if (key == monthValues[4]) {
                expenseEntries.add(BarEntry(4f, value.toFloat()))
            }
            if (key == monthValues[5]) {
                expenseEntries.add(BarEntry(5f, value.toFloat()))
            }
            if (key == monthValues[6]) {
                expenseEntries.add(BarEntry(6f, value.toFloat()))
            }
            if (key == monthValues[7]) {
                expenseEntries.add(BarEntry(7f, value.toFloat()))
            }
            if (key == monthValues[8]) {
                expenseEntries.add(BarEntry(8f, value.toFloat()))
            }
            if (key == monthValues[9]) {
                expenseEntries.add(BarEntry(9f, value.toFloat()))
            }
            if (key == monthValues[10]) {
                expenseEntries.add(BarEntry(10f, value.toFloat()))
            }
            if (key == monthValues[11]) {
                expenseEntries.add(BarEntry(11f, value.toFloat()))
            } else {
                expenseEntries.add(BarEntry(11f, -1f))
            }

        }
        Collections.sort(expenseAmount)
        MAxValue = expenseAmount!![expenseAmount!!.size - 1]
        MinValue = expenseAmount!![0]
        // return expenseEntries!!.subList(0, size)
        return expenseEntries

    }


    private fun getRevenueDetails(year: String, firstTime: Boolean) {
        if (NetworkConnection.isNetworkConnected(appContext)) {
            //Create retrofit Service
            val credentials = LandlordRevenueDetailCredential()

            credentials.userCatalogId = Kotpref.userId

            yearInSpinner = year

            credentials.year = year
            val service: ApiInterface =
                ApiClient(appContext).provideService(ApiInterface::class.java)
            val apiCall: Observable<AgentCommisionYearlyResponse> =
                service.getAgentCommissionYearly(credentials)

            RxAPICallHelper().call(
                apiCall,
                object : RxAPICallback<AgentCommisionYearlyResponse> {
                    override fun onSuccess(response: AgentCommisionYearlyResponse) {
                        if (response.data != null) {
                            apiResponse = response.data!!
                            if (isFirstTime) {
                                setSpinner()
                            }
                            setChart(apiResponse)
                        }
                    }

                    override fun onFailed(t: Throwable) {

                    }
                })
        } else {
            Toast.makeText(appContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
        }
    }


    companion object {
        private val TAG = AgentDistributionRankingfragment::class.java.name

        @JvmField
        var MAxValue = 0.0

        @JvmField
        var MinValue = 0.0

        @JvmStatic
        fun newInstance() =
            AgentDistributionRankingfragment().apply {
                arguments = Bundle().apply {
                    putSerializable("data", "data")
                }
            }
    }
}
