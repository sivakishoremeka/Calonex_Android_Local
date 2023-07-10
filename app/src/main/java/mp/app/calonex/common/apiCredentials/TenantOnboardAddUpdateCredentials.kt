package mp.app.calonex.common.apiCredentials

import mp.app.calonex.landlord.model.TenantInfoPayload
import java.io.Serializable

class TenantOnboardAddUpdateCredentials : Serializable {

    var activeLink: Boolean? = false
    var isPrimarySigned: Boolean? = false
    var deletedisPrimarySigned: Boolean? = false
    var monthFreeStandards: Boolean? = true
    var renewLease: Boolean? = false
    var dontShowToLandlord: Boolean? = false
    var freezedRecord: Boolean? = false
    var propertyAddress: String? = ""
    var propertyCity: String? = ""
    var propertyState: String? = ""
    var propertyZipCode: String? = ""
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
    var leaseTerminateNotice: Boolean? = false
    var securityAmount: String? = ""
    var securityTransferTo: String? = ""
    var leaseSigningStatus: String? = ""
    var unitId: String? = ""
    var unitNumber: String? = ""
    var action: String? = ""
    var landlordServiceFee: String? = ""
    var tenantServiceFee: String? = ""
    var discountedRent: String? = ""
    var applicantName: String? = ""

    var coment: String? = ""
    var deadLine: String? = ""
    var uploadedBy: String? = ""
    var modifyByRole: String? = ""
    var editorId: String? = ""
    var discount: String? = ""
    var amenities: String? = "0.0"
    var brokerId: String? = ""
    var agentId: String? = ""
    var leaseStatus: String? = "18"
    var tenantBaseInfoDto = ArrayList<TenantInfoPayload>()


    /*================Need to check with below request=======================
    *
    *
    * {
  "leaseId": "15668",
  "propertyId": "1991",
  "unitId": "9628",
  "unitNumber": "2",
  "leaseStartDate": "1676851200000",
  "leaseEndDate": "1695148200000",
  "landlordId": "5556",
  "rentAmount": "1000",
  "leaseDuration": 7,
  "agentId": "5563",
  "brokerId": null,
  "uploadedBy": null,
  "securityAmount": 677,
  "freezedRecord": false,
  "dontShowToLandlord": false,
  "tenantBaseInfoDto": [

  ],
  "action": null,
  "comment": null,
  "deadLine": "1676419200000",
  "lateFee": 120,
  "rentBeforeDueDate": 96,
  "monthsFree": 2,
  "propertyAddress": "Debjit Test",
  "propertyCity": "Floral Park",
  "propertyState": "New York",
  "propertyZipCode": "11005",
  "leaseSigningStatus": "20",
  "leaseStatus": "15",
  "oldLeaseData": null,
  "terminateReason": null,
  "leaseTerminateNotice": false,
  "leaseTerminateNoticeDate": null,
  "brokerPhoneNumber": null,
  "agentPhoneNumber": null,
  "brokerLicenseNumber": null,
  "agentLicenseNumber": null,
  "amenities": 0,
  "deleted": false,
  "monthFreeStandards": true,
  "renewLease": false,
  "landlordServiceFee": 2,
  "tenantServiceFee": 2,
  "discount": 4,
  "discountedRent": 96,
  "tenantName": null,
  "applicantName": "Patricia Smith",
  "activeLink": true,
  "modifyByRole": "CX-Tenant",
  "editorId": "5564",
  "securityTransferTo": null
}
    *
    * */

}