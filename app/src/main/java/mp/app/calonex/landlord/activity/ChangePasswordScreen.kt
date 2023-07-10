package mp.app.calonex.landlord.activity

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputEditText
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_change_password_screen.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.ChngPasswordCredentials
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.RSA
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.response.ChngePasswordResponse
import java.util.regex.Matcher
import java.util.regex.Pattern

class ChangePasswordScreen : CxBaseActivity2() {

    private var editCurrentPassword: TextInputEditText? = null
    private var editNewPassword: TextInputEditText? = null
    private var editNewConfirmPassword: TextInputEditText? = null
    private var btnPaswrdSubmit: Button? = null
    private var currentPswrd: String? = null
    private var newPswrd: String? = null
    private var newCnfrmPswrd: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password_screen)
        initComponent()
        actionComponent()
        startHandler()
    }

    fun initComponent() {
        editCurrentPassword = findViewById(R.id.edit_current_password)
        editNewPassword = findViewById(R.id.edit_new_password)
        editNewConfirmPassword = findViewById(R.id.edit_new_cnfrm_password)
        btnPaswrdSubmit = findViewById(R.id.btn_paswrd_submit)

    }

    fun actionComponent() {
        editCurrentPassword!!.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
                layout_current_paswrd?.isPasswordVisibilityToggleEnabled = true
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {

            }
        })

        editNewPassword!!.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
                layout_new_pswrd?.isPasswordVisibilityToggleEnabled = true
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {

            }
        })

        editNewConfirmPassword!!.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
                layout_new_pswrd?.isPasswordVisibilityToggleEnabled = true
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {

            }
        })


        header_back.setOnClickListener {
            super.onBackPressed()
            Util.navigationBack(this)
        }

        btnPaswrdSubmit!!.setOnClickListener { view ->
            // Hide the keyboard
            val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            validComponent()

        }

    }

    fun validComponent() {
        currentPswrd = editCurrentPassword!!.text.toString().trim()
        newPswrd = editNewPassword!!.text.toString().trim()
        newCnfrmPswrd = editNewConfirmPassword!!.text.toString().trim()

        // Check the password string is empty or not+

        if (currentPswrd!!.isNullOrEmpty()) {
            layout_current_paswrd?.isPasswordVisibilityToggleEnabled = false
            editCurrentPassword?.error = getString(R.string.error_pwd)
            editCurrentPassword?.requestFocus()
            return
        }

        if (!currentPswrd!!.equals(Kotpref.password)) {
            layout_current_paswrd?.isPasswordVisibilityToggleEnabled = false
            editCurrentPassword?.error = getString(R.string.error_valid_pwd)
            editCurrentPassword?.requestFocus()
            return
        }

        if (newPswrd!!.isNullOrEmpty()) {
            layout_new_pswrd?.isPasswordVisibilityToggleEnabled = false
            editNewPassword?.error = getString(R.string.error_pwd)
            editNewPassword?.requestFocus()
            return
        }

        if (newPswrd!!.length < 8) {
            layout_new_pswrd?.isPasswordVisibilityToggleEnabled = false
            editNewPassword?.error = getString(R.string.error_pwd_length)
            editNewPassword?.requestFocus()
            return
        }
        var pattern: Pattern = Pattern.compile("[^a-zA-Z0-9]")
        var matcher: Matcher = pattern.matcher(newPswrd)
        var isStringContainsSpecialCharacter: Boolean = matcher.find()
        if (!isStringContainsSpecialCharacter) {
            editNewPassword?.error = getString(R.string.error_pwd_character)
            editNewPassword?.requestFocus()
            return
        }
        if (newCnfrmPswrd!!.isNullOrEmpty()) {
            layout_new_cnfrm_pswrd?.isPasswordVisibilityToggleEnabled = false
            editNewConfirmPassword?.error = getString(R.string.error_pwd)
            editNewConfirmPassword?.requestFocus()
            return
        }

        // Check the password string is empty or not
        if (!newCnfrmPswrd!!.equals(newPswrd)) {
            layout_new_cnfrm_pswrd?.isPasswordVisibilityToggleEnabled = false
            editNewConfirmPassword?.error = getString(R.string.error_confirm_paswrd)
            editNewConfirmPassword?.requestFocus()
            return
        }

        chngPswrdApi()

    }

    fun chngPswrdApi() {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            pb_chn_pswrd!!.visibility = View.VISIBLE
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            var credential = ChngPasswordCredentials()
            credential.newPassword = RSA.encrypt(newCnfrmPswrd!!)
            credential.userCatalogId = Kotpref.userId

            var pswrdService: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)
            var apiCall: Observable<ChngePasswordResponse> =
                pswrdService.chngPassword(credential) //Test API Key

            RxAPICallHelper().call(apiCall, object : RxAPICallback<ChngePasswordResponse> {
                override fun onSuccess(response: ChngePasswordResponse) {
                    pb_chn_pswrd!!.visibility = View.GONE
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    if (response!!.responseCode!!.equals(200)) {
                        editCurrentPassword!!.setText("")
                        editNewPassword!!.setText("")
                        editNewConfirmPassword!!.setText("")
                        Kotpref.password = newCnfrmPswrd!!

                        val builder = AlertDialog.Builder(this@ChangePasswordScreen)
                        builder.setTitle("Success")
                        // Display a message on alert dialog
                        builder.setMessage("Password changed successfully")

                        // Set a positive button and its click listener on alert dialog
                        builder.setPositiveButton("Close") { dialog, which ->
                            dialog.dismiss()
                            onBackPressed()
                        }

                        val dialog: AlertDialog = builder.create()
                        dialog.show()

                    } else {
                        Toast.makeText(
                            applicationContext,
                            response!!.responseDescription!!,
                            Toast.LENGTH_SHORT
                        ).show()
                    }


                }

                override fun onFailed(t: Throwable) {
                    pb_chn_pswrd!!.visibility = View.GONE

                    try {

                        Toast.makeText(
                            applicationContext,
                            getString(R.string.error_something),
                            Toast.LENGTH_SHORT
                        ).show()


                    } catch (e: Exception) {
                        Toast.makeText(applicationContext, e.message.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }

                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                    Log.e(getString(R.string.on_fail), t.toString())

                }
            })

        } else {
            Toast.makeText(
                applicationContext,
                getString(R.string.error_network),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Util.navigationBack(this)
    }
}