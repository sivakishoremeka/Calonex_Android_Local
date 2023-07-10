package mp.app.calonex.registration.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_forget_password.*
import kotlinx.android.synthetic.main.activity_invite_landlord_screen.*
import kotlinx.android.synthetic.main.activity_invite_landlord_screen.btn_back
import kotlinx.android.synthetic.main.activity_user_confirm_detail_screen.*
import mp.app.calonex.R
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.UsPhoneNumberFormatter
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.LoginScreen
import mp.app.calonex.registration.response.UserRegistrationResponse
import retrofit2.HttpException
import java.lang.Exception

class InviteLandlordScreen : AppCompatActivity() {

    private var editInviteLdName:TextInputEditText?=null
    private var editInviteLdEmail:TextInputEditText?=null
    private var editInviteLdPhn: TextInputEditText?=null
    private var btnInviteLdNxt:TextView?=null
    private var btn_back:TextView?=null
    private var inviteLdName:String?=null
    private var inviteLdEmail:String?=null
    private var inviteLdPhn:String?=null
    private var txtSkipInvite:TextView?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite_landlord_screen)
        try {
            initComponent()
            actionComponent()
        }catch (e: Exception)
        {
            Log.e("Tenant Invite ",e.message.toString())
        }
    }

    fun initComponent(){
        editInviteLdName=findViewById(R.id.edit_invite_ld_name)
        editInviteLdEmail=findViewById(R.id.edit_invite_ld_email)
        editInviteLdPhn=findViewById(R.id.edit_invite_ld_phn)
        btnInviteLdNxt=findViewById(R.id.btn_invite_ld_nxt)
        btn_back=findViewById(R.id.btn_back)
        txtSkipInvite=findViewById(R.id.txt_skip_invite)
        val addLineNumberFormatter= UsPhoneNumberFormatter(editInviteLdPhn!!)
        editInviteLdPhn!!.addTextChangedListener(addLineNumberFormatter)
    }

    override fun onResume() {
        super.onResume()
        if(Kotpref.isRegisterConfirm){


        }
    }

    fun actionComponent(){
        btnInviteLdNxt!!.setOnClickListener {
            validComponent()

        }

        btn_back!!.setOnClickListener { view ->
            // Util.navigationNext(this,LoginScreen::class.java)
            finish()
        }

      /*  txtSkipInvite!!.setOnClickListener {
            Util.navigationNext(this, UserDocumentScreen::class.java)

        }*/
    }

    fun validComponent() {
        inviteLdName = editInviteLdName!!.text.toString().trim()
        inviteLdEmail = editInviteLdEmail!!.text.toString().trim()
        inviteLdPhn = editInviteLdPhn!!.text.toString().trim()

        if(Util.valueMandetory(applicationContext, inviteLdName, editInviteLdName!!)){
            return
        }
        if(Util.valueMandetory(applicationContext, inviteLdEmail, editInviteLdEmail!!)){
            return
        }

        if((!Util.isEmailValid(inviteLdEmail!!))){
            editInviteLdEmail?.error = getString(R.string.error_valid_email)
            editInviteLdEmail?.requestFocus()
            return
        }

        if(Util.valueMandetory(applicationContext, inviteLdPhn, editInviteLdPhn!!)){
            return
        }
        if(inviteLdPhn!!.length!=14){
            editInviteLdPhn?.error  = getString(R.string.error_phn)
            editInviteLdPhn?.requestFocus()
            return
        }

        LoginScreen.registrationPayload.landlordEmail=inviteLdEmail
        LoginScreen.registrationPayload.landlordName=inviteLdName
        LoginScreen.registrationPayload.landlordPhoneNum=inviteLdPhn!!.filter { it.isDigit() }

        registerApi()

    }

    private fun registerApi() {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            //Create retrofit Service
            pb_ut_confirm!!.visibility = View.VISIBLE
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

            val registerService: ApiInterface = ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<UserRegistrationResponse> = registerService.registerUser(
                LoginScreen.registrationPayload
            )

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
                Log.e("onSuccess", it.responseDto?.responseDescription.toString())



                if(it!!.responseDto!!.responseCode.equals(200)) {
                    Util.alertOkIntentMessage(
                        this@InviteLandlordScreen,
                        getString(R.string.alert),
                        getString(R.string.tag_invite_landlord),
                        LoginScreen::class.java
                    )


                }else{
                    pb_ut_confirm!!.visibility = View.GONE
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    Toast.makeText(applicationContext,it!!.responseDto!!.responseDescription, Toast.LENGTH_SHORT).show()
                }



            },
                { e ->
                    Log.e("onFailure", e.toString())
                    pb_ut_confirm!!.visibility = View.GONE
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    val exception: HttpException = e as HttpException
                    if(exception.code().equals(400) ){
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.error_email_exist),
                            Toast.LENGTH_SHORT
                        ).show()

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

        Util.navigationBack(this)
    }
}