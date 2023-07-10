package mp.app.calonex.landlord.fragment

import android.app.Dialog
import android.content.Context
import android.telephony.PhoneNumberUtils
import android.text.InputType
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.github.anastr.speedviewlib.SpeedView
import com.github.anastr.speedviewlib.components.Section
import com.google.android.material.textfield.TextInputEditText
import io.reactivex.Observable
import mp.app.calonex.R
import mp.app.calonex.common.apiCredentials.GeneralReportDetailsByID
import mp.app.calonex.common.network.ApiClient
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.Util
import mp.app.calonex.common.utility.Util.setEditReadOnly
import mp.app.calonex.landlord.model.ObTenantPayload
import mp.app.calonex.tenant.model.GeneralReportResponse

class TenantLeaseDialogFragment(context: Context) : Dialog(context) {
    var sv_calonex_score: SpeedView? = null
    fun customDialog(obTenantPayload: ObTenantPayload) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.fragment_tenant_info_dialog)
        dialog.setTitle("Tenant Information")

        val txtFdTenantRole: TextView = dialog.findViewById(R.id.txt_fd_tenant_role)
        val editFdTenantName: TextInputEditText = dialog.findViewById(R.id.edit_fd_tenant_name)
        val editFdTenantEmail: TextInputEditText = dialog.findViewById(R.id.edit_fd_tenant_email)
        val editFdTenantPhn: TextInputEditText = dialog.findViewById(R.id.edit_fd_tenant_phn)
        val editFdTenantAdd: TextInputEditText = dialog.findViewById(R.id.edit_fd_tenant_address)
        val editFdTenantCity: TextInputEditText = dialog.findViewById(R.id.edit_fd_tenant_city)
        val editFdTenantState: TextInputEditText = dialog.findViewById(R.id.edit_fd_tenant_state)
        val editFdTenantZipcode: TextInputEditText = dialog.findViewById(R.id.edit_fd_tenant_zip)
        val editFdTenantDriving: TextInputEditText =
            dialog.findViewById(R.id.edit_fd_tenant_driving)
        val ivFdClose: ImageView = dialog.findViewById(R.id.iv_fd_close)
        sv_calonex_score = dialog.findViewById(R.id.sv_calonex_score)

        setEditReadOnly(editFdTenantName, false, InputType.TYPE_NULL)
        setEditReadOnly(editFdTenantEmail, false, InputType.TYPE_NULL)
        setEditReadOnly(editFdTenantPhn, false, InputType.TYPE_NULL)
        setEditReadOnly(editFdTenantAdd, false, InputType.TYPE_NULL)
        setEditReadOnly(editFdTenantCity, false, InputType.TYPE_NULL)
        setEditReadOnly(editFdTenantState, false, InputType.TYPE_NULL)
        setEditReadOnly(editFdTenantZipcode, false, InputType.TYPE_NULL)
        setEditReadOnly(editFdTenantDriving, false, InputType.TYPE_NULL)

        getGeneralReport(obTenantPayload.userId)

        sv_calonex_score!!.clearSections()
        sv_calonex_score!!.addSections(
            Section(
                0f,
                .32f,
                context.resources.getColor(R.color.colorReportRed),
                sv_calonex_score!!.dpTOpx(30f)
            ),
            Section(
                .32f,
                .65f,
                context.resources.getColor(R.color.colorReportYellow),
                sv_calonex_score!!.dpTOpx(30f)
            ),
            Section(
                .65f,
                .80f,
                context.resources.getColor(R.color.colorReportBlue),
                sv_calonex_score!!.dpTOpx(30f)
            ),
            Section(
                .80f,
                1.0f,
                context.resources.getColor(R.color.colorReportGreen),
                sv_calonex_score!!.dpTOpx(30f)
            )
        )

        txtFdTenantRole!!.text = obTenantPayload.role
        editFdTenantName!!.setText(obTenantPayload.tenantFirstName + " " + obTenantPayload.tenantLastName)
        editFdTenantEmail!!.setText(obTenantPayload.emailId)
        editFdTenantPhn!!.setText(
            PhoneNumberUtils.formatNumber(
                obTenantPayload.phone,
                "US"
            )
        )
        editFdTenantAdd!!.setText(obTenantPayload.address)
        editFdTenantCity!!.setText(obTenantPayload.city)
        editFdTenantState!!.setText(obTenantPayload.state)
        editFdTenantZipcode!!.setText(obTenantPayload.zipcode)
        editFdTenantDriving!!.setText(obTenantPayload.licenseNo)


        ivFdClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun getGeneralReport(tenantId: String?) {

        val credentials = GeneralReportDetailsByID()
        credentials.userCatalogId = tenantId

        val bookKeepingService: ApiInterface =
            ApiClient(context).provideService(ApiInterface::class.java)
        val apiCall: Observable<GeneralReportResponse> =
            bookKeepingService.generateReport(credentials) //Test API Key

        RxAPICallHelper().call(apiCall, object : RxAPICallback<GeneralReportResponse> {
            override fun onSuccess(response: GeneralReportResponse) {
                if (response.responseDto.responseCode == 200) {

                    try {
                        sv_calonex_score!!.setSpeedAt(response.data.cx_Score.toFloat())

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }

            override fun onFailed(throwable: Throwable) {
                // show error
                Log.e("onFailure", throwable.toString())
                //
                try {
                    Util.apiFailure(context, throwable)
                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.error_something),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }
}