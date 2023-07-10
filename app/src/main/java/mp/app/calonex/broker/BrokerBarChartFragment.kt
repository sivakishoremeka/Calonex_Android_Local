package mp.app.calonex.broker

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
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
import mp.app.calonex.landlord.adapter.CustomSpinnerAdapter
import mp.app.calonex.landlord.model.LandlordRevenueDetail
import mp.app.calonex.landlord.response.LandlordRevenueDetailResponse
import org.json.JSONException
import java.util.*
import kotlin.collections.ArrayList


class BrokerBarChartFragment : Fragment() {
    var barChart: BarChart? = null
    var dataSets = ArrayList<IBarDataSet>()
    var defaultBarWidth = -1f
    var xAxisValues: MutableList<String>? = null
    var set1Broker: BrokerMyBarDataSet? = null
    var set2Broker: BrokerMyBarDataSet? = null
    var json_string: String? = null
    var yearSelected = ArrayList<String>()
    var spinnerYearAdapter: ArrayAdapter<String>? = null
    var expenseEntries: ArrayList<BarEntry>? = null
    var expenseAmount: MutableList<Double>? = null
    var spinnerYear: Spinner? = null
    lateinit var appContext: Context
    var yearInSpinner: String = ""
    var isFirstTime: Boolean = true

    var apiResponse = LandlordRevenueDetail()


    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext = context
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.bar_graph_fragment, container, false)
        barChart = rootView.findViewById(R.id.barchart)
        spinnerYear = rootView.findViewById(R.id.spinner_year)

        getRevenueDetails("2020", true)

        return rootView
    }

    private fun setSpinner() {

        yearSelected = ArrayList<String>()

        for (item in apiResponse.year!!) {
            yearSelected.add(item)
        }
        val spinnerStateAdapter = CustomSpinnerAdapter(appContext, yearSelected)
        spinnerYear?.adapter = spinnerStateAdapter

        spinnerYear!!.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {

                if (!isFirstTime) {
                    getRevenueDetails(yearSelected[position], false)
                }
                isFirstTime = false
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

    }


    @Throws(JSONException::class)
    private fun setChart(response: LandlordRevenueDetail) {
        val size: Int = response.monthlyIncome!!.size


        val expenseEntries = getExpenseEntries(size)
        dataSets = ArrayList()
        set2Broker =
            BrokerMyBarDataSet(expenseEntries, "", 0)
        set2Broker!!.setColors(
            Color.parseColor("#395096"),
            Color.parseColor("#395096"),
            Color.parseColor("#395096")
        )
        set2Broker!!.valueTextSize = 10f
        dataSets.add(set2Broker!!)
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
        barChart!!.legend.isEnabled = false;

        val xAxis = barChart!!.xAxis
        xAxis.granularity = 1f
        xAxis.labelRotationAngle = 0f
        xAxis.labelCount = size
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.axisMaximum = getExpenseEntries(size).size.toFloat()

        //xAxis.setCenterAxisLabels(false)
        //  xAxis.setAvoidFirstLastClipping(false);

        // barChart!!.setFitBars(true);

        barChart!!.xAxis.valueFormatter = IndexAxisValueFormatter(xAxisValues)
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
        xAxis.axisMinimum = data.xMin - .5f;
        xAxis.axisMaximum = data.xMax + .5f;
        barChart!!.invalidate()

    }

    @Throws(JSONException::class)
    private fun getExpenseEntries(size: Int): MutableList<BarEntry> {
        expenseEntries = ArrayList()
        expenseAmount = ArrayList()
        xAxisValues = ArrayList()

//        val root = JSONObject(json_string)
//        val jsonarray_info = apiResponse.monthlyIncome
//        val jsonSize = jsonarray_info!!.size
        xAxisValues!!.add("")
        for (i in 0 until apiResponse.monthlyIncome!!.size) {
//            val json_objectdetail = jsonarray_info.getJSONObject(i)
            expenseAmount!!.add(apiResponse.monthlyIncome!![i].monthyRevenue.toDouble())
            expenseEntries!!.add(
                BarEntry(
                    i + 1f,
                    apiResponse.monthlyIncome!![i].monthyRevenue.toFloat()
                )
            )
            xAxisValues!!.add(apiResponse.monthlyIncome!![i].month.substring(0, 3))
        }
        Collections.sort(expenseAmount)
        MAxValue = expenseAmount!![expenseAmount!!.size - 1]
        MinValue = expenseAmount!![0]
        return expenseEntries!!.subList(0, size)
        //return expenseEntries

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
            val apiCall: Observable<LandlordRevenueDetailResponse> =
                service.getRevenueDetail(credentials)

            RxAPICallHelper().call(
                apiCall,
                object : RxAPICallback<LandlordRevenueDetailResponse> {
                    override fun onSuccess(response: LandlordRevenueDetailResponse) {
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
        private val TAG = BrokerBarChartFragment::class.java.name

        @JvmField
        var MAxValue = 0.0

        @JvmField
        var MinValue = 0.0

        @JvmStatic
        fun newInstance() =
            BrokerBarChartFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("data", "data")
                }
            }
    }
}



