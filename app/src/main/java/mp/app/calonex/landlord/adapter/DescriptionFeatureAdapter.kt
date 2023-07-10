package mp.app.calonex.landlord.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_desc_feature.view.*
import mp.app.calonex.R


class DescriptionFeatureAdapter(
    var context: Context,
    val keys: ArrayList<String>,
    val keysValue: ArrayList<Boolean>
) : RecyclerView.Adapter<DescriptionFeatureAdapter.ViewHolder>() {


    var keyName = ArrayList<String>()
    var keyValue = ArrayList<Boolean>()


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        holder.txtFeature.text = keyName[position]



        if (keyName[position].equals("Swimming pool")) {
            //holder.imgFeature.setImageResource(R.drawable.swimming_active)
            if (keysValue[position]) {
                holder.imgFeature.setImageResource(R.drawable.ic_swimming_pool_active)
            } else {
                holder.imgFeature.setImageResource(R.drawable.ic_swimming_pool_inactive)
            }

        } else
            if (keyName[position].equals("Wheelchair Access")) {
                //holder.imgFeature.setImageResource(R.drawable.wheelchair_active)
                if (keysValue[position]) {
                    holder.imgFeature.setImageResource(R.drawable.ic_wheelchair_active)
                } else {
                    holder.imgFeature.setImageResource(R.drawable.ic_wheelchair_inactive)
                }
            } else
                if (keyName[position].equals("Pets")) {
                    //holder.imgFeature.setImageResource(R.drawable.pets_active)
                    if (keysValue[position]) {
                        holder.imgFeature.setImageResource(R.drawable.ic_pets_active)
                    } else {
                        holder.imgFeature.setImageResource(R.drawable.ic_pets_inactive)
                    }
                } else
                    if (keyName[position].equals("Elevator")) {
                        //holder.imgFeature.setImageResource(R.drawable.elevator_active)
                        if (keysValue[position]) {
                            holder.imgFeature.setImageResource(R.drawable.ic_lift_active)
                        } else {
                            holder.imgFeature.setImageResource(R.drawable.ic_lift_inactive)
                        }

                    } else if (keyName[position].equals("Door Man")) {

                        if (keysValue[position]) {
                            holder.imgFeature.setImageResource(R.drawable.ic_door_man_active)
                        } else {
                            holder.imgFeature.setImageResource(R.drawable.ic_door_man_inactive)
                        }

                    } else if (keyName[position].equals("Gym", true)) {
                        if (keysValue[position]) {
                            holder.imgFeature.setImageResource(R.drawable.ic_gym_active)
                        } else {
                            holder.imgFeature.setImageResource(R.drawable.ic_gym_inactive)
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
        val txtFeature = itemView.txt_feature!!
        val imgFeature = itemView.img_feature!!
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(context).inflate(R.layout.item_desc_feature, parent, false)

        return ViewHolder(v)
    }


}