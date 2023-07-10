package mp.app.calonex.registration.adapter

import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.item_applicant_tenant.view.*
import mp.app.calonex.LdRegBankAdd.CreateNewSubscriptionResponseModel
import mp.app.calonex.LdRegBankAdd.CreateSubscriptionModel
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.UpdateSubscriptionCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.SubscribePlanDetailScreen
import mp.app.calonex.landlord.activity.SubscribePlanDetailScreen.Companion.subscriptionDetail
import mp.app.calonex.landlord.model.UserDetail
import mp.app.calonex.registration.activity.UserContactDetailScreen
import mp.app.calonex.registration.model.SubcriptionPlanModel
import mp.app.calonex.registration.model.SubscriptionPayload
import mp.app.calonex.tenant.response.ResponseDtoResponse
import java.util.ArrayList

class SubscriptionPlanAdapter(
    val context: Context,
    val isRegister: Boolean?,
    val pbMsdPlan: ProgressBar?,
    val activity: Activity?,
    val spList: ArrayList<SubcriptionPlanModel>?,
    val userDetailResponse: UserDetail
) : RecyclerView.Adapter<SubscriptionPlanAdapter.ViewHolder>() {

    companion object {
        var subscriptionPayload = SubscriptionPayload()

    }

    private fun modifyValidation(position: Int) {
        var previousUnit = subscriptionDetail.numberOfUnits
        var previousDuration = subscriptionDetail.currentplan
        var price = spList!![position].finalPrice
        var selectUnit = spList!![position].paymentPlanMaxUnit
        var selectUnitID = spList!![position].paymentPlanId
        var selectDuration = spList!![position].subscriptionPlanDuration
        if (selectUnit.equals("100+")) {
            selectUnit = "101"
        }
        if (previousUnit.isNullOrEmpty())
            previousUnit = "0"

        if (previousUnit.equals("100+")) {
            previousUnit = "101"
        }
        if (previousDuration.isNullOrEmpty())
            previousDuration = "0"

        if (selectDuration.isNullOrEmpty())
            selectDuration = "0"

        try {
            if ((selectUnit.equals(previousUnit) && selectDuration.equals(previousDuration)) ||
                (selectUnit!!.toInt() < previousUnit!!.toInt() && selectDuration!!.toInt() < previousDuration!!.toInt())
            ) {
                Util.alertOkMessage(
                    activity!!,
                    context.getString(R.string.alert),
                    context.getString(R.string.error_modify_plan)
                )
                return
            } else if ((selectUnit!!.toInt() < previousUnit!!.toInt() && selectDuration.equals(
                    previousDuration
                )) ||
                (selectUnit!!.toInt() < previousUnit!!.toInt() && selectDuration!!.toInt() > previousDuration!!.toInt())
            ) {
                Util.alertOkMessage(
                    activity!!,
                    context.getString(R.string.alert),
                    context.getString(R.string.error_modify_unit_duration)
                )
                return
            } else if (selectUnit!!.toInt() > previousUnit!!.toInt() && selectDuration.equals(
                    previousDuration
                )
            ) {
                selectDuration = "0"

            } else if ((selectUnit.equals(previousUnit) && selectDuration!!.toInt() < previousDuration!!.toInt()) ||
                (selectUnit!!.toInt() > previousUnit!!.toInt() && selectDuration!!.toInt() < previousDuration!!.toInt())
            ) {
                Util.alertOkMessage(
                    activity!!,
                    context.getString(R.string.alert),
                    context.getString(R.string.error_modify_unit_duration)
                )
                return
            } else if (selectUnit.equals(previousUnit) && selectDuration!!.toInt() > previousDuration!!.toInt()) {
                selectUnit = "0"

            }
            if (selectUnit.equals("101")) {
                selectUnit = "100+"
            }

            if (subscriptionDetail.price == "0") {
                createSubscription(selectUnit, selectDuration, price!!, selectUnitID!!)
            } else {
                modifySubscription(selectUnit!!, selectDuration!!)

            }

            //modifySubscription(selectUnit2!!.toString(),selectDuration!!)

        } catch (e: Exception) {
            e.message
        }

    }

    private fun createSubscription(
        unitSelect: String,
        durationSelect: String,
        price: String,
        unitID: String
    ) {
        if (NetworkConnection.isNetworkConnected(activity!!)) {
            pbMsdPlan?.visibility = View.VISIBLE
            activity.window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )

            val credentials = CreateSubscriptionModel()
            credentials.userCatalogId = Kotpref.userId
            credentials.numberOfUnits = unitSelect
            credentials.subscriptionPlanDuration = durationSelect
            //Other Details
            credentials.finalPrice = price
            credentials.paymentPlanId = unitID
            credentials.isAutoPay = "1"
            credentials.isCreditCard = false
            credentials.bankAccountNumber = userDetailResponse.bankAccountNo
            credentials.routingNumber = userDetailResponse.routingNo


            val signatureApi: ApiInterface =
                ApiClient(activity!!).provideService(ApiInterface::class.java)
            val apiCall: Observable<CreateNewSubscriptionResponseModel> =
                signatureApi.createNewSubscription(credentials)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", it.responseDto?.responseDescription.toString())


                    pbMsdPlan?.visibility = View.GONE
                    activity!!.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                    if (it!!.responseDto!!.responseCode.equals(200)) {
                        Util.alertOkIntentMessage(
                            activity!!,
                            "Alert",
                            "Your plan has been modified successfully",
                            SubscribePlanDetailScreen::class.java
                        )
                    } else if (it!!.responseDto!!.responseCode.equals(201)) {
                        Util.alertOkIntentMessage(
                            activity!!,
                            "Alert",
                            "Your plan has been modified successfully",
                            SubscribePlanDetailScreen::class.java
                        )
                    } else {
                        Toast.makeText(
                            context,
                            it.responseDto!!.responseDescription,
                            Toast.LENGTH_SHORT
                        ).show()
                    }


                },
                    { e ->
                        Log.e("onFailure", e.toString())
                        pbMsdPlan?.visibility = View.GONE
                        activity!!.getWindow()
                            .clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        e.message?.let {
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        }
                    })
        } else {
            Toast.makeText(context, context.getString(R.string.error_network), Toast.LENGTH_SHORT)
                .show()

        }
    }

    private fun modifySubscription(unitSelect: String, durationSelect: String) {
        if (NetworkConnection.isNetworkConnected(activity!!)) {
            pbMsdPlan?.visibility = View.VISIBLE
            activity.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )

            val credentials = UpdateSubscriptionCredential()
            credentials.userCatalogId = Kotpref.userId
            credentials.numberOfUnit = unitSelect
            credentials.subscriptionPlanDuration = durationSelect

            val signatureApi: ApiInterface =
                ApiClient(activity!!).provideService(ApiInterface::class.java)
            val apiCall: Observable<ResponseDtoResponse> =
                signatureApi.updateSubscription(credentials)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", it.responseDto?.responseDescription.toString())


                    pbMsdPlan?.visibility = View.GONE
                    activity!!.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                    if (it!!.responseDto!!.responseCode.equals(200)) {
                        Util.alertOkIntentMessage(
                            activity!!,
                            "Alert",
                            "Your plan has been modified successfully",
                            SubscribePlanDetailScreen::class.java
                        )
                    } else {
                        Toast.makeText(
                            context,
                            it.responseDto!!.responseDescription,
                            Toast.LENGTH_SHORT
                        ).show()
                    }


                },
                    { e ->
                        Log.e("onFailure", e.toString())
                        pbMsdPlan?.visibility = View.GONE
                        activity!!.getWindow()
                            .clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        e.message?.let {
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        }
                    })
        } else {
            Toast.makeText(context, context.getString(R.string.error_network), Toast.LENGTH_SHORT)
                .show()

        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtItemPlanDuration = itemView.findViewById(R.id.txt_item_plan_duration) as TextView
        val txtItemPrice = itemView.findViewById(R.id.txt_item_sp_price) as TextView
        val txtItemDiscount = itemView.findViewById(R.id.txt_item_sp_discount) as TextView
        val txtItemFinalPrice = itemView.findViewById(R.id.txt_item_sp_final_price) as TextView
        val btnSelectPlan = itemView.findViewById(R.id.btn_select_plan) as Button

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_subcription_plan, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return spList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == 0) {
            holder.txtItemPlanDuration.background =
                ActivityCompat.getDrawable(context, R.drawable.btn_dk_blue_2_round_new)
        } else if (position == 1) {
            holder.txtItemPlanDuration.background =
                ActivityCompat.getDrawable(context, R.drawable.btn_dk_green_2_round_new)
        } else if (position == 2) {
            holder.txtItemPlanDuration.background =
                ActivityCompat.getDrawable(context, R.drawable.btn_dk_yellow_2_round_new)
        } else {
            holder.txtItemPlanDuration.background =
                ActivityCompat.getDrawable(context, R.drawable.btn_dk_blue_2_round_new)
        }

        holder.txtItemPlanDuration.text = spList!![position].subscriptionPlanDuration + " Year Plan"
        holder.txtItemPrice.text = "$ " + spList!![position].price
        holder.txtItemPrice.paintFlags =
            holder.txtItemPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        if (spList!![position].currentPlanText.equals("5+1")) {
            holder.txtItemDiscount.text = spList!![position].discount + " % + 1 yr free"
        } else {
            holder.txtItemDiscount.text = spList!![position].discount + " %"
        }

        holder.txtItemFinalPrice.text = "$ " + spList!![position].finalPrice
        if (isRegister!!) {
            holder.btnSelectPlan!!.text = context.getString(R.string.select)
        } else {
            holder.btnSelectPlan!!.text = context.getString(R.string.upgrade)
        }


        holder.btnSelectPlan.setOnClickListener {
            if (isRegister!!) {
                /*   subscriptionPayload.finalPrice = spList!![position].finalPrice
                   subscriptionPayload.numberOfUnits = spList!![position].paymentPlanMaxUnit
                   subscriptionPayload.subscriptionPlanDuration = spList!![position].subscriptionPlanDuration
                   Kotpref.planSelected = subscriptionPayload.subscriptionPlanDuration + " year plan | $" + subscriptionPayload.finalPrice
                   Util.navigationNext(activity!!, UserContactDetailScreen::class.java)*/

                //Changed 3rd June 2023
                modifyValidation(position)
            } else {

                modifyValidation(position)

            }
        }

    }

}