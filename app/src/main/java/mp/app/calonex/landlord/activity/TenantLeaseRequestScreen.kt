package mp.app.calonex.landlord.activity

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_tenant_lease_request_screen.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.FindLeaseCredentials
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.adapter.TenantLeaseRequestAdapter
import mp.app.calonex.landlord.model.LeaseRequestInfo
import mp.app.calonex.landlord.response.FindLeaseResponse

class TenantLeaseRequestScreen : CxBaseActivity2() {

    private var rvLeaseReq: RecyclerView? = null
    private var pbLr: ProgressBar? = null
    private var srLeaseReq: SwipeRefreshLayout? = null
    private var tenantLeaseRequestAdapter: TenantLeaseRequestAdapter? = null
    private var propertyId: String? = ""
    private var isHistory: Boolean? = false
    private var isTenant: Boolean? = false
    private var unitId: String? = ""
    private var txtLeaseTryAgain: TextView? = null
    private var iv_back: ImageView? = null
    private var layoutLeaseEmpty: RelativeLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tenant_lease_request_screen)
        initComponent()
        actionComponent()
        startHandler()
    }

    private fun initComponent() {
        rvLeaseReq = findViewById(R.id.rv_lease)
        srLeaseReq = findViewById(R.id.sr_lease_req)
        iv_back = findViewById(R.id.iv_back)
        pbLr = findViewById(R.id.pb_lr)
        txtLeaseTryAgain = findViewById(R.id.txt_lease_try_again)
        layoutLeaseEmpty = findViewById(R.id.layout_lease_empty)
        propertyId = intent.getStringExtra(getString(R.string.intent_property_id))
        isHistory = intent.getBooleanExtra(getString(R.string.is_lease_history), false)
        isTenant = intent.getBooleanExtra("isTenant", false)

        if (isHistory!!) {
            unitId = intent.getStringExtra(getString(R.string.intent_unit_id))
        }


        //** Set the colors of the Pull To Refresh View
        srLeaseReq?.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                applicationContext,
                R.color.colorPrimary
            )
        )
        srLeaseReq?.setColorSchemeColors(Color.WHITE)


    }

    override fun onResume() {
        super.onResume()
        getLeaseList()
    }

    private fun actionComponent() {
        srLeaseReq?.setOnRefreshListener {

            getLeaseList()
            srLeaseReq?.isRefreshing = false

        }
        txtLeaseTryAgain!!.setOnClickListener {
            layoutLeaseEmpty!!.visibility = View.GONE
            srLeaseReq!!.visibility = View.GONE
            getLeaseList()

        }

        iv_back!!.setOnClickListener {
            onBackPressed()
        }

    }

    private fun getLeaseList() {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            //Create retrofit Service
            pbLr!!.visibility = View.VISIBLE
            val credential = FindLeaseCredentials()
            credential.userId = Kotpref.userId
            credential.userRole = Kotpref.userRole
            credential.propertyId = propertyId
            if (isHistory!!) {
                credential.leaseHistory = isHistory!!
                if (!isTenant!!) {
                    credential.unitId = unitId
                }
            }
            val findApi: ApiInterface = ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<FindLeaseResponse> =
                findApi.findLease(credential)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", it.responseDto?.responseDescription.toString())

                    pbLr!!.visibility = View.GONE

                    if (it.data != null && it.data!!.isNotEmpty()) {
                        setLeaseList(it.data!!)
                        srLeaseReq!!.visibility = View.VISIBLE
                        layoutLeaseEmpty!!.visibility = View.GONE
                    } else {
                        layoutLeaseEmpty!!.visibility = View.VISIBLE
                        srLeaseReq!!.visibility = View.GONE
                    }

                },
                    { e ->
                        Log.e("onFailure", e.toString())
                        pbLr!!.visibility = View.GONE
                        e.message?.let {
                            Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                        }
                    })
        } else {
            Toast.makeText(
                applicationContext,
                getString(R.string.error_network),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setLeaseList(listResponse: ArrayList<LeaseRequestInfo>) {
        rvLeaseReq?.layoutManager = LinearLayoutManager(applicationContext)

        tenantLeaseRequestAdapter =
            TenantLeaseRequestAdapter(this, pbLr!!, this@TenantLeaseRequestScreen, listResponse)
        rvLeaseReq?.adapter = tenantLeaseRequestAdapter
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Util.navigationBack(this)
    }
}