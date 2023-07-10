package mp.app.calonex.landlord.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_dynamic.*
import mp.app.calonex.R


class TestDeepLink : AppCompatActivity() {

    private var til_routin: TextInputLayout? = null
    private var iv_google: ImageView? = null
    private var iv_fb: CircleImageView? = null
    private var civ_twitter: CircleImageView? = null
    private var et_role: AutoCompleteTextView? = null
    private var civ_apple: CircleImageView? = null

    private var btn_signup: AppCompatButton? = null
    private var role: String? = null
    private lateinit var edit_ut_licence: TextInputEditText
    private var til_ln: TextInputLayout? = null
    private var txt_signin: TextView? = null


/* CX-Landlord or CX-Tenant or CX-Broker or CX-Agent.*/


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_social_login)

        et_role = findViewById(R.id.et_role);
        iv_google = findViewById(R.id.iv_google);
        iv_fb = findViewById(R.id.iv_fb)
        civ_apple = findViewById(R.id.civ_apple);
        //civ_twitter = findViewById(R.id.civ_twitter);
        civ_apple = findViewById(R.id.civ_apple);
        btn_signup = findViewById(R.id.btn_signup)
        edit_ut_licence = findViewById(R.id.edit_ut_licence)
        til_ln = findViewById(R.id.til_ln);
        txt_signin = findViewById(R.id.txt_signin);

        btn_signup!!.setOnClickListener {
            if (checkValidation()) {
                val intent = Intent(this@TestDeepLink, SignupByEmailActivity::class.java)
                intent.putExtra("ROLE", et_role!!.text.toString().trim())
                if (et_role!!.text.toString().trim().equals("Broker") || et_role!!.text.toString().trim().equals("Agent")) {
                    if (edit_ut_licence.text?.length == 11) {
                        intent.putExtra("LICENSE", edit_ut_licence.text.toString())
                        startActivity(intent)
                    } else {
                        edit_ut_licence.error = "Invalid License number."
                        edit_ut_licence.requestFocus()
                    }
                } else {
                    intent.putExtra("LICENSE", "")
                    startActivity(intent)
                }
            }
        }

        et_role!!.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.e("asdf", et_role!!.text.trim().toString())
                if (et_role!!.text.toString().trim().equals("Broker") || et_role!!.text.toString().trim().equals("Agent")) {
                    til_ln!!.visibility = View.VISIBLE
                } else {
                    til_ln!!.visibility = View.GONE
                }
            }
        })



        txt_signin!!.setOnClickListener {
            onBackPressed()
        }

        /*civ_twitter!!.setOnClickListener {

            if (et_role?.text.toString().isNullOrEmpty()) {
                et_role?.setError("Mandatory Field.")
            } else {
                if (et_role?.text.toString().equals("Landlord"))
                    role = "CX-Landlord"
                else if (et_role?.text.toString().equals("Broker"))
                    role = "CX-Broker"
                else if (et_role?.text.toString().equals("Agent"))
                    role = "CX-Agent"
                else if (et_role?.text.toString().equals("Tenant"))
                    role = "CX-Tenant"

                signUp(role, "twitter", "register")
            }
        }*/

        iv_fb!!.setOnClickListener {
            if (et_role?.text.toString().isNullOrEmpty()) {
                et_role?.setError("Mandatory Field.")
            } else if ((et_role!!.text.toString().trim().equals("Broker") || et_role!!.text.toString().trim().equals("Agent")) && edit_ut_licence.text?.length != 11) {
                edit_ut_licence.error = "Invalid Driving License number."
                edit_ut_licence.requestFocus()
            } else {
                if (et_role?.text.toString().equals("Landlord"))
                    role = "CX-Landlord"
                else if (et_role?.text.toString().equals("Broker"))
                    role = "CX-Broker"
                else if (et_role?.text.toString().equals("Agent"))
                    role = "CX-Agent"
                else if (et_role?.text.toString().equals("Tenant"))
                    role = "CX-Tenant"

                signUp(role, "facebook", "register")
            }
        }
        civ_apple!!.setOnClickListener {
            var role: String = "";
            if (et_role?.text.toString().isNullOrEmpty()) {
                et_role?.setError("Mandatory Field.")
            } else if ((et_role!!.text.toString().trim().equals("Broker") || et_role!!.text.toString().trim().equals("Agent")) && edit_ut_licence.text?.length != 11) {
                edit_ut_licence.error = "Invalid Driving License number."
                edit_ut_licence.requestFocus()
            } else {
                if (et_role?.text.toString().equals("Landlord"))
                    role = "CX-Landlord"
                else if (et_role?.text.toString().equals("Broker"))
                    role = "CX-Broker"
                else if (et_role?.text.toString().equals("Agent"))
                    role = "CX-Agent"
                else if (et_role?.text.toString().equals("Tenant"))
                    role = "CX-Tenant"

                signUp(role, "apple", "register")
            }
        }

        iv_google!!.setOnClickListener {

            if (et_role?.text.toString().isNullOrEmpty()) {
                et_role?.setError("Mandatory Field.")
                et_role?.requestFocus()
            } else if ((et_role!!.text.toString().trim().equals("Broker") || et_role!!.text.toString().trim().equals("Agent")) && edit_ut_licence.text?.length != 11) {
                edit_ut_licence.error = "Invalid Driving License number."
                edit_ut_licence.requestFocus()
            } else {
                if (et_role?.text.toString().equals("Landlord"))
                    role = "CX-Landlord"
                else if (et_role?.text.toString().equals("Broker"))
                    role = "CX-Broker"
                else if (et_role?.text.toString().equals("Agent"))
                    role = "CX-Agent"
                else if (et_role?.text.toString().equals("Tenant"))
                    role = "CX-Tenant"

                signUp(role, "google", "register")
            }
        }

        val adapterPlusMinus = ArrayAdapter(
            this@TestDeepLink,
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(R.array.role)
        )
        adapterPlusMinus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        et_role!!.setAdapter<ArrayAdapter<String>>(adapterPlusMinus)

        try {
            Log.d("account detai", "Plan Detail account information")
        } catch (e: Exception) {
            Log.e("Plan Detail", e.message.toString())
        }
    }

    private fun signUp(role: String?, aacount: String?, authtype: String) {
        if ((et_role!!.text.toString().trim().equals("Broker") || et_role!!.text.toString().trim().equals("Agent"))) {
            var ln: String = edit_ut_licence!!.text.toString()
            val uri =
                Uri.parse("https://api.calonex.com/social-service/oauth2/authorization/$aacount?license=$ln&authtype=$authtype&redirect_uri=https://app.calonex.com/social-login&role=$role")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            Log.d("google sign up", "signUp: $uri")
            startActivity(intent)
        } else {
            val uri =
                Uri.parse("https://api.calonex.com/social-service/oauth2/authorization/$aacount?authtype=$authtype&redirect_uri=https://app.calonex.com/social-login&role=$role")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            Log.d("google sign up", "signUp: $uri")
            startActivity(intent)
            // Log.d("google sign up", "signUp: uri")
        }
    }

    private fun checkValidation(): Boolean {
        var flag = true
        if (et_role?.text.toString().isNullOrEmpty()) {
            et_role!!.setError("Mandatory Field.")
            et_role?.requestFocus()
            flag = false

        } else if (edit_tenant_last_name?.text.toString().isNullOrEmpty()) {
            edit_tenant_last_name?.setError("Mandatory Field.")
            edit_tenant_last_name?.requestFocus()
            flag = false
        }

        else if ((et_role!!.text.equals("Broker") || et_role!!.text.equals("Agent")) && edit_ut_licence.text?.length != 11) {
            edit_ut_licence.error = "Invalid Driving License number."
            edit_ut_licence.requestFocus()
            flag = false
        }

        return flag
    }
}