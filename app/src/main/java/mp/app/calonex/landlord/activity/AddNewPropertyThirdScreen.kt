package mp.app.calonex.landlord.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.JsonArray
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_new_property_third_screen.*
import mp.app.calonex.R
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.utility.NetworkConnection
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.AddNewPropertyFirstScreen.Companion.isEditing
import mp.app.calonex.landlord.activity.AddNewPropertyFirstScreen.Companion.propertyIdFirst
import mp.app.calonex.landlord.activity.AddNewPropertyFirstScreen.Companion.result
import mp.app.calonex.landlord.activity.UploadImageScreen.Companion.listOfMultipart
import mp.app.calonex.landlord.adapter.PropertyListAdapter.Companion.propertyDetailResponse
import mp.app.calonex.landlord.model.PropertyExpenseAddPropertyPayload
import mp.app.calonex.landlord.response.AddPropertyPostResponse
import mp.app.calonex.landlord.response.UploadImagesPostResponse
import mp.app.calonex.service.FeaturesService.Companion.allModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import java.util.*


class AddNewPropertyThirdScreen : CxBaseActivity2() {

    var linearLayout: LinearLayout? = null
    var propertyId: String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_property_third_screen)
        linearLayout = findViewById(R.id.expenses_ll)
        supportActionBar?.hide()
        actionComponent()
        startHandler()
        expense_year.text = Calendar.getInstance().get(Calendar.YEAR).toString()
        val cal = Calendar.getInstance()
        val currentdate = Calendar.getInstance().time
       // val currentdate = LocalDate.now()
        val currentMonth = currentdate.month - 1
        val month = currentdate.month - 1
        val year = cal.get(Calendar.YEAR)
        expense_month.text = currentMonth.toString()
        expense_year.text = year.toString()

        val vi: LayoutInflater? = layoutInflater

        val propertyExpensesIdList = JsonArray()


        for (i in 0 until allModel?.propertyExpensesListResponse?.data!!.size) {
            val item =  allModel!!.propertyExpensesListResponse.data!![i]
            val view: View? = vi?.inflate(R.layout.item_expenses_add_property, linearLayout, false)
            var expenseEdit: TextInputEditText? = view?.findViewById(R.id.edit_expense)
            expenses_ll.addView(view)
            expenseEdit?.hint = item.expensesType
            expenseEdit?.compoundDrawablePadding=5
            if(item.expensesType.equals("GAS")){
                expenseEdit?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.gas,0,0,0)
            }else if(item.expensesType.equals("Electricity")){
                expenseEdit?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.electricity,0,0,0)
            }else if(item.expensesType.equals("Misc") ||item.expensesType.equals("Water")){
                expenseEdit?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.misc_icon,0,0,0)
            }

            val expenseList = PropertyExpenseAddPropertyPayload()

            expenseList.expensesType = item.expensesType
            expenseList.propertyExpensesId = item.propertyExpensesId
            expenseList.expensesMonth = month
            expenseList.expensesYear = year

            propertyExpensesIdList.add(item.propertyExpensesId)

            expenseEdit?.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable) {
                    expenseList.expensesAmount = s.toString()
                }

                override fun beforeTextChanged(
                    s: CharSequence, start: Int,
                    count: Int, after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence, start: Int,
                    before: Int, count: Int
                ) {
                }
            })

            if (isEditing){
                if(propertyDetailResponse.propertyExpensesDetailsDTO.size > i) {

                    expenseEdit?.setText(propertyDetailResponse.propertyExpensesDetailsDTO[i].expensesAmount)
                    expenseList.expensesAmount = expenseEdit?.text.toString()
                }

            }

            result.propertyExpensesDetailsDTO.addAll(listOf(expenseList))
        }
        result.propertyExpensesTypeDTO.add("propertyExpensesId", propertyExpensesIdList)

    }


    private fun actionComponent() {

        btn_login!!.setOnClickListener { _ ->
            addProperty()
        }

        expense_skip.setOnClickListener {
            addProperty()
        }
    }

    @SuppressLint("CheckResult")
    private fun addProperty() {
        if (NetworkConnection.isNetworkConnected(applicationContext)) {

            val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

            add_property_progress.visibility = View.VISIBLE

            propertyId = propertyIdFirst
            result.propertyId = propertyId
            val editPropertyCall: ApiInterface =
                ApiClient(this).provideService(ApiInterface::class.java)
            val editApiCall: Observable<AddPropertyPostResponse> =
                editPropertyCall.editProperty(result)

            editApiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it.data != null) {

                        if (listOfMultipart.size != 0) {
                            val id: RequestBody = RequestBody.create(
                                "multipart/form-data".toMediaTypeOrNull(),
                                propertyId!!
                            )
                            // Multipart Images
                            val apiCallImagesUpload: Observable<UploadImagesPostResponse> =
                                editPropertyCall.uploadImages(id, listOfMultipart)

                            apiCallImagesUpload.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread()).subscribe({
                                    add_property_progress.visibility = View.GONE
//                                    toast("upload image success")
                                    Log.e(
                                        "onSuccess",
                                        it.responseDto?.get("responseDescription").toString()
                                    )
                                },
                                    { e ->
                                        //                                        toast("upload image fail")
                                        add_property_progress.visibility = View.GONE
                                        Log.e("error", e.message.toString())

                                    })

                        }

                        add_property_progress.visibility = View.GONE

                        if(isEditing) {
                            Toast.makeText(
                                applicationContext,
                                "Property Edited Successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Property Added Successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        val intent = Intent(this, HomeActivityCx::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        //intent.putExtra("isFeatureRefreshed", true)
                        startActivity(intent)
                        Util.navigationNext(this)
                        finish()
                    }

                },
                    { e ->

                        add_property_progress.visibility = View.GONE
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
    override fun onBackPressed() {
        super.onBackPressed()
        Util.navigationBack(this@AddNewPropertyThirdScreen)
    }
}
