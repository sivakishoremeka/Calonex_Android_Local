package mp.app.calonex.landlord.model

import java.io.Serializable

class PropertyDetail: Serializable {

    val propertyId: Long = 0
    var createdOn: Long = 0
    var version: Long = 0
    var address: String = ""
    var city: String = ""
    var state: String = ""
    var zipCode: String = ""
    var numberOfUnits: String = ""
    var description: String = ""
    var brokerOrAgentLiscenceID: String = ""
    var primaryContact: String = ""
    var primaryContactId: String = ""
    var broker: String = ""
    var brokerPhone: String = ""
    var brokerEmailID: String = ""
    var buildingType: String = ""
    var propertyStatus: String = ""
    var propertyStatusID:String = ""
    var buildingTypeId: Long = 0
    var totalRevenue: String = ""

    var propertyUnitDetailsDTO = ArrayList<UnitDetailsPD>()
    var unitFeatureDTO = ArrayList<UnitFeaturePD>()
    var unitUtilitiesDTO = ArrayList<UnitUtilityPD>()
    var buildingFeatureDTO = ArrayList<BuildingFeaturePD>()
    var parkingTypeDTO = ArrayList<ParkingTypePD>()
    var propertyExpensesDetailsDTO = ArrayList<PropertyExpensePD>()

    var onBoardedBy: String = ""
    var brokerAgentPropertyStatus: String = ""
    var linkedByBrokerAgent: Boolean = false
    var approved: Boolean = false

    var landlordFullName: String? = ""
    var landlordEmailId: String? = ""
    var landlordId: String? = ""
}