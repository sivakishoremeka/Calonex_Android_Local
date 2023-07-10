package mp.app.calonex.tenant.model

data class Tenant(

    var emailId: String = "",
    var cxId: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var address: String = "",
    var city: String = "",
    var state: String = "",
    var zipCode: String = "",
    var phone: String = "",
    var license: String = "",
    var profilePic: String = "",
    var ssn: String = "",
    var voidCheck: String = "",
    var monthPayStab: String = "",
    var signature: String = "",

    var unitDetails: ArrayList<TenantUnitDetail> = ArrayList<TenantUnitDetail>()

)
