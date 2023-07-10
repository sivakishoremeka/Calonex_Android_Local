package mp.app.calonex.agent.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.agent_dashboard_property_list_item.view.*

import mp.app.calonex.R
import mp.app.calonex.agent.model.PropertyAgent


class AgentOnBoardedPropertyAdapter (
    var context: Context,
    var list: ArrayList<PropertyAgent>
) : RecyclerView.Adapter<AgentOnBoardedPropertyAdapter.ViewHolder>() {

    private var propertylist = ArrayList<PropertyAgent>()

    init {
        propertylist = list
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var Bp = propertylist.get(position);
        if (Bp.address == null || TextUtils.isEmpty(Bp.address)) {
            holder.locations.text = "NA"
        } else {
            holder.locations.text = Bp.address
        }
        if (Bp.brokerEmailID == null || TextUtils.isEmpty(Bp.brokerEmailID)) {
            holder.email.text = "NA"
        } else {
            holder.email.text = Bp.brokerEmailID
        }
        if (Bp.primaryContact == null || TextUtils.isEmpty(Bp.primaryContact)) {
            holder.phone.text = "NA"
        } else {
            holder.phone.text = Bp.primaryContact
        }

        if (Bp.broker == null || TextUtils.isEmpty(Bp.broker)) {
            holder.name.text = "NA"
        } else {
            holder.name.text = Bp.broker
        }
        try {
            Glide.with(context)
                .load(context.getString(R.string.base_url_img2) + Bp.fileList.get(0).fileName)
                .placeholder(R.drawable.bg_default_property)
                .into(holder.image);

        }catch (E:Exception){
            Glide.with(context)
                .load(R.drawable.bg_default_property)

                .into(holder.image);

        }





    }

    override fun getItemCount(): Int {
        return propertylist.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.name!!
        val locations = itemView.locations!!
        val email = itemView.email!!
        val phone = itemView.phone!!
        val image = itemView.property_image!!
        val viewassignedagent = itemView.viewassignedagent!!

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var v = LayoutInflater.from(context)
            .inflate(R.layout.agent_dashboard_property_list_item, parent, false)

        return ViewHolder(v)
    }


}