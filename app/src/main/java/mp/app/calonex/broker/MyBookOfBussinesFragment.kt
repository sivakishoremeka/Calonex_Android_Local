package mp.app.calonex.broker

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import io.reactivex.Observable
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.LandlordPaymentHistoryCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.landlord.response.BrokerBookOfBusinessResponse


class MyBookOfBussinesFragment : Fragment() {

    private var amountText: TextView? = null

    lateinit var appContext: Context
    private var showTopTenTextView: TextView? = null
    private var header1fragment: TextView? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.my_book_of_business_fragment, container, false)
        amountText = rootView.findViewById(R.id.amount_tv)
        getMyBookOfBusiness()

        return rootView
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            showTopTenTextView!!.visibility = View.GONE
            header1fragment!!.text = "My Book Of Busines"
        }
    }

    private fun getMyBookOfBusiness() {

        if (NetworkConnection.isNetworkConnected(appContext)) {
            //Create retrofit Service
            val credentials = LandlordPaymentHistoryCredential()

            credentials.userCatalogId = Kotpref.userId
            val service: ApiInterface =
                ApiClient(appContext).provideService(ApiInterface::class.java)
            val apiCall: Observable<BrokerBookOfBusinessResponse> =
                service.getBookOfBusiness(credentials)

            RxAPICallHelper().call(
                apiCall,
                object : RxAPICallback<BrokerBookOfBusinessResponse> {
                    override fun onSuccess(response: BrokerBookOfBusinessResponse) {

                        if (response.data != null) {
                            Log.e("Book Business Response", "Success")
                            amountText!!.text = "$${response.data!!.marketValue}"
                        }


                    }

                    override fun onFailed(t: Throwable) {

                    }
                })
        } else {
            Toast.makeText(appContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        //private val TAG = MyBookOfBussinesFragment::class.java.name

        @JvmStatic
        fun newInstance(showtopTenText: TextView?, header1: TextView?) =
            MyBookOfBussinesFragment().apply {
                this.showTopTenTextView = showtopTenText
                this.header1fragment = header1
            }
    }

}
