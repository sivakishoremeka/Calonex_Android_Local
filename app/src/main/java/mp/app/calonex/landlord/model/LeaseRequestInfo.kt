package mp.app.calonex.landlord.model

class LeaseRequestInfo {
    var action: String = ""
    var agentId: String = ""
    var amenities: String = ""
    var agentLicenseNumber: String = ""
    var brokerId: String = ""
    var comment: String = ""
    var landlordId: String = ""
    var lateFee: String = ""
    var leaseDuration: String = ""
    var leaseEndDate: String = ""
    var leaseId: String = ""
    var leaseSigningStatus: String = ""
    var leaseStartDate: String = ""
    var leaseStatus: String = ""
    var discount: String = ""
    var propertyId: String = ""
    var unitId: String = ""
    var unitNumber: String = ""



    var rentAmount: String = ""



    var uploadedBy: String = ""
    var securityAmount: String = ""
    var freezedRecord: Boolean = false
    var dontShowToLandlord: Boolean = false
    var tenantBaseInfoDto =ArrayList<ObTenantPayload>()


    var landlordName: String = ""
    var brokerName: String = ""
    var agentName: String = ""
    var landlordEmailId: String = ""
    var brokerEmailId: String = ""
    var agentEmailId: String = ""
    var createdOn: String = ""
    var modifiedOn: String = ""

    var rentBeforeDueDate: String = ""
    var monthsFree: String = ""
    var propertyAddress: String = ""
    var propertyCity: String = ""
    var propertyState: String = ""
    var propertyZipCode: String = ""

    var oldLeaseData: String = ""
    var brokerPhoneNumber: String = ""
    var agentPhoneNumber: String = ""
    var brokerLicenseNumber: String = ""

    var deleted:Boolean= false
}