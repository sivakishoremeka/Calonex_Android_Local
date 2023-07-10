package mp.app.calonex.LdRegBankAdd

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
import mp.app.calonex.landlord.activity.LoginScreen.Companion.bankAddModelLL
import mp.app.calonex.registration.model.SubcriptionPlanModel

class SubscriptionPlanAdapterAddBank(
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
                bankAddModelLL.finalPrice = spList[position].finalPrice
                bankAddModelLL.numberOfUnits = spList[position].paymentPlanMaxUnit
                bankAddModelLL.subscriptionPlanDuration =
                    spList[position].subscriptionPlanDuration
                Kotpref.planSelected =
                    bankAddModelLL.subscriptionPlanDuration + " year plan | $" + bankAddModelLL.finalPrice
                //Util.navigationNext(activity!!, UserContactDetailScreenNew::class.java)
                Util.navigationNext(activity!!, UserAccountScreenBankAdd::class.java)
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
}