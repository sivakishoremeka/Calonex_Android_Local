package mp.app.calonex.landlord.model

import android.graphics.Bitmap

class ObTenantPayload {

    var accountId:String? = ""
    var address:String? = ""
    var amenities:String? = ""
    var city:String? = ""
    var clientId:String? = ""
    var coSignerFlag:Boolean? =  false
    var coTenantFlag:Boolean? =  false
    var cxId:String? = ""
    var deleted:Boolean? =  false
    var emailId:String? = ""
    var existingTenant:Boolean? =  false
    var landlordApprovalStatus:String? = ""
    var leaseAgreementAcceptance:String? = ""
    var leaseId:String? = ""
    var leaseSigningStatus:String? = ""
    var leaseTerminated:Boolean? =  false
    var licenseNo:String? = ""
    var oldLeaseId:String? = ""
    var payDay:String? = ""
    var phone:String? = ""
    var propertyId:String? = ""
    var renewLease:Boolean? =  false
    var rentAmount:String? = ""
    var rentAmountBeforeDueDate:String? = ""
    var rentPercentage:String? = ""
    var role:String? = ""
    var securityAmount:String? = ""
    var ssn:String? = ""
    var state:String? = ""
    var tenantDataModifed:Boolean? =  false
    var tenantFirstName:String? = ""
    var tenantId:String? = ""
    var tenantLastName:String? = ""
    var tenantMiddleName:String? = ""
    var tenantSignature:String? = ""
    var unitId:String? = ""
    var unitNumber:String? = ""
    var userId:String? = ""
    var zipcode:String? = ""
    var bitmapListPaystub=ArrayList<Bitmap>()
    var bitmapListW2s=ArrayList<Bitmap>()
    var bitmapListLicence=ArrayList<Bitmap>()

    
}