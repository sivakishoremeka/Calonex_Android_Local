package mp.app.calonex.common.apiCredentials

import com.google.gson.annotations.Expose

class LinkPropertyActionCredentialLandlord {

    @Expose
    var action: String? = ""
    @Expose
    var propertyId: String? = ""
    @Expose
    var userCatalogID: String? = "";
    @Expose
    var onBoardedBy: String? = "";
    @Expose
    var notificationId: Long=0;
   //  @Expose
   // var fromEmailId: String? = "";
  /*  @Expose
    var propertyLinkRequestId: String?="";
*/}