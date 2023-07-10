package mp.app.calonex.landlord.adapter

import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_login_screen.*
import kotlinx.android.synthetic.main.item_ob_rent_distribution.view.*
import kotlinx.android.synthetic.main.item_security_return.view.*
import mp.app.calonex.R
import mp.app.calonex.landlord.activity.LeaseRequestDetailScreen.Companion.refundTenantList
import mp.app.calonex.landlord.model.ObTenantPayload

class RefundSecurityAdapter (var context: Context,var activity: Activity, var list: ArrayList<ObTenantPayload>) : RecyclerView.Adapter<RefundSecurityAdapter.ViewHolder>() {



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

        if(position==0){
            holder.txtItemName.text =valueTenant+"*"

        }else{
            holder.txtItemName.text =valueTenant
        }
        if(!list[position].coTenantFlag!! && list[position].coSignerFlag!!){
            holder.editItemRfndAmt!!.visibility=View.INVISIBLE
            holder.editItemRfndCmnt!!.visibility=View.INVISIBLE
        }else{
            holder.editItemRfndAmt!!.visibility=View.VISIBLE
            holder.editItemRfndCmnt!!.visibility=View.VISIBLE
        }

        holder.txtItemSecurityAmt.text="$ "+list[position].securityAmount


        holder.editItemRfndAmt!!.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                var valRefund:String=holder.editItemRfndAmt!!.text.toString()
                if(valRefund.length>0){
                    if(validateAmt(list[holder.adapterPosition].securityAmount!!.toFloat().toInt(),holder.editItemRfndAmt!!.text.toString().toInt())){
                        refundTenantList[holder.adapterPosition].refundableAmount=holder.editItemRfndAmt!!.text.toString()
                    }else{
                        holder.editItemRfndAmt!!.error  = context.getString(R.string.error_security)
                        holder.editItemRfndAmt!!.requestFocus()
                    }
                }


            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {

            }
        })
        holder.editItemRfndCmnt!!.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {

                refundTenantList[holder.adapterPosition].deductedAmountReason=holder.editItemRfndCmnt!!.text.toString()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {


            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {

            }
        })

    }

 private fun validateAmt(security:Int,rfnd:Int):Boolean{
     if(rfnd>security){
         return false
     }else{
         return true
     }
 }







    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val txtItemName=itemView.txt_item_rtrn_name
        val txtItemSecurityAmt = itemView.txt_item_security_amt
        val editItemRfndAmt=itemView.edit_item_rfnd_amt
        val editItemRfndCmnt=itemView.edit_item_rfnd_cmnt


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(context).inflate(R.layout.item_security_return, parent, false)
        return ViewHolder(v)
    }


}