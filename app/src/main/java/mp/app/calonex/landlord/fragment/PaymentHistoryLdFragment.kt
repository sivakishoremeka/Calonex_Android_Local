package mp.app.calonex.landlord.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_ld_payment.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.LandlordPaymentHistoryCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.NotifyScreen
import mp.app.calonex.landlord.adapter.CustomSpinnerAdapter
import mp.app.calonex.landlord.adapter.LandlordPaymentListAdapter
import mp.app.calonex.landlord.model.LandlordPaymentHistory
import mp.app.calonex.landlord.response.LandlordPaymentHistoryListResponse

class PaymentHistoryLdFragment : Fragment() {

    private lateinit var appContext: Context
    private var ldPropertyListAdapter: LandlordPaymentListAdapter? = null
    private var refreshPayment: SwipeRefreshLayout? = null
    private var spinnerAddress:Spinner?=null
    private var addressList= ArrayList<String>()
    private var paymentList= ArrayList<LandlordPaymentHistory>()
    private var btnTryAgain: Button?=null
    private var searchPayment: SearchView? = null
    private var layoutLphNotify:RelativeLayout?=null
    private var txtLphNotify:TextView?=null
    private var profilePic:CircleImageView?=null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext = context

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_ld_payment, container, false)
        profilePic=rootView.findViewById(R.id.profile_pic)
        refreshPayment = rootView.findViewById(R.id.refresh_ld_payment)
        searchPayment = rootView.findViewById(R.id.search_payment)
        spinnerAddress=rootView.findViewById(R.id.spinner_property)
        btnTryAgain=rootView.findViewById(R.id.btn_try_again)
        layoutLphNotify=rootView.findViewById(R.id.layout_lp_notify)
        txtLphNotify=rootView.findViewById(R.id.txt_lp_notify)
        val searchTextViewId: Int = searchPayment!!.getContext().getResources()
            .getIdentifier("android:id/search_src_text", null, null)
        val searchText = searchPayment!!.findViewById(searchTextViewId) as EditText
        searchText.setTextColor(Color.WHITE)
        refreshPayment?.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                appContext,
                R.color.colorPrimary
            )
        )
        Glide.with(appContext)
            .load(Kotpref.profile_image)
            .placeholder(R.drawable.profile_default_new)
            .into(profilePic!!)
        refreshPayment?.setColorSchemeColors(Color.WHITE)
        refreshPayment?.setOnRefreshListener {

            getPaymentList()
            refreshPayment?.isRefreshing = false

        }

        getPaymentList()
        layoutLphNotify!!.setOnClickListener{
            Util.navigationNext(requireActivity(), NotifyScreen::class.java)
        }
        btnTryAgain?.setOnClickListener {
            btnTryAgain?.visibility=View.GONE
            getPaymentList()
        }
        searchPayment!!.setOnClickListener {
          //  searchPayment!!.isIconified = false
        }

        searchPayment!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty() &&ldPropertyListAdapter!=null) {
                    ldPropertyListAdapter!!.filter.filter(newText)
                }else{
                    try {
                        if(ldPropertyListAdapter!=null)
                        ldPropertyListAdapter!!.filter.filter("")
                    }
                    catch (E :Exception)
                    {
                        E.printStackTrace()
                    }
                }
                return true
            }

        })
        return rootView

    }
    override fun onStart() {
        super.onStart()
        if(Kotpref.notifyCount!=null && !Kotpref.notifyCount.isEmpty() && Integer.parseInt(Kotpref.notifyCount)>0) {
            txtLphNotify!!.text = Kotpref.notifyCount
            txtLphNotify!!.visibility = View.VISIBLE
        }
        else
        {
            txtLphNotify!!.visibility = View.GONE
        }



    }
    private fun getPaymentList() {
        if (NetworkConnection.isNetworkConnected(appContext)) {
            //Create retrofit Service
            pb_payment?.visibility = View.VISIBLE
            val credentials = LandlordPaymentHistoryCredential()

            credentials.userCatalogId = Kotpref.userId
            val paymentListService: ApiInterface =
                ApiClient(appContext).provideService(ApiInterface::class.java)
            val apiCall: Observable<LandlordPaymentHistoryListResponse> =
                paymentListService.landlordPaymentHistory(credentials)

            RxAPICallHelper().call(
                apiCall,
                object : RxAPICallback<LandlordPaymentHistoryListResponse> {
                    override fun onSuccess(paymentListResponse: LandlordPaymentHistoryListResponse) {
                        Log.e(
                            "onSuccessPayment",
                            paymentListResponse.responseDto?.responseDescription.toString()
                        )
                        if (paymentListResponse.data != null && !paymentListResponse.data?.isEmpty()!!) {
                            setPaymentList(paymentListResponse.data!!)
                        }else{
                            btnTryAgain?.visibility=View.VISIBLE
                        }
                        pb_payment?.visibility = View.GONE
                    }

                    override fun onFailed(t: Throwable) {
                        // show error
                        Log.e("onFailure", t.toString())
                        btnTryAgain?.visibility=View.VISIBLE
                        pb_payment?.visibility = View.GONE
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
        } else {
            Toast.makeText(appContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
        }
    }

    private fun setPaymentList(listResponse: ArrayList<LandlordPaymentHistory>) {


        //getAddressList(listResponse)
        paymentList=listResponse
        setList(listResponse)
        var spinnerAddressAdapter =  CustomSpinnerAdapter(appContext,  addressList)
        /*spinnerAddress?.adapter=spinnerAddressAdapter

        spinnerAddress?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {

                if (position> 0){
                    val value:String= addressList.get(position)
                    applyFilter(value,paymentList)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // another interface callback
            }
        }*/


    }

private fun getAddressList(rootList:ArrayList<LandlordPaymentHistory>) {
    addressList.add("Select Property")
    val addList= mutableListOf<String>()
    for (item in rootList) {
        addList.add(item.propertyAddress)
    }
    addressList.addAll(ArrayList<String>(addList.toSet().toList()))
}

    private fun setList(rootList:ArrayList<LandlordPaymentHistory>) {
        rv_ld_payments?.layoutManager = LinearLayoutManager(appContext)
        val list:MutableList<LandlordPaymentHistory>?=rootList.toMutableList()

        val listPayment= ArrayList(list!!.reversed())
        ldPropertyListAdapter = LandlordPaymentListAdapter(
            appContext,
            activity,
            listPayment
        )
        rv_ld_payments?.adapter = ldPropertyListAdapter
    }

    private fun applyFilter(address:String,rootList:ArrayList<LandlordPaymentHistory>) {
        lateinit var filterList: List<LandlordPaymentHistory>

            filterList =    rootList.filter { it.propertyAddress == address }



        val array = arrayListOf<LandlordPaymentHistory>()
        array.addAll(filterList)
        setList(array)
    }


}