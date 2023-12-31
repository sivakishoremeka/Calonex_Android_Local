package mp.app.calonex.tenant.model

data class GeneralReportDataModel(
    val csn_No: String,
    val cxScoreLeaseDetails: ArrayList<CxScoreLeaseDetail>,
    val cx_Score: Int,
    val employer: String,
    val employerAddress: String,
    val id: Int,
    val landlordId: Int,
    val noOfJobChangeInYear: Int,
    val presentStatus: String,
    val reportDate: String,
    val reportNo: String,
    val scoreStatus: String,
    val tenantAddress: String,
    val tenantEmail: String,
    val tenantId: Int,
    val tenantName: String,
    val tenantStatus: String,
    val totalAddressChangeInYear: Int,
    val totalAmountCollected: Double,
    val totalAmountOwed: Double,
    val totalDefaulterCount: Int,
    val totalDepositPaidCount: Int,
    val totalDepositRefunded: Double,
    val totalEvictionCount: Int,
    val totalJudgeMentCount: Int,
    val totalLatePaymentCount: Int,
    val totalPaidOnTimeCount: Int,
    val totalPaymentWithHeldCount: Int,
    val yearsEmployed: Int
)