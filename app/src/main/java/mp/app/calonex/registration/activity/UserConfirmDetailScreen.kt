package mp.app.calonex.registration.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_select_role_screen.*
import kotlinx.android.synthetic.main.activity_signature_on_screen.*
import kotlinx.android.synthetic.main.activity_user_confirm_detail_screen.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.SocialLoginAccountSetupCredentials
import mp.app.calonex.common.apiCredentials.UserDetailCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.common.utility.Util.bitmapToFile
import mp.app.calonex.landlord.activity.HomeActivityCx
import mp.app.calonex.landlord.activity.LoginScreen.Companion.registrationPayload3
import mp.app.calonex.landlord.model.UserDetail
import mp.app.calonex.landlord.response.UserDetailResponse
import mp.app.calonex.registration.activity.UserDocumentScreen.Companion.documentPayloadList
import mp.app.calonex.registration.adapter.SubscriptionPlanAdapter.Companion.subscriptionPayload
import mp.app.calonex.registration.model.RegistrationPropertyPayload
import mp.app.calonex.registration.response.*
import mp.app.calonex.tenant.response.ResponseDtoResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File

class UserConfirmDetailScreen : AppCompatActivity() {
    private var layoutContactInfo: LinearLayout? = null
    private var layoutPropertyInfo: LinearLayout? = null
    private var layoutDocs: LinearLayout? = null
    private var layoutAccountInfo: LinearLayout? = null
    private var btnConfirmSign: Button? = null
    private var isBtnCnfrm: Boolean? = false
    private var txtPlan: TextView? = null
    private var count: Int = 0
    private var pb_reg_confirm: ProgressBar? = null
    private var socialLoginAccountSetupCredentials: SocialLoginAccountSetupCredentials?=null
    var userDetailResponse = UserDetail()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_confirm_detail_screen)
        registrationPayload3.userRoleName = "CX-Landlord"
        //registrationPayload3.userRoleName = getString(R.string.cx_landlord)
        if (Kotpref.isCardUseToPay) {
            subscriptionPayload.token = Kotpref.stripeToken
        }
        //Log.e("PAYMENT_LOG", Gson().toJson(subscriptionPayload))
        initComponent()
        actionComponent()
    }

    fun initComponent() {
        pb_reg_confirm = findViewById(R.id.pb_reg_confirm);
        txtPlan = findViewById(R.id.txt_plan)
        txtPlan!!.text = Kotpref.planSelected
        layoutContactInfo = findViewById(R.id.layout_contact_info)
        layoutPropertyInfo = findViewById(R.id.layout_property_info)
        layoutDocs = findViewById(R.id.layout_document)
        layoutAccountInfo = findViewById(R.id.layout_acc_info)
        btnConfirmSign = findViewById(R.id.btn_cnfirm_sign)
    }

    fun actionComponent() {
        getUserInfo()
        layoutContactInfo!!.setOnClickListener {
            Kotpref.isRegisterConfirm = true
            Util.navigationNext(this, UserContactDetailScreen::class.java)
        }

        /*layoutPropertyInfo!!.setOnClickListener {
            Kotpref.isRegisterConfirm = true
            Util.navigationNext(this, UserPropertyDetailScreen::class.java)
        }

        layoutDocs!!.setOnClickListener {
            Kotpref.isRegisterConfirm = true
            Util.navigationNext(this, UserDocumentScreen::class.java)
        }*/

        layoutAccountInfo!!.setOnClickListener {
            Kotpref.isRegisterConfirm = true
            Util.navigationNext(this, UserAccountScreen::class.java)
        }

        btnConfirmSign!!.setOnClickListener {
            isBtnCnfrm = true
            createPlanSubcription()
        }
    }

    private fun createPlanSubcription() {

        try {
            if (NetworkConnection.isNetworkConnected(applicationContext)) {
                pb_reg_confirm!!.visibility = View.VISIBLE
                getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )

                //Log.e("Final_I_LOG_1", Gson().toJson(subscriptionPayload))
                subscriptionPayload.userCatalogId = Kotpref.userId


                val signatureApi: ApiInterface =
                    ApiClient(this).provideService(ApiInterface::class.java)
                val apiCall: Observable<SelectPlanResponse2> =
                    signatureApi.createSubscription(subscriptionPayload)

                apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        //Log.e("Final_O_LOG_1", Gson().toJson(it.responseDto))
                        Log.e("onSuccess", it.responseDto?.responseDescription.toString())

                        if (it!!.responseDto!!.responseCode.equals(201)) {

                            if (it.data != null) {
                                registrationPayload3.subscriptionDetailsId =
                                    it.data!!.subscriptionDetailsId
                                registrationPayload3.stripeCustomerId = it.data!!.stripeCustomerId
                             //  registerApi()
                                socialLoginUserRegisterApi()


/*
                                try {
                                    Kotpref.loginRole = "landlord"
                                    Kotpref.isLogin = true
                                    Kotpref.bankAccountVerified = true
                                    Kotpref.subscriptionActive = true
                                    Util.navigationNext(this, HomeActivityCx::class.java)
                                    finish()
                                } catch (e: Exception) {
                                    e.printStackTrace()

                                    Toast.makeText(
                                        applicationContext,
                                        " Something went wrong",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }
*/



                            }
                        } else {
                            pb_reg_confirm!!.visibility = View.GONE
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            Toast.makeText(
                                applicationContext,
                                it!!.responseDto!!.responseDescription,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                        { e ->
                            Log.e("onFailure", e.toString())
                            pb_reg_confirm!!.visibility = View.GONE
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            val exception: HttpException = e as HttpException
                            if (exception.code().equals(409)) {
                                Toast.makeText(
                                    applicationContext,
                                    getString(R.string.error_bank_detail),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                e.message?.let {
                                    Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT)
                                        .show()
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("CheckResult")
    private fun registerApi() {

        try {
            if (NetworkConnection.isNetworkConnected(applicationContext)) {
                //Create retrofit Service
                pb_reg_confirm!!.visibility = View.VISIBLE
                getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )

                //Log.e("Final_I_LOG_2", Gson().toJson(registrationPayload3))
                val registerService: ApiInterface =
                    ApiClient(this).provideService(ApiInterface::class.java)
                val apiCall: Observable<UserRegistrationResponse3> =
                    registerService.setupUser(registrationPayload3)

                apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        //Log.e("Final_O_LOG_2", Gson().toJson(it))
                        Log.e("onSuccess", it.responseCode.toString())

                        if (it!!.responseCode.equals(200)) {

                            /*ganram 12-07-22*/
/*
                        for (item in documentPayloadList){
                            // registerUploadApi(item.imgBitmap!!,item.imgKey!!,registerDetail.userCatalogID!!)
                            registerUploadApi(item.imgBitmap!!,item.imgKey!!,registrationPayload3.userId!!)
                        }

*/

                            try {
                                Kotpref.loginRole = "landlord"
                                Kotpref.isLogin = true
                                Kotpref.bankAccountVerified = true
                                Kotpref.subscriptionActive = true
                                Util.navigationNext(this, HomeActivityCx::class.java)
                                finish()
                            } catch (e: Exception) {
                                e.printStackTrace()

                                Toast.makeText(
                                    applicationContext,
                                    it.responseDescription + "",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }

                        } else {
                            pb_reg_confirm!!.visibility = View.GONE
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            Toast.makeText(
                                applicationContext,
                                it.responseDescription + "",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                        { e ->
                            Log.e("onFailure", e.toString())
                            pb_reg_confirm!!.visibility = View.GONE
                            isBtnCnfrm = false
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            val exception: HttpException = e as HttpException
                            if (exception.code().equals(400)) {
                                Toast.makeText(
                                    applicationContext,
                                    getString(R.string.error_email_exist),
                                    Toast.LENGTH_SHORT
                                ).show()

                            } else {
                                e.message?.let {
                                    Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT)
                                        .show()
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("CheckResult")
    fun registerUploadApi(bitmapUpload: Bitmap, keyValue: String, idValue: String) {

        try {
            if (NetworkConnection.isNetworkConnected(applicationContext)) {
                pb_reg_confirm!!.visibility = View.VISIBLE
                getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
                val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
                builder.addFormDataPart("userId", idValue)
                builder.addFormDataPart("uploadFileType", keyValue)
                try {
                    var file: File? =
                        bitmapToFile(this@UserConfirmDetailScreen, bitmapUpload, keyValue)

                    builder.addFormDataPart(
                        "file",
                        file!!.name,
                        file.asRequestBody(MultipartBody.FORM)
                    )

                } catch (e: Exception) {
                    e.printStackTrace()
                }
                val requestBody: RequestBody = builder.build()
                val uploadSign: ApiInterface =
                    ApiClient(this).provideService(ApiInterface::class.java)

                val apiCallUploadSign: Observable<ResponseDtoResponse> =
                    uploadSign.uploadDocument(requestBody)

                apiCallUploadSign.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe({
                        count = count + 1
                        if (!it.responseDto?.responseCode!!.equals(200)) {

                            pb_reg_confirm?.visibility = View.GONE
                            //  getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            /*Toast.makeText(
                                this,
                                it.responseDto?.exceptionDescription!!,
                                Toast.LENGTH_SHORT
                            ).show()*/
                        }

                        if (count == documentPayloadList.size) {
                            pb_reg_confirm!!.visibility = View.GONE
                            //  getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            /*alertOkIntentMessage(
                                this@UserConfirmDetailScreen,
                                "Alert",
                                "Your registration request has been sent to Admin. You will be notified on your registered Email ID once your request is approved by the admin",
                                LoginScreen::class.java
                            )*/

                            try {
                                Kotpref.loginRole = "landlord"
                                Kotpref.isLogin = true
                                Kotpref.bankAccountVerified = true
                                Kotpref.subscriptionActive = true
                                Util.navigationNext(this, HomeActivityCx::class.java)

                                // startActivity(Intent(this@UserConfirmDetailScreen, HomeActivityCx::class.java))
                                finish()
                            } catch (e: Exception) {
                                e.printStackTrace()
                                //   Util.navigationNext(this, HomeActivityCx::class.java)
                                //  startActivity(Intent(this@UserConfirmDetailScreen, HomeActivityCx::class.java))
                                //  finish()
                            }


                            /* if (Kotpref.isCardUseToPay) {
                                 Kotpref.isLogin = true
                                 startActivity(Intent(this@UserConfirmDetailScreen, HomeActivityCx::class.java))
                                 finish()
                             } else {
                               *//*  val intent = Intent(
                                    this@UserConfirmDetailScreen,
                                    VerifyAccountDetailsActivity::class.java
                                )
                                intent.putExtra("FromLandlord", true)
                                startActivity(intent)
                                finish()*//*
                                val intent = Intent(this, LoginScreen::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()*/

                        }

                    },
                        { e ->
                            count = count + 1
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            pb_reg_confirm?.visibility = View.GONE
                            /*Toast.makeText(
                                this,
                                e.message,
                                Toast.LENGTH_SHORT
                            ).show()*/
                            if (count == documentPayloadList.size) {
                                //  pb_reg_confirm!!.visibility = View.GONE
                                //  getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                                /*alertOkIntentMessage(
                                    this@UserConfirmDetailScreen,
                                    "Alert",
                                    "Your registration request has been sent to Admin. You will be notified on your registered Email ID once your request is approved by the admin",
                                    LoginScreen::class.java
                                )*/
                                startActivity(
                                    Intent(
                                        this@UserConfirmDetailScreen,
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (!isBtnCnfrm!!) {
            Util.navigationBack(this)
        }
    }


    @SuppressLint("CheckResult")
    private fun socialLoginUserRegisterApi() {
        try {
            if (NetworkConnection.isNetworkConnected(applicationContext)) {
                //Create retrofit Service
                pb_reg_confirm!!.visibility = View.VISIBLE
                getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )

                registrationPayload3.userId = Kotpref.userId

                var registrationPropertyPayload= RegistrationPropertyPayload()
                val gson = Gson()
                val valueString=gson.toJson(registrationPropertyPayload)
                val jsonObject: JsonObject = JsonParser().parse(valueString).getAsJsonObject()
                registrationPayload3.userPropertyRegisterDto=jsonObject

                //Log.e("Final_I_LOG_2", Gson().toJson(registrationPayload3))
                val registerService: ApiInterface =
                    ApiClient(this).provideService(ApiInterface::class.java)
                val apiCall: Observable<UserRegistrationResponse3> =
                    registerService.setupUserSocial(registrationPayload3)

                apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        //Log.e("Final_O_LOG_2", Gson().toJson(it))
                        Log.e("onSuccess", it.responseCode.toString())

                        if (it!!.status == 200) {
                            Toast.makeText(
                                applicationContext,
                                it.message + "",
                                Toast.LENGTH_SHORT
                            ).show()

                            Kotpref.loginRole = "landlord"
                            Kotpref.isLogin = true
                            Kotpref.bankAccountVerified = true
                            Kotpref.subscriptionActive = true
                            Util.navigationNext(this@UserConfirmDetailScreen, HomeActivityCx::class.java)
                            finish()

                            /*ganram 12-07-22*/
/*
                        for (item in documentPayloadList){
                            // registerUploadApi(item.imgBitmap!!,item.imgKey!!,registerDetail.userCatalogID!!)
                            registerUploadApi(item.imgBitmap!!,item.imgKey!!,registrationPayload3.userId!!)
                        }

*/

                            /*try {
                                Kotpref.loginRole = "landlord"
                                Kotpref.isLogin = true
                                Kotpref.bankAccountVerified = true
                                Kotpref.subscriptionActive = true
                                Util.navigationNext(this@UserConfirmDetailScreen, HomeActivityCx::class.java)
                                finish()

                            } catch (e: Exception) {
                                e.printStackTrace()

                                Toast.makeText(
                                    applicationContext,
                                    it.responseDescription + "",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }*/

                        } else {
                            pb_reg_confirm!!.visibility = View.GONE
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            Toast.makeText(
                                applicationContext,
                                it.responseDescription + "",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                        { e ->
                            Log.e("onFailure", e.toString())
                            pb_reg_confirm!!.visibility = View.GONE
                            isBtnCnfrm = false
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            val exception: HttpException = e as HttpException
                            if (exception.code().equals(400)) {
                                Toast.makeText(
                                    applicationContext,
                                    getString(R.string.error_email_exist),
                                    Toast.LENGTH_SHORT
                                ).show()

                            } else {
                                e.message?.let {
                                    Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT)
                                        .show()
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getUserInfo() {
        //Create retrofit Service

        val credentials = UserDetailCredential()

        credentials.userId = Kotpref.userId
        val userDetailService: ApiInterface =
            ApiClient(this@UserConfirmDetailScreen).provideService(ApiInterface::class.java)
        val apiCall: Observable<UserDetailResponse> =
            userDetailService.getUserDetail(credentials) //Test API Key

        RxAPICallHelper().call(apiCall, object : RxAPICallback<UserDetailResponse> {
            override fun onSuccess(userDetail: UserDetailResponse) {
                userDetailResponse = userDetail.data!!
                registrationPayload3.firstName=userDetailResponse.firstName
                registrationPayload3.lastName=userDetailResponse.lastName
                registrationPayload3.emailId=userDetailResponse.emailId




            }

            override fun onFailed(t: Throwable) {
                // show error
                Log.e("onFailure", t.toString())
                //  progressBarList?.visibility = View.GONE
            }
        })

    }


}