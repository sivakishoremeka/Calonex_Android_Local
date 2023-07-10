package mp.app.calonex.LdRegBankAdd


import com.google.gson.annotations.SerializedName

data class CreateNewSubscriptionResponseModel(
    @SerializedName("data")
    var `data`: CreateSubscriptionData,
    @SerializedName("responseDto")
    var responseDto: CreateSubscriptionResponseDto
)


data class CreateSubscriptionData(
    @SerializedName("stripeCustomerId")
    var stripeCustomerId: String,
    @SerializedName("subscriptionDetailsId")
    var subscriptionDetailsId: Int
)

data class CreateSubscriptionResponseDto(
    @SerializedName("exceptionCode")
    var exceptionCode: Int,
    @SerializedName("responseCode")
    var responseCode: Int,
    @SerializedName("responseDescription")
    var responseDescription: String
)