package mp.app.calonex.broker


import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
import mp.app.calonex.agent.activity.LinkPropertyAgent
import mp.app.calonex.common.apiCredentials.LandlordPaymentHistoryCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.AccountLinkDetailScreen
import mp.app.calonex.landlord.model.BrokerAllProperties
import mp.app.calonex.landlord.response.BrokerDashboardAllPropertiesResponse
import org.json.JSONException


open class AllPropertiesChartFragment : Fragment() {
    private var mChart: HorizontalBarChart? = null
    private var headerTv1: TextView? = null
    private var headerTv2: TextView? = null
    private var refresh: TextView? = null
    private var header1Frgamnet: TextView? = null
    private var showTopTenTextView: TextView? = null
    private var layout_add_property: LinearLayout?=null

    lateinit var appContext: Context

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
        refresh = rootView.findViewById(R.id.refresh_all_properties)
        layout_add_property=rootView.findViewById(R.id.layout_add_property);

        layout_add_property!!.visibility = View.VISIBLE
        layout_add_property!!.setOnClickListener {
            if (!Kotpref.bankAdded) {
                bankAddDialog()
            }else if (!Kotpref.bankAccountVerified) {
                bankVerifyDialog()
            }else {
                val intent = Intent(appContext, LinkPropertyAgent::class.java)
                startActivity(intent)
            }
        }

        refresh!!.setOnClickListener {
            getAllProperties()
        }

        getAllProperties()

        return rootView
    }

    private fun bankAddDialog() {
        val mBuilder = AlertDialog.Builder(requireContext())
        mBuilder.setTitle("Error")
        mBuilder.setMessage("Add your bank account to get payment directly to your bank account.")
        mBuilder.setPositiveButton("Ok") { dialog, which ->
            dialog.dismiss()
            Util.navigationNext(requireActivity(), AccountLinkDetailScreen::class.java)
        }

        mBuilder.show()
    }
    private fun bankVerifyDialog() {
        val mBuilder = AlertDialog.Builder(requireContext())
        mBuilder.setTitle("Error")
        mBuilder.setMessage("Your bank account is still pending for verification, We're not able to charge you until you verify your bank account, Please check your bank account to get verification amount.")
        mBuilder.setPositiveButton("Ok") { dialog, which ->
            dialog.dismiss()
            Util.navigationNext(requireActivity(), AccountLinkDetailScreen::class.java)
        }

        mBuilder.show()
    }

    private fun getAllProperties() {

        if (NetworkConnection.isNetworkConnected(appContext)) {
            //Create retrofit Service
            val credentials = LandlordPaymentHistoryCredential()

            credentials.userCatalogId = Kotpref.userId
            val service: ApiInterface =
                ApiClient(appContext).provideService(ApiInterface::class.java)
            val apiCall: Observable<BrokerDashboardAllPropertiesResponse> =
                service.getAllProperties(credentials)

            RxAPICallHelper().call(
                apiCall,
                object : RxAPICallback<BrokerDashboardAllPropertiesResponse> {
                    override fun onSuccess(response: BrokerDashboardAllPropertiesResponse) {

                        if (response.data != null) {
                            Log.e("All Ai Response", "Success")
                            refresh!!.visibility = View.GONE
                            setChart(response.data!!)
                        }


                    }

                    override fun onFailed(t: Throwable) {
                        refresh!!.visibility = View.VISIBLE

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
            header1Frgamnet!!.text = "All Properties"
            showTopTenTextView!!.text = "Showing Top 10"
        }
    }

    @Throws(JSONException::class)
    private fun setChart(response: ArrayList<BrokerAllProperties>) {
        headerTv1!!.text = "Property Address"
        headerTv2!!.text = "No. of units Rented"
        val labels = ArrayList<String>()
        for (item in response) {
            labels.add(item.propertyAddress)
        }

        mChart!!.setDrawBarShadow(false)
        mChart!!.setDrawValueAboveBar(true)
        mChart!!.description.isEnabled = false
        mChart!!.setPinchZoom(false)
        mChart!!.setDrawGridBackground(false)
        mChart!!.setScaleEnabled(false)
        mChart!!.setExtraOffsets(0f, 0f, 0f, 40f)
        val xl = mChart!!.xAxis
        xl.position = XAxis.XAxisPosition.BOTTOM
        xl.setDrawAxisLine(true)
        xl.setDrawGridLines(false)
        xl.granularity = 1f
        xl.labelCount = labels.size
        mChart!!.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
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
        /* val yVals1 = LinkedList<BarEntry>()
         for (i in 0 until response.size) {
             yVals1.add(BarEntry(i.toFloat(), response[i].numberOfUnitsRented.toFloat()))
         }*/

        val yVals1 = ArrayList<BarEntry>()
        for (i in 0 until response.size) {
            yVals1.add(
                BarEntry(
                    (response.size - i).toFloat() - 1,
                    response[i].numberOfUnitsRented.toFloat()
                )
            )
        }
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
        mChart!!.invalidate()
        mChart!!.legend.isEnabled = false
        data.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return (value.toInt()).toString()
            }
        })
        val barChartRender =
            RoundedHorizontalBarChartRenderer(
                mChart,
                mChart!!.animator,
                mChart!!.viewPortHandler
            )
        barChartRender.setmRadius(10f)
        barChartRender.initBuffers()
        mChart!!.renderer = barChartRender
    }

    companion object {
        //private val TAG = AllPropertiesChartFragment::class.java.name

        @JvmStatic
        fun newInstance(header1: TextView?, showTopTenTextView: TextView?) =
            AllPropertiesChartFragment().apply {
                this.header1Frgamnet = header1
                this.showTopTenTextView = showTopTenTextView
            }

    }
}
