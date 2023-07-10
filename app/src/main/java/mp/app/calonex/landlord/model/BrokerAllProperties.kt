package mp.app.calonex.landlord.model

data class BrokerAllProperties(
    var propertyAddress: String = "",
    var numberOfUnitsRented: String = "",
    var unitStatus: String = "",
    var propertyId: String = "",
    var propertyImgURI: String = "",
    var propertyOwnerName: String = "",
    var propertyEmail: String = "",
    var propertyContact: String = ""
)