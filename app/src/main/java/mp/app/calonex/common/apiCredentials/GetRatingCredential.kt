package mp.app.calonex.common.apiCredentials

import com.google.gson.annotations.Expose

class GetRatingCredential {

    @Expose
    var fromTenantId: String? = ""

    @Expose
    var landlordId: String? = ""

    @Expose
    var propertyId: String? = ""

    @Expose
    var unitId: String? = ""
}