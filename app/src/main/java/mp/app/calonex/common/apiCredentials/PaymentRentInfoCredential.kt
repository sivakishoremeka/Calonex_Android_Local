package mp.app.calonex.common.apiCredentials

import com.google.gson.annotations.Expose

class PaymentRentInfoCredential {

    @Expose
    var leaseId: Int? = 0

    @Expose
    var tenantId: Int? = 0

    @Expose
    var deviceTime: Long? = 0
}