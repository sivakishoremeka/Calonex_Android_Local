package mp.app.calonex.common.tenant

import com.google.gson.annotations.Expose
import mp.app.calonex.landlord.model.TenantInfoPayload

class TenantLeaseUpdateRequest {
    @Expose
    var activeLink: String? = ""
    @Expose
    var propertyId: String? = ""
    @Expose
    var unitNumber: String? = ""
    @Expose
    var propertyAddress: String? = ""
    @Expose
    var propertyCity: String? = ""
    @Expose
    var propertyState: String? = ""
    @Expose
    var propertyZipCode: String? = ""
    @Expose
    var leaseStartDate: String? = ""
    @Expose
    var leaseEndDate: String? = ""
    @Expose
    var leaseDuration: String? = ""
    @Expose
    var lateFee: String? = ""
    @Expose
    var discount: String? = ""
    @Expose
    var rentBeforeDueDate: String? = ""
    @Expose
    var monthsFree: String? = ""
    @Expose
    var rentAmount: String? = ""
    @Expose
    var securityAmount: String? = ""
    @Expose
    var amenities: String? = ""
    @Expose
    var tenantBaseInfoDto= ArrayList<TenantInfoPayload>()
    @Expose
    var isPrimarySigned: String? = ""
    @Expose
    var leaseStatus: String? = ""
    @Expose
    var leaseId: String? = ""
    @Expose
    var brokerId: String? = ""
    @Expose
    var agentId: String? = ""
    @Expose
    var landlordId: String? = ""
    @Expose
    var unitId: String? = ""
    @Expose
    var modifyByRole: String? = ""
    @Expose
    var editorId: String? = ""
     @Expose
    var securityTransferTo: String? = ""


        /*
        "activeLink": false,
        "propertyId": "2467",
        "unitNumber": "1",
        "propertyAddress": "Ratan Building",
        "propertyCity": "Elmont",
        "propertyState": "New York",
        "propertyZipCode": "",
        "leaseStartDate": "1677522600000",
        "leaseEndDate": "1693161000000",
        "leaseDuration": 6,
        "lateFee": 0,
        "discount": 0,
        "rentBeforeDueDate": 2,
        "monthsFree": 2,
        "rentAmount": 2,
        "securityAmount": 12,
        "amenities": 0,
        "tenantBaseInfoDto": [
        {
            "tenantId": "4399",
            "cxId": "TN1674458326590",
            "accountId": null,
            "tenantFirstName": "Debjit",
            "tenantLastName": "dey",
            "tenantMiddleName": null,
            "address": null,
            "city": null,
            "state": null,
            "zipcode": null,
            "phone": "9565885655",
            "emailId": "t2301@yopmail.com",
            "role": "CX-PrimaryTenant",
            "licenseNo": "U2FsdGVkX18IsfILKAhN9SUFPT/SrXTHMih0zXmoLB0=",
            "ssn": null,
            "rentPercentage": "100.0",
            "rentAmount": "2",
            "securityAmount": "12",
            "leaseId": "15969",
            "payDay": null,
            "coTenantFlag": false,
            "coSignerFlag": false,
            "rentAmountBeforeDueDate": null,
            "clientId": null,
            "userId": "5773",
            "propertyId": null,
            "unitId": null,
            "unitNumber": null,
            "leaseAgreementAcceptance": "Accept",
            "landlordApprovalStatus": "25",
            "leaseSigningStatus": "22",
            "deleted": false,
            "existingTenant": false,
            "tenantDataModifed": false,
            "amenities": "0.00",
            "renewLease": false,
            "oldLeaseId": null,
            "leaseTerminated": false,
            "securityRefunded": false,
            "refundedSecurityAmount": null
        }
        ],
        "isPrimarySigned": false,
        "leaseStatus": "18",
        "leaseId": "15969",
        "brokerId": "5616",
        "agentId": "",
        "landlordId": "5773",
        "unitId": "10143",
        "modifyByRole": "CX-Tenant",
        "editorId": "5773",
        "securityTransferTo": null
    }*/
}