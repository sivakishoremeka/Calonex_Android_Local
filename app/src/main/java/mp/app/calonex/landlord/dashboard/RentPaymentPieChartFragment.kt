package mp.app.calonex.landlord.dashboard

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import io.reactivex.Observable
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.LandlordPaymentHistoryCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.landlord.response.LdDashboardRentPaymentResponse
import org.json.JSONException
import java.util.*

class RentPaymentPieChartFragment : Fragment() {
    private lateinit var chart: PieChart

    private var latePayment: String = "0"
    private var onTimePayment: String = "0"
    private var defaulted: String = "0"

    lateinit var appContext: Context


    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.occupancy_pie_chart_fragment, container, false)
        chart = rootView.findViewById(R.id.chart1)

        getRentPayment()

        return rootView
    }

    override fun onResume() {
        super.onResume()
        getRentPayment()
    }

    private fun getRentPayment() {

        if (NetworkConnection.isNetworkConnected(appContext)) {
            //Create retrofit Service
            val credentials = LandlordPaymentHistoryCredential()

            credentials.userCatalogId = Kotpref.userId
            val service: ApiInterface =
                ApiClient(appContext).provideService(ApiInterface::class.java)
            val apiCall: Observable<LdDashboardRentPaymentResponse> =
                service.getRentPaymentDetail(credentials)

            RxAPICallHelper().call(
                apiCall,
                object : RxAPICallback<LdDashboardRentPaymentResponse> {
                    override fun onSuccess(response: LdDashboardRentPaymentResponse) {

                        if (response.data != null) {
                            latePayment = response.data!!.latePayment
                            onTimePayment = response.data!!.onTimePayment
                            defaulted = response.data!!.defaulted
                            try {
                                if (latePayment == "0" && onTimePayment == "0" && onTimePayment == "0") {
//do nothing
                                } else
                                    setChart()
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }


                    }

                    override fun onFailed(t: Throwable) {

                    }
                })
        } else {
            Toast.makeText(appContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(JSONException::class)
    private fun setChart() {
        chart.setUsePercentValues(true)
        chart.getDescription().isEnabled = false
        chart.setExtraOffsets(5f, 10f, 5f, 5f)
        chart.setDragDecelerationFrictionCoef(0.95f)

        //chart.setCenterTextTypeface(tfLight);
        //chart.setCenterText(generateCenterSpannableText());
//        chart.setDrawHoleEnabled(false)
        chart.setHoleRadius(0.0f)
        chart.setHoleColor(Color.WHITE)
        chart.setTransparentCircleColor(Color.WHITE)
        chart.setTransparentCircleAlpha(110)
        chart.setHoleRadius(58f)
        chart.setTransparentCircleRadius(61f)
        chart.setDrawCenterText(true)
        chart.setRotationAngle(0f)
        // enable rotation of the chart by touch
        chart.setRotationEnabled(false)
        chart.setHighlightPerTapEnabled(false)
        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);


        // add a selection listener
        // chart.setOnChartValueSelectedListener(this);
        chart.animateY(1400, Easing.EaseInOutQuad)
        // chart.spin(2000, 0, 360);
        val l = chart.getLegend()
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.xEntrySpace = 7f
        l.yEntrySpace = 0f
        l.yOffset = 0f
        l.setCustom(entriesPie)

        // entry label styling
        chart.setEntryLabelColor(Color.WHITE)
        // chart.setEntryLabelTypeface(tfRegular);
        chart.setEntryLabelTextSize(12f)
        //setData()
    }

    @Throws(JSONException::class)
    private fun setData() {
//        val json_string = "{\n" + "  \"responseDto\": {\n        \"responseCode\": 0,\n        \"exceptionCode\": 0\n    },\n    \"data\": {\n        \"latePayment\": \"3\",\n        \"onTimePayment\": \"5\",\n        \"defaulted\": \"3\"\n    }\n}\n" + "}"
        val entries = ArrayList<PieEntry>()

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
//        val root = JSONObject(json_string)
//        val sys = root.getJSONObject("data")

        Log.e("Chard Data","onTimePayment==> "+onTimePayment.toFloat())
        Log.e("Chard Data","latePayment==> "+latePayment.toFloat())
        Log.e("Chard Data","defaulted==> "+defaulted.toFloat())

        entries.add(PieEntry(onTimePayment.toFloat(), ""))
        entries.add(PieEntry(latePayment.toFloat(), ""))
        entries.add(PieEntry(defaulted.toFloat(), ""))
        val dataSet = PieDataSet(entries, "")
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0F, 40F)
        dataSet.selectionShift = 5f
        //dataSet.setDrawValues(false)
        dataSet.setColors(
            Color.parseColor("#1b9d43"),
            Color.parseColor("#fa5838"),
            Color.parseColor("#005696")
        )


        //dataSet.setSelectionShift(0f);
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)
        //data.setValueTypeface(tfLight);
        chart.data = data

        // undo all highlights
        chart.highlightValues(null)
        chart.invalidate()
    }

    @get:Throws(JSONException::class)
    val entriesPie: ArrayList<LegendEntry>
        get() {

            Log.e("Chard Data","onTimePayment==> "+onTimePayment.toFloat())
            Log.e("Chard Data","latePayment==> "+latePayment.toFloat())
            Log.e("Chard Data","defaulted==> "+defaulted.toFloat())

            val colorList = ArrayList<Int>()
            colorList.add(Color.parseColor("#fa5838"))
            colorList.add(Color.parseColor("#1b9d43"))
            colorList.add(Color.parseColor("#005696"))
//            val json_string = "{\n" + "  \"responseDto\": {\n        \"responseCode\": 0,\n        \"exceptionCode\": 0\n    },\n    \"data\": {\n        \"latePayment\": \"3\",\n        \"onTimePayment\": \"5\",\n        \"defaulted\": \"3\"\n    }\n}\n" + "}"
            val entries = ArrayList<PieEntry>()
//            val root = JSONObject(json_string)
//            val sys = root.getJSONObject("data")
            entries.add(PieEntry(latePayment.toFloat(), "Late Payment"))
            entries.add(PieEntry(onTimePayment.toFloat(), "OnTime Payment"))
            entries.add(PieEntry(defaulted.toFloat(), "Defaulted"))

            val pieEntries = ArrayList<LegendEntry>()
            for (i in entries.indices) {
                val entry = LegendEntry()
                entry.formColor = colorList[i]
                entry.label = entries[i].label + "  " + entries[i].value.toInt()
                pieEntries.add(entry)
            }
            return pieEntries
        }

    companion object {
        @JvmStatic
        fun newInstance() =
            RentPaymentPieChartFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("data", "data")
                }
            }
    }
}