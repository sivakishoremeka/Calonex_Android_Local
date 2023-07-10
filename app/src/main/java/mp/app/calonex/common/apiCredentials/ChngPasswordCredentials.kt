package mp.app.calonex.common.apiCredentials

import com.google.gson.annotations.Expose

class ChngPasswordCredentials {

    @Expose
    var newPassword: String? = ""
    @Expose
    var userCatalogId: String? = ""

}