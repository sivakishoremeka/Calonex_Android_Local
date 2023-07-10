package mp.app.calonex.tenant.fragment

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
import android.view.WindowManager
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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import mp.app.calonex.R
import mp.app.calonex.agent.responce.AgentBookKeepingResponse
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.tenant.model.GetCreditePointsTenant
import mp.app.calonex.tenant.response.CreditPointResponse
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs


class TenantDashboardChart2Fragment : Fragment() {
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

        val todayDate = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val todayString = formatter.format(todayDate)

        startDate = "2020-01-01"
        endDate = todayString

        val text1 = "<font color=#000000>Credit Point: </font> <font color=#17AE66>$0.00</font>"
        credit_point!!.text = Html.fromHtml(text1)
        val text2 = "<font color=#000000>Cash Value: </font><font color=#17AE66>$0.00</font>"
        cash_value!!.text = Html.fromHtml(text2)

        getTenantCreditPoints()
        getDashboardDataList()

        return rootView
    }

    private fun getDashboardDataList() {
        val bookKeepingService: ApiInterface =
            ApiClient(appContext).provideService(ApiInterface::class.java)
        val apiCall: Observable<AgentBookKeepingResponse> =
            bookKeepingService.getBookKeepingInfo(startDate, endDate) //Test API Key

        RxAPICallHelper().call(apiCall, object : RxAPICallback<AgentBookKeepingResponse> {
            @SuppressLint("ResourceType")
            override fun onSuccess(response: AgentBookKeepingResponse) {
                if (response.responseCode == "200") {

                    val labels = ArrayList<String>()
                    labels.add("Balance")

                    barEntriesArrayList = ArrayList()
                    barEntriesArrayList.add(BarEntry(0f, abs(response.amount!!.balance)))

                    set1 = BarDataSet(barEntriesArrayList, "")

                    if (response.amount!!.balance < 0) {
                        set1!!.setColors(Color.parseColor(getString(R.color.colorLightRed)))
                    } else {
                        set1!!.setColors(Color.parseColor(getString(R.color.colorLightGreen)))
                    }

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
                    //xAxis.axisMinimum = data.xMin - .5f
                    xAxis.axisMinimum = data.xMin - .5f
                    xAxis.axisMaximum = data.xMax + .5f
                    barChart!!.xAxis.setDrawGridLines(false)
                    barChart!!.invalidate()
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

    @SuppressLint("CheckResult")
    private fun getTenantCreditPoints() {
        if (NetworkConnection.isNetworkConnected(appContext)) {
            requireActivity().window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

            val year = Calendar.getInstance().get(Calendar.YEAR)
        val credentials = GetCreditePointsTenant()
            credentials.tenantId = Kotpref.userId
            credentials.year = year.toLong()

            val creditPointsService: ApiInterface = ApiClient(appContext).provideService(ApiInterface::class.java)
        val apiCall: Observable<CreditPointResponse> = creditPointsService.getCreditPoint(credentials) //Test API Key

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
                if (it.data.size > 0) {
                    var creditPoints = 0.0f

                    for (item in it.data) {
                        creditPoints += item.amount!!.toFloat()
                    }

                    val text1 = "<font color=#000000>Credit Point: </font> <font color=#17AE66> " + creditPoints.toBigDecimal().setScale(2, RoundingMode.UP).toDouble() + "</font>"
                    credit_point!!.text = Html.fromHtml(text1)

                    if (creditPoints > 0) {
                        val cashValue = creditPoints / 12
                        val text2 = "<font color=#000000>Cash Value: </font> <font color=#17AE66> $" + cashValue.toBigDecimal().setScale(2, RoundingMode.UP).toDouble() + "</font>"
                        cash_value!!.text = Html.fromHtml(text2)
                    } else {
                        val text2 = "<font color=#000000>Cash Value: </font><font color=#17AE66>$0.00</font>"
                        cash_value!!.text = Html.fromHtml(text2)
                    }
                }
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            },
                { e ->
                    Log.e("onFailure", e.toString())

                    requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    e.message?.let {
                        Toast.makeText(appContext, it, Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            Toast.makeText(appContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = TenantDashboardChart2Fragment()
    /*.apply {
                arguments = Bundle().apply {
                    putSerializable("data", "data")
                }
            }*/
    }
}
