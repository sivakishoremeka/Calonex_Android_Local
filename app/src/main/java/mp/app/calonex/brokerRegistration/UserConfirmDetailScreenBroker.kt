package mp.app.calonex.brokerRegistration

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_user_confirm_detail_screen_agent.*
import mp.app.calonex.R
import mp.app.calonex.broker.activity.HomeActivityBroker
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.LoginScreen
import mp.app.calonex.landlord.activity.LoginScreen.Companion.registrationPayloadBroker
import mp.app.calonex.registration.response.UserRegistrationResponse3
import retrofit2.HttpException

class UserConfirmDetailScreenBroker : AppCompatActivity() {
    private var layoutContactInfo: LinearLayout? = null
    private var layoutBusinessInfo: LinearLayout? = null

    private var btnConfirmSign: Button? = null
    private var isBtnCnfrm: Boolean? = false
    private var count: Int = 0
    private var signup: TextView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_confirm_detail_screen_broker)

        initComponent()
        actionComponent()
    }

    fun initComponent() {
        signup=findViewById(R.id.signup);

        layoutContactInfo = findViewById(R.id.layout_contact_info)
        layoutBusinessInfo = findViewById(R.id.layout_business_info)

        btnConfirmSign = findViewById(R.id.btn_cnfirm_sign)
    }

    fun actionComponent() {


        signup!!.setOnClickListener {
            // startActivity(Intent(this@QuickRegistrationActivity, LoginScreen::class.java))
            val intent = Intent(this, LoginScreen::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }


        layoutContactInfo!!.setOnClickListener {
            Kotpref.isRegisterConfirm = true
            Util.navigationNext(this, UserContactDetailScreenBroker::class.java)
        }

        layoutBusinessInfo!!.setOnClickListener {
            Kotpref.isRegisterConfirm = true
            Util.navigationNext(this, BusinessInfoScreenBroker::class.java)
        }

        btnConfirmSign!!.setOnClickListener {
            isBtnCnfrm = true
            registerApi()
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
            val apiCall: Observable<UserRegistrationResponse3> =
                registerService.setupUserBroker(registrationPayloadBroker)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    //Log.e("Final_O_LOG_2", Gson().toJson(it))
                    Log.e("onSuccess", it.responseCode.toString())

                    if (it!!.responseCode.equals(200)) {
                        // var registerDetail:RegisterDetail=it!!.data!!
                        Kotpref.isLogin = true
                        startActivity(
                            Intent(
                                this@UserConfirmDetailScreenBroker,
                                HomeActivityBroker::class.java
                            )
                        )
                        finish()

                    } else {
                        pb_reg_confirm!!.visibility = View.GONE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        Toast.makeText(
                            applicationContext,
                            it.responseDescription,
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

    /* @SuppressLint("CheckResult")
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
             var file: File? =
                 bitmapToFile(this@UserConfirmDetailScreenBroker, bitmapUpload, keyValue)
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
                         *//*Toast.makeText(
                            this,
                            it.responseDto?.exceptionDescription!!,
                            Toast.LENGTH_SHORT
                        ).show()*//*

                    }

                    if (count == documentPayloadList.size) {
                        pb_reg_confirm!!.visibility = View.GONE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        *//*alertOkIntentMessage(
                            this@UserConfirmDetailScreen,
                            "Alert",
                            "Your registration request has been sent to Admin. You will be notified on your registered Email ID once your request is approved by the admin",
                            LoginScreen::class.java
                        )*//*
                        //Kotpref.loginRole = "landlord"
                        Kotpref.isLogin = true
                        startActivity(
                            Intent(
                                this@UserConfirmDetailScreenBroker,
                                HomeActivityAgent::class.java
                            )
                        )
                        finish()
                    }
                },
                    { e ->
                        count = count + 1
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        pb_reg_confirm.visibility = View.GONE
                        *//*Toast.makeText(
                            this,
                            e.message,
                            Toast.LENGTH_SHORT
                        ).show()*//*
                        if (count == documentPayloadList.size) {
                            pb_reg_confirm!!.visibility = View.GONE
                            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            *//*alertOkIntentMessage(
                                this@UserConfirmDetailScreen,
                                "Alert",
                                "Your registration request has been sent to Admin. You will be notified on your registered Email ID once your request is approved by the admin",
                                LoginScreen::class.java
                            )*//*
                            startActivity(
                                Intent(
                                    this@UserConfirmDetailScreenBroker,
                                    HomeActivityAgent::class.java
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
    }*/

    override fun onBackPressed() {
        super.onBackPressed()
        if (!isBtnCnfrm!!) {
            Util.navigationBack(this)
        }
    }

}