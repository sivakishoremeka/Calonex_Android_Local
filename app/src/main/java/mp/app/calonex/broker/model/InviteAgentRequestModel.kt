package mp.app.calonex.broker.model

import com.google.gson.annotations.SerializedName

data class InviteAgentRequestModel(
    @SerializedName("agentName"            ) var agentName            : String? = null,
    @SerializedName("agentEmailId"         ) var agentEmailId         : String? = null,
    @SerializedName("agentPhoneNo"         ) var agentPhoneNo         : String? = null,
    @SerializedName("location"             ) var location             : String? = null,
    @SerializedName("agentLicenseNo"       ) var agentLicenseNo       : String? = null,
    @SerializedName("commissionPercentage" ) var commissionPercentage : String? = null,
    @SerializedName("brokerFullName"       ) var brokerFullName       : String? = null,
    @SerializedName("brokerUserId"         ) var brokerUserId         : String? = null
)
