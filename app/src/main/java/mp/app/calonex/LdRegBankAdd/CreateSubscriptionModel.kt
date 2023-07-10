package mp.app.calonex.LdRegBankAdd

import com.google.gson.annotations.SerializedName

data class CreateSubscriptionModel(
    @SerializedName("finalPrice")               var finalPrice: String? = null,
    @SerializedName("numberOfUnits")            var numberOfUnits: String? = null,
    @SerializedName("subscriptionPlanDuration") var subscriptionPlanDuration: String? = null,
    @SerializedName("paymentPlanId")            var paymentPlanId: String? = null,
    @SerializedName("isAutoPay")                var isAutoPay: String? = null,
    @SerializedName("isCreditCard")             var isCreditCard: Boolean? = null,
    @SerializedName("bankUserName")             var bankUserName: String? = null,
    @SerializedName("bankAccountNumber")        var bankAccountNumber: String? = null,
    @SerializedName("routingNumber")            var routingNumber: String? = null,
    @SerializedName("userCatalogId")            var userCatalogId: String? = null
)
/*
*
{
    "finalPrice": "69.95",
    "numberOfUnits": "4",
    "subscriptionPlanDuration": "1",
    "paymentPlanId": "1",
    "isAutoPay": 0,
    "isCreditCard": false,
    "bankAccountNumber": "U2FsdGVkX1/XL3ifaupLDe1J2OSOSVy25ZBcZwSsqK0=",
    "routingNumber": "U2FsdGVkX1+24mx5M15HTYnxP2noYFv1osCv0GUqruk=",
    "userCatalogId": "6919"
}
* */