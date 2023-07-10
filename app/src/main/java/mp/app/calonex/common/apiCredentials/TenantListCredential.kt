package mp.app.calonex.common.apiCredentials

import com.google.gson.annotations.Expose

class TenantListCredential {

    @Expose
    var landlordId: String? = "0"

    @Expose
    var propertyId: String? = "0"

    @Expose
    var unitId: String? = "0"

}