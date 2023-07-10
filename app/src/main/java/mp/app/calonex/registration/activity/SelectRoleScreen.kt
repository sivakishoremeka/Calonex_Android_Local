package mp.app.calonex.registration.activity

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.Html
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import mp.app.calonex.R
import mp.app.calonex.agent.activity.QuickRegistrationActivityAgent
import mp.app.calonex.broker.activity.QuickRegistrationActivityBroker
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.LoginScreen
import mp.app.calonex.landlord.activity.QuickRegistrationActivity


class SelectRoleScreen : AppCompatActivity() {

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_role_screen)
        var btnLandlord = findViewById<LinearLayout>(R.id.btn_landlord)
        var btn_broker = findViewById<LinearLayout>(R.id.btn_broker)
        var btn_agent = findViewById<LinearLayout>(R.id.btn_agent)
        var btnTenant = findViewById<LinearLayout>(R.id.btn_tenant)
        var btnSignup = findViewById<TextView>(R.id.signup)

        var text_1 = findViewById<TextView>(R.id.text_1)
        var text_2 = findViewById<TextView>(R.id.text_2)

        var text_1_broker = findViewById<TextView>(R.id.text_1_broker)
        var text_1_agent = findViewById<TextView>(R.id.text_1_agent)

        text_1.text = Html.fromHtml(getString(R.string.landlord_b_click_here_text))
        text_2.text = Html.fromHtml(getString(R.string.tenant_b_click_here_text))

        text_1_broker.text = Html.fromHtml(getString(R.string.broker_b_click_here_text))
        text_1_agent.text = Html.fromHtml(getString(R.string.agent_b_click_here_text))


        // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
           // .requestIdToken(" 418185046855-2p95cbblmrpqdntmpjbi7h5msekfgike.apps.googleusercontent.com")
            .requestEmail()
            .build()
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        val googleLoginButton = findViewById<SignInButton>(R.id.sign_in_button)
        googleLoginButton.setOnClickListener {
            signIn()
        }


        btnLandlord!!.setOnClickListener {
            //getPlanSubcription()
            /*Kotpref.isRegisterConfirm=false
            LoginScreen.registrationPayload.userRoleName=getString(R.string.cx_landlord)
            LoginScreen.registrationPayload.deviceId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID)*/
            Util.navigationNext(this, QuickRegistrationActivity::class.java)
        }
        btnTenant!!.setOnClickListener {

            Kotpref.isRegisterConfirm = false
            LoginScreen.registrationPayload.userRoleName = getString(R.string.cx_tenant)
            LoginScreen.registrationPayload.deviceId = Settings.Secure.getString(
                contentResolver,
                Settings.Secure.ANDROID_ID
            )
            Util.navigationNext(this, UserTenantDetailScreen::class.java)
        }
        btnSignup!!.setOnClickListener {
            /* val intent = Intent(this@SelectRoleScreen, LoginScreen::class.java)
            startActivity(intent)
           */ finish()
        }

        btn_broker.setOnClickListener {
            Util.navigationNext(this, QuickRegistrationActivityBroker::class.java)

            /*
             Please click here if you’re a broker to register to start acquiring business and adding Landlord.
             */
        }

        btn_agent.setOnClickListener {
            Util.navigationNext(this, QuickRegistrationActivityAgent::class.java)

            /*
            To invite your broker or link yourself with your broker click here to get started.
             */
        }
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(
            signInIntent, RC_SIGN_IN
        )
    }

    companion object {
        const val RC_SIGN_IN = 9001
    }

    /*private fun getPlanSubcription() {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            //Create retrofit Service
            pb_select_role!!.visibility = View.VISIBLE
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

            val signatureApi: ApiInterface = ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<SubcriptionPlanResponse> =
                signatureApi.getSubscription("")

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
                Log.e("onSuccess", it.responseDto?.responseDescription.toString())


                pb_select_role!!.visibility = View.GONE
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)


                if (it.data != null) {
                    subcriptionPlanList=it.data!!
                    val intent=Intent(this,SelectSubcriptionPlan::class.java)
                    intent.putExtra(getString(R.string.is_register),true)
                    startActivity(intent)
                    Util.navigationNext(this)
                }




            },
                { e ->
                    Log.e("onFailure", e.toString())
                    pb_select_role!!.visibility = View.GONE
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    e.message?.let {
                        Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            Toast.makeText(applicationContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show()

        }
    }


    companion object {
        var registrationPayload=RegistrationPayload()
        var subcriptionPlanList=ArrayList<SubcriptionPlanModel>()
    }*/

    override fun onBackPressed() {
        super.onBackPressed()
        Util.navigationBack(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(
                ApiException::class.java
            )




            // Signed in successfully
            val googleId = account?.id ?: ""
            Log.i("Google ID", googleId)
            val googleFirstName = account?.givenName ?: ""
            Log.i("Google First Name", googleFirstName)
            val googleLastName = account?.familyName ?: ""
            Log.i("Google Last Name", googleLastName)
            val googleEmail = account?.email ?: ""
            Log.i("Google Email", googleEmail)
            val googleProfilePicURL = account?.photoUrl.toString()



            Log.i("Google Profile Pic URL", googleProfilePicURL)
            val googleIdToken = account?.idToken ?: ""
            Log.i("Google ID Token", googleIdToken)

            Toast.makeText(
                applicationContext,
                googleFirstName+" "+googleLastName+" "+googleEmail,
                Toast.LENGTH_SHORT
            ).show()

        } catch (e: ApiException) {
            // Sign in was unsuccessful

            Log.d(
                "failed code=","failed code= "+ e.statusCode.toString()
            )
            Log.d(
                "failed code=","failed code= "+ e.printStackTrace()

            )

        }
    }

    private fun signOut() {
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(this) {
                // Update your UI here
            }
    }


    private fun revokeAccess() {
        mGoogleSignInClient.revokeAccess()
            .addOnCompleteListener(this) {
                // Update your UI here
            }
    }
}