package mp.app.calonex.agentBrokerRegLd

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_user_confirm_detail_screen.*
import mp.app.calonex.R
import mp.app.calonex.agent.activity.HomeActivityAgent
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.common.utility.Util.bitmapToFile
import mp.app.calonex.landlord.activity.HomeActivityCx
import mp.app.calonex.agent.fragment.ProfileFragmentAgent.Companion.registrationPayload
import mp.app.calonex.agentBrok.UserContactDetailScreenNew
import mp.app.calonex.agentBrokerRegLd.UserDocumentScreenNew.Companion.documentPayloadList
import mp.app.calonex.agentBrokerRegLd.SubscriptionPlanAdapterNew.Companion.subscriptionPayload
import mp.app.calonex.broker.activity.HomeActivityBroker
import mp.app.calonex.landlord.activity.CxBaseActivity2
import mp.app.calonex.registration.response.SelectPlanResponse2
import mp.app.calonex.registration.response.UserRegistrationResponse
import mp.app.calonex.tenant.response.ResponseDtoResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File

class UserConfirmDetailScreenNew : CxBaseActivity2() {
    private var layoutContactInfo: LinearLayout? = null
    private var layoutPropertyInfo: LinearLayout? = null
    private var layoutDocs: LinearLayout? = null
    //private var layoutSubs: LinearLayout? = null
    private var layoutAccountInfo: LinearLayout? = null
    private var btnConfirmSign: Button? = null
    private var isBtnCnfrm: Boolean? = false
    private var txtPlan: TextView? = null
    private var count: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_confirm_detail_screen_new)
        registrationPayload.userRoleName = "CX-Landlord"
        //registrationPayload3.userRoleName = getString(R.string.cx_landlord)
        if (Kotpref.isCardUseToPay) {
            subscriptionPayload.token = Kotpref.stripeToken
        }
        //Log.e("PAYMENT_LOG", Gson().toJson(subscriptionPayload))
        initComponent()
        actionComponent()
        startHandler()
    }

    fun initComponent() {
        txtPlan = findViewById(R.id.txt_plan)
        txtPlan!!.text = Kotpref.planSelected
        layoutContactInfo = findViewById(R.id.layout_contact_info)
        layoutPropertyInfo = findViewById(R.id.layout_property_info)
        layoutDocs = findViewById(R.id.layout_document)
        //layoutSubs = findViewById(R.id.layout_subs_plan)
        layoutAccountInfo = findViewById(R.id.layout_acc_info)
        btnConfirmSign = findViewById(R.id.btn_cnfirm_sign)
    }

    fun actionComponent() {

        layoutContactInfo!!.setOnClickListener {
            Kotpref.isRegisterConfirm = true
            Util.navigationNext(this, UserContactDetailScreenNew::class.java)
        }

        layoutPropertyInfo!!.setOnClickListener {
            Kotpref.isRegisterConfirm = true
            Util.navigationNext(this, UserPropertyDetailScreenNew::class.java)
        }

        layoutDocs!!.setOnClickListener {
            Kotpref.isRegisterConfirm = true
            Util.navigationNext(this, UserDocumentScreenNew::class.java)
        }

        /*layoutSubs!!.setOnClickListener {
            Kotpref.isRegisterConfirm = true
            Util.navigationNext(this, SelectSubcriptionPlanNew::class.java)
        }*/

        layoutAccountInfo!!.setOnClickListener {
            Kotpref.isRegisterConfirm = true
            Util.navigationNext(this, UserAccountScreenNew::class.java)
        }

        btnConfirmSign!!.setOnClickListener {
            isBtnCnfrm = true
            createPlanSubcription()
        }
    }

    private fun createPlanSubcription() {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            //Create retrofit Service
            pb_reg_confirm!!.visibility = View.VISIBLE
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            subscriptionPayload.userCatalogId=Kotpref.userId

            //Log.e("Final_I_LOG_1", Gson().toJson(subscriptionPayload))
            val signatureApi: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)
            Log.d("TAG", "createPlanSubcriptionapi: "+ subscriptionPayload)

            val apiCall: Observable<SelectPlanResponse2> =
                signatureApi.createSubscription(subscriptionPayload)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    //Log.e("Final_O_LOG_1", Gson().toJson(it.responseDto))
                    Log.e("onSuccess", it.responseDto?.responseDescription.toString())

                    if (it!!.responseDto!!.responseCode.equals(201)) {

                        if (it.data != null) {
                            registrationPayload.subscriptionDetailsId =
                                it.data!!.subscriptionDetailsId
                            registrationPayload.stripeCustomerId = it.data!!.stripeCustomerId
                            registerApi()
                        }
                    } else {
                        pb_reg_confirm!!.visibility = View.GONE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        Toast.makeText(
                            applicationContext,
                            it.responseDto!!.responseDescription,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                    { e ->
                        Log.e("onFailure", e.toString())
                        pb_reg_confirm!!.visibility = View.GONE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        val exception: HttpException = e as HttpException
                        if (exception.code().equals(409)) {
                            Toast.makeText(
                                applicationContext,
                                getString(R.string.error_bank_detail),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            e.message?.let {
                                Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                            }
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

    @SuppressLint("CheckResult")
    private fun registerApi() {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            //Create retrofit Service
            pb_reg_confirm!!.visibility = View.VISIBLE
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )

            //Log.e("Final_I_LOG_2", Gson().toJson(registrationPayload3))
            val registerService: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<UserRegistrationResponse> =
                registerService.registerUser2(registrationPayload)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    //Log.e("Final_O_LOG_2", Gson().toJson(it))
                    Log.e("onSuccess", it.responseDto!!.responseDescription)

                    if (it!!.responseDto!!.responseCode.equals(200)) {
                        // var registerDetail:RegisterDetail=it!!.data!!
                        for (item in documentPayloadList) {
                            // registerUploadApi(item.imgBitmap!!,item.imgKey!!,registerDetail.userCatalogID!!)
                            registerUploadApi(
                                item.imgBitmap!!,
                                item.imgKey!!,
                                registrationPayload.userId!!
                            )
                        }
                    } else {
                        pb_reg_confirm!!.visibility = View.GONE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        Toast.makeText(
                            applicationContext,
                            it.responseDto!!.responseDescription,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                    { e ->
                        Log.e("onFailure", e.toString())
                        pb_reg_confirm!!.visibility = View.GONE
                        isBtnCnfrm = false
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        val exception: HttpException = e as HttpException
                        if (exception.code().equals(400)) {
                            Toast.makeText(
                                applicationContext,
                                getString(R.string.error_email_exist),
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {
                            e.message?.let {
                                Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                            }
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

    @SuppressLint("CheckResult")
    fun registerUploadApi(bitmapUpload: Bitmap, keyValue: String, idValue: String) {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            pb_reg_confirm!!.visibility = View.VISIBLE
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
            builder.addFormDataPart("userId", idValue)
            builder.addFormDataPart("uploadFileType", keyValue)
            var file: File? = bitmapToFile(this@UserConfirmDetailScreenNew, bitmapUpload, keyValue)
            builder.addFormDataPart(
                "file", file!!.name,
                file.asRequestBody(MultipartBody.FORM)
            )
            val requestBody: RequestBody = builder.build()
            val uploadSign: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)

            val apiCallUploadSign: Observable<ResponseDtoResponse> =
                uploadSign.uploadDocument(requestBody)

            apiCallUploadSign.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({
                    count = count + 1
                    if (!it.responseDto?.responseCode!!.equals(200)) {

                        pb_reg_confirm.visibility = View.GONE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        /*Toast.makeText(
                            this,
                            it.responseDto?.exceptionDescription!!,
                            Toast.LENGTH_SHORT
                        ).show()*/
                    }

                    if (count == documentPayloadList.size) {
                        pb_reg_confirm!!.visibility = View.GONE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        /*alertOkIntentMessage(
                            this@UserConfirmDetailScreen,
                            "Alert",
                            "Your registration request has been sent to Admin. You will be notified on your registered Email ID once your request is approved by the admin",
                            LoginScreen::class.java
                        )*/
                        //Kotpref.loginRole = "landlord"
                        if (Kotpref.userRole.contains("Agent", true)) {
                            startActivity(
                                Intent(
                                    this@UserConfirmDetailScreenNew,
                                    HomeActivityAgent::class.java
                                )
                            )
                        } else {
                            startActivity(
                                Intent(
                                    this@UserConfirmDetailScreenNew,
                                    HomeActivityBroker::class.java
                                )
                            )
                        }

                        finish()
                    }

                },
                    { e ->
                        count = count + 1
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        pb_reg_confirm.visibility = View.GONE
                        /*Toast.makeText(
                            this,
                            e.message,
                            Toast.LENGTH_SHORT
                        ).show()*/
                        if (count == documentPayloadList.size) {
                            pb_reg_confirm!!.visibility = View.GONE
                            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            /*alertOkIntentMessage(
                                this@UserConfirmDetailScreen,
                                "Alert",
                                "Your registration request has been sent to Admin. You will be notified on your registered Email ID once your request is approved by the admin",
                                LoginScreen::class.java
                            )*/
                            startActivity(
                                Intent(
                                    this@UserConfirmDetailScreenNew,
                                    HomeActivityCx::class.java
                                )
                            )
                            finish()
                        }

                        Log.e("error", e.message.toString())

                    })

        } else {
            Toast.makeText(
                applicationContext,
                getString(R.string.error_network),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (!isBtnCnfrm!!) {
            Util.navigationBack(this)
        }
    }

}