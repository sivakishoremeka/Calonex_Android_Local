package mp.app.calonex.broker.responce

import com.google.gson.annotations.SerializedName

data class BrokerFranchiseInfoResponse
    (
    @SerializedName("franchiseName" ) var franchiseName : String? = null,
    @SerializedName("percentage"    ) var percentage    : Int?    = 0
            )
