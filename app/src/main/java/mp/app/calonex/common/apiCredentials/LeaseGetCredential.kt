package mp.app.calonex.common.apiCredentials

import com.google.gson.annotations.Expose

class LeaseGetCredential {

    @Expose
    var brokerId: String? = "0"

    @Expose
    var landlordId: String? = "0"

    @Expose
    var leaseId: String? = "0"

    @Expose
    var propertyId: String? = "0"

    @Expose
    var tenantId: String? = "0"

    @Expose
    var unitId: String? = "0"
}