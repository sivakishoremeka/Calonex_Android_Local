package mp.app.calonex.models.broker


import com.google.gson.annotations.SerializedName

data class AssignAgentResponseDao(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("responseDto")
    val responseDto: ResponseDto
)