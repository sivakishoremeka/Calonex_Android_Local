package mp.app.calonex.rentcx

import com.google.gson.annotations.SerializedName


data class MarketplaceAddorUpdateResponse(
    @SerializedName("responseDto" ) var responseDto : ResponseDto? = ResponseDto(),
    @SerializedName("data"        ) var data        : Data4?        = Data4()
)


data class Data4 (

    @SerializedName("leaseId"            ) var leaseId            : String?                      = null,
    @SerializedName("propertyId"         ) var propertyId         : String?                      = null,
    @SerializedName("unitId"             ) var unitId             : String?                      = null,
    @SerializedName("unitNumber"         ) var unitNumber         : String?                      = null,
    @SerializedName("leaseStartDate"     ) var leaseStartDate     : String?                      = null,
    @SerializedName("leaseEndDate"       ) var leaseEndDate       : String?                      = null,
    @SerializedName("landlordId"         ) var landlordId         : String?                      = null,
    @SerializedName("rentAmount"         ) var rentAmount         : String?                      = null,
    @SerializedName("leaseDuration"      ) var leaseDuration      : String?                      = null,
    @SerializedName("agentId"            ) var agentId            : String?                      = null,
    @SerializedName("brokerId"           ) var brokerId           : String?                      = null,
    @SerializedName("leaseStatus"        ) var leaseStatus        : String?                      = null,
    @SerializedName("leaseSigningStatus" ) var leaseSigningStatus : String?                      = null,
    @SerializedName("uploadedBy"         ) var uploadedBy         : String?                      = null,
    @SerializedName("securityAmount"     ) var securityAmount     : String?                      = null,
    @SerializedName("freezedRecord"      ) var freezedRecord      : Boolean?                     = null,
    @SerializedName("fromRentCX"         ) var fromRentCX         : Boolean?                     = null,
    @SerializedName("applicationFee"     ) var applicationFee     : String?                      = null,
    @SerializedName("dontShowToLandlord" ) var dontShowToLandlord : Boolean?                     = null,
    @SerializedName("tenantBaseInfoDto"  ) var tenantBaseInfoDto  : ArrayList<TenantBaseInfoDto2> = arrayListOf(),
    @SerializedName("action"             ) var action             : String?                      = null,
    @SerializedName("comment"            ) var comment            : String?                      = null,
    @SerializedName("lateFee"            ) var lateFee            : String?                      = null,
    @SerializedName("rentBeforeDueDate"  ) var rentBeforeDueDate  : String?                      = null,
    @SerializedName("monthsFree"         ) var monthsFree         : String?                      = null,
    @SerializedName("securityTransferTo" ) var securityTransferTo : String?                      = null,
    @SerializedName("directAccess"       ) var directAccess       : String?                      = null,
    @SerializedName("editorId"           ) var editorId           : String?                      = null,
    @SerializedName("modifyByRole"       ) var modifyByRole       : String?                      = null,
    @SerializedName("deadline"           ) var deadline           : String?                      = null,
    @SerializedName("existingTenant"     ) var existingTenant     : Boolean?                     = null,
    @SerializedName("renewLease"         ) var renewLease         : Boolean?                     = null,
    @SerializedName("oldLeaseId"         ) var oldLeaseId         : String?                      = null,
    @SerializedName("amenities"          ) var amenities          : String?                      = null,
    @SerializedName("leaseTerminated"    ) var leaseTerminated    : Boolean?                     = null,
    @SerializedName("monthFreeStandards" ) var monthFreeStandards : Boolean?                     = null,
    @SerializedName("terminateReason"    ) var terminateReason    : String?                      = null,
    @SerializedName("leaseRenewal"       ) var leaseRenewal       : Boolean?                     = null,
    @SerializedName("password"           ) var password           : String?                      = null,
    @SerializedName("userId"             ) var userId             : String?                      = null,
    @SerializedName("tenantServiceFee"   ) var tenantServiceFee   : Int?                         = null,
    @SerializedName("landlordServiceFee" ) var landlordServiceFee : Int?                         = null,
    @SerializedName("discount"           ) var discount           : Int?                         = null

)


data class TenantBaseInfoDto2 (

    @SerializedName("tenantId"                 ) var tenantId                 : String?  = null,
    @SerializedName("cxId"                     ) var cxId                     : String?  = null,
    @SerializedName("accountId"                ) var accountId                : String?  = null,
    @SerializedName("tenantFirstName"          ) var tenantFirstName          : String?  = null,
    @SerializedName("tenantLastName"           ) var tenantLastName           : String?  = null,
    @SerializedName("tenantMiddleName"         ) var tenantMiddleName         : String?  = null,
    @SerializedName("address"                  ) var address                  : String?  = null,
    @SerializedName("city"                     ) var city                     : String?  = null,
    @SerializedName("state"                    ) var state                    : String?  = null,
    @SerializedName("zipcode"                  ) var zipcode                  : String?  = null,
    @SerializedName("phone"                    ) var phone                    : String?  = null,
    @SerializedName("emailId"                  ) var emailId                  : String?  = null,
    @SerializedName("role"                     ) var role                     : String?  = null,
    @SerializedName("licenseNo"                ) var licenseNo                : String?  = null,
    @SerializedName("ssn"                      ) var ssn                      : String?  = null,
    @SerializedName("rentPercentage"           ) var rentPercentage           : String?  = null,
    @SerializedName("rentAmount"               ) var rentAmount               : String?  = null,
    @SerializedName("securityAmount"           ) var securityAmount           : String?  = null,
    @SerializedName("leaseId"                  ) var leaseId                  : String?  = null,
    @SerializedName("payDay"                   ) var payDay                   : String?  = null,
    @SerializedName("coTenantFlag"             ) var coTenantFlag             : Boolean? = null,
    @SerializedName("coSignerFlag"             ) var coSignerFlag             : Boolean? = null,
    @SerializedName("rentAmountBeforeDueDate"  ) var rentAmountBeforeDueDate  : String?  = null,
    @SerializedName("clientId"                 ) var clientId                 : String?  = null,
    @SerializedName("userId"                   ) var userId                   : String?  = null,
    @SerializedName("propertyId"               ) var propertyId               : String?  = null,
    @SerializedName("unitId"                   ) var unitId                   : String?  = null,
    @SerializedName("unitNumber"               ) var unitNumber               : String?  = null,
    @SerializedName("leaseAgreementAcceptance" ) var leaseAgreementAcceptance : String?  = null,
    @SerializedName("landlordApprovalStatus"   ) var landlordApprovalStatus   : String?  = null,
    @SerializedName("leaseSigningStatus"       ) var leaseSigningStatus       : String?  = null,
    @SerializedName("deleted"                  ) var deleted                  : Boolean? = null,
    @SerializedName("tenantSignature"          ) var tenantSignature          : String?  = null,
    @SerializedName("existingTenant"           ) var existingTenant           : Boolean? = null,
    @SerializedName("tenantDataModifed"        ) var tenantDataModifed        : Boolean? = null,
    @SerializedName("amenities"                ) var amenities                : String?  = null,
    @SerializedName("renewLease"               ) var renewLease               : Boolean? = null,
    @SerializedName("oldLeaseId"               ) var oldLeaseId               : String?  = null,
    @SerializedName("leaseTerminated"          ) var leaseTerminated          : Boolean? = null,
    @SerializedName("securityRefunded"         ) var securityRefunded         : Boolean? = null,
    @SerializedName("refundedSecurityAmount"   ) var refundedSecurityAmount   : Int?     = null

)