package mp.app.calonex.landlord.model

data class LandlordPaymentHistory(
    var tenantName: String="",
    var propertyAddress: String="",
    var unitNumber: String="",
    var paymentType: String="",
    var amount: String="",
    var transactionId: String="",
    var paymentDate: String="",
    var status: String=""
    )