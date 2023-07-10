package mp.app.calonex.common.tenant

import com.google.gson.annotations.Expose

class TenantLeaseCredentials {
    @Expose
    var userId: String? = ""
    @Expose
    var userRole: String? = ""
    @Expose
    var leaseId: String? = ""
    @Expose
    var unitId: String? = ""
    @Expose
    var leaseHistory:Boolean=true
}