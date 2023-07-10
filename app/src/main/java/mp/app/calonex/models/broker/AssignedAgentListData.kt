package mp.app.calonex.models.broker


import com.google.gson.annotations.SerializedName

data class AssignedAgentListData(
    @SerializedName("data")
    val `assignedAgents`: ArrayList<AssignedAgentModel>,
    @SerializedName("responseDto")
    val responseDto: ResponseDto
)