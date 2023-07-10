package mp.app.calonex.rentcx

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import mp.app.calonex.R
import mp.app.calonex.agent.activity.HomeActivityAgent
import mp.app.calonex.broker.activity.HomeActivityBroker
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.landlord.activity.CxBaseActivity2
import mp.app.calonex.tenant.activity.HomeActivityTenant

class PaymentDoneActivity : CxBaseActivity2() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_done)

        val btnLogin: Button = findViewById(R.id.btn_login)

        startHandler()

        btnLogin.setOnClickListener {
            if (Kotpref.loginRole.equals("tenant")) {
                startActivity(Intent(this@PaymentDoneActivity, HomeActivityTenant::class.java))
                finish()
            } else if (Kotpref.loginRole.equals("broker")) {
                startActivity(Intent(this@PaymentDoneActivity, HomeActivityBroker::class.java))
                finish()
            } else if (Kotpref.loginRole.equals("agent")) {
                startActivity(Intent(this@PaymentDoneActivity, HomeActivityAgent::class.java))
                finish()
            } else{
                finish()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@PaymentDoneActivity, MarketplaceActivity::class.java))
        finish()
    }
}