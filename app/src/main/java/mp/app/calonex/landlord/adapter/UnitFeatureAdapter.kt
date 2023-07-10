package mp.app.calonex.landlord.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_switch.view.*
import mp.app.calonex.R
import mp.app.calonex.landlord.model.UnitFeature

class UnitFeatureAdapter(
    private val items: List<UnitFeature>,
    private val featureCheckboxCallback: FeatureCheckboxCallback
) :
    RecyclerView.Adapter<UnitFeatureAdapter.ViewHolder>() {


    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_switch, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.txtSwitch.text = items[position].unitFeature

        /*viewHolder.itemView.setOnClickListener {
            viewHolder.switchFeature.toggle()
            var isChecked =  viewHolder.switchFeature.isChecked
            viewHolder.bind(items[position], featureCheckboxCallback.sendState(isChecked, items[position].unitFeatureId, items[position].unitFeature))
        }*/

        viewHolder.switchFeature.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            viewHolder.bind(items[position], featureCheckboxCallback.sendState(isChecked, items[position].unitFeatureId, items[position].unitFeature))
        })

//        viewHolder.bind(items[position], clickListener)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val switchFeature = itemView.switch_feature!!
        val txtSwitch=itemView.txt_switch

        fun bind(unitFeature: UnitFeature, clickListener: Unit) {
            /* switchFeature.setOnClickListener { clickListener(buildingFeature)}*/
        }
    }
}
