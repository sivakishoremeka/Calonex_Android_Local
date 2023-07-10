package mp.app.calonex.tenant.model

data class Lease(

    val leaseStartDate: Long = 0,
    val rentAmount: String = "",
    val leaseDuration: String = "",
    val securityDeposit: String = ""
)
