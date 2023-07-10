package mp.app.calonex.models.broker


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("agentId")
    val agentId: Int,
    @SerializedName("agentPropertyId")
    val agentPropertyId: Int,
    @SerializedName("propertyId")
    val propertyId: Int,
    @SerializedName("status")
    val status: String
)