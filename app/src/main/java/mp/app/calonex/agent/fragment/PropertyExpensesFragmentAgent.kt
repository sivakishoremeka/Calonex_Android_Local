package mp.app.calonex.agent.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.GetPropertyByIdApiCredential
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface

import mp.app.calonex.common.utility.Kotpref.propertyIdNew
import mp.app.calonex.common.utility.NetworkConnection

import mp.app.calonex.landlord.model.PropertyExpensePayload
import mp.app.calonex.landlord.model.PropertyExpensesDetailsDTO
import mp.app.calonex.landlord.response.PropertyDetailResponse
import mp.app.calonex.landlord.response.PropertyExpenseResponse

class PropertyExpensesFragmentAgent  : Fragment() {

    private var Data1: TextInputEditText? = null
    private var Data2: TextInputEditText? = null
    private var Data3: TextInputEditText? = null
    private var Data4: TextInputEditText? = null

    private var btn_reg_next: TextView? = null
    private var pbUnit: ProgressBar?=null

    var spinner_1: Spinner? = null
    var spinner_2: Spinner? = null

    var switch_1: SwitchCompat?  = null
    var block_1: LinearLayout? = null

    var monthdata: Long? = 1
    var yeardata: Long? = 2022

        lateinit var appContext: Context
    var month: Long? = 0
    var year: Long? = 0
    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext =  context

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView =  inflater.inflate(R.layout.fragment_property_expenses, container, false)

        Data1=rootView.findViewById(R.id.edit_1)
        Data2=rootView.findViewById(R.id.edit_2)
        Data3=rootView.findViewById(R.id.edit_3)
        Data4=rootView.findViewById(R.id.edit_4)

        btn_reg_next=rootView.findViewById(R.id.btn_reg_next)

        switch_1 =rootView.findViewById(R.id.switch_1)
        block_1 =rootView.findViewById(R.id.block_1)

        spinner_1 = rootView.findViewById(R.id.spinner_1)
        spinner_2 = rootView.findViewById(R.id.spinner_2)

        pbUnit=rootView.findViewById(R.id.pb_unit)

        return rootView
    }

    /*override fun onResume() {
        super.onResume()
        getPropertyById(propertyIdNew)

    }*/

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        switch_1!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                block_1!!.visibility = View.GONE
            } else {
                block_1!!.visibility = View.VISIBLE
                getPropertyById(propertyIdNew)
            }
        }

        val months = resources.getStringArray(R.array.Months)
        val years = resources.getStringArray(R.array.Years)

        val adapter1 = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, months)
        spinner_1!!.adapter = adapter1
        val adapter2 = ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_item, years)
        spinner_2!!.adapter = adapter2

        spinner_1!!.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
                monthdata = months[position].toLong()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        spinner_2!!.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
                yeardata = years[position].toLong()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        btn_reg_next!!.setOnClickListener {
            if (switch_1!!.isChecked) {
                requireActivity().finish()
            } else {
                submitExpense()
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun getPropertyById(propertyId: Long?) {
        if (NetworkConnection.isNetworkConnected(appContext)) {

            requireActivity().window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            pbUnit!!.visibility = View.VISIBLE
            var credential = GetPropertyByIdApiCredential()
            credential.propertyId = propertyId

            val propertyDetails: ApiInterface =
                ApiClient(appContext).provideService(ApiInterface::class.java)
            val apiCall: Observable<PropertyDetailResponse> =
                propertyDetails.getPropertyById(credential) //Test API Key

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
                Log.e("onSuccess", it.responseCode.toString())

                pbUnit!!.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                if (it.data != null) {

                        month = it.data!!.propertyExpensesDetailsDTO[0].expensesMonth
                        year = it.data!!.propertyExpensesDetailsDTO[0].expensesYear
                        Data1!!.setText(it.data!!.propertyExpensesDetailsDTO[0].expensesAmount)
                        Data2!!.setText(it.data!!.propertyExpensesDetailsDTO[1].expensesAmount)
                        Data3!!.setText(it.data!!.propertyExpensesDetailsDTO[2].expensesAmount)
                        Data4!!.setText(it.data!!.propertyExpensesDetailsDTO[3].expensesAmount)

                }
                else{
                    Toast.makeText(appContext,it.responseCode!!, Toast.LENGTH_SHORT).show()
                }
            },
                { e ->
                    // show error
                    requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    pbUnit!!.visibility = View.GONE
                    Log.e("onFailure", e.toString())
                    //  progressBarList?.visibility = View.GONE
                })
        } else {
            Toast.makeText(appContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
        }
    }

@SuppressLint("CheckResult")
    private fun submitExpense() {
        if (NetworkConnection.isNetworkConnected(appContext)) {
            //Create retrofit Service
            pbUnit!!.visibility = View.VISIBLE
            requireActivity().window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

            val credentials = PropertyExpensePayload()

            val data1 = PropertyExpensesDetailsDTO()
            data1.propertyExpensesId = 1
            data1.expensesAmount = if (Data1!!.text.toString().isNotEmpty()) Data1!!.text.toString().toDouble() else 0.0
            data1.expensesType = "GAS"
            data1.expensesMonth = if (month != 0L) month else 0L
            data1.expensesYear = if (year != 0L) year else 0L
            credentials.propertyExpensesDetailsDTO.add(data1)

            val data2 = PropertyExpensesDetailsDTO()
            data2.propertyExpensesId = 2
            data2.expensesAmount = if (Data2!!.text.toString().isNotEmpty()) Data2!!.text.toString().toDouble() else 0.0
            data2.expensesType = "Electricity"
            data2.expensesMonth = if (month != 0L) month else 0L
            data2.expensesYear = if (year != 0L) year else 0L
            credentials.propertyExpensesDetailsDTO.add(data2)

            val data3 = PropertyExpensesDetailsDTO()
            data3.propertyExpensesId = 3
            data3.expensesAmount = if (Data3!!.text.toString().isNotEmpty()) Data3!!.text.toString().toDouble() else 0.0
            data3.expensesType = "Water"
            data3.expensesMonth = if (month != 0L) month else 0L
            data3.expensesYear = if (year != 0L) year else 0L
            credentials.propertyExpensesDetailsDTO.add(data3)

            val data4 = PropertyExpensesDetailsDTO()
            data4.propertyExpensesId = 4
            data4.expensesAmount = if (Data4!!.text.toString().isNotEmpty()) Data4!!.text.toString().toDouble() else 0.0
            data4.expensesType = "Misc"
            data4.expensesMonth = if (month != 0L) month else 0L
            data4.expensesYear = if (year != 0L) year else 0L
            credentials.propertyExpensesDetailsDTO.add(data4)



            /*
"propertyExpensesDetailsDTO": [
    {
      "propertyExpensesId": 1,
      "expensesAmount": 1000,
      "expensesType": "GAS",
      "expensesMonth": 10,
      "expensesYear": 2021
    }
             */

            val validateService: ApiInterface = ApiClient(appContext).provideService(ApiInterface::class.java)
            val apiCall: Observable<PropertyExpenseResponse> = validateService.propertyExpense(credentials)

            apiCall.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({
                Log.e("onSuccess", it.responseDto?.responseCode.toString())

                pbUnit!!.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                if(it!!.responseDto!!.responseCode!!.equals(200)) {
                    //startActivity(Intent(this@LinkPropertyAgent, HomeActivityAgent::class.java))

                    Toast.makeText(appContext, "Done", Toast.LENGTH_SHORT).show()
                    requireActivity().finish()
                }
                else{
                    Toast.makeText(appContext,it.responseDto!!.responseCode!!, Toast.LENGTH_SHORT).show()
                }
            },
                { e ->
                    Log.e("onFailure", e.toString())
                    pbUnit!!.visibility = View.GONE

                    requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    e.message?.let {
                        Toast.makeText(appContext, it, Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            Toast.makeText(appContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
        }
    }
}