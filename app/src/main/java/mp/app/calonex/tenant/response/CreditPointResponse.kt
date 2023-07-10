package mp.app.calonex.tenant.response

import com.google.gson.annotations.SerializedName

data class CreditPointResponse (
    @SerializedName("responseDto" ) var responseDto : ResponseDto?    = ResponseDto(),
    @SerializedName("data"        ) var data        : ArrayList<Data> = arrayListOf()
)

data class Data (
    @SerializedName("date"          ) var date          : Long?     = null,
    @SerializedName("amount"        ) var amount        : String?  = null,
    @SerializedName("property"      ) var property      : String?  = null,
    @SerializedName("transactionId" ) var transactionId : String?  = null,
    @SerializedName("reversal"      ) var reversal      : Boolean? = null,
    @SerializedName("paymentStatus" ) var paymentStatus : String?  = null,
    @SerializedName("invoiceDate"   ) var invoiceDate   : Long?     = null
)

data class ResponseDto (
    @SerializedName("responseCode"        ) var responseCode        : Int?    = null,
    @SerializedName("responseDescription" ) var responseDescription : String? = null,
    @SerializedName("exceptionCode"       ) var exceptionCode       : Int?    = null,
    @SerializedName("message"             ) var message             : String? = null
)