package mp.app.calonex.LdRegBankAdd

import com.google.gson.annotations.SerializedName

data class AddBankModelResponse(
    @SerializedName("responseDto" ) var responseDto : ResponseDto? = ResponseDto(),
    @SerializedName("data"        ) var data        : Data?        = Data()
)

data class ResponseDto (

    @SerializedName("responseCode"        ) var responseCode        : Int?    = null,
    @SerializedName("responseDescription" ) var responseDescription : String? = null,
    @SerializedName("exceptionCode"       ) var exceptionCode       : Int?    = null

)

data class Data (

    @SerializedName("subscriptionDetailsId" ) var subscriptionDetailsId : Int?    = null,
    @SerializedName("stripeCustomerId"      ) var stripeCustomerId      : String? = null

)