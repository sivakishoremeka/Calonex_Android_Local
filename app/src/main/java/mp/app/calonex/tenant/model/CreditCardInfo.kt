package mp.app.calonex.tenant.model

data class CreditCardInfo(

    val last4: String = "",
    val brand: String = "",
    val country: String = "",
    val exp_month: String = "",
    val exp_year: String = "",
    val funding: String = "",
    var name: String=""
)
