package mp.app.calonex.broker


import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import io.reactivex.Observable
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.LandlordPaymentHistoryCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.landlord.model.BrokerBookOfBusinessOfAgent
import mp.app.calonex.landlord.response.BrokerBookOfBusinessOfAgentResponse
import org.json.JSONException
import java.util.*


class BookOfBusinessAgentChartFragment : Fragment() {
    private var mChart: HorizontalBarChart? = null
    private var headerTv1: TextView? = null
    private var headerTv2: TextView? = null
    lateinit var appContext: Context
    private var header1fragment: TextView? = null
    private var showTopTenTextView: TextView? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.brokar_graph_fragment, container, false)
        mChart = rootView.findViewById(R.id.horizontal_chart)
        headerTv1 = rootView.findViewById(R.id.header_tv1)
        headerTv2 = rootView.findViewById(R.id.header_tv2)
        getBookOfBusinessOfAgent()
        return rootView
    }

    private fun getBookOfBusinessOfAgent() {

        if (NetworkConnection.isNetworkConnected(appContext)) {
            //Create retrofit Service
            val credentials = LandlordPaymentHistoryCredential()

            credentials.userCatalogId = Kotpref.userId
            val service: ApiInterface =
                ApiClient(appContext).provideService(ApiInterface::class.java)
            val apiCall: Observable<BrokerBookOfBusinessOfAgentResponse> =
                service.getBookOfBusinessOfAgent(credentials)

            RxAPICallHelper().call(
                apiCall,
                object : RxAPICallback<BrokerBookOfBusinessOfAgentResponse> {
                    override fun onSuccess(response: BrokerBookOfBusinessOfAgentResponse) {

                        if (response.data != null) {
                            Log.e("Book Of Business Agent", "Success")
                            setChart(response.data!!)
                        }


                    }

                    override fun onFailed(t: Throwable) {
                        Log.e("Book Of Business Agent", "Failed" + t.toString())
                    }
                })
        } else {
            Toast.makeText(appContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
        }
    }
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            showTopTenTextView!!.visibility = View.VISIBLE
            header1fragment!!.text = "Book of Business of Agent"
            showTopTenTextView!!.text = "Showing Top 10"

        } else {
        }
    }


    @Throws(JSONException::class)
    private fun setChart(response: ArrayList<BrokerBookOfBusinessOfAgent>) {
        headerTv1!!.text = "Agent Name"
        headerTv2!!.text = "Onboarded Properties/Units"

        val labels = ArrayList<String>()
        val yVals1 = ArrayList<BarEntry>()

        for (item in response){
            labels.add(item.agentName)
        }

        /*  for (i in 0 until response.size) {
              yVals1.add(BarEntry(i.toFloat(), response[i].unitNumber.toFloat()))

              // yVals1.add(BarEntry(i, (i + 1) * 10))
          }*/
        for (i in 0 until response.size) {
            yVals1.add(BarEntry((response.size - i).toFloat() -1, response[i].unitNumber.toFloat()))
        }
//        yVals1.add(BarEntry(0f, 28f))
//        yVals1.add(BarEntry(1f, 10f))
//        yVals1.add(BarEntry(2f, 23f))
//        yVals1.add(BarEntry(3f, 6f))
//        yVals1.add(BarEntry(4f, 50f))
//        yVals1.add(BarEntry(5f, 2f))

//        labels.add("rizwan kumar")
//        labels.add("John Smith")
//        labels.add("David guetta")
//        labels.add("Linda clark")
//        labels.add("John doe")
//        labels.add("John wick")
        mChart!!.setDrawBarShadow(false)
        mChart!!.setDrawValueAboveBar(true)
        mChart!!.description.isEnabled = false
        mChart!!.setPinchZoom(false)
        mChart!!.setDrawGridBackground(false)
        mChart!!.setScaleEnabled(false)
        mChart!!.setExtraOffsets(0f, 0f, 0f, 30f)

        val xl = mChart!!.xAxis
        xl.position = XAxis.XAxisPosition.BOTTOM
        xl.setDrawAxisLine(true)
        xl.setDrawGridLines(false)
        xl.granularity = 1f
        mChart!!.xAxis.valueFormatter = IndexAxisValueFormatter(labels.asReversed())
        //mChart!!.getXAxis().setSpaceMax(1F);
        val yl = mChart!!.axisLeft
        yl.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
        yl.setDrawGridLines(false)
        yl.isEnabled = false
        yl.axisMinimum = 0f
        val yr = mChart!!.axisRight
        yr.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
        yr.setDrawGridLines(false)
        yr.axisMinimum = 0f
        yr.isEnabled = false


        val set1: BarDataSet
        set1 = BarDataSet(yVals1, "DataSet 1")
        set1.color = Color.parseColor("#3771DA")
        val dataSets = ArrayList<IBarDataSet>()
        dataSets.add(set1)
        val data = BarData(dataSets)
        data.setValueTextSize(10f)
        data.barWidth = 0.6f
        data.isHighlightEnabled = false
        mChart!!.data = data
        mChart!!.legend.isEnabled = false
        mChart!!.invalidate()
        data.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return (value.toInt()).toString()
            }
        })
        val barChartRender =
            RoundedHorizontalBarChartRenderer(mChart, mChart!!.getAnimator(), mChart!!.getViewPortHandler())
        barChartRender.setmRadius(10f)
        barChartRender.initBuffers()
        mChart!!.setRenderer(barChartRender)

    }

    companion object {
        private val TAG = BookOfBusinessAgentChartFragment::class.java.name
        @JvmStatic
        fun newInstance(header1: TextView?, showTopTenTextView: TextView?) =
            BookOfBusinessAgentChartFragment().apply {
                this.header1fragment = header1
                this.showTopTenTextView = showTopTenTextView

            }
    }

}
