package mp.app.calonex.LdRegBankAdd

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.AdapterView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import mp.app.calonex.R
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.landlord.activity.LoginScreen
import mp.app.calonex.landlord.adapter.SpinnerCustomAdapter
import mp.app.calonex.landlord.activity.LoginScreen.Companion.subcriptionPlanList
import mp.app.calonex.registration.activity.SelectSubcriptionPlan
import mp.app.calonex.registration.model.SubcriptionPlanModel
import params.com.stepprogressview.StepProgressView

class SelectSubcriptionPlanBankAdd : AppCompatActivity() {

    private var spinnerUnits:Spinner?=null
    private var vpSubscripePlan: ViewPager? = null
    private var tlSpIndicator: TabLayout?=null
    private var pbMsdPlan: ProgressBar?=null
    private var unitList=ArrayList<String>()
    private var isRegister:Boolean=false
    private var planList= ArrayList<SubcriptionPlanModel>()

    private lateinit var step_prog: StepProgressView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_subcription_plan_bank_add)
        initComponent()
        actionComponent()
    }

    fun initComponent(){
        spinnerUnits=findViewById(R.id.spinner_sp_units)
        vpSubscripePlan=findViewById(R.id.vp_subs_pan)
        tlSpIndicator=findViewById(R.id.tl_sp_indicator)
        pbMsdPlan=findViewById(R.id.pb_msd_plan)

        step_prog = findViewById(R.id.step_prog)

        var text_1 = findViewById<TextView>(R.id.text_1)
        text_1.setText(Html.fromHtml(getString(R.string.select_units)))

        isRegister=intent.getBooleanExtra(getString(R.string.is_register),false)

        planList=subcriptionPlanList

        LoginScreen.bankAddModelLL.userCatalogId = Kotpref.userId

        var listUnit=mutableListOf<String>()

        listUnit.add("Select Number of Unit ")

        for (item in planList){
            listUnit.add(item.paymentPlanMinUnit+ "  -  " +item.paymentPlanMaxUnit)
        }
        unitList.addAll(ArrayList<String>(listUnit.toSet().toList()))
        val spinnerUnitAdapter =  SpinnerCustomAdapter(
            applicationContext,
            unitList
        )
        spinnerUnits!!.adapter=spinnerUnitAdapter
        tlSpIndicator!!.setupWithViewPager(vpSubscripePlan)
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
                    vpSubscripePlan!!.adapter=SubscriptionPlanAdapterAddBank(applicationContext,isRegister!!,pbMsdPlan!!,this@SelectSubcriptionPlanBankAdd,planList!!)

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

}