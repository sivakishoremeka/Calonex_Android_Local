package mp.app.calonex.agent.activity

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.jaredrummler.materialspinner.MaterialSpinner
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_bookkeeping_agent.*
import kotlinx.android.synthetic.main.dashboard_property_list_item.*
import mp.app.calonex.R
import mp.app.calonex.agent.adapter.AgentBookKeepingAdapter
import mp.app.calonex.agent.model.AgentBookKeepingUser
import mp.app.calonex.agent.model.MontlyRevenueDTO
import mp.app.calonex.agent.model.filtered
import mp.app.calonex.agent.responce.AgentBookKeepingResponse
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.Util
import mp.app.calonex.common.utility.Util.navigationNext
import mp.app.calonex.landlord.activity.CxBaseActivity2
import mp.app.calonex.landlord.activity.NotifyScreen
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.ArrayList


class BookKeepingListActivityAgent : CxBaseActivity2() {
    var selectedMonth = 0
    var startDateFromDialog = false
    var endDateFromDialog = false

    private lateinit var appContext: Context
    private var type: MaterialSpinner? = null
    private var dateRange: MaterialSpinner? = null

    private var ldPropertyListAdapter: AgentBookKeepingAdapter? = null

    private var refreshBookKeeping: SwipeRefreshLayout? = null
    private var layoutLphNotify: RelativeLayout? = null
    private var txtLphNotify: TextView? = null

    private var editStartDate: TextView? = null
    private var editEndDate: TextView? = null
    private var txtSearchButton: TextView? = null
    private var txtTotalEarning: TextView? = null
    private var txtTotalExpance: TextView? = null
    private var txtTotalBalance: TextView? = null
    private var txtPlatformEarn: TextView? = null
    private var txtPlatformExpance: TextView? = null
    private var txtAddButton: TextView? = null
    private var cal = Calendar.getInstance()
    private var year = cal.get(Calendar.YEAR)
    private var month = cal.get(Calendar.MONTH)
    private var day = cal.get(Calendar.DAY_OF_MONTH)
    val myFormat = "MMM dd, yyyy"

    var startDate = "2020-01-01"
    var endDate = ""

    var startDateFlag: Boolean = false

    var chart: BarChart? = null

    //var fromTenant: Boolean = false

    // .toBigDecimal().setScale(2, RoundingMode.UP).toDouble() // 7928330719

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookkeeping_agent)

        appContext = this@BookKeepingListActivityAgent

        refreshBookKeeping = findViewById(R.id.refresh_ld_payment)
        layoutLphNotify = findViewById(R.id.layout_notify)
        txtLphNotify = findViewById(R.id.txt_lph_notify)
        editStartDate = findViewById(R.id.edit_start_date)
        editEndDate = findViewById(R.id.edit_end_date)
        txtSearchButton = findViewById(R.id.search_button)
        txtTotalEarning = findViewById(R.id.total_earning)
        txtTotalExpance = findViewById(R.id.total_expenses)
        txtTotalBalance = findViewById(R.id.total_balance)
        txtPlatformEarn = findViewById(R.id.platform_earning)
        txtPlatformExpance = findViewById(R.id.platform_expence)
        txtAddButton = findViewById(R.id.add_button)
        chart = findViewById(R.id.barchart);
        chart!!.axisLeft.setDrawGridLines(false);
        chart!!.axisRight.setDrawGridLines(false);
        chart!!.getXAxis().setDrawGridLines(false);
        chart!!.setVisibleXRangeMaximum(15F)

        val yAxisRight: YAxis = chart!!.getAxisRight()
        val yAxisLeft: YAxis = chart!!.axisLeft
        yAxisRight.isEnabled = true
        yAxisRight.setStartAtZero(false);
        yAxisLeft.setStartAtZero(false);
        chart!!.getDescription().setEnabled(false);




        type = findViewById<View>(R.id.type) as MaterialSpinner
        dateRange = findViewById<View>(R.id.daterange) as MaterialSpinner
        type!!.setItems("All", "Earnings", "Expense");
        dateRange!!.setItems("1 month", "3 month", "1 year", "Custom");

        type!!.setOnItemSelectedListener(MaterialSpinner.OnItemSelectedListener<String> { view, position, id, item ->
            var datalist =
                list.stream().filter(({ i -> i.type.equals(item.toString(), true) })).collect(
                    Collectors.toList()
                );
            if (position == 0) {
                setList(list)
            } else {
                setList(datalist as ArrayList<AgentBookKeepingUser>)

            }


        })
        dateRange!!.setOnItemSelectedListener(MaterialSpinner.OnItemSelectedListener<String> { view, position, id, item ->
            val currentDate: LocalDate = LocalDate.now()
            selectedMonth = position
            startDateFromDialog = false
            endDateFromDialog = false
            if (position == 0) {

                val formatter2 = DateTimeFormat.forPattern("yyyy-MM-dd")
                val enddate = currentDate.toString(formatter2)
                endDate = enddate
                startDate = currentDate.minusMonths(1).toString(formatter2)
            }
            if (position == 1) {

                val formatter2 = DateTimeFormat.forPattern("yyyy-MM-dd")
                val enddate = currentDate.toString(formatter2)
                endDate = enddate
                startDate = currentDate.minusMonths(3).toString(formatter2)
            }
            if (position == 2) {
                val formatter2 = DateTimeFormat.forPattern("yyyy-MM-dd")
                val enddate = currentDate.toString(formatter2)
                endDate = enddate
                startDate = currentDate.minusMonths(12).toString(formatter2)
            }

            if (position != 3) {
                getBookKeepingList()
            } else {
                openDialog()
            }

        })


        startHandler()


        /*val block_text_1: TextView = findViewById(R.id.block_text_1)
        val block_text_2: TextView = findViewById(R.id.block_text_2)
        val scroll_1: HorizontalScrollView = findViewById(R.id.scroll_1)
        val block_text_3: TextView = findViewById(R.id.block_text_3)
        val block_text_4: TextView = findViewById(R.id.block_text_4)
        val layout_1: LinearLayout = findViewById(R.id.layout_1)
        val layout_2: RelativeLayout = findViewById(R.id.layout_2)*/

        /*fromTenant = intent.getBooleanExtra("fromTenant", false)

        if (fromTenant) {
            block_text_1.visibility = View.INVISIBLE
            block_text_2.visibility = View.INVISIBLE
            scroll_1.visibility = View.INVISIBLE
            block_text_3.visibility = View.INVISIBLE
            block_text_4.visibility = View.INVISIBLE
            layout_1.visibility = View.INVISIBLE
            layout_2.visibility = View.INVISIBLE
        }*/

        val todayDate = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val todayString = formatter.format(todayDate)

        startDate = "2020-01-01"
        endDate = todayString

        Log.e("DateNow", endDate)

        txtAddButton!!.setOnClickListener {
            navigationNext(this, BookKeepingItemEditActivityAgent::class.java)
        }

        refreshBookKeeping?.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                appContext,
                R.color.colorPrimary
            )
        )

        header_back.setOnClickListener {
            super.onBackPressed()
            Util.navigationBack(this)
        }

        if (Kotpref.profile_image.isNotEmpty()) {
            Glide.with(appContext)
                .load(Kotpref.profile_image)
                .placeholder(R.drawable.profile_default_new)
                .into(profile_pic!!)
        }

        refreshBookKeeping?.setColorSchemeColors(Color.WHITE)
        refreshBookKeeping?.setOnRefreshListener {

            getBookKeepingList()
            refreshBookKeeping?.isRefreshing = false
        }

        layoutLphNotify!!.setOnClickListener {
            Util.navigationNext(this@BookKeepingListActivityAgent, NotifyScreen::class.java)
        }

        //Util.setEditReadOnly(editStartDate!!, true, InputType.TYPE_NULL)
        // Util.setEditReadOnly(editEndDate!!, true, InputType.TYPE_NULL)

        editStartDate?.setOnClickListener {
            val dateDialog = DatePickerDialog(
                this,
                { view, year, monthOfYear, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    editStartDate!!.setText(sdf.format(cal.time))
                    startDateFlag = true

                    val formatter2 = SimpleDateFormat("yyyy-MM-dd")
                    val todayString2 = formatter2.format(cal.time)
                    startDate = todayString2
                },
                year,
                month,
                day
            )
            dateDialog.show()
            val cal1 = Calendar.getInstance()
            cal1.add(Calendar.DAY_OF_MONTH, 0)
            dateDialog.datePicker.maxDate = cal1.timeInMillis
            editStartDate!!.error = null
        }

        editEndDate?.setOnClickListener {
            if (startDateFlag) {
                val dateDialog = DatePickerDialog(
                    this,
                    { view, year, monthOfYear, dayOfMonth ->
                        cal.set(Calendar.YEAR, year)
                        cal.set(Calendar.MONTH, monthOfYear)
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                        val sdf = SimpleDateFormat(myFormat, Locale.US)
                        editEndDate!!.setText(sdf.format(cal.time))

                        val formatter3 = SimpleDateFormat("yyyy-MM-dd")
                        val todayString3 = formatter3.format(cal.time)
                        Log.e("DateNow 3", todayString3)
                        endDate = todayString3
                    },
                    year,
                    month,
                    day
                )
                dateDialog.show()
                val cal1 = Calendar.getInstance()
                cal1.add(Calendar.DAY_OF_MONTH, 0)
                dateDialog.datePicker.maxDate = cal1.timeInMillis
                editEndDate!!.error = null
            } else {
                Toast.makeText(appContext, "Select Start date first.", Toast.LENGTH_SHORT).show()
            }
        }

        Log.e("DateNow", endDate)

        txtSearchButton!!.setOnClickListener {

            Log.d("TAG", "onCreate: start date " + startDate)
            if (startDate < endDate) {

                getBookKeepingList()

            } else
                Toast.makeText(
                    applicationContext,
                    "End date should always grater then start date",
                    Toast.LENGTH_SHORT
                ).show()

        }
    }

    override fun onStart() {
        super.onStart()

        if (Kotpref.notifyCount != null && !Kotpref.notifyCount.isEmpty() && Integer.parseInt(
                Kotpref.notifyCount
            ) > 0
        ) {
            txtLphNotify!!.text = Kotpref.notifyCount
            txtLphNotify!!.visibility = View.VISIBLE
        } else {
            txtLphNotify!!.visibility = View.GONE
        }


    }

    override fun onResume() {
        super.onResume()
        //getBookKeepingList()
        getBookKeepingList()

    }

    private fun getBookKeepingList() {
        val bookKeepingService: ApiInterface =
            ApiClient(this).provideService(ApiInterface::class.java)
        val apiCall: Observable<AgentBookKeepingResponse> =
            bookKeepingService.getBookKeepingInfo(startDate, endDate) //Test API Key

        RxAPICallHelper().call(apiCall, object : RxAPICallback<AgentBookKeepingResponse> {
            override fun onSuccess(response: AgentBookKeepingResponse) {
                if (response.responseCode == "200") {
                    txtTotalEarning!!.text = "$" + response.amount!!.totalEarnings
                    txtTotalExpance!!.text = "$" + response.amount!!.totalExpenses
                    txtTotalBalance!!.text = "$" + response.amount!!.balance

                    var platformEarn = 0.0f
                    var platformExpance = 0.0f

                    for (item in response.fromSystem!!) {
                        if (item.type == "expense") {
                            platformExpance += item.amount
                        } else {
                            platformEarn += item.amount
                        }
                    }

                    txtPlatformEarn!!.text =
                        "$" + platformEarn.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
                    txtPlatformExpance!!.text =
                        "$" + platformExpance.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()

                    setList(response.userAdded!!)
                    showChart(response)
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

    private fun getBook() {
        val bookKeepingService: ApiInterface =
            ApiClient(this).provideService(ApiInterface::class.java)
        val apiCall: Observable<AgentBookKeepingResponse> =
            bookKeepingService.getBookKeepingInfo(startDate, endDate) //Test API Key

        RxAPICallHelper().call(apiCall, object : RxAPICallback<AgentBookKeepingResponse> {
            override fun onSuccess(response: AgentBookKeepingResponse) {
                if (response.responseCode == "200") {
                    txtTotalEarning!!.text = "$" + response.amount!!.totalEarnings
                    txtTotalExpance!!.text = "$" + response.amount!!.totalExpenses
                    txtTotalBalance!!.text = "$" + response.amount!!.balance

                    var platformEarn = 0.0f
                    var platformExpance = 0.0f

                    for (item in response.fromSystem!!) {
                        if (item.type == "expense") {
                            platformExpance += item.amount
                        } else {
                            platformEarn += item.amount
                        }
                    }

                    txtPlatformEarn!!.text =
                        "$" + platformEarn.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
                    txtPlatformExpance!!.text =
                        "$" + platformExpance.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()

                    setList(response.userAdded!!)
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

    private fun setList(rootList: ArrayList<AgentBookKeepingUser>) {
        rv_ld_payments?.layoutManager = LinearLayoutManager(appContext)
        list = rootList
        val listPayment = ArrayList(list!!.reversed())
        ldPropertyListAdapter = AgentBookKeepingAdapter(
            appContext,
            this@BookKeepingListActivityAgent,
            listPayment
        )
        rv_ld_payments?.adapter = ldPropertyListAdapter
    }


    fun showChart(response: AgentBookKeepingResponse) {
        if (selectedMonth == 0) {
            showBarChart(
                response.amount!!.totalEarnings,
                response.amount!!.totalExpenses,
                response.amount!!.balance
            )
        } else if (selectedMonth == 1) {
            showBarChart3Month(
                response.amount!!.totalEarnings,
                response.amount!!.totalExpenses,
                response.amount!!.balance
            )
        } else {
            showBarChart1Year(response.montlyRevenueDTO!!)
        }
    }

    private fun showBarChart(totalearning: Float, totalExpenses: Float, totalBalance: Float) {
        chart!!.data = null
        var earningEntry: ArrayList<BarEntry> = ArrayList()
        var expenseEntry: ArrayList<BarEntry> = ArrayList()
        var balanceEntry: ArrayList<BarEntry> = ArrayList()

        earningEntry.add(BarEntry(0f, totalearning))
        expenseEntry.add(BarEntry(0f, totalExpenses))
        balanceEntry.add(BarEntry(0f, totalBalance))

        val earningDataSet = BarDataSet(earningEntry, "Earning")
        earningDataSet.color = Color.parseColor("#FFAE00")
        val expensesDataSet = BarDataSet(expenseEntry, "Expense")
        expensesDataSet.setColors(Color.parseColor("#02B717"))
        val balanceDataSet = BarDataSet(balanceEntry, "Balance")
        balanceDataSet.setColors(Color.parseColor("#006CFF"))
        var data = BarData(earningDataSet, expensesDataSet, balanceDataSet)
        data.barWidth = 0.15f
        chart!!.setData(data)

        val xAxis: XAxis = chart!!.getXAxis()
        // xAxis.valueFormatter = IndexAxisValueFormatter(months)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.setCenterAxisLabels(true)
        xAxis.isGranularityEnabled = true

        val barSpace = 0.02f
        val groupSpace = 0.3f
        val groupCount = 1

        //IMPORTANT *****

        //IMPORTANT *****
        data.barWidth = 0.15f

        chart!!.getXAxis().setAxisMaximum(
            0 + chart!!.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount
        )
        chart!!.groupBars(0F, groupSpace, barSpace) // perform the "explicit" grouping
        chart!!.invalidate()
        //***** IMPORTANT
    }

    private fun showBarChart3Month(totalearning: Float, totalExpenses: Float, totalBalance: Float) {
        chart!!.data = null
        var earningEntry: ArrayList<BarEntry> = ArrayList()
        var expenseEntry: ArrayList<BarEntry> = ArrayList()
        var balanceEntry: ArrayList<BarEntry> = ArrayList()

        earningEntry.add(BarEntry(0f, totalearning))
        expenseEntry.add(BarEntry(0f, totalExpenses))
        balanceEntry.add(BarEntry(0f, totalBalance))

        val earningDataSet = BarDataSet(earningEntry, "Earning")
        earningDataSet.color = Color.parseColor("#FFAE00")
        val expensesDataSet = BarDataSet(expenseEntry, "Expense")
        expensesDataSet.setColors(Color.parseColor("#02B717"))
        val balanceDataSet = BarDataSet(balanceEntry, "Balance")
        balanceDataSet.setColors(Color.parseColor("#006CFF"))
        var data = BarData(earningDataSet, expensesDataSet, balanceDataSet)
        data.barWidth = 0.15f
        chart!!.setData(data)

        val xAxis: XAxis = chart!!.getXAxis()
        // xAxis.valueFormatter = IndexAxisValueFormatter(months)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.setCenterAxisLabels(true)
        xAxis.isGranularityEnabled = true

        val barSpace = 0.02f
        val groupSpace = 0.3f
        val groupCount = 1

        //IMPORTANT *****

        //IMPORTANT *****
        data.barWidth = 0.15f

        chart!!.getXAxis().setAxisMaximum(
            0 + chart!!.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount
        )
        chart!!.groupBars(0F, groupSpace, barSpace) // perform the "explicit" grouping
        chart!!.invalidate()
        //***** IMPORTANT
    }


    private fun showBarChart1Year(montlyRevenueDTO: ArrayList<MontlyRevenueDTO>) {
        chart!!.data = null

        var filterData = ArrayList<filtered>()
        var months = Arrays.asList(
            "January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December"
        )

        var count = 1
        var earningEntry: ArrayList<BarEntry> = ArrayList()
        var expenseEntry: ArrayList<BarEntry> = ArrayList()
        var balanceEntry: ArrayList<BarEntry> = ArrayList()


        for (month in months) {
            var filter = filtered()
            for (k in montlyRevenueDTO) {
                if (k.month!!.equals(count.toString())) {
                    if (k.type.equals("earnings", true)) {
                        filter.totalEarnings = k.totalAmount
                    }
                    if (k.type.equals("expenses", true)) {
                        filter.totalExpenses = k.totalAmount
                    }
                    filter.totalBalance = (filter.totalEarnings - filter.totalExpenses)

                }
            }
            filter.month = count.toString()
            filterData.add(filter)

            ++count

        }
        var monthcount = 1
        for (i in months) {
            for (j in filterData) {
                if (j.month!!.equals(monthcount.toString())) {
                    earningEntry.add(BarEntry(0f, j.totalEarnings))
                    expenseEntry.add(BarEntry(0f, j.totalExpenses))
                    balanceEntry.add(BarEntry(0f, j.totalBalance))

                } else {
                    earningEntry.add(BarEntry(0f, 0f))
                    expenseEntry.add(BarEntry(0f, 0f))
                    balanceEntry.add(BarEntry(0f, 0f))
                }


            }
        }


        /* for (i in months) {

             for (j in filterData)
             {
             if (count.equals(j.month)){



                 if (j.type.equals("earnings",true)){
                     earningEntry.add(BarEntry(0f, j.totalAmount))
                     totalearning=j.totalAmount
                 }
                 if (j.type.equals("expense",true))
                 {
                     expenseEntry.add(BarEntry(0f, j.totalAmount))
                     totalexpenses=j.totalAmount
                 }

                 balanceEntry.add(BarEntry(0f, (totalearning-totalexpenses)))

             }
             }


         }*/

        val earningDataSet = BarDataSet(earningEntry, "Earning")
        earningDataSet.color = Color.parseColor("#FFAE00")
        val expensesDataSet = BarDataSet(expenseEntry, "Expenses")
        expensesDataSet.setColors(Color.parseColor("#02B717"))
        val balanceDataSet = BarDataSet(balanceEntry, "Balance")
        balanceDataSet.setColors(Color.parseColor("#006CFF"))
        var data = (BarData(earningDataSet, expensesDataSet, balanceDataSet))
        data.barWidth = 0.15f
        chart!!.setData(data)



        val xAxis: XAxis = chart!!.getXAxis()
        xAxis.valueFormatter = IndexAxisValueFormatter(months)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.setCenterAxisLabels(true)
        xAxis.isGranularityEnabled = true

        val barSpace = 0.02f
        val groupSpace = 0.3f
        val groupCount = 12

        //IMPORTANT *****

        //IMPORTANT *****

        chart!!.getXAxis().setAxisMaximum(
            0 + chart!!.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount
        )
        chart!!.groupBars(0F, groupSpace, barSpace) // perform the "explicit" grouping
        chart!!.invalidate()
        //***** IMPORTANT
    }

    companion object {
        var list: ArrayList<AgentBookKeepingUser> = ArrayList()
    }

    fun openDialog() {
        customdatePicker()
    }

    private fun customdatePicker() {
        //Inflate the dialog with custom view
        val mDialogView =
            LayoutInflater.from(this).inflate(R.layout.custom_date_picker_dialog, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this).setView(mDialogView)
        val mdialog: AlertDialog = mBuilder.create()
        mdialog.show()
        var startDateDialog: TextView = mDialogView.findViewById(R.id.edit_start_date)
        var endDateDialog: TextView = mDialogView.findViewById(R.id.edit_end_date)
        startDateDialog?.setOnClickListener {
            val dateDialog = DatePickerDialog(
                this,
                { view, year, monthOfYear, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    editStartDate!!.setText(sdf.format(cal.time))
                    startDateFlag = true

                    val formatter2 = SimpleDateFormat("yyyy-MM-dd")
                    val todayString2 = formatter2.format(cal.time)
                    startDate = todayString2
                    startDateFromDialog = true
                    startDateDialog.setText(startDate)
                },
                year,
                month,
                day
            )
            dateDialog.show()
            val cal1 = Calendar.getInstance()
            cal1.add(Calendar.DAY_OF_MONTH, 0)
            dateDialog.datePicker.maxDate = cal1.timeInMillis
            editStartDate!!.error = null
        }

        endDateDialog?.setOnClickListener {
            if (startDateFlag) {
                val dateDialog = DatePickerDialog(
                    this,
                    { view, year, monthOfYear, dayOfMonth ->
                        cal.set(Calendar.YEAR, year)
                        cal.set(Calendar.MONTH, monthOfYear)
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                        val sdf = SimpleDateFormat(myFormat, Locale.US)
                        editEndDate!!.setText(sdf.format(cal.time))

                        val formatter3 = SimpleDateFormat("yyyy-MM-dd")
                        val todayString3 = formatter3.format(cal.time)
                        Log.e("DateNow 3", todayString3)
                        endDate = todayString3
                        endDateFromDialog = true
                        endDateDialog.setText(endDate)
                    },
                    year,
                    month,
                    day
                )
                dateDialog.show()
                val cal1 = Calendar.getInstance()
                cal1.add(Calendar.DAY_OF_MONTH, 0)
                dateDialog.datePicker.maxDate = cal1.timeInMillis
                editEndDate!!.error = null
            } else {
                Toast.makeText(appContext, "Select Start date first.", Toast.LENGTH_SHORT).show()
            }
        }

        var search: TextView = mDialogView.findViewById(R.id.search_button)


        search.setOnClickListener {
            if (startDateFromDialog && endDateFromDialog) {
                mdialog.dismiss()
                getBookKeepingList()
            } else {
                Toast.makeText(
                    this@BookKeepingListActivityAgent,
                    "Need to select start date and End date ",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }


}
