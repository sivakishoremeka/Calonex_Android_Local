package mp.app.calonex.rentcx

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_marketplace_list.*
import mp.app.calonex.R
import mp.app.calonex.agent.activity.NotifyScreenAgent
import mp.app.calonex.common.apiCredentials.LandlordPaymentHistoryCredential
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.CxBaseActivity2


class MarketplaceActivity : CxBaseActivity2() {
    private var profilePic:CircleImageView?=null
    private lateinit var appContext: Context
    private var ldPropertyListAdapter: MarketplaceListAdapter? = null
    private var refreshPayment: SwipeRefreshLayout? = null
    private var addressList = ArrayList<String>()
    private var btnTryAgain: Button? = null
    private var searchPayment: SearchView? = null
    private var layoutLphNotify: RelativeLayout? = null
    private var txtLphNotify: TextView? = null
    private var ivBack: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marketplace_list)

        appContext = this@MarketplaceActivity
        profilePic=findViewById(R.id.profile_pic)
        refreshPayment = findViewById(R.id.refresh_ld_payment)
        searchPayment = findViewById(R.id.search_payment)
        btnTryAgain = findViewById(R.id.btn_try_again)
        layoutLphNotify = findViewById(R.id.layout_lph_notify)
        txtLphNotify = findViewById(R.id.txt_lph_notify)
        ivBack = findViewById(R.id.iv_back)
        // for search when data is not available
        val listPayment = ArrayList<Properties>()
        ldPropertyListAdapter = MarketplaceListAdapter(
            appContext,
            this,
            listPayment
        )

        Glide.with(appContext)
            .load(Kotpref.profile_image)
            .placeholder(R.drawable.profile_default_new)
            .into(profilePic!!)

        startHandler()

        val searchTextViewId: Int = searchPayment!!.context.resources
            .getIdentifier("android:id/search_src_text", null, null)
        val searchText = searchPayment!!.findViewById(searchTextViewId) as EditText
        searchText.setTextColor(Color.WHITE)
        refreshPayment?.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                appContext,
                R.color.colorPrimary
            )
        )
        refreshPayment?.setColorSchemeColors(Color.WHITE)
        refreshPayment?.setOnRefreshListener {

            getMarketplaceLists()
            refreshPayment?.isRefreshing = false

        }

        getMarketplaceLists()
        layoutLphNotify!!.setOnClickListener {
            Util.navigationNext(this@MarketplaceActivity, NotifyScreenAgent::class.java)
        }
        btnTryAgain?.setOnClickListener {
            btnTryAgain?.visibility = View.GONE
            getMarketplaceLists()
        }
        searchPayment!!.setOnClickListener {
            //  searchPayment!!.isIconified = false
        }

        ivBack!!.setOnClickListener {
            onBackPressed()
        }

        searchPayment!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()) {
                    ldPropertyListAdapter!!.filter.filter(newText)
                } else {
                    ldPropertyListAdapter!!.filter.filter("")
                }
                return true
            }
        })
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

    private fun getMarketplaceLists() {
        if (NetworkConnection.isNetworkConnected(appContext)) {
            //Create retrofit Service
            pb_payment?.visibility = View.VISIBLE
            val credentials = LandlordPaymentHistoryCredential()

            credentials.userCatalogId = Kotpref.userId
            val RentcxListService: ApiInterface =
                ApiClient2(appContext).provideService(ApiInterface::class.java)
            val apiCall: Observable<MarketplacePropertiesResponse> =
                RentcxListService.getMarketplaceProperties("20", "1")

            RxAPICallHelper().call(
                apiCall,
                object : RxAPICallback<MarketplacePropertiesResponse> {
                    override fun onSuccess(paymentListResponse: MarketplacePropertiesResponse) {
                        Log.e(
                            "onSuccessPayment", paymentListResponse.properties.size.toString()
                        )
                        if (paymentListResponse.properties != null && !paymentListResponse.properties?.isEmpty()!!) {
                            setList(paymentListResponse.properties!!)
                        } else {
                            btnTryAgain?.visibility = View.VISIBLE
                        }
                        pb_payment?.visibility = View.GONE
                    }

                    override fun onFailed(t: Throwable) {
                        // show error
                        Log.e("onFailure", t.toString())
                        btnTryAgain?.visibility = View.VISIBLE
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

    private fun setList(rootList: ArrayList<Properties>) {
        rv_ld_payments?.layoutManager = LinearLayoutManager(appContext)
        val list: MutableList<Properties>? = rootList.toMutableList()

        //val listPayment = ArrayList(list!!.reversed())
        val listPayment = ArrayList(list)
        ldPropertyListAdapter = MarketplaceListAdapter(
            appContext,
            this,
            listPayment
        )
        rv_ld_payments?.adapter = ldPropertyListAdapter
    }
}
