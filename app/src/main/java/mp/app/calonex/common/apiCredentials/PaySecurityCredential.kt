package mp.app.calonex.common.apiCredentials

import com.google.gson.annotations.Expose

class PaySecurityCredential {

    @Expose
    var userId: String? = ""

    @Expose
    var securityAmount: String? = ""

    @Expose
    var leaseId: String? = ""
}