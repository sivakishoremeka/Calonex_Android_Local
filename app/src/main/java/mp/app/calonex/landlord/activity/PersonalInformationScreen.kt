package mp.app.calonex.landlord.activity

import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.UpdateUserDetailCredentials
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.UsPhoneNumberFormatter
import mp.app.calonex.common.utility.Util
import mp.app.calonex.common.utility.Util.navigationBack
import mp.app.calonex.landlord.fragment.ProfileLdFragment.Companion.listUserImages
import mp.app.calonex.landlord.fragment.ProfileLdFragment.Companion.userDetailResponse
import mp.app.calonex.landlord.response.UpdateUserResponse

class PersonalInformationScreen : CxBaseActivity2() {

    private var txtUserName: TextView? = null
    private var txtUserEmail: TextView? = null
    private var txtUserPhnNo: TextView? = null
    private var ivUpdatePhn: ImageView? = null
    private var backButton: ImageView? = null
    private var pbUserDetail: ProgressBar? = null
    private var imgUserPic: ImageView? = null
    private var txtUserType: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_information_screen)

        initComponent()
        actionComponent()
        startHandler()
    }

    private fun initComponent() {
        txtUserName = findViewById(R.id.txt_user_name)
        txtUserEmail = findViewById(R.id.txt_user_email)
        txtUserPhnNo = findViewById(R.id.txt_user_phn_no)
        ivUpdatePhn = findViewById(R.id.iv_update_phn)
        pbUserDetail = findViewById(R.id.pb_user_detail)
        backButton = findViewById(R.id.header_back)
        imgUserPic = findViewById(R.id.img_user_pic)
        txtUserType = findViewById(R.id.txt_user_type)


        txtUserName!!.text = "${userDetailResponse.firstName} ${userDetailResponse.lastName}"
        txtUserEmail!!.text = userDetailResponse.emailId
        txtUserType?.text = userDetailResponse.userRoleName


        if (!userDetailResponse.phoneNumber.isNullOrEmpty()) {
            txtUserPhnNo!!.text =
                PhoneNumberUtils.formatNumber(userDetailResponse.phoneNumber, "US")
        }


        var profileImg: String = ""
        val customPb: CircularProgressDrawable = Util.customProgress(this)
        for (item in listUserImages) {

            if (item.uploadFileType.equals(getString(R.string.key_profile_pic))) {
                profileImg = item.fileName
                break
            }
        }

        if (!profileImg.isNullOrEmpty()) {
            Glide.with(this)
                .load(getString(R.string.base_url_img2) + profileImg)
                .placeholder(customPb)
                .into(imgUserPic!!)
        }
    }

    private fun actionComponent() {

        ivUpdatePhn!!.setOnClickListener {
            updatePhnDialog()
        }

        backButton!!.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navigationBack(this)
    }

    private fun updatePhnDialog() {
        //Inflate the dialog with custom view
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.layout_update_mobile, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setTitle("Update Phone Number")
        var editPhn: TextInputEditText = mDialogView.findViewById(R.id.edit_user_phn_no)
        val addLineNumberFormatter = UsPhoneNumberFormatter(editPhn)
        editPhn.addTextChangedListener(addLineNumberFormatter)
        editPhn.setText(txtUserPhnNo!!.text.toString())

        // button click of custom layout

        mBuilder.setPositiveButton(getString(R.string.update)) { dialog, which ->
            dialog.dismiss()
            //get text from EditTexts of custom layout
            val updatePhn = editPhn.text.toString()
            if (updatePhn.length != 14) {
                editPhn.error = getString(R.string.error_phn)
                editPhn.requestFocus()
                return@setPositiveButton
            }
            modifyDetail(updatePhn.filter { it.isDigit() })
        }

        mBuilder.setNegativeButton(getString(R.string.cancel)) { dialog, which ->
            dialog.dismiss()
        }
        mBuilder.show()
    }

    private fun modifyDetail(valPhn: String?) {
        if (NetworkConnection.isNetworkConnected(this)) {
            pbUserDetail!!.visibility = View.VISIBLE
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            val credentials = UpdateUserDetailCredentials()
            credentials.userId = Kotpref.userId
            credentials.emailId = userDetailResponse.emailId
            credentials.phoneNumber = valPhn
            val signatureApi: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<UpdateUserResponse> =
                signatureApi.updateUserDetail(credentials)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", it.responseDescription.toString())

                    pbUserDetail!!.visibility = View.GONE
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                    if (it!!.responseCode.equals(0)) {

                        try {
                            //getUserInfo()
                            txtUserPhnNo!!.text = valPhn
                        } catch (e: Exception) {
                            Log.e("Personal Information", e.message.toString())
                        }
                    } else {
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    }
                },
                    { e ->
                        Log.e("onFailure", e.toString())
                        pbUserDetail!!.visibility = View.GONE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        e.message?.let {
                            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                        }
                    })
        } else {
            Toast.makeText(this, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
        }
    }
}