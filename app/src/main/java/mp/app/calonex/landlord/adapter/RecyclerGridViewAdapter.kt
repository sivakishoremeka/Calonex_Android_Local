package mp.app.calonex.landlord.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mp.app.calonex.landlord.holder.BuildingFeatureListHolder
import mp.app.calonex.R
import mp.app.calonex.landlord.model.BuildingFeature

internal class RecyclerGridViewAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var listOfBf = listOf<BuildingFeature>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return BuildingFeatureListHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_switch,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = listOfBf.size

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val movieViewHolder = viewHolder as BuildingFeatureListHolder
        movieViewHolder.bindView(listOfBf[position])
    }

    fun setBFList(listOfMovies: List<BuildingFeature>?) {
        if (listOfMovies != null) {
            this.listOfBf = listOfMovies
        }
        notifyDataSetChanged()
    }
}