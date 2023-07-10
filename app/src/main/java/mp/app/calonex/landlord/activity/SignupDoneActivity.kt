package mp.app.calonex.landlord.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import mp.app.calonex.R
import mp.app.calonex.common.utility.Kotpref


class SignupDoneActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_done)
        val btnLogin: Button = findViewById(R.id.btn_login)
        var textView: TextView=findViewById(R.id.textView);


      /*  if(Kotpref.isLogin && Kotpref.loginRole.equals("agent",true)||Kotpref.loginRole.equals("broker",true))
        {
            btnLogin!!.setText("Back To Home")
        }*/



        if(Kotpref.loginRole.equals("broker",true))
        {
            textView.setText("We have sent you a verification email to "+  intent.getStringExtra("email")+ " Please check your email and verify the same before you can login")

        }
        else if(Kotpref.loginRole.equals("landlord",true)) {
            textView.setText("We have sent you a verification email to "+ intent.getStringExtra("email")+ " Please check your email and verify the same before you can login")

        }

        else if(Kotpref.loginRole.equals("agent",true)) {
            textView.setText("We have sent you a verification email to "+  intent.getStringExtra("email")+ " Please check your email and verify the same before you can login")
        }
        else if( Kotpref.loginRole.equals("tenant",true)) {
            textView.setText("Thank you for registering with Calonex")

        }



        btnLogin.setOnClickListener {


                val intent = Intent(this, LoginScreen::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@SignupDoneActivity, LoginScreen::class.java))
        finish()
    }
}