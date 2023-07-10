package mp.app.calonex.landlord.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import mp.app.calonex.R
import mp.app.calonex.agent.activity.BookKeepingListActivityAgent
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
import mp.app.calonex.landlord.activity.*
import mp.app.calonex.landlord.model.FetchDocumentModel
import mp.app.calonex.landlord.model.UserDetail
import mp.app.calonex.landlord.response.FetchDocumentResponse
import mp.app.calonex.landlord.response.UserDetailResponse
import mp.app.calonex.tenant.activity.TenantPropertyHistoryScreen
import mp.app.calonex.tenant.model.GetCreditePointsTenant
import mp.app.calonex.tenant.response.CreditPointResponse
import mp.app.calonex.tenant.response.ResponseDtoResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.math.RoundingMode
import java.util.*

class ProfileLdFragment : Fragment() {
    private val TAG: String = "ProfileLdFragment"
    lateinit var appContext: Context
    private var txtUserName: TextView? = null
    private var accountverificationwarning: TextView? = null
    private var txtLogout: Button? = null
    private var layout_logout: LinearLayout? = null
    private var txtUserType: TextView? = null
    private var txtUserEmail: TextView? = null
    private var txtUserPhnNo: TextView? = null
    private var imgUserPic: ImageView? = null
    private var ibProfilePic: ImageView? = null
    private var ivUpdatePhn: ImageView? = null
    private var layoutPersonalInfo: LinearLayout? = null
    private var layoutUserDocs: LinearLayout? = null
    private var layoutChngPaswrd: LinearLayout? = null
    private var layoutRegisterLd: LinearLayout? = null
    private var layoutSubsDetail: LinearLayout? = null
    private var layoutSubsInvoice: LinearLayout? = null
    private var layoutLeaseHistory: LinearLayout? = null
    private var layoutAccInfo: LinearLayout? = null
    private var layoutPropertyRequest: LinearLayout? = null
    private var layoutBookkeeping: LinearLayout? = null
    private var layoutMarketplace: LinearLayout? = null

    private var ll_credit_cash: LinearLayout? = null
    private var tv_credit_points: TextView? = null
    private var tv_cash_points: TextView? = null


    private var switchFingerPrint: Switch? = null
    private var pbProfile: ProgressBar? = null

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
        val rootView = inflater.inflate(R.layout.fragment_user_profile, container, false)
        try {

            init(rootView)

        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
        }

        return rootView
    }

    override fun onResume() {
        super.onResume()
        getUserInfo()
        fetchImages()
    }

    private fun init(view: View) {
        txtUserName = view.findViewById(R.id.txt_user_name)
        accountverificationwarning = view.findViewById(R.id.account_verification_warning)
        txtUserType = view.findViewById(R.id.txt_user_type)
        txtLogout = view.findViewById(R.id.btn_logout)
        layout_logout = view.findViewById(R.id.layout_logout)
        txtUserEmail = view.findViewById(R.id.txt_user_email_address)
        txtUserPhnNo = view.findViewById(R.id.txt_user_mobile)
        imgUserPic = view.findViewById(R.id.img_user_pic)
        ibProfilePic = view.findViewById(R.id.ib_profile_pic)

        // ivUpdatePhn=view.findViewById(R.id.iv_update_phn)
        layoutPersonalInfo = view.findViewById(R.id.layout_personal_info)
        layoutUserDocs = view.findViewById(R.id.layout_user_doc)
        layoutChngPaswrd = view.findViewById(R.id.layout_chng_paswrd)
        layoutRegisterLd = view.findViewById(R.id.layout_register_ld)
        switchFingerPrint = view.findViewById(R.id.switch_fingerprint)
        layoutSubsDetail = view.findViewById(R.id.layout_subscription_detail)
        layoutSubsInvoice = view.findViewById(R.id.layout_subscription_invoice)
        layoutLeaseHistory = view.findViewById(R.id.layout_lease_history)
        layoutAccInfo = view.findViewById(R.id.layout_acc_detail)
        layoutPropertyRequest = view.findViewById(R.id.layout_cx_request_info)
        layoutBookkeeping = view.findViewById(R.id.layout_bookkeeping)
        layoutMarketplace = view.findViewById(R.id.layout_marketplace)
        pbProfile = view.findViewById(R.id.pb_profile)

        ll_credit_cash = view.findViewById(R.id.ll_credit_cash)
        tv_credit_points = view.findViewById(R.id.tv_credit_points)
        tv_cash_points = view.findViewById(R.id.tv_cash_points)

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



        componentAction()
    }

    private fun componentAction() {
        switchFingerPrint!!.isChecked = Kotpref.isFingerPrint

        if (!Kotpref.bankAdded) {
            accountverificationwarning!!.text =
                "Add your bank account to get payment directly to your bank account."
        } else if (!Kotpref.bankAccountVerified) {
            accountverificationwarning!!.text =
                "Your bank account is still pending for verification, We're not able to charge you until you verify your bank account, Please check your bank account to get verification amount."
        } else {
            accountverificationwarning!!.text = ""
        }

        txtLogout!!.setOnClickListener(View.OnClickListener {
            alertLogout()
        })

        layout_logout!!.setOnClickListener(View.OnClickListener {
            alertLogout()
        })

        ibProfilePic!!.setOnClickListener {
            showPictureDialog()
        }

        /*   ivUpdatePhn!!.setOnClickListener {
              updatePhnDialog()
          }*/
        layoutPersonalInfo!!.setOnClickListener {
            navigationNext(requireActivity(), PersonalInformationScreen::class.java)

        }
        layoutUserDocs!!.setOnClickListener {
            navigationNext(requireActivity(), DocumentDetailScreen::class.java)

        }
        layoutChngPaswrd!!.setOnClickListener {
            navigationNext(requireActivity(), ChangePasswordScreen::class.java)
        }

        layoutRegisterLd!!.setOnClickListener {
            navigationNext(requireActivity(), QuickRegistrationNewActivity::class.java)
        }

        layoutSubsDetail!!.setOnClickListener {
            navigationNext(requireActivity(), SubscribePlanDetailScreen::class.java)
        }
        layoutSubsInvoice!!.setOnClickListener {
            navigationNext(requireActivity(), LandLordSubscriptionInvoiceActivity::class.java)
        }

        layoutPropertyRequest!!.setOnClickListener {
            navigationNext(requireActivity(), TenantPropertyHistoryScreen::class.java)
        }

        layoutBookkeeping!!.setOnClickListener {
            //navigationNext(requireActivity(), BookKeepingListActivityAgent::class.java)
            /*if (userDetailResponse.userRoleName.contains("Tenant", true)){
                val intent = Intent(appContext, BookKeepingListActivityAgent::class.java)
                intent.putExtra("fromTenant", true)
                startActivity(intent)
            } else {
                val intent = Intent(appContext, BookKeepingListActivityAgent::class.java)
                startActivity(intent)
            }*/
            val intent = Intent(appContext, BookKeepingListActivityAgent::class.java)
            startActivity(intent)
        }

        layoutMarketplace!!.setOnClickListener {
            try {
                val viewIntent = Intent(
                    "android.intent.action.VIEW",
                    Uri.parse("https://rentcx.calonex.com/")
                )
                startActivity(viewIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            //  navigationNext(requireActivity(), MarketplaceActivity::class.java)
        }

        layoutLeaseHistory!!.setOnClickListener {
            val intent =
                Intent(
                    appContext,
                    TenantLeaseRequestScreen::class.java
                )
            intent.putExtra("isTenant", true)
            intent.putExtra(appContext.getString(R.string.is_lease_history), true)
            startActivity(intent)

//            navigationNext(activity!!, TenantLeaseRequestScreen::class.java)
        }
        if (Kotpref.bankAccountVerified) {
            accountverificationwarning!!.visibility = View.GONE
        }

        layoutAccInfo!!.setOnClickListener {
            if (userDetailResponse.userRoleName.contains("Tenant", true)) {
                // navigationNext(requireActivity(), AddBankDetailsActivity::class.java)
                navigationNext(requireActivity(), AccountLinkDetailScreen::class.java)
            } else {
                //navigationNext(requireActivity(), AccountDetailScreen::class.java)
                navigationNext(requireActivity(), AccountLinkDetailScreen::class.java)
            }
        }

        accountverificationwarning!!.setOnClickListener {
            if (userDetailResponse.userRoleName.contains("Tenant", true)) {
                // navigationNext(requireActivity(), AddBankDetailsActivity::class.java)
                navigationNext(requireActivity(), AccountLinkDetailScreen::class.java)
            } else {
                //navigationNext(requireActivity(), AccountDetailScreen::class.java)
                navigationNext(requireActivity(), AccountLinkDetailScreen::class.java)
            }
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


            // startActivity(Intent(appContext, LoginScreen::class.java))
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
         resultCode == Activity.RESULT_CANCELED
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
        txtUserPhnNo?.text = userDetailResponse.phoneNumber

        txtUserType?.text = userDetailResponse.userRoleName

        if (userDetailResponse.userRoleName.contains("Tenant", true)) {
            layoutSubsDetail!!.visibility = View.GONE
            layoutSubsInvoice!!.visibility = View.GONE
            layoutRegisterLd!!.visibility = View.GONE
            // layoutLeaseHistory!!.visibility = View.VISIBLE

            /*if (userDetailResponse.stripAccountVerified) {
                layoutAccInfo!!.visibility = View.VISIBLE
            } else {
                layoutAccInfo!!.visibility = View.GONE
            }*/

            layoutAccInfo!!.visibility = View.VISIBLE

            layoutUserDocs!!.visibility = View.GONE
            layoutPropertyRequest!!.visibility = View.VISIBLE
            layoutBookkeeping!!.visibility = View.VISIBLE
            layoutMarketplace!!.visibility = View.VISIBLE

            if (Kotpref.authProvider != "CX") {
                layoutChngPaswrd!!.visibility = View.GONE
            }

            ll_credit_cash!!.visibility = View.VISIBLE
            getBook()
        } else {
            ll_credit_cash!!.visibility = View.GONE

        }

        if (userDetailResponse.userRoleName.contains("Landlord", true)) {
            layoutBookkeeping!!.visibility = View.VISIBLE
            layoutMarketplace!!.visibility = View.GONE
            layoutRegisterLd!!.visibility = View.GONE
            if (Kotpref.authProvider != "CX") {
                layoutChngPaswrd!!.visibility = View.GONE
            }
        }

    }

    private fun getBook() {

        if (NetworkConnection.isNetworkConnected(appContext)) {
            requireActivity().window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )

            val year = Calendar.getInstance().get(Calendar.YEAR)
            val credentials = GetCreditePointsTenant()
            credentials.tenantId = Kotpref.userId
            credentials.year = year.toLong()

            val creditPointsService: ApiInterface =
                ApiClient(appContext).provideService(ApiInterface::class.java)
            val apiCall: Observable<CreditPointResponse> =
                creditPointsService.getCreditPoint(credentials) //Test API Key

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                    try {

                        if (it.data.size > 0) {
                            var creditPoints = 0.0f

                            for (item in it.data) {
                                creditPoints += item.amount!!.toFloat()
                            }

                            tv_credit_points!!.text =
                                "$" + creditPoints.toBigDecimal().setScale(2, RoundingMode.UP)
                                    .toDouble()

                            var cashFlow = creditPoints / 12

                            tv_cash_points!!.text =
                                "$" + cashFlow.toBigDecimal().setScale(2, RoundingMode.UP)
                                    .toDouble()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                    requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                },
                    { e ->
                        Log.e("onFailure", e.toString())

                        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        e.message?.let {
                            Toast.makeText(appContext, it, Toast.LENGTH_SHORT).show()
                        }
                    })
        } else {
            Toast.makeText(appContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
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

                        //pbProfile!!.visibility = View.GONE
                        requireActivity().window
                            .clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                        fetchImages()

                        // imgUserPic!!.setImageBitmap(BitmapFactory.decodeFile(fileProfile!!.absolutePath))


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

    companion object {
        var userDetailResponse = UserDetail()
        var listUserImages = ArrayList<FetchDocumentModel>()
    }

}