package mp.app.calonex.LdRegBankAdd

import com.google.gson.annotations.SerializedName

data class AddBankModel(
    @SerializedName("finalPrice"               ) var finalPrice               : String? = null,
    @SerializedName("numberOfUnits"            ) var numberOfUnits            : String? = null,
    @SerializedName("subscriptionPlanDuration" ) var subscriptionPlanDuration : String? = null,
    @SerializedName("bankUserName"             ) var bankUserName             : String? = null,
    @SerializedName("bankAccountNumber"        ) var bankAccountNumber        : String? = null,
    @SerializedName("routingNumber"            ) var routingNumber            : String? = null,
    @SerializedName("userCatalogId"            ) var userCatalogId            : String? = null
)
