package mp.app.calonex.tenant.response

import mp.app.calonex.landlord.model.TenantInfoPayload

class TenantOnboardResponseData {

    var dontShowToLandlord: Boolean? = false
    var landlordId: String? = ""
    var lateFee: String? = ""
    var leaseDuration: String? = ""
    var leaseEndDate: String? = ""
    var leaseId: String? = ""
    var leaseStartDate: String? = ""
    var monthsFree: String? = ""
    var propertyId: String? = ""
    var rentAmount: String? = ""
    var rentBeforeDueDate: String? = ""
    var reviewByLandlord: Boolean? = false
    var securityAmount: String? = ""
    var securityTransferTo: String? = ""
    var unitId: String? = ""
    var unitNumber: String? = ""
    var uploadedBy: String? = ""
    var tenantBaseInfoDto = ArrayList<TenantInfoPayload>()

}