package mp.app.calonex.tenant.model

data class CxScoreLeaseDetail(
    val amountCollected: Double,
    val amountOwed: Double,
    val collectedBy: String,
    val collectedDate: String,
    val csn_No: String,
    val defaulterCount: Int,
    val depositPaid: Boolean,
    val depositPaidCount: Int,
    val evictionCount: Int,
    val id: Int,
    val judgeMentCount: Int,
    val landLordAddress: String,
    val landLordName: String,
    val landlordId: String,
    val latePaymentCount: Int,
    val leaseEnd: String,
    val leaseStart: String,
    val paidOnTimeCount: Int,
    val paymentWithHeldCount: Int,
    val releaseType: String,
    val tenantAddress: String,
    val tenantId: String
)