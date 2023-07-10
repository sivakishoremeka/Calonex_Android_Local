package mp.app.calonex.rentcx

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.gson.Gson
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_marketplace_details1.*
import mp.app.calonex.R
import mp.app.calonex.common.network.ApiInterface
import mp.app.calonex.common.network.RxAPICallHelper
import mp.app.calonex.common.network.RxAPICallback
import mp.app.calonex.landlord.activity.CxBaseActivity2


class MarketplaceDetails1Activity : CxBaseActivity2() {
    var fname1: String? = null
    var fname2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marketplace_details1)

        startHandler()

        val itemId = intent.getIntExtra("PropertyId", 0)
        if (itemId != null || itemId != 0) {
            getPropertyDetailsInfo(itemId.toString())
        }

        property_img_text.setOnClickListener {
            if (!fname2.isNullOrEmpty()) {
                Glide.with(this@MarketplaceDetails1Activity)
                    .load(this@MarketplaceDetails1Activity.getString(R.string.base_url_img2) + fname2)
                    //.placeholder(customPb)
                    .into(property_image)
            }
        }

        unit_img_text.setOnClickListener {
            if (!fname1.isNullOrEmpty()) {
                Glide.with(this@MarketplaceDetails1Activity)
                    .load(this@MarketplaceDetails1Activity.getString(R.string.base_url_img2) + fname1)
                    //.placeholder(customPb)
                    .into(property_image)
            }
        }

        copy_link_text.setOnClickListener {
            copyTextToClipboard("https://rentcx.calonex.com/properties/$itemId")
            // https://rentcx.calonex.com/properties/7253
            Toast.makeText(this@MarketplaceDetails1Activity, "Link copied", Toast.LENGTH_SHORT).show()
        }

        header_back.setOnClickListener {
            finish()
        }
    }

    private fun getPropertyDetailsInfo(itemId: String) {
        val propertyDetailsInfo: ApiInterface =
            ApiClient2(this).provideService(ApiInterface::class.java)
        val apiCall: Observable<PropertyDetailsDataResponce> =
            propertyDetailsInfo.getMarketplacePropertyDetails(itemId) //Test API Key

        RxAPICallHelper().call(apiCall, object : RxAPICallback<PropertyDetailsDataResponce> {
            override fun onSuccess(response: PropertyDetailsDataResponce) {

                if (response.property != null) {
                    /*marketplace_item_root!!*/

                    PropertyDetailsData = response

                    Log.e("MCX_LOG", Gson().toJson(response))

                    property_id.text = response.property!!.PropertyID.toString()
                    unit_number.text = response.property!!.UnitNumber.toString()
                    address_str.text = response.property!!.PropertyDetail!!.Address1.toString()
                    description_str.text =
                        response.property!!.PropertyDetail!!.Description.toString()
                    bedroom_text.text = response.property!!.UnitType!!.UnitType.toString()
                    bathroom_text.text = response.property!!.BathroomType!!.BathroomType.toString()
                    area_text.text = response.property!!.SquareFootArea.toString() + " Sq. Ft."
                    rent_text.text = "$" + response.property!!.RentPerMonth.toString()
                    security_text.text = "$" + response.property!!.SecurityAmount.toString()

                    var gym_flag = false
                    var sweeming_flag = false
                    for (item in response.property!!.PropertyBuildingFeatures) {
                        if (item.BuildingFeatureID == 6) {
                            gym_flag = true
                        }

                        if (item.BuildingFeatureID == 1) {
                            sweeming_flag = true
                        }
                    }

                    if (gym_flag) {
                        gym_text.text = "Yes"
                        gym_text.setTextColor(
                            ContextCompat.getColor(
                                this@MarketplaceDetails1Activity,
                                R.color.colorDeepGreen
                            )
                        )
                    } else {
                        gym_text.text = "No"
                        gym_text.setTextColor(
                            ContextCompat.getColor(
                                this@MarketplaceDetails1Activity,
                                R.color.colorDeepRed
                            )
                        )
                    }

                    if (sweeming_flag) {
                        sweeming_text.text = "Yes"
                        sweeming_text.setTextColor(
                            ContextCompat.getColor(
                                this@MarketplaceDetails1Activity,
                                R.color.colorDeepGreen
                            )
                        )
                    } else {
                        sweeming_text.text = "No"
                        sweeming_text.setTextColor(
                            ContextCompat.getColor(
                                this@MarketplaceDetails1Activity,
                                R.color.colorDeepRed
                            )
                        )
                    }

                    if (response.property!!.PropertyParkingDetails.size > 0) {
                        parking_text.text = "Yes"
                        parking_text.setTextColor(
                            ContextCompat.getColor(
                                this@MarketplaceDetails1Activity,
                                R.color.colorDeepGreen
                            )
                        )
                    } else {
                        parking_text.text = "No"
                        parking_text.setTextColor(
                            ContextCompat.getColor(
                                this@MarketplaceDetails1Activity,
                                R.color.colorDeepRed
                            )
                        )
                    }

                    fname1 = response.property!!.FilesDetails[0].fileName // unit img -- def
                    fname2 = response.property!!.PropertyDetail!!.FilesDetails[0].fileName // property img

                    // set property image
                    if (!fname1.isNullOrEmpty()) {
                        Glide.with(this@MarketplaceDetails1Activity)
                            .load(this@MarketplaceDetails1Activity.getString(R.string.base_url_img2) + fname1)
                            //.placeholder(customPb)
                            .into(property_image)
                    }

                    var list1: String? = ""
                    var list2: String? = ""
                    var list3: String? = ""
                    var list4: String? = ""

                    // Building Features
                    for (item in response.property!!.PropertyBuildingFeatures) {
                        list1 += item.BuildingFeature!!.BuildingFeature + "\n"
                    }

                    // Parking
                    for (item in response.property!!.PropertyParkingDetails) {
                        list2 += item.ParkingType!!.ParkingType + "\n"
                    }

                    // Unit Features
                    for (item in response.property!!.PropertyUnitFeatures) {
                        list3 += item.UnitFeature!!.UnitFeature + "\n"
                    }

                    // Utilities Included
                    for (item in response.property!!.PropertyUnitUtilities) {
                        list4 += item.Utility!!.UtilitieName + "\n"
                    }

                    list_1_text.text = list1
                    list_2_text.text = list2
                    list_3_text.text = list3
                    list_4_text.text = list4

                    property_apply_text.setOnClickListener {
                        val intent = Intent(this@MarketplaceDetails1Activity, MarketplaceDetails2Activity::class.java)
                        //intent.putExtra("PropertyId", listPayment[position].PropertyUnitID)
                        startActivity(intent)
                    }
                }
            }

            override fun onFailed(t: Throwable) {

            }
        })
    }

    private fun copyTextToClipboard(value: String) {
        val clipboardManager = this@MarketplaceDetails1Activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", value)
        clipboardManager.setPrimaryClip(clipData)
    }

    companion object {
        var PropertyDetailsData = PropertyDetailsDataResponce()
    }
}