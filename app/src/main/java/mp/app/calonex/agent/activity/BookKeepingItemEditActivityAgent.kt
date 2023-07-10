package mp.app.calonex.agent.activity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import mp.app.calonex.R
import mp.app.calonex.agent.fragment.ProfileFragmentAgent
import mp.app.calonex.agent.model.AgentBookKeepingAddResponce
import mp.app.calonex.agent.model.BookkeepingAddAgent
import mp.app.calonex.agent.model.BookkeepingUpdateAgent
import mp.app.calonex.agent.responce.AgentBookKeepingGetCategoriesResponse
import mp.app.calonex.agent.responce.CategoryListForBookKeepingResponseItem
import mp.app.calonex.common.apiCredentials.FetchDocumentCredential
import mp.app.calonex.common.apiCredentials.UserDetailCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.CxBaseActivity2
import mp.app.calonex.landlord.model.UserDetail
import mp.app.calonex.landlord.response.FetchDocumentResponse
import mp.app.calonex.landlord.response.UserDetailResponse
import mp.app.calonex.tenant.response.ResponseDtoResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class BookKeepingItemEditActivityAgent : CxBaseActivity2() {

    private var edt_upload_document: TextInputEditText? = null
    private var edit_date: TextInputEditText? = null
    private var edit_amount: TextInputEditText? = null
    private var spinner_type: Spinner? = null
    private var spinner_category: Spinner? = null
    private var edit_comment: TextInputEditText? = null
    private var btn_save: TextView? = null
    private var iv_header_logo: ImageView? = null
    private var imgUserPic: ImageView? = null
    private var pb_ut_confirm: ProgressBar? = null

    private var cal = Calendar.getInstance()
    private var year = cal.get(Calendar.YEAR)
    private var month = cal.get(Calendar.MONTH)
    private var day = cal.get(Calendar.DAY_OF_MONTH)
    val myFormat = "MMM dd, yyyy"

    var categoryStrings: ArrayList<String> = arrayListOf<String>()
    var type: String = ""
    var category: String = ""
    var startDate: String = ""

    var uid: Int? = null
    var itemId: Int? = 0
    var adapterCategory: ArrayAdapter<String>? = null
    var amount: Float = 0.0f
    var des: String? = null;
    var datee: Long = 0

    var typeStrings = arrayOf("Earnings", "Expenses")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookkeeping_edit_agent)

        edit_date = findViewById(R.id.edit_ut_date)
        edit_amount = findViewById(R.id.edit_amount)
        spinner_type = findViewById(R.id.spinner_type)
        spinner_category = findViewById(R.id.spinner_category)
        edit_comment = findViewById(R.id.edit_comment)
        btn_save = findViewById(R.id.btn_save)
        iv_header_logo = findViewById(R.id.iv_back)
        imgUserPic = findViewById(R.id.img_user_pic)
        pb_ut_confirm = findViewById(R.id.pb_ut_confirm)
        edt_upload_document = findViewById(R.id.edt_upload_document)

        startHandler()

        itemId = intent.getIntExtra("ForEdit", 0)

        if (intent.hasExtra("amount")) {
            amount = intent.getFloatExtra("amount", 0f)
            datee = intent.getLongExtra("date", 0)
            des = intent.getStringExtra("des")
            edit_amount!!.setText(amount.toString())
            edit_comment!!.setText(des)

            Log.d("TAG", "onCreate: on express " + intent.getStringExtra("type"))
            if (intent.getStringExtra("type").equals("expense"))
                spinner_type!!.setSelection(1)
            else
                spinner_type!!.setSelection(0)

            val sdf = SimpleDateFormat(myFormat, Locale.US)
            edit_date!!.setText(sdf.format(datee))

        }


        //Changed Category on 28-01-2023 added type wise static category and remove the API call
        //getBookKeepingCate()
        pb_ut_confirm!!.visibility = View.GONE

        categoryStrings.clear()

        adapterCategory = ArrayAdapter(
            this@BookKeepingItemEditActivityAgent,
            R.layout.simple_spinner_item,
            categoryStrings
        )
        spinner_category!!.adapter = adapterCategory

        getUserInfo()

        Util.setEditReadOnly(edit_date!!, true, InputType.TYPE_NULL)

        edit_date?.setOnClickListener {
            val dateDialog = DatePickerDialog(
                this,
                { view, year, monthOfYear, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val sdf = SimpleDateFormat(myFormat, Locale.US)
                    edit_date!!.setText(sdf.format(cal.time))

                    val formatter2 = SimpleDateFormat("yyyy-MM-dd")
                    val todayString2 = formatter2.format(cal.time)
                    startDate = todayString2
                },
                year,
                month,
                day
            )
            dateDialog.show()
            val cal1 = Calendar.getInstance()
            cal1.add(Calendar.DAY_OF_MONTH, 0)
            dateDialog.datePicker.maxDate = cal1.timeInMillis
            edit_date!!.error = null
        }

        spinner_type!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                type = typeStrings[position]

                categoryStrings.clear()
                getCategoryDetails(type)

                try {
                    Log.e("Extras", "category>> " + intent.getStringExtra("category"))
                    if (intent.getStringExtra("category")!!.isNotEmpty())
                        spinner_category!!.setSelection(
                            categoryStrings.indexOf(
                                intent.getStringExtra(
                                    "category"
                                )!!
                            )
                        )
                    else
                        spinner_category!!.setSelection(0)
                } catch (e: Exception) {
                    e.printStackTrace()
                    spinner_category!!.setSelection(0)

                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }


        spinner_category!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                category = categoryStrings[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        btn_save!!.setOnClickListener {
            try {
                if (itemId == null || itemId == 0) {
                    if (checkValidate()) {
                        addBookkeepingItm()
                    }
                } else if (checkValidateUpdate()) {
                    updateBookkeepingItm()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        edt_upload_document!!.setOnClickListener {
            showPictureDialog()
        }

        iv_header_logo!!.setOnClickListener {
            onBackPressed()
        }
    }

    var categoryList: ArrayList<CategoryListForBookKeepingResponseItem> = arrayListOf()

    private fun getCategoryDetails(type: String) {

        val userDetailService: ApiInterface =
            ApiClient(this@BookKeepingItemEditActivityAgent).provideService(ApiInterface::class.java)
        val apiCall: Observable<ArrayList<CategoryListForBookKeepingResponseItem>> =
            userDetailService.getBookKeepingCategory(Kotpref.userRole, type) //Test API Key

        RxAPICallHelper().call(
            apiCall,
            object : RxAPICallback<ArrayList<CategoryListForBookKeepingResponseItem>> {
                override fun onSuccess(userDetail: ArrayList<CategoryListForBookKeepingResponseItem>) {
                    categoryList.clear()
                    categoryList = userDetail

                    Log.e("Role", "userRoleName>> " + Gson().toJson(categoryList))

                    for (i in 0 until categoryList.size) {
                        categoryStrings.add(categoryList[i].name)
                    }
                    Log.e("Type", "Category >> " + Gson().toJson(categoryStrings))
                    adapterCategory = ArrayAdapter(
                        this@BookKeepingItemEditActivityAgent,
                        R.layout.simple_spinner_item,
                        categoryStrings
                    )
                    spinner_category!!.adapter = adapterCategory
                    if (categoryStrings.size > 0) {
                        spinner_category!!.setSelection(0)
                    }

                }

                override fun onFailed(t: Throwable) {
                    // show error
                    Log.e("onFailure", t.toString())
                    //  progressBarList?.visibility = View.GONE
                }
            })

    }

    private fun checkValidate(): Boolean {

        if (edit_date!!.text.toString().isEmpty()) {
            Toast.makeText(this, "Please choose Date", Toast.LENGTH_LONG).show()
            return false
        }
        if (edit_amount!!.text.toString().isEmpty()) {
            Toast.makeText(this, "Please Enter Amount", Toast.LENGTH_LONG).show()
            return false
        }

        return true

        /* return try {
             fileProfile?.length()!! > 0
         } catch (e: Exception) {
             e.printStackTrace()
             Toast.makeText(this, "Please Choose your bill", Toast.LENGTH_LONG).show()
             false
         }*/

    }

    private fun checkValidateUpdate(): Boolean {

        if (edit_date!!.text.toString().isEmpty()) {
            Toast.makeText(this, "Please choose Date", Toast.LENGTH_LONG).show()
            return false
        }
        if (edit_amount!!.text.toString().isEmpty()) {
            Toast.makeText(this, "Please Enter Amount", Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }

    private fun getBookKeepingCate() {
        val bookKeepingService: ApiInterface =
            ApiClient(this).provideService(ApiInterface::class.java)
        val apiCall: Observable<List<AgentBookKeepingGetCategoriesResponse>> =
            bookKeepingService.getBookKeepingCategories() //Test API Key

        RxAPICallHelper().call(
            apiCall,
            object : RxAPICallback<List<AgentBookKeepingGetCategoriesResponse>> {
                override fun onSuccess(response: List<AgentBookKeepingGetCategoriesResponse>) {
                    if (response[0].id != null) {
                        uid = response[0].id
                        categoryStrings.add(response[0].name!!)

                    }


                    //Changed Category on 28-01-2023 added type wise static category
                    categoryStrings.clear()

                    adapterCategory = ArrayAdapter(
                        this@BookKeepingItemEditActivityAgent,
                        R.layout.simple_spinner_item,
                        categoryStrings
                    )
                    spinner_category!!.adapter = adapterCategory
                    pb_ut_confirm!!.visibility = View.GONE

                }

                override fun onFailed(t: Throwable) {
                    // show error
                    Log.e("onFailure", t.toString())
                    //
                    pb_ut_confirm!!.visibility = View.GONE

                    try {
                        Util.apiFailure(this@BookKeepingItemEditActivityAgent, t)
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@BookKeepingItemEditActivityAgent,
                            getString(R.string.error_something),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
    }

    @SuppressLint("CheckResult")
    private fun addBookkeepingItm() {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )

            // {"date":"2022-03-15T13:37:53.344Z","amount":123,"type":"earnings","description":"Test new","category":7172}
            // {"amount":222.0,"date":"2022-03-17","description":"test","type":"Earnings"}
            val credentials = BookkeepingAddAgent()
            credentials.date = startDate
            credentials.amount = edit_amount!!.text.toString().toFloat()
            credentials.type = type.lowercase()
            credentials.description = edit_comment!!.text.toString()
            credentials.category = categoryList[spinner_category!!.selectedItemPosition].id
            //Added New param on 25-01-2023
            credentials.userId = Kotpref.userId
            pb_ut_confirm!!.visibility = View.VISIBLE

            val validateService: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<AgentBookKeepingAddResponce> =
                validateService.addBookkeepingItem(credentials)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", Gson().toJson(it))

                    if (it.responseDto!!.responseCode == 200) {
                        //Toast.makeText(applicationContext, it.responseCode, Toast.LENGTH_SHORT).show()
                        it.data?.let { bookKeeping ->
                            itemId = bookKeeping.id
                        }
                        if (itemId != 0) {
                            try {
                                if (fileProfile?.length()!! > 0) {
                                    profileUploadApi("$itemId")
                                }else{
                                    this@BookKeepingItemEditActivityAgent.window
                                        .clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                                    pb_ut_confirm!!.visibility = View.GONE
                                    finish()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                finish()
                            }
                        } else {
                            pb_ut_confirm!!.visibility = View.GONE
                            finish()
                        }
                        //
                    }
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
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
            Toast.makeText(
                applicationContext,
                getString(R.string.error_network),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getCategoryValue(selectedItem: String): Int? {
        /*
        Subscription Commission
        Rent
        Other Incomes
        Agent Commission
        Other Expenses
        Subscriptions
        * */


        when (selectedItem) {
            "Subscription Commission" -> {
                return 1
            }
            "Rent" -> {
                return 2
            }
            "Other Incomes" -> {
                return 3
            }
            "Agent Commission" -> {
                return 4
            }
            "Other Expenses" -> {
                return 5
            }
            "Subscriptions" -> {
                return 6
            }
            else -> {
                return 0

            }
        }

    }

    @SuppressLint("CheckResult")
    private fun updateBookkeepingItm() {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {
            getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )

            val credentials = BookkeepingUpdateAgent()
            credentials.date = startDate
            credentials.amount = edit_amount!!.text.toString().toFloat()
            credentials.type = type.lowercase()
            credentials.description = edit_comment!!.text.toString()
            credentials.category = categoryList[spinner_category!!.selectedItemPosition].id
            credentials.userId = Kotpref.userId.toInt()
            credentials.id = itemId
            pb_ut_confirm!!.visibility = View.VISIBLE

            val validateService: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)
            val apiCall: Observable<AgentBookKeepingAddResponce> =
                validateService.updateBookkeepingItem(credentials)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", it.responseCode.toString())

                    if (it.responseCode == "200") {
                        //Toast.makeText(applicationContext, it.responseCode, Toast.LENGTH_SHORT).show()
                        //finish()
                        try {
                            if (fileProfile?.length()!! > 0) {
                                profileUploadApi("${it.id}")
                            } else {
                                finish()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            finish()

                        }

                    }

                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
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
            Toast.makeText(
                applicationContext,
                getString(R.string.error_network),
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private val GALLERY = 1
    private val CAMERA = 2
    private var fileProfile: File? = null
    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this@BookKeepingItemEditActivityAgent)
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
        if (resultCode == RESULT_CANCELED) {
            return
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                val contentURI = data.data
                fileProfile =
                    Util.getRealPathFromUri(this@BookKeepingItemEditActivityAgent, contentURI!!)
                //profileUploadApi()
                imgUserPic!!.visibility = View.VISIBLE
                imgUserPic!!.setImageBitmap(BitmapFactory.decodeFile(fileProfile!!.absolutePath))
            }
        } else if (requestCode == CAMERA) {
            if (data != null) {
                val thumbnail = data.extras!!.get("data") as Bitmap
                fileProfile =
                    Util.bitmapToFile(
                        this@BookKeepingItemEditActivityAgent,
                        thumbnail,
                        getString(R.string.profile_pic)
                    )
                imgUserPic!!.visibility = View.VISIBLE

                imgUserPic!!.setImageBitmap(BitmapFactory.decodeFile(fileProfile!!.absolutePath))
                //profileUploadApi()
            }
        }
    }

    private fun fetchImages() {
        if (NetworkConnection.isNetworkConnected(this@BookKeepingItemEditActivityAgent)) {

            val credentials = FetchDocumentCredential()
            credentials.userId = Kotpref.userId

            val signatureApi: ApiInterface =
                ApiClient(this@BookKeepingItemEditActivityAgent).provideService(ApiInterface::class.java)
            val apiCall: Observable<FetchDocumentResponse> =
                signatureApi.fetchDocument(credentials)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("onSuccess", it.responseDto!!.responseDescription.toString())

                    if (it.responseDto!!.responseCode.equals(200) && it.data != null) {
                        ProfileFragmentAgent.listUserImages = it.data!!

                    } else {
                        Toast.makeText(
                            this@BookKeepingItemEditActivityAgent,
                            it.responseDto!!.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                },
                    { e ->
                        Log.e("onFailure", e.toString())

                        e.message?.let {
                            Toast.makeText(
                                this@BookKeepingItemEditActivityAgent,
                                it,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
        } else {
            Toast.makeText(
                this@BookKeepingItemEditActivityAgent,
                getString(R.string.error_network),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun profileUploadApi(moneyBookId: String) {
        if (NetworkConnection.isNetworkConnected(this@BookKeepingItemEditActivityAgent)) {
            //pbProfile!!.visibility = View.VISIBLE
            this@BookKeepingItemEditActivityAgent.window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            val builder = MultipartBody.Builder().setType(MultipartBody.FORM)

            builder.addFormDataPart("moneyBookId", moneyBookId)
            builder.addFormDataPart(
                "uploadFileType",
                this@BookKeepingItemEditActivityAgent.getString(R.string.key_money_book)
            )

            builder.addFormDataPart(
                "file", fileProfile!!.name,
                fileProfile!!.asRequestBody(MultipartBody.FORM)
            )
            val requestBody: RequestBody = builder.build()
            val uploadSign: ApiInterface =
                ApiClient(this@BookKeepingItemEditActivityAgent).provideService(ApiInterface::class.java)

            val apiCallUploadSign: Observable<ResponseDtoResponse> =
                uploadSign.uploadDocument(requestBody)

            apiCallUploadSign.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({

                    if (it.responseDto?.responseCode!!.equals(200)) {

                        // pbProfile!!.visibility = View.GONE
                        this@BookKeepingItemEditActivityAgent.window
                            .clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        pb_ut_confirm!!.visibility = View.GONE

                        finish()
                        //imgUserPic!!.setImageBitmap(BitmapFactory.decodeFile(fileProfile!!.absolutePath))

                    }
                },
                    { e ->
                        this@BookKeepingItemEditActivityAgent.window
                            .clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        //pbProfile!!.visibility = View.GONE

                        Log.e("error", e.message.toString())
                    })
        } else {
            Toast.makeText(
                this@BookKeepingItemEditActivityAgent,
                getString(R.string.error_network),
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun getUserInfo() {
        //Create retrofit Service
        val credentials = UserDetailCredential()

        credentials.userId = Kotpref.userId
        val userDetailService: ApiInterface =
            ApiClient(this@BookKeepingItemEditActivityAgent).provideService(ApiInterface::class.java)
        val apiCall: Observable<UserDetailResponse> =
            userDetailService.getUserDetail(credentials) //Test API Key

        RxAPICallHelper().call(apiCall, object : RxAPICallback<UserDetailResponse> {
            override fun onSuccess(userDetail: UserDetailResponse) {
                userDetailResponse = userDetail.data!!

                Log.e("Role", "userRoleName>> " + userDetailResponse.userRoleName)
                setRefreshSelection()

            }

            override fun onFailed(t: Throwable) {
                // show error
                Log.e("onFailure", t.toString())
                //  progressBarList?.visibility = View.GONE
            }
        })

    }

    private fun setRefreshSelection() {
        var adapter_type = ArrayAdapter(this, R.layout.simple_spinner_item, typeStrings)
        spinner_type!!.adapter = adapter_type
        spinner_type!!.setSelection(0)


        if (intent.hasExtra("amount")) {
            amount = intent.getFloatExtra("amount", 0f)
            datee = intent.getLongExtra("date", 0)
            des = intent.getStringExtra("des")
            edit_amount!!.setText(amount.toString())
            edit_comment!!.setText(des)

            Log.d("TAG", "onCreate: on express " + intent.getStringExtra("type"))
            if (intent.getStringExtra("type").equals("expense"))
                spinner_type!!.setSelection(1)
            else
                spinner_type!!.setSelection(0)

            val sdf = SimpleDateFormat(myFormat, Locale.US)
            edit_date!!.setText(sdf.format(datee))

        }


    }

    companion object {
        var userDetailResponse = UserDetail()

    }

}