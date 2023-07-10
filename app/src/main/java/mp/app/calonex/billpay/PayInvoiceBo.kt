package mp.app.calonex.billpay

import com.google.gson.annotations.SerializedName

data class PayInvoiceBo(

    @SerializedName("amount"   ) var amount   : String? = null,
    @SerializedName("invoiceId"   ) var invoiceId   : String? = null,
    //@SerializedName("leaseId" ) var leaseId : String? = null,
    @SerializedName("isCreditCard"  ) var isCreditCard  : Boolean? = null,
    @SerializedName("tenantId"   ) var tenantId   : String? = null,
    @SerializedName("token"  ) var token  : String? = null
)
