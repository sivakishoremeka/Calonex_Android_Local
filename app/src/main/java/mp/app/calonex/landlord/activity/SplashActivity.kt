package mp.app.calonex.landlord.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_login_screen.pb_login
import mp.app.calonex.R
import mp.app.calonex.agent.activity.HomeActivityAgent
import mp.app.calonex.agent.activity.NotifyScreenAgent
import mp.app.calonex.agentRegistration.UserContactDetailScreenAgent
import mp.app.calonex.broker.activity.HomeActivityBroker
import mp.app.calonex.common.apiCredentials.Credentials
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.*
import mp.app.calonex.landlord.model.User
import mp.app.calonex.landlord.model.ZipCityStateModel
import mp.app.calonex.landlord.response.SocialLoginResponse
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
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess


class SplashActivity : AppCompatActivity() {

    private var sentToSettings = false
    private var firebase_token: String? = null

    private var timer: Timer? = null
    private var progressBar: ProgressBar? = null
    private var i = 0
    private var TAG: String = SplashActivity::class.java.simpleName
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        //Initailize the Preference


        progressBar = findViewById(R.id.progressBar2)

        firebase_token = FirebaseInstanceId.getInstance().token
        if (firebase_token != null) {
            Kotpref.firebaseToken = firebase_token.toString()
        }
        checkReadExternalStoragePermission()

        val encrypt: String = AesUtil().encryptString("test@123")
        val decrypt: String =
            AesUtil().decryptString("GgeoHZ6kpQRI3brKQLJ27G7PfsqU91pmGcN9ey9IpE8=")
        val encrypt1: String? = RSA.encrypt("Test@123")
        //val decrypt1: String? = RSA.decrypt("GgeoHZ6kpQRI3brKQLJ27G7PfsqU91pmGcN9ey9IpE8=")
        Log.e("Encrypt => ", encrypt1!!)
        Log.e("Decrypt => ", decrypt)
    }

    /**
     * Check the permission to read the storage of the device
     */
    private fun checkReadExternalStoragePermission() {

        val hasPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            checkWriteExternalStoragePermission()
        } else {
            // ask the permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                0
            )
        }
    }

    /**
     * Check the permission to save something on device
     */
    private fun checkWriteExternalStoragePermission() {

        val hasPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {

            handler()
        } else {
            // ask the permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1
            )
        }
    }

    /**
     * Check the status of permission
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // The result of the popup opened with the requestPermissions() method
        // is in that method, you need to check that your application comes here
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == 0) {
                checkWriteExternalStoragePermission()
            } else if (requestCode == 1) {
                handler()
            }
        } else {
            getAlertDialog()

        }
    }

    /**
     * This method will call when user provide the require permission from the setting of device
     */
    override fun onPostResume() {
        super.onPostResume()
        if (sentToSettings) {
            checkReadExternalStoragePermission()
        }
    }


    /**
     * Method allows communicating back with UI thread from background thread
     */
    private fun handler() {
        // This method will be executed once the timer is over
//        Handler().postDelayed({
        zipCityStateArrayList = Util.getZipCityStateList(this)
        Log.e("ERROR_LOGIN", Kotpref.isLogin.toString())

        Log.e("ERROR_PROVIDER", Kotpref.authProvider)
        if (!Kotpref.isLogin) {


            // Start your app main activity
            /*Handler().postDelayed({
                startActivity(Intent(this, LoginScreen::class.java))
                this.finish()
            }, 2000)*/

            val period: Long = 100
            timer = Timer()
            timer!!.schedule(object : TimerTask() {
                override fun run() {
                    if (i < 100) {
                        progressBar!!.progress = i
                        i++
                    } else {
                        //closing the timer
                        timer!!.cancel()
                        val intent = Intent(this@SplashActivity, LoginScreen::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }, 0, period)
        } else {
            if (Kotpref.authProvider != "CX") {
                if (NetworkConnection.isNetworkConnected(applicationContext)) {
                    // pb_login.visibility = View.VISIBLE
                    /*getWindow().setFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    )*/

                    var loginService: ApiInterface =
                        ApiClient(this).provideServiceSocial(ApiInterface::class.java)

                    var apiCall: Observable<SocialLoginResponse> =
                        loginService.socialLogin("Bearer " + Kotpref.accessToken) // socialToken

                    RxAPICallHelper().call(apiCall, object : RxAPICallback<SocialLoginResponse> {
                        override fun onSuccess(user: SocialLoginResponse) {
                            Log.e("LOGIN_LOG_O_1", Gson().toJson(user))
                            //Log.e(getString(R.string.on_success), user.data.userName)
                            //  pb_login.visibility = View.GONE
                            try {
                                Log.d(TAG, "onSuccess: user is" + user)

                                Kotpref.accessToken = user.body!!.access_token.toString()
                                //Kotpref.userId = user.body?.userId.toString()
                                Kotpref.bankAccountVerified = user.body!!.bankAccountVerified
                                Kotpref.bankAdded = user.body!!.bankAdded
                                //Kotpref.isLogin = true;
                                Kotpref.subscriptionActive = user.body!!.subscriptionActive
                                //Kotpref.authProvider = user.body!!.authProvider.toString()

                                Log.v(
                                    "TAG",
                                    "AuthProv: " + Kotpref.authProvider
                                )
                                Log.d(
                                    "TAG",
                                    " user.body.responseDescription: " + user.body!!.responseDescription
                                )
                                user.body!!.responseDescription


                                val intent =
                                    Intent(this@SplashActivity, FirebaseService::class.java)
                                startService(intent)

                                // Here need to handle for multi role profile
                                Kotpref.userRole = user.body!!.userRoles[0]

                                if (Kotpref.userRole.contains("CX-Tenant")) {
//                        Kotpref.tenantId = user.body.tenantId
                                    Kotpref.loginRole = "tenant"
//                        Kotpref.cxId = user.body.cxId
                                    if (user.body!!.userName != null)
                                        Kotpref.userName = user.body!!.userName.toString()
//                        val intent = Intent(this@LoginScreen, TenantVerifyUpdateDetailsFirstActivity::class.java)

                                    if (!Kotpref.bankAdded) {
                                        val intent = Intent(
                                            this@SplashActivity,
                                            //TenantPaymentSettingActivity::class.java
                                            HomeActivityTenant::class.java
                                        )
                                        startActivity(intent)
                                        finish()
                                    } else if (!Kotpref.bankAccountVerified && Kotpref.bankAdded) {
                                        val intent = Intent(
                                            this@SplashActivity,
                                            //  VerifyAccountDetailsActivity::class.java
                                            HomeActivityTenant::class.java
                                        )
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        startActivity(
                                            Intent(
                                                this@SplashActivity,
                                                HomeActivityTenant::class.java
                                            )
                                        )
                                        finish()
                                    }
                                    // TenantPaymentSettingActivity
                                    /*if(!Kotpref.bankAccountVerified && Kotpref.bankAdded)
                                    {
                                        val intent = Intent(this@LoginScreen, VerifyAccountDetailsActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    else {
                                        val intent = Intent(this@LoginScreen, LeaseListActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }*/
                                } else if (Kotpref.userRole.contains("Landlord", true)) {
                                    //Kotpref.isLogin = false
                                    Kotpref.setupComplete = user.body!!.setupComplete
                                    Kotpref.loginRole = "landlord"
                                    // getDetailSubscription()

                                    if (!Kotpref.bankAdded && Kotpref.setupComplete) {
                                        startActivity(
                                            Intent(
                                                this@SplashActivity,
                                                // SelectSubcriptionPlanBankAdd::class.java
                                                HomeActivityCx::class.java
                                            )
                                        )
                                        finish()
                                    } else if (!Kotpref.bankAdded && !Kotpref.setupComplete) {
                                        Kotpref.isRegisterConfirm = false
                                        LoginScreen.registrationPayload.userRoleName =
                                            getString(R.string.cx_landlord)
                                        LoginScreen.registrationPayload.deviceId =
                                            Settings.Secure.getString(
                                                getContentResolver(),
                                                Settings.Secure.ANDROID_ID
                                            )
                                        //  getPlanSubcription()
                                    } else if (!Kotpref.bankAccountVerified && Kotpref.bankAdded) {
                                        val intent = Intent(
                                            this@SplashActivity,
                                            //  VerifyAccountDetailsActivity::class.java
                                            HomeActivityCx::class.java

                                        )
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        //Kotpref.isLogin = true
                                        startActivity(
                                            Intent(
                                                this@SplashActivity,
                                                HomeActivityCx::class.java
                                            )
                                        )
                                        finish()
                                    }

                                } else if (Kotpref.userRole.contains("Broker", true)) {
                                    //Kotpref.isLogin = false
                                    Kotpref.setupComplete = user.body!!.setupComplete
                                    Kotpref.loginRole = "broker"
                                    Kotpref.userName = user.body!!.userName.toString()

                                    if (Kotpref.setupComplete) {
                                        //Kotpref.isLogin = true
                                        val intent =
                                            Intent(
                                                this@SplashActivity,
                                                HomeActivityBroker::class.java
                                            )
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        val intent = Intent(
                                            this@SplashActivity,
                                            // UserContactDetailScreenBroker::class.java
                                            HomeActivityBroker::class.java
                                        )
                                        //intent.putExtra(getString(R.string.is_register),true)
                                        startActivity(intent)
                                        finish()
                                    }
                                } else if (Kotpref.userRole.contains("Agent", true)) {
                                    //Kotpref.isLogin = false
                                    Kotpref.setupComplete = user.body!!.setupComplete
                                    Kotpref.loginRole = "agent"
                                    //val intent = Intent(this@LoginScreen, AgentGarphActivity::class.java)
                                    if (Kotpref.setupComplete) {
                                        //Kotpref.isLogin = true
                                        val intent =
                                            Intent(
                                                this@SplashActivity,
                                                HomeActivityAgent::class.java
                                            )
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        val intent =
                                            Intent(
                                                this@SplashActivity,
                                                //UserContactDetailScreenAgent::class.java
                                                HomeActivityAgent::class.java
                                            )

                                        //intent.putExtra(getString(R.string.is_register),true)
                                        startActivity(intent)
                                        finish()
                                    }
                                } else {
                                    Toast.makeText(
                                        applicationContext,
                                        getString(R.string.error_invalid_cred),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        override fun onFailed(t: Throwable) {
                            //   pb_login.visibility = View.GONE
                            try {
                                val exception: HttpException = t as HttpException

                                exception.printStackTrace()

                                if (exception.code().equals(404)) {
                                    Toast.makeText(
                                        applicationContext,
                                        getString(R.string.error_invalid_cred),
                                        Toast.LENGTH_SHORT
                                    ).show()

                                } else if (exception.code().equals(401)) {
                                    Toast.makeText(
                                        applicationContext,
                                        "You have either entered the wrong password or your account is not activated.",
                                        Toast.LENGTH_SHORT
                                    ).show()


                                } else if (exception.code().equals(405)) {
                                    Toast.makeText(
                                        applicationContext,
                                        getString(R.string.error_network),
                                        Toast.LENGTH_SHORT
                                    ).show()

                                } else {
                                    Toast.makeText(
                                        applicationContext,
                                        getString(R.string.error_something),
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    t.printStackTrace()

                                }
                            } catch (e: Exception) {
                                Toast.makeText(
                                    applicationContext,
                                    getString(R.string.error_something),
                                    Toast.LENGTH_SHORT
                                ).show()
                                e.printStackTrace()
                            }
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            Log.e(getString(R.string.on_fail), t.toString())
                        }
                    })
                } else {
                    // showToast(getString(R.string.error_network))
//            Toast.makeText(applicationContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
                }

            } else {
                // refrsh the token
                if (NetworkConnection.isNetworkConnected(this)) {

                    val credentials = Credentials()

                    credentials.emailId = Kotpref.emailId
                    //credentials.password = Kotpref.password
                    credentials.password = RSA.encrypt(Kotpref.password)
                    credentials.deviceId = Kotpref.firebaseToken

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
                                Log.e("Stripe", "Login Data-=> " + Gson().toJson(response.body()!!))
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

                                    var user = response.body()!!

                                    Kotpref.accessToken = user.access_token
                                    if (Kotpref.loginRole.equals("landlord")) {
                                        val checkNotify: String? =
                                            intent.getStringExtra("pushNotification")
                                        if (checkNotify.isNullOrEmpty()) {
                                            startActivity(
                                                Intent(
                                                    this@SplashActivity,
                                                    HomeActivityCx::class.java
                                                )
                                            )
                                        } else {
                                            if (checkNotify.equals("yes")) {
                                                if (intent.getBooleanExtra(
                                                        getString(R.string.intent_pn_msg),
                                                        false
                                                    )
                                                ) {
                                                    val intent =
                                                        Intent(
                                                            this@SplashActivity,
                                                            HomeActivityCx::class.java
                                                        )
                                                    intent.putExtra(
                                                        getString(R.string.intent_pn_msg),
                                                        true
                                                    )
                                                    startActivity(intent)
                                                    this@SplashActivity.finish()
                                                } else {
                                                    val intent =
                                                        Intent(
                                                            this@SplashActivity,
                                                            NotifyScreen::class.java
                                                        )
                                                    startActivity(intent)
                                                    this@SplashActivity.finish()
                                                }

                                            } else {
                                                startActivity(
                                                    Intent(
                                                        this@SplashActivity,
                                                        HomeActivityCx::class.java
                                                    )
                                                )
                                            }
                                        }

                                    } else if (Kotpref.loginRole.equals("tenant")) {
                                        if (!Kotpref.bankAdded) {
                                            val intent = Intent(
                                                this@SplashActivity,
                                                // TenantPaymentSettingActivity::class.java
                                                HomeActivityTenant::class.java

                                            )
                                            startActivity(intent)
                                            finish()
                                        } else if (!Kotpref.bankAccountVerified && Kotpref.bankAdded) {
                                            val intent = Intent(
                                                this@SplashActivity,
                                                // VerifyAccountDetailsActivity::class.java
                                                HomeActivityTenant::class.java
                                            )
                                            startActivity(intent)
                                            finish()
                                        } else {
                                            startActivity(
                                                Intent(
                                                    this@SplashActivity,
                                                    HomeActivityTenant::class.java
                                                )
                                            )
                                            finish()
                                        }

                                    } else if (Kotpref.userRole.contains("Broker", true)) {
                                        Kotpref.loginRole = "broker"

                                        //val intent = Intent(this@SplashActivity, BrokarDashboardGarphActivity::class.java)
                                        val intent =
                                            Intent(
                                                this@SplashActivity,
                                                HomeActivityBroker::class.java
                                            )
                                        startActivity(intent)
                                        finish()
                                    } else if (Kotpref.userRole.contains("Agent", true)) {
                                        Kotpref.loginRole = "agent"
                                        val checkNotifyAgent: String? =
                                            intent.getStringExtra("pushNotification")
                                        if (checkNotifyAgent.isNullOrEmpty()) {
                                            startActivity(
                                                Intent(
                                                    this@SplashActivity,
                                                    HomeActivityAgent::class.java
                                                )
                                            )
                                        } else {
                                            if (checkNotifyAgent.equals("yes")) {
                                                if (intent.getBooleanExtra(
                                                        getString(R.string.intent_pn_msg),
                                                        false
                                                    )
                                                ) {
                                                    val intent = Intent(
                                                        this@SplashActivity,
                                                        HomeActivityAgent::class.java
                                                    )
                                                    intent.putExtra(
                                                        getString(R.string.intent_pn_msg),
                                                        true
                                                    )
                                                    startActivity(intent)
                                                    this@SplashActivity.finish()
                                                } else {
                                                    val intent = Intent(
                                                        this@SplashActivity,
                                                        NotifyScreenAgent::class.java
                                                    )
                                                    startActivity(intent)
                                                    this@SplashActivity.finish()
                                                }
                                            } else {
                                                startActivity(
                                                    Intent(
                                                        this@SplashActivity,
                                                        HomeActivityAgent::class.java
                                                    )
                                                )
                                            }
                                        }
                                    } else {
                                        startActivity(
                                            Intent(
                                                this@SplashActivity,
                                                LoginScreen::class.java
                                            )
                                        )
                                    }
                                    this@SplashActivity.finish()
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                                Toast.makeText(
                                    applicationContext,
                                    getString(R.string.error_something),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<User>, t: Throwable) {
                            Toast.makeText(
                                applicationContext,
                                getString(R.string.error_something),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })

                    /*val loginService: ApiInterface =
                        ApiClient(this).provideService(ApiInterface::class.java)
                    val apiCall: Observable<User> = loginService.login(credentials) //Test API Key

                    RxAPICallHelper().call(apiCall, object : RxAPICallback<User> {
                        override fun onSuccess(user: User) {
                            Kotpref.accessToken = user.access_token
                            if (Kotpref.loginRole.equals("landlord")) {
                                val checkNotify: String? = intent.getStringExtra("pushNotification")
                                if (checkNotify.isNullOrEmpty()) {
                                    startActivity(
                                        Intent(
                                            this@SplashActivity,
                                            HomeActivityCx::class.java
                                        )
                                    )
                                } else {
                                    if (checkNotify.equals("yes")) {
                                        if (intent.getBooleanExtra(
                                                getString(R.string.intent_pn_msg),
                                                false
                                            )
                                        ) {
                                            val intent =
                                                Intent(
                                                    this@SplashActivity,
                                                    HomeActivityCx::class.java
                                                )
                                            intent.putExtra(getString(R.string.intent_pn_msg), true)
                                            startActivity(intent)
                                            this@SplashActivity.finish()
                                        } else {
                                            val intent =
                                                Intent(
                                                    this@SplashActivity,
                                                    NotifyScreen::class.java
                                                )
                                            startActivity(intent)
                                            this@SplashActivity.finish()
                                        }

                                    } else {
                                        startActivity(
                                            Intent(
                                                this@SplashActivity,
                                                HomeActivityCx::class.java
                                            )
                                        )
                                    }
                                }

                            } else if (Kotpref.loginRole.equals("tenant")) {
                                if (!Kotpref.bankAdded) {
                                    val intent = Intent(
                                        this@SplashActivity,
                                        // TenantPaymentSettingActivity::class.java
                                        HomeActivityTenant::class.java

                                    )
                                    startActivity(intent)
                                    finish()
                                } else if (!Kotpref.bankAccountVerified && Kotpref.bankAdded) {
                                    val intent = Intent(
                                        this@SplashActivity,
                                        // VerifyAccountDetailsActivity::class.java
                                        HomeActivityTenant::class.java
                                    )
                                    startActivity(intent)
                                    finish()
                                } else {
                                    startActivity(
                                        Intent(
                                            this@SplashActivity,
                                            HomeActivityTenant::class.java
                                        )
                                    )
                                    finish()
                                }

                            } else if (Kotpref.userRole.contains("Broker", true)) {
                                Kotpref.loginRole = "broker"

                                //val intent = Intent(this@SplashActivity, BrokarDashboardGarphActivity::class.java)
                                val intent =
                                    Intent(this@SplashActivity, HomeActivityBroker::class.java)
                                startActivity(intent)
                                finish()
                            } else if (Kotpref.userRole.contains("Agent", true)) {
                                Kotpref.loginRole = "agent"
                                val checkNotifyAgent: String? =
                                    intent.getStringExtra("pushNotification")
                                if (checkNotifyAgent.isNullOrEmpty()) {
                                    startActivity(
                                        Intent(
                                            this@SplashActivity,
                                            HomeActivityAgent::class.java
                                        )
                                    )
                                } else {
                                    if (checkNotifyAgent.equals("yes")) {
                                        if (intent.getBooleanExtra(
                                                getString(R.string.intent_pn_msg),
                                                false
                                            )
                                        ) {
                                            val intent = Intent(
                                                this@SplashActivity,
                                                HomeActivityAgent::class.java
                                            )
                                            intent.putExtra(getString(R.string.intent_pn_msg), true)
                                            startActivity(intent)
                                            this@SplashActivity.finish()
                                        } else {
                                            val intent = Intent(
                                                this@SplashActivity,
                                                NotifyScreenAgent::class.java
                                            )
                                            startActivity(intent)
                                            this@SplashActivity.finish()
                                        }
                                    } else {
                                        startActivity(
                                            Intent(
                                                this@SplashActivity,
                                                HomeActivityAgent::class.java
                                            )
                                        )
                                    }
                                }
                            } else {
                                startActivity(Intent(this@SplashActivity, LoginScreen::class.java))
                            }
                            this@SplashActivity.finish()
                        }

                        override fun onFailed(t: Throwable) {
                            try {
                                val exception: HttpException = t as HttpException
//                        if (exception.code().equals(401)) {
                                startActivity(Intent(this@SplashActivity, LoginScreen::class.java))
//                        }

                            } catch (e: Exception) {
                                Toast.makeText(
                                    applicationContext,
                                    getString(R.string.error_server),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            Log.e("error", t.toString())
                        }
                    })*/

                } else {
                    Toast.makeText(this, getString(R.string.error_network), Toast.LENGTH_SHORT)
                        .show()
                    Handler().postDelayed({
                        exitProcess(1)
                    }, 3000)
                }
            }
        }
        // close this activity or del this activity from the stack
//            finish()
        // Set the interval
//        }, 1000)
    }

    /**
     * Method to call the dialog when the user deny the required perrmissions
     */
    private fun getAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.app_permission))
        builder.setMessage(getString(R.string.tag_permission))
        // Navigate to permission screen of app in setting
        builder.setPositiveButton(getString(R.string.grant)) { dialog, which ->
            dialog.cancel()
            sentToSettings = true
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            //val intent = Intent(Settings.ACTION_SETTINGS)
            //val uri = Uri.fromParts(getString(R.string.tag_package), packageName, null)
            intent.data = Uri.parse("package:" + packageName)
            //intent.data = uri
            startActivityForResult(intent, 101)

        }
        // Dismiss the dialogbox
        builder.setNegativeButton(getString(R.string.cancel)) { dialog, which ->
            dialog.cancel()
            finish()
            moveTaskToBack(true)
        }
        builder.show()
    }

    companion object {
        var zipCityStateArrayList = ArrayList<ZipCityStateModel>()
    }


}
