package mp.app.calonex.landlord.model

import mp.app.calonex.agent.model.PropertyImageModel

data class Property(
    var primaryContactId: String = "",

    val propertyId: Long = 0,
    val address: String = "",
    val userCatalogID: String = "",
    val ownerName: String = "",
    val city: String = "",
    val state: String = "",
    val zipCode: String = "",
    val phone: String = "",
    val propertyStatusID: String = "",
    val description: String = "",
    val brokerOrAgentLiscenceID: String = "",
    val primaryContact: String = "",
    val broker: String = "",
    val brokerPhone: String = "",
    val brokerEmailID: String = "",
    val unitsOccupied: Long = 0,
    val revenue: String = "",
    val tenantRequest: Long = 0,
    val buildingTypeID: Long = 0,
    var brokerAgentPropertyStatus: String = "",
    val numberOfUnits: String = "",
    val createdOn: Long? = null,
    val dateUpdated: Long? = null,
    val approved: Boolean = false,
    val linkedByBrokerAgent: Boolean = false,
    var availableNumberOfUnit: String = "",
    val responseDescription: String = "",
    val responseCode: Long = 0,
    var onBoardedBy: String = "",
    var tenantApproved: String = "",
    var unitsVacants: String = "",
    var tenantFirstName: String = "",
    var tenantLastName: String = "",
    var tenantEmailId: String = "",
    var propertyImgURI: String = "",
    var unitsOccupiedOutsideCX: String = "",
    var unitsOccupiedInsideCX: String = "",

    var linkedByBrokerAgentOnAdd: String = "",
    val fileList: List<PropertyImageModel> = ArrayList()


)