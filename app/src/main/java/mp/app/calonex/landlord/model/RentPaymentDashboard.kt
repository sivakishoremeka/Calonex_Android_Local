package mp.app.calonex.landlord.model

data class RentPaymentDashboard(
    var latePayment: String = "",
    var onTimePayment: String = "",
    var defaulted: String = ""
)