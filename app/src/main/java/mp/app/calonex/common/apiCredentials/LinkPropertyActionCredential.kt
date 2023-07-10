package mp.app.calonex.common.apiCredentials

import com.google.gson.annotations.Expose

class LinkPropertyActionCredential {

    @Expose
    var action: String? = ""
    @Expose
    var agentLicenseNo: String? = ""
    @Expose
    var brokerAgentLinkRequestId: String? = ""
    @Expose
    var commissionPercentage: String? = ""
    @Expose
    var onBoardedByEmail: String? = ""
    @Expose
    var notificationId: String? = ""
    @Expose
    var propertyId: String? = ""
    @Expose
    var propertyLinkRequestId: String? = ""
    @Expose
    var userCatalogId:String?="";
}