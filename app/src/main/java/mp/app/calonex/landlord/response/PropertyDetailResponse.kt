package mp.app.calonex.landlord.response

import com.google.gson.annotations.SerializedName
import mp.app.calonex.landlord.model.PropertyDetail

data class PropertyDetailResponse
    (@SerializedName("responseCode"             )var responseCode: Int?,
     @SerializedName("exceptionCode"            )var exceptionCode: Int?,
     @SerializedName("data"                     )var data: PropertyDetail?,
     @SerializedName("responseDescription"      )var responseDescription: String?)