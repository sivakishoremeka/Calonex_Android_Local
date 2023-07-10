package mp.app.calonex.tenant.model

data class InvoiceCharge(
    val chargeAmount: Double,
    val chargeDescription: String,
    val chargeType: String,
    val invoiceId: Any,
    val subscriptionId: Any,
    val totalTax: Double
)