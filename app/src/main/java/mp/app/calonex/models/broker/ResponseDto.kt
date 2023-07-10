package mp.app.calonex.models.broker


import com.google.gson.annotations.SerializedName

data class ResponseDto(
    @SerializedName("exceptionCode")
    val exceptionCode: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("responseCode")
    val responseCode: Int,
    @SerializedName("responseDescription")
    val responseDescription: String,
    @SerializedName("status")
    val status: Int
)