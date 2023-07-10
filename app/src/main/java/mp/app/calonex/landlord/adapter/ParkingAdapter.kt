package mp.app.calonex.landlord.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_desc_parking.view.*
import kotlinx.android.synthetic.main.item_switch.view.*
import mp.app.calonex.R
import mp.app.calonex.landlord.model.ParkingType

class ParkingAdapter(
    var context: Context,
    private val items: List<ParkingType>,
    private val parkingCheckboxCallback: ParkingCheckboxCallback
) :
    RecyclerView.Adapter<ParkingAdapter.ViewHolder>() {

    private var isOnStreet: Boolean = false
    private var isOffStreet: Boolean = false
    private var isShared: Boolean = false
    private var isCovered: Boolean = false

    private var parkingStatus: Boolean = false
    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_parking, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.txtParking.text = items[position].parkingType

        if (items[position].parkingType.equals("On Street Parking", false)) {
            isOnStreet = false
            viewHolder.imgFeature.setImageResource(R.drawable.ic_parking_on_street_inactive)
        } else if (items[position].parkingType.equals("Off Street Parking", false)) {
            isOffStreet = false
            viewHolder.imgFeature.setImageResource(R.drawable.ic_parking_off_street_inactive)
        }else if (items[position].parkingType.equals("Off  Street Parking", false)) {
            isOffStreet = false
            viewHolder.imgFeature.setImageResource(R.drawable.ic_parking_off_street_inactive)
        } else if (items[position].parkingType.equals("Shared Driveway", false)) {
            isShared = false
            viewHolder.imgFeature.setImageResource(R.drawable.ic_parking_shared_inactive)
        } else if (items[position].parkingType.equals("Covered Parking", false)) {
            isCovered = false
            viewHolder.imgFeature.setImageResource(R.drawable.ic_parking_shaded_inactive)
        }

        viewHolder.itemView.setOnClickListener {
          //  val bgColor = viewHolder.txtParking.background as ColorDrawable
          //  val colorId = bgColor.color

            Log.e("Check","parkingType===> ["+items[position].parkingType+"]")

            if (items[position].parkingType.equals("On Street Parking", false)) {
                if (!isOnStreet) {
                    isOnStreet = true
                    viewHolder.imgFeature.setImageResource(R.drawable.ic_parking_on_street_active)
                } else {
                    isOnStreet = false
                    viewHolder.imgFeature.setImageResource(R.drawable.ic_parking_on_street_inactive)
                }
                parkingStatus = isOnStreet

            }else if (items[position].parkingType.equals("Off Street Parking", false)) {
                if (!isOffStreet) {
                    isOffStreet = true
                    viewHolder.imgFeature.setImageResource(R.drawable.ic_parking_off_street_active)
                } else {
                    isOffStreet = false
                    viewHolder.imgFeature.setImageResource(R.drawable.ic_parking_off_street_inactive)
                }
                parkingStatus = isOffStreet

            }else if (items[position].parkingType.equals("Off  Street Parking", false)) {
                if (!isOffStreet) {
                    isOffStreet = true
                    viewHolder.imgFeature.setImageResource(R.drawable.ic_parking_off_street_active)
                } else {
                    isOffStreet = false
                    viewHolder.imgFeature.setImageResource(R.drawable.ic_parking_off_street_inactive)
                }
                parkingStatus = isOffStreet

            }else if (items[position].parkingType.equals("Shared Driveway", false)) {
                if (!isShared) {
                    isShared = true
                    viewHolder.imgFeature.setImageResource(R.drawable.ic_parking_shared_active)
                } else {
                    isShared = false
                    viewHolder.imgFeature.setImageResource(R.drawable.ic_parking_shared_inactive)
                }
                parkingStatus = isShared

            }else if (items[position].parkingType.equals("Covered Parking", false)) {
                if (!isCovered) {
                    isCovered = true
                    viewHolder.imgFeature.setImageResource(R.drawable.ic_parking_shaded_active)
                } else {
                    isCovered = false
                    viewHolder.imgFeature.setImageResource(R.drawable.ic_parking_shaded_inactive)
                }
                parkingStatus = isOnStreet

            }

            parkingCheckboxCallback.sendState(
                parkingStatus,
                items[position].parkingTypeId,
                items[position].parkingType
            )
        }


//        viewHolder.bind(items[position], clickListener)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtParking = itemView.txt_parking!!
        val imgFeature = itemView.img_feature!!
    }
}