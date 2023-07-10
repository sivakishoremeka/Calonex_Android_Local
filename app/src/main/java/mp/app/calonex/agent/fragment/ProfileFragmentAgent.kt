package mp.app.calonex.agent.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_user_profile_agent.*
import mp.app.calonex.R
import mp.app.calonex.agent.activity.*

import mp.app.calonex.agent.model.RegistrationPayloadNew
import mp.app.calonex.broker.activity.AgentsActivityBroker
import mp.app.calonex.broker.activity.SubscriptionListActivityBroker
import mp.app.calonex.common.apiCredentials.FetchDocumentCredential
import mp.app.calonex.common.apiCredentials.UserDetailCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.BiometricUtil
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.common.utility.Util.bitmapToFile
import mp.app.calonex.common.utility.Util.getRealPathFromUri
import mp.app.calonex.common.utility.Util.navigationNext
import mp.app.calonex.landlord.activity.AccountLinkDetailScreen
import mp.app.calonex.landlord.activity.ChangePasswordScreen
import mp.app.calonex.landlord.activity.LoginScreen

import mp.app.calonex.landlord.model.FetchDocumentModel
import mp.app.calonex.landlord.model.UserDetail
import mp.app.calonex.landlord.response.FetchDocumentResponse
import mp.app.calonex.landlord.response.UserDetailResponse
import mp.app.calonex.registration.model.SubcriptionPlanModel
import mp.app.calonex.registration.response.SubcriptionPlanResponse
import mp.app.calonex.tenant.response.ResponseDtoResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ProfileFragmentAgent : Fragment() {
    private val TAG: String = "ProfileFragmentAgent"
    lateinit var appContext: Context
    private var txtUserName: TextView? = null

    //    private var txtLogout: Button? = null
    private var txtLogout: LinearLayout? = null
    private var txtUserType: TextView? = null
    private var accountVerificationWarning: TextView? = null
    private var txtUserEmail: TextView? = null
    private var txtUserPhnNo: TextView? = null
    private var imgUserPic: ImageView? = null
    private var ibProfilePic: ImageView? = null
    private var ll_edit_pic: LinearLayout? = null
    private var ivUpdatePhn: ImageView? = null
    private var layoutPersonalInfo: LinearLayout? = null
    private var layoutUserDocs: LinearLayout? = null
    private var layoutChngPaswrd: LinearLayout? = null
    private var layoutSubsDetail: LinearLayout? = null
    private var layoutAgentsDetail: LinearLayout? = null
    private var layoutRegLandlord: LinearLayout? = null
    private var layoutAccInfo: LinearLayout? = null
    private var layoutOnboardProperty: LinearLayout? = null
    private var layoutBookKeeping: LinearLayout? = null
    private var layoutMarketplace: LinearLayout? = null
    private var switchFingerPrint: Switch? = null
    private var pbProfile: ProgressBar? = null
    private var ll_franchise: LinearLayout? = null

    private val GALLERY = 1
    private val CAMERA = 2
    private var fileProfile: File? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_user_profile_agent, container, false)
        try {
            init(rootView)
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }
        return rootView
    }

    override fun onResume() {

        Log.e("Check", "<==onResume==>")
        getUserInfo()
        super.onResume()

    }

    private fun init(view: View) {
        ll_franchise = view.findViewById(R.id.ll_franchise);
        txtUserName = view.findViewById(R.id.txt_user_name)
        txtUserType = view.findViewById(R.id.txt_user_type)
        //txtLogout = view.findViewById(R.id.btn_logout)
        txtLogout = view.findViewById(R.id.layout_logout)
        /*txtUserEmail=view.findViewById(R.id.txt_user_email)
        txtUserPhnNo=view.findViewById(R.id.txt_user_phn_no) */

        txtUserEmail = view.findViewById(R.id.txt_user_email_address)
        accountVerificationWarning = view.findViewById(R.id.account_verification_warning)
        txtUserPhnNo = view.findViewById(R.id.txt_user_mobile)
        imgUserPic = view.findViewById(R.id.img_user_pic)
        ibProfilePic = view.findViewById(R.id.ib_profile_pic)
        ll_edit_pic = view.findViewById(R.id.ll_edit_pic)
        // ivUpdatePhn=view.findViewById(R.id.iv_update_phn)

        layoutPersonalInfo = view.findViewById(R.id.layout_personal_info)
        layoutUserDocs = view.findViewById(R.id.layout_user_doc)
        layoutChngPaswrd = view.findViewById(R.id.layout_chng_paswrd)
        switchFingerPrint = view.findViewById(R.id.switch_fingerprint)
        layoutSubsDetail = view.findViewById(R.id.layout_subscription_detail)
        layoutAgentsDetail = view.findViewById(R.id.layout_agent_detail)
        layoutRegLandlord = view.findViewById(R.id.layout_reg_landlord)
        layoutAccInfo = view.findViewById(R.id.layout_acc_detail)
        layoutOnboardProperty = view.findViewById(R.id.layout_onboard_property)
        layoutBookKeeping = view.findViewById(R.id.layout_book_keeping)
        layoutMarketplace = view.findViewById(R.id.layout_marketplace)
        pbProfile = view.findViewById(R.id.pb_profile)
        var layoutPrivacy: LinearLayout = view.findViewById(R.id.layout_privacy)
        var layoutTerms: LinearLayout = view.findViewById(R.id.layout_terms)

        layoutPrivacy.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse("https://app.calonex.com/account/privacy")
            startActivity(i)
        }
        layoutTerms.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse("https://app.calonex.com/account/terms-and-conditions")
            startActivity(i)
        }


        fetchImages()
        componentAction()

        if (Kotpref.userRole.contains("agent", true)) {
            accountVerificationWarning!!.visibility = View.GONE
        }
    }

    private fun componentAction() {
        switchFingerPrint!!.isChecked = Kotpref.isFingerPrint


        if (!Kotpref.bankAdded) {
            accountVerificationWarning!!.text =
                "Add your bank account to get payment directly to your bank account."
        } else if (!Kotpref.bankAccountVerified) {
            accountVerificationWarning!!.text =
                "Your bank account is still pending for verification, We're not able to charge you until you verify your bank account, Please check your bank account to get verification amount."
        } else {
            accountVerificationWarning!!.text = ""
        }

        ll_franchise!!.setOnClickListener(View.OnClickListener {
            startActivity(Intent(appContext, FranchiseSettingsAgent::class.java))

        })


        txtLogout!!.setOnClickListener(View.OnClickListener {
            alertLogout()
        })

        ll_edit_pic!!.setOnClickListener {
            showPictureDialog()
        }

        /*   ivUpdatePhn!!.setOnClickListener {
              updatePhnDialog()
          }*/

        layoutPersonalInfo!!.setOnClickListener {
            navigationNext(requireActivity(), PersonalInformationScreenAgent::class.java)
        }

        layoutUserDocs!!.setOnClickListener {
            navigationNext(requireActivity(), DocumentDetailScreenAgent::class.java)
        }

        layoutChngPaswrd!!.setOnClickListener {
            navigationNext(requireActivity(), ChangePasswordScreen::class.java)
        }

        layoutAccInfo!!.setOnClickListener {
            navigationNext(requireActivity(), AccountLinkDetailScreen::class.java)
        }

        accountVerificationWarning!!.setOnClickListener {
            Log.e("clicking for", "account_verification_warning")
            navigationNext(requireActivity(), AccountLinkDetailScreen::class.java)
        }

        layoutOnboardProperty!!.setOnClickListener {
            navigationNext(requireActivity(), AgentOnBoardedPropertyActivity::class.java)
        }

        layoutAgentsDetail!!.setOnClickListener {
            navigationNext(requireActivity(), AgentsActivityBroker::class.java)
        }

        layoutSubsDetail!!.setOnClickListener {
            if (userDetailResponse.userRoleName.contains("Agent", true)) {
                navigationNext(requireActivity(), SubscriptionListActivityAgent::class.java)
            } else {
                navigationNext(requireActivity(), SubscriptionListActivityBroker::class.java)
            }
        }

        layoutRegLandlord!!.setOnClickListener {
            if (Kotpref.userRole.contains("Agent")) {
                Util.navigationNext(requireActivity(), LinkPropertyAgent::class.java)
            } else {
                if (!Kotpref.bankAdded) {
                    bankAddDialog()
                } else if (!Kotpref.bankAccountVerified) {
                    bankVerifyDialog()
                } else {
                    val intent = Intent(appContext, LinkPropertyAgent::class.java)
                    intent.putExtra("fromRegisterLandlord", "Register LandLord")
                    startActivity(intent)

                }
            }
            //navigationNext(requireActivity(), QuickRegistrationActivity::class.java)
            //navigationNext(requireActivity(), QuickRegistrationNewActivity::class.java)
            //navigationNext(requireActivity(), LinkPropertyAgent::class.java)
            /* if(Kotpref.bankAccountVerified) {
                 navigationNext(requireActivity(), LinkPropertyAgent::class.java)
             }else{
                 *//*Util.alertOkIntentMessage(
                    appContext as Activity,
                    "Units Added",
                    "All the units have been added successfully",
                    HomeActivityCx::class.java
                )*//*
            Util.alertOkIntentMessage(
                requireActivity(),
                "Bank Account Not Verified",
                "Please verify your bank account details to register a landlord.",
                AccountLinkDetailScreen::class.java
            )
                *//*val intent =
                    Intent(this, AccountLinkDetailScreen::class.java)
                startActivity(intent)*//*
            }*/
        }

        layoutBookKeeping!!.setOnClickListener {
            navigationNext(requireActivity(), BookKeepingListActivityAgent::class.java)
        }

        layoutMarketplace!!.setOnClickListener {
            Log.e("clicking for", "market place")
            //navigationNext(requireActivity(), MarketplaceActivity::class.java)
            val viewIntent = Intent(
                "android.intent.action.VIEW",
                Uri.parse("https://rentcx.calonex.com/")
            )
            startActivity(viewIntent)

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
        if (Kotpref.bankAccountVerified) {
            accountVerificationWarning!!.visibility = View.GONE
        }

        registrationPayload.deviceId =
            Settings.Secure.getString(appContext.contentResolver, Settings.Secure.ANDROID_ID)
        getPlanSubcription()
    }

    private fun bankAddDialog() {
        val mBuilder = AlertDialog.Builder(requireContext())
        mBuilder.setTitle("Error")
        mBuilder.setMessage("Add your bank account to get payment directly to your bank account.")
        mBuilder.setPositiveButton("Ok") { dialog, which ->
            dialog.dismiss()
            Util.navigationNext(requireActivity(), AccountLinkDetailScreen::class.java)
        }

        mBuilder.show()
    }

    private fun bankVerifyDialog() {
        val mBuilder = AlertDialog.Builder(requireContext())
        mBuilder.setTitle("Error")
        mBuilder.setMessage("Your bank account is still pending for verification, We're not able to charge you until you verify your bank account, Please check your bank account to get verification amount.")
        mBuilder.setPositiveButton("Ok") { dialog, which ->
            dialog.dismiss()
            Util.navigationNext(requireActivity(), AccountLinkDetailScreen::class.java)
        }

        mBuilder.show()
    }

    private fun getUserInfo() {
        //Create retrofit Service

        val credentials = UserDetailCredential()
        pbProfile!!.visibility = View.VISIBLE
        credentials.userId = Kotpref.userId
        val userDetailService: ApiInterface =
            ApiClient(appContext).provideService(ApiInterface::class.java)
        val apiCall: Observable<UserDetailResponse> =
            userDetailService.getUserDetail(credentials) //Test API Key

        RxAPICallHelper().call(apiCall, object : RxAPICallback<UserDetailResponse> {
            override fun onSuccess(userDetail: UserDetailResponse) {
                userDetailResponse = userDetail.data!!
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
        pbProfile!!.visibility = View.GONE

        txtUserName?.text = userDetailResponse.firstName + " " + userDetailResponse.lastName

        txtUserEmail?.text = userDetailResponse.emailId
        try {
            if (userDetailResponse.phoneNumber.isNotEmpty()) {
                txtUserPhnNo?.text = userDetailResponse.phoneNumber
                txtUserPhnNo?.visibility = View.VISIBLE
            } else {
                txtUserPhnNo?.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        txtUserType?.text = userDetailResponse.userRoleName

        if (userDetailResponse.userRoleName.contains("Agent", true)) {
            layoutRegLandlord!!.visibility = View.VISIBLE
            layoutSubsDetail!!.visibility = View.GONE

            /*if (userDetailResponse.stripAccountVerified) {
                layoutAccInfo!!.visibility = View.VISIBLE
            } else {
                layoutAccInfo!!.visibility = View.GONE
            }*/
            layoutAccInfo!!.visibility = View.VISIBLE

            layoutUserDocs!!.visibility = View.VISIBLE
            layoutOnboardProperty!!.visibility = View.VISIBLE
            layoutAgentsDetail!!.visibility = View.GONE
            ll_franchise!!.visibility = View.GONE
            layoutMarketplace!!.visibility = View.VISIBLE
            if (Kotpref.authProvider != "CX") {
                layoutChngPaswrd!!.visibility = View.GONE
            }
        }

        if (userDetailResponse.userRoleName.contains("Broker", true)) {
            layoutRegLandlord!!.visibility = View.VISIBLE
            layoutSubsDetail!!.visibility = View.GONE
            //layoutSubsDetail!!.visibility = View.VISIBLE

            /*if (userDetailResponse.stripAccountVerified) {
                layoutAccInfo!!.visibility = View.VISIBLE
            } else {
                layoutAccInfo!!.visibility = View.GONE
            }*/

            layoutAccInfo!!.visibility = View.VISIBLE

            layoutUserDocs!!.visibility = View.VISIBLE
            layoutOnboardProperty!!.visibility = View.VISIBLE
            layoutAgentsDetail!!.visibility = View.VISIBLE
            ll_franchise!!.visibility = View.VISIBLE
            layoutMarketplace!!.visibility = View.VISIBLE
            if (Kotpref.authProvider != "CX") {
                layoutChngPaswrd!!.visibility = View.GONE
            }
        }

        var profileImg: String = ""
        val customPb: CircularProgressDrawable = Util.customProgress(requireContext())
        for (item in listUserImages) {

            if (item.uploadFileType.equals(getString(R.string.key_profile_pic))) {
                profileImg = item.fileName
                break
            }
        }

        if (!profileImg.isNullOrEmpty()) {
            Glide.with(appContext)
                .load(getString(R.string.base_url_img2) + profileImg)
                .placeholder(customPb)
                .into(imgUserPic!!)
        }
    }

    private fun alertLogout() {
        val builder = AlertDialog.Builder(appContext)
        builder.setTitle(getString(R.string.logout))
        builder.setMessage(getString(R.string.tag_logout))
        builder.setPositiveButton(getString(R.string.yes)) { dialog, which ->
            Kotpref.isLogin = false
            //  startActivity(Intent(appContext, LoginScreen::class.java))
            val intent = Intent(appContext, LoginScreen::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
            dialog.dismiss()
        }
        builder.setNegativeButton(getString(R.string.no)) { dialog, which ->
            dialog.dismiss()
        }
        builder.show()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        /* if (resultCode == this.RESULT_CANCELED)
         {
         return
         }*/
        if (requestCode == GALLERY && resultCode != Activity.RESULT_CANCELED) {
            if (data != null) {
                val contentURI = data.data
                fileProfile = getRealPathFromUri(requireActivity(), contentURI!!)
                profileUploadApi()
            }
        } else if (requestCode == CAMERA && resultCode != Activity.RESULT_CANCELED) {
            if (data != null) {
                val thumbnail = data.extras!!.get("data") as Bitmap
                fileProfile =
                    bitmapToFile(requireActivity(), thumbnail, getString(R.string.profile_pic))
                profileUploadApi()
            }
        }
    }

    private fun fetchImages() {
        if (NetworkConnection.isNetworkConnected(appContext)) {

            val credentials = FetchDocumentCredential()
            credentials.userId = Kotpref.userId

            val signatureApi: ApiInterface =
                ApiClient(appContext).provideService(ApiInterface::class.java)
            val apiCall: Observable<FetchDocumentResponse> =
                signatureApi.fetchDocument(credentials)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", it.responseDto!!.responseDescription.toString())

                    if (it.responseDto!!.responseCode.equals(200) && it.data != null) {
                        listUserImages = it.data!!

                    } else {
                        Toast.makeText(appContext, it.responseDto!!.message, Toast.LENGTH_SHORT)
                            .show()
                    }

                    setProfileImage()
                },
                    { e ->
                        Log.e("onFailure", e.toString())

                        e.message?.let {
                            Toast.makeText(appContext, it, Toast.LENGTH_SHORT).show()
                        }
                    })
        } else {
            Toast.makeText(appContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
        }
    }

    private fun setProfileImage() {
        var profileImg: String = ""
        val customPb: CircularProgressDrawable = Util.customProgress(requireContext())
        for (item in listUserImages) {

            if (item.uploadFileType.equals(getString(R.string.key_profile_pic))) {
                profileImg = item.fileName
                break
            }

        }
        if (!profileImg.isNullOrEmpty()) {

            Log.e("URL", "profileImg===> " + getString(R.string.base_url_img) + profileImg)

            Kotpref.profile_image = getString(R.string.base_url_img2) + profileImg

            Glide.with(appContext)
                .load(getString(R.string.base_url_img) + profileImg)
                .placeholder(customPb)
                .into(imgUserPic!!)
        }
    }

    private fun profileUploadApi() {
        if (NetworkConnection.isNetworkConnected(appContext)) {
            pbProfile!!.visibility = View.VISIBLE
            requireActivity().window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
            builder.addFormDataPart("userId", Kotpref.userId)
            builder.addFormDataPart(
                "uploadFileType",
                appContext.getString(R.string.key_profile_pic)
            )

            builder.addFormDataPart(
                "file", fileProfile!!.name,
                fileProfile!!.asRequestBody(MultipartBody.FORM)
            )
            val requestBody: RequestBody = builder.build()
            val uploadSign: ApiInterface =
                ApiClient(appContext).provideService(ApiInterface::class.java)

            val apiCallUploadSign: Observable<ResponseDtoResponse> =
                uploadSign.uploadDocument(requestBody)

            apiCallUploadSign.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({

                    if (it.responseDto?.responseCode!!.equals(200)) {

                        pbProfile!!.visibility = View.GONE
                        requireActivity().window
                            .clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        fetchImages()
                        //imgUserPic!!.setImageBitmap(BitmapFactory.decodeFile(fileProfile!!.absolutePath))
                    }
                },
                    { e ->
                        requireActivity().window
                            .clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        pbProfile!!.visibility = View.GONE

                        Log.e("error", e.message.toString())
                    })
        } else {
            Toast.makeText(appContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("CheckResult")
    private fun getPlanSubcription() {
        if (NetworkConnection.isNetworkConnected(appContext)) {
            //Create retrofit Service
            //pb_login!!.visibility = View.VISIBLE
            requireActivity().window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )

            val signatureApi: ApiInterface =
                ApiClient(appContext).provideService(ApiInterface::class.java)
            val apiCall: Observable<SubcriptionPlanResponse> = signatureApi.getSubscription("")

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", it.responseDto?.responseDescription.toString())

                    //pb_login!!.visibility = View.GONE
                    try {
                        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    if (it.data != null) {
                        subcriptionPlanList = it.data!!

                        //intent.putExtra("FromLandlord", true)
                        //Util.navigationNext(this)
                    }
                },
                    { e ->
                        Log.e("onFailure", e.toString())
                        //pb_login!!.visibility = View.GONE

                        try {
                            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        } catch (e1: Exception) {
                            e1.printStackTrace()
                        }

                        e.message?.let {
                            Toast.makeText(appContext, it, Toast.LENGTH_SHORT).show()
                        }
                    })
        } else {
            Toast.makeText(
                appContext,
                getString(R.string.error_network),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        var userDetailResponse = UserDetail()
        var listUserImages = ArrayList<FetchDocumentModel>()

        var registrationPayload = RegistrationPayloadNew()
        var subcriptionPlanList = ArrayList<SubcriptionPlanModel>()
    }
}