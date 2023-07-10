package mp.app.calonex.broker.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_get_agent_commision_broker.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.LinkPropertyActionCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.landlord.activity.CxBaseActivity2
import mp.app.calonex.landlord.response.LinkRequestActionResponseNew

class GetAgentCommisionActivityBroker : CxBaseActivity2() {
    var notificationId: String = ""
    var agentNo: String = ""
    var agentName: String = ""
    var agentMail: String = ""

    var amount: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_agent_commision_broker)

        notificationId = intent.getStringExtra("NOTIFICATION ID").toString()
        agentNo = intent.getStringExtra("AGENT LICENSE").toString()
        agentName = intent.getStringExtra("AGENT NAME").toString()
        agentMail = intent.getStringExtra("AGENT EMAIL").toString()

        startHandler()

        Log.e("aaa", agentMail)

        txt_commission_description.text = "Please set the commission percentage for the Agent $agentName ($agentMail) with license number $agentNo to complete the on-boarding process."

        payment_info_action_done.setOnClickListener {
            if (!TextUtils.isEmpty(txt_comission_percnent.text.toString())) {
                amount = txt_comission_percnent.text.toString()
                actionPropertyReject()
            } else {
                Toast.makeText(this@GetAgentCommisionActivityBroker, "Invalid percentage value.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun actionPropertyReject() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        pb_link!!.visibility = View.VISIBLE

        var credential = LinkPropertyActionCredential()
        credential.notificationId = notificationId
        credential.commissionPercentage = amount
        credential.agentLicenseNo = agentNo
        credential.action = "APPROVED"

        val propertyLinkAction: ApiInterface = ApiClient(this).provideService(ApiInterface::class.java)
        val apiCall: Observable<LinkRequestActionResponseNew> = propertyLinkAction.propertyActionAgentFromBroker(credential) //Test API Key
        RxAPICallHelper().call(apiCall, object : RxAPICallback<LinkRequestActionResponseNew> {
            override fun onSuccess(propertyDetail: LinkRequestActionResponseNew) {

                val intent = Intent(this@GetAgentCommisionActivityBroker, HomeActivityBroker::class.java)
                startActivity(intent)
                finish()
            }

            override fun onFailed(t: Throwable) {
                // show error
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                pb_link!!.visibility = View.GONE
                Log.e("onFailure", t.toString())
                Toast.makeText(this@GetAgentCommisionActivityBroker, "Unable to set the commission. Error: " + t.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }
}