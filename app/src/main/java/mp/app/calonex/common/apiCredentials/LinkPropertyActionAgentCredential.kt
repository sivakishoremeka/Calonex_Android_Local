package mp.app.calonex.common.apiCredentials

import com.google.gson.annotations.Expose

class LinkPropertyActionAgentCredential {

    @Expose
    var action: String? = ""
    @Expose
    var agentLicenseNo: String? = ""

    @Expose
    var commissionPercentage: String? = ""

    @Expose
    var notificationId: Long=0

    @Expose
    var userCatalogId:String?="";
}