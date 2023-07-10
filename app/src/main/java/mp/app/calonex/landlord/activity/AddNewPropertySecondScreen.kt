package mp.app.calonex.landlord.activity


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_new_property_second_screen.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.BrokerAgentCredentials
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.AddNewPropertyFirstScreen.Companion.isEditing
import mp.app.calonex.landlord.activity.AddNewPropertyFirstScreen.Companion.result
import mp.app.calonex.landlord.adapter.PropertyListAdapter.Companion.propertyDetailResponse
import mp.app.calonex.landlord.response.BrokerAgentDetailResponse

class AddNewPropertySecondScreen : CxBaseActivity2() {

    var propertyId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_property_second_screen)
        supportActionBar?.hide()
        if (isEditing) {
            updateUi()
        }
        actionComponent()
        startHandler()
    }

    private fun updateUi() {
        edit_primary_contact.setText(propertyDetailResponse.primaryContact)
        edit_broker_name.setText(propertyDetailResponse.broker)
        edit_broker_phone.setText(PhoneNumberUtils.formatNumber(propertyDetailResponse.brokerPhone,"US"))
        edit_broker_email.setText(propertyDetailResponse.brokerEmailID)
        edit_ba_add.setText(propertyDetailResponse.brokerOrAgentLiscenceID)
    }

    private fun updateValues() {
        result.brokerOrAgentLiscenceID = edit_ba_add?.text.toString().trim()
        result.primaryContact = edit_primary_contact?.text.toString().trim()
        result.broker = edit_broker_name?.text.toString().trim()
        result.brokerEmailID = edit_broker_email?.text.toString().trim()
        result.brokerPhone = edit_broker_phone?.text.toString().trim()
        result.linkedByBrokerAgent = true
    }

    private fun actionComponent() {
        edit_ba_add?.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_SEARCH ||
                i == EditorInfo.IME_ACTION_DONE ||
                keyEvent != null &&
                keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER
            ) {
                if (keyEvent == null || !keyEvent.isShiftPressed() || keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    // the user is done typing.
                    val imm =
                        this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                    var credential = BrokerAgentCredentials()
                    credential.licenceId = edit_ba_add?.text.toString().trim()

                    if (credential.licenceId.equals("")) {
                        Toast.makeText(
                            applicationContext,
                            "Please enter Broker or Agent ID",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@setOnEditorActionListener false
                    }
                    progressBar_broker.visibility = View.VISIBLE
                    val brokerAgentDetails: ApiInterface =
                        ApiClient(this).provideService(ApiInterface::class.java)
                    val apiCall: Observable<BrokerAgentDetailResponse> =
                        brokerAgentDetails.getBrokerAgentList(credential) //Test API Key

                    apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            Log.e("onSuccess", it.statusCode.toString())

                            progressBar_broker.visibility = View.GONE

                            if (it.data != null && it.responseDto!!.responseDescription.contains(
                                    "success"
                                )
                            ) {

                                edit_primary_contact.setText(it.data!!.primaryContact)
                                edit_broker_name.setText(it.data!!.brokerName)
                                edit_broker_phone.setText(it.data!!.brokerPhone)
                                edit_broker_email.setText(it.data!!.brokerEmailID)

                            } else if (it.responseDto != null && it.responseDto!!.responseCode == 404) {
                                Toast.makeText(
                                    applicationContext,
                                    "Invalid Broker/Agent IDD",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }


                        },
                            { e ->
                                Log.e("onFailure", e.toString())
                                progressBar_broker.visibility = View.GONE
                                e.message?.let {
                                    Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT)
                                        .show()

                                }
                            })
                    return@setOnEditorActionListener true // consume.
                }
            }



            false
        }



        btn_next!!.setOnClickListener {
            updateValues()
            val intent = Intent(this, AddNewPropertyThirdScreen::class.java)
            startActivity(intent)
            Util.navigationNext(this)

        }

        broker_skip!!.setOnClickListener {
            updateValues()
            val intent = Intent(this, AddNewPropertyThirdScreen::class.java)
            startActivity(intent)
            Util.navigationNext(this)
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        Util.navigationBack(this@AddNewPropertySecondScreen)
    }

}
