package mp.app.calonex.landlord.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import mp.app.calonex.R
import mp.app.calonex.agent.activity.HomeActivityAgent
import mp.app.calonex.broker.activity.HomeActivityBroker
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.tenant.activity.HomeActivityTenant

class SignupDoneActivityNew : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_done_new)

        val btnLogin: Button = findViewById(R.id.btn_login)
        var textView: TextView = findViewById(R.id.textView);

        textView.setText("We have sent you a verification email to " + intent.getStringExtra("email") + " Please check your email and verify the same before you can login")

        btnLogin.setOnClickListener {
            if(Kotpref.loginRole.equals("tenant",true)) {
                startActivity(Intent(this@SignupDoneActivityNew, HomeActivityTenant::class.java)).apply {
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            } else if(Kotpref.loginRole.equals("broker",true)) {
                startActivity(Intent(this@SignupDoneActivityNew, HomeActivityBroker::class.java)).apply {
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            } else {
                startActivity(Intent(this@SignupDoneActivityNew, HomeActivityAgent::class.java)).apply {
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            }
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(Kotpref.loginRole.equals("tenant",true)) {
            startActivity(Intent(this@SignupDoneActivityNew, HomeActivityTenant::class.java))
        } else if(Kotpref.loginRole.equals("broker",true)) {
            startActivity(Intent(this@SignupDoneActivityNew, HomeActivityBroker::class.java))
        } else {
            startActivity(Intent(this@SignupDoneActivityNew, HomeActivityAgent::class.java))
        }
        finish()
    }
}