package mp.app.calonex.landlord.activity

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mp.app.calonex.R
import mp.app.calonex.common.utility.Util
import mp.app.calonex.landlord.activity.ObLeaseSpecificationScreen.Companion.obLeaseTenantPayload
import mp.app.calonex.landlord.adapter.ObItemRentDistributionAdapter
import mp.app.calonex.landlord.adapter.PropertyListAdapter
import mp.app.calonex.landlord.model.ObTenantPayload


class ObRentDistributionScreen : CxBaseActivity2() {
    private var txtObProperty: TextView? = null
    private var txtObUnit: TextView? = null
    private var rvObRdTenant: RecyclerView? = null
    private var btnRdNxt: Button? = null
    private var tenantRdArrayList = ArrayList<ObTenantPayload>()
    private var obItemRentDistributionScreen: ObItemRentDistributionAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ob_rent_distribution_screen)
        initComponent()
        actionComponent()
        startHandler()
    }

    private fun initComponent() {
        txtObProperty = findViewById(R.id.txt_ob_property_add)
        txtObUnit = findViewById(R.id.txt_ob_unit)
        rvObRdTenant = findViewById(R.id.rv_ob_rd_tenant)
        btnRdNxt = findViewById(R.id.btn_rd_nxt)
        btnRdNxt!!.isEnabled = false
        txtObProperty!!.text =
            ", ${PropertyListAdapter.propertyDetailResponse.address}, ${PropertyListAdapter.propertyDetailResponse.city}, ${PropertyListAdapter.propertyDetailResponse.state}, ${PropertyListAdapter.propertyDetailResponse.zipCode}"
        txtObUnit!!.text = "Unit : ${obLeaseTenantPayload.unitNumber}"
        rvObRdTenant?.layoutManager = LinearLayoutManager(applicationContext)

        tenantRdArrayList = obLeaseTenantPayload.tenantBaseInfoDto

        obItemRentDistributionScreen = ObItemRentDistributionAdapter(
            applicationContext, btnRdNxt!!,
            this@ObRentDistributionScreen,
            tenantRdArrayList
        )
        rvObRdTenant?.adapter = obItemRentDistributionScreen
    }

    private fun actionComponent() {

        btnRdNxt!!.setOnClickListener {

            Util.navigationNext(this, ObLeaseConfirmScreen::class.java)
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        Util.navigationBack(this)
    }

}