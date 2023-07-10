package mp.app.calonex.landlord.model

import mp.app.calonex.landlord.response.*

data class ApiAllModel(var buildingTypeListResponse: BuildingTypeListResponse,var buildingFeatureListResponse: BuildingFeatureListResponse,
                       var parkingTypeListResponse: ParkingTypeListResponse, var bathroomTypeListResponse: BathroomTypeListResponse,
                       var unitFeatureListResponse: UnitFeatureListResponse, var unitTypeListResponse: UnitTypeListResponse,
                       var unitUtilitiesResponse: UnitUtilitiesResponse, var propertyExpensesListResponse: PropertyExpensesListResponse)
