package mp.app.calonex.landlord.fragment


import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.Observable
import kotlinx.android.synthetic.main.fragment_ld_property.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.GetAvailableUnitApiCredential
import mp.app.calonex.common.apiCredentials.GetPropertyListApiCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.AddNewPropertyFirstScreen
import mp.app.calonex.landlord.activity.NotifyScreen
import mp.app.calonex.landlord.adapter.PropertyListAdapter
import mp.app.calonex.landlord.model.Property
import mp.app.calonex.landlord.response.AvailableUnitResponse
import mp.app.calonex.landlord.response.PropertyListResponse
import mp.app.calonex.service.FeaturesService
import mp.app.calonex.service.FeaturesService.Companion.allModel


class PropertyLdFragment : Fragment() {
    val BackgroundIntentServiceAction = "android.intent.action.CUSTOME_ACTION_1"
    private val TAG: String =
        PropertyLdFragment::class.java.getSimpleName()

    private lateinit var appContext: Context
    private var ldPropertyListAdapter: PropertyListAdapter? = null
    private var searchProperty: SearchView? = null
    private var rvLandlordProperty: RecyclerView? = null
    private var refreshProperties: SwipeRefreshLayout? = null
    private var layoutAddProperty: LinearLayout? = null
    private var btnTryAgain: Button? = null
    private var layoutLpNotify: RelativeLayout? = null
    private var txtLpNotify: TextView? = null
    private var profilePic: CircleImageView? = null

    private var allowRefresh = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext = context

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_ld_property, container, false)
        initComponents(rootView)

        return rootView
    }

    override fun onResume() {
        super.onResume()
        if (allowRefresh) {
            allowRefresh = false
            //listResponse.clear() //clear array
            if (rvLandlordProperty?.adapter != null) {
                rvLandlordProperty?.adapter?.notifyDataSetChanged() // notify adapter
                getPropertyList() // getdatas from service
            }
        }

        getPropertyList()
        getAvailableUnit()
    }

    override fun onPause() {
        super.onPause()
        if (!allowRefresh) {
            allowRefresh = true
        }
    }

    private fun initComponents(viewRoot: View) {
        profilePic=viewRoot.findViewById(R.id.profile_pic)
        Glide.with(appContext)
            .load(Kotpref.profile_image)
            .placeholder(R.drawable.profile_default_new)
            .into(profilePic!!)
        searchProperty = viewRoot.findViewById(R.id.search_ld_property)
        rvLandlordProperty = viewRoot.findViewById(R.id.rv_ld_properties)
        refreshProperties = viewRoot.findViewById(R.id.refresh_ld_properties)
        layoutAddProperty = viewRoot.findViewById(R.id.layout_add_property)
        btnTryAgain = viewRoot.findViewById(R.id.btn_try_again)
        layoutLpNotify = viewRoot.findViewById(R.id.layout_lp_notify)
        txtLpNotify = viewRoot.findViewById(R.id.txt_lp_notify)

        val searchTextViewId: Int = searchProperty!!.getContext().getResources()
            .getIdentifier("android:id/search_src_text", null, null)
        val searchText = searchProperty!!.findViewById(searchTextViewId) as EditText
        searchText!!.setTextColor(Color.WHITE)

        val dividerItemDecoration =
            DividerItemDecoration(rvLandlordProperty!!.context, DividerItemDecoration.VERTICAL)
        dividerItemDecoration.setDrawable(
            ContextCompat.getDrawable(requireActivity(), R.drawable.item_list_divider)!!
        )


        rvLandlordProperty!!.addItemDecoration(dividerItemDecoration)

        //** Set the colors of the Pull To Refresh View
        refreshProperties?.setProgressBackgroundColorSchemeColor(
            ContextCompat.getColor(
                appContext,
                R.color.colorPrimary
            )
        )
        refreshProperties?.setColorSchemeColors(Color.WHITE)


        actionComponent()
    }

    override fun onStart() {
        super.onStart()
        /*if(Kotpref.notifyCount!=null && !Kotpref.notifyCount.isEmpty() && Integer.parseInt(Kotpref.notifyCount)>0)
        txtLpNotify!!.text= Kotpref.notifyCount*/

        if (Kotpref.notifyCount != null && !Kotpref.notifyCount.isEmpty() && Integer.parseInt(
                Kotpref.notifyCount
            ) > 0
        ) {
            txtLpNotify!!.text = Kotpref.notifyCount
            txtLpNotify!!.visibility = View.VISIBLE
        } else {
            txtLpNotify!!.visibility = View.GONE
        }
    }


    private fun actionComponent() {
        searchProperty!!.setOnClickListener {
            searchProperty!!.isIconified = false
        }

        searchProperty!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty() && ldPropertyListAdapter != null) {
                    ldPropertyListAdapter!!.filter.filter(newText)

                } else {
                    if (ldPropertyListAdapter != null)
                        ldPropertyListAdapter!!.filter.filter("")

                }
                return true
            }

        })

        refreshProperties?.setOnRefreshListener {

            getPropertyList()
            refresh_ld_properties?.isRefreshing = false

        }

        layoutAddProperty!!.setOnClickListener {
            Kotpref.isAddPropertyFromLd = true
            Util.navigationNext(requireActivity(), AddNewPropertyFirstScreen::class.java)
        }

        layoutLpNotify!!.setOnClickListener {
            Util.navigationNext(requireActivity(), NotifyScreen::class.java)
        }
        btnTryAgain?.setOnClickListener {
            btnTryAgain?.visibility = View.GONE
            getPropertyList()
        }
    }

    private fun getPropertyList() {
        if (NetworkConnection.isNetworkConnected(appContext)) {
            //Create retrofit Service
            pb_property?.visibility = View.VISIBLE
            var credentials = GetPropertyListApiCredential()

            credentials!!.userCatalogID = Kotpref.userId
            var propertyListService: ApiInterface =
                ApiClient(appContext).provideService(ApiInterface::class.java)
            var apiCall: Observable<PropertyListResponse> =
                propertyListService.getPropertyList(credentials)

            RxAPICallHelper().call(apiCall, object : RxAPICallback<PropertyListResponse> {
                override fun onSuccess(propertyListResponse: PropertyListResponse) {
                    Log.e("onSuccessProperty", propertyListResponse.responseCode.toString())
                    if (!propertyListResponse.data?.isEmpty()!!) {
                        setPropertyList(propertyListResponse.data!!)
                        refreshProperties!!.visibility = View.VISIBLE
                    } else {
                        refreshProperties!!.visibility = View.GONE
                        btnTryAgain?.visibility = View.VISIBLE
                    }
                    pb_property?.visibility = View.GONE
                    if (allModel == null) {
                        val intent = Intent(appContext, FeaturesService::class.java).apply {
                            setAction(BackgroundIntentServiceAction)
                        }
                        appContext.startService(intent)

                    }

                }

                override fun onFailed(t: Throwable) {
                    // show error
                    Log.e("onFailure", t.toString())
                    pb_property?.visibility = View.GONE

                    try {
                        Util.apiFailure(appContext, t)
                    } catch (e: Exception) {
                        Toast.makeText(
                            appContext,
                            getString(R.string.error_server),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    refreshProperties!!.visibility = View.GONE
                    btnTryAgain!!.visibility = View.VISIBLE
                }
            })
        } else {
            Toast.makeText(appContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show()

        }
    }


    private fun setPropertyList(listResponse: ArrayList<Property>) {
        try {
            rvLandlordProperty?.layoutManager = LinearLayoutManager(appContext)
            if (listResponse.size == 1) {
                ldPropertyListAdapter = PropertyListAdapter(
                    appContext,
                    activity,
                    pb_property!!,
                    listResponse as ArrayList<Property>
                )
            } else {
                ldPropertyListAdapter = PropertyListAdapter(
                    appContext,
                    activity,
                    pb_property!!,
                    listResponse.reversed() as ArrayList<Property>
                )
            }

            txt_total_properties.setText("Showing: " + listResponse.size.toString() + " Properties")
            rvLandlordProperty?.adapter = ldPropertyListAdapter
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getAvailableUnit() {
        if (NetworkConnection.isNetworkConnected(appContext)) {
            //Create retrofit Service
            var credentials = GetAvailableUnitApiCredential()

            credentials!!.userCatalogId = Kotpref.userId
            var propertyListService: ApiInterface =
                ApiClient(appContext).provideService(ApiInterface::class.java)
            var apiCall: Observable<AvailableUnitResponse> =
                propertyListService.getAvailableUnit(credentials)
            RxAPICallHelper().call(apiCall, object : RxAPICallback<AvailableUnitResponse> {
                override fun onSuccess(availableUnitResponse: AvailableUnitResponse) {
                    Log.e("TAG", availableUnitResponse.toString())

                    Kotpref.unitNumber = availableUnitResponse.data.toString()

                    Log.d(TAG, "onSuccess:number of unit " + Kotpref.unitNumber)


                }

                override fun onFailed(t: Throwable) {
                    // show error
                    Log.e("onFailure", t.toString())
                    Kotpref.unitNumber = "0"
                    /*
                    try {
                        Util.apiFailure(appContext, t)
                    } catch (e: Exception) {
                        Toast.makeText(
                            appContext,
                            getString(R.string.error_server),
                            Toast.LENGTH_SHORT
                        ).show()
                    }*/

                }
            })
        } else {
            Toast.makeText(appContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show()

        }
    }

}