package mp.app.calonex.LdRegBankAdd

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_select_role_screen.*
import kotlinx.android.synthetic.main.activity_signature_on_screen.*
import kotlinx.android.synthetic.main.activity_user_confirm_detail_screen.*
import mp.app.calonex.R
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.LoginScreen.Companion.bankAddModelLL
import mp.app.calonex.registration.response.*
import retrofit2.HttpException

class UserConfirmDetailScreenBankAdd : AppCompatActivity() {
    private var layoutContactInfo:LinearLayout?=null
    private var layoutPropertyInfo:LinearLayout?=null
    private var layoutDocs:LinearLayout?=null
    private var layoutAccountInfo:LinearLayout?=null
    private var btnConfirmSign:Button?=null
    private var isBtnCnfrm:Boolean?=false
    private var txtPlan:TextView?=null
    private var count:Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_confirm_detail_screen_bank_add)

        initComponent()
        actionComponent()
    }

    fun initComponent(){
        txtPlan=findViewById(R.id.txt_plan)
        txtPlan!!.text= Kotpref.planSelected
        layoutContactInfo=findViewById(R.id.layout_contact_info)
        layoutPropertyInfo=findViewById(R.id.layout_property_info)
        layoutDocs=findViewById(R.id.layout_document)
        layoutAccountInfo=findViewById(R.id.layout_acc_info)
        btnConfirmSign=findViewById(R.id.btn_cnfirm_sign)
    }

    fun actionComponent(){

        layoutAccountInfo!!.setOnClickListener {
            Kotpref.isRegisterConfirm=true
            Util.navigationNext(this,UserAccountScreenBankAdd::class.java)
        }


        btnConfirmSign!!.setOnClickListener {
            isBtnCnfrm=true
            createPlanSubcription()
        }
    }

    @SuppressLint("CheckResult")
    private fun createPlanSubcription() {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            //Create retrofit Service
            pb_reg_confirm!!.visibility = View.VISIBLE
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

            //Log.e("Final_I_LOG_1", Gson().toJson(subscriptionPayload))
            val signatureApi: ApiInterface = ApiClient(this).provideService(ApiInterface::class.java)
            Log.d("TAG", "createPlanSubcriptionapi: "+bankAddModelLL)
            val apiCall: Observable<AddBankModelResponse> =
                signatureApi.createSubscriptionBankAdd(bankAddModelLL)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
                //Log.e("Final_O_LOG_1", Gson().toJson(it.responseDto))
                Log.e("onSuccess", it.responseDto?.responseDescription.toString())

                if(it!!.responseDto!!.responseCode!!.equals(201)) {

                    if (it.data != null) {
                        //registrationPayload3.subscriptionDetailsId = it.data!!.subscriptionDetailsId
                        //registrationPayload3.stripeCustomerId = it.data!!.stripeCustomerId

                       // Util.navigationNext(this, VerifyAccountDetailsActivityAddBank::class.java)
                    }
                }else{
                    pb_reg_confirm!!.visibility = View.GONE
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    Toast.makeText(applicationContext,it!!.responseDto!!.responseDescription, Toast.LENGTH_SHORT).show()
                }
            },
                { e ->
                    Log.e("onFailure", e.toString())
                    pb_reg_confirm!!.visibility = View.GONE
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    val exception: HttpException = e as HttpException
                    if(exception.code().equals(409)){
                        Toast.makeText(applicationContext, getString(R.string.error_bank_detail), Toast.LENGTH_SHORT).show()
                    }else{
                        e.message?.let {
                            Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                        }
                    }
                })
        } else {
            Toast.makeText(applicationContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(!isBtnCnfrm!!){
            Util.navigationBack(this)
        }
    }

}