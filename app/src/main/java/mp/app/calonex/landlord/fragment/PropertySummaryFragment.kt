package mp.app.calonex.landlord.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mp.app.calonex.R
import mp.app.calonex.landlord.adapter.PropertyListAdapter.Companion.propertyDetailResponse
import mp.app.calonex.landlord.adapter.SummaryAdapter
import mp.app.calonex.landlord.model.UnitDetailsPD
import java.util.*

class PropertySummaryFragment  : Fragment() {

    private var rvSummary: RecyclerView? = null

    var unitDetailsList = ArrayList<UnitDetailsPD>()

    private lateinit var appContext: Context
    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext =  context

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView =  inflater.inflate(R.layout.fragment_property_summary, container, false)

        rvSummary = rootView.findViewById<View>(R.id.rv_summary) as RecyclerView

        rvSummary = rootView.findViewById<View>(R.id.rv_summary) as RecyclerView

        var unitDetailsList = ArrayList<UnitDetailsPD>()
        unitDetailsList.addAll(propertyDetailResponse!!.propertyUnitDetailsDTO)
        val summaryAdapter = SummaryAdapter(appContext, unitDetailsList )
        //adapter.setData(unitDetailsList)

        val dividerItemDecoration =
            DividerItemDecoration(rvSummary!!.context, DividerItemDecoration.VERTICAL)
        dividerItemDecoration.setDrawable(
            ContextCompat.getDrawable(
                activity!!,
                R.drawable.item_list_divider
            )!!
        )

        rvSummary?.layoutManager = LinearLayoutManager(appContext)
        rvSummary?.adapter=summaryAdapter


        return rootView
    }


}