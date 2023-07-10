package mp.app.calonex.tenant.response.invoiceStats


import com.google.gson.annotations.SerializedName

data class ResponseDto(
    @SerializedName("exceptionCode")
    var exceptionCode: Int,
    @SerializedName("responseCode")
    var responseCode: Int,
    @SerializedName("responseDescription")
    var responseDescription: String
)