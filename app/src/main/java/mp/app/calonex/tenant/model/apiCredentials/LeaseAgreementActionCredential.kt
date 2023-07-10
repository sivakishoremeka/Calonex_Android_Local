package mp.app.calonex.tenant.model.apiCredentials

import com.google.gson.annotations.Expose

class LeaseAgreementActionCredential {

    @Expose
    var userId: String? = ""

    @Expose
    var leaseId: String? = ""

    @Expose
    var action: String? = ""
}