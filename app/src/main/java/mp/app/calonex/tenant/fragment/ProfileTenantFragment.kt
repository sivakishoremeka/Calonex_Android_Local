package mp.app.calonex.tenant.fragment


import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import io.reactivex.Observable
import kotlinx.android.synthetic.main.layout_update_mobile.view.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.UserDetailCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.BiometricUtil
import mp.app.calonex.common.utility.CircleTransform
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.ChangePasswordScreen
import mp.app.calonex.landlord.activity.DocumentDetailScreen
import mp.app.calonex.landlord.activity.LoginScreen
import mp.app.calonex.landlord.activity.PersonalInformationScreen
import mp.app.calonex.landlord.model.UserDetail
import mp.app.calonex.landlord.response.UserDetailResponse
import java.io.IOException


class ProfileTenantFragment : Fragment() {
    private val TAG: String = "ProfileLdFragment"
    lateinit var appContext: Context
    private var txtUserName: TextView? = null
    private var txtLogout: Button? = null
    private var txtUserType: TextView? = null
    private var txtUserEmail: TextView? = null
    private var txtUserPhnNo: TextView? = null
    private var imgUserPic: ImageView? = null
    private var ibProfilePic: ImageButton? = null
    private var ivUpdatePhn: ImageView? = null
    private var layoutPersonalInfo: LinearLayout? = null
    private var layoutUserDocs: LinearLayout? = null
    private var layout_acc_detail: LinearLayout? = null
    private var layoutChngPaswrd: LinearLayout? = null
    private var switchFingerPrint: Switch? = null
    private var layoutSubscriptionView: LinearLayout? = null


    private val GALLERY = 1
    private val CAMERA = 2


    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext = context

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_user_profile, container, false)
        try {
            getUserInfo()
            init(rootView)

        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
        }

        return rootView
    }

    private fun init(view: View) {
        txtUserName = view.findViewById(R.id.txt_user_name)
        txtUserType = view.findViewById(R.id.txt_user_type)
        txtLogout = view.findViewById(R.id.btn_logout)
        /*txtUserEmail=view.findViewById(R.id.txt_user_email)
        txtUserPhnNo=view.findViewById(R.id.txt_user_phn_no) */
        imgUserPic = view.findViewById(R.id.img_user_pic)
        // ibProfilePic=view.findViewById(R.id.ib_profile_pic)
        // ivUpdatePhn=view.findViewById(R.id.iv_update_phn)
        layoutPersonalInfo = view.findViewById(R.id.layout_personal_info)
        layoutUserDocs = view.findViewById(R.id.layout_user_doc)
        layoutChngPaswrd = view.findViewById(R.id.layout_chng_paswrd)
        switchFingerPrint = view.findViewById(R.id.switch_fingerprint)
        layoutSubscriptionView = view.findViewById(R.id.layout_subscription_detail)
        layout_acc_detail = view.findViewById(R.id.layout_acc_detail)

        layoutUserDocs!!.visibility = View.GONE
        layoutSubscriptionView!!.visibility = View.GONE
        componentAction()
    }

    private fun componentAction() {

        if (Kotpref.isFingerPrint) {
            switchFingerPrint!!.isChecked = true
        } else {
            switchFingerPrint!!.isChecked = false
        }
        txtLogout!!.setOnClickListener(View.OnClickListener {
            alertLogout()
        })

        /*   ibProfilePic!!.setOnClickListener {
               showPictureDialog()
           }

           ivUpdatePhn!!.setOnClickListener {
               updatePhnDialog()
           }*/
        layoutPersonalInfo!!.setOnClickListener {
            val intent = Intent(getActivity(), PersonalInformationScreen::class.java)

            Util.navigationNext(requireActivity(), PersonalInformationScreen::class.java)
        }

        layoutUserDocs!!.setOnClickListener {
            Util.navigationNext(requireActivity(), DocumentDetailScreen::class.java)
        }

        layoutChngPaswrd!!.setOnClickListener {
            Util.navigationNext(requireActivity(), ChangePasswordScreen::class.java)
        }

        layout_acc_detail!!.setOnClickListener {
            Util.navigationNext(requireActivity(), ChangePasswordScreen::class.java)
        }

        switchFingerPrint!!.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
                if (BiometricUtil.hasBiometricEnrolled(appContext)) {
                    Kotpref.isFingerPrint = true
                } else {
                    switchFingerPrint!!.isChecked = false
                    Kotpref.isFingerPrint = false
                    Util.alertOkMessage(
                        appContext,
                        getString(R.string.alert),
                        getString(R.string.error_msg_biometric_not_setup)
                    )
                }
            } else {
                Kotpref.isFingerPrint = false
            }
        }


    }

    private fun alertLogout() {
        val builder = AlertDialog.Builder(appContext)
        builder.setTitle(getString(R.string.logout))
        builder.setMessage(getString(R.string.tag_logout))
        builder.setPositiveButton(getString(R.string.yes)) { dialog, which ->
            Kotpref.isLogin = false
//            Kotpref.clear()

            val intent = Intent(appContext, LoginScreen::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            dialog.dismiss()
        }
        builder.setNegativeButton(getString(R.string.no)) { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun updatePhnDialog() {
        //Inflate the dialog with custom view
        val mDialogView =
            LayoutInflater.from(appContext).inflate(R.layout.layout_update_mobile, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(appContext)
            .setView(mDialogView)
            .setTitle("Update Phone Number")

        mDialogView.edit_user_phn_no!!.setText(txtUserPhnNo!!.text.toString())

        // button click of custom layout

        mBuilder.setPositiveButton(appContext.getString(R.string.update)) { dialog, which ->
            dialog.dismiss()
            //get text from EditTexts of custom layout
            val updatePhn = mDialogView.edit_user_phn_no!!.text.toString()

            //set the input text in TextView
            txtUserPhnNo!!.setText(updatePhn)
        }

        mBuilder.setNegativeButton(appContext.getString(R.string.cancel)) { dialog, which ->
            dialog.dismiss()
        }
        mBuilder.show()

    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(appContext)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(
            pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    fun choosePhotoFromGallary() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        /* if (resultCode == this.RESULT_CANCELED)
         {
         return
         }*/
        if (requestCode == GALLERY) {
            if (data != null) {
                val contentURI = data!!.data
                try {
                    val bitmap =
                        MediaStore.Images.Media.getBitmap(appContext.contentResolver, contentURI)

                    //  imageview!!.setImageBitmap(bitmap)

                } catch (e: IOException) {
                    e.printStackTrace()

                }

            }

        } else if (requestCode == CAMERA) {
            if (data != null) {
                val thumbnail = data!!.extras!!.get("data") as Bitmap
                //  imageview!!.setImageBitmap(thumbnail)
            }

        }
    }

    private fun getUserInfo() {
        //Create retrofit Service

        val credentials = UserDetailCredential()

        credentials.userId = Kotpref.userId
        val userDetailService: ApiInterface =
            ApiClient(appContext).provideService(ApiInterface::class.java)
        val apiCall: Observable<UserDetailResponse> =
            userDetailService.getUserDetail(credentials) //Test API Key

        RxAPICallHelper().call(apiCall, object : RxAPICallback<UserDetailResponse> {
            override fun onSuccess(response: UserDetailResponse) {
                userDetailResponse = response.data!!
                setUserInfo()
            }

            override fun onFailed(t: Throwable) {
                // show error
                Log.e("onFailure", t.toString())
                //  progressBarList?.visibility = View.GONE
            }
        })

    }

    private fun setUserInfo() {
        txtUserName?.text = userDetailResponse.firstName + " " + userDetailResponse.lastName
        txtUserType?.text = userDetailResponse.userRoleName

        Glide.with(appContext)
            .load(R.drawable.property_img1)
            .transform(CircleTransform())
            .into(imgUserPic!!)

        if (userDetailResponse.userRoleName.contains("Tenant", true)) {
            if (Kotpref.authProvider != "CX") {
                layoutChngPaswrd!!.visibility = View.GONE
            }
        }
    }

    companion object {
        var userDetailResponse = UserDetail()
    }
}