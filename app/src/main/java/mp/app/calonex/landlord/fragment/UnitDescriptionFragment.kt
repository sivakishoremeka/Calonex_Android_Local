package mp.app.calonex.landlord.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import mp.app.calonex.R
import mp.app.calonex.landlord.adapter.PropertyListAdapter.Companion.propertyDetailResponse
import mp.app.calonex.landlord.adapter.UnitDescAdapter
import mp.app.calonex.landlord.model.UnitDetailsPD

class UnitDescriptionFragment  : Fragment() {

    private var rvUnit: RecyclerView? = null
    private var pbUnit: ProgressBar?=null

    lateinit var appContext: Context
    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext =  context

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView =  inflater.inflate(R.layout.fragment_unit_dedescription, container, false)


        rvUnit = rootView.findViewById<View>(R.id.rv_units) as RecyclerView
        pbUnit=rootView.findViewById(R.id.pb_unit)



        rvUnit?.layoutManager = LinearLayoutManager(appContext)

        val dividerItemDecoration =
            DividerItemDecoration(rvUnit!!.context, DividerItemDecoration.VERTICAL)
        dividerItemDecoration.setDrawable(
            ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.item_list_divider
            )!!
        )
        var unitDetailsList = java.util.ArrayList<UnitDetailsPD>()
        unitDetailsList.addAll(propertyDetailResponse!!.propertyUnitDetailsDTO)

        val unitDescAdapter = UnitDescAdapter(appContext,pbUnit!!,requireActivity(), unitDetailsList)
        rvUnit?.adapter = unitDescAdapter



        return rootView
    }


}