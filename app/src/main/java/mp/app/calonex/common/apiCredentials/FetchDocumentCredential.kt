package mp.app.calonex.common.apiCredentials

import com.google.gson.annotations.Expose

class FetchDocumentCredential {
    @Expose
    var tenantId: String? = ""
    @Expose
    var propertyId: String? = ""
    @Expose
    var unitId: String? = ""
    @Expose
    var userId: String? = ""
    @Expose
    var landlordId: String? = ""
    @Expose
    var leaseId: String? = ""
}