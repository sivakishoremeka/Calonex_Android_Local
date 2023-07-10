package mp.app.calonex.tenant.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_lease_list.*
import kotlinx.android.synthetic.main.activity_lease_list.no_lease_tenant
import kotlinx.android.synthetic.main.activity_tenant_lease_request_screen.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.TenantFindApiCredentials
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.landlord.activity.CxBaseActivity2
import mp.app.calonex.landlord.activity.LoginScreen
import mp.app.calonex.landlord.adapter.LeaseListAdapter
import mp.app.calonex.tenant.model.LeaseTenantInfo
import mp.app.calonex.tenant.response.FindApiResponse

class LeaseListActivity : CxBaseActivity2() {

    private var leaseListAdapter: LeaseListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lease_list)

        pb_lease_list.visibility = View.VISIBLE

        val dividerItemDecoration =
            DividerItemDecoration(rv_lease_list!!.context, DividerItemDecoration.VERTICAL)
        dividerItemDecoration.setDrawable(
            ContextCompat.getDrawable(this, R.drawable.item_list_divider)!!
        )
        header_back.setOnClickListener {
            super.onBackPressed()
        }

        getLeaseList()

        rv_lease_list!!.addItemDecoration(dividerItemDecoration)
        txt_tenant_logout!!.setOnClickListener {
            Kotpref.clear()
           // startActivity(Intent(this, LoginScreen::class.java))

            val intent = Intent(this, LoginScreen::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

    }

    @SuppressLint("CheckResult")
    private fun getLeaseList() {

        val credential = TenantFindApiCredentials()
        credential.userId = Kotpref.userId
        credential.userRole = "CX-Tenant"

        val findApi: ApiInterface = ApiClient(this).provideService(ApiInterface::class.java)
        val apiCall: Observable<FindApiResponse> =
            findApi.find(credential)

        apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
            Log.e("onSuccess", it.responseDto?.responseDescription.toString())



            if (it.data != null && it.data!!.isNotEmpty()) {
                setLeaseList(it.data!!)
                leaseListApiResponse = it.data!!
                sr_ld_lease_req.visibility = View.VISIBLE
                no_lease_tenant.visibility = View.GONE
            } else {
                no_lease_tenant.visibility = View.VISIBLE
                sr_ld_lease_req.visibility = View.GONE
            }
            pb_lease_list.visibility = View.GONE


        },
            { e ->
                Log.e("onFailure", e.toString())
                pb_lease_list.visibility = View.GONE
                e.message?.let {
                    Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun setLeaseList(listResponse: ArrayList<LeaseTenantInfo>) {
        rv_lease_list?.layoutManager = LinearLayoutManager(this)
        leaseListAdapter = LeaseListAdapter(this@LeaseListActivity, listResponse as ArrayList<LeaseTenantInfo>)
        rv_lease_list?.adapter = leaseListAdapter
    }

    companion object {
        var leaseListApiResponse = ArrayList<LeaseTenantInfo>()
        var leaseItem = LeaseTenantInfo()
    }
}

