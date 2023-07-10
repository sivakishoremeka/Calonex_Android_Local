package mp.app.calonex.landlord.model


class ObLeaseTenantPayload {

    var action:String? = ""
    var agentEmailId:String? = ""
    var agentId:String? = ""
    var agentLicenseNumber:String? = ""
    var agentName:String? = ""
    var agentPhoneNumber:String? = ""
    var amenities:String? = ""
    var brokerId:String? = ""
    var brokerEmailId:String? = ""
    var brokerLicenseNumber:String? = ""
    var brokerName:String? = ""
    var brokerPhoneNumber:String? = ""
    var comment:String? = ""
    var deadline:String? = ""
    var directAccess:String? = ""

    var dontShowToLandlord:Boolean? =  false
    var editorId:String? = ""
    var existingTenant:Boolean? =  false
    var freezedRecord:Boolean? =  false
    var password:String? = ""
    var landlordId:String? = ""
    var lateFee:String? = ""
    var leaseDuration:String? = ""
    var leaseEndDate:String? = ""
    var leaseId:String? = ""
    var leaseSigningStatus:String? = ""
    var leaseStartDate:String? = ""
    var leaseStatus:String? = ""
    var leaseTerminated:Boolean? =  false
    var monthFreeStandards:Boolean? =  false
    var modifyByRole:String? = ""
    var monthsFree:String? = ""
    var oldLeaseId:String? = ""
    var propertyId:String? = ""
    var renewLease:Boolean? =  false
    var rentAmount:String? = ""
    var rentBeforeDueDate:String? = ""
    var securityAmount:String? = ""
    var securityTransferTo:String? = ""
    var tenantBaseInfoDto=ArrayList<ObTenantPayload>()
    var terminateReason:String? = ""
    var unitId:String? = ""
    var unitNumber:String? = ""
    var uploadedBy:String? = ""

    var discount:Int? = 0
    var landlordServiceFee:Int? = 0
    var lateFeeApplicable:Boolean? = false
    //var leaseDocument:Boolean? = false
    var tenantServiceFee:Int? = 0
}