package mp.app.calonex.landlord.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import io.reactivex.Observable
import mp.app.calonex.R
import mp.app.calonex.agent.activity.HomeActivityAgent
import mp.app.calonex.broker.activity.HomeActivityBroker
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.landlord.response.SocialLoginResponse
import mp.app.calonex.service.FirebaseService
import mp.app.calonex.tenant.activity.HomeActivityTenant
import retrofit2.HttpException


class SocialLoginCheck : AppCompatActivity() {

    private var TAG: String = SocialLoginCheck::class.java.simpleName

    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.social_login_check)
        val action: String? = intent?.action
        val data: Uri? = intent?.data

        /*
        *
        // ATTENTION: This was auto-generated to handle app links.
        val appLinkIntent: Intent = intent
        val appLinkAction: String? = appLinkIntent.action
        val appLinkData: Uri? = appLinkIntent.data*/

        Log.e(TAG, "The social data is " + Gson().toJson(data))

        if (data?.getQueryParameter("error") != null) {

            Log.e(TAG, "onCreate:the data is " + data?.getQueryParameter("error"))
            Toast.makeText(
                applicationContext,
                data?.getQueryParameter("error"),
                Toast.LENGTH_SHORT
            ).show()
            val intent = Intent(this, LoginScreen::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()


        } else if (data?.getQueryParameter("token") != null) {
            token = data?.getQueryParameter("token")
            Log.e(TAG, "onCreate:the data is " + data?.getQueryParameter("token"))

            Kotpref.socialToken = token
            loginData(token)


        }


    }


    private fun loginData(token: String?) {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            // pb_login.visibility = View.VISIBLE
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )

            var loginService: ApiInterface =
                ApiClient(this).provideServiceSocial(ApiInterface::class.java)

            var apiCall: Observable<SocialLoginResponse> =
                loginService.socialLogin("Bearer " + token)

            RxAPICallHelper().call(apiCall, object : RxAPICallback<SocialLoginResponse> {
                override fun onSuccess(user: SocialLoginResponse) {
                    Log.e("LOGIN_LOG_O_1", Gson().toJson(user))
                    //Log.e(getString(R.string.on_success), user.data.userName)
                    //  pb_login.visibility = View.GONE
                    try {
                        Log.e(TAG, "onSuccess: user is" + user)

                        Kotpref.accessToken = user.body!!.access_token.toString()
                        //  Kotpref.emailId = email
                        //  Kotpref.password = password
                        Kotpref.userId = user.body?.userId.toString()
                        Kotpref.bankAccountVerified = user.body!!.bankAccountVerified
                        Kotpref.bankAdded = user.body!!.bankAdded
//                    Kotpref.tenantId = user.body.userId
                        Kotpref.isLogin = true;
                        Kotpref.subscriptionActive = user.body!!.subscriptionActive
                        Kotpref.authProvider = user.body!!.authProvider.toString()

                        Log.e(
                            "TAG",
                            "AuthProv: " + Kotpref.authProvider
                        )
                        Log.e(
                            "TAG",
                            " user.body.responseDescription: " + user.body!!.responseDescription
                        )
                        user.body!!.responseDescription


                        val intent = Intent(this@SocialLoginCheck, FirebaseService::class.java)
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
                                    this@SocialLoginCheck,
                                    //TenantPaymentSettingActivity::class.java
                                    HomeActivityTenant::class.java
                                )
                                startActivity(intent)
                                finish()
                            } else if (!Kotpref.bankAccountVerified && Kotpref.bankAdded) {
                                val intent = Intent(
                                    this@SocialLoginCheck,
                                    //  VerifyAccountDetailsActivity::class.java
                                    HomeActivityTenant::class.java
                                )
                                startActivity(intent)
                                finish()
                            } else {
                                startActivity(
                                    Intent(
                                        this@SocialLoginCheck,
                                        HomeActivityTenant::class.java
                                    )
                                )
                                finish()
                            }

                        } else if (Kotpref.userRole.contains("CX-Landlord", true)) {
                            Kotpref.isLogin = false
                            Kotpref.setupComplete = user.body!!.setupComplete
                            Kotpref.loginRole = "landlord"
                            // getDetailSubscription()

                            if (!Kotpref.bankAdded && Kotpref.setupComplete) {
                                startActivity(
                                    Intent(
                                        this@SocialLoginCheck,
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
                                    this@SocialLoginCheck,
                                    //  VerifyAccountDetailsActivity::class.java
                                    HomeActivityCx::class.java

                                )
                                startActivity(intent)
                                finish()
                            } else {
                                Kotpref.isLogin = true
                                startActivity(
                                    Intent(
                                        this@SocialLoginCheck,
                                        HomeActivityCx::class.java
                                    )
                                )
                                finish()
                            }

                        } else if (Kotpref.userRole.contains("CX-Broker", true)) {
                            Kotpref.isLogin = false
                            Kotpref.setupComplete = user.body!!.setupComplete
                            Kotpref.loginRole = "broker"
                            Kotpref.userName = user.body!!.userName.toString()

                            if (Kotpref.setupComplete) {
                                Kotpref.isLogin = true
                                val intent =
                                    Intent(this@SocialLoginCheck, HomeActivityBroker::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                val intent = Intent(
                                    this@SocialLoginCheck,
                                    // UserContactDetailScreenBroker::class.java
                                    HomeActivityBroker::class.java
                                )
                                //intent.putExtra(getString(R.string.is_register),true)
                                startActivity(intent)
                                finish()
                            }
                        } else if (Kotpref.userRole.contains("CX-Agent", true)) {
                            Kotpref.isLogin = false
                            Kotpref.setupComplete = user.body!!.setupComplete
                            Kotpref.loginRole = "agent"
                            //val intent = Intent(this@LoginScreen, AgentGarphActivity::class.java)
                            if (Kotpref.setupComplete) {
                                Kotpref.isLogin = true
                                val intent =
                                    Intent(this@SocialLoginCheck, HomeActivityAgent::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                val intent =
                                    Intent(
                                        this@SocialLoginCheck,
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
                                "Please activate your account before trying to login",
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
    }


}