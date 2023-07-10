package mp.app.calonex.registration.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login_screen.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.UserDetailCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.landlord.activity.SubscribePlanDetailScreen.Companion.subcriptionList
import mp.app.calonex.landlord.adapter.SpinnerCustomAdapter
import mp.app.calonex.landlord.activity.SubscribePlanDetailScreen.Companion.subcriptionPlanList
import mp.app.calonex.landlord.model.SubscriptionDetail
import mp.app.calonex.landlord.model.UserDetail
import mp.app.calonex.landlord.response.UserDetailResponse
import mp.app.calonex.registration.adapter.SubscriptionPlanAdapter
import mp.app.calonex.registration.model.SubcriptionPlanModel
import mp.app.calonex.registration.response.SubcriptionPlanResponse
import mp.app.calonex.utility.HorizontalMarginItemDecoration
import params.com.stepprogressview.StepProgressView

//import params.com.stepprogressview.StepProgressView

class SelectSubcriptionPlan : AppCompatActivity() {
    private var spinnerUnits:Spinner?=null
    private var vpSubscripePlan: ViewPager2? = null
    private var tlSpIndicator: TabLayout?=null
    private var pbMsdPlan: ProgressBar?=null
    private var unitList=ArrayList<String>()
    private var isRegister:Boolean=false
    private var planList= ArrayList<SubcriptionPlanModel>()
    var subscriptionDetail = SubscriptionDetail()
    private lateinit var step_prog: StepProgressView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_subcription_plan)
        initComponent()
        actionComponent()
    }

    fun initComponent(){
        spinnerUnits=findViewById(R.id.spinner_sp_units)
        vpSubscripePlan=findViewById(R.id.vp_subs_pan)
        tlSpIndicator=findViewById(R.id.tl_sp_indicator)
        pbMsdPlan=findViewById(R.id.pb_msd_plan)
        step_prog = findViewById(R.id.step_prog)
        vpSubscripePlan!!.setPadding(1, 0, 1, 0);
        vpSubscripePlan!!.setClipToPadding(false);

        //getPlanSubcription()
        getUserInfo()
        setupCarousel()
        val HideStepBar = intent.getBooleanExtra("HideStepBar", false)
        if (HideStepBar) {
            step_prog.visibility = View.VISIBLE
        }

        var text_1 = findViewById<TextView>(R.id.text_1)
        text_1.setText(Html.fromHtml(getString(R.string.select_units)))

        isRegister=intent.getBooleanExtra(getString(R.string.is_register),false)

        if(isRegister){
            planList=subcriptionList
        }else{
            planList= subcriptionList
        }

        //LoginScreen.registrationPayload3.userId = Kotpref.userId

        var listUnit=mutableListOf<String>()

        listUnit.add("Select Number of Unit ")

        for (item in planList){
            listUnit.add(item.paymentPlanMinUnit+ "  -  " +item.paymentPlanMaxUnit)
        }
        unitList.addAll(ArrayList<String>(listUnit.toSet().toList()))
        val spinnerUnitAdapter =  SpinnerCustomAdapter(applicationContext,  unitList )
        spinnerUnits!!.adapter=spinnerUnitAdapter
      //  tlSpIndicator!!.setupWithViewPager(vpSubscripePlan)
    }

    fun actionComponent(){
        spinnerUnits?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {

                if (position> 0){
                    var value:String= unitList.get(position)
                    var planList:ArrayList<SubcriptionPlanModel>?=getUnitList(value.substringBefore("  -"),value.substringAfter("-  "))
                    vpSubscripePlan!!.adapter=SubscriptionPlanAdapter(applicationContext,isRegister,pbMsdPlan!!,this@SelectSubcriptionPlan,planList!!, userDetailResponse)

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // another interface callback
            }
        }

    }

    private fun  getUnitList(valueMin:String,valueMax:String):ArrayList<SubcriptionPlanModel> {
           var filterList:List<SubcriptionPlanModel>?=null

        filterList = planList.filter { it.paymentPlanMinUnit==valueMin &&  it.paymentPlanMaxUnit==valueMax}

        return  filterList as ArrayList<SubcriptionPlanModel>
    }

    private fun getPlanSubscription() {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            //Create retrofit Service
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )

            val signatureApi: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<SubcriptionPlanResponse> = signatureApi.getSubscription("")

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("fucks---", it.toString())
                    Log.e("onSuccess---", it.responseDto?.responseDescription.toString())

                    pb_login!!.visibility = View.GONE
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                    if (it.data != null) {
                        subcriptionPlanList = it.data!!

                        Log.e("asdf size", subcriptionPlanList.size.toString())

                        /*  val intent=Intent(this,SelectSubcriptionPlan::class.java)
                          intent.putExtra(getString(R.string.is_register),true)
                          intent.putExtra("HideStepBar", true)
                          startActivity(intent)
                          finish()

                          intent.putExtra("FromLandlord", true)
      */
                        //Util.navigationNext(this)
                    }
                },
                    { e ->
                        Log.e("onFailure---", e.toString())
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
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

    private fun setupCarousel(){

        vpSubscripePlan!!.offscreenPageLimit = 1

        val nextItemVisiblePx = resources.getDimension(R.dimen.viewpager_next_item_visible)
        val currentItemHorizontalMarginPx = resources.getDimension(R.dimen.viewpager_current_item_horizontal_margin)
        val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
        val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->
            page.translationX = -pageTranslationX * position
            page.scaleY = 1 - (0.25f * kotlin.math.abs(position))
            page.alpha = 0.25f + (1 - kotlin.math.abs(position))
        }
        vpSubscripePlan?.setPageTransformer(pageTransformer)
        val itemDecoration = HorizontalMarginItemDecoration(
            this,
            R.dimen.viewpager_current_item_horizontal_margin
        )
        vpSubscripePlan?.addItemDecoration(itemDecoration)
    }

    private fun getUserInfo() {
        //Create retrofit Service

        val credentials = UserDetailCredential()

        credentials.userId = Kotpref.userId
        val userDetailService: ApiInterface =
            ApiClient(this).provideService(ApiInterface::class.java)
        val apiCall: Observable<UserDetailResponse> =
            userDetailService.getUserDetail(credentials) //Test API Key

        RxAPICallHelper().call(apiCall, object : RxAPICallback<UserDetailResponse> {
            override fun onSuccess(userDetail: UserDetailResponse) {
                userDetailResponse = userDetail.data!!
                //setUserInfo()
            }

            override fun onFailed(t: Throwable) {
                // show error
                Log.e("onFailure", t.toString())
                //  progressBarList?.visibility = View.GONE
            }
        })
    }

    companion object{
        var userDetailResponse = UserDetail()

    }
}