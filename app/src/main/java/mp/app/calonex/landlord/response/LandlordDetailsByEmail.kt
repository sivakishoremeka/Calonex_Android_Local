package mp.app.calonex.landlord.response

import com.google.gson.annotations.SerializedName

data class LandlordDetailsByEmail(
    @SerializedName("phoneNumber")
    var phoneNumber: String,
    @SerializedName("userCatalogId")
    var userCatalogId: Int,
    @SerializedName("userFullAddress")
    var userFullAddress: String,
    @SerializedName("userFullName")
    var userFullName: String
)