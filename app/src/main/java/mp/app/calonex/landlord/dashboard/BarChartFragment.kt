package mp.app.calonex.landlord.dashboard

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
import mp.app.calonex.landlord.adapter.CustomSpinnerAdapter
import mp.app.calonex.landlord.model.LandlordRevenueDetail
import mp.app.calonex.landlord.response.LandlordRevenueDetailResponse
import org.json.JSONException
import java.text.DecimalFormat
import java.util.*


class BarChartFragment : Fragment() {
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
    var spinnerYear: Spinner? = null
    var expenseTextView: TextView? = null
    var profitTextView: TextView? = null
    var incomeTextView: TextView? = null
    lateinit var appContext: Context
    var yearInSpinner: String = ""
    var isFirstTime: Boolean = true

    var apiResponse = LandlordRevenueDetail()

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
        val rootView = inflater.inflate(R.layout.bar_graph_fragment, container, false)

        val calendar = Calendar.getInstance(TimeZone.getDefault())
        val year: Int = calendar.get(Calendar.YEAR)

        barChart = rootView.findViewById(R.id.barchart)
        spinnerYear = rootView.findViewById(R.id.spinner_year)
        expenseTextView = rootView.findViewById(R.id.expenseTextview)
        profitTextView = rootView.findViewById(R.id.profitTextview)
        incomeTextView = rootView.findViewById(R.id.incomeTextview)
        Log.d(TAG, "onCreateView: year is "+year)

        getRevenueDetails(year.toString(), true)

        return rootView
    }

    override fun onResume() {
        super.onResume()
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        val year: Int = calendar.get(Calendar.YEAR)
        getRevenueDetails(year.toString(), true)
    }
    private fun setSpinner() {

        yearSelected = ArrayList<String>()

        for (item in apiResponse.year!!) {
            yearSelected.add(item)
        }
        if (yearSelected.size == 0) {
            spinnerYear!!.visibility = View.INVISIBLE
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
        val labels = java.util.ArrayList<String>()
        for (element in monthValues) {
            labels.add(element)
        }
        val size: Int = response.monthlyIncome!!.size

//        incomeTextView!!.text = "Income " + "$" + response.income

        Log.d(TAG, "setChart: express is "+ response.yearlyExpenses?.get(0)?.expense!!.toDouble())
        displayExpenseProfitView(
            response.yearlyExpenses?.get(0)?.expense!!.toDouble(),
            response.profit.toDouble(),
            response.income.toDouble()
        )

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
        barChart!!.legend.isEnabled = false;

        val xAxis = barChart!!.xAxis
        xAxis.granularity = 1f
        xAxis.labelRotationAngle = 0f
        xAxis.labelCount = labels.size
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setLabelRotationAngle(-20f);
        /*xAxis.axisMaximum = getExpenseEntries(size)!!.size.toFloat()
        xAxis.axisMinimum = data.xMin -.5f;
        xAxis.axisMaximum = data.xMax +.5f;*/
        //xAxis.setCenterAxisLabels(false)
        //  xAxis.setAvoidFirstLastClipping(false);

        // barChart!!.setFitBars(true);

        barChart!!.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        barChart!!.setExtraBottomOffset(10f)
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
        barChart!!.getXAxis().setDrawGridLines(false);
        barChart!!.invalidate()

    }

    @Throws(JSONException::class)
    private fun getExpenseEntries(size: Int): MutableList<BarEntry> {

        var expenseEntries = ArrayList<BarEntry>()
        expenseAmount = ArrayList()
        xAxisValues = ArrayList()
        val yaxisEntryMap = LinkedHashMap<String, String>()
        for (i in 0 until apiResponse.monthlyIncome!!.size) {
//            val json_objectdetail = jsonarray_info.getJSONObject(i)
            expenseAmount!!.add(apiResponse.monthlyIncome!![i].monthyRevenue.toDouble())
            yaxisEntryMap.put(
                apiResponse.monthlyIncome!![i].month.substring(0, 3),
                apiResponse.monthlyIncome!![i].monthyRevenue
            )

        }
        for ((key, value) in yaxisEntryMap) {
            if (key == monthValues[0]) {
                expenseEntries.add(BarEntry(0f, value.toFloat()))
            } else {
                expenseEntries.add(BarEntry(0f, -100000f))
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
                expenseEntries.add(BarEntry(11f, -100000f))
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
            Log.e("DASH_data_1", "API-NUM-1")
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
                        Log.e("DDDD", response.toString())
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

    private fun displayExpenseProfitView(expense: Double, profit: Double, income: Double) {
        val expenseText = "Expense\n"
        val incomeText = "Income\n"
        val profitText = "Profit\n"
        val lossText = "Loss\n"
        if (expense <= 0) {
            val zeroFormat = DecimalFormat("0.00")
            expenseTextView!!.text = expenseText.plus("$ " + zeroFormat.format(expense))
        } else {
            val format = DecimalFormat("##,###.00")
            expenseTextView!!.text = expenseText.plus("$ " + format.format(expense))
        }
        if (income <= 0) {
            val zeroFormat = DecimalFormat("0.00")
            incomeTextView!!.text = incomeText.plus("$ " + zeroFormat.format(income))
        } else {
            val format = DecimalFormat("##,###.00")
            incomeTextView!!.text = incomeText.plus("$ " + format.format(income))
        }
        if (profit == 0.0) {
            val zeroFormat = DecimalFormat("0.00")
            profitTextView!!.text = profitText.plus("$ " + zeroFormat.format(profit))
        } else if (profit < 0){
            val zeroFormat = DecimalFormat("0.00")
            profitTextView!!.text = lossText.plus("$ " + zeroFormat.format(profit))
        }
        else {
            val format = DecimalFormat("##,###.00")
            profitTextView!!.text = profitText.plus("$ " + format.format(profit))
        }
    }


    companion object {
        private val TAG = BarChartFragment::class.java.name

        @JvmField
        var MAxValue = 0.0

        @JvmField
        var MinValue = 0.0

        @JvmStatic
        fun newInstance() =
            BarChartFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("data", "data")
                }
            }
    }
}
