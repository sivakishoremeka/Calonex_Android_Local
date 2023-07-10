package mp.app.calonex.landlord.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import mp.app.calonex.R
import mp.app.calonex.landlord.model.UserDetail

class SpinnerCustomAdapter(
    val context: Context,
    var listItemsTxt: ArrayList<String>) : BaseAdapter() {


    val mInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val vh: ItemRowHolder
        if (convertView == null) {
            view = mInflater.inflate(R.layout.item_spinner_custom, parent, false)
            vh = ItemRowHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ItemRowHolder
        }

        // make hint item color gray
        if(position == 0){
            vh.label.setTextColor(Color.GRAY)
        }else{
            vh.label.setTextColor(Color.BLACK)
        }

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
        return listItemsTxt.size
    }

    private class ItemRowHolder(row: View?) {

        val label: TextView

        init {
            this.label = row?.findViewById(R.id.txtDropDownLabel) as TextView
        }
    }
}