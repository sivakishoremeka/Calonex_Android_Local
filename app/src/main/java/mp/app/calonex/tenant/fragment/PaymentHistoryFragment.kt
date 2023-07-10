package mp.app.calonex.tenant.fragment


import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_ld_property.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.TenantPaymentHistoryCredentials
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.landlord.adapter.PaymentHistoryAdapter
import mp.app.calonex.tenant.model.PaymentHistory
import mp.app.calonex.tenant.response.PaymentHistoryResponse


class PaymentHistoryFragment : Fragment() {
    lateinit var appContext: Context
    private var paymentHistoryListAdapter: PaymentHistoryAdapter? = null
    private var txt_no_data: TextView? = null
    private var progress_bar: ProgressBar? = null
    private var payment_history_rv: RecyclerView? = null
    private var swiprRefreshPayment: SwipeRefreshLayout? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.activity_tenant_payment_history, container, false)
        txt_no_data = rootView.findViewById(R.id.txt_no_data)
        progress_bar = rootView.findViewById(R.id.pb_payment_history)
        payment_history_rv = rootView.findViewById(R.id.payment_history_rv)
        swiprRefreshPayment = rootView.findViewById(R.id.refresh_tenant_payment_history)

        swiprRefreshPayment?.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                appContext,
                R.color.colorPrimary
            )
        )
        swiprRefreshPayment?.setColorSchemeColors(Color.WHITE)

        swiprRefreshPayment?.setOnRefreshListener {

            getPaymentHistory()
            swiprRefreshPayment?.isRefreshing = false

        }

        txt_no_data?.visibility = View.GONE

        progress_bar?.visibility = View.VISIBLE

        val dividerItemDecoration =
            DividerItemDecoration(payment_history_rv!!.context, DividerItemDecoration.VERTICAL)
        dividerItemDecoration.setDrawable(
            this.context?.let { ContextCompat.getDrawable(it, R.drawable.item_list_divider) }!!
        )
//        headerBack = rootView.findViewById(R.id.header_back)

        getPaymentHistory()

        return rootView
    }

    override fun onResume() {
        super.onResume()
        getPaymentHistory()
    }

    private fun getPaymentHistory() {
        val credential = TenantPaymentHistoryCredentials()
        credential.tenantId = Kotpref.userId
        credential.year = "0"

        val paymentHistoryService: ApiInterface =
            ApiClient(appContext).provideService(ApiInterface::class.java)
        val apiCall: Observable<PaymentHistoryResponse> =
            paymentHistoryService.paymentHistory(credential)

        apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
            Log.e("onSuccess", it.responseDto?.responseDescription.toString())

            progress_bar?.visibility = View.GONE
            txt_no_data?.visibility = View.GONE

            if (it.responseDto!!.responseCode == 204) {
                txt_no_data?.visibility = View.VISIBLE
            } else {

                if (it.data != null) {
                    setLeaseList(it.data!!)
                }
            }


        },
            { e ->
                txt_no_data?.visibility = View.VISIBLE
                Log.e("onFailure", e.toString())
                progress_bar?.visibility = View.GONE
                e.message?.let {
                    Toast.makeText(appContext, it, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun setLeaseList(listResponse: ArrayList<PaymentHistory>) {

        payment_history_rv?.layoutManager = LinearLayoutManager(appContext)
        paymentHistoryListAdapter =
            PaymentHistoryAdapter(appContext, listResponse as ArrayList<PaymentHistory>)
        payment_history_rv?.adapter = paymentHistoryListAdapter

    }

}