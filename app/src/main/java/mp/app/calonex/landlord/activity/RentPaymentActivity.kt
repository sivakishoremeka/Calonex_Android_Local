package mp.app.calonex.landlord.activity

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_applicant_tenant.*
import kotlinx.android.synthetic.main.activity_rent_payment.*
import kotlinx.android.synthetic.main.activity_rent_payment.iv_back
import kotlinx.android.synthetic.main.activity_rent_payment.profile_pic
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.FetchDocumentCredential
import mp.app.calonex.common.apiCredentials.LandlordPaymentHistoryCredential
import mp.app.calonex.common.apiCredentials.NotificationCredential
import mp.app.calonex.common.apiCredentials.UserDetailCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.model.AppNotifications
import mp.app.calonex.landlord.model.FetchDocumentModel
import mp.app.calonex.landlord.model.NotificationLinkRequestModel
import mp.app.calonex.landlord.model.UserDetail
import mp.app.calonex.landlord.response.AppNotificationResponse
import mp.app.calonex.landlord.response.FetchDocumentResponse
import mp.app.calonex.landlord.response.LdDashboardRentPaymentResponse
import mp.app.calonex.landlord.response.UserDetailResponse
import org.json.JSONException
import java.util.ArrayList

class RentPaymentActivity : AppCompatActivity() {
    private var layoutLphNotify: RelativeLayout? = null
    private var txtLphNotify: TextView? = null

    var userDetailResponse = UserDetail()
    var listUserImages = ArrayList<FetchDocumentModel>()
    lateinit var appContext: Context
    var appNotifications = ArrayList<AppNotifications>()
    var mNotificationLinkRequest = NotificationLinkRequestModel()
    var linkRequestList = ArrayList<AppNotifications>()
    var alertsList = ArrayList<AppNotifications>()
    var messageList = ArrayList<AppNotifications>()
    private lateinit var chart: PieChart

    private var latePayment: String = "0"
    private var onTimePayment: String = "0"
    private var defaulted: String = "0"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rent_payment)
        chart = findViewById(R.id.chart1)
        layoutLphNotify = findViewById(R.id.layout_lp_notify)
        txtLphNotify = findViewById(R.id.txt_lp_notify)
        appContext = this@RentPaymentActivity
        iv_back.setOnClickListener {
            onBackPressed()
        }
        setData()
        fetchImages()
        getUserInfo()
        getRentPayment()
        getNotificationList()

    }

    override fun onBackPressed() {
        finish()
    }

    private fun getUserInfo() {
        //Create retrofit Service

        val credentials = UserDetailCredential()

        credentials.userId = Kotpref.userId
        val userDetailService: ApiInterface =
            ApiClient(this).provideService(ApiInterface::class.java)
        val apiCall: Observable<UserDetailResponse> =
            userDetailService.getUserDetail(credentials) //Test API Key

        RxAPICallHelper().call(apiCall, object : RxAPICallback<UserDetailResponse> {
            override fun onSuccess(userDetail: UserDetailResponse) {
                userDetailResponse = userDetail.data!!
                setUserInfo()
            }

            override fun onFailed(t: Throwable) {
                // show error
                Log.e("onFailure", t.toString())
                //  progressBarList?.visibility = View.GONE
            }
        })
    }

    private fun getRentPayment() {

        if (NetworkConnection.isNetworkConnected(this)) {
            //Create retrofit Service
            val credentials = LandlordPaymentHistoryCredential()

            credentials.userCatalogId = Kotpref.userId
            val service: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)
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
                                if (latePayment.equals("0") && onTimePayment.equals("0") && onTimePayment.equals(
                                        "0"
                                    )
                                ) {
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
            Toast.makeText(this, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(JSONException::class)
    private fun setChart() {
        chart.setUsePercentValues(true)
        chart.centerText = "Rent Payment"
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
        chart.holeRadius = 75f



        chart.legend.isEnabled = false


        setData()
    }

    @Throws(JSONException::class)
    private fun setData() {
//        val json_string = "{\n" + "  \"responseDto\": {\n        \"responseCode\": 0,\n        \"exceptionCode\": 0\n    },\n    \"data\": {\n        \"latePayment\": \"3\",\n        \"onTimePayment\": \"5\",\n        \"defaulted\": \"3\"\n    }\n}\n" + "}"
        val entries = ArrayList<PieEntry>()

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
//        val root = JSONObject(json_string)
//        val sys = root.getJSONObject("data")

        Log.e("Chard Data", "onTimePayment==> " + onTimePayment.toFloat())
        Log.e("Chard Data", "latePayment==> " + latePayment.toFloat())
        Log.e("Chard Data", "defaulted==> " + defaulted.toFloat())

        var total = onTimePayment.toFloat() +
                latePayment.toFloat() +
                defaulted.toFloat()

//        ontimepay.text = (25.toFloat()*100/total).toString() + " %"
        ontimepay.text = (onTimePayment.toFloat()*100/total).toString() + " %"
//        latepayment.text = (10.toFloat()*100/total).toString() + " %"
        latepayment.text = (latePayment.toFloat()*100/total).toString() + " %"
//        defaultedtxt.text = (15.toFloat()*100/total).toString() + " %"
        defaultedtxt.text = (defaulted.toFloat()*100/total).toString() + " %"

        ontimepaytitle.text = "On-Time Pay"
        latepaymenttitle.text = "Late Payment"
        defaultedtitle.text = "Defaulted"

        //if (onTimePayment.toFloat()>0.0){
//        entries.add(PieEntry(25.toFloat(), ""))
        entries.add(PieEntry(onTimePayment.toFloat(), ""))
        //}
        // if (latePayment.toFloat()>0.0){
//        entries.add(PieEntry(10.toFloat(), ""))
        entries.add(PieEntry(latePayment.toFloat(), ""))
        // }
        // if (defaulted.toFloat()>0.0){
        entries.add(PieEntry(defaulted.toFloat(), ""))
//        entries.add(PieEntry(15.toFloat(), ""))
        // }


        val dataSet = PieDataSet(entries, "")
        dataSet.setDrawIcons(false)

        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0F, 40F)
        dataSet.selectionShift = 5f
        //dataSet.setDrawValues(false)
        dataSet.setColors(
            Color.parseColor("#FD2254"),
            Color.parseColor("#00B7FE"),
            Color.parseColor("#FFC531")
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


    private fun setUserInfo() {

        var profileImg: String = ""
        val customPb: CircularProgressDrawable = Util.customProgress(this)
        for (item in listUserImages) {

            if (item.uploadFileType.equals(getString(R.string.key_profile_pic))) {
                profileImg = item.fileName
                break
            }
        }
        Log.e("imageUrl", "" + getString(R.string.base_url_img2) + profileImg)

        if (!profileImg.isNullOrEmpty()) {

            Kotpref.profile_image = getString(R.string.base_url_img2) + profileImg

            Log.e("imageUrl", "" + R.string.base_url_img2 + profileImg)
            Glide.with(appContext)
                .load(getString(R.string.base_url_img2) + profileImg)
                .placeholder(customPb)
                .into(profile_pic!!)
        }
    }


    private fun fetchImages() {
        if (NetworkConnection.isNetworkConnected(appContext)) {

            val credentials = FetchDocumentCredential()
            credentials.userId = Kotpref.userId

            val signatureApi: ApiInterface =
                ApiClient(appContext).provideService(ApiInterface::class.java)
            val apiCall: Observable<FetchDocumentResponse> =
                signatureApi.fetchDocument(credentials)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", it.responseDto!!.responseDescription.toString())

                    if (it.responseDto!!.responseCode.equals(200) && it.data != null) {
                        listUserImages = it.data!!

                    } else {
                        Toast.makeText(appContext, it.responseDto!!.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                },
                    { e ->
                        Log.e("onFailure", e.toString())

                        e.message?.let {
                            Toast.makeText(appContext, it, Toast.LENGTH_SHORT).show()
                        }
                    })
        } else {
            Toast.makeText(appContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
        }
    }

    private fun getNotificationList() {

        this@RentPaymentActivity.getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        val credentials = NotificationCredential()

        credentials.userCatalog = Kotpref.userId
        val notifyListService: ApiInterface =
            ApiClient(appContext).provideService(ApiInterface::class.java)
        val apiCall: Observable<AppNotificationResponse> =
            notifyListService.getAppNotifications(credentials) //Test API Key 3600248I

        RxAPICallHelper().call(apiCall, object : RxAPICallback<AppNotificationResponse> {
            override fun onSuccess(response: AppNotificationResponse) {
                Log.e("onSuccess", response.responseCode.toString())

                if (!response.data?.isEmpty()!!) {
                    appNotifications = response.data!!

                    for (i in 0..appNotifications.size - 1) {
                        val gson = Gson()

                        if (appNotifications[i].activityType == "1") {
                            val objectList = gson.fromJson(
                                appNotifications[i].notificationData,
                                NotificationLinkRequestModel::class.java
                            )
                            mNotificationLinkRequest = objectList
                            linkRequestList.add(appNotifications[i])
                        } else if (appNotifications[i].activityType == "2") {
                            alertsList.add(appNotifications[i])
                        } else if (appNotifications[i].activityType == "3") {
                            messageList.add(appNotifications[i])
                        }
                    }
                }

                Kotpref.notifyCount = (linkRequestList.size + alertsList.size).toString()
                txtLphNotify!!.text = Kotpref.notifyCount
                //todo change class name
                //(activity as HomeActivityAgent?)!!.addBadgeNew(messageList.size.toString())

                this@RentPaymentActivity!!.getWindow()
                    .clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                if (Integer.parseInt(Kotpref.notifyCount) < 1) {
                    txtLphNotify!!.visibility = View.GONE

                } else {
                    txtLphNotify!!.visibility = View.VISIBLE

                }
            }

            override fun onFailed(throwable: Throwable) {
                // show error
                Log.e("onFailure", throwable.toString())

                this@RentPaymentActivity!!.getWindow()
                    .clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                txtLphNotify!!.text =
                    (linkRequestList.size + alertsList.size).toString()
                if (Integer.parseInt(Kotpref.notifyCount) < 1) {
                    txtLphNotify!!.visibility = View.GONE

                } else {
                    txtLphNotify!!.visibility = View.VISIBLE

                }
            }
        })
    }


}