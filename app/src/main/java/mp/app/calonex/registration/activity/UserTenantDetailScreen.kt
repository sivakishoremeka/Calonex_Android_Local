package mp.app.calonex.registration.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_bank_details.*
import mp.app.calonex.R
import kotlinx.android.synthetic.main.activity_user_tenant_detail_screen.*
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.RSA
import mp.app.calonex.common.utility.UsPhoneNumberFormatter
import mp.app.calonex.common.utility.Util
import mp.app.calonex.common.utility.Util.isEmailValid
import mp.app.calonex.common.utility.Util.navigationBack
import mp.app.calonex.common.utility.Util.valueMandetory
import mp.app.calonex.landlord.activity.LoginScreen
import mp.app.calonex.landlord.activity.LoginScreen.Companion.registrationPayload4
import mp.app.calonex.registration.response.UserRegistrationResponse4
import retrofit2.HttpException
import java.util.regex.Matcher
import java.util.regex.Pattern

class UserTenantDetailScreen : AppCompatActivity() {
    private var editUtFirstName: TextInputEditText? = null
    private var editUtLastName: TextInputEditText? = null
    private var editUtEmail: TextInputEditText? = null
    private var editUtPhnNmbr: TextInputEditText? = null
    private var editPassword: TextInputEditText? = null
    private var editConfPassword: TextInputEditText? = null

    private var add__progress: ProgressBar? = null

    private var btnUtNxt: TextView? = null
    private  var signup:TextView?=null

    private var utFirstName: String? = null
    private var utLastName: String? = null
    private var utEmail: String? = null
    private var utPhnNmbr: String? = null
    private var utPassword: String? = null
    private var utConfPassword: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_tenant_detail_screen)
        try {
            initComponent()
            actionComponent()
        } catch (e: Exception) {
            Log.e("Tenant Sign ", e.message.toString())
        }
    }

    fun initComponent() {
        signup=findViewById(R.id.signup);
        editUtFirstName = findViewById(R.id.edit_ut_first_name)
        editUtLastName = findViewById(R.id.edit_ut_last_name)
        editUtEmail = findViewById(R.id.edit_ut_email)
        editUtPhnNmbr = findViewById(R.id.edit_ut_phn)
        editPassword = findViewById(R.id.edit_cred_password)
        editConfPassword = findViewById(R.id.edit_cred_cnfrm_password)
        add__progress=findViewById(R.id.add__progress);

        btnUtNxt = findViewById(R.id.btn_ut_next)

        val addLineNumberFormatter = UsPhoneNumberFormatter(editUtPhnNmbr!!)
        editUtPhnNmbr!!.addTextChangedListener(addLineNumberFormatter)
    }


    fun actionComponent() {



        signup!!.setOnClickListener {
            // startActivity(Intent(this@QuickRegistrationActivity, LoginScreen::class.java))
            val intent = Intent(this, LoginScreen::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }


        btnUtNxt!!.setOnClickListener { view ->
            // Hide the keyboard
            val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)

            validComponent()
        }
    }


    fun validComponent() {
        utFirstName = editUtFirstName!!.text.toString().trim()
        utLastName = editUtLastName!!.text.toString().trim()
        utEmail = editUtEmail!!.text.toString().trim()
        utPhnNmbr = editUtPhnNmbr!!.text.toString().trim()
        utPassword = editPassword!!.text.toString().trim()
        utConfPassword = editConfPassword!!.text.toString().trim()


        if (valueMandetory(applicationContext, utFirstName, editUtFirstName!!)) {
            return
        }
        if (valueMandetory(applicationContext, utLastName, editUtLastName!!)) {
            return
        }
        if (valueMandetory(applicationContext, utEmail, editUtEmail!!)) {
            return
        }
        if ((!isEmailValid(utEmail!!))) {
            editUtEmail?.error = getString(R.string.error_valid_email)
            editUtEmail?.requestFocus()
            return
        }

        if (valueMandetory(applicationContext, utPhnNmbr, editUtPhnNmbr!!)) {
            return
        }

        if (utPhnNmbr!!.length != 14) {
            editUtPhnNmbr?.error = getString(R.string.error_phn)
            editUtPhnNmbr?.requestFocus()
            return
        }


        // Check the password string is empty or not+

        if(utPassword!!.isNullOrEmpty()){
            layout_cred_paswrd?.isPasswordVisibilityToggleEnabled=false
            editPassword?.error  = getString(R.string.error_pwd)
            editPassword?.requestFocus()
            return
        }

        if(utPassword!!.length<8){
            layout_cred_paswrd?.isPasswordVisibilityToggleEnabled=false
            editPassword?.error  = getString(R.string.error_pwd_length)
            editPassword?.requestFocus()
            return
        }

        var pattern: Pattern = Pattern.compile("[^a-zA-Z0-9]")
        var matcher: Matcher = pattern.matcher(utPassword)
        var isStringContainsSpecialCharacter: Boolean = matcher.find()

        if(!isStringContainsSpecialCharacter){
            layout_cred_paswrd?.isPasswordVisibilityToggleEnabled=false
            editPassword?.error  = getString(R.string.error_pwd_character)
            editPassword?.requestFocus()
            return
        }

        // Check the password string is empty or not
        if(utConfPassword!!.isNullOrEmpty()){
            layout_cred_cnfrm_pswrd!!.isPasswordVisibilityToggleEnabled=false
            editConfPassword!!.error  = getString(R.string.error_pwd)
            editConfPassword!!.requestFocus()
            return
        }

        // Check the password string is empty or not
        if(! utConfPassword!!.equals(utPassword)){
            layout_cred_cnfrm_pswrd!!.isPasswordVisibilityToggleEnabled=false
            editConfPassword!!.error  = getString(R.string.error_confirm_paswrd)
            editConfPassword!!.requestFocus()
            return
        }


        registrationPayload4.firstName = utFirstName
        registrationPayload4.middleName = ""
        registrationPayload4.lastName = utLastName
        registrationPayload4.emailId = utEmail
        registrationPayload4.password = RSA.encrypt(utPassword!!)
        registrationPayload4.confirmPassword = RSA.encrypt(utConfPassword!!)
        registrationPayload4.tenantId = ""
        registrationPayload4.userRoleName = "CX-Tenant"
        registrationPayload4.phoneNumber = utPhnNmbr!!.filter { it.isDigit() }

        //navigationNext(this, InviteLandlordScreen::class.java)
        registerApi()
    }

    @SuppressLint("CheckResult")
    private fun registerApi() {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            add__progress?.visibility = View.VISIBLE
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

            val registerService: ApiInterface = ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<UserRegistrationResponse4> = registerService.registerUser2(registrationPayload4)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
                Log.e("onSuccess", it.responseDto?.responseDescription.toString())


                add__progress?.visibility = View.GONE

                if(it!!.responseDto!!.responseCode.equals(200)) {
                    Util.alertOkIntentMessage(
                        this@UserTenantDetailScreen,
                        getString(R.string.alert),
                        "Thank you for registering with Calonex. Login here",
                        LoginScreen::class.java
                    )


                }else{
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    Toast.makeText(applicationContext,it!!.responseDto!!.responseDescription, Toast.LENGTH_SHORT).show()
                }



            },
                { e ->
                    Log.e("onFailure", e.toString())
                    add__progress?.visibility = View.GONE

                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    val exception: HttpException = e as HttpException
                    if(exception.code().equals(400) ){
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.error_email_exist),
                            Toast.LENGTH_SHORT
                        ).show()

                    }else{
                        e.message?.let {
                            Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                        }
                    }
                })
        } else {
            Toast.makeText(applicationContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show()

        }
    }


    override fun onBackPressed() {
        super.onBackPressed()

        navigationBack(this)
    }
}