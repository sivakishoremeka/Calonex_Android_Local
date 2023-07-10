package mp.app.calonex.tenant.model

data class TenantProperty(

    val address: String = "",
    val city: String = "",
    val state: String = "",
    val zipCode: String = "",
    val landlordFirstName: String = "",
    val landlordMiddleName: String = "",
    val landlordLastName: String = "",
    var unitDetails: ArrayList<TenantUnitDetail> = ArrayList<TenantUnitDetail>()

)
