package mp.app.calonex.agent.fragment

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_property_description_agent.*
import mp.app.calonex.R
import mp.app.calonex.common.utility.Kotpref
import mp.app.calonex.common.utility.Util

import mp.app.calonex.landlord.adapter.DescriptionExpenseAdapter
import mp.app.calonex.landlord.adapter.DescriptionFeatureAdapter
import mp.app.calonex.landlord.adapter.DescriptionParkingAdapter

import mp.app.calonex.agent.adapter.PropertyListAdapterAgent
import mp.app.calonex.agent.adapter.PropertyListAdapterAgent.Companion.propertyDetailResponse
import mp.app.calonex.landlord.fragment.FullImageFragment
import mp.app.calonex.landlord.model.*
import mp.app.calonex.service.FeaturesService
import java.math.RoundingMode
import java.util.ArrayList

class PropertyDescriptionFragmentAgent  : Fragment() {

    private var rvFeature: RecyclerView? = null
    private var rvParking: RecyclerView? = null
    private var rvExpense: RecyclerView? = null
    private var imgProofOwn: ImageView?=null
    private var imageDocList= ArrayList<String>()


    lateinit var appContext: Context
    override fun onAttach(context: Context) {
        super.onAttach(context)
        appContext =  context

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView =  inflater.inflate(R.layout.fragment_property_description_agent, container, false)

        rvFeature = rootView.findViewById(R.id.rv_desc_feature)
        rvParking = rootView.findViewById(R.id.rv_desc_parking)
        rvExpense = rootView.findViewById(R.id.rv_desc_expense)
        imgProofOwn=rootView.findViewById(R.id.img_proof_own)


        return rootView
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        var featureListPD = ArrayList<BuildingFeaturePD>()
        var parkingListPD = ArrayList<ParkingTypePD>()
        var expenseListPD = ArrayList<PropertyExpensePD>()
        var defaultFeatureList = ArrayList<BuildingFeature>()
        var defaultParkingList = ArrayList<ParkingType>()
        var hashMapFeature:HashMap<String,Boolean> = HashMap<String,Boolean>()
        var hashMapParking:HashMap<String,Boolean> = HashMap<String,Boolean>()
        var listDefaultFeature=ArrayList<BuildingFeature>()
        var listDefaultParking=ArrayList<ParkingType>()
        featureListPD.addAll(propertyDetailResponse!!.buildingFeatureDTO)
        parkingListPD.addAll(propertyDetailResponse!!.parkingTypeDTO)
        expenseListPD.addAll(propertyDetailResponse!!.propertyExpensesDetailsDTO)
        if (FeaturesService.allModel != null) {
            listDefaultFeature =
                FeaturesService.allModel?.buildingFeatureListResponse?.data as ArrayList<BuildingFeature>
             listDefaultParking =
                FeaturesService.allModel?.parkingTypeListResponse?.data as ArrayList<ParkingType>
        }
        defaultFeatureList.addAll(listDefaultFeature)

        for(item in defaultFeatureList){

            hashMapFeature.put(item.buildingFeature,false)
        }
        for(item in featureListPD){

            if(hashMapFeature.containsKey(item.buildingFeatureName)){
                hashMapFeature.put(item.buildingFeatureName,true)
            }
        }

        defaultParkingList.addAll(listDefaultParking)
        for(item in defaultParkingList){

            hashMapParking.put(item.parkingType,false)
        }
        for(item in parkingListPD){

            if(hashMapParking.containsKey(item.parkingTypeName)){
                hashMapParking.put(item.parkingTypeName,true)
            }
        }


        val featureAdapter = DescriptionFeatureAdapter(appContext,ArrayList(hashMapFeature.keys),ArrayList(hashMapFeature.values))
        rvFeature?.layoutManager = GridLayoutManager(appContext,3)
        rvFeature?.adapter = featureAdapter

        val parkingAdapter = DescriptionParkingAdapter(appContext,ArrayList(hashMapParking.keys),ArrayList(hashMapParking.values))
        rvParking?.layoutManager = GridLayoutManager(appContext,2)
        rvParking?.adapter = parkingAdapter

        val expenseAdapter = DescriptionExpenseAdapter(appContext,expenseListPD)
        rvExpense?.layoutManager = GridLayoutManager(appContext,3)
        rvExpense?.adapter = expenseAdapter
        var docValue:String?=""
        for(item in PropertyListAdapterAgent.listPropertyImages)
        {
            if(item.uploadFileType.equals(getString(R.string.key_img_po))){
                docValue=item.fileName
                break
            }
        }
        if(docValue.isNullOrEmpty()){
            imgProofOwn!!.visibility=View.GONE
        }else {
            imageDocList.add(docValue)
            Glide.with(appContext)
                .load(getString(R.string.base_url_img) + docValue)
                .placeholder(Util.customProgress(appContext))
                .into(imgProofOwn!!)
        }
        txt_pd_about!!.text=propertyDetailResponse!!.description
        if(propertyDetailResponse!!.linkedByBrokerAgent){
            layout_brokr!!.visibility=View.VISIBLE
            txt_broker_name!!.text=propertyDetailResponse!!.broker
            txt_broker_email!!.text=propertyDetailResponse!!.brokerEmailID
            txt_broker_contact!!.text=
                PhoneNumberUtils.formatNumber(propertyDetailResponse!!.brokerPhone,"US")
            txt_licence_id!!.text=propertyDetailResponse!!.brokerOrAgentLiscenceID
        }else{
            layout_brokr!!.visibility=View.GONE
        }

        var totalExpenses = 0.00
        for (item in propertyDetailResponse!!.propertyExpensesDetailsDTO) {
            if (item.expensesAmount != null) {
                totalExpenses += item.expensesAmount.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
            }
        }

        if (Kotpref.loginRole == "agent") {
            ld_title!!.visibility = View.VISIBLE
            layout_landlord!!.visibility = View.VISIBLE
            txt_landlord_name!!.text = propertyDetailResponse.landlordFullName
            txt_landlord_email!!.text = propertyDetailResponse.landlordEmailId
        } else {
            ld_title!!.visibility = View.GONE
            layout_landlord!!.visibility = View.GONE
        }

        txt_revinew!!.text = "$" + propertyDetailResponse.totalRevenue

        if (totalExpenses == 0.00) {
            txt_expense!!.text = "$0.00"
        } else {
            txt_expense!!.text = "$" + totalExpenses.toString()
        }

        imgProofOwn!!.setOnClickListener {
            if(imageDocList.size>0){
                Kotpref.propertyImageIndex=0
                FullImageFragment(requireActivity()).customDialog(imageDocList)
            }

        }


    }


}