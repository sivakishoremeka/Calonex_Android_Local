package mp.app.calonex.landlord.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_desc_feature.view.*
import kotlinx.android.synthetic.main.item_desc_parking.view.*
import kotlinx.android.synthetic.main.item_desc_parking.view.img_feature
import mp.app.calonex.R

class DescriptionParkingAdapter(
    var context: Context,
    val keys: ArrayList<String>,
    val keysValue: ArrayList<Boolean>
) : RecyclerView.Adapter<DescriptionParkingAdapter.ViewHolder>() {


    var keyName = ArrayList<String>()
    var keyValue = ArrayList<Boolean>()


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtParking.text = keyName[position]
        /*if(!keyValue[position]){
            holder.txtParking.setBackgroundResource(R.drawable.btn_lt_grey_round)
            holder.txtParking.setTextColor(ContextCompat.getColor(context,android.R.color.black))
        }*/

        if (keyName[position].equals("On Street Parking")) {
            if (keysValue[position]) {
                holder.imgFeature.setImageResource(R.drawable.ic_parking_on_street_active)
            } else {
                holder.imgFeature.setImageResource(R.drawable.ic_parking_on_street_inactive)
            }

        } else if (keyName[position].equals("Off  Street Parking")) {
            if (keysValue[position]) {
                holder.imgFeature.setImageResource(R.drawable.ic_parking_off_street_active)
            } else {
                holder.imgFeature.setImageResource(R.drawable.ic_parking_off_street_inactive)
            }

        } else if (keyName[position].equals("Shared Driveway")) {
            if (keysValue[position]) {
                holder.imgFeature.setImageResource(R.drawable.ic_parking_shared_active)
            } else {
                holder.imgFeature.setImageResource(R.drawable.ic_parking_shared_inactive)
            }

        } else if (keyName[position].equals("Covered Parking")) {
            if (keysValue[position]) {
                holder.imgFeature.setImageResource(R.drawable.ic_parking_shaded_active)
            } else {
                holder.imgFeature.setImageResource(R.drawable.ic_parking_shaded_inactive)
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
        var v = LayoutInflater.from(context).inflate(R.layout.item_desc_parking, parent, false)
        return ViewHolder(v)
    }


}