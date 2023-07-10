package mp.app.calonex.common.apiCredentials

import com.google.gson.annotations.Expose


class CredentialsTest {

    @Expose
    var authtype: String? = ""

    @Expose
    var redirect_uri: String? = ""

    @Expose
    var role: String? = ""

}