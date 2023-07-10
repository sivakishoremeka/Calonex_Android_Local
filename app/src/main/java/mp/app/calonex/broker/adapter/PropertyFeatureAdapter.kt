package mp.app.calonex.broker.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_desc_feature.view.*
import kotlinx.android.synthetic.main.item_desc_parking.view.*
import kotlinx.android.synthetic.main.item_desc_parking.view.img_feature
import mp.app.calonex.R

class PropertyFeatureAdapter(
    var context: Context,
    val keys: ArrayList<String>,
    val keysValue: ArrayList<Boolean>
) : RecyclerView.Adapter<PropertyFeatureAdapter.ViewHolder>() {


    var keyName = ArrayList<String>()
    var keyValue = ArrayList<Boolean>()


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtParking.text = keyName[position]

       // Log.e("Feature","Name >> "+keyName[position])
        //Log.e("Feature","Value >> "+keyValue[position])


        if (keyName[position].equals("Air Conditioning")) {
            if (keysValue[position]) {
                holder.imgFeature.setImageResource(R.drawable.ic_air_condition_active)
            } else {
                holder.imgFeature.setImageResource(R.drawable.ic_air_condition_inactive)
            }

        } else if (keyName[position].equals("Dishwasher")) {
            if (keysValue[position]) {
                holder.imgFeature.setImageResource(R.drawable.ic_dishwasher_active)
            } else {
                holder.imgFeature.setImageResource(R.drawable.ic_dishwasher_inactive)
            }

        } else if (keyName[position].equals("In-Unit Laundry")) {
            if (keysValue[position]) {
                holder.imgFeature.setImageResource(R.drawable.ic_unit_laundry_active)
            } else {
                holder.imgFeature.setImageResource(R.drawable.ic_unit_laundry_inactive)
            }

        } else if (keyName[position].equals("Pets OK")) {
            if (keysValue[position]) {
                holder.imgFeature.setImageResource(R.drawable.ic_pets_active_without_round)
            } else {
                holder.imgFeature.setImageResource(R.drawable.ic_pets_inactive)
            }

        } else if (keyName[position].equals("Water")) {
            if (keysValue[position]) {
                holder.imgFeature.setImageResource(R.drawable.ic_water_active)
            } else {
                holder.imgFeature.setImageResource(R.drawable.ic_water_inactive)
            }

        } else if (keyName[position].equals("Cable")) {
            if (keysValue[position]) {
                holder.imgFeature.setImageResource(R.drawable.ic_cable_active)
            } else {
                holder.imgFeature.setImageResource(R.drawable.ic_cable_inactive)
            }

        } else if (keyName[position].equals("Heat")) {
            if (keysValue[position]) {
                holder.imgFeature.setImageResource(R.drawable.ic_heat_active)
            } else {
                holder.imgFeature.setImageResource(R.drawable.ic_heat_inactive)
            }

        } else if (keyName[position].equals("Internet")) {
            if (keysValue[position]) {
                holder.imgFeature.setImageResource(R.drawable.ic_internet_active)
            } else {
                holder.imgFeature.setImageResource(R.drawable.ic_internet_inactive)
            }

        }


    }

    init {
        keyName = keys
        keyValue = keysValue
    }

    override fun getItemCount(): Int {
        return (keys.size)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtParking = itemView.txt_parking!!
        val imgFeature = itemView.img_feature!!


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(context).inflate(R.layout.item_property_feature, parent, false)
        return ViewHolder(v)
    }


}