package mp.app.calonex.landlord.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import mp.app.calonex.R
import java.util.*

class CustomAutoAdapter(val context: Context?, var listItemsTxt: ArrayList<String>) : BaseAdapter(),Filterable {
    val mInflater: LayoutInflater = LayoutInflater.from(context)
    private var listData = ArrayList<String>()

    init {
        listData = listItemsTxt
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val vh: ItemRowHolder
        if (convertView == null) {
            view = mInflater.inflate(R.layout.view_drop_down_menu, parent, false)
            vh = ItemRowHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ItemRowHolder
        }


            vh.label.setTextColor(Color.BLACK)


        vh.label.text = listItemsTxt[position]

        return view
    }

    override fun getItem(position: Int): Any? {

        return null

    }

    override fun getItemId(position: Int): Long {

        return 0

    }

    override fun getCount(): Int {
        return listItemsTxt!!.size
    }

    private class ItemRowHolder(row: View?) {

        val label: TextView

        init {
            this.label = row?.findViewById(R.id.txtDropDownLabel) as TextView
        }
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    listData = listItemsTxt
                } else {
                    val resultList = ArrayList<String>()
                    for (row in listItemsTxt) {
                        if (row.toLowerCase().contains(charSearch.toLowerCase())
                        ) {
                            resultList.add(row)
                        }
                    }
                    listData = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = listData
                return filterResults
            }


            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                listData = results?.values as ArrayList<String>
                notifyDataSetChanged()
            }

        }
    }

}
