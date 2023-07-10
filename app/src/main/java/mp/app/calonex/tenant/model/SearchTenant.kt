package mp.app.calonex.tenant.model

data class SearchTenant(
    val alternatePhoneNumber: String = "",
    var emailId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val middleName: String = "",
    var phoneNumber: String="",
    var userCatalogId:String=""
)
