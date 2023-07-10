package mp.app.calonex.tenant.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_tenant_submit_mobile.*
import mp.app.calonex.R
import mp.app.calonex.landlord.activity.HomeActivityCx
import java.util.concurrent.TimeUnit

class TenantPhoneAuthActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null
    var mobileNumber: String = ""
    var verificationID: String = ""
    var token_: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tenant_submit_mobile)
        mAuth = FirebaseAuth.getInstance()

        txt_next.setOnClickListener {
            mobileNumber = etNumber.text.toString()

            if (mobileNumber.length > 0) {
                progressBar.visibility = View.VISIBLE
                loginTask()
            } else {
                etNumber.setError("Enter valid phone number")
            }
        }

        txt_bck.setOnClickListener{
            val intent = Intent(this, TenantListActivity::class.java)
            startActivity(intent)
        }

    }

    private fun loginTask() {

        var mCallBacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                if (credential != null) {
                    signInWithPhoneAuthCredential(credential)
                }
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                progressBar.visibility = View.GONE
                Toast.makeText(applicationContext,"Invalid phone number or verification failed.", Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(verificationId, token)
                progressBar.visibility = View.GONE
                verificationID = verificationId.toString()
                token_ = token.toString()

                etNumber.setText("")

                etNumber.setHint("Enter OTP ")
                btnSignIn.setText("Verify OTP")

                btnSignIn.setOnClickListener {
                    progressBar.visibility = View.VISIBLE
                    verifyAuthentication(verificationID, etNumber.text.toString())
                }

                Log.e("Login : verificationId ", verificationId)
                Log.e("Login : token ", token_)

            }

            override fun onCodeAutoRetrievalTimeOut(verificationId: String) {
                super.onCodeAutoRetrievalTimeOut(verificationId)
                progressBar.visibility = View.GONE
                // toast("Time out")
            }
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            mobileNumber,            // Phone number to verify
            60,                  // Timeout duration
            TimeUnit.SECONDS,        // Unit of timeout
            this,                // Activity (for callback binding)
            mCallBacks);

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {

        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener(this, object : OnCompleteListener<AuthResult> {
                override fun onComplete(task: Task<AuthResult>) {
                    if (task.isSuccessful()) {
                        val user = task.getResult()?.user
                        progressBar.visibility = View.GONE
                        startActivity(
                            Intent(
                                this@TenantPhoneAuthActivity,
                                HomeActivityCx::class.java
                            )
                        )

                    } else {
                        if (task.getException() is FirebaseAuthInvalidCredentialsException) {
                            progressBar.visibility = View.GONE
                            Toast.makeText(applicationContext,"Invalid OPT", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
    }

    private fun verifyAuthentication(verificationID: String, otpText: String) {

        val phoneAuthCredential =
            PhoneAuthProvider.getCredential(verificationID, otpText) as PhoneAuthCredential
        signInWithPhoneAuthCredential(phoneAuthCredential)
    }
}
