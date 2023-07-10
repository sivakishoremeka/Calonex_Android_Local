package mp.app.calonex.landlord.model

data class CxMsgUsers(
val userId: String = "",
val userName: String = "",
val userFirstName: String = "",
val userLastName: String = "",
val propertyId: String = "",
val propertyAddress: String = "",
val unitId: String = "",
val unitNumber: String = "",
val role: String = "",
val brokerLicenseNumber: String = "",
val agentLicenseNumber: String = ""
)