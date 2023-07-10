package mp.app.calonex.tenant.response.invoiceStats


import com.google.gson.annotations.SerializedName

data class TenantInvoiceStatDao(
    @SerializedName("data")
    var `data`: Data,
    @SerializedName("responseDto")
    var responseDto: ResponseDto
)