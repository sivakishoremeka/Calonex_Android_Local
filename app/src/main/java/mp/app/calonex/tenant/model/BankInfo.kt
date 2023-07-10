package mp.app.calonex.tenant.model

data class BankInfo(
    val userId: String = "",
    val userName: String = "",
    val accountNo: String = "",
    val routingNo: String = "",
    val autoPay: Boolean = false
)
