package mp.app.calonex.common.apiCredentials

import com.google.gson.annotations.Expose

class CxNewMsgCredentials {

    @Expose
    var topic: String? = ""
    @Expose
    var priority: String? = ""
    @Expose
    var propertyId: String? = ""
    @Expose
    var unitId: String? = ""
    @Expose
    var message: String? = ""
    @Expose
    var receiverId: String? = ""
    @Expose
    var senderId: String? = ""
    @Expose
    var msg: String? = ""

}