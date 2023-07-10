package mp.app.calonex.common.apiCredentials

import com.google.gson.annotations.Expose


class Credentials {

    @Expose
    var emailId: String? = ""

    @Expose
    var password: String? = ""

    @Expose
    var deviceId: String? = ""


}