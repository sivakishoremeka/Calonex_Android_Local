package mp.app.calonex.landlord.adapter

import android.app.Activity
import android.content.Context
import android.text.InputType
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_lease_request_detail_screen.*
import kotlinx.android.synthetic.main.item_lr_detail.view.*
import mp.app.calonex.R
import mp.app.calonex.landlord.fragment.TenantLeaseDialogFragment
import mp.app.calonex.landlord.model.FetchSecurityInfo
import mp.app.calonex.landlord.model.ObTenantPayload


class LeaseRequestDetailAdapter(
    var context: Context,
    var activity: Activity,
    var leaseStatus:Int,
    var list: ArrayList<ObTenantPayload>,
    var securityList:ArrayList<FetchSecurityInfo>
) : RecyclerView.Adapter<LeaseRequestDetailAdapter.ViewHolder>() {

    private var valueTenant:String?=null
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if(list[position].tenantFirstName.isNullOrEmpty() && list[position].tenantLastName.isNullOrEmpty()){

            valueTenant =list[position].emailId
        }else{
            if(list[position].tenantFirstName.isNullOrEmpty()){
                valueTenant =list[position].tenantLastName
            } else if(list[position].tenantLastName.isNullOrEmpty()){
                valueTenant =list[position].tenantFirstName
            }else {
                valueTenant = list[position].tenantFirstName + " " + list[position].tenantLastName
            }
        }

        holder.txtItemLrdRentPrcnt.text=list[position].rentPercentage
        holder.txtItemLrdAmt.text="$ "+list[position].rentAmount
        holder.txtItemLrdSecurity.text="$ "+list[position].securityAmount
        if(leaseStatus>23){
            holder.layoutSecurityValue!!.visibility=View.VISIBLE
            for(item in securityList){
                if(item.tenantId!!.equals(list[position].userId!!.toInt())){
                    if((item.deductedAmount==null)||(item.deductedAmount!!.toInt()==0)){
                        holder.txtItemLrdDeduction.text = "$ 0.00"
                    }else {
                        holder.txtItemLrdDeduction.text = "$ " + item.deductedAmount
                    }
                    holder.txtItemLrdReason.text=item.deductedAmountReason
                }
            }
        }else{
            holder.layoutSecurityValue!!.visibility=View.GONE
            holder.txtItemLrdDeduction.text=""
            holder.txtItemLrdReason.text=""
        }
        setReadOnly(holder.cbItemLrdCoSigner!!, false, InputType.TYPE_NULL)
        setReadOnly(holder.cbItemLrdCoTenant!!, false, InputType.TYPE_NULL)
        holder.cbItemLrdCoSigner.isChecked = list[position].coSignerFlag!!

        holder.cbItemLrdCoTenant.isChecked = list[position].coTenantFlag!!
        if(position==0){
            valueTenant=valueTenant+"*"

        }else{
            valueTenant=valueTenant
        }
        val content = SpannableString(valueTenant)
        content.setSpan(UnderlineSpan(), 0, valueTenant!!.length, 0)
        holder.txtItemLrdName.text=content
        holder.txtItemLrdName.setTextColor(ContextCompat.getColor(context,R.color.colorPrimaryDark))
        holder.txtItemLrdName.setOnClickListener {
            TenantLeaseDialogFragment(activity!!).customDialog(list[holder.adapterPosition])
        }

    }

    private fun setReadOnly(checkBox: CheckBox, value: Boolean, input: Int) {
        checkBox.isFocusable = value
        checkBox.isClickable=value
        checkBox.isFocusable = value
        checkBox.inputType = input
    }
    override fun getItemCount(): Int {
        return list.size
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val txtItemLrdName=itemView.txt_item_lrd_name
        val txtItemLrdRentPrcnt = itemView.txt_item_rd_lrd_prcnt
        val txtItemLrdAmt=itemView.txt_item_lrd_amt
        val cbItemLrdCoSigner=itemView.cb_item_lrd_co_signer
        val cbItemLrdCoTenant=itemView.cb_item_lrd_co_tenant
        val txtItemLrdSecurity=itemView.txt_item_lrd_secuity
        val txtItemLrdDeduction=itemView.txt_item_lrd_deduction
        val txtItemLrdReason=itemView.txt_item_lrd_reason
        val layoutSecurityValue=itemView.layout_security_value

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(context).inflate(R.layout.item_lr_detail, parent, false)
        return ViewHolder(v)
    }

}