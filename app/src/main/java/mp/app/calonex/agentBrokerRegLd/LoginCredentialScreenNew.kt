package mp.app.calonex.agentBrokerRegLd

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login_credential_screen.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.EmailCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.RSA
import mp.app.calonex.common.utility.Util
import mp.app.calonex.agent.fragment.ProfileFragmentAgent.Companion.registrationPayload
import mp.app.calonex.agentBrok.UserContactDetailScreenNew
import mp.app.calonex.landlord.activity.CxBaseActivity2
import mp.app.calonex.registration.response.ValidateEmailResponse
import java.util.regex.Matcher
import java.util.regex.Pattern

class LoginCredentialScreenNew : CxBaseActivity2() {

    private var editCredEmail:TextInputEditText?=null
    private var editCredPassword:TextInputEditText?=null
    private var editCredConfirmPassword:TextInputEditText?=null
    private var txtPlan:TextView?=null
    private var btnCredNext:TextView?=null
    private var btnCredSignin:TextView?=null
    private var credEmail:String?=null
    private var credPassword:String?=null
    private var credConfirmPassword:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_credential_screen_new)
        initComponent()
        actionComponent()
        startHandler()
    }

    fun initComponent(){
        txtPlan=findViewById(R.id.txt_plan)
        txtPlan!!.text=Kotpref.planSelected
        editCredEmail=findViewById(R.id.edit_cred_email)
        editCredPassword=findViewById(R.id.edit_cred_password)
        editCredConfirmPassword=findViewById(R.id.edit_cred_cnfrm_password)
        btnCredNext=findViewById(R.id.btn_cred_next)
        btnCredSignin=findViewById(R.id.txt_signin)

    }

    fun actionComponent(){

        editCredPassword!!.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
                layout_cred_paswrd?.isPasswordVisibilityToggleEnabled = true
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {

            }
        })

        editCredConfirmPassword!!.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
                layout_cred_cnfrm_pswrd?.isPasswordVisibilityToggleEnabled = true
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {

            }
        })

        btnCredNext!!.setOnClickListener { view ->
            // Hide the keyboard
            val imm =getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            validComponent()

        }

        btnCredSignin!!.setOnClickListener { view ->
        }

    }

    fun validComponent(){
        credEmail=editCredEmail!!.text.toString().trim()
        credPassword=editCredPassword!!.text.toString().trim()
        credConfirmPassword=editCredConfirmPassword!!.text.toString().trim()

        // Check the email string is empty or not
        if(credEmail!!.isNullOrEmpty()  ){
            editCredEmail?.error = getString(R.string.error_email)
            editCredEmail?.requestFocus()
            return
        }
        if((! Util.isEmailValid(credEmail!!))){
            editCredEmail?.error = getString(R.string.error_valid_email)
            editCredEmail?.requestFocus()
            return
        }


        // Check the password string is empty or not+

        if(credPassword!!.isNullOrEmpty()){
            layout_cred_paswrd?.isPasswordVisibilityToggleEnabled=false
            editCredPassword?.error  = getString(R.string.error_pwd)
            editCredPassword?.requestFocus()
            return
        }

        if(credPassword!!.length<8){
            layout_cred_paswrd?.isPasswordVisibilityToggleEnabled=false
            editCredPassword?.error  = getString(R.string.error_pwd_length)
            editCredPassword?.requestFocus()
            return
        }
        var pattern: Pattern = Pattern.compile("[^a-zA-Z0-9]")
        var matcher: Matcher = pattern.matcher(credPassword)
        var isStringContainsSpecialCharacter: Boolean = matcher.find()
        if(!isStringContainsSpecialCharacter){
            layout_cred_paswrd?.isPasswordVisibilityToggleEnabled=false
            editCredPassword?.error  = getString(R.string.error_pwd_character)
            editCredPassword?.requestFocus()
            return
        }

        // Check the password string is empty or not
        if(credConfirmPassword!!.isNullOrEmpty()){
            layout_cred_cnfrm_pswrd!!.isPasswordVisibilityToggleEnabled=false
            editCredConfirmPassword!!.error  = getString(R.string.error_pwd)
            editCredConfirmPassword!!.requestFocus()
            return
        }

        // Check the password string is empty or not
        if(! credConfirmPassword!!.equals(credPassword)){
            layout_cred_cnfrm_pswrd!!.isPasswordVisibilityToggleEnabled=false
            editCredConfirmPassword!!.error  = getString(R.string.error_confirm_paswrd)
            editCredConfirmPassword!!.requestFocus()
            return
        }

        registrationPayload.emailId=credEmail!!
        registrationPayload.password= RSA.encrypt(credPassword!!)
        registrationPayload.confirmPassword= RSA.encrypt(credConfirmPassword!!)
        validateApi()

    }

    private fun validateApi() {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            //Create retrofit Service
            pb_validate!!.visibility = View.VISIBLE
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            val credentials = EmailCredential()
            credentials.emailId= credEmail
            val validateService: ApiInterface = ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<ValidateEmailResponse> = validateService.emailValidate(credentials)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
                Log.e("onSuccess", it.responseDto?.responseDescription.toString())

                pb_validate!!.visibility = View.GONE
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)


                if(it!!.responseDto!!.responseCode.equals(200) && it.data!=null) {


                   if(it.data!!.canRegister!!){
                       Util.navigationNext(this, UserContactDetailScreenNew::class.java)
                   }else{
                       Toast.makeText(applicationContext,it.data!!.canRegisterMessage, Toast.LENGTH_SHORT).show()
                   }

                }

                else{
                    Toast.makeText(applicationContext,it!!.responseDto!!.responseDescription, Toast.LENGTH_SHORT).show()
                }



            },
                { e ->
                    Log.e("onFailure", e.toString())
                    pb_validate!!.visibility = View.GONE

                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    e.message?.let {
                        Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            Toast.makeText(applicationContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show()

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Util.navigationBack(this)
    }
}