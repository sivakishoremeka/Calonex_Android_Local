package mp.app.calonex.agentBrokerRegLd

/*import mp.app.calonex.landlord.activity.SubscribePlanDetailScreen
import mp.app.calonex.landlord.activity.SubscribePlanDetailScreen.Companion.subscriptionDetail*/
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.viewpager.widget.PagerAdapter
import mp.app.calonex.R
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.Util
import mp.app.calonex.registration.model.SubcriptionPlanModel
import mp.app.calonex.registration.model.SubscriptionPayload

class SubscriptionPlanAdapterNew(
    val context: Context,
    val isRegister: Boolean?,
    val pbMsdPlan: ProgressBar?,
    val activity: Activity?,
    val spList: ArrayList<SubcriptionPlanModel>?
) : PagerAdapter() {
    private val inflater: LayoutInflater

    init {
        inflater = LayoutInflater.from(context)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return spList!!.size
    }

    override fun instantiateItem(view: ViewGroup, position: Int): Any {
        val spLayout = inflater.inflate(R.layout.item_subcription_plan, view, false)!!
        val txtItemPlanDuration = spLayout.findViewById(R.id.txt_item_plan_duration) as TextView
        val txtItemPrice = spLayout.findViewById(R.id.txt_item_sp_price) as TextView
        val txtItemDiscount = spLayout.findViewById(R.id.txt_item_sp_discount) as TextView
        val txtItemFinalPrice = spLayout.findViewById(R.id.txt_item_sp_final_price) as TextView
        val btnSelectPlan = spLayout.findViewById(R.id.btn_select_plan) as Button

        if (position == 0) {
            txtItemPlanDuration.background = ActivityCompat.getDrawable(context, R.drawable.btn_dk_blue_2_round_new)
        } else if (position == 1) {
            txtItemPlanDuration.background = ActivityCompat.getDrawable(context, R.drawable.btn_dk_green_2_round_new)
        } else if (position == 2) {
            txtItemPlanDuration.background = ActivityCompat.getDrawable(context, R.drawable.btn_dk_yellow_2_round_new)
        } else {
            txtItemPlanDuration.background = ActivityCompat.getDrawable(context, R.drawable.btn_dk_blue_2_round_new)
        }

        txtItemPlanDuration.text = spList!![position].subscriptionPlanDuration + " Year Plan"
        txtItemPrice.text = "$ " + spList[position].price
        txtItemPrice.paintFlags = txtItemPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        if (spList[position].currentPlanText.equals("5+1")) {
            txtItemDiscount.text = spList[position].discount + " % + 1 yr free"
        } else {
            txtItemDiscount.text = spList[position].discount + " %"
        }

        txtItemFinalPrice.text = "$ " + spList[position].finalPrice
        if (isRegister!!) {
            btnSelectPlan.text = context.getString(R.string.select)
        } else {
            btnSelectPlan.text = context.getString(R.string.upgrade)
        }

        view.addView(spLayout, 0)

        btnSelectPlan.setOnClickListener {
            if (isRegister) {
                subscriptionPayload.finalPrice = spList[position].finalPrice
                subscriptionPayload.numberOfUnits = spList[position].paymentPlanMaxUnit
                subscriptionPayload.subscriptionPlanDuration =
                    spList[position].subscriptionPlanDuration
                Kotpref.planSelected =
                    subscriptionPayload.subscriptionPlanDuration + " year plan | $" + subscriptionPayload.finalPrice
                //Util.navigationNext(activity!!, UserContactDetailScreenNew::class.java)
                Util.navigationNext(activity!!, LoginCredentialScreenNew::class.java)
            }/*else{
                modifyValidation(position)
            }*/
        }

        return spLayout
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}

    override fun saveState(): Parcelable? {
        return null
    }

    companion object {
        var subscriptionPayload = SubscriptionPayload()

    }

    /*private fun modifyValidation(position: Int){
        var previousUnit=subscriptionDetail.numberOfUnits
        var previousDuration=subscriptionDetail.currentplan
        var selectUnit=spList!![position].paymentPlanMaxUnit
        var selectDuration=spList!![position].subscriptionPlanDuration
        if(selectUnit.equals("100+")){
            selectUnit="101"
        }
        if(previousUnit.equals("100+")){
            previousUnit="101"
        }

        if((selectUnit.equals(previousUnit) && selectDuration.equals(previousDuration)) ||
            (selectUnit!!.toInt()<previousUnit!!.toInt() && selectDuration!!.toInt()<previousDuration!!.toInt())){
            Util.alertOkMessage(activity!!,context.getString(R.string.alert),context.getString(R.string.error_modify_plan))
            return
        }else if((selectUnit!!.toInt()<previousUnit!!.toInt() && selectDuration.equals(previousDuration) )||
            (selectUnit!!.toInt()<previousUnit!!.toInt() && selectDuration!!.toInt()>previousDuration!!.toInt())){
                Util.alertOkMessage(activity!!,context.getString(R.string.alert),context.getString(R.string.error_modify_unit_duration))
                return
        }else if(selectUnit!!.toInt()>previousUnit!!.toInt() && selectDuration.equals(previousDuration) ){
            selectDuration="0"

        }else if((selectUnit.equals(previousUnit) && selectDuration!!.toInt()<previousDuration!!.toInt() )||
            (selectUnit!!.toInt()>previousUnit!!.toInt() && selectDuration!!.toInt()<previousDuration!!.toInt())){
            Util.alertOkMessage(activity!!,context.getString(R.string.alert),context.getString(R.string.error_modify_unit_duration))
            return
        }else if(selectUnit.equals(previousUnit) && selectDuration!!.toInt()>previousDuration!!.toInt()){
            selectUnit="0"

        }
        if(selectUnit.equals("101")){
            selectUnit="100+"
        }
        modifySubscription(selectUnit!!,selectDuration!!)


    }*/

    /*private fun modifySubscription(unitSelect:String,durationSelect:String) {
        if (NetworkConnection.isNetworkConnected(activity!!)) {
            pbMsdPlan?.visibility= View.VISIBLE
            activity.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            val credentials = UpdateSubscriptionCredential()
            credentials.userCatalogId= Kotpref.userId

            credentials.numberOfUnit=unitSelect
            credentials.subscriptionPlanDuration=durationSelect
            val signatureApi: ApiInterface = ApiClient(activity!!).provideService(ApiInterface::class.java)
            val apiCall: Observable<ResponseDtoResponse> =
                signatureApi.updateSubscription(credentials)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
                Log.e("onSuccess", it.responseDto?.responseDescription.toString())


                pbMsdPlan?.visibility= View.GONE
                activity!!.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                if(it!!.responseDto!!.responseCode.equals(200)) {
                    Util.alertOkIntentMessage(
                        activity!!,
                        "Alert",
                        "Your plan has been modified successfully",
                        SubscribePlanDetailScreen::class.java
                    )
                }else{
                    Toast.makeText(context, it.responseDto!!.responseDescription, Toast.LENGTH_SHORT).show()
                }



            },
                { e ->
                    Log.e("onFailure", e.toString())
                    pbMsdPlan?.visibility= View.GONE
                    activity!!.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    e.message?.let {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            Toast.makeText(context, context.getString(R.string.error_network), Toast.LENGTH_SHORT).show()

        }
    }*/

}