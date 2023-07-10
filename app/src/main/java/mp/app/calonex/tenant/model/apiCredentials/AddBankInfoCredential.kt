package mp.app.calonex.tenant.model.apiCredentials

import com.google.gson.annotations.Expose

class AddBankInfoCredential {

    @Expose
    var userId: String? = ""

    @Expose
    var accountNo: String? = ""

    @Expose
    var userName: String? = ""

    @Expose
    var routingNo: String? = ""

    @Expose
    var autoPay: Boolean? = false

    @Expose
    var isCreditCard: Boolean? = false

    @Expose
    var token: String? = ""
}