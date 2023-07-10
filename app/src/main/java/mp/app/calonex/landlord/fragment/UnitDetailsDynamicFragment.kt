package mp.app.calonex.landlord.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.telephony.PhoneNumberUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.JsonElement
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_new_property_first_screen.*
import kotlinx.android.synthetic.main.fragment_dynamic.*
import mp.app.calonex.R
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.UsPhoneNumberFormatter
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.AddNewPropertyFirstScreen.Companion.isEditing
import mp.app.calonex.landlord.activity.AddNewPropertyFirstScreen.Companion.propertyIdFirst
import mp.app.calonex.landlord.activity.HomeActivityCx
import mp.app.calonex.landlord.activity.PropertyDetailScreen
import mp.app.calonex.landlord.activity.UnitDescriptionDynamicTabActivity.Companion.moveNext
import mp.app.calonex.landlord.adapter.*
import mp.app.calonex.landlord.adapter.PropertyListAdapter.Companion.propertyDetailResponse
import mp.app.calonex.landlord.model.*
import mp.app.calonex.landlord.response.AddUnitDetailResponse
import mp.app.calonex.service.FeaturesService
import mp.app.calonex.tenant.response.ResponseDtoResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class UnitDetailsDynamicFragment : Fragment() {

    private var rvFeature: RecyclerView? = null
    private var showonmarketplace: CheckBox? = null
    private var rvUtilities: RecyclerView? = null
    private var rvImg: RecyclerView? = null
    private var rv_photos_from_api: RecyclerView? = null
    private var edit_date: TextView? = null
    private var lease_start_date: TextView? = null
    private var btn_save_unit: TextView? = null
    private var unit_description_skip: TextView? = null
    private var spinner_unit_type: Spinner? = null
    private var spinner_bathroom_type: Spinner? = null
    private var spinner_unit_status: Spinner? = null
    private var edit_square_foot: TextInputEditText? = null
    private var addPhoto: TextView? = null

    private var edit_unit_no: TextInputEditText? = null
    private var edit_rent_month: TextInputEditText? = null

    private var edit_increase_yearly: TextInputEditText? = null
    private var edit_application_fee: TextInputEditText? = null

    private var edit_months_free: TextInputEditText? = null
    private var edit_security_deposit: TextInputEditText? = null
    private var edit_discount: TextInputEditText? = null
    private var edit_late_fee_after_due_date: TextInputEditText? = null
    private var edit_tenant_first_name: TextInputEditText? = null
    private var edit_tenant_middle_name: TextInputEditText? = null
    private var edit_tenant_last_name: TextInputEditText? = null
    private var edit_tenant_email: TextInputEditText? = null
    private var edit_tenant_phone: TextInputEditText? = null

    val unitTypeList: MutableList<UnitType> = ArrayList()
    var mArrayUri = ArrayList<Uri>()
    var mArrayUnitImgLiveUri = ArrayList<Uri>()

    var unitTypeId: Long = 0
    var bathroomTypeId: Long = 0
    var unitNumber: String? = null
    var unitStatus: String? = ""
    var isUnitIdAvailable: Boolean = false

    var fragPosition: Int = 0
    var fragIsLast: Boolean = false
    var unitId: String? = null

    var dateAvailableTimeStamp: String? = null
    var leaseStartDateTimeStamp: String? = null

    lateinit var appContext: Context
    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext = context

    }

    companion object {
        fun addFrag(value: Int, isLast: Boolean): Fragment {
            var fragment = UnitDetailsDynamicFragment()
            var bundle = Bundle()
            bundle.putInt("position", value)
            bundle.putBoolean("isLast", isLast)
            fragment.arguments = bundle
            return fragment
        }

        var unitFeatureIds = ArrayList<Long>()
        var unitUtilitiesIds = ArrayList<Long>()
        var jsonElementsFeature: JsonElement? = null
        var jsonElementsUtility: JsonElement? = null

        var Unit = object : FeatureCheckboxCallback {
            override fun sendState(
                state: Boolean,
                buildingFeatureId: Long,
                buildingFeature: String
            ) {
                if (state) {
                    unitFeatureIds.add(buildingFeatureId)
                } else {
                    if (unitFeatureIds.contains(buildingFeatureId)) {
                        unitFeatureIds.remove(buildingFeatureId)
                    }
                }
                jsonElementsFeature = Gson().toJsonTree(unitFeatureIds)
            }
        }
        var UnitUtilities = object : FeatureCheckboxCallback {
            override fun sendState(
                state: Boolean,
                buildingFeatureId: Long,
                buildingFeature: String
            ) {
                if (state) {
                    unitUtilitiesIds.add(buildingFeatureId)
                } else {
                    if (unitUtilitiesIds.contains(buildingFeatureId)) {
                        unitUtilitiesIds.remove(buildingFeatureId)
                    }
                }
                jsonElementsUtility = Gson().toJsonTree(unitUtilitiesIds)
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        retainInstance = true
        val rootView = inflater.inflate(R.layout.fragment_dynamic, container, false)

        fragPosition = arguments?.getInt("position", 0)?.plus(1)!!
        fragIsLast = arguments?.getBoolean("isLast", false)!!

        rvFeature = rootView.findViewById<View>(R.id.rv_unit_feature) as RecyclerView
        showonmarketplace = rootView.findViewById<View>(R.id.showonmarketplace) as CheckBox
        rvUtilities = rootView.findViewById<View>(R.id.rv_utilities_included) as RecyclerView
        rvImg = rootView.findViewById<View>(R.id.rv_photos) as RecyclerView
        rv_photos_from_api = rootView.findViewById<View>(R.id.rv_photos_from_api) as RecyclerView
        edit_date = rootView.findViewById<View>(R.id.edit_date) as TextView
        edit_unit_no = rootView.findViewById<View>(R.id.edit_unit_no) as TextInputEditText
        edit_rent_month = rootView.findViewById<View>(R.id.edit_rent_month) as TextInputEditText
        edit_increase_yearly =
            rootView.findViewById<View>(R.id.edit_increase_yearly) as TextInputEditText
        edit_application_fee =
            rootView.findViewById<View>(R.id.edit_application_fee) as TextInputEditText
        edit_square_foot = rootView.findViewById<View>(R.id.edit_square_foot) as TextInputEditText

        edit_months_free = rootView.findViewById<View>(R.id.edit_months_free) as TextInputEditText
        edit_security_deposit =
            rootView.findViewById<View>(R.id.edit_security_deposit) as TextInputEditText
        edit_discount =
            rootView.findViewById<View>(R.id.edit_discount) as TextInputEditText
        edit_late_fee_after_due_date =
            rootView.findViewById<View>(R.id.edit_late_fee_after_due_date) as TextInputEditText

        edit_tenant_first_name =
            rootView.findViewById<View>(R.id.edit_tenant_first_name) as TextInputEditText
        edit_tenant_middle_name =
            rootView.findViewById<View>(R.id.edit_tenant_middle_name) as TextInputEditText
        edit_tenant_last_name =
            rootView.findViewById<View>(R.id.edit_tenant_last_name) as TextInputEditText
        edit_tenant_email = rootView.findViewById<View>(R.id.edit_tenant_email) as TextInputEditText
        edit_tenant_phone = rootView.findViewById<View>(R.id.edit_tenant_phone) as TextInputEditText
        val addLineNumberFormatter = UsPhoneNumberFormatter(edit_tenant_phone!!)
        edit_tenant_phone!!.addTextChangedListener(addLineNumberFormatter)

        lease_start_date = rootView.findViewById<View>(R.id.lease_start_date) as TextView
        unit_description_skip = rootView.findViewById<View>(R.id.unit_description_skip) as TextView
        btn_save_unit = rootView.findViewById<View>(R.id.btn_save_unit) as Button
        addPhoto = rootView.findViewById<View>(R.id.add_photo) as TextView
        spinner_unit_type = rootView.findViewById<View>(R.id.spinner_unit_type) as Spinner
        spinner_bathroom_type = rootView.findViewById<View>(R.id.spinner_bathroom_type) as Spinner
        spinner_unit_status = rootView.findViewById<View>(R.id.spinner_unit_status) as Spinner

        rvFeature!!.layoutManager = GridLayoutManager(appContext, 2)
        rvUtilities!!.layoutManager = GridLayoutManager(appContext, 2)

        edit_rent_month!!.setText("0.00")
        edit_increase_yearly!!.setText("0.00")
        edit_application_fee!!.setText("0.00")
        edit_square_foot!!.setText("0")


        if (!Kotpref.linkBrokerLicenceNo.toString().trim().isNullOrEmpty()) {
            spinner_unit_status!!.setSelection(1)
            unitStatus = "Vacant"
            //view_unit_vacant!!.visibility = View.VISIBLE
            //view_unit_occupied!!.visibility = View.GONE
        }

        if (Kotpref.isAddPropertyFromLd) {
            lease_start_date!!.visibility = View.GONE
            leaseStartDateTimeStamp = ""
        }

        if (fragIsLast) {
            unit_description_skip!!.setText("Finish")
            unit_description_skip!!.visibility = View.INVISIBLE
        } else {
            unit_description_skip!!.setText("Skip")
        }

        edit_unit_no!!.setText("" + fragPosition)

        var featureAdapter =
            FeaturesService.allModel?.unitFeatureListResponse?.data?.let {
                UnitFeatureAdapter(
                    it,
                    Unit
                )
            }

        var utilitiesAdapter =
            FeaturesService.allModel?.unitUtilitiesResponse?.data?.let {
                UnitUtilitiesAdapter(
                    it,
                    UnitUtilities
                )
            }

        rvFeature?.adapter = featureAdapter
        rvUtilities?.adapter = utilitiesAdapter

        unitTypeList.add(0, UnitType(-1, "Select Unit Type"))

        val bathroomTypeList: MutableList<BathroomType> = ArrayList()
        bathroomTypeList.add(0, BathroomType(-1, "Select Bathroom Type"))


        for (i in 1..FeaturesService.allModel!!.unitTypeListResponse.data!!.size) {
            unitTypeList.add(i, FeaturesService.allModel!!.unitTypeListResponse.data!![i - 1])
        }

        for (i in 1..FeaturesService.allModel!!.bathroomTypeListResponse.data!!.size) {
            bathroomTypeList.add(
                i,
                FeaturesService.allModel!!.bathroomTypeListResponse.data!![i - 1]
            )
        }

        Log.e("Unit Type", "Final Unit Type list===> " + Gson().toJson(unitTypeList))
        Log.e("Bathroom Type", "Final Bathroom Type list===> " + Gson().toJson(bathroomTypeList))

        if (spinner_unit_type != null) {
            var spinnerAdapter =
                CustomUnitTypeSpinnerAdapter(appContext, unitTypeList)
            spinner_unit_type!!.adapter = spinnerAdapter
        }

        if (spinner_bathroom_type != null) {
            var spinnerAdapter: CustomBathroomTypeSpinnerAdapter =
                CustomBathroomTypeSpinnerAdapter(appContext, bathroomTypeList)
            spinner_bathroom_type!!.adapter = spinnerAdapter
        }

        val unitStatusList =
            arrayOf("Select Unit Type", "Vacant", "Occupied")

        if (spinner_unit_status != null) {
            val adapter = ArrayAdapter(
                appContext,
                android.R.layout.simple_spinner_item, unitStatusList
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner_unit_status!!.adapter = adapter
        }

        spinner_unit_status?.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position > 0) {
                    unitStatus = unitStatusList[position]

                    if (unitStatus.equals("Vacant", true)) {
                        view_unit_vacant.visibility = View.VISIBLE
                        view_unit_occupied.visibility = View.GONE
                        /*edit_security_deposit!!.setText("0.00")
                        edit_months_free!!.setText("0")
                        edit_discount!!.setText("0")
                        edit_late_fee_after_due_date!!.setText("0.00")*/
                    } else if (unitStatus!!.contains("Occupied", true)) {
                        view_unit_occupied.visibility = View.VISIBLE
                        view_unit_vacant.visibility = View.GONE
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }

        })


        spinner_unit_type?.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {

                if (position > 0) {
                    unitTypeId = unitTypeList[position].unitTypeId
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }

        })


        spinner_bathroom_type?.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position > 0) {
                    bathroomTypeId = bathroomTypeList[position].bathroomTypeId
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }

        })

        if (isEditing) {
            val unitDetails = PropertyDetailScreen.propertyDetailResponseLocal
            val unitFileList = PropertyDetailScreen.listPropertyFilesLocal

            if (unitDetails != null) {
                updateUi(unitDetails)
            }
        }

        actionComponent()

        return rootView
    }

    private fun updateUi(details: PropertyDetail) {

        var unitDetails: UnitDetailsPD? = null

        if (fragPosition <= details.propertyUnitDetailsDTO.size) {
            unitDetails = details.propertyUnitDetailsDTO[fragPosition - 1]
        }



        if (unitDetails != null) {
            showonmarketplace!!.isChecked = unitDetails.published
            edit_unit_no!!.setText(unitDetails.unitNumber)
            edit_rent_month?.setText(unitDetails.rentPerMonth)
            edit_increase_yearly?.setText(unitDetails.increaseYearlyPercentage)
            edit_application_fee?.setText(unitDetails.applicationFees)
            spinner_bathroom_type?.setSelection(unitDetails.bathroomTypeID.toInt())

            Log.e("Unit Data", "unitDetails===> " + Gson().toJson(unitDetails))

            try {
                if (unitTypeList.size > 0) {
                    for (i in 0 until unitTypeList.size) {
                        Log.e(
                            "Check Unit Type Id",
                            "Type id in spinner = ${unitTypeList[i].unitTypeId.toInt()}, Type ID = ${unitDetails.unitTypeId}."
                        )
                        if (unitTypeList[i].unitTypeId.toInt() == unitDetails.unitTypeId.toInt()) {
                            spinner_unit_type!!.setSelection(i)
                            unitTypeId = unitTypeList[i].unitTypeId
                        }
                    }
                }
            } catch (e: Exception) {
            }

            //spinner_unit_type?.setSelection()

            if (unitDetails.dateOfAvailibility != null && !unitDetails.dateOfAvailibility.equals("0")) {
                Util.getDateTimeStamp(unitDetails.dateOfAvailibility)
                edit_date?.setText(Util.getDateTimeStamp(unitDetails.dateOfAvailibility))
            }

//s,nskjdjhsdg

            edit_square_foot?.setText(Objects.requireNonNull(unitDetails.squareFootArea))

            unitId = unitDetails.propertyUnitID

            // Corrected the index for Vacant or Occupied
            // The app was crashing because of this
            var unitStatusNumber: Int = unitDetails.unitStatus.toInt()
            if (unitStatusNumber == 1)
                spinner_unit_status?.setSelection(1)
            else if (unitStatusNumber == 3)
                spinner_unit_status?.setSelection(2)

            //spinner_unit_status?.setSelection(Objects.requireNonNull(unitDetails.unitStatus.toInt()) - 1)


            if (Objects.requireNonNull(unitDetails.unitStatus).toInt() == 1) {
                if (unitDetails.monthsFree != null)
                    edit_months_free?.setText(Objects.requireNonNull(unitDetails.monthsFree))
                if (unitDetails.securityAmount != null)
                    edit_security_deposit?.setText(Objects.requireNonNull(unitDetails.securityAmount))
                if (unitDetails.discount != null)
                    edit_discount?.setText((unitDetails.discount))
                /* if (unitDetails.leaseStartDate != null) {
                     lease_start_date?.setText(
                         Util.getDateTimeStamp(
                             Objects.requireNonNull(
                                 unitDetails.leaseStartDate
                             )
                         )
                     )
                 }*/
                if (unitDetails.lateFee != null)
                    edit_late_fee_after_due_date?.setText(Objects.requireNonNull(unitDetails.lateFee))
            } else {

                try {


                    //occupied
                    if (!unitDetails.tenantFirstName.isNullOrEmpty())
                        edit_tenant_first_name?.setText(Objects.requireNonNull(unitDetails.tenantFirstName))


                    if (!unitDetails.tenantLastName.isNullOrEmpty())
                        edit_tenant_last_name!!.setText(Objects.requireNonNull(unitDetails.tenantLastName!!))

                    if (!unitDetails.tenantEmailId.isNullOrEmpty())
                        edit_tenant_email!!.setText(Objects.requireNonNull(unitDetails.tenantEmailId!!))
                    if (unitDetails.tenantPhoneNumber.isNullOrEmpty()) {
                        edit_tenant_phone!!.setText("")
                    } else {
                        edit_tenant_phone!!.setText(
                            Objects.requireNonNull(
                                PhoneNumberUtils.formatNumber(
                                    unitDetails.tenantPhoneNumber!!,
                                    "US"
                                )
                            )
                        )

                    }
                } catch (e: Exception) {
                    //   Log.e("tag",e.localizedMessage)
                    e.printStackTrace()
                }

            }

            try {
                if (unitTypeList.size > 0) {
                    for (i in 0 until unitTypeList.size) {
                        if (unitTypeList[i].unitTypeId.toInt() == propertyDetailResponse.propertyUnitDetailsDTO[fragPosition - 1].unitTypeId.toInt()) {
                            spinner_unit_type!!.setSelection(i)
                            unitTypeId = unitTypeList[i].unitTypeId
                        }
                    }
                }
            } catch (e: Exception) {
            }

        }


        Handler().postDelayed(
            {
                var unitFeatureDto = details?.unitFeatureDTO
                if (unitFeatureDto != null) {
                    for (i in 0 until FeaturesService.allModel?.unitFeatureListResponse!!.data?.size!!) {
                        val viewHolder = rvFeature?.findViewHolderForAdapterPosition(i)

                        val textView =
                            (viewHolder as? UnitFeatureAdapter.ViewHolder)?.txtSwitch?.text
                        val switch =
                            (viewHolder as? UnitFeatureAdapter.ViewHolder)?.switchFeature!!

                        for (j in 0 until unitFeatureDto.size) {
                            if (unitFeatureDto[j].unitFeature.equals(textView) && unitFeatureDto[j].propertyUnitID.equals(
                                    unitId
                                )
                            ) {
                                //rvFeature?.findViewHolderForAdapterPosition(i)?.itemView?.performClick()
                                switch!!.isChecked = true
                            }
                        }
                    }
                }

                var unitUtilitiesDto = details?.unitUtilitiesDTO
                if (unitUtilitiesDto != null) {
                    for (i in 0 until FeaturesService.allModel?.unitUtilitiesResponse!!.data?.size!!) {
                        val viewHolder = rvUtilities?.findViewHolderForAdapterPosition(i)

                        val textView =
                            (viewHolder as? UnitUtilitiesAdapter.ViewHolder)?.txtSwitch?.text
                        val switch =
                            (viewHolder as? UnitUtilitiesAdapter.ViewHolder)?.switchFeature!!

                        for (j in 0 until unitUtilitiesDto.size) {
                            if (unitUtilitiesDto[j].utilitieName.equals(textView) && unitUtilitiesDto[j].propertyUnitID.equals(
                                    unitId
                                )
                            ) {
                                //rvUtilities?.findViewHolderForAdapterPosition(i)?.itemView?.performClick()
                                switch!!.isChecked = true
                            }
                        }
                    }
                }


            }, 2000
        )


        mArrayUnitImgLiveUri.clear()
        try {
            for (i in 0 until unitDetails!!.fileList.size) {
                if (unitDetails!!.fileList[i].uploadFileType == getString(R.string.key_img_unit)) {
                    mArrayUnitImgLiveUri.add(Uri.parse(getString(R.string.base_url_img2) + unitDetails!!.fileList[i].fileName))
                }
            }
        } catch (e: java.lang.Exception) {
            Log.e("e", "" + e.toString())
        }


        var adapter: ImgListForLandlordAdapter? = null
        if (mArrayUnitImgLiveUri.size > 0) {
            adapter = ImgListForLandlordAdapter(requireContext(), mArrayUnitImgLiveUri)
            rv_photos_from_api?.layoutManager = GridLayoutManager(requireContext(), 3)
            rv_photos_from_api?.adapter = adapter
        }

        Log.e("Unit Image List", "mArrayUnitImgLiveUri===>" + Gson().toJson(mArrayUnitImgLiveUri))
    }

    var fileUnitImageList = ArrayList<File>()
    internal var REQUEST_GALLERY_CODE = 200
    internal lateinit var imagesEncodedList: MutableList<String>
    private var adapter: ImgListForLandlordAdapter? = null
    var listOfMultipart = ArrayList<MultipartBody.Part>()


    @Deprecated("Deprecated in Java")
    @SuppressLint("NewApi")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.e("Tag", "On Activity Result===>")
        try {
            // When an Image is picked
            if (requestCode == REQUEST_GALLERY_CODE && resultCode == Activity.RESULT_OK
                && null != data
            ) {
                // Get the Image from data
                val filePathColumn = arrayOf(MediaStore.MediaColumns.DATA)
                var filePath: String?
                var uri: Uri
                imagesEncodedList = ArrayList()
                rvImg?.layoutManager = GridLayoutManager(requireContext(), 3)

                if (data.data != null) {

                    uri = data.data!!
                    filePath = getRealPathFromUri(uri)
                    var file = File(filePath)

                    Log.e("Upload Image Screen", "filePath=> $filePath")

                    listOfMultipart.add(prepareFilePart("files", file))

                    val mImageUri = data.data

                    if (mImageUri != null) {
                        mArrayUri.add(mImageUri)
                    }
                    val set = HashSet<Uri>(mArrayUri)
                    mArrayUri.clear()
                    mArrayUri.addAll(set)
                    adapter = ImgListForLandlordAdapter(
                        requireContext(),
                        mArrayUri
                    )

                    rvImg?.adapter = adapter
                } else {
                    if (data.clipData != null) {
                        val mClipData = data.clipData

                        for (i in 0 until mClipData!!.itemCount) {

                            val item = mClipData.getItemAt(i)
                            val uri = item.uri
                            mArrayUri.add(uri)
                            val cursor =
                                requireActivity().contentResolver.query(
                                    uri,
                                    filePathColumn,
                                    null,
                                    null,
                                    null
                                )
                            // Move to first row
                            cursor!!.moveToFirst()

                            filePath = uri.path

                            imagesEncodedList.add(filePath!!)

                            var file = File(filePath)

                            Log.e("FilePath", "filePath=" + filePath);

                            listOfMultipart.add(prepareFilePart("files", file))

                            cursor.close()
                            val set = HashSet<Uri>(mArrayUri)
                            mArrayUri.clear()
                            mArrayUri.addAll(set)
                            adapter = ImgListForLandlordAdapter(
                                requireContext(),
                                mArrayUri
                            )
                            rvImg?.adapter = adapter

                        }
                        Log.e(
                            "LOG_TAG",
                            "Selected Images" + mArrayUri.size
                        )
                    }
                }

            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_LONG)
                .show()
        }

        super.onActivityResult(requestCode, resultCode, data)
    }


    @NonNull
    private fun prepareFilePart(
        partName: String,
        file: File
    ): MultipartBody.Part {


        var requestFile: RequestBody =
            file.asRequestBody("image/*".toMediaTypeOrNull())

        var propertyImagePart: MultipartBody.Part? = null
        propertyImagePart = MultipartBody.Part.createFormData("files", file.name, requestFile)
        return propertyImagePart
    }


    private fun getRealPathFromUri(
        contentUri: Uri
    ): String {
        val cursor =
            requireActivity().contentResolver.query(contentUri, null, null, null, null)

        if (cursor == null) {
            return contentUri.path.toString()
        } else {
            cursor.moveToFirst()
            var index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            return cursor.getString(index)
        }

    }

    private fun actionComponent() {

        val cal = Calendar.getInstance()
        val calLease = Calendar.getInstance()

        addPhoto?.setOnClickListener { view ->
            Log.e("Tag", "mArrayUnitUri==>> " + mArrayUri.size)
            if ((mArrayUri.size + mArrayUnitImgLiveUri.size) >= 8) {
                Toast.makeText(requireContext(), "Maximum 8 images are allowed.", Toast.LENGTH_LONG)
                    .show()
            } else {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, REQUEST_GALLERY_CODE)
            }
        }

        edit_date!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val year = cal.get(Calendar.YEAR)
                val month = cal.get(Calendar.MONTH)
                val day = cal.get(Calendar.DAY_OF_MONTH)

                val dpd = DatePickerDialog(
                    appContext,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        cal.set(Calendar.YEAR, year)
                        cal.set(Calendar.MONTH, monthOfYear)
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                        val myFormat = "MMMM dd yyyy" // mention the format you need
                        val sdf = SimpleDateFormat(myFormat, Locale.US)
                        edit_date!!.text = sdf.format(cal.time)
                        dateAvailableTimeStamp = cal.timeInMillis.toString()

                    },
                    year,
                    month,
                    day
                )

                dpd.datePicker.minDate = System.currentTimeMillis() - 1000

                dpd.show()
            }

        })


        lease_start_date!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val year = calLease.get(Calendar.YEAR)
                val month = calLease.get(Calendar.MONTH)
                val day = calLease.get(Calendar.DAY_OF_MONTH)

                val dpd = DatePickerDialog(
                    appContext,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        calLease.set(Calendar.YEAR, year)
                        calLease.set(Calendar.MONTH, monthOfYear)
                        calLease.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                        val myFormat = "MMMM dd yyyy" // mention the format you need
                        val sdf = SimpleDateFormat(myFormat, Locale.US)
                        lease_start_date!!.text = sdf.format(calLease.time)
                        leaseStartDateTimeStamp = calLease.timeInMillis.toString()

                    },
                    year,
                    month,
                    day
                )

                dpd.datePicker.minDate = System.currentTimeMillis() - 1000

                dpd.show()
            }

        })


        btn_save_unit?.setOnClickListener { view ->

            if (edit_increase_yearly?.text!!.isEmpty()) {
                edit_increase_yearly?.setText("0")
            }

            if (edit_application_fee?.text!!.isEmpty()) {
                edit_application_fee?.setText("0")
            }

            if (Util.valueMandetory(appContext, edit_unit_no?.text.toString(), edit_unit_no!!)) {
                return@setOnClickListener
            }

            if (Util.valueMandetory(
                    appContext,
                    edit_rent_month?.text.toString(),
                    edit_rent_month!!
                )
            ) {
                return@setOnClickListener
            }

            if (edit_date?.text!!.isEmpty()) {
                edit_date?.error = getString(R.string.error_mandetory)
                edit_date?.requestFocus()
                return@setOnClickListener
            }

            if (Util.valueMandetory(
                    appContext,
                    edit_square_foot?.text.toString(),
                    edit_square_foot!!
                )
            ) {
                return@setOnClickListener
            }

            /* if (spinner_bathroom_type?.selectedItemPosition == 0) {
                 return@setOnClickListener
             }
 */

            if (spinner_bathroom_type?.selectedItemPosition == 0) {

                Toast.makeText(
                    appContext,
                    "Please select bathroom type",
                    Toast.LENGTH_SHORT
                ).show()

                spinner_bathroom_type?.requestFocus()


                return@setOnClickListener
            }


            if (spinner_unit_status?.selectedItemPosition == 0) {
                return@setOnClickListener
            } else if (spinner_unit_status?.selectedItem.toString().contains("Occupied", true)) {

                if (edit_tenant_first_name!!.text!!.isEmpty()) {
                    edit_tenant_first_name?.error = getString(R.string.error_mandetory)
                    edit_tenant_first_name?.requestFocus()
                    return@setOnClickListener
                }
                if (edit_tenant_last_name!!.text!!.isEmpty()) {
                    edit_tenant_last_name?.error = getString(R.string.error_mandetory)
                    edit_tenant_last_name?.requestFocus()
                    return@setOnClickListener
                }
                if (edit_tenant_email!!.text!!.isEmpty()) {
                    edit_tenant_email?.error = getString(R.string.error_mandetory)
                    edit_tenant_email?.requestFocus()
                    return@setOnClickListener
                }
                if (edit_tenant_phone!!.text!!.length != 14) {
                    edit_tenant_phone?.error = getString(R.string.error_phn)
                    edit_tenant_phone?.requestFocus()
                    return@setOnClickListener
                }


            } else {
                /*if (!Kotpref.isAddPropertyFromLd) {
                    if (lease_start_date!!.text.isEmpty()) {
                        lease_start_date?.error = getString(R.string.error_mandetory)
                        lease_start_date?.requestFocus()
                        return@setOnClickListener
                    }
                }*/
                // as per new logic no need of lease start date---- Debjit 7/3/2023

                if (edit_security_deposit!!.text!!.isEmpty()) {
                    edit_security_deposit?.error = getString(R.string.error_mandetory)
                    edit_security_deposit?.requestFocus()
                    return@setOnClickListener
                }
                if (edit_months_free!!.text!!.isEmpty()) {
                    edit_months_free?.error = getString(R.string.error_mandetory)
                    edit_months_free?.requestFocus()
                    return@setOnClickListener
                }
                if (edit_discount!!.text!!.isEmpty()) {
                    edit_discount!!.setText("0")
                }
            }

            val credentials = AddUnitCredentials()
            credentials.propertyId = propertyIdFirst
            credentials.numberOfUnits = edit_unit_no!!.text.toString()

            requireActivity().window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )

            add__progress.visibility = View.VISIBLE

            unitNumber = edit_unit_no!!.text.toString()
            credentials.propertyUnitDetails.addProperty("unitTypeId", unitTypeId)
            credentials.propertyUnitDetails.addProperty("unitNumber", unitNumber)
            credentials.propertyUnitDetails.addProperty("discount", 0)
            credentials.propertyUnitDetails.addProperty("published", showonmarketplace!!.isChecked)

            credentials.propertyUnitDetails.addProperty(
                "rentPerMonth",
                edit_rent_month?.text.toString().toDouble()
            )

            credentials.propertyUnitDetails.addProperty(
                "increaseYearlyPercentage",
                edit_increase_yearly?.text.toString().toDouble()
            )

            credentials.propertyUnitDetails.addProperty(
                "applicationFees",
                edit_application_fee?.text.toString().toDouble()
            )

            credentials.propertyUnitDetails.addProperty(
                "dateAvailable", dateAvailableTimeStamp
            )
            credentials.propertyUnitDetails.add(
                "unitFeatureIds",
                jsonElementsFeature
            )
            credentials.propertyUnitDetails.add(
                "unitUtilitiesIds",
                jsonElementsUtility
            )
            credentials.propertyUnitDetails.addProperty("unitAvailabilityStatus", false)
            credentials.propertyUnitDetails.addProperty("bathroomTypeID", bathroomTypeId)
            credentials.propertyUnitDetails.addProperty(
                "squareFootArea",
                edit_square_foot?.text.toString().toDouble()
            )
            credentials.propertyUnitDetails.addProperty("applicationFees", 0)

            credentials.propertyUnitDetails.addProperty("isActive", true)

            if (edit_months_free?.text!!.isEmpty()) {
                edit_months_free?.setText("0")
            }

            if (edit_security_deposit?.text!!.isEmpty()) {
                edit_security_deposit?.setText("0")
            }

            if (edit_discount?.text!!.isEmpty()) {
                edit_discount?.setText("0")
            }

            if (edit_late_fee_after_due_date?.text!!.isEmpty()) {
                edit_late_fee_after_due_date?.setText("0")
            }

            if (edit_security_deposit?.text!!.isEmpty()) {
                edit_security_deposit?.setText("0")
            }

            if (unitStatus!!.contains("Vacant", true)) {
                // Status Code for Vacant is 1
                credentials.propertyUnitDetails.addProperty("unitStatus", "1")
                /*  credentials.propertyUnitDetails.addProperty(
                      "leaseStartDate",
                      leaseStartDateTimeStamp
                  )*/
                // no need as per new requirement
                credentials.propertyUnitDetails.addProperty(
                    "monthsFree",
                    edit_months_free?.text.toString()
                )
                credentials.propertyUnitDetails.addProperty(
                    "securityAmount",
                    edit_security_deposit?.text.toString()
                )
                credentials.propertyUnitDetails.addProperty(
                    "discount",
                    edit_discount?.text.toString()
                )
                credentials.propertyUnitDetails.addProperty(
                    "lateFee",
                    edit_late_fee_after_due_date?.text.toString()
                )
            }
            credentials.propertyUnitDetails.addProperty(
                "userCatalogId",
                Kotpref.userId
            )
            if (unitStatus!!.contains("occupied", true)) {
                // Status Code for OCCUPIED_OUTSIDE_CX is 2
                // Status Code for OCCUPIED_INSIDE_CX is 3

                credentials.propertyUnitDetails.addProperty("unitStatus", "2")
                credentials.propertyUnitDetails.addProperty(
                    "tenantFirstName",
                    edit_tenant_first_name?.text.toString()
                )
                credentials.propertyUnitDetails.addProperty(
                    "tenantMiddleName",
                    edit_tenant_middle_name?.text.toString()
                )
                credentials.propertyUnitDetails.addProperty(
                    "tenantLastName",
                    edit_tenant_last_name?.text.toString()
                )
                credentials.propertyUnitDetails.addProperty(
                    "tenantEmailId",
                    edit_tenant_email?.text.toString()
                )

                credentials.propertyUnitDetails.addProperty(
                    "tenantPhoneNumber",
                    edit_tenant_phone?.text.toString()!!.filter { it.isDigit() }
                )
            }
            if (unitId != null && !unitId.equals("")) {
                isUnitIdAvailable = true
            }

            if (isEditing && isUnitIdAvailable) {
                //unitId = edit_unit_no!!.text.toString()

                credentials.propertyUnitDetails.addProperty(
                    "propertyUnitID",
                    unitId
                )
                if (!credentials.propertyId.isNullOrEmpty())
                    credentials.id = credentials.propertyId?.toLong()


                val addPropertyCall: ApiInterface =
                    ApiClient(appContext).provideService(ApiInterface::class.java)
                val apiCall: Observable<AddUnitDetailResponse> =
                    addPropertyCall.editUnit(credentials)

                apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        Log.e("onSuccess", it.responseDto.toString())

                        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                        add__progress.visibility = View.GONE
                        if (it.responseDto!!.responseCode == 200) {

                            if (mArrayUri.size > 0) {
                                startUploadUnitFileProcess(it.responseDto!!.propertyUnitId)
                            } else {

                                Kotpref.isAddPropertyFromLd = false
                                if (fragIsLast) {
                                    Util.alertOkIntentMessage(
                                        appContext as Activity,
                                        "Units Added",
                                        "All the units have been added successfully",
                                        HomeActivityCx::class.java
                                    )
                                } else {
                                    Util.alertOkMessage(
                                        appContext,
                                        "Unit Added",
                                        "The Unit has been saved. Now complete and add the next Unit"
                                    )
                                    moveNext()
                                }
                            }

                            /*
                            Util.alertOkIntentMessage(
                                appContext as Activity,
                                "Unit Updated Successfully!",
                                "The unit has been updated succesfully", PropertyDetailScreen::class.java)*/

                        }
                    },
                        { e ->
                            Log.e("onFailure", e.toString())
                            add__progress.visibility = View.GONE
                        })
            } else {
                val addPropertyCall: ApiInterface =
                    ApiClient(appContext).provideService(ApiInterface::class.java)
                val apiCall: Observable<AddUnitDetailResponse> =
                    addPropertyCall.addUnit(credentials)

                apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        Log.e("onSuccess", it.responseCode.toString())

                        add__progress.visibility = View.GONE
                        if (it.responseDto != null && it.responseDto!!.responseCode == 200) {
                            if (mArrayUri.size > 0) {
                                startUploadUnitFileProcess(it.responseDto!!.propertyUnitId)
                            } else {

                                Kotpref.isAddPropertyFromLd = false
                                if (fragIsLast) {
                                    Util.alertOkIntentMessage(
                                        appContext as Activity,
                                        "Units Added",
                                        "All the units have been added successfully",
                                        HomeActivityCx::class.java
                                    )
                                } else {
                                    Util.alertOkMessage(
                                        appContext,
                                        "Unit Added",
                                        "The Unit has been saved. Now complete and add the next Unit"
                                    )
                                    moveNext()
                                }
                            }
                        } else {
                            Util.alertOkMessage(
                                appContext,
                                "Failed",
                                "" + it.responseDto!!.responseDescription
                            )
                        }
                    },
                        { e ->
                            Log.e("onFailure", e.toString())
                            add__progress.visibility = View.GONE
                        })
            }
        }

        unit_description_skip?.setOnClickListener {
            //val intent = Intent(appContext, HomeActivityCx::class.java)
            //startActivity(intent)
            moveNext()
        }
    }

    private fun startUploadUnitFileProcess(propertyUnitId: String) {
        Log.e("Tag", "startUploadUnitFileProcess==>$propertyUnitId")
        try {

            Log.e("Tag", "mArrayUri Size==>${mArrayUri.size}")

            for (item in mArrayUri) {
                requireActivity().grantUriPermission(
                    requireActivity().getPackageName(),
                    item,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                //val path: String? = Util.getRealPathFromUri(this@UploadImageScreen, item)
                val file: File? =
                    Util.getRealPathFromUri(requireActivity(), item)
                fileUnitImageList.add(file!!)
            }
            Log.e("Tag", "fileUnitImageList Size==>${fileUnitImageList.size}")

            for (i in 0 until fileUnitImageList.size) {
                if (i == fileUnitImageList.size - 1) {
                    uploadUnitImage(fileUnitImageList[i], propertyUnitId, true)

                } else {
                    uploadUnitImage(fileUnitImageList[i], propertyUnitId, false)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun uploadUnitImage(item: File, unitId: String, b: Boolean) {

        Log.e("Tag", "uploadUnitImage unitId==>$unitId, last item flag===$b")

        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        builder.addFormDataPart("propertyId", Kotpref.propertyId)
        builder.addFormDataPart("unitId", unitId)
        builder.addFormDataPart("uploadFileType", getString(R.string.key_img_unit))
        add__progress.visibility = View.VISIBLE

        builder.addFormDataPart(
            "file", item!!.name,
            item.asRequestBody(MultipartBody.FORM)
        )
        val requestBody: RequestBody = builder.build()
        val uploadSign: ApiInterface =
            ApiClient(requireContext()).provideService(ApiInterface::class.java)

        val apiCallUploadSign: Observable<ResponseDtoResponse> =
            uploadSign.uploadDocument(requestBody)

        apiCallUploadSign.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({
                add__progress.visibility = View.GONE

                if (it.responseDto?.responseCode!!.equals(200)) {
                    if (b) {
                        Kotpref.isAddPropertyFromLd = false
                        if (fragIsLast) {
                            Util.alertOkIntentMessage(
                                appContext as Activity,
                                "Units Added",
                                "All the units have been added successfully",
                                HomeActivityCx::class.java
                            )
                        } else {
                            Util.alertOkMessage(
                                appContext,
                                "Unit Added",
                                "The Unit has been saved. Now complete and add the next Unit"
                            )
                            moveNext()
                        }
                    }

                } else {
                    Toast.makeText(
                        requireContext(),
                        it.responseDto?.exceptionDescription!!,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
                { e ->
                    add__progress.visibility = View.GONE

                    Toast.makeText(
                        requireContext(),
                        e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("error", e.message.toString())

                })
    }

}