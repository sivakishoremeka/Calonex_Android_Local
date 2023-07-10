package mp.app.calonex.landlord.activity

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_bookkeeping_agent.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.LandlordPaymentHistoryCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.model.ResponseDto
import mp.app.calonex.landlord.model.SubscriptionDetail
import mp.app.calonex.landlord.response.SubscriptionDetailResponse
import mp.app.calonex.registration.activity.SelectSubcriptionPlan
import mp.app.calonex.registration.model.SubcriptionPlanModel
import mp.app.calonex.registration.response.SubcriptionPlanResponse
import retrofit2.HttpException
import java.util.*
import kotlin.collections.ArrayList

class SubscribePlanDetailScreen : CxBaseActivity2() {

    private var editSdCurrentPlan: TextInputEditText? = null
    private var editSdUnit: TextInputEditText? = null
    private var editSdPrice: TextInputEditText? = null
    private var editSdRenew: TextInputEditText? = null
    private var txtSdStatus: TextView? = null
    private var layoutSdBtns: LinearLayout? = null
    private var btnSdCancel: TextView? = null
    private var btnSdModify: TextView? = null
    private var pbSdPlan: ProgressBar? = null
    private var renewDate: String? = ""
    private var layoutDetail: LinearLayout? = null
    private var btnSDUndo: Button? = null
    private var header_back: ImageView? = null
    private var tv_free_subcription_note: TextView? = null
    val myFormat = "MMM dd, yyyy"
    var fromAddUnitPage = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscribe_plan_detail_screen)
        fromAddUnitPage = intent.getBooleanExtra("fromAddUnitPage", false)
        try {
            initComponent()
            actionComponent()
            startHandler()
        } catch (e: Exception) {
            Log.e("Plan Detail", e.message.toString())
        }

    }

    private fun initComponent() {
        tv_free_subcription_note = findViewById(R.id.tv_free_subcription_note);
        header_back = findViewById(R.id.header_back);
        layoutDetail = findViewById(R.id.layout_detail)
        editSdCurrentPlan = findViewById(R.id.edit_sd_curnt_plan)
        editSdUnit = findViewById(R.id.edit_sd_unit)
        editSdPrice = findViewById(R.id.edit_sd_price)
        editSdRenew = findViewById(R.id.edit_sd_renw_date)
        txtSdStatus = findViewById(R.id.txt_sd_status)
        layoutSdBtns = findViewById(R.id.layout_subs_btn)
        btnSdCancel = findViewById(R.id.btn_sd_cancel)
        btnSdModify = findViewById(R.id.btn_sd_modfy)
        pbSdPlan = findViewById(R.id.pb_sd_plan)
        btnSDUndo = findViewById(R.id.btn_sd_undo)
        Util.setEditReadOnly(editSdCurrentPlan!!, false, InputType.TYPE_NULL)
        Util.setEditReadOnly(editSdUnit!!, false, InputType.TYPE_NULL)
        Util.setEditReadOnly(editSdPrice!!, false, InputType.TYPE_NULL)
        Util.setEditReadOnly(editSdRenew!!, false, InputType.TYPE_NULL)
    }

    private fun actionComponent() {
        header_back?.setOnClickListener {
            if (fromAddUnitPage) {
                super.onBackPressed()
                Util.navigationBack(this@SubscribePlanDetailScreen)
            } else {
                super.onBackPressed()
                // Util.navigationBack(this)
                Util.navigationBack(this)
            }
        }



        btnSdCancel!!.setOnClickListener {
            cancelSubscriptionDialog()
        }
        btnSdModify!!.setOnClickListener {

            Log.e("CheckBank", "Added or not==> " + Kotpref.bankAdded)
            Log.e("CheckBank", "Verified or not==> " + Kotpref.bankAccountVerified)

            if (Kotpref.bankAccountVerified) {
                if ((!Kotpref.bankAdded)) {
                    /*val intent = Intent(this, SelectSubcriptionPlan::class.java)
                intent.putExtra(getString(R.string.is_register), true)
                intent.putExtra("HideStepBar", true)
                intent.putExtra("FromLandlord", true)
                startActivity(intent)
                finish()*/
                    getPlanSubcription()
                } else
                    modifySubscriptionDialog()
            } else {
                bankAddDialog()
                /*val intent =
                    Intent(this, AccountLinkDetailScreen::class.java)
                startActivity(intent)*/
            }
        }
        btnSDUndo!!.setOnClickListener {
            resumeSubscription()
        }
    }

    private fun bankAddDialog() {
        val mBuilder = AlertDialog.Builder(this)
        mBuilder.setTitle("Error")
        mBuilder.setMessage("Your Bank information is not added or verified yet. Please check and verify your account details.")
        mBuilder.setPositiveButton("Ok") { dialog, which ->
            dialog.dismiss()
            Util.navigationNext(this, AccountLinkDetailScreen::class.java)
        }

        mBuilder.show()
    }

    override fun onResume() {
        super.onResume()
        getDetailSubscription()
    }

    private fun updateUi() {

        try {
            if (subscriptionDetail.currentPlanText.isNullOrEmpty()) {
                editSdCurrentPlan!!.setText("${subscriptionDetail.currentplan} year")
            } else {
                editSdCurrentPlan!!.setText("${subscriptionDetail.currentPlanText} year")
            }
            if (subscriptionDetail.numberOfUnits.equals("101")) {
                editSdUnit!!.setText("100+")
            } else {
                editSdUnit!!.setText(subscriptionDetail.numberOfUnits)
            }
            editSdPrice!!.setText("$ ${subscriptionDetail.price}")
            renewDate = Util.convertLongToTime(
                subscriptionDetail.planRenewalDate!!.toLong(),
                "MMM dd, yyyy"
            )
            editSdRenew!!.setText(renewDate)

            Kotpref.unitNumber = (subscriptionDetail.numberOfUnits).toString()
            if (subscriptionDetail.subscriptionCancel!!) {
                txtSdStatus!!.visibility = View.VISIBLE
                txtSdStatus!!.text = subscriptionDetail.statusPlan
                btnSDUndo!!.visibility = View.VISIBLE
                layoutSdBtns!!.visibility = View.GONE
            } else {
                txtSdStatus!!.visibility = View.GONE
                btnSDUndo!!.visibility = View.GONE
                layoutSdBtns!!.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun cancelSubscriptionDialog() {
        val mBuilder = AlertDialog.Builder(this)
        mBuilder.setTitle(getString(R.string.cancel_subs))
        mBuilder.setMessage(getString(R.string.tag_cancel_plan1) + " " + renewDate + getString(R.string.tag_cancel_plan2))


        mBuilder.setPositiveButton(getString(R.string.confirm)) { dialog, which ->
            cancelSubscription()
            dialog.dismiss()
        }

        mBuilder.setNegativeButton(getString(R.string.cancel)) { dialog, which ->
            dialog.dismiss()
        }

        mBuilder.show()

    }

    private fun modifySubscriptionDialog() {
        val mBuilder = AlertDialog.Builder(this)
        mBuilder.setTitle(getString(R.string.modify_subs))
        mBuilder.setMessage(getString(R.string.tag_modify_plan))


        mBuilder.setPositiveButton(getString(R.string.view)) { dialog, which ->
            getPlanSubcription()
            dialog.dismiss()
        }

        mBuilder.setNegativeButton(getString(R.string.cancel)) { dialog, which ->
            dialog.dismiss()
        }

        mBuilder.show()

    }


    private fun cancelSubscription() {
        if (NetworkConnection.isNetworkConnected(this)) {
            pbSdPlan!!.visibility = View.VISIBLE
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            val credentials = LandlordPaymentHistoryCredential()
            credentials.userCatalogId = Kotpref.userId
            val signatureApi: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<ResponseDto> =
                signatureApi.cancelSubscription(credentials)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    //  Log.e("onSuccess", it.responseDto?.responseDescription.toString())


                    pbSdPlan!!.visibility = View.GONE
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                    if (it!!.responseCode!!.equals(200)) {
                        //Util.alertOkMessage(applicationContext,getString(R.string.alert),)


                        getDetailSubscription()
                    }


                },
                    { e ->
                        Log.e("onFailure", e.toString())
                        pbSdPlan!!.visibility = View.GONE
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        e.message?.let {
                            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                        }
                    })
        } else {
            Toast.makeText(this, getString(R.string.error_network), Toast.LENGTH_SHORT).show()

        }
    }

    private fun resumeSubscription() {
        if (NetworkConnection.isNetworkConnected(this)) {
            pbSdPlan!!.visibility = View.VISIBLE
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            val credentials = LandlordPaymentHistoryCredential()
            credentials.userCatalogId = Kotpref.userId
            val signatureApi: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<ResponseDto> =
                signatureApi.planResubscribe(credentials)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                    pbSdPlan!!.visibility = View.GONE
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                    if (it!!.responseCode!!.equals(200)) {
                        //Util.alertOkMessage(applicationContext,getString(R.string.alert),)
                        Util.alertOkMessage(
                            this,
                            getString(R.string.alert),
                            getString(R.string.undo_tag)
                        )
                        getDetailSubscription()

                    }


                },
                    { e ->
                        Log.e("onFailure", e.toString())
                        pbSdPlan!!.visibility = View.GONE
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        val exception: HttpException = e as HttpException
                        try {
                            if (exception.code().equals(409)) {
                                Toast.makeText(
                                    applicationContext,
                                    getString(R.string.error_resubscribe),
                                    Toast.LENGTH_SHORT
                                ).show()

                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    getString(R.string.error_something),
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                applicationContext,
                                getString(R.string.error_something),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
        } else {
            Toast.makeText(this, getString(R.string.error_network), Toast.LENGTH_SHORT).show()

        }
    }

    private fun getDetailSubscription() {
        if (NetworkConnection.isNetworkConnected(this)) {
            pbSdPlan!!.visibility = View.VISIBLE
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            val credentials = LandlordPaymentHistoryCredential()
            credentials.userCatalogId = Kotpref.userId
            val signatureApi: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<SubscriptionDetailResponse> =
                signatureApi.getSubscriptionDetail(credentials)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                    pbSdPlan!!.visibility = View.GONE
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)


                    if (it.responseDto?.responseCode == 200) {
                        if (it.data != null) {
                            subscriptionDetail = it.data!!

                            Log.e(
                                "Subs",
                                "subscriptionDetail==> " + Gson().toJson(subscriptionDetail)
                            )

                            Kotpref.unitNumber = subscriptionDetail.numberOfUnits.toString()
                            if (Kotpref.unitNumber.isNullOrEmpty())
                                Kotpref.unitNumber = "0"


                        }
                        renewDate = Util.convertLongToTime(
                            subscriptionDetail.planRenewalDate!!.toLong(),
                            "MMM dd, yyyy"
                        )
                        subscriptionDetail.statusPlan = it.responseDto!!.message

                        //if(subscriptionDetail.numberOfUnits.isNullOrEmpty())
                        if (subscriptionDetail.price == "0") {
                            tv_free_subcription_note!!.text =
                                it.responseDto!!.message + " " + renewDate
                            layoutDetail!!.visibility = View.GONE
                            btnSdModify!!.text = "Buy subscription"
                        } else
                            updateUi()
                    }


                },
                    { e ->
                        Log.e("onFailure", e.toString())

                        pbSdPlan!!.visibility = View.GONE
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        layoutDetail!!.visibility = View.GONE
                        layoutSdBtns!!.visibility = View.GONE

                        //txtSdStatus!!.text=e.responseDto!!.exceptionDescription
                        txtSdStatus!!.visibility = View.VISIBLE
                        e.message?.let {
                            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                        }
                    })
        } else {
            Toast.makeText(this, getString(R.string.error_network), Toast.LENGTH_SHORT).show()

        }
    }

    private fun getPlanSubcription() {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            //Create retrofit Service
            pbSdPlan!!.visibility = View.VISIBLE
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )

            val signatureApi: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<SubcriptionPlanResponse> =
                signatureApi.getSubscription("")

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", it.responseDto?.responseDescription.toString())


                    pbSdPlan!!.visibility = View.GONE
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)


                    if (it.data != null) {
                        subcriptionList = it.data!!
                        //Util.navigationNext(this, ModifySubscriptionPlan::class.java)
                        val intent = Intent(this, SelectSubcriptionPlan::class.java)
                        if (btnSdModify!!.text.toString() == "Buy subscription") {
                            intent.putExtra(getString(R.string.is_register), true)
                        } else {
                            intent.putExtra(getString(R.string.is_register), false)
                        }
                        startActivity(intent)
                        Util.navigationNext(this)
                    }


                },
                    { e ->
                        Log.e("onFailure", e.toString())
                        pbSdPlan!!.visibility = View.GONE
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


    companion object {

        var subcriptionList = ArrayList<SubcriptionPlanModel>()
        var subcriptionPlanList = ArrayList<SubcriptionPlanModel>()
        var subscriptionDetail = SubscriptionDetail()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Util.navigationBack(this@SubscribePlanDetailScreen)
    }

}