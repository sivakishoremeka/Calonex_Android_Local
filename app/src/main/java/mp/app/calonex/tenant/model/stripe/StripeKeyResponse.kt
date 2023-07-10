package mp.app.calonex.tenant.model.stripe

import com.google.gson.annotations.SerializedName

data class StripeKeyResponse(
    @SerializedName("stripeSecreteKey")
    var stripeSecreteKey: String = ""
)
