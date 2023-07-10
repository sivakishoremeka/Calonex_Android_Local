package mp.app.calonex.landlord.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_invite_landlord_screen.*
import kotlinx.android.synthetic.main.activity_login_credential_screen.*
import kotlinx.android.synthetic.main.activity_login_screen.*
import kotlinx.android.synthetic.main.activity_tenant_verify_update_details_first.*
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.EmailCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.RSA
import mp.app.calonex.common.utility.UsSSNNumberFormatter
import mp.app.calonex.common.utility.Util
import mp.app.calonex.common.utility.Util.bitmapToFile
import mp.app.calonex.landlord.adapter.CustomSpinnerAdapter
import mp.app.calonex.landlord.model.ZipCityStateModel
import mp.app.calonex.registration.model.DocsPayload
import mp.app.calonex.registration.model.DocumentPayload
import mp.app.calonex.registration.model.RegistrationPayload2
import mp.app.calonex.registration.response.UserRegistrationResponse2
import mp.app.calonex.registration.response.ValidateEmailResponse
import mp.app.calonex.tenant.response.ResponseDtoResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException


class QuickRegistrationActivity : AppCompatActivity() {
    private lateinit var edit_ut_first_name: TextInputEditText
    private lateinit var edit_ut_last_name: TextInputEditText
    private lateinit var edit_ut_email: TextInputEditText
    private lateinit var edit_ut_pswrd: TextInputEditText
    private lateinit var edit_ut_re_pswrd: TextInputEditText
    private lateinit var edit_ut_ssn: TextInputEditText
    private lateinit var edit_ut_licence: TextInputEditText

    private lateinit var spinner_reg_state_issue: Spinner

    private lateinit var layout_front_licence: RelativeLayout
    private lateinit var layout_back_licence: RelativeLayout

    private lateinit var img_front_licence: ImageView
    private lateinit var img_back_licence: ImageView

    private lateinit var txt_signin: TextView

    private lateinit var btn_signup: Button

    private lateinit var pb_ut_confirm: ProgressBar

    private var regStateIssue: String? = null

    private var isLayoutFrontLicence: Boolean? = false
    private var isLayoutBackLicence: Boolean? = false

    private var isImgFrontLicence: Boolean? = false
    private var isImgBackLicence: Boolean? = false

    private var bitmapLicenceFront: Bitmap? = null
    private var bitmapLicenceBack: Bitmap? = null

    private val GALLERY = 1
    private val CAMERA = 2

    private var zcsList = ArrayList<ZipCityStateModel>()
    private var stateIssueList = ArrayList<String>()

    private lateinit var registrationPayload: RegistrationPayload2

    private var count: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quick_registration)

        edit_ut_first_name = findViewById(R.id.edit_ut_first_name)
        edit_ut_last_name = findViewById(R.id.edit_ut_last_name)
        edit_ut_email = findViewById(R.id.edit_ut_email)
        edit_ut_pswrd = findViewById(R.id.edit_ut_pswrd)
        edit_ut_re_pswrd = findViewById(R.id.edit_ut_re_pswrd)
        edit_ut_ssn = findViewById(R.id.edit_ut_ssn)



        val addSsnFormatter = UsSSNNumberFormatter(edit_ut_ssn!!)
        edit_ut_ssn!!.addTextChangedListener(addSsnFormatter)



        edit_ut_licence = findViewById(R.id.edit_ut_licence)

        spinner_reg_state_issue = findViewById(R.id.spinner_reg_state_issue)

        layout_front_licence = findViewById(R.id.layout_front_licence)
        layout_back_licence = findViewById(R.id.layout_back_licence)

        img_front_licence = findViewById(R.id.img_front_licence)
        img_back_licence = findViewById(R.id.img_back_licence)

        btn_signup = findViewById(R.id.btn_signup)

        txt_signin = findViewById(R.id.txt_signin)

        pb_ut_confirm = findViewById(R.id.pb_ut_confirm)

        zcsList = Util.getZipCityStateList(applicationContext)
        val listState = mutableListOf<String>()


        stateIssueList.add("State Issued")
        for(item in zcsList) {
            listState.add(item.state!!)
        }

        stateIssueList.addAll(ArrayList<String>(listState.toSet().sorted().toList()))
        val spinnerStateIssueAdapter =  CustomSpinnerAdapter(applicationContext,  stateIssueList)
        spinner_reg_state_issue.adapter = spinnerStateIssueAdapter

        spinner_reg_state_issue.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (position > 0){
                    regStateIssue = stateIssueList.get(position)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // another interface callback
            }
        }

        registrationPayload = RegistrationPayload2()

        btn_signup.setOnClickListener {
            validData()
        }

        layout_front_licence.setOnClickListener {
            isLayoutFrontLicence = true
            showPictureDialog()
        }

        layout_back_licence.setOnClickListener {
            isLayoutBackLicence = true
            showPictureDialog()
        }

        img_front_licence.setOnClickListener {
            isImgFrontLicence = true
            showPictureDialog()
        }

        img_back_licence.setOnClickListener {
            isImgBackLicence = true
            showPictureDialog()
        }

        txt_signin.setOnClickListener {
           // startActivity(Intent(this@QuickRegistrationActivity, LoginScreen::class.java))
            val intent = Intent(this, LoginScreen::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }



        edit_ut_re_pswrd.customSelectionActionModeCallback = object : ActionMode.Callback {
            override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                return false
            }
            override fun onDestroyActionMode(mode: ActionMode) {}
            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                return false
            }
            override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
                return false
            }
        }
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    fun choosePhotoFromGallary() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    public override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /* if (resultCode == this.RESULT_CANCELED)
         {
         return
         }*/
        if (requestCode == GALLERY) {
            if (data != null) {
                val contentURI = data.data
                try {
                    if(Build.VERSION.SDK_INT < 28) {
                        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,contentURI!!)
                        displayImage(bitmap)
                    } else {
                        val source = ImageDecoder.createSource(this.contentResolver, contentURI!!)
                        val bitmap = ImageDecoder.decodeBitmap(source)
                        displayImage(bitmap)
                    }
                }
                catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        } else if (requestCode == CAMERA) {
            if (data != null) {
                val thumbnail = data.extras!!.get("data") as Bitmap
                displayImage(thumbnail)
            }
        }
    }

    fun displayImage(bitmap: Bitmap) {
        if(isLayoutFrontLicence!! || isImgFrontLicence!!) {
            isLayoutFrontLicence = false
            isImgFrontLicence = false
            layout_front_licence.visibility = View.GONE
            img_front_licence.visibility = View.VISIBLE
            bitmapLicenceFront = bitmap
            img_front_licence.setImageBitmap(bitmapLicenceFront)

        }

        if(isLayoutBackLicence!! || isImgBackLicence!!) {
            isLayoutBackLicence = false
            isImgBackLicence = false
            layout_back_licence.visibility = View.GONE
            img_back_licence.visibility = View.VISIBLE
            bitmapLicenceBack = bitmap

            // SAMIT - 26.05.22
            // Compare images for driver licenses. Do not allow same images
            if (compareBitmaps(bitmapLicenceFront, bitmapLicenceBack)) {
                Toast.makeText(applicationContext,"The front and the back of licence can't be the same", Toast.LENGTH_SHORT).show()
                layout_back_licence.visibility = View.VISIBLE
                img_back_licence.visibility = View.GONE
                return
            }

            img_back_licence.setImageBitmap(bitmapLicenceBack)
            documentPayload.regLicenceBack = bitmapLicenceBack
        }
    }

    // SAMIT - 26.05.22
    // Compare images for driver licenses
    fun compareBitmaps(bitmap1: Bitmap?, bitmap2: Bitmap?) : Boolean {
        if (bitmap1 == null) {
            return false
        }

        if (bitmap2 == null) {
            return false
        }

        // First Image
        val byteArrayOutputStream1 = ByteArrayOutputStream()
        bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream1)
        val byteArray1: ByteArray = byteArrayOutputStream1.toByteArray()
        val encoded1: String = Base64.encodeToString(byteArray1, Base64.DEFAULT)

        // Second Image
        val byteArrayOutputStream2 = ByteArrayOutputStream()
        bitmap2.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream2)
        val byteArray2: ByteArray = byteArrayOutputStream2.toByteArray()
        val encoded2: String = Base64.encodeToString(byteArray2, Base64.DEFAULT)

        if (encoded1 == encoded2) {
            return true
        }

        return false
    }

    fun validData() {
        if (TextUtils.isEmpty(edit_ut_first_name.text)) {
            edit_ut_first_name.error = "First name required"
            edit_ut_first_name.requestFocus()
            return
        }

        if (TextUtils.isEmpty(edit_ut_last_name.text)) {
            edit_ut_last_name.error = "Last name required"
            edit_ut_last_name.requestFocus()
            return
        }

        if (TextUtils.isEmpty(edit_ut_email.text) || (!Util.isEmailValid(edit_ut_email.text.toString().trim()))) {
            edit_ut_email.error = "Valid email Id required"
            edit_ut_email.requestFocus()
            return
        }

        if (edit_ut_pswrd.text.toString().trim().length < 8) {
            edit_ut_pswrd.error = "Required minimum 8 digits with one special character."
            edit_ut_pswrd.requestFocus()
            return
        }

        if (edit_ut_pswrd.text.toString().trim() != edit_ut_re_pswrd.text.toString().trim()) {
            edit_ut_re_pswrd.error = "Password not matched."
            edit_ut_re_pswrd.requestFocus()
            return
        }

        if (TextUtils.isEmpty(edit_ut_ssn.text)) {
            edit_ut_ssn.error = "Valid SSN number required."
            edit_ut_ssn.requestFocus()
            return
        }

        if (TextUtils.isEmpty(edit_ut_licence.text) || edit_ut_licence.text?.length != 9) {
            edit_ut_licence.error = "Invalid Driving License number."
            edit_ut_licence.requestFocus()
            return
        }

        if (regStateIssue.isNullOrEmpty()) {
            Toast.makeText(applicationContext,"Please Select State Issued", Toast.LENGTH_SHORT).show()
            return
        }

        if (bitmapLicenceFront == null) {
            Toast.makeText(applicationContext,"Please upload the front pic of licence", Toast.LENGTH_SHORT).show()
            return
        } else {
            val documentLf = DocsPayload()
            documentLf.imgBitmap = bitmapLicenceFront

            documentLf.imgKey = getString(R.string.key_img_lf)
            documentPayloadList.add(documentLf)
            documentPayload.regLicenceFront = bitmapLicenceFront


            Log.d("TAG", "validData:image name "+bitmapLicenceFront)

        }

        if (bitmapLicenceBack == null) {
            Toast.makeText(applicationContext,"Please upload the back pic of licence", Toast.LENGTH_SHORT).show()
            return
        } else {
            val documentLb = DocsPayload()
            documentLb.imgBitmap = bitmapLicenceBack
            documentLb.imgKey = getString(R.string.key_img_lb)
            documentPayloadList.add(documentLb)
            documentPayload.regLicenceBack = bitmapLicenceBack
        }

        registrationPayload.firstName = edit_ut_first_name.text.toString().trim()
        registrationPayload.lastName = edit_ut_last_name.text.toString().trim()
        registrationPayload.emailId = edit_ut_email.text.toString().trim()
        registrationPayload.password = RSA.encrypt(edit_ut_pswrd.text.toString().trim())
        registrationPayload.ssn = edit_ut_ssn.text.toString().trim()
        registrationPayload.drivingLicenseNumber = RSA.encrypt(edit_ut_licence.text.toString().trim())
        registrationPayload.stateIssued = regStateIssue
        registrationPayload.userRoleName = "CX-Landlord"

       //registrationPayload.confirmPassword = RSA.encrypt(edit_ut_re_pswrd.text.toString().trim())

        validateApi()

        //registerApi()
    }

    @SuppressLint("CheckResult")
    private fun registerApi() {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            //Create retrofit Service
            pb_ut_confirm.visibility = View.VISIBLE
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            //Log.e("REG_LOG_I_2", Gson().toJson(registrationPayload))
            val registerService: ApiInterface = ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<UserRegistrationResponse2> = registerService.registerUserNew(registrationPayload)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
                //Log.e("REG_LOG_O_2", Gson().toJson(it.responseDto))
                Log.e("onSuccess", it.responseDto?.responseDescription.toString())

                if(it!!.responseDto!!.responseCode.equals(200)) {


                    for (item in documentPayloadList) {
                        // registerUploadApi(item.imgBitmap!!,item.imgKey!!,registerDetail.userCatalogID!!)
                        registerUploadApi(
                            item.imgBitmap!!,
                            item.imgKey!!,
                            it!!.responseDto!!.userId!!
                        )
                    }
                }else{
                    pb_ut_confirm.visibility = View.GONE
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    Toast.makeText(applicationContext, it.responseDto!!.responseDescription, Toast.LENGTH_SHORT).show()
                }
            },
                { e ->
                    Log.e("onFailure", e.toString())
                    pb_ut_confirm.visibility = View.GONE
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    val exception: HttpException = e as HttpException
                    if(exception.code().equals(400) ) {
                        Toast.makeText(applicationContext, getString(R.string.error_email_exist), Toast.LENGTH_SHORT).show()
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


    @SuppressLint("CheckResult")
    private fun validateApi() {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            //Create retrofit Service
            //pb_validate.visibility = View.VISIBLE
            pb_ut_confirm.visibility = View.VISIBLE
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            val credentials = EmailCredential()
            credentials.emailId = edit_ut_email.text.toString().trim()

            //Log.e("REG_LOG_I_1", Gson().toJson(credentials))
            val validateService: ApiInterface = ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<ValidateEmailResponse> = validateService.emailValidate(credentials)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
                //Log.e("REG_LOG_O_1", Gson().toJson(it.responseDto))
                Log.e("onSuccess", it.responseDto?.responseDescription.toString())

                pb_ut_confirm!!.visibility = View.GONE
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                if(it!!.responseDto!!.responseCode.equals(200) && it.data!=null) {
                    if(it.data!!.canRegister!!){
                        //Util.navigationNext(this, UserContactDetailScreen::class.java)
                        registerApi()
                    }else{
                        Toast.makeText(applicationContext,it.data!!.canRegisterMessage, Toast.LENGTH_SHORT).show()
                    }
                } else{
                    Toast.makeText(applicationContext,it!!.responseDto!!.responseDescription, Toast.LENGTH_SHORT).show()
                }
            },
                { e ->
                    Log.e("onFailure", e.toString())
                    pb_ut_confirm!!.visibility = View.GONE

                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    e.message?.let {
                        Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            Toast.makeText(applicationContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
        }
    }



    @SuppressLint("CheckResult")
    fun registerUploadApi(bitmapUpload: Bitmap, keyValue: String, idValue: String) {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            pb_ut_confirm!!.visibility = View.VISIBLE
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
            builder.addFormDataPart("userId", idValue)
            builder.addFormDataPart("uploadFileType", keyValue)
            var file: File? = bitmapToFile(this@QuickRegistrationActivity, bitmapUpload, keyValue)
            Log.d("TAG", "registerUploadApi:image name "+file?.name)

            builder.addFormDataPart(
                "file", file!!.name,
                file.asRequestBody(MultipartBody.FORM)
            )
            val requestBody: RequestBody = builder.build()
            val uploadSign: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)

            val apiCallUploadSign: Observable<ResponseDtoResponse> =
                uploadSign.uploadDocument(requestBody)

            apiCallUploadSign.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({
                    count = count + 1
                    if (!it.responseDto?.responseCode!!.equals(200)) {

                        pb_ut_confirm.visibility = View.GONE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        /*Toast.makeText(
                            this,
                            it.responseDto?.exceptionDescription!!,
                            Toast.LENGTH_SHORT
                        ).show()*/
                    }

                    if (count == documentPayloadList.size) {
                        pb_ut_confirm!!.visibility = View.GONE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                      //  startActivity(Intent(this@QuickRegistrationActivity, SignupDoneActivity::class.java))

                       // finish()
                        val intent = Intent(this, SignupDoneActivity::class.java)
                        intent.putExtra("role","landlord")
                        intent.putExtra("email",edit_ut_email.text.toString().trim())
                        startActivity(intent)
                        finish()
                    }

                },
                    { e ->
                        count = count + 1
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        pb_ut_confirm.visibility = View.GONE
                        Toast.makeText(
                            this,
                            e.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        if (count == documentPayloadList.size) {
                            pb_ut_confirm!!.visibility = View.GONE
                            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                           /* startActivity(Intent(this@QuickRegistrationActivity, SignupDoneActivity::class.java))
                            finish()*/
                            val intent = Intent(this, SignupDoneActivity::class.java)
                            intent.putExtra("role","landlord")
                            intent.putExtra("email",edit_ut_email.text.toString().trim())
                            startActivity(intent)
                            finish()
                        }

                        Log.e("error", e.message.toString())

                    })

        } else {
            Toast.makeText(
                applicationContext,
                getString(R.string.error_network),
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    companion object {
        var documentPayload = DocumentPayload()
        var documentPayloadList = ArrayList<DocsPayload>()
    }

    /*
    https://api.calonex.com/api/root-service/api/users/registrationNew

{
  "firstName": "ww",
  "lastName": "ww",
  "emailId": "ww@yopmail.com",
  "password": "U2FsdGVkX1+8WR+JPRZvDMmd37niKTAq/YuUifA6yM4=",
  "confirmPassword": "U2FsdGVkX1+8WR+JPRZvDMmd37niKTAq/YuUifA6yM4=",
  "drivingLicenseNumber": "111111111",
  "stateIssued": "Alaska",
  "ssn": "111111111",
  "userRoleName": "CX-Landlord"
}

{
  "responseDto": {
    "responseCode": 200,
    "responseDescription": "Registration Success",
    "exceptionCode": 0
  }
}
     */

    /*
    2022-01-25 13:25:33.244 E/REG_LOG_I_1: {"emailId":"ss@yopmail.com"}
2022-01-25 13:25:35.301 E/REG_LOG_O_1: {"exceptionCode":0,"responseCode":200,"responseDescription":"OK"}
2022-01-25 13:25:35.301 E/onSuccess: OK
2022-01-25 13:25:35.311 E/REG_LOG_I_2: {"confirmPassword":"","drivingLicenseNumber":"111111111","emailId":"ss@yopmail.com","firstName":"ss","lastName":"ss","password":"U2FsdGVkX19v+9AVi4I+m2YS8m9AEcPowcIPXfNxqn4\u003d","ssn":"11111111111","stateIssued":"Alaska","userRoleName":"CX-Landlord"}
2022-01-25 13:25:41.383 E/REG_LOG_O_2: {"exceptionCode":0,"responseCode":200,"responseDescription":"Registration Success"}
2022-01-25 13:25:41.383 E/onSuccess: Registration Success
     */
}