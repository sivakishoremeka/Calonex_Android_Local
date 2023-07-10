package mp.app.calonex.common.apiCredentials

import com.google.gson.annotations.Expose


class TenantPaymentHistoryCredentials {

    @Expose
    var tenantId: String? = ""

    @Expose
    var year: String? = ""

}