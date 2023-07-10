package mp.app.calonex.broker.responce

import com.google.gson.annotations.SerializedName
import mp.app.calonex.landlord.model.ResponseDto

data class BrokerInviteAgentResponse
    (var responseDto: ResponseDto?, var data: Data3)

data class Data3 (

    @SerializedName("deleted"                  ) var deleted                  : Boolean? = null,
    @SerializedName("createdOn"                ) var createdOn                : String?  = null,
    @SerializedName("brokerAgentLinkRequestId" ) var brokerAgentLinkRequestId : Int?     = null,
    @SerializedName("agentName"                ) var agentName                : String?  = null,
    @SerializedName("agentEmailId"             ) var agentEmailId             : String?  = null,
    @SerializedName("agentPhoneNo"             ) var agentPhoneNo             : String?  = null,
    @SerializedName("location"                 ) var location                 : String?  = null,
    @SerializedName("agentLicenseNo"           ) var agentLicenseNo           : String?  = null,
    @SerializedName("commissionPercentage"     ) var commissionPercentage     : Int?     = null,
    @SerializedName("brokerUserId"             ) var brokerUserId             : Int?     = null,
    @SerializedName("status"                   ) var status                   : String?  = null,
    @SerializedName("id"                       ) var id                       : Int?     = null

)