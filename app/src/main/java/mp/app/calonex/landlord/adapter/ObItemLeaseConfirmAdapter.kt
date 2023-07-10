package mp.app.calonex.landlord.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.item_ob_lease_confirm.view.*
import mp.app.calonex.R
import mp.app.calonex.landlord.model.ObTenantPayload

class ObItemLeaseConfirmAdapter(var context: Context, var list: ArrayList<ObTenantPayload>) :
    RecyclerView.Adapter<ObItemLeaseConfirmAdapter.ViewHolder>() {


    private var valueTenant: String? = null
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Log.e("Tenant", "Item== $position=> " + Gson().toJson(list[position]))
        if (list[position].tenantFirstName.isNullOrEmpty() && list[position].tenantLastName.isNullOrEmpty()) {

            valueTenant = list[position].emailId
        } else {
            if (list[position].tenantFirstName.isNullOrEmpty()) {
                valueTenant = list[position].tenantLastName
            } else if (list[position].tenantLastName.isNullOrEmpty()) {
                valueTenant = list[position].tenantFirstName
            } else {
                valueTenant = list[position].tenantFirstName + " " + list[position].tenantLastName
            }
        }



        holder.txtItemLcRentPrcnt.text = list[position].rentPercentage
        holder.txtItemLcAmt.text = list[position].rentAmount
        holder.txtItemLcSecurityAmt.text = list[position].securityAmount

        if (position == 0) {
            holder.txtItemLcName.text = valueTenant + "*"
            holder.cbItemLcCoSigner.text = "Primary Tenant"

        } else {
            holder.txtItemLcName.text = valueTenant
            holder.cbItemLcCoSigner.text = "Co-Signer"

        }
        //statusTenant(list[position].coSignerFlag!!, holder.cbItemLcCoSigner!!)
        //statusTenant(list[position].coTenantFlag!!, holder.cbItemLcCoTenant!!)
    }


    private fun statusTenant(flag: Boolean, txt: TextView) {
        if (flag) {
            txt.setBackgroundResource(R.drawable.btn_dk_blue_round)
            txt.setTextColor(Color.WHITE)
        } else {
            txt.setBackgroundResource(R.drawable.btn_lt_blue_grey_round)
            txt.setTextColor(Color.BLACK)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val txtItemLcName = itemView.txt_item_lc_tenant_name
        val txtItemLcRentPrcnt = itemView.txt_item_lc_rent_prcnt
        val txtItemLcAmt = itemView.txt_item_lc_rent_amt
        val txtItemLcSecurityAmt = itemView.txt_item_lc_security_amt
        val cbItemLcCoSigner = itemView.txt_item_lc_co_signer
        val cbItemLcCoTenant = itemView.txt_item_lc_co_tenant


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(context).inflate(R.layout.item_ob_lease_confirm, parent, false)
        return ViewHolder(v)
    }


}