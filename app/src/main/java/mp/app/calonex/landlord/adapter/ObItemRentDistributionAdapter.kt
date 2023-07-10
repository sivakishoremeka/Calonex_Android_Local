package mp.app.calonex.landlord.adapter

import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_add_new_property_second_screen.*
import kotlinx.android.synthetic.main.activity_login_screen.*
import kotlinx.android.synthetic.main.item_ob_rent_distribution.view.*
import kotlinx.android.synthetic.main.item_ob_tenant.view.*
import mp.app.calonex.R
import mp.app.calonex.common.utility.DigitDecimalInputFilter
import mp.app.calonex.landlord.activity.ObLeaseSpecificationScreen.Companion.obLeaseTenantPayload
import mp.app.calonex.landlord.model.ObTenantPayload

class ObItemRentDistributionAdapter (var context: Context,var btn:Button?,var activity:Activity?, var list: ArrayList<ObTenantPayload>) : RecyclerView.Adapter<ObItemRentDistributionAdapter.ViewHolder>() {



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

        if(obLeaseTenantPayload.renewLease!!){
            holder.editItemRdRentPrcnt!!.setText(list[position].rentPercentage)
            holder.txtItemRdAmt!!.setText(list[position].rentAmount)
            btn!!.isEnabled=true
            btn!!.setBackgroundResource(R.drawable.btn_green_round)
        }


        setReadOnly(holder.cbItemRdCoSigner!!,false, InputType.TYPE_NULL)
        setReadOnly(holder.cbItemRdCoTenant!!,false, InputType.TYPE_NULL)
        if(!list[position].coTenantFlag!! && list[position].coSignerFlag!!){
            holder.editItemRdRentPrcnt!!.visibility=View.INVISIBLE
            holder.txtItemRdAmt!!.visibility=View.INVISIBLE
        }else{
            holder.editItemRdRentPrcnt!!.visibility=View.VISIBLE
            holder.txtItemRdAmt!!.visibility=View.VISIBLE
        }

        holder.cbItemRdCoSigner.isChecked = list[position].coSignerFlag!!

        holder.cbItemRdCoTenant.isChecked = list[position].coTenantFlag!!
        if(position==0){
            holder.txtItemRdName.text =valueTenant+"*"
        }else{
            holder.txtItemRdName.text =valueTenant
        }

        holder.editItemRdRentPrcnt!!.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                try {
                    list[holder.adapterPosition].rentPercentage=holder.editItemRdRentPrcnt!!.text.toString()

                    if(validPrcnt()){
                        holder.editItemRdRentPrcnt?.error = context.getString(R.string.error_prcnt)
                        holder.editItemRdRentPrcnt?.requestFocus()

                    }else {
                        list[holder.adapterPosition].rentAmount = calAmt(holder.editItemRdRentPrcnt!!.text.toString(), holder.txtItemRdAmt!!)
                        holder.txtItemRdAmt!!.filters = arrayOf(DigitDecimalInputFilter(8, 2))
                        list[holder.adapterPosition].securityAmount = (((holder.editItemRdRentPrcnt!!.text.toString().toInt())*(obLeaseTenantPayload.securityAmount!!.toFloat().toInt()))/100).toString()
                        list[holder.adapterPosition].amenities= (((holder.editItemRdRentPrcnt!!.text.toString().toInt())*(obLeaseTenantPayload.amenities!!.toFloat().toInt()))/100).toString()


                        obLeaseTenantPayload.tenantBaseInfoDto=list
                    }
                }catch (e:Exception){
                    System.out.println("percentage "+e.localizedMessage)
                }

            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {

            }
        })


    }


    private fun calAmt(value:String,txt:TextView):String{
        var amt:Int?=0
        amt=((value.toInt())*(obLeaseTenantPayload.rentAmount!!.toFloat().toInt()))/100

        txt.text=amt.toString()

        return amt.toString()

    }



    private fun validPrcnt():Boolean{

        var totalPrcnt=0
        for(item in list){
            if(item.rentPercentage.isNullOrEmpty()) {
                totalPrcnt = totalPrcnt
            }else{
                totalPrcnt = totalPrcnt+ (item.rentPercentage!!.toInt())
            }
        }

        if(totalPrcnt>100){

            return true

        }else{
            if(totalPrcnt==100){
                btn!!.isEnabled=true
                btn!!.setBackgroundResource(R.drawable.btn_green_round)
            }else{
                btn!!.isEnabled=false
                btn!!.setBackgroundResource(R.drawable.btn_grey_round)
            }
            return false
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

        val txtItemRdName=itemView.txt_item_rd_name
        val editItemRdRentPrcnt = itemView.edit_item_rd_rent_prcnt
        val txtItemRdAmt=itemView.txt_item_rd_amt
        val cbItemRdCoSigner=itemView.cb_item_rd_co_signer
        val cbItemRdCoTenant=itemView.cb_item_rd_co_tenant


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(context).inflate(R.layout.item_ob_rent_distribution, parent, false)
        return ViewHolder(v)
    }


}