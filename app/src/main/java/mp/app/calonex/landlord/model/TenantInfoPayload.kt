package mp.app.calonex.landlord.model

import android.net.Uri
import lombok.Data
import lombok.Getter
import lombok.Setter
import java.io.Serializable
@Data
@Setter
@Getter
 class TenantInfoPayload : Serializable {

    var address: String? = ""
    var city: String? = ""
    var cxId: String? = ""
    var emailId: String? = ""

    var leaseId: String = ""

    var licenseNo: String? = ""
    var payDay: String? = ""
    var phone: String? = ""
    var rentAmount: String? = ""
    var refundedSecurityAmount: String? = ""
    var rentPercentage: String? = ""
    var role: String? = ""
    var securityAmount: String? = ""
    var ssn: String? = ""
    var state: String? = ""
    var tenantFirstName: String? = ""
    var oldLeaseId: String? = ""
    var tenantId: String? = ""
    var tenantLastName: String? = ""
    var tenantMiddleName: String? = ""
    var zipcode: String? = ""
    var rentAmountBeforeDueDate: String? = ""
    var clientId: String? = ""
    var userId: String? = ""
    var accountId: String? = ""
    var propertyId: String = ""
    var unitId: String? = ""
    var leaseAgreementAcceptance: String? = ""
    var landlordApprovalStatus: String? = ""
    var leaseStatus: String? = ""
    var leaseSigningStatus: String? = ""
    var deleted: Boolean? = false
    var tenantSignature: String? = ""
    var signature: String? = ""
    var existingTenant: Boolean? = false
    var coTenantFlag: Boolean? = false
    var coSignerFlag: Boolean? = false
    var renewLease: Boolean? = false
    var leaseTerminated: Boolean? = false
    var securityRefunded: Boolean? = false
    var tenantDataModifed: Boolean? = false
    var unitNumber: String = ""
    var leaseMonth: String=""
    var amenities: String="0.0"
    var requestdate: Long=0
    var paystubImagesArray = ArrayList<Uri>()
    var drivingLicenseImagesArray = ArrayList<Uri>()
    var w2sImagesArray = ArrayList<Uri>()
    var voidCheckImagesArray = ArrayList<Uri>()

    /*
    * {
      "tenantId": "4251",
      "cxId": "TN1670657435954",
      "accountId": "01",
      "tenantFirstName": "Patricia",
      "tenantLastName": "Smith",
      "tenantMiddleName": null,
      "address": null,
      "city": null,
      "state": null,
      "zipcode": null,
      "phone": "3479657499",
      "emailId": "t0312us@yopmail.com",
      "role": "CX-PrimaryTenant",
      "licenseNo": null,
      "ssn": null,
      "rentPercentage": "100.0",
      "rentAmount": "1000",
      "securityAmount": "677",
      "leaseId": "15668",
      "payDay": "20",
      "coTenantFlag": true,
      "coSignerFlag": true,
      "rentAmountBeforeDueDate": null,
      "clientId": null,
      "userId": "5564",
      "propertyId": null,
      "unitId": null,
      "unitNumber": null,
      "leaseAgreementAcceptance": "25",
      "landlordApprovalStatus": "25",
      "leaseSigningStatus": null,
      "deleted": false,
      "existingTenant": false,
      "tenantDataModifed": false,
      "amenities": "0.00",
      "renewLease": false,
      "oldLeaseId": null,
      "leaseTerminated": false,
      "securityRefunded": false,
      "refundedSecurityAmount": null
    }*/

}