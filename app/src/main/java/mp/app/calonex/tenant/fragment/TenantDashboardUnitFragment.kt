package mp.app.calonex.tenant.fragment


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.TenantFindApiCredentials
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.landlord.model.TenantInfoPayload
import mp.app.calonex.tenant.adapter.TenantDashboardListAdapter
import mp.app.calonex.tenant.model.LeaseTenantInfo
import mp.app.calonex.tenant.response.FindApiResponse
import java.util.*


class TenantDashboardUnitFragment : Fragment() {
    lateinit var appContext: Context
    private var cal = Calendar.getInstance()
    val myFormat = "MMM dd, yyyy"
    var startDate = "2020-01-01"
    var endDate = ""
    var set1: BarDataSet? = null
    var barChart: BarChart? = null
    var barDataSet: BarDataSet? = null
    var barEntriesArrayList = ArrayList<BarEntry>()

    var tenant_list_rv: RecyclerView? = null
    var msg: TextView? = null

    var leaseTenant: LeaseTenantInfo? = null

    var leasePosition : Int = 0




    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fregment_dashboard_tenant_unit, container, false)

        tenant_list_rv =rootView.findViewById(R.id.tenant_list_rv)
        msg =rootView.findViewById(R.id.msg)


        tenantListPermanant.clear()
        tenantListTemporary.clear()
        findApiResponse = LeaseTenantInfo()

        findLease()
        return rootView
    }

    private fun tenantList() {
        Log.e("tenantList", "tenantListPermanant size"+tenantListPermanant.size)

        if (tenantListPermanant.size == 0) {
            msg!!.visibility = View.VISIBLE
        } else {
            msg!!.visibility = View.GONE
        }

        tenant_list_rv!!.layoutManager = LinearLayoutManager(appContext)
        var tenantListAdapter = TenantDashboardListAdapter(appContext, tenantListPermanant, false)
        tenant_list_rv!!.adapter = tenantListAdapter

    }
    private fun findLease() {
        Log.e("TenantListActivity:::::", "findLease Called")

      //  tenant_list_progress.visibility = View.VISIBLE
        val credential = TenantFindApiCredentials()
        credential.userId = Kotpref.userId
        credential.userRole = "CX-Tenant"

        val findApi: ApiInterface = ApiClient(appContext).provideService(ApiInterface::class.java)
        val apiCall: Observable<FindApiResponse> =
            findApi.find(credential)

        apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
            Log.e("onSuccess", it.responseDto?.responseDescription.toString())


            if (it.data != null) {
                findApiResponse = it.data!![leasePosition]
//                txt_tenant_address.text = it.data!![0].propertyAddress
//                txt_tenant_unit_no.text = it.data!![0].unitNumber
//                txt_landlord_name.text = it.data!![0].landlordName

                Kotpref.propertyId = findApiResponse.propertyId
                Kotpref.leaseId = findApiResponse.leaseId
                Kotpref.unitNumber = findApiResponse.unitNumber

                for (dataItems in it.data!!) {
                    for (item in dataItems.tenantBaseInfoDto) {
                        if (item.userId == Kotpref.userId) {
                            Kotpref.exactRole = item.role.toString()
                        }

                        // Manipulate the data as not everything comes from the API
                        item.unitNumber = dataItems.unitNumber
                        item.requestdate = dataItems.leaseStartDate.toLong()
                        item.leaseStatus = dataItems.leaseStatus

                        tenantListPermanant.add(item)
                    }
                }
                leaseTenant = findApiResponse
               // staticTenantList = findApiResponse.tenantBaseInfoDto
                //tenantListPermanant = findApiResponse.tenantBaseInfoDto
                //tenantListTemporary = findApiResponse.tenantBaseInfoDto

             //   updateUi()
             //   tenant_list_progress.visibility = View.GONE

            }


            tenantList();

        },
            { e ->
               // tenant_list_progress.visibility = View.GONE
                Log.e("onFailure", e.toString())
                e.message?.let {
                    Toast.makeText(appContext, it, Toast.LENGTH_SHORT).show()
                }
            })

    }
    companion object {
        @JvmStatic
        fun newInstance() = TenantDashboardUnitFragment()
        var staticTenantList = ArrayList<TenantInfoPayload>()
        var primaryTpayDay = String()

        var findApiResponse = LeaseTenantInfo()
        var tenantListPermanant = ArrayList<TenantInfoPayload>()
        var tenantListTemporary = ArrayList<TenantInfoPayload>()



    }





}
