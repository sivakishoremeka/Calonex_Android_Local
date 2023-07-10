package mp.app.calonex.agentRegistration

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
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_user_confirm_detail_screen_agent.*
import mp.app.calonex.R
import mp.app.calonex.agent.activity.HomeActivityAgent
import mp.app.calonex.agentRegistration.UserDocumentScreenAgent.Companion.documentPayloadList
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.common.utility.Util.bitmapToFile
import mp.app.calonex.landlord.activity.LoginScreen
import mp.app.calonex.landlord.activity.LoginScreen.Companion.registrationPayloadAgent
import mp.app.calonex.registration.response.UserRegistrationResponse3
import mp.app.calonex.tenant.response.ResponseDtoResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File

class UserConfirmDetailScreenAgent : AppCompatActivity() {
    private var layoutContactInfo: LinearLayout? = null
    private var layoutBrokerInfo: LinearLayout? = null
    private var layoutDocs: LinearLayout? = null
    private var btnConfirmSign: Button? = null
    private var isBtnCnfrm: Boolean? = false
    private var count: Int = 0
    private var signup:TextView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_confirm_detail_screen_agent)

        initComponent()
        actionComponent()
    }

    fun initComponent() {
        signup=findViewById(R.id.signup)
        layoutContactInfo = findViewById(R.id.layout_contact_info)
        layoutBrokerInfo = findViewById(R.id.layout_broker_info)
        layoutDocs = findViewById(R.id.layout_document)

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
            Util.navigationNext(this, UserContactDetailScreenAgent::class.java)
        }

        layoutBrokerInfo!!.setOnClickListener {
            Kotpref.isRegisterConfirm = true
            Util.navigationNext(this, BrokerInfoScreenAgent::class.java)
        }

        layoutDocs!!.setOnClickListener {
            Kotpref.isRegisterConfirm = true
            Util.navigationNext(this, UserDocumentScreenAgent::class.java)
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
                registerService.setupUserAgent(registrationPayloadAgent)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    //Log.e("Final_O_LOG_2", Gson().toJson(it))
                    Log.e("onSuccess", it.responseDescription)

                    if (it!!.responseCode.equals(200)) {
                        // var registerDetail:RegisterDetail=it!!.data!!
                        for (item in documentPayloadList) {
                            // registerUploadApi(item.imgBitmap!!,item.imgKey!!,registerDetail.userCatalogID!!)
                            registerUploadApi(
                                item.imgBitmap!!,
                                item.imgKey!!,
                                registrationPayloadAgent.userId!!
                            )
                        }
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
            var file: File? =
                bitmapToFile(this@UserConfirmDetailScreenAgent, bitmapUpload, keyValue)
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
                        Kotpref.isLogin = true
                        startActivity(
                            Intent(
                                this@UserConfirmDetailScreenAgent,
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
                                    this@UserConfirmDetailScreenAgent,
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
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (!isBtnCnfrm!!) {
            Util.navigationBack(this)
        }
    }

}