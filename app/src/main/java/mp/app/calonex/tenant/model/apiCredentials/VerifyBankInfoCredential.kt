package mp.app.calonex.tenant.model.apiCredentials

import com.google.gson.annotations.Expose

class VerifyBankInfoCredential {

    @Expose
    var userCatelogId: String? = ""

    @Expose
    var firstamount: String? = ""

    @Expose
    var secondamount: String? = ""
}