package mp.app.calonex.landlord.dashboard

import android.content.Context
import android.graphics.Color
import android.os.Bundle
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
import mp.app.calonex.landlord.response.LandlordOccupencyResponse
import org.json.JSONException
import java.util.*

class OcupancyPieChartFragment : Fragment() {
    private lateinit var chart: PieChart
    lateinit var appContext: Context

    private var vacant: String = "0"
    private var inactive: String = "0"
    private var occupiedOutsideCx: String = "0"
    private var occupiedInsideCx: String = "0"


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

        getOccupency()

        return rootView
    }

    override fun onResume() {
        super.onResume()
        getOccupency()
    }
    private fun getOccupency() {

        if (NetworkConnection.isNetworkConnected(appContext)) {
            //Create retrofit Service
            val credentials = LandlordPaymentHistoryCredential()

            credentials.userCatalogId = Kotpref.userId
            val service: ApiInterface =
                ApiClient(appContext).provideService(ApiInterface::class.java)
            val apiCall: Observable<LandlordOccupencyResponse> =
                service.getOccupancy(credentials)

            RxAPICallHelper().call(
                apiCall,
                object : RxAPICallback<LandlordOccupencyResponse> {
                    override fun onSuccess(response: LandlordOccupencyResponse) {

                        if (response.data != null) {
                            if (response.data!!.vacant.isNotEmpty())
                                vacant = response.data!!.vacant

                            if (response.data!!.inactive.isNotEmpty())
                                inactive = response.data!!.inactive

                            if (response.data!!.occupiedInsideCx.isNotEmpty())
                                occupiedInsideCx = response.data!!.occupiedInsideCx

                            if (response.data!!.occupiedOutsideCx.isNotEmpty())
                                occupiedOutsideCx = response.data!!.occupiedOutsideCx

                            try {

                                if(vacant.equals(0) && inactive.equals("0") && occupiedInsideCx.equals("0") && occupiedOutsideCx.equals("0"))
                                {
                                    //do nothing
                                }
                                else
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
        setData()
    }

    @Throws(JSONException::class)
    private fun setData() {
        val entries = ArrayList<PieEntry>()

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        entries.add(PieEntry(vacant.toFloat(), ""))
        entries.add(PieEntry(inactive.toFloat(), ""))
        entries.add(PieEntry(occupiedInsideCx.toFloat(), ""))
        entries.add(PieEntry(occupiedOutsideCx.toFloat(), ""))
        val dataSet = PieDataSet(entries, "")
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0F, 40F)
        dataSet.selectionShift = 5f

        // dataSet.setDrawValues(false)
        dataSet.setColors(
            Color.parseColor("#207cca"),
            Color.parseColor("#fa5838"),
            Color.parseColor("#1b9d43"),
            Color.parseColor("#8a39cf")
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
            val colorList = ArrayList<Int>()
            colorList.add(Color.parseColor("#207cca"))
            colorList.add(Color.parseColor("#fa5838"))
            colorList.add(Color.parseColor("#1b9d43"))
            colorList.add(Color.parseColor("#8a39cf"))
            val entries = ArrayList<PieEntry>()
            entries.add(PieEntry(vacant.toFloat(), "vacant"))
            entries.add(PieEntry(inactive.toFloat(), "inactive"))
            entries.add(PieEntry(occupiedInsideCx.toFloat(), "occupied inside cx"))
            entries.add(PieEntry(occupiedOutsideCx.toFloat(), "occupied outside cx"))
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
            OcupancyPieChartFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("data", "data")
                }
            }
    }
}