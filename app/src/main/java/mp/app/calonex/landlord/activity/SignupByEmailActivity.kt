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
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import mp.app.calonex.R
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.RSA
import mp.app.calonex.common.utility.Util
import mp.app.calonex.registration.model.RegistrationPayload
import mp.app.calonex.registration.model.RegistrationPayload2
import mp.app.calonex.registration.response.UserRegistrationResponse
import mp.app.calonex.registration.response.UserRegistrationResponse2
import retrofit2.HttpException
import java.util.regex.Matcher
import java.util.regex.Pattern

class SignupByEmailActivity : AppCompatActivity() {

    private var edit_reg_first_name: TextInputEditText? = null
    private var edit_reg_last_name: TextInputEditText? = null
    private var edit_reg_email: TextInputEditText? = null
    private var edit_reg_pwd: TextInputEditText? = null
    private var edit_reg_crm_pwd: TextInputEditText? = null
    private var txt_register: TextView? = null

    private var cb_termcon: CheckBox? = null

    private var btn_signup: Button? = null

    private var role: String? = null

    private var get_role: String? = null
    private var get_license: String? = null


    private var pb_select_role: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_by_email)

        edit_reg_first_name = findViewById(R.id.edit_reg_first_name);
        edit_reg_last_name = findViewById(R.id.edit_reg_last_name);
        edit_reg_email = findViewById(R.id.edit_reg_email);
        edit_reg_pwd = findViewById(R.id.edit_reg_pwd);
        edit_reg_crm_pwd = findViewById(R.id.edit_reg_crm_pwd)

        pb_select_role = findViewById(R.id.pb_select_role)
        cb_termcon = findViewById(R.id.cb_termcon)

        txt_register = findViewById(R.id.txt_register)

        btn_signup = findViewById(R.id.btn_signup)

        get_role = intent.getStringExtra("ROLE")
        get_license = intent.getStringExtra("LICENSE")

        Log.e("val-1", get_role!!)
        Log.e("val-2", get_license!!)

        role = "CX-" + get_role!!

        btn_signup!!.isEnabled = false
        btn_signup!!.setBackgroundResource(R.drawable.btn_dk_grey_round)

        cb_termcon!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                btn_signup!!.isEnabled = true
                btn_signup!!.setBackgroundResource(R.drawable.btn_dk_blue_round_new)
            } else {
                btn_signup!!.isEnabled = false
                btn_signup!!.setBackgroundResource(R.drawable.btn_dk_grey_round)
            }
        }

        btn_signup!!.setOnClickListener {

            if (checkValidation()) {
                //registerApi()
                if (role.equals("CX-Tenant")) {
                    registerTenantApi()
                } else {
                    registerApi()
                }
            }
        }

        txt_register!!.setOnClickListener {
            val uri: Uri =
                Uri.parse("https://calonex.com/privacy-policy/")

            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        /*txt_register!!.setOnClickListener {
            val intent = Intent(this, TermsNConditionActivity::class.java)
            startActivity(intent)
        }*/
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

        if (!cb_termcon!!.isChecked) {
            Toast.makeText(
                applicationContext,
                "Select Terms & Conditions and Privacy Policy Check box first.",
                Toast.LENGTH_SHORT
            ).show()
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
        registrationPayload4.userRoleName = role

        if (!get_license!!.isNullOrEmpty()) {
            registrationPayload4.agentLicenseNumber = get_license!!.trim()
            registrationPayload4.brokerLicenseNumber = get_license!!.trim()
        }

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
                        if (role.equals("CX-Landlord"))
                            Kotpref.loginRole = "landlord"
                        else if (role.equals("CX-Broker"))
                            Kotpref.loginRole = "broker"
                        else if (role.equals("CX-Tenant"))
                            Kotpref.loginRole = "tenant"
                        else if (role.equals("CX-Agent"))
                            Kotpref.loginRole = "agent"


                        val intent = Intent(this, SignupDoneActivity::class.java)
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
                            //Toast.makeText(applicationContext, getString(R.string.error_email_exist), Toast.LENGTH_SHORT).show()
                            Toast.makeText(
                                applicationContext,
                                "You are already registered with the same Licence No/Email ID.",
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


    private fun registerTenantApi() {

        var registrationPayload = RegistrationPayload()
        registrationPayload.firstName = edit_reg_first_name?.text.toString()
        registrationPayload.lastName = edit_reg_last_name?.text.toString()
        registrationPayload.emailId = edit_reg_email?.text.toString()
        registrationPayload.confirmPassword = RSA.encrypt(edit_reg_crm_pwd?.text.toString())
        registrationPayload.password = RSA.encrypt(edit_reg_crm_pwd?.text.toString())
        registrationPayload.userRoleName = role

        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            //Create retrofit Service
            pb_select_role!!.visibility = View.VISIBLE
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )

            val registerService: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<UserRegistrationResponse> = registerService.registerUser(
                registrationPayload
            )

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", it.responseDto?.responseDescription.toString())



                    if (it!!.responseDto!!.responseCode.equals(200)) {
                        Util.alertOkIntentMessage(
                            this@SignupByEmailActivity,
                            getString(R.string.alert),
                            getString(R.string.tag_invite_landlord),
                            LoginScreen::class.java
                        )


                        if (it!!.responseDto!!.responseCode.equals(200)) {
                            if (role.equals("CX-Landlord"))
                                Kotpref.loginRole = "landlord"
                            else if (role.equals("CX-Broker"))
                                Kotpref.loginRole = "broker"
                            else if (role.equals("CX-Tenant"))
                                Kotpref.loginRole = "tenant"
                            else if (role.equals("CX-Agent"))
                                Kotpref.loginRole = "agent"


                            val intent = Intent(this, SignupDoneActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                            intent.putExtra("email", edit_reg_email?.text.toString())
                            startActivity(intent)
                            finish()
                        }


                    } else {
                        pb_select_role!!.visibility = View.GONE
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