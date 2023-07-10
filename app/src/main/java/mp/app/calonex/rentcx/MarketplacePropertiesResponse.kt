package mp.app.calonex.rentcx

import com.google.gson.annotations.SerializedName


data class MarketplacePropertiesResponse(
    @SerializedName("properties") var properties: ArrayList<Properties> = arrayListOf()
)

data class Properties(
    @SerializedName("PropertyUnitID") var PropertyUnitID: Int? = null,
    @SerializedName("PropertyID") var PropertyID: Int? = null,
    @SerializedName("UnitTypeID") var UnitTypeID: Int? = null,
    @SerializedName("UnitNumber") var UnitNumber: Int? = null,
    @SerializedName("RentPerMonth") var RentPerMonth: String? = null,
    @SerializedName("City") var City: String? = null,
    @SerializedName("State") var State: String? = null,
    @SerializedName("ZipCode") var ZipCode: String? = null,
    @SerializedName("DateAvilable") var DateAvilable: String? = null,
    @SerializedName("SquareFootArea") var SquareFootArea: String? = null,
    @SerializedName("BathroomTypeID") var BathroomTypeID: Int? = null,
    @SerializedName("CreatedBy") var CreatedBy: Int? = null,
    @SerializedName("ModifiedBy") var ModifiedBy: String? = null,
    @SerializedName("isDeleted") var isDeleted: Boolean? = null,
    @SerializedName("LastPaymentDate") var LastPaymentDate: String? = null,
    @SerializedName("SecurityAmount") var SecurityAmount: String? = null,
    @SerializedName("LateFee") var LateFee: String? = null,
    @SerializedName("LeaseStartDate") var LeaseStartDate: String? = null,
    @SerializedName("RentBeforeDueDate") var RentBeforeDueDate: String? = null,
    @SerializedName("MonthsFree") var MonthsFree: Int? = null,
    @SerializedName("TenantFirstName") var TenantFirstName: String? = null,
    @SerializedName("TenantMiddleName") var TenantMiddleName: String? = null,
    @SerializedName("TenantLastName") var TenantLastName: String? = null,
    @SerializedName("TenantEmailId") var TenantEmailId: String? = null,
    @SerializedName("TenantPhoneNo") var TenantPhoneNo: String? = null,
    @SerializedName("UnitStatus") var UnitStatus: String? = null,
    @SerializedName("EscrowEnabled") var EscrowEnabled: String? = null,
    @SerializedName("CurrentLeaseId") var CurrentLeaseId: String? = null,
    @SerializedName("CanAddNewLease") var CanAddNewLease: Boolean? = null,
    @SerializedName("CanRenewalLease") var CanRenewalLease: Boolean? = null,
    @SerializedName("IncreaseYearlyPercentage") var IncreaseYearlyPercentage: String? = null,
    @SerializedName("discount") var discount: String? = null,
    @SerializedName("ApplicationFees") var ApplicationFees: String? = null,
    @SerializedName("ApplicationFeeRefundable") var ApplicationFeeRefundable: Boolean? = null,
    @SerializedName("CreatedON") var CreatedON: String? = null,
    @SerializedName("ModifiedON") var ModifiedON: String? = null,
    @SerializedName("PropertyUnitImages") var PropertyUnitImages: ArrayList<String> = arrayListOf(),
    @SerializedName("BathroomType") var BathroomType: BathroomType? = BathroomType(),
    @SerializedName("UnitType") var UnitType: UnitType? = UnitType(),
    @SerializedName("FilesDetails") var FilesDetails: ArrayList<FilesDetails> = arrayListOf(),
    @SerializedName("PropertyBuildingFeatures") var PropertyBuildingFeatures: ArrayList<PropertyBuildingFeatures> = arrayListOf(),
    @SerializedName("PropertyParkingDetails") var PropertyParkingDetails: ArrayList<PropertyParkingDetails> = arrayListOf(),
    @SerializedName("PropertyUnitFeatures") var PropertyUnitFeatures: ArrayList<PropertyUnitFeatures> = arrayListOf(),
    @SerializedName("PropertyUnitUtilities") var PropertyUnitUtilities: ArrayList<PropertyUnitUtilities> = arrayListOf(),
    @SerializedName("PropertyDetail") var PropertyDetail: PropertyDetail? = PropertyDetail()
)

data class PropertyBuildingFeatures(
    @SerializedName("PropertyBuildingFeatureID") var PropertyBuildingFeatureID: Int? = null,
    @SerializedName("BuildingFeatureID") var BuildingFeatureID: Int? = null,
    @SerializedName("PropertyID") var PropertyID: Int? = null,
    @SerializedName("CreatedBy") var CreatedBy: Int? = null,
    @SerializedName("isDeleted") var isDeleted: Boolean? = null,
    @SerializedName("CreatedON") var CreatedON: String? = null,
    @SerializedName("BuildingFeature") var BuildingFeature: BuildingFeature? = BuildingFeature()
)

data class BuildingFeature(
    @SerializedName("BuildingFeatureID") var BuildingFeatureID: Int? = null,
    @SerializedName("BuildingFeature") var BuildingFeature: String? = null,
    @SerializedName("CreatedBy") var CreatedBy: Int? = null,
    @SerializedName("ModifiedBy") var ModifiedBy: String? = null,
    @SerializedName("isDeleted") var isDeleted: Boolean? = null,
    @SerializedName("CreatedON") var CreatedON: String? = null,
    @SerializedName("ModifiedON") var ModifiedON: String? = null
)

data class PropertyUnitUtilities(
    @SerializedName("PropertyUnitUtilitiesID") var PropertyUnitUtilitiesID: Int? = null,
    @SerializedName("PropertyUnitID") var PropertyUnitID: Int? = null,
    @SerializedName("UtilityID") var UtilityID: Int? = null,
    @SerializedName("CreatedBy") var CreatedBy: Int? = null,
    @SerializedName("ModifiedBy") var ModifiedBy: Int? = null,
    @SerializedName("isDeleted") var isDeleted: Boolean? = null,
    @SerializedName("CreatedON") var CreatedON: String? = null,
    @SerializedName("ModifiedON") var ModifiedON: String? = null,
    @SerializedName("Utility") var Utility: Utility? = Utility()
)

data class Utility(
    @SerializedName("UtilityID") var UtilityID: Int? = null,
    @SerializedName("UtilitieName") var UtilitieName: String? = null,
    @SerializedName("CreatedBy") var CreatedBy: Int? = null,
    @SerializedName("ModifiedBy") var ModifiedBy: Int? = null,
    @SerializedName("isDeleted") var isDeleted: Boolean? = null,
    @SerializedName("CreatedON") var CreatedON: String? = null,
    @SerializedName("ModifiedON") var ModifiedON: String? = null
)

data class BathroomType(
    @SerializedName("BathroomTypeID") var BathroomTypeID: Int? = null,
    @SerializedName("BathroomType") var BathroomType: String? = null,
    @SerializedName("CreatedBy") var CreatedBy: Int? = null,
    @SerializedName("ModifiedBy") var ModifiedBy: Int? = null,
    @SerializedName("isDeleted") var isDeleted: Boolean? = null,
    @SerializedName("CreatedON") var CreatedON: String? = null,
    @SerializedName("ModifiedON") var ModifiedON: String? = null
)

data class UnitType(
    @SerializedName("UnitTypeID") var UnitTypeID: Int? = null,
    @SerializedName("UnitType") var UnitType: String? = null,
    @SerializedName("CreatedBy") var CreatedBy: Int? = null,
    @SerializedName("ModifiedBy") var ModifiedBy: String? = null,
    @SerializedName("isDeleted") var isDeleted: Boolean? = null,
    @SerializedName("CreatedON") var CreatedON: String? = null,
    @SerializedName("ModifiedON") var ModifiedON: String? = null
)

data class FilesDetails(
    @SerializedName("id") var id: String? = null,
    @SerializedName("data") var data: Data? = Data(),
    @SerializedName("emailId") var emailId: String? = null,
    @SerializedName("fileName") var fileName: String? = null,
    @SerializedName("fileType") var fileType: String? = null,
    @SerializedName("uploadFileType") var uploadFileType: String? = null,
    @SerializedName("tenantId") var tenantId: String? = null,
    @SerializedName("propertyId") var propertyId: String? = null,
    @SerializedName("unitId") var unitId: String? = null,
    @SerializedName("userId") var userId: String? = null
)

data class Data(
    @SerializedName("type") var type: String? = null,
    @SerializedName("data") var data: ArrayList<Int> = arrayListOf()
)

data class PropertyParkingDetails(
    @SerializedName("PropertyParkingID") var PropertyParkingID: Int? = null,
    @SerializedName("PropertyID") var PropertyID: Int? = null,
    @SerializedName("ParkingTypeID") var ParkingTypeID: Int? = null,
    @SerializedName("isDeleted") var isDeleted: Boolean? = null,
    @SerializedName("CreatedBy") var CreatedBy: Int? = null,
    @SerializedName("ModifiedBy") var ModifiedBy: String? = null,
    @SerializedName("CreatedON") var CreatedON: String? = null,
    @SerializedName("ModifiedON") var ModifiedON: String? = null,
    @SerializedName("ParkingType") var ParkingType: ParkingType? = ParkingType()
)

data class ParkingType(
    @SerializedName("ParkingTypeID") var ParkingTypeID: Int? = null,
    @SerializedName("ParkingType") var ParkingType: String? = null,
    @SerializedName("CreatedBy") var CreatedBy: Int? = null,
    @SerializedName("ModifiedBy") var ModifiedBy: String? = null,
    @SerializedName("isDeleted") var isDeleted: Boolean? = null,
    @SerializedName("CreatedON") var CreatedON: String? = null,
    @SerializedName("ModifiedON") var ModifiedON: String? = null
)

data class PropertyUnitFeatures(
    @SerializedName("PropertyUnitFeatureID") var PropertyUnitFeatureID: Int? = null,
    @SerializedName("PropertyUnitID") var PropertyUnitID: Int? = null,
    @SerializedName("UnitFeatureID") var UnitFeatureID: Int? = null,
    @SerializedName("CreatedBy") var CreatedBy: Int? = null,
    @SerializedName("ModifiedBy") var ModifiedBy: Int? = null,
    @SerializedName("CreatedON") var CreatedON: String? = null,
    @SerializedName("ModifiedON") var ModifiedON: String? = null,
    @SerializedName("UnitFeature") var UnitFeature: UnitFeature? = UnitFeature()
)

data class UnitFeature(
    @SerializedName("UnitFeatureID") var UnitFeatureID: Int? = null,
    @SerializedName("UnitFeature") var UnitFeature: String? = null,
    @SerializedName("CreatedBy") var CreatedBy: Int? = null,
    @SerializedName("ModifiedBy") var ModifiedBy: String? = null,
    @SerializedName("isDeleted") var isDeleted: Boolean? = null,
    @SerializedName("CreatedON") var CreatedON: String? = null,
    @SerializedName("ModifiedON") var ModifiedON: String? = null
)

data class PropertyDetail(
    @SerializedName("PropertyID") var PropertyID: Int? = null,
    @SerializedName("UserCatalogID") var UserCatalogID: Int? = null,
    @SerializedName("Address1") var Address1: String? = null,
    @SerializedName("City") var City: String? = null,
    @SerializedName("State") var State: String? = null,
    @SerializedName("ZipCode") var ZipCode: String? = null,
    @SerializedName("NumberOfUnits") var NumberOfUnits: String? = null,
    @SerializedName("Description") var Description: String? = null,
    @SerializedName("IsLinkedByBrokerAgent") var IsLinkedByBrokerAgent: Boolean? = null,
    @SerializedName("PropertyStatusID") var PropertyStatusID: Int? = null,
    @SerializedName("PrimaryContactId") var PrimaryContactId: String? = null,
    @SerializedName("BrokerAgentPropertyStatus") var BrokerAgentPropertyStatus: String? = null,
    @SerializedName("OnBordedBy") var OnBordedBy: String? = null,
    @SerializedName("PrimaryContact") var PrimaryContact: String? = null,
    @SerializedName("AllowPropertyAccess") var AllowPropertyAccess: Int? = null,
    @SerializedName("Broker") var Broker: String? = null,
    @SerializedName("BrokerOrAgentLiscenceID") var BrokerOrAgentLiscenceID: String? = null,
    @SerializedName("BrokerPhone") var BrokerPhone: String? = null,
    @SerializedName("BrokerEmailID") var BrokerEmailID: String? = null,
    @SerializedName("Broker_Id") var BrokerId: String? = null,
    @SerializedName("BuildingTypeID") var BuildingTypeID: Int? = null,
    @SerializedName("ParkingTypeID") var ParkingTypeID: String? = null,
    @SerializedName("Driver_License") var DriverLicense: String? = null,
    @SerializedName("OnBordedStatus") var OnBordedStatus: String? = null,
    @SerializedName("Phone") var Phone: String? = null,
    @SerializedName("Bank_Account_Number") var BankAccountNumber: String? = null,
    @SerializedName("Routing_Number") var RoutingNumber: String? = null,
    @SerializedName("Property_Documents") var PropertyDocuments: String? = null,
    @SerializedName("isDeleted") var isDeleted: Boolean? = null,
    @SerializedName("ApprovedById") var ApprovedById: Int? = null,
    @SerializedName("CreatedBy") var CreatedBy: Int? = null,
    @SerializedName("ModifiedBy") var ModifiedBy: String? = null,
    @SerializedName("ApprovedByName") var ApprovedByName: String? = null,
    @SerializedName("Address2") var Address2: String? = null,
    @SerializedName("Latitude") var Latitude: String? = null,
    @SerializedName("Longitude") var Longitude: String? = null,
    @SerializedName("CreatedON") var CreatedON: String? = null,
    @SerializedName("ModifiedON") var ModifiedON: String? = null,
    @SerializedName("UserCatalog") var UserCatalog: UserCatalog? = UserCatalog(),
    @SerializedName("BuildingType") var BuildingType: BuildingType? = BuildingType(),
    @SerializedName("ParkingType") var ParkingType: String? = null,
    @SerializedName("PropertyImages") var PropertyImages: ArrayList<String> = arrayListOf(),
    @SerializedName("FilesDetails") var FilesDetails: ArrayList<FilesDetails> = arrayListOf()
)

data class BuildingType(
    @SerializedName("BuildingTypeID") var BuildingTypeID: Int? = null,
    @SerializedName("BuildingType") var BuildingType: String? = null,
    @SerializedName("CreatedBy") var CreatedBy: Int? = null,
    @SerializedName("ModifiedBy") var ModifiedBy: String? = null,
    @SerializedName("isDeleted") var isDeleted: Boolean? = null,
    @SerializedName("CreatedON") var CreatedON: String? = null,
    @SerializedName("ModifiedON") var ModifiedON: String? = null
)

data class UserCatalog(
    @SerializedName("UserCatalogID") var UserCatalogID: Int? = null,
    @SerializedName("UserRoleName") var UserRoleName: String? = null,
    @SerializedName("BrokerLicenseNO") var BrokerLicenseNO: String? = null,
    @SerializedName("AgentLicenseNO") var AgentLicenseNO: String? = null,
    @SerializedName("EmailID") var EmailID: String? = null,
    @SerializedName("FirstName") var FirstName: String? = null,
    @SerializedName("MiddleName") var MiddleName: String? = null,
    @SerializedName("LastName") var LastName: String? = null,
    @SerializedName("DOB") var DOB: String? = null,
    @SerializedName("Ssn") var Ssn: String? = null,
    @SerializedName("BussinessName") var BussinessName: String? = null,
    @SerializedName("Address1") var Address1: String? = null,
    @SerializedName("Address2") var Address2: String? = null,
    @SerializedName("City") var City: String? = null,
    @SerializedName("State") var State: String? = null,
    @SerializedName("ZipCode") var ZipCode: String? = null,
    @SerializedName("AlternatePhone") var AlternatePhone: String? = null,
    @SerializedName("ImageName") var ImageName: String? = null,
    @SerializedName("StateIssued") var StateIssued: String? = null,
    @SerializedName("BankUserName") var BankUserName: String? = null,
    @SerializedName("BankAccountNO") var BankAccountNO: String? = null,
    @SerializedName("RoutingNO") var RoutingNO: String? = null,
    @SerializedName("VoidBankCheque") var VoidBankCheque: String? = null,
    @SerializedName("AccountType") var AccountType: String? = null,
    @SerializedName("AlternatePhoneNumber") var AlternatePhoneNumber: String? = null,
    @SerializedName("BankPhoneNO") var BankPhoneNO: String? = null,
    @SerializedName("BusinessName") var BusinessName: String? = null,
    @SerializedName("DrivingLicenseNO") var DrivingLicenseNO: String? = null,
    @SerializedName("ImagePath") var ImagePath: String? = null,
    @SerializedName("imageUrl") var imageUrl: String? = null,
    @SerializedName("PhoneNumber") var PhoneNumber: String? = null,
    @SerializedName("VoidedBankCheque") var VoidedBankCheque: String? = null,
    @SerializedName("UserName") var UserName: String? = null,
    @SerializedName("UserRoleID") var UserRoleID: String? = null,
    @SerializedName("confirmation_token") var confirmationToken: String? = null,
    @SerializedName("enabled") var enabled: String? = null,
    @SerializedName("File") var File: String? = null,
    @SerializedName("isDeleted") var isDeleted: Int? = null,
    @SerializedName("BrokerId") var BrokerId: String? = null,
    @SerializedName("BrokerName") var BrokerName: String? = null,
    @SerializedName("DeviceID") var DeviceID: String? = null,
    @SerializedName("FireBaseToken") var FireBaseToken: String? = null,
    @SerializedName("CommissionPercentage") var CommissionPercentage: String? = null,
    @SerializedName("isAutoPay") var isAutoPay: String? = null,
    @SerializedName("CxId") var CxId: String? = null,
    @SerializedName("SubscriptionDetailsId") var SubscriptionDetailsId: String? = null,
    @SerializedName("ApprovedById") var ApprovedById: Int? = null,
    @SerializedName("landlordName") var landlordName: String? = null,
    @SerializedName("landlordPhoneNum") var landlordPhoneNum: String? = null,
    @SerializedName("landlordEmail") var landlordEmail: String? = null,
    @SerializedName("landlordId") var landlordId: String? = null,
    @SerializedName("SignupThroughInvite") var SignupThroughInvite: Boolean? = null,
    @SerializedName("IsLoginAllowed") var IsLoginAllowed: Boolean? = null,
    @SerializedName("InvitedById") var InvitedById: Long? = null,
    @SerializedName("ApprovedByName") var ApprovedByName: String? = null,
    @SerializedName("BankName") var BankName: String? = null,
    @SerializedName("TenantId") var TenantId: String? = null,
    @SerializedName("stripeAccId") var stripeAccId: String? = null,
    @SerializedName("setupComplete") var setupComplete: Boolean? = null,
    @SerializedName("CreatedON") var CreatedON: String? = null,
    @SerializedName("ModifiedON") var ModifiedON: String? = null
)