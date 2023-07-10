package mp.app.calonex.landlord.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_link_request_action.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.GetPropertyListApiCredential
import mp.app.calonex.common.apiCredentials.LinkPropertyActionCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.adapter.CustomSpinnerAdapter
import mp.app.calonex.landlord.model.NotificationLinkRequestModel
import mp.app.calonex.landlord.model.Property
import mp.app.calonex.landlord.response.LinkRequestActionResponse
import mp.app.calonex.landlord.response.PropertyListResponse

class LinkRequestActionActivity : CxBaseActivity2() {

    var mNotificationLinkRequest: NotificationLinkRequestModel? = null

    var spinnerExistProperty: Spinner? = null
    var propertyListWithoutBrokerAgent = ArrayList<Property>()
    var propertyExistId:String?=""
    private var btnLinkProperty:Button?=null
    private var btnNewProperty:Button?=null
    private var viewLine:View?=null
    private var listItemsTxt= ArrayList<String>()
    private var isLink:Boolean=false
    var notifyId:String?=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_link_request_action)

        mNotificationLinkRequest =intent.getSerializableExtra(getString(R.string.link_request_property)) as NotificationLinkRequestModel?
        spinnerExistProperty=findViewById(R.id.spinner_exist_property)
        btnLinkProperty=findViewById(R.id.btn_link_req)
        btnNewProperty=findViewById(R.id.btn_new_property)
        viewLine=findViewById(R.id.view_line)
        notifyId=intent.getStringExtra(getString(R.string.notification_id)).toString()
        spinnerExistProperty!!.visibility=View.GONE
        viewLine!!.visibility=View.GONE
        getPropertyByLandlordId()
        actionComponent()
        startHandler()

    }

    private fun getPropertyByLandlordId() {

        if (NetworkConnection.isNetworkConnected(this)) {
            pb_link!!.visibility = View.VISIBLE

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            var credentials = GetPropertyListApiCredential()

            credentials!!.userCatalogID = Kotpref.userId
            var propertyListService: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)
            var apiCall: Observable<PropertyListResponse> =
                propertyListService.getPropertyByLandlordId(credentials)

            RxAPICallHelper().call(apiCall, object : RxAPICallback<PropertyListResponse> {
                override fun onSuccess(propertyListResponse: PropertyListResponse) {
                    Log.e("onSuccess", propertyListResponse.responseCode.toString())
                    pb_link!!.visibility = View.GONE
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    if (!propertyListResponse.data?.isEmpty()!!) {
                        propertyListWithoutBrokerAgent = propertyListResponse.data!!
                        updateUi()
                    }
                }

                override fun onFailed(t: Throwable) {
                    // show error
                    updateUi()
                    pb_link!!.visibility = View.GONE
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    Log.e("onFailure", t.toString())
                    try {
                        Util.apiFailure(this@LinkRequestActionActivity, t)
                    } catch (e: Exception) {
                    }
                }
            })
        } else {
            Toast.makeText(this, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUi() {
        txt_link_add.setText(mNotificationLinkRequest?.propAddress+", "+mNotificationLinkRequest?.city+", "+mNotificationLinkRequest?.state+", "+mNotificationLinkRequest?.zipCode)
        txt_link_email.setText(mNotificationLinkRequest?.requestedBy)

        if(propertyListWithoutBrokerAgent.size>0) {
            txt_link_tag!!.text=getString(R.string.link_exist_tag)
            spinnerExistProperty!!.visibility=View.VISIBLE
            viewLine!!.visibility=View.VISIBLE
            listItemsTxt.add(getString(R.string.exist_property))
            for (item in propertyListWithoutBrokerAgent) {
                listItemsTxt.add(item.address + ", " + item.city + ", " + item.state + ", " + item.zipCode)
            }
            var spinnerAdapter = CustomSpinnerAdapter(applicationContext, listItemsTxt)
            //var spinnerAdapter= PropertyListWithoutBrokerCutomSpinnerAdapter(this, propertyListWithoutBrokerAgent)
            spinnerExistProperty!!.adapter = spinnerAdapter
        }else{
            spinnerExistProperty!!.visibility=View.GONE
            viewLine!!.visibility=View.GONE
        }
    }

    private fun actionComponent() {

        btnNewProperty!!.setOnClickListener {

            val intent = Intent(this, AddNewPropertyFirstScreen::class.java)
            intent.putExtra(getString(R.string.is_link_request), true)
            intent.putExtra(getString(R.string.notification_id), notifyId)
            intent.putExtra(getString(R.string.link_request_property), mNotificationLinkRequest)
            startActivity(intent)
            Util.navigationNext(this)
            finish()
        }

        spinnerExistProperty?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                if(position>0){
                    propertyExistId=propertyListWithoutBrokerAgent[position-1].propertyId.toString()
                    btnNewProperty!!.visibility=View.GONE
                    btnLinkProperty!!.visibility=View.VISIBLE
                }else{
                    spinnerExistProperty?.setSelection(0)
                    btnNewProperty!!.visibility=View.VISIBLE
                    btnLinkProperty!!.visibility=View.GONE
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // another interface callback
            }
        }

        btnLinkProperty!!.setOnClickListener {
            actionProperty()
        }

    }


    fun actionProperty() {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            pb_link.visibility = View.VISIBLE
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        var credential = LinkPropertyActionCredential()
        credential.action="APPROVED"
        credential.propertyId = propertyExistId
        credential.notificationId = notifyId
        credential.propertyLinkRequestId=mNotificationLinkRequest?.propertyLinkRequestId.toString()
        val propertyLinkAction: ApiInterface =
            ApiClient(this).provideService(ApiInterface::class.java)
        val apiCall: Observable<LinkRequestActionResponse> =
            propertyLinkAction.propertyAction(credential) //Test API Key

        RxAPICallHelper().call(apiCall, object : RxAPICallback<LinkRequestActionResponse> {
            override fun onSuccess(linkRequestResponse: LinkRequestActionResponse) {
                pb_link!!.visibility = View.GONE
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                //onBackPressed()
                val intent = Intent(this@LinkRequestActionActivity, HomeActivityCx::class.java)
                startActivity(intent)
                finish()
            }

            override fun onFailed(t: Throwable) {
                // show error
                Log.e("onFailure", t.toString())
                pb_link?.visibility = View.GONE
                isLink=false
                try{
                    Util.apiFailure(applicationContext,t)
                }catch (e:Exception){
                    Toast.makeText(applicationContext,getString(R.string.error_server), Toast.LENGTH_SHORT).show()
                }
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }

        })
        } else {
            Toast.makeText(applicationContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        Util.navigationBack(this@LinkRequestActionActivity)


    }

}
