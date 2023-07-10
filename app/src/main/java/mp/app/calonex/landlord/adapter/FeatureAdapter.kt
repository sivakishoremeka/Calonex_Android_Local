package mp.app.calonex.landlord.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_desc_feature.view.*
import mp.app.calonex.R
import mp.app.calonex.landlord.model.BuildingFeature

class FeatureAdapter(
    private val items: List<BuildingFeature>,
    private val featureCheckboxCallback: FeatureCheckboxCallback
) :
    RecyclerView.Adapter<FeatureAdapter.ViewHolder>() {

    private var isSwimmingPool: Boolean = false
    private var isWheelChair: Boolean = false
    private var isPets: Boolean = false
    private var isElevator: Boolean = false
    private var isDoorMan: Boolean = false
    private var isFeatureStatus: Boolean = false
    private var isGym: Boolean = false


    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_desc_feature, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.txtFeature.text = items[position].buildingFeature
        if (items[position].buildingFeature.equals("Swimming pool")) {
            isSwimmingPool = false
            viewHolder.imgFeature.setImageResource(R.drawable.ic_swimming_pool_inactive)
        } else if (items[position].buildingFeature.equals("Wheelchair Access")) {
            isWheelChair = false
            viewHolder.imgFeature.setImageResource(R.drawable.ic_wheelchair_inactive)
        } else if (items[position].buildingFeature.equals("Pets")) {
            isPets = false
            viewHolder.imgFeature.setImageResource(R.drawable.ic_pets_inactive_rounded)
        } else if (items[position].buildingFeature.equals("Elevator")) {
            isElevator = false
            viewHolder.imgFeature.setImageResource(R.drawable.ic_lift_inactive)
        } else if (items[position].buildingFeature.equals("Door Man")) {
            isDoorMan = false
            viewHolder.imgFeature.setImageResource(R.drawable.ic_door_man_inactive)
        } else if (items[position].buildingFeature.equals("Gym", true)) {
            isGym = false
            viewHolder.imgFeature.setImageResource(R.drawable.ic_gym_inactive)
        }



        viewHolder.itemView.setOnClickListener {
            if (items[position].buildingFeature.equals("Swimming pool")) {
                if (!isSwimmingPool) {
                    isSwimmingPool = true
                    viewHolder.imgFeature.setImageResource(R.drawable.ic_swimming_pool_active)
                } else {
                    isSwimmingPool = false
                    viewHolder.imgFeature.setImageResource(R.drawable.ic_swimming_pool_inactive)
                }
                isFeatureStatus = isSwimmingPool

            } else if (items[position].buildingFeature.equals("Wheelchair Access")) {
                if (!isWheelChair) {
                    isWheelChair = true
                    viewHolder.imgFeature.setImageResource(R.drawable.ic_wheelchair_active)
                } else {
                    isWheelChair = false
                    viewHolder.imgFeature.setImageResource(R.drawable.ic_wheelchair_inactive)
                }
                isFeatureStatus = isWheelChair
            } else if (items[position].buildingFeature.equals("Pets")) {
                if (!isPets) {
                    isPets = true
                    viewHolder.imgFeature.setImageResource(R.drawable.ic_pets_active)
                } else {
                    isPets = false
                    viewHolder.imgFeature.setImageResource(R.drawable.ic_pets_inactive_rounded)
                }
                isFeatureStatus = isPets
            } else if (items[position].buildingFeature.equals("Elevator")) {
                if (!isElevator) {
                    isElevator = true
                    viewHolder.imgFeature.setImageResource(R.drawable.ic_lift_active)
                } else {
                    isElevator = false
                    viewHolder.imgFeature.setImageResource(R.drawable.ic_lift_inactive)
                }
                isFeatureStatus = isElevator
            } else if (items[position].buildingFeature.equals("Door Man")) {
                if (!isDoorMan) {
                    isDoorMan = true
                    viewHolder.imgFeature.setImageResource(R.drawable.ic_door_man_active)
                } else {
                    isDoorMan = false
                    viewHolder.imgFeature.setImageResource(R.drawable.ic_door_man_inactive)
                }
                isFeatureStatus = isDoorMan
            } else if (items[position].buildingFeature.equals("Gym", true)) {
                if (!isGym) {
                    isGym = true
                    viewHolder.imgFeature.setImageResource(R.drawable.ic_gym_active)
                } else {
                    isGym = false
                    viewHolder.imgFeature.setImageResource(R.drawable.ic_gym_inactive)
                }
                isFeatureStatus = isGym
            }
            featureCheckboxCallback.sendState(
                isFeatureStatus,
                items[position].buildingFeatureId,
                items[position].buildingFeature
            )

        }


//        viewHolder.bind(items[position], clickListener)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtFeature = itemView.txt_feature!!
        val imgFeature = itemView.img_feature!!
    }
}
