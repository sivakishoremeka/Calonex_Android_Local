package mp.app.calonex.rentcx

import com.google.gson.annotations.SerializedName


data class ApplicationFeesResponce (

    @SerializedName("applicationfee" ) var applicationfee : String? = null,
    @SerializedName("total"          ) var total          : String? = null,
    @SerializedName("message"        ) var message          : String? = null

)