package mp.app.calonex.tenant.model

import mp.app.calonex.landlord.model.TenantInfoPayload

data class LeaseTenantInfo(

    val leaseId: String = "",
    val propertyId: String = "",
    val unitId: String = "",
    val unitNumber: String = "",
    val leaseStartDate: String = "",
    val leaseEndDate: String = "",
    val landlordId: String = "",
    val rentAmount: String = "",
    val leaseDuration: String = "",
    val agentId: String = "",
    val brokerId: String = "",
    val leaseStatus: String = "",
    val requestStatus: String = "",
    val uploadedBy: String = "",
    val securityAmount: String = "",
    val reviewByLandlord: Boolean = false,
    val freezedRecord: Boolean = false,
    val dontShowToLandlord: String = "",
    var tenantBaseInfoDto: ArrayList<TenantInfoPayload> = ArrayList<TenantInfoPayload>(),

    val landlordName: String = "",

    //Added 31-01-2023
    val applicantName: String = "",
    val tenantName: String = "",

    val brokerName: String = "",
    val agentName: String = "",
    val landlordEmailId: String = "",
    val brokerEmailId: String = "",
    val agentEmailId: String = "",
    val createdOn: String = "",
    val modifiedOn: String = "",
    val lateFee: String = "",
    val discount: String = "",
    val rentBeforeDueDate: String = "",
    val monthsFree: String = "",
    val propertyAddress: String = "",
    val propertyCity: String = "",
    val propertyState: String = "",
    val propertyZipCode: String = "",
    val landlordApprovalStatus: String = "",
    val leaseSigningStatus: String = "",
    val deadLine: String = "",
    val tenantDataModifed: Boolean = false,

    //for lease renewal

    val renewLease: Boolean = false,
    val oldLeaseId: String = ""




)
