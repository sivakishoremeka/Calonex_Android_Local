package mp.app.calonex.rentcx


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_marketplace_list.view.*
import mp.app.calonex.R
import java.util.*
import kotlin.collections.ArrayList

class MarketplaceListAdapter(
    var context: Context,
    var activity: FragmentActivity?,
    var list: ArrayList<Properties>
) : RecyclerView.Adapter<MarketplaceListAdapter.ViewHolder>(),
    Filterable {

    private var listPayment = ArrayList<Properties>()

    init {
        listPayment = list
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.property_id.text = listPayment[position].PropertyID.toString()
        holder.unit_number.text = listPayment[position].UnitNumber.toString()
        holder.address_str.text = listPayment[position].PropertyDetail!!.Address1.toString()
        holder.description_str.text = listPayment[position].PropertyDetail!!.Description.toString()
        holder.bedroom_text.text = listPayment[position].UnitType!!.UnitType.toString()
        holder.bathroom_text.text = listPayment[position].BathroomType!!.BathroomType.toString()
        holder.area_text.text = listPayment[position].SquareFootArea.toString() + " Sq. Ft."
        holder.rent_text.text = "$" + listPayment[position].RentPerMonth.toString()
        holder.security_text.text = "$" + listPayment[position].SecurityAmount.toString()

        var gym_flag = false
        var sweeming_flag = false
        for (item in listPayment[position].PropertyBuildingFeatures) {
            if (item.BuildingFeatureID == 6) {
                gym_flag = true
            }

            if (item.BuildingFeatureID == 1) {
                sweeming_flag = true
            }
        }

        if (gym_flag) {
            holder.gym_text.text = "Yes"
            holder.gym_text.setTextColor(ContextCompat.getColor(context, R.color.colorDeepGreen))
        } else {
            holder.gym_text.text = "No"
            holder.gym_text.setTextColor(ContextCompat.getColor(context, R.color.colorDeepRed))
        }

        if (sweeming_flag) {
            holder.sweeming_text.text = "Yes"
            holder.sweeming_text.setTextColor(ContextCompat.getColor(context, R.color.colorDeepGreen))
        } else {
            holder.sweeming_text.text = "No"
            holder.sweeming_text.setTextColor(ContextCompat.getColor(context, R.color.colorDeepRed))
        }

        if (listPayment[position].PropertyParkingDetails.size > 0) {
            holder.parking_text.text = "Yes"
            holder.parking_text.setTextColor(ContextCompat.getColor(context, R.color.colorDeepGreen))
        } else {
            holder.parking_text.text = "No"
            holder.parking_text.setTextColor(ContextCompat.getColor(context, R.color.colorDeepRed))
        }

        //var fname = listPayment[position].FilesDetails[0].fileName
        var fname = listPayment[position].PropertyDetail!!.FilesDetails[0].fileName

        // set property image
        if (!fname.isNullOrEmpty()) {
            Glide.with(context)
                .load(context.getString(R.string.base_url_img2) + fname)
                //.placeholder(customPb)
                .into(holder.property_image)
        }

        //holder.ivCopy.setOnClickListener {
            //copyTextToClipboard(listPayment[holder.adapterPosition].transactionId)
        //}

        holder.marketplace_item_root.setOnClickListener {

            val intent = Intent(activity!!, MarketplaceDetails1Activity::class.java)
            intent.putExtra("PropertyId", listPayment[position].PropertyUnitID)
            activity!!.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return listPayment.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val unit_number = itemView.unit_number!!
        val property_id = itemView.property_id!!
        val address_str = itemView.address_str!!
        val description_str = itemView.description_str!!
        val bedroom_text = itemView.bedroom_text!!
        val bathroom_text = itemView.bathroom_text!!
        val area_text = itemView.area_text!!
        val rent_text = itemView.rent_text!!
        val security_text = itemView.security_text!!
        val gym_text = itemView.gym_text!!
        val sweeming_text = itemView.sweeming_text!!
        val parking_text = itemView.parking_text!!
        val property_image = itemView.property_image!!
        val marketplace_item_root = itemView.marketplace_item_root!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(context).inflate(R.layout.item_marketplace_list, parent, false)

        return ViewHolder(v)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    listPayment = list
                } else {
                    val resultList = ArrayList<Properties>()
                    for (row in list) {
                        if (//row.ZipCode!!.lowercase(Locale.getDefault()).contains(charSearch) ||
                            row.RentPerMonth.toString().contains(charSearch) ||
                            row.SecurityAmount.toString().contains(charSearch) ||
                            row.DateAvilable!!.lowercase(Locale.getDefault()).contains(charSearch)
                        ) {
                            resultList.add(row)
                        }
                    }
                    listPayment = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = listPayment
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                listPayment = results?.values as ArrayList<Properties>
                notifyDataSetChanged()
            }
        }
    }

    /*private fun copyTextToClipboard(value: String) {
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", value)
        clipboardManager.setPrimaryClip(clipData)
    }*/
}