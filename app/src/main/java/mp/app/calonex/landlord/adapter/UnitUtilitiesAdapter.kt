package mp.app.calonex.landlord.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_switch.view.*
import mp.app.calonex.R
import mp.app.calonex.landlord.model.UnitUtility

class UnitUtilitiesAdapter(
    private val items: List<UnitUtility>,
    private val featureCheckboxCallback: FeatureCheckboxCallback
) :
    RecyclerView.Adapter<UnitUtilitiesAdapter.ViewHolder>() {


    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_switch, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.txtSwitch.text = items[position].utilitieName

        viewHolder.switchFeature.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            viewHolder.bind(items[position], featureCheckboxCallback.sendState(isChecked, items[position].utilityId, items[position].utilitieName))
        })

//        viewHolder.bind(items[position], clickListener)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val switchFeature = itemView.switch_feature!!
        val txtSwitch=itemView.txt_switch

        fun bind(unitUtility: UnitUtility, clickListener: Unit) {
            /* switchFeature.setOnClickListener { clickListener(buildingFeature)}*/
        }
    }
}
