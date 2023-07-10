package mp.app.calonex.common.apiCredentials

import com.google.gson.annotations.Expose

class FranchiseDetailCredential {
    @Expose
    var userId: String? = ""
    @Expose
    var franchiseName: String? = ""

    @Expose
    var percentage: String? = ""
}