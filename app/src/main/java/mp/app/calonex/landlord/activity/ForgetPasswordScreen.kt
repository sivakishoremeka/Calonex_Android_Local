package mp.app.calonex.landlord.activity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_forget_password.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.EmailCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.tenant.response.ResponseDtoResponse
import java.lang.Exception

class ForgetPasswordScreen : AppCompatActivity() {
    private val Tag :String?= "ForgetPasswordScreen"
    private var email: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)
        supportActionBar?.hide()
        try {
            initAction()
        } catch (e: Exception) {
            Log.d(Tag, e.localizedMessage)
        }
    }

    /**
     * Method to initaialize the component of the class
     */
    private fun initAction() {

        //Define the click event on the login Button
        btn_send_email!!.setOnClickListener { view ->

            // Hide the keyboard
            val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)

            checkEnterValues()
        }

        btn_back!!.setOnClickListener { view ->
            // Util.navigationNext(this,LoginScreen::class.java)
            finish()
        }
    }


    private fun checkEnterValues() {
        // Set the value of email and password from their respective input layout into their strings
        email = edit_fp_username?.text.toString().trim()


        // Check the email string is empty or not
        if (email!!.isEmpty() || (!Util.isEmailValid(email!!))) {
            edit_fp_username?.error = getString(R.string.error_email)
            edit_fp_username?.requestFocus()
            return
        }

        forgotPass()
       

    }

    private fun forgotPass() {
        if (NetworkConnection.isNetworkConnected(this)) {
            pb_forget!!.visibility= View.VISIBLE
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            val credentials = EmailCredential()
            credentials.emailId= email

            val signatureApi: ApiInterface = ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<ResponseDtoResponse> =
                signatureApi.passwordForgot(credentials)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
                Log.e("onSuccess", it.responseDto.toString())


                pb_forget!!.visibility= View.GONE
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                if(it!!.responseDto!!.responseCode.equals(200)) {

                    Util.alertOkIntentMessage(
                        this,
                        getString(R.string.app_name),
                        "A request has been send on your email to get the password",
                        LoginScreen::class.java)
                }else{

                    Toast.makeText(this, it!!.responseDto!!.responseDescription, Toast.LENGTH_SHORT).show()
                }



            },
                { e ->
                    Log.e("onFailure", e.toString())
                    pb_forget!!.visibility= View.GONE
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    e.message?.let {
                        Toast.makeText(this, "Email id not found", Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            Toast.makeText(this, getString(R.string.error_network), Toast.LENGTH_SHORT).show()

        }
    }
    
    override fun onBackPressed() {
        super.onBackPressed()
        Util.navigationBack(this@ForgetPasswordScreen)
    }

}
