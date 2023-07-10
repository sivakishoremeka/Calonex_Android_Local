package mp.app.calonex.common.apiCredentials

import com.google.gson.annotations.Expose

class CxMessageSendNewMessageCredential {

    @Expose
    var messageId: String? = ""

    @Expose
    var msg: String? = ""

    @Expose
    var receiverId: String? = ""

    @Expose
    var recentMsg: String? = ""

    @Expose
    var senderId: String? = ""

    @Expose
    var subMessage: String? = ""

    @Expose
    var isClosed: Boolean? = false
}