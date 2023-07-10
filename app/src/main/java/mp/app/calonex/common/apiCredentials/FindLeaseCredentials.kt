package mp.app.calonex.common.apiCredentials

import com.google.gson.annotations.Expose

class FindLeaseCredentials {
    @Expose
    var userId: String? = ""
    @Expose
    var userRole: String? = ""
    @Expose
    var propertyId: String? = ""
    @Expose
    var leaseId: String? = ""
    @Expose
    var unitId: String? = ""
    @Expose
    var leaseHistory:Boolean=false
}