package mp.app.calonex.landlord.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_tenant_property_history.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.NotificationCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.RSA
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.model.AppNotifications
import mp.app.calonex.landlord.model.NotificationLinkRequestModel
import mp.app.calonex.landlord.model.NotificationLinkRequestModelNew
import mp.app.calonex.landlord.response.AppNotificationResponse
import mp.app.calonex.registration.model.RegistrationPayload2
import mp.app.calonex.registration.response.UserRegistrationResponse2
import retrofit2.HttpException
import java.util.regex.Matcher
import java.util.regex.Pattern

class QuickRegistrationNewActivity : AppCompatActivity() {
    private var edit_reg_first_name: TextInputEditText? = null
    private var edit_reg_last_name: TextInputEditText? = null
    private var edit_reg_email: TextInputEditText? = null
    private var edit_reg_pwd: TextInputEditText? = null
    private var edit_reg_crm_pwd: TextInputEditText? = null

    private var btn_signup: TextView? = null
    private var txtRegister: TextView? = null
    private var iv_back: ImageView? = null

    private var pb_select_role: ProgressBar? = null
    private var profile_pic: CircleImageView? = null
    private var layoutLphNotify: RelativeLayout? = null
    private var txtLphNotify: TextView? = null
    var linkRequestListt = java.util.ArrayList<AppNotifications>()
    var linkRequestListForLdReq = java.util.ArrayList<AppNotifications>()
    var alertsListt = java.util.ArrayList<AppNotifications>()
    var messageList = java.util.ArrayList<AppNotifications>()
    var appNotifications = java.util.ArrayList<AppNotifications>()
    var mNotificationLinkRequest = NotificationLinkRequestModel()
    var mNotificationLinkRequestNew = NotificationLinkRequestModelNew()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quick_registration_new)

        layoutLphNotify=findViewById(R.id.layout_lp_notify)

        edit_reg_first_name = findViewById(R.id.edit_reg_first_name);
        edit_reg_last_name = findViewById(R.id.edit_reg_last_name);
        edit_reg_email = findViewById(R.id.edit_reg_email);
        edit_reg_pwd = findViewById(R.id.edit_reg_pwd);
        edit_reg_crm_pwd = findViewById(R.id.edit_reg_crm_pwd)

        pb_select_role = findViewById(R.id.pb_select_role)

        btn_signup = findViewById(R.id.btn_signup)
        txtRegister = findViewById(R.id.txt_register)
        iv_back = findViewById(R.id.iv_back)

        iv_back!!.setOnClickListener {
            onBackPressed()
        }


        btn_signup!!.setOnClickListener {
            if (checkValidation()) {
                registerApi()
            }
        }

        txtRegister!!.setOnClickListener {
            val uri: Uri =
                Uri.parse("https://calonex.com/privacy-policy/")

            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        layoutLphNotify!!.visibility = View.VISIBLE
        linkRequestListt.clear()
        alertsListt.clear()
        messageList.clear()
        getNotificationList()
    }
    private fun getNotificationList() {
        pb_select_role!!.visibility = View.VISIBLE
        this@QuickRegistrationNewActivity.getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        val credentials = NotificationCredential()

        credentials.userCatalog = Kotpref.userId
        val notifyListService: ApiInterface =
            ApiClient(this).provideService(ApiInterface::class.java)
        val apiCall: Observable<AppNotificationResponse> =
            notifyListService.getAppNotifications(credentials) //Test API Key 3600248I

        RxAPICallHelper().call(apiCall, object : RxAPICallback<AppNotificationResponse> {
            override fun onSuccess(response: AppNotificationResponse) {
                Log.e("onSuccess", response.responseCode.toString())

                if (!response.data?.isEmpty()!!) {
                    appNotifications = response.data!!
                    linkRequestListt.clear()
                    alertsListt.clear()
                    messageList.clear()
                    for (i in 0..appNotifications.size - 1) {
                        val gson = Gson()

                        if (appNotifications[i].activityType == "1") {
                            val objectList = gson.fromJson(
                                appNotifications[i].notificationData,
                                NotificationLinkRequestModel::class.java
                            )
                            mNotificationLinkRequest = objectList
                            linkRequestListt.add(appNotifications[i])
                        } else if (appNotifications[i].activityType == "2") {
                            alertsListt.add(appNotifications[i])
                        } else if (appNotifications[i].activityType == "3") {
                            messageList.add(appNotifications[i])
                        }
                    }
                }

                Kotpref.notifyCount = (linkRequestListt.size + alertsListt.size).toString()
                txt_lp_notify!!.text = Kotpref.notifyCount
                //todo change class name
                //(activity as HomeActivityAgent?)!!.addBadgeNew(messageList.size.toString())
                pb_select_role!!.visibility = View.GONE
                this@QuickRegistrationNewActivity!!.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                if (Integer.parseInt(Kotpref.notifyCount) < 1) {
                    txt_lp_notify!!.visibility = View.GONE

                } else {
                    txt_lp_notify!!.visibility = View.VISIBLE

                }
            }

            override fun onFailed(throwable: Throwable) {
                // show error
                Log.e("onFailure", throwable.toString())
                pb_select_role!!.visibility = View.GONE
                this@QuickRegistrationNewActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                txt_lp_notify!!.text =
                    (linkRequestListt.size + alertsListt.size).toString()
                if (Integer.parseInt(Kotpref.notifyCount) < 1) {
                    txt_lp_notify!!.visibility = View.GONE

                } else {
                    txt_lp_notify!!.visibility = View.VISIBLE

                }
            }
        })
    }


    private fun checkValidation(): Boolean {
        var flag = true
        if (edit_reg_first_name?.text.toString().isNullOrEmpty()) {
            edit_reg_first_name?.setError("Mandatory Field.")
            edit_reg_first_name?.requestFocus()
            flag = false
        } else if (edit_reg_last_name?.text.toString().isNullOrEmpty()) {
            edit_reg_last_name?.setError("Mandatory Field.")
            edit_reg_last_name?.requestFocus()
            flag = false
        } else if (edit_reg_email?.text.toString().isNullOrEmpty()) {
            edit_reg_email?.setError("Mandatory Field.")
            edit_reg_email?.requestFocus()
            flag = false
        } else if (!Util.isEmailValid(edit_reg_email?.text.toString())) {
            edit_reg_email?.setError("Valid Email Required")
            edit_reg_email?.requestFocus()
            flag = false
        } else if (edit_reg_pwd?.text.toString().isNullOrEmpty()) {
            edit_reg_pwd?.setError("Mandatory Field.")
            flag = false
        } else if (edit_reg_pwd?.text.toString().length < 8) {
            edit_reg_pwd?.setError("Password should have 8 or more characters.")
            flag = false
        }

        var pattern: Pattern = Pattern.compile("[^a-zA-Z0-9]")
        var matcher: Matcher = pattern.matcher(edit_reg_pwd?.text.toString())
        var isStringContainsSpecialCharacter: Boolean = matcher.find()

        if (!isStringContainsSpecialCharacter) {
            //layout_cred_paswrd?.isPasswordVisibilityToggleEnabled = false
            edit_reg_pwd?.error = getString(R.string.error_pwd_character)
            edit_reg_pwd?.requestFocus()

        } else if (edit_reg_crm_pwd?.text.toString().isNullOrEmpty()) {
            edit_reg_crm_pwd?.setError("Mandatory Field.")
            edit_reg_crm_pwd?.requestFocus()
            flag = false

        } else if (!edit_reg_crm_pwd?.text.toString().equals(edit_reg_crm_pwd?.text.toString())) {
            edit_reg_crm_pwd?.setError("New Password and Confirm Password should be same")
            edit_reg_crm_pwd?.requestFocus()
            flag = false
        }

        return flag
    }

    @SuppressLint("CheckResult")
    private fun registerApi() {

        var registrationPayload4 = RegistrationPayload2()
        registrationPayload4.firstName = edit_reg_first_name?.text.toString()
        registrationPayload4.lastName = edit_reg_last_name?.text.toString()
        registrationPayload4.emailId = edit_reg_email?.text.toString()
        registrationPayload4.confirmPassword = RSA.encrypt(edit_reg_crm_pwd?.text.toString())
        registrationPayload4.password = RSA.encrypt(edit_reg_crm_pwd?.text.toString())
        registrationPayload4.userRoleName = "CX-Landlord"

        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            //Create retrofit Service
            pb_select_role!!.visibility = View.VISIBLE
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            //Log.e("REG_LOG_I_2", Gson().toJson(registrationPayload))
            val registerService: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<UserRegistrationResponse2> =
                registerService.registerUserNew(registrationPayload4)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    //Log.e("REG_LOG_O_2", Gson().toJson(it.responseDto))
                    Log.e("onSuccess", it.responseDto?.responseDescription.toString())

                    if (it!!.responseDto!!.responseCode.equals(200)) {

                        val intent = Intent(this, SignupDoneActivityNew::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                        intent.putExtra("email", edit_reg_email?.text.toString())
                        startActivity(intent)
                        finish()

                    } else {
                        pb_select_role!!.visibility = View.GONE
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        Toast.makeText(
                            applicationContext,
                            it.responseDto!!.responseDescription,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                    { e ->
                        Log.e("onFailure", e.toString())
                        pb_select_role!!.visibility = View.GONE
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
}