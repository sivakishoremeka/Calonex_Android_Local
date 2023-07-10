package mp.app.calonex.landlord.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.registration_new.*
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
import mp.app.calonex.utility.ValidationUtils.isValidConfirmPassword
import mp.app.calonex.utility.ValidationUtils.isValidPassword
import retrofit2.HttpException

class RegistrationNewActivity : AppCompatActivity() {

    //selector design
    var selectedIndex: Int = -1
    var selectedUserType: String = ""
    var role: String = ""
    lateinit var landlordSelector: LinearLayout
    lateinit var usertypelayout: LinearLayout
    lateinit var landlordCard: CardView
    lateinit var landlordIcon: ImageView
    lateinit var landlordText: TextView

    lateinit var tenantSelector: LinearLayout
    lateinit var tenantCard: CardView
    lateinit var tenantIcon: ImageView
    lateinit var tenantText: TextView

    lateinit var brokerSelector: LinearLayout
    lateinit var brokerCard: CardView
    lateinit var brokerIcon: ImageView
    lateinit var brokerText: TextView

    lateinit var agentSelector: LinearLayout
    lateinit var agentCard: CardView
    lateinit var agentIcon: ImageView
    lateinit var agentText: TextView

    /*------------------------------------------------------*/
    /*-----------------------user form ---------------------*/
    private var edit_reg_first_name: TextInputEditText? = null
    private var edit_reg_licence_number: TextInputEditText? = null
    private lateinit var input_layout_licence_number: TextInputLayout
    private var edit_reg_last_name: TextInputEditText? = null
    private var edit_reg_email: TextInputEditText? = null
    private var edit_reg_pwd: TextInputEditText? = null
    private var edit_reg_crm_pwd: TextInputEditText? = null
    private var txt_register: TextView? = null
    private var cb_termcon: CheckBox? = null
    private var get_license: String? = null

    /*-----------------------user form Ends ---------------------*/
    /*--------------social Login design----------- */
    private var iv_google: ImageView? = null
    private var iv_fb: CircleImageView? = null
    private var civ_apple: CircleImageView? = null
    /*--------------social Login design Ends----------- */


    private var til_routin: TextInputLayout? = null


    private var btn_signup: Button? = null

    private var til_ln: TextInputLayout? = null
    private var txt_signin: TextView? = null
    private var termCondition: TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration_new)
        setView()
        landlordSelector.setOnClickListener {
            selectedUserType = "CX-Landlord"
            updateSelection(0)

        }
        agentSelector.setOnClickListener {
            selectedUserType = "CX-Agent"
            updateSelection(3)

        }
        tenantSelector.setOnClickListener {
            selectedUserType = "CX-Tenant"
            updateSelection(1)
        }
        brokerSelector.setOnClickListener {
            selectedUserType = "CX-Broker"
            updateSelection(2)
        }

    }

    private fun updateSelection(index: Int) {
        if (index == 0) {
            role = "CX-Landlord"
            selectedIndex = index;
            landlordSelected()
            showLicenceLayout(0)
            agentUnselected()
            brokerUnselected()
            tenantUnselected()
            showLayouterror(1)


        }
        if (index == 1) {
            role = "CX-Tenant"
            selectedIndex = index;
            tenantSelected()
            agentUnselected()
            brokerUnselected()
            landlordUnSelected()
            showLicenceLayout(0)
            showLayouterror(1)

        }
        if (index == 2) {
            role = "CX-Broker"
            selectedIndex = index;
            brokerSelected()
            agentUnselected()
            landlordUnSelected()
            tenantUnselected()
            showLicenceLayout(1)
            edit_reg_licence_number!!.hint = "Broker Licence Number"
            showLayouterror(1)

        }
        if (index == 3) {
            role = "CX-Agent"
            selectedIndex = index;
            agentSelected()
            landlordUnSelected()
            brokerUnselected()
            tenantUnselected()
            showLicenceLayout(1)
            edit_reg_licence_number!!.hint = "Agent Broker Licence Number"
            showLayouterror(1)

        }
    }

    private fun setView() {
        termCondition = findViewById(R.id.txt_term_condition)
        landlordSelector = findViewById(R.id.landlord_selector)
        usertypelayout = findViewById(R.id.usertypelayout)
        landlordCard = findViewById(R.id.landlord_card)
        landlordIcon = findViewById(R.id.landlord_icon)
        landlordText = findViewById(R.id.landlord_text)
        tenantSelector = findViewById(R.id.tenant_selector)
        tenantCard = findViewById(R.id.tenant_card)
        tenantIcon = findViewById(R.id.tenant_icon)
        tenantText = findViewById(R.id.tenant_text)
        brokerSelector = findViewById(R.id.broker_selector)
        brokerCard = findViewById(R.id.broker_card)
        brokerIcon = findViewById(R.id.broker_icon)
        brokerText = findViewById(R.id.broker_text)
        agentSelector = findViewById(R.id.agent_selector)
        agentCard = findViewById(R.id.agent_card)
        agentIcon = findViewById(R.id.agent_icon)
        agentText = findViewById(R.id.agent_text)

        edit_reg_licence_number = findViewById(R.id.edit_reg_licence)
        input_layout_licence_number = findViewById(R.id.input_layout_licence_number)
        edit_reg_first_name = findViewById(R.id.edit_reg_first_name);
        edit_reg_last_name = findViewById(R.id.edit_reg_last_name);
        edit_reg_email = findViewById(R.id.edit_reg_email);
        edit_reg_pwd = findViewById(R.id.edit_reg_pwd);
        edit_reg_crm_pwd = findViewById(R.id.edit_reg_confirm_pwd)
        cb_termcon = findViewById(R.id.cb_termcon)


        iv_google = findViewById(R.id.iv_google);
        iv_fb = findViewById(R.id.im_fb)
        civ_apple = findViewById(R.id.iv_apple);
        btn_signup = findViewById(R.id.btn_signup)

        showLicenceLayout(0)
        showLayouterror(1)
        socialLogin()


        edit_reg_pwd?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {

                if (edit_reg_pwd!!.text.toString().isNotEmpty()) {
                    Log.e(
                        "Check",
                        "Password is ==> " + isValidPassword(edit_reg_pwd?.text.toString())
                    )
                    if (!isValidPassword(edit_reg_pwd?.text.toString())) {
                        txt_layout_pswrd?.error = getString(R.string.error_password_login)

                    } else {
                        //txt_layout_pswrd.errorEna
                        txt_layout_pswrd?.error = ""
                    }
                }
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {

            }
        })

        edit_reg_crm_pwd?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                Log.e(
                    "Check",
                    "Password is ==> " + isValidPassword(edit_reg_crm_pwd?.text.toString())
                )
                if (edit_reg_crm_pwd!!.text.toString().isNotEmpty()) {

                    if (!isValidPassword(edit_reg_crm_pwd?.text.toString())) {
                        txt_layout_confirm_pswrd?.error = getString(R.string.error_password_login)

                    } else {
                        txt_layout_confirm_pswrd?.error = ""
                    }
                }
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {

            }
        })



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
        termCondition!!.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse("https://app.calonex.com/account/terms-and-conditions");
            startActivity(i)
        }
        btn_signup!!.isEnabled = false
        cb_termcon!!.setOnCheckedChangeListener { buttonView, isChecked ->
            btn_signup!!.isEnabled = isChecked
        }

    }

    private fun socialLogin() {
        iv_fb!!.setOnClickListener {
            if (validationForSocialLogin()) {

                socialLogin(selectedUserType, "facebook", "register")

            }
        }
        civ_apple!!.setOnClickListener {

            if (validationForSocialLogin()) {
                socialLogin(selectedUserType, "apple", "register")
            }

        }


        iv_google!!.setOnClickListener {

            if (validationForSocialLogin()) {
                socialLogin(selectedUserType, "google", "register")
            }

        }


    }


    private fun landlordSelected() {
        landlordCard.setCardBackgroundColor(Color.parseColor("#007AFF"))
        landlordIcon.setImageResource(R.drawable.reg_landlord_white)
        landlordText.setTextColor((Color.parseColor("#4194CC")))
    }

    private fun landlordUnSelected() {
        landlordCard.setCardBackgroundColor(Color.parseColor("#F4FAFF"))
        landlordIcon.setImageResource(R.drawable.reg_landlord_blue)
        landlordText.setTextColor((Color.parseColor("#2D3748")))
    }


    private fun agentSelected() {
        agentCard.setCardBackgroundColor(Color.parseColor("#007AFF"))
        agentIcon.setImageResource(R.drawable.reg_agent_white)
        agentText.setTextColor((Color.parseColor("#4194CC")))
    }

    private fun agentUnselected() {
        agentCard.setCardBackgroundColor(Color.parseColor("#F4FAFF"))
        agentIcon.setImageResource(R.drawable.reg_agent_blue)
        agentText.setTextColor((Color.parseColor("#2D3748")))
    }

    private fun brokerSelected() {
        brokerCard.setCardBackgroundColor(Color.parseColor("#007AFF"))
        brokerIcon.setImageResource(R.drawable.reg_broker_white)
        brokerText.setTextColor((Color.parseColor("#4194CC")))
    }

    private fun brokerUnselected() {
        brokerCard.setCardBackgroundColor(Color.parseColor("#F4FAFF"))
        brokerIcon.setImageResource(R.drawable.reg_broker_blue)
        brokerText.setTextColor((Color.parseColor("#2D3748")))
    }

    private fun tenantSelected() {
        tenantCard.setCardBackgroundColor(Color.parseColor("#007AFF"))
        tenantIcon.setImageResource(R.drawable.reg_tenant_white)
        tenantText.setTextColor((Color.parseColor("#4194CC")))
    }

    private fun tenantUnselected() {
        tenantCard.setCardBackgroundColor(Color.parseColor("#F4FAFF"))
        tenantIcon.setImageResource(R.drawable.reg_tenant_blue)
        tenantText.setTextColor((Color.parseColor("#2D3748")))
    }

    private fun showLicenceLayout(i: Int) {
        if (i == 0)
            input_layout_licence_number.visibility = View.GONE
        else
            input_layout_licence_number.visibility = View.VISIBLE

    }

    private fun showLayouterror(i: Int) {
        if (i == 0)
            usertypelayout.setBackgroundDrawable(getResources().getDrawable(R.drawable.layout_error))
        else
            usertypelayout.setBackgroundDrawable(null)

    }


    private fun socialLogin(role: String?, aacount: String?, authtype: String) {
        if (selectedIndex == 2 || selectedIndex == 3) {
            var ln: String = edit_reg_licence_number!!.text.toString()
            val uri =
                Uri.parse("https://api.calonex.com/social-service/oauth2/authorization/$aacount?license=$ln&authtype=$authtype&redirect_uri=https://app.calonex.com/social-login&role=$role")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            Log.e("google sign up", "signUp: $uri")
            startActivity(intent)
        } else {
            val uri =
                Uri.parse("https://api.calonex.com/social-service/oauth2/authorization/$aacount?authtype=$authtype&redirect_uri=https://app.calonex.com/social-login&role=$role")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            Log.e("google sign up", "signUp: $uri")
            startActivity(intent)
            // Log.d("google sign up", "signUp: uri")
        }
    }

    private fun validationForSocialLogin(): Boolean {

        //Log
        Log.e("Social", "Login for selectedIndex>> $selectedIndex")
        Log.e("Social", "License Length >> ${edit_reg_licence_number!!.text?.toString()!!.length}")
        var flag = true
        if (selectedIndex == -1) {
            showLayouterror(0)
            flag = false

        } else if (selectedIndex == 2 && edit_reg_licence_number!!.text?.toString()!!.length != 11) {
            input_layout_licence_number.error = "Invalid Broker License number."
            input_layout_licence_number.requestFocus()
            flag = false
        } else if (selectedIndex == 3 && edit_reg_licence_number!!.text?.toString()!!.length != 11) {
            input_layout_licence_number.error = "Invalid Agent Broker License number."
            input_layout_licence_number.requestFocus()
            flag = false
        }

        return flag
    }

    private fun checkValidation(): Boolean {
        Log.e("Check", "Tag ==> $selectedIndex")
        Log.e("Check", "License ==> ${edit_reg_licence_number!!.text?.toString()!!}")
        Log.e(
            "Check",
            "License No Count ==> ${edit_reg_licence_number!!.text?.toString()!!.length}"
        )

        if (selectedIndex == -1) {
            showLayouterror(0)
            Toast.makeText(this, "Please Select User type first.", Toast.LENGTH_LONG).show()
            return false
        } else if (selectedIndex == 2 && edit_reg_licence_number!!.text?.toString()!!.length != 11) {
            input_layout_licence_number.error = "Invalid Broker License number."
            input_layout_licence_number.requestFocus()
            return false
        } else if (selectedIndex == 3 && edit_reg_licence_number!!.text?.toString()!!.length != 11) {
            input_layout_licence_number.error = "Invalid Agent Broker License number."
            input_layout_licence_number.requestFocus()
            return false
        }

        if (edit_reg_first_name?.text.toString().isNullOrEmpty()) {
            edit_reg_first_name?.error = "Mandatory Field."
            edit_reg_first_name?.requestFocus()
            return false
        } else if (edit_reg_last_name?.text.toString().isNullOrEmpty()) {
            edit_reg_last_name?.error = "Mandatory Field."
            edit_reg_last_name?.requestFocus()
            return false
        } else if (edit_reg_email?.text.toString().isNullOrEmpty()) {
            edit_reg_email?.error = "Mandatory Field."
            edit_reg_email?.requestFocus()
            return false
        } else if (!Util.isEmailValid(edit_reg_email?.text.toString())) {
            edit_reg_email?.error = "Valid Email Required"
            edit_reg_email?.requestFocus()
            return false
        }

        /*else if (edit_reg_pwd?.text.toString().isNullOrEmpty()) {
            edit_reg_pwd?.setError("Mandatory Field.")
            return false
        } else if (edit_reg_pwd?.text.toString().length < 8) {
            edit_reg_pwd?.setError("Password should have 8 or more characters.")
            return false
        }
*/
        /* var pattern: Pattern = Pattern.compile("[^a-zA-Z0-9]")
         var matcher: Matcher = pattern.matcher(edit_reg_pwd?.text.toString())
         var isStringContainsSpecialCharacter: Boolean = matcher.find()*/

        if (!isValidPassword(edit_reg_pwd?.text.toString())) {
            //layout_cred_paswrd?.isPasswordVisibilityToggleEnabled = false
            txt_layout_pswrd?.error = getString(R.string.error_password_login)
            //edit_reg_pwd?.requestFocus()
            return false

        }

        if (!isValidPassword(edit_reg_crm_pwd?.text.toString())) {
            txt_layout_confirm_pswrd?.error = getString(R.string.error_password_login)
            //edit_reg_crm_pwd?.requestFocus()
            return false

        }

        if (!isValidConfirmPassword(
                edit_reg_pwd?.text.toString(),
                edit_reg_crm_pwd?.text.toString()
            )
        ) {
            Toast.makeText(
                this,
                "New Password and Confirm Password need to be same",
                Toast.LENGTH_LONG
            ).show()
            return false
        }

        if (!cb_termcon!!.isChecked) {
            Toast.makeText(
                applicationContext,
                "Select Terms & Conditions and Privacy Policy Check box first.",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        return true
    }

    @SuppressLint("CheckResult")
    private fun registerApi() {

        var registrationPayload4 = RegistrationPayload2()
        registrationPayload4.firstName = edit_reg_first_name?.text.toString()
        registrationPayload4.lastName = edit_reg_last_name?.text.toString()
        registrationPayload4.emailId = edit_reg_email?.text.toString()
        registrationPayload4.confirmPassword = RSA.encrypt(edit_reg_pwd?.text.toString())
        registrationPayload4.password = RSA.encrypt(edit_reg_pwd?.text.toString())
        registrationPayload4.userRoleName = role

        try {
            get_license = edit_reg_licence!!.text.toString()
            if (get_license!!.isNotEmpty()) {
                registrationPayload4.agentLicenseNumber = get_license!!.trim()
                registrationPayload4.brokerLicenseNumber = get_license!!.trim()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            //Create retrofit Service
            pb!!.visibility = View.VISIBLE
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
                        Kotpref.loginRole = selectedUserType

                        val intent = Intent(this, SignupDoneActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                        intent.putExtra("email", edit_reg_email?.text.toString())
                        startActivity(intent)
                        finish()

                    } else {
                        pb!!.visibility = View.GONE
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
                        pb!!.visibility = View.GONE
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
        registrationPayload.password = RSA.encrypt(edit_reg_pwd?.text.toString())
        registrationPayload.userRoleName = role

        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            //Create retrofit Service
            pb!!.visibility = View.VISIBLE
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
                            this@RegistrationNewActivity,
                            getString(R.string.alert),
                            getString(R.string.tag_invite_landlord),
                            LoginScreen::class.java
                        )


                        if (it!!.responseDto!!.responseCode.equals(200)) {
                            Kotpref.loginRole = selectedUserType


                            val intent = Intent(this, SignupDoneActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                            intent.putExtra("email", edit_reg_email?.text.toString())
                            startActivity(intent)
                            finish()
                        }


                    } else {
                        pb!!.visibility = View.GONE
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
                        pb!!.visibility = View.GONE
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