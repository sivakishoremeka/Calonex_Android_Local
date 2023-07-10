package mp.app.calonex.landlord.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login_screen.*
import mp.app.calonex.LdRegBankAdd.AddBankModel
import mp.app.calonex.R
import mp.app.calonex.agent.activity.HomeActivityAgent
import mp.app.calonex.agentRegistration.RegistrationPayloadAgentModel
import mp.app.calonex.agentRegistration.UserContactDetailScreenAgent
import mp.app.calonex.broker.activity.HomeActivityBroker
import mp.app.calonex.brokerRegistration.RegistrationPayloadBrokerModel
import mp.app.calonex.common.apiCredentials.Credentials
import mp.app.calonex.common.apiCredentials.LandlordPaymentHistoryCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.network.RxAPICallbackForLogin
import mp.app.calonex.common.utility.*
import mp.app.calonex.landlord.model.SubscriptionDetail
import mp.app.calonex.landlord.model.User
import mp.app.calonex.landlord.response.SubscriptionDetailResponse
import mp.app.calonex.registration.model.*
import mp.app.calonex.registration.response.SubcriptionPlanResponse
import mp.app.calonex.service.FirebaseService
import mp.app.calonex.tenant.activity.HomeActivityTenant
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


@Suppress("UNREACHABLE_CODE")
class LoginScreen : AppCompatActivity() {

    private val Tag: String? = "LoginScreen"
    private var email: String? = null
    private var password: String? = null
    private var layoutBioAuth: LinearLayout? = null
    private var layoutBiometric: LinearLayout? = null
    private var txtRegister: TextView? = null
    val executor = Executors.newSingleThreadExecutor()
    private var biometricPrompt: BiometricPrompt? = null
    lateinit var promptInfo: PromptInfo
    private var iv_google: ImageView? = null
    private var im_fb: ImageView? = null
    private var iv_twitter: ImageView? = null
    private var iv_apple: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_login_screen)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
        )
        try {
            init()
        } catch (e: Exception) {
            Log.d(Tag, e.localizedMessage)
        }

    }


    /**
     * Method to initaialize the component of the class
     */
    private fun init() {
        //iv_twitter = findViewById(R.id.iv_twitter)
        iv_apple = findViewById(R.id.iv_apple);
        im_fb = findViewById(R.id.im_fb);
        iv_google = findViewById(R.id.iv_google);
        layoutBioAuth = findViewById(R.id.layout_bio_auth)
        layoutBiometric = findViewById(R.id.layout_biometric)
        txtRegister = findViewById(R.id.txt_register)
        // Check device support the biometric or not
        if (BiometricUtil.isHardwareAvailable(this) && Kotpref.isFingerPrint) {
            if (biometricPrompt == null)
                biometricPrompt = BiometricPrompt(this, executor, callback)
        } else {
            // hide the biometric layout
            layoutBiometric?.visibility = View.INVISIBLE
        }

        actionComponent()
    }


    /**
     * Method to define the action performed by the components on activity
     */
    private fun actionComponent() {
        getPlanSubcription()
        iv_apple!!.setOnClickListener {
            login("apple", "login")

        }

        /*iv_twitter!!.setOnClickListener {
            login("twitter", "login")
        }*/
        im_fb!!.setOnClickListener {
            login("facebook", "login")
        }
        iv_google!!.setOnClickListener {
            login("google", "login")
        }

        txtRegister!!.setOnClickListener {

            Util.navigationNext(this@LoginScreen, RegistrationNewActivity::class.java)
        }

        //Define the click event on the login Button
        btn_login!!.setOnClickListener { view ->

            // Hide the keyboard
            val imm =
                this@LoginScreen.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)

            checkLoginValues()
        }

        // Define the action when user click on biometric layout
        layoutBioAuth?.setOnClickListener {

            if (BiometricUtil.hasBiometricEnrolled(this)) {
                if (Util.isNullOrEmpty(Kotpref.emailId) || Util.isNullOrEmpty(Kotpref.password)) {

                    Util.alertYesMessage(
                        this,
                        getString(R.string.alert),
                        getString(R.string.msg_biometric)
                    )
                } else {
                    // Enable the biometric authentication
                    biometricPrompt!!.authenticate(buildBiometricPrompt())
                }

            } else {

                Util.alertOkMessage(
                    this,
                    getString(R.string.alert),
                    getString(R.string.error_msg_biometric_not_setup)
                )
                //Toast.makeText(applicationContext, getString(R.string.error_msg_biometric_not_setup), Toast.LENGTH_SHORT).show()

            }
        }



        txt_forget_password!!.setOnClickListener {
            val intent = Intent(this@LoginScreen, ForgetPasswordScreen::class.java)
            startActivity(intent)
        }

        edit_password!!.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
                txt_layout_pswrd?.isPasswordVisibilityToggleEnabled = true
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {

            }
        })

    }

    private fun checkLoginValues() {
        // Set the value of email and password from their respective input layout into their strings
        email = edit_email?.text.toString().trim()
        password = edit_password?.text.toString().trim()

        // Check the email string is empty or not
        if (email!!.isEmpty()) {
            edit_email?.error = getString(R.string.error_email)
            edit_email?.requestFocus()
            return
        }
        if ((!Util.isEmailValid(email!!))) {
            edit_email?.error = getString(R.string.error_valid_email)
            edit_email?.requestFocus()
            return
        }


        // Check the password string is empty or not
        if (password!!.isEmpty()) {
            txt_layout_pswrd?.isPasswordVisibilityToggleEnabled = false
            //edit_password?.error = getString(R.string.error_pwd)
            txt_layout_pswrd.error = getString(R.string.error_pwd)
            edit_password?.requestFocus()
            return
        }

        loginData(email, password)
    }


    /**
     * Check the status of permission
     * @param email
     * @param password
     */
    private fun loginData(email: String?, password: String?) {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            pb_login.visibility = View.VISIBLE
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            var credentials = Credentials()
            credentials.emailId = email!!
            credentials.password = RSA.encrypt(password!!)


            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.calonex.com/api/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val apiCall = retrofit.create(ApiInterface::class.java)

            val call = apiCall.login(credentials)
            call.enqueue(object : Callback<User> {
                override fun onResponse(
                    call: Call<User>,
                    response: Response<User>
                ) {
                    try {
                        this@LoginScreen.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                        if (!response.isSuccessful) {
                            Log.e("Response", "Response Status -=> " + response.isSuccessful)
                            Log.e(
                                "Stripe",
                                "Login Error Data-=> " + Gson().toJson(response.toString())
                            )
                            pb_login.visibility = View.GONE

                            val type = object : TypeToken<User>() {}.type
                            var errorResponse: User? = null
                            try {
                                errorResponse =
                                    Gson().fromJson(response.errorBody()?.charStream(), type)
                                Log.e(
                                    "Login",
                                    "Login errorResponse-=> " + Gson().toJson(errorResponse)
                                )

                                Toast.makeText(
                                    applicationContext,
                                    "" + errorResponse!!.responseDescription,
                                    Toast.LENGTH_SHORT
                                ).show()

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        } else {
                            Log.e("Stripe", "Login Data-=> " + Gson().toJson(response.body()))
                            var user = response.body()!!
                            Log.e(getString(R.string.on_success), user.userName)
                            pb_login.visibility = View.GONE

                            Kotpref.accessToken = user.access_token
                            Kotpref.emailId = email!!
                            Kotpref.password = password!!
                            Kotpref.userId = user.userId
                            Kotpref.bankAccountVerified = user.bankAccountVerified
                            Kotpref.bankAdded = user.bankAdded
                            // Kotpref.tenantId = user.userId
                            Kotpref.isLogin = true;
                            Kotpref.subscriptionActive = user.subscriptionActive
                            Log.d("TAG", " user.responseDescription: " + user.responseDescription)
                            user.responseDescription

                            Kotpref.authProvider = "CX"
                            Log.v(
                                "TAG",
                                "AuthProv: " + Kotpref.authProvider
                            )

                            val intent = Intent(this@LoginScreen, FirebaseService::class.java)
                            startService(intent)

                            // Here need to handle for multi role profile
                            Kotpref.userRole = user.userRoles[0]

                            if (Kotpref.userRole.contains("CX-Tenant", true)) {
                                Kotpref.isLogin = false
                                Kotpref.loginRole = "tenant"
                                Kotpref.userName = user.userName
                                startActivity(
                                    Intent(
                                        this@LoginScreen,
                                        HomeActivityTenant::class.java
                                    )
                                )
                                finish()
                            } else if (Kotpref.userRole.contains("Landlord", true)) {
                                Kotpref.isLogin = false
                                Kotpref.setupComplete = user.setupComplete
                                Kotpref.loginRole = "landlord"
                                getDetailSubscription()
                                startActivity(Intent(this@LoginScreen, HomeActivityCx::class.java))
                                finish()
                            } else if (Kotpref.userRole.contains("Broker", true)) {
                                Kotpref.isLogin = false
                                Kotpref.setupComplete = user.setupComplete
                                Kotpref.loginRole = "broker"
                                Kotpref.userName = user.userName
                                val intent =
                                    Intent(this@LoginScreen, HomeActivityBroker::class.java)
                                startActivity(intent)
                                finish()
                            } else if (Kotpref.userRole.contains("Agent", true)) {
                                Kotpref.isLogin = false
                                Kotpref.setupComplete = user.setupComplete
                                Kotpref.loginRole = "agent"
                                Kotpref.agentLicenceNo = user.agentLicenseNo
                                Log.e("AgNo", Kotpref.agentLicenceNo)
                                //val intent = Intent(this@LoginScreen, AgentGarphActivity::class.java)
                                if (Kotpref.setupComplete) {
                                    val intent =
                                        Intent(this@LoginScreen, HomeActivityAgent::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    //val intent = Intent(this@LoginScreen, NewBrokerInfoScreenAgent::class.java)
                                    val intent =
                                        Intent(
                                            this@LoginScreen,
                                            UserContactDetailScreenAgent::class.java
                                        )
                                    //intent.putExtra(getString(R.string.is_register),true)
                                    startActivity(intent)
                                    finish()
                                }
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    user.responseDescription,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        pb_login.visibility = View.GONE

                        Toast.makeText(
                            applicationContext,
                            getString(R.string.error_something),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.e("Failure", "Error call===>" + Gson().toJson(call))
                    pb_login.visibility = View.GONE

                    Toast.makeText(
                        applicationContext,
                        getString(R.string.error_something),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        } else {
            //showToast(getString(R.string.error_network))
            Toast.makeText(
                applicationContext,
                getString(R.string.error_network),
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun buildBiometricPrompt(): PromptInfo {
        promptInfo = PromptInfo.Builder()
            .setTitle(getString(R.string.title_biometric))
            .setSubtitle(getString(R.string.sub_title_biometric))
            .setDescription(getString(R.string.biometric_desc))
            .setNegativeButtonText(getString(R.string.cancel))
            .build()
        return promptInfo
    }

    private val callback: BiometricPrompt.AuthenticationCallback =
        object : BiometricPrompt.AuthenticationCallback() {

            @SuppressLint("RestrictedApi")
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)

                if (errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    runOnUiThread {
                        Util.alertOkMessage(
                            this@LoginScreen,
                            getString(R.string.alert),
                            getString(R.string.authentication_error_text)
                        )
                    }
                }
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)

                runOnUiThread {
                    loginData(Kotpref.emailId, Kotpref.password)
                }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()

                Util.alertOkMessage(
                    applicationContext,
                    getString(R.string.alert),
                    getString(R.string.error_biometric)
                )
            }
        }

    @SuppressLint("CheckResult")
    private fun getPlanSubcription() {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            //Create retrofit Service
            pb_login!!.visibility = View.VISIBLE
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )

            val signatureApi: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<SubcriptionPlanResponse> = signatureApi.getSubscription("")

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", it.responseDto?.responseDescription.toString())

                    pb_login!!.visibility = View.GONE
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                    if (it.data != null) {
                        subcriptionPlanList = it.data!!


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
                        Log.e("onFailure", e.toString())
                        pb_login!!.visibility = View.GONE
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
        var registrationPayload = RegistrationPayload()
        var registrationPayload3 = RegistrationPayload3()
        var registrationPayload4 = RegistrationPayload4()
        var subcriptionPlanList = ArrayList<SubcriptionPlanModel>()
        var registrationPayloadAgent = RegistrationPayloadAgentModel()
        var registrationPayloadBroker = RegistrationPayloadBrokerModel()
        var bankAddModelLL = AddBankModel()
        var subscriptionDetail = SubscriptionDetail()
    }

    private fun getDetailSubscription() {
        if (NetworkConnection.isNetworkConnected(this)) {
            //  pbSdPlan!!.visibility= View.VISIBLE
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

                    //  pbSdPlan!!.visibility= View.GONE
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)


                    if (it.responseDto?.responseCode == 200) {
                        if (it.data != null) {
                            subscriptionDetail = it.data!!

                            Kotpref.unitNumber =
                                SubscribePlanDetailScreen.subscriptionDetail.numberOfUnits.toString()


                        }

                        //  updateUi()
                    }


                },
                    { e ->
                        Log.e("onFailure", e.toString())

                        //  pbSdPlan!!.visibility= View.GONE
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        //  layoutDetail!!.visibility= View.GONE
                        //  layoutSdBtns!!.visibility= View.GONE

                        //txtSdStatus!!.text=e.responseDto!!.exceptionDescription
                        //   txtSdStatus!!.visibility= View.VISIBLE
                        e.message?.let {
                            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                        }
                    })
        } else {
            Toast.makeText(this, getString(R.string.error_network), Toast.LENGTH_SHORT).show()

        }
    }

    private fun login(aacount: String?, authtype: String) {
        val uri =
            Uri.parse("https://api.calonex.com/social-service/oauth2/authorization/$aacount?authtype=$authtype&redirect_uri=https://app.calonex.com/social-login")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        Log.e("google sign up", "signUp: $uri")
        startActivity(intent)
        // Log.d("google sign up", "signUp: uri")


    }

    override fun onBackPressed() {
        //super.onBackPressed()
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(startMain)
    }
}
