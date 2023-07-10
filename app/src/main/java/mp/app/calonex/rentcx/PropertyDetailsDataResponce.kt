package mp.app.calonex.rentcx

import com.google.gson.annotations.SerializedName


data class PropertyDetailsDataResponce (
    @SerializedName("property" ) var property : Property? = Property()
)

data class Property (

    @SerializedName("PropertyUnitID"           ) var PropertyUnitID           : Int?                                = null,
    @SerializedName("PropertyID"               ) var PropertyID               : Int?                                = null,
    @SerializedName("UnitTypeID"               ) var UnitTypeID               : Int?                                = null,
    @SerializedName("UnitNumber"               ) var UnitNumber               : Int?                                = null,
    @SerializedName("RentPerMonth"             ) var RentPerMonth             : String?                             = null,
    @SerializedName("City"                     ) var City                     : String?                             = null,
    @SerializedName("State"                    ) var State                    : String?                             = null,
    @SerializedName("ZipCode"                  ) var ZipCode                  : String?                             = null,
    @SerializedName("DateAvilable"             ) var DateAvilable             : String?                             = null,
    @SerializedName("SquareFootArea"           ) var SquareFootArea           : String?                             = null,
    @SerializedName("BathroomTypeID"           ) var BathroomTypeID           : Int?                                = null,
    @SerializedName("CreatedBy"                ) var CreatedBy                : Int?                                = null,
    @SerializedName("ModifiedBy"               ) var ModifiedBy               : String?                             = null,
    @SerializedName("isDeleted"                ) var isDeleted                : Boolean?                            = null,
    @SerializedName("LastPaymentDate"          ) var LastPaymentDate          : String?                             = null,
    @SerializedName("SecurityAmount"           ) var SecurityAmount           : String?                             = null,
    @SerializedName("LateFee"                  ) var LateFee                  : String?                             = null,
    @SerializedName("LeaseStartDate"           ) var LeaseStartDate           : String?                             = null,
    @SerializedName("RentBeforeDueDate"        ) var RentBeforeDueDate        : String?                             = null,
    @SerializedName("MonthsFree"               ) var MonthsFree               : Int?                                = null,
    @SerializedName("TenantFirstName"          ) var TenantFirstName          : String?                             = null,
    @SerializedName("TenantMiddleName"         ) var TenantMiddleName         : String?                             = null,
    @SerializedName("TenantLastName"           ) var TenantLastName           : String?                             = null,
    @SerializedName("TenantEmailId"            ) var TenantEmailId            : String?                             = null,
    @SerializedName("TenantPhoneNo"            ) var TenantPhoneNo            : String?                             = null,
    @SerializedName("UnitStatus"               ) var UnitStatus               : String?                             = null,
    @SerializedName("EscrowEnabled"            ) var EscrowEnabled            : String?                             = null,
    @SerializedName("CurrentLeaseId"           ) var CurrentLeaseId           : String?                             = null,
    @SerializedName("CanAddNewLease"           ) var CanAddNewLease           : Boolean?                            = null,
    @SerializedName("CanRenewalLease"          ) var CanRenewalLease          : Boolean?                            = null,
    @SerializedName("IncreaseYearlyPercentage" ) var IncreaseYearlyPercentage : String?                             = null,
    @SerializedName("discount"                 ) var discount                 : String?                             = null,
    @SerializedName("ApplicationFees"          ) var ApplicationFees          : String?                             = null,
    @SerializedName("ApplicationFeeRefundable" ) var ApplicationFeeRefundable : Boolean?                            = null,
    @SerializedName("CreatedON"                ) var CreatedON                : String?                             = null,
    @SerializedName("ModifiedON"               ) var ModifiedON               : String?                             = null,
    @SerializedName("PropertyUnitImages"       ) var PropertyUnitImages       : ArrayList<String>                   = arrayListOf(),
    @SerializedName("BathroomType"             ) var BathroomType             : BathroomType?                       = BathroomType(),
    @SerializedName("UnitType"                 ) var UnitType                 : UnitType?                           = UnitType(),
    @SerializedName("FilesDetails"             ) var FilesDetails             : ArrayList<FilesDetails>             = arrayListOf(),
    @SerializedName("PropertyBuildingFeatures" ) var PropertyBuildingFeatures : ArrayList<PropertyBuildingFeatures> = arrayListOf(),
    @SerializedName("PropertyParkingDetails"   ) var PropertyParkingDetails   : ArrayList<PropertyParkingDetails> = arrayListOf(),
    @SerializedName("PropertyUnitFeatures"     ) var PropertyUnitFeatures     : ArrayList<PropertyUnitFeatures>     = arrayListOf(),
    @SerializedName("PropertyUnitUtilities"    ) var PropertyUnitUtilities    : ArrayList<PropertyUnitUtilities>    = arrayListOf(),
    @SerializedName("PropertyDetail"           ) var PropertyDetail           : PropertyDetail?                     = PropertyDetail()

)