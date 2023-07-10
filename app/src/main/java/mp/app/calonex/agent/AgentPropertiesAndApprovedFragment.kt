package mp.app.calonex.agent

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import io.reactivex.Observable
import kotlinx.android.synthetic.main.agent_properties_approved_fragment.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.LandlordRevenueDetailCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection


class AgentPropertiesAndApprovedFragment : Fragment() {

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
            inflater.inflate(R.layout.agent_properties_approved_fragment, container, false)
        getMyBookOfBusiness()

        return rootView
    }

    private fun getMyBookOfBusiness() {

        if (NetworkConnection.isNetworkConnected(appContext)) {
            //Create retrofit Service
            val credentials = LandlordRevenueDetailCredential()

            credentials.userCatalogId = Kotpref.userId
            val service: ApiInterface =
                ApiClient(appContext).provideService(ApiInterface::class.java)
            val apiCall: Observable<AgentPropertiesAndTenantResponse> =
                service.getSumOfAgentPropertiesAndApprovedTenant(credentials)

            RxAPICallHelper().call(
                apiCall,
                object : RxAPICallback<AgentPropertiesAndTenantResponse> {
                    override fun onSuccess(response: AgentPropertiesAndTenantResponse) {

                        if (response.data != null) {
                            setData(response.data!!)
                        }


                    }

                    override fun onFailed(t: Throwable) {

                    }
                })
        } else {
            Toast.makeText(appContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
        }
    }

    private fun setData(data: AgentPorpertyAndTenantRespnseDetail) {
        onboarded_property.text = "${data.onboardedProperties}\n Onboarded property"
        tenant_approved.text = "${data.tenantApproved}\n Tenant  Approved"
    }

    companion object {
        //private val TAG = AgentPropertiesAndApprovedFragment::class.java.name

        @JvmStatic
        fun newInstance() =
            AgentPropertiesAndApprovedFragment().apply {
            }
    }

}
