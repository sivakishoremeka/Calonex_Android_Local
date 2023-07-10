package mp.app.calonex.rentcx

import com.google.gson.annotations.SerializedName


data class MarketplaceMakePayRequestModel (
    @SerializedName("token"   ) var token   : String? = null,
    @SerializedName("total"   ) var total   : String? = null,
    @SerializedName("leaseId" ) var leaseId : String? = null,
    @SerializedName("unitId"  ) var unitId  : String? = null,
    @SerializedName("email"   ) var email   : String? = null,
    @SerializedName("userId"  ) var userId  : String? = null
)

