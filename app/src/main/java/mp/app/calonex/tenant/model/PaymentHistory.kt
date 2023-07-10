package mp.app.calonex.tenant.model

import com.google.gson.annotations.SerializedName

data class PaymentHistory(

  /*  val amount: String = "",
    val property: String = "",
    val date: String = "",
    val security: String = "",
    val reversal: String = "",
    val invoiceDate: String = "",
    val paymentStatus : String = "",
    val transactionId : String= ""*/


    @SerializedName("date"          ) var date          : String?     = null,
    @SerializedName("amount"        ) var amount        : String?  = null,
    @SerializedName("property"      ) var property      : String?  = null,
    @SerializedName("transactionId" ) var transactionId : String?  = null,
    @SerializedName("reversal"      ) var reversal      : Boolean? = null,
    @SerializedName("paymentStatus" ) var paymentStatus : String?  = null,
    @SerializedName("invoiceDate"   ) var invoiceDate   : String?     = null
)
