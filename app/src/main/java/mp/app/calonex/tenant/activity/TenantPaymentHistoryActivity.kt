package mp.app.calonex.tenant.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_tenant_payment_history.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.TenantPaymentHistoryCredentials
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.landlord.activity.CxBaseActivity2
import mp.app.calonex.landlord.adapter.PaymentHistoryAdapter
import mp.app.calonex.tenant.model.PaymentHistory
import mp.app.calonex.tenant.response.PaymentHistoryResponse

class TenantPaymentHistoryActivity : CxBaseActivity2() {

    private var headerBack: ImageView? = null

    private var paymentHistoryListAdapter: PaymentHistoryAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tenant_payment_history)

        txt_no_data.visibility = View.GONE

        pb_payment_history.visibility = View.VISIBLE

        val dividerItemDecoration =
            DividerItemDecoration(payment_history_rv!!.context, DividerItemDecoration.VERTICAL)
        dividerItemDecoration.setDrawable(
            ContextCompat.getDrawable(this, R.drawable.item_list_divider)!!
        )

        getPaymentHistory()


        headerBack = findViewById(R.id.header_back)

        actionComponent()

    }

    private fun getPaymentHistory() {
        val credential = TenantPaymentHistoryCredentials()
        credential.tenantId = Kotpref.userId
        credential.year = "0"

        val paymentHistoryService: ApiInterface =
            ApiClient(this).provideService(ApiInterface::class.java)
        val apiCall: Observable<PaymentHistoryResponse> =
            paymentHistoryService.paymentHistory(credential)

        apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
            Log.e("onSuccess", it.responseDto?.responseDescription.toString())

            pb_payment_history.visibility = View.GONE
            txt_no_data.visibility = View.GONE

            if (it.responseDto!!.responseCode == 0) {
                txt_no_data.visibility = View.VISIBLE
            } else {

                if (it.data != null) {
                    setLeaseList(it.data!!)
                }
            }


        },
            { e ->
                txt_no_data.visibility = View.VISIBLE
                Log.e("onFailure", e.toString())
                pb_payment_history.visibility = View.GONE
                e.printStackTrace()
                e.message?.let {
                    Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun setLeaseList(listResponse: ArrayList<PaymentHistory>) {

        payment_history_rv?.layoutManager = LinearLayoutManager(this)
        paymentHistoryListAdapter =
            PaymentHistoryAdapter(this, listResponse as ArrayList<PaymentHistory>)
        payment_history_rv?.adapter = paymentHistoryListAdapter

    }

    private fun actionComponent() {
        headerBack?.setOnClickListener {
            super.onBackPressed()
        }

    }
}
