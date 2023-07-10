package mp.app.calonex.landlord.adapter

import android.content.Context
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_ob_tenant.view.*
import mp.app.calonex.R
import mp.app.calonex.landlord.model.ObTenantPayload

class ObItemTenantAdapter(var context: Context, val list: ArrayList<ObTenantPayload>) :
    RecyclerView.Adapter<ObItemTenantAdapter.ViewHolder>() {


    private var valueTenant: String? = null
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        if (list[position].tenantFirstName.isNullOrEmpty() && list[position].tenantLastName.isNullOrEmpty()) {

            valueTenant = list[position].emailId + " " + list[position].tenantLastName
        } else {
            if (list[position].tenantFirstName.isNullOrEmpty()) {
                valueTenant = list[position].tenantLastName
            } else if (list[position].tenantLastName.isNullOrEmpty()) {
                valueTenant = list[position].tenantFirstName
            } else {
                valueTenant = list[position].tenantFirstName + " " + list[position].tenantLastName
            }
        }




        setReadOnly(holder.cbItemObCoSigner!!, false, InputType.TYPE_NULL)
        setReadOnly(holder.cbItemObCoTenant!!, false, InputType.TYPE_NULL)
        if (list[position].coSignerFlag!!) {
            holder.cbItemObCoSigner.isChecked = true
        } else {
            holder.cbItemObCoSigner.isChecked = false
        }

        if (list[position].coTenantFlag!!) {
            holder.cbItemObCoTenant.isChecked = true
        } else {
            holder.cbItemObCoTenant.isChecked = false
        }


        if (position == 0) {
            holder.txtItemObFname.text = valueTenant + "*"
            holder.cbItemObCoSigner.isChecked = true
            holder.cbItemObCoTenant.isChecked = true
            holder.cbItemObCoSigner.visibility = View.INVISIBLE
            //holder.cbItemObCoTenant.visibility = View.INVISIBLE
        } else {
            holder.txtItemObFname.text = valueTenant
            holder.cbItemObCoSigner.visibility = View.VISIBLE
            //holder.cbItemObCoTenant.visibility = View.INVISIBLE
        }

        holder.txtRentPercentage.text = list[position].rentPercentage

        holder.imgItemObDel!!.setOnClickListener {
            deleteItem(holder.adapterPosition)
        }

    }

    fun deleteItem(position: Int) {
        list.removeAt(position)
        notifyDataSetChanged()
    }

    private fun setReadOnly(checkBox: CheckBox, value: Boolean, input: Int) {
        checkBox.isFocusable = value
        checkBox.isClickable = value
        checkBox.isFocusable = value
        checkBox.inputType = input
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtItemObFname = itemView.txt_item_ob_name!!
        val cbItemObCoSigner = itemView.cb_item_ob_co_signer!!
        val cbItemObCoTenant = itemView.cb_item_ob_co_tenant!!
        val imgItemObDel = itemView.img_item_ob_del!!
        val txtRentPercentage = itemView.txt_item_ob_percentage!!

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(context).inflate(R.layout.item_ob_tenant, parent, false)
        return ViewHolder(v)
    }


}