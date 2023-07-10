package mp.app.calonex.broker.responce

import com.google.gson.annotations.SerializedName
import mp.app.calonex.landlord.model.ResponseDto

data class BrokerAgentListResponse
    (var responseDto: ResponseDto?, var data: ArrayList<Data>?)

data class Data (

    @SerializedName("agentId"                  ) var agentId                  : String? = null,
    @SerializedName("agentName"                ) var agentName                : String? = null,
    @SerializedName("agentEmailId"             ) var agentEmailId             : String? = null,
    @SerializedName("agentPhoneNo"             ) var agentPhoneNo             : String? = null,
    @SerializedName("agentLicenseNo"           ) var agentLicenseNo           : String? = null,
    @SerializedName("location"                 ) var location                 : String? = null,
    @SerializedName("commissionPercentage"     ) var commissionPercentage     : String? = null,
    @SerializedName("brokerUserId"             ) var brokerUserId             : String? = null,
    @SerializedName("brokerFullName"           ) var brokerFullName           : String? = null,
    @SerializedName("status"                   ) var status                   : String? = null,
    @SerializedName("brokerLicenseNo"          ) var brokerLicenseNo          : String? = null,
    @SerializedName("createdOn"                ) var createdOn                : String? = null,
    @SerializedName("brokerAgentLinkRequestId" ) var brokerAgentLinkRequestId : String? = null

)