package mp.app.calonex.registration.model

import com.google.gson.annotations.SerializedName

data class SubcriptionPlanModel (

    /*var paymentPlanMinUnit: String? = ""
    var paymentPlanMaxUnit: String? = ""
    var subscriptionPlanDuration: String? = ""
    var finalPrice: String? = ""
    var price: String? = ""
    var currentPlanText: String? = ""
    var isCreditCard: Boolean? = null
    var discount: String? = ""
    var creditCard: Boolean? = null*/


    @SerializedName("paymentPlanMinUnit"        ) var paymentPlanMinUnit        : String?  = "",
    @SerializedName("paymentPlanId"             ) var paymentPlanId             : String?  = "",
    @SerializedName("paymentPlanMaxUnit"        ) var paymentPlanMaxUnit        : String?  = "",
    @SerializedName("subscriptionPlanDuration"  ) var subscriptionPlanDuration  : String?  = "",
    @SerializedName("finalPrice"                ) var finalPrice                : String?  = "",
    @SerializedName("price"                     ) var price                     : String?  = "",
    @SerializedName("discount"                  ) var discount                  : String?  = "",
    @SerializedName("currentPlanText"           ) var currentPlanText           : String?  = "",
    @SerializedName("creditCard"                ) var creditCard                : Boolean? = null,
    @SerializedName("isCreditCard"              ) var TheCreditCard             : Boolean? = null

    /*
     {
      "paymentPlanMinUnit": "1",
      "paymentPlanMaxUnit": "4",
      "subscriptionPlanDuration": "1",
      "finalPrice": "69.95",
      "price": "69.95",
      "discount": "0",
      "creditCard": false,
      "isCreditCard": false
    }
     */

)