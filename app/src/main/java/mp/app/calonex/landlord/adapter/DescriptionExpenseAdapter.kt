package mp.app.calonex.landlord.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_desc_expense.view.*
import kotlinx.android.synthetic.main.item_view_expense.view.*
import mp.app.calonex.R

import mp.app.calonex.landlord.model.PropertyExpensePD

class DescriptionExpenseAdapter (var context: Context, val buildingExpenseList: ArrayList<PropertyExpensePD>) : RecyclerView.Adapter<DescriptionExpenseAdapter.ViewHolder>() {



    private var tasksExpense: ArrayList<PropertyExpensePD>



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtExpense.text=tasksExpense[position].propertyExpensesType+" : "
        //holder.txtValue.text= " $"+tasksExpense[position].expensesAmount
        holder.txtValue.text= if (tasksExpense[position].expensesAmount == null) " $0" else " $"+tasksExpense[position].expensesAmount



    }

    init {
        tasksExpense = buildingExpenseList
    }

    override fun getItemCount(): Int {
        return (buildingExpenseList.size )
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtExpense= itemView.txt_expense!!
        val txtValue= itemView.txt_value!!

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(context).inflate(R.layout.item_desc_expense, parent, false)
        return ViewHolder(v)
    }


}