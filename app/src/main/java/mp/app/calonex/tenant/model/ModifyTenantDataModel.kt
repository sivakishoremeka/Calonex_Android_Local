package mp.app.calonex.tenant.model

data class ModifyTenantDataModel(

    val deleted: Boolean = false,
    val phoneVerified: Boolean = false,
    val coTenantFlag: Boolean = false,
    val coSignerFlag: Boolean = false,
    val landlordApprovalStatus: Boolean = false,
    val leaseSigningStatus: Boolean = false,

    var createdOn: String = "",
    var modifiedOn: String = "",
    var tenantId: String = "",
    var accountId: String = "",
    var tenantFirstName: String = "",
    var tenantLastName: String = "",
    var tenantMiddleName: String = "",

    var address: String = "",
    var city: String = "",
    var state: String = "",
    var zipCode: String = "",
    var phone: String = "",
    var emailId: String = "",
    var role: String = "",
    var licenseNo: String = "",
    var ssn: String = "",
    var rentPercentage: String = "",
    var securityAmount: String = "",
    var rentAmount: String = "",
    var rentAmountBeforeDueDate: String = "",
    var payDay: String = "",
    var cxId: String = "",


    var voidCheck: String = "",
    var monthPayStab: String = "",
    var signature: String = "",
    var leaseAgreementAcceptance: String = ""


)
