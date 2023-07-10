package mp.app.calonex.tenant.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_rent_distribution_tenant.*
import mp.app.calonex.R
import mp.app.calonex.landlord.activity.CxBaseActivity2
import mp.app.calonex.tenant.activity.TenantListActivity.Companion.findApiResponse
import mp.app.calonex.tenant.activity.TenantListActivity.Companion.tenantListTemporary
import mp.app.calonex.tenant.activity.TenantListEditActivity.Companion.isEdited
import mp.app.calonex.tenant.activity.TenantListEditActivity.Companion.isRemoved

class RentDistributionTenantActivity : CxBaseActivity2() {

    var linearLayout: LinearLayout? = null
    val total_rent: String = findApiResponse.rentAmount
    val total_security: String = findApiResponse.securityAmount
    val rentAmountBeforeDueDate = findApiResponse.rentBeforeDueDate
    val securityAmt = findApiResponse.securityAmount


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rent_distribution_tenant)
        actionComponent()

        linearLayout = findViewById(R.id.rent_distribution_tenant_list_ll)
        rent_distribution_total_rent.text = "$"+total_rent
        rent_distribution_security.text = "$"+total_security


        val vi: LayoutInflater? = layoutInflater

        var rentPercentTotal : Int = 0
        var rentAmountTotal : Double = 0.0

        for (i in 0 until tenantListTemporary.size) {
            val item = tenantListTemporary[i]
            val view: View? = vi?.inflate(R.layout.item_rent_distribution, linearLayout, false)

            val rentText: TextView? = view?.findViewById(R.id.rd_item_rent_amount)
            val tenant_name: TextView? = view?.findViewById(R.id.rd_item_tenant_name)
            val rent_percent: EditText? = view?.findViewById(R.id.rd_item_rent_percent)

            rent_distribution_tenant_list_ll.addView(view)

            if (tenantListTemporary[i].rentPercentage.isNullOrEmpty()){
                tenantListTemporary[i].rentPercentage = "0"
            }
            rentPercentTotal += (tenantListTemporary[i].rentPercentage)!!.toDouble().toInt()

            rent_percent?.setText(tenantListTemporary[i].rentPercentage!!.toDouble().toInt().toString())

            if (tenantListTemporary[i].rentAmount.isNullOrEmpty()){
                tenantListTemporary[i].rentAmount = "0"
            }

            rentAmountTotal += (tenantListTemporary[i].rentAmount)!!.toDouble()

            rentText?.text = tenantListTemporary[i].rentAmount

            if (tenantListTemporary[i].role.equals("CX-CoSigner", true)){
                rent_percent!!.isEnabled = false
            }

            rent_percent?.isFocusable = true


            tenant_name?.text = item.tenantFirstName + " " + item.tenantLastName

            if (rentPercentTotal == 100) {
                btn_rent_percent_done.isClickable = true
                btn_rent_percent_done.background =
                    resources.getDrawable(R.drawable.btn_green_round)
            } else {
                btn_rent_percent_done.isClickable = false
                btn_rent_percent_done.background =
                    resources.getDrawable(R.drawable.btn_lt_grey_round)
            }


            rent_percent?.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable?) {
                    tenantListTemporary[i].rentPercentage = s.toString()

                    var rentPercent = 0
                    if (s.toString().isEmpty()) {
                        rentPercent = 0
//                        tenantListTemporary[i].securityAmount = "0"
//                        tenantListTemporary[i].rentAmountBeforeDueDate = "0"
//                        rentText?.text = "0"
//                        tenantListTemporary[i].rentAmount = "0"
//                        return
                    } else {
                        rentPercent = s.toString().toDouble().toInt()
                    }

                    if (rentPercent > 100) {
                        rent_percent.error = getString(R.string.error_prcnt)
                        return
                    }
                    var cal_rent_percent: Int? = 0
                    for (i in 0 until tenantListTemporary.size) {
                        val item = tenantListTemporary[i]
                        if (item.rentPercentage?.isNotEmpty()!!) {
                            cal_rent_percent =
                                cal_rent_percent?.plus(item.rentPercentage!!.toDouble().toInt())
                        }
                    }

                    if (cal_rent_percent!! > 100) {
                        rent_percent.error = getString(R.string.error_prcnt)
                    }
                    rent_distribution_total_percent.text = cal_rent_percent.toString() + "%"
                    rent_distribution_total_sum.text = "$" +
                        ((total_rent.toDouble() * cal_rent_percent) / 100).toString()

                    var securityAmount = (securityAmt.toDouble() * rentPercent) / 100
                    val number3digits: Double = Math.round(securityAmount * 1000.0) / 1000.0
                    securityAmount = Math.round(number3digits * 10.0) / 10.0
                    val number2digits: Double = Math.round(number3digits * 100.0) / 100.0
                    tenantListTemporary[i].securityAmount = securityAmount.toString()


                    var rentAmount = (total_rent.toDouble() * rentPercent) / 100

                    val rentAmount3digits: Double = Math.round(rentAmount * 1000.0) / 1000.0
                    rentAmount = Math.round(rentAmount3digits * 10.0) / 10.0
//                    val rentAmount2digits: Double = Math.round(rentAmount3digits * 100.0) / 100.0

                    rentText?.text = rentAmount.toString()
                    tenantListTemporary[i].rentAmount = rentAmount.toString()

                    if (cal_rent_percent == 100) {
                        btn_rent_percent_done.isClickable = true
                        btn_rent_percent_done.background =
                            resources.getDrawable(R.drawable.btn_green_round)
                    } else {
                        btn_rent_percent_done.isClickable = false
                        btn_rent_percent_done.background =
                            resources.getDrawable(R.drawable.btn_lt_grey_round)
                    }


                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

            })

        }

        rent_distribution_total_percent.text = rentPercentTotal.toString() + "%"
        rent_distribution_total_sum.text = "$"+rentAmountTotal.toString()


    }

    private fun actionComponent() {

        btn_rent_percent_done!!.setOnClickListener { _ ->
            // first it will check the sum of rent distribution should be equal to the total rent
            var rent_percent: Int? = 0
            for (i in 0 until tenantListTemporary.size) {
                val item = tenantListTemporary[i]

                if (item.rentPercentage.equals("")) {
                    Toast.makeText(
                        applicationContext,
                        "Field can not be blank for " + item.tenantFirstName,
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                /*if (item.rentPercentage.equals("0")) {
                    if (item.role?.contains("CoSigner",true)!!||item.role?.contains("CoTenant",true)!!) {
                        rent_percent = rent_percent?.plus(0)
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Please add the rent distribution for " + item.tenantFirstName,
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }
                } else {*/
                    rent_percent = rent_percent?.plus(item.rentPercentage!!.toDouble().toInt())
              //  }

            }

            if (rent_percent != 100) {
                Toast.makeText(
                    applicationContext,
                    "Sum of rent not correct",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                Toast.makeText(
                    applicationContext,
                    "Rent Distribution complete",
                    Toast.LENGTH_SHORT
                )
                    .show()
                isRemoved = false
                isEdited = true
                val intent =
                    Intent(this, TenantListEditActivity::class.java)
                intent.putExtra("tenant_added", true)
                startActivity(intent)
                finish()

            }




            btn_rent_done!!.setOnClickListener { _ ->
                // first it will check the sum of rent distribution should be equal to the total rent
                var rent_sum: Double? = 0.0
                for (i in 0 until tenantListTemporary.size) {
                    val item = tenantListTemporary[i]
                    if (item.rentAmount.equals("")) {
                        Toast.makeText(
                            applicationContext,
                            "Please add the rent amount for new Tenant",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }
                    rent_sum = rent_sum?.plus(item.rentAmount?.toDouble()!!)
                }

                rent_distribution_total_sum.text = "$"+rent_sum.toString()

                if (total_rent.toDouble() != rent_sum) {
                    Toast.makeText(
                        applicationContext,
                        "Sum of rent not correct",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Rent Distribution complete",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    isRemoved = false
                    isEdited = true
                    val intent =
                        Intent(this, TenantListEditActivity::class.java)
                    intent.putExtra("tenant_added", true)
                    startActivity(intent)
                    finish()
                }

            }


        }

    }

}
