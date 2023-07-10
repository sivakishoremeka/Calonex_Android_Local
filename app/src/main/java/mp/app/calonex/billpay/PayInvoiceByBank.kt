package mp.app.calonex.billpay

import com.google.gson.annotations.SerializedName

data class PayInvoiceByBank(
    @SerializedName("invoiceId") var invoiceId: String? = null,
    @SerializedName("tenantId") var tenantId: String? = null,
    @SerializedName("amount") var amount: String? = null,
    @SerializedName("bankAccountNumber") var bankAccountNumber: String? = null,
    @SerializedName("routingNumber") var routingNumber: String? = null,
    @SerializedName("bankUserName") var bankUserName: String? = null,
    @SerializedName("isCreditCard") var isCreditCard: Boolean? = null
)

/*
* {
  "invoiceId": 197153,
  "tenantId": 7156,
  "amount": 115,
  "bankAccountNumber": "U2FsdGVkX18ZNYvEQbo7wPpdPMakV54YrkF8zFYNmZ0=",
  "routingNumber": "U2FsdGVkX1+/PDLCPD3kkYktIHfx7DLz+FVN/DiI0Dw=",
  "bankUserName": "drags",
  "isCreditCard": false
}
* */
