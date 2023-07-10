package mp.app.calonex.rentcx

import com.google.gson.annotations.SerializedName


data class AddorUpdateRequestModel (
    @SerializedName("fromRentCX"         ) var fromRentCX         : Boolean?                     = null,
    @SerializedName("leaseStartDate"     ) var leaseStartDate     : Int?                         = null,
    @SerializedName("leaseEndDate"       ) var leaseEndDate       : Int?                         = null,
    @SerializedName("leaseDuration"      ) var leaseDuration      : Int?                         = null,
    @SerializedName("rentAmount"         ) var rentAmount         : String?                      = null,
    @SerializedName("securityAmount"     ) var securityAmount     : String?                      = null,
    @SerializedName("action"             ) var action             : String?                      = null,
    @SerializedName("comment"            ) var comment            : String?                      = null,
    @SerializedName("leaseId"            ) var leaseId            : String?                      = null,
    @SerializedName("agentId"            ) var agentId            : String?                      = null,
    @SerializedName("brokerId"           ) var brokerId           : String?                      = null,
    @SerializedName("applicationFee"     ) var applicationFee     : String?                      = null,
    @SerializedName("unitId"             ) var unitId             : Int?                         = null,
    @SerializedName("landlordId"         ) var landlordId         : Int?                         = null,
    @SerializedName("propertyId"         ) var propertyId         : Int?                         = null,
    @SerializedName("uploadedBy"         ) var uploadedBy         : String?                      = null,
    @SerializedName("unitNumber"         ) var unitNumber         : Int?                         = null,
    @SerializedName("lateFee"            ) var lateFee            : String?                      = null,
    @SerializedName("lateFeeApplicable"  ) var lateFeeApplicable  : Boolean?                     = null,
    @SerializedName("rentBeforeDueDate"  ) var rentBeforeDueDate  : String?                      = null,
    @SerializedName("monthsFree"         ) var monthsFree         : Int?                         = null,
    @SerializedName("monthFreeStandards" ) var monthFreeStandards : Boolean?                     = null,
    @SerializedName("amenities"          ) var amenities          : String?                      = null,
    @SerializedName("securityTransferTo" ) var securityTransferTo : String?                      = null,
    @SerializedName("dontShowToLandlord" ) var dontShowToLandlord : Boolean?                     = null,
    @SerializedName("leaseDocument"      ) var leaseDocument      : String?                      = null,
    @SerializedName("existingTenant"     ) var existingTenant     : Boolean?                     = null,
    @SerializedName("landlordServiceFee" ) var landlordServiceFee : Int?                         = null,
    @SerializedName("tenantServiceFee"   ) var tenantServiceFee   : Int?                         = null,
    @SerializedName("discount"           ) var discount           : String?                      = null,
    @SerializedName("tenantBaseInfoDto"  ) var tenantBaseInfoDto  : ArrayList<TenantBaseInfoDto> = arrayListOf()

)

data class TenantBaseInfoDto (

    @SerializedName("accountId"        ) var accountId        : String?  = null,
    @SerializedName("tenantFirstName"  ) var tenantFirstName  : String?  = null,
    @SerializedName("tenantMiddleName" ) var tenantMiddleName : String?  = null,
    @SerializedName("tenantLastName"   ) var tenantLastName   : String?  = null,
    @SerializedName("rentAmount"       ) var rentAmount       : String?  = null,
    @SerializedName("rentPercentage"   ) var rentPercentage   : String?  = null,
    @SerializedName("securityAmount"   ) var securityAmount   : String?  = null,
    @SerializedName("emailId"          ) var emailId          : String?  = null,
    @SerializedName("phone"            ) var phone            : String?  = null,
    @SerializedName("licenseNo"        ) var licenseNo        : String?  = null,
    @SerializedName("ssn"              ) var ssn              : String?  = null,
    @SerializedName("address"          ) var address          : String?  = null,
    @SerializedName("city"             ) var city             : String?  = null,
    @SerializedName("state"            ) var state            : String?  = null,
    @SerializedName("zipcode"          ) var zipcode          : String?  = null,
    @SerializedName("role"             ) var role             : String?  = null,
    @SerializedName("tenantId"         ) var tenantId         : String?  = null,
    @SerializedName("leaseId"          ) var leaseId          : String?  = null,
    @SerializedName("payDay"           ) var payDay           : String?  = null,
    @SerializedName("cxId"             ) var cxId             : String?  = null,
    @SerializedName("existingTenant"   ) var existingTenant   : Boolean? = null,
    @SerializedName("clientId"         ) var clientId         : String?  = null

)