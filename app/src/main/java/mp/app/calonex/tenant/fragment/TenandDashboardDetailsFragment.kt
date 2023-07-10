package mp.app.calonex.tenant.fragment

import android.annotation.SuppressLint
import android.content.Context
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


class TenandDashboardDetailsFragment : Fragment() {
    private var cal = Calendar.getInstance()
    private var year = cal.get(Calendar.YEAR)
    private var month = cal.get(Calendar.MONTH)
    private var day = cal.get(Calendar.DAY_OF_MONTH)
    val myFormat = "MMM dd, yyyy"

    var startDate = "2020-01-01"
    var endDate = ""

    var data_0: TextView? = null
    var data_1: TextView? = null
    var data_2: TextView? = null
    var data_3: TextView? = null
    var data_4: TextView? = null
    var data_5: TextView? = null
    var data_6: TextView? = null

    var credit_point: TextView? = null
    var cash_value: TextView? = null

    lateinit var appContext: Context
    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView =
            inflater.inflate(R.layout.activity_tenant_dashbord_2, container, false)

        data_0 = rootView.findViewById(R.id.data_0)
        data_1 = rootView.findViewById(R.id.data_1)
        data_2 = rootView.findViewById(R.id.data_2)
        data_3 = rootView.findViewById(R.id.data_3)
        data_4 = rootView.findViewById(R.id.data_4)
        data_5 = rootView.findViewById(R.id.data_5)
        data_6 = rootView.findViewById(R.id.data_6)

        credit_point = rootView.findViewById(R.id.credit_point)
        cash_value = rootView.findViewById(R.id.cash_value)

        val todayDate = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val todayString = formatter.format(todayDate)

        startDate = "2020-01-01"
        endDate = todayString

        val text1up = "<font color=#000000>Credit Point: </font> <font color=#17AE66>$0.00</font>"
        credit_point!!.text = Html.fromHtml(text1up)
        val text2up = "<font color=#000000>Cash Value: </font><font color=#17AE66>$0.00</font>"
        cash_value!!.text = Html.fromHtml(text2up)

        val text0 = "<font color=#000000>Platform Earning: </font> <font color=#238A12> $0.00" + "</font>"
        data_0!!.text = Html.fromHtml(text0)

        val text1 = "<font color=#000000>Platform Expense: </font> <font color=#A30E0E> $0.00" + "</font>"
        data_1!!.text = Html.fromHtml(text1)

        val text2 = "<font color=#000000>Outside Earning: </font> <font color=#238A12> $0.00" + "</font>"
        data_2!!.text = Html.fromHtml(text2)

        val text3 = "<font color=#000000>Outside Expense: </font> <font color=#A30E0E> $0.00" + "</font>"
        data_3!!.text = Html.fromHtml(text3)

        val text4 = "<font color=#000000>Total Earning: </font> <font color=#238A12> $0.00</font>"
        data_4!!.text = Html.fromHtml(text4)

        val text5 = "<font color=#000000>Total Expense: </font> <font color=#A30E0E> $0.00</font>"
        data_5!!.text = Html.fromHtml(text5)

        val text6 = "<font color=#000000>Total Balance: </font> <font color=#A30E0E> $0.00</font>"
        data_6!!.text = Html.fromHtml(text6)

        getTenantCreditPoints()
        getBookKeepingList()

        return rootView
    }

    private fun getBookKeepingList() {
        val bookKeepingService: ApiInterface =
            ApiClient(appContext).provideService(ApiInterface::class.java)
        val apiCall: Observable<AgentBookKeepingResponse> =
            bookKeepingService.getBookKeepingInfo(startDate, endDate) //Test API Key

        RxAPICallHelper().call(apiCall, object : RxAPICallback<AgentBookKeepingResponse> {
            override fun onSuccess(response: AgentBookKeepingResponse) {
                if (response.responseCode == "200") {

                    var totalPlatformEarning = 0.0f
                    var totalPlatformExpense = 0.0f

                    for (item in response.fromSystem!!) {
                        if (item.type == "expense") {
                            totalPlatformExpense += item.amount
                        } else {
                            totalPlatformEarning += item.amount
                        }
                    }

                    val text0 = "<font color=#000000>Platform Earning: </font> <font color=#238A12> $" + totalPlatformEarning.toBigDecimal().setScale(2, RoundingMode.UP).toDouble() + "</font>"
                    data_0!!.text = Html.fromHtml(text0)

                    val text1 = "<font color=#000000>Platform Expense: </font> <font color=#A30E0E> $" + totalPlatformExpense.toBigDecimal().setScale(2, RoundingMode.UP).toDouble() + "</font>"
                    data_1!!.text = Html.fromHtml(text1)

                    var totalExpense = 0.0f
                    var totalEarning = 0.0f

                    for (item in response.userAdded!!) {
                        if (item.type == "earnings") {
                            totalEarning += item.amount
                        } else {
                            totalExpense += item.amount
                        }
                    }

                    val text2 = "<font color=#000000>Outside Earning: </font> <font color=#238A12> $" + totalEarning.toBigDecimal().setScale(2, RoundingMode.UP).toDouble() + "</font>"
                    data_2!!.text = Html.fromHtml(text2)

                    val text3 = "<font color=#000000>Outside Expense: </font> <font color=#A30E0E> $" + totalExpense.toBigDecimal().setScale(2, RoundingMode.UP).toDouble() + "</font>"
                    data_3!!.text = Html.fromHtml(text3)

                    val text4 = "<font color=#000000>Total Earning: </font> <font color=#238A12> $" + response.amount!!.totalEarnings.toBigDecimal().setScale(2, RoundingMode.UP).toDouble() + "</font>"
                    data_4!!.text = Html.fromHtml(text4)

                    val text5 = "<font color=#000000>Total Expense: </font> <font color=#A30E0E> $" + response.amount!!.totalExpenses.toBigDecimal().setScale(2, RoundingMode.UP).toDouble() + "</font>"
                    data_5!!.text = Html.fromHtml(text5)

                    if (response.amount!!.balance >= 0) {
                        val text6 = "<font color=#000000>Total Balance: </font> <font color=#238A12> $" + response.amount!!.balance.toBigDecimal().setScale(2, RoundingMode.UP).toDouble() + "</font>"
                        data_6!!.text = Html.fromHtml(text6)
                    } else {
                        val text6 = "<font color=#000000>Total Balance: </font> <font color=#A30E0E> $" + response.amount!!.balance.toBigDecimal().setScale(2, RoundingMode.UP).toDouble() + "</font>"
                        data_6!!.text = Html.fromHtml(text6)
                    }
                }
            }

            override fun onFailed(throwable: Throwable) {
                // show error
                Log.e("onFailure", throwable.toString())
                //
                try {
                    Util.apiFailure(appContext, throwable)
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
        //private val TAG = AgentPropertiesAndApprovedFragment::class.java.name

        @JvmStatic
        fun newInstance() = TenandDashboardDetailsFragment()
    }

}
