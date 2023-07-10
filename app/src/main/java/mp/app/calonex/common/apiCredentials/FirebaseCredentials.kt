package mp.app.calonex.common.apiCredentials

import com.google.gson.annotations.Expose


class FirebaseCredentials {

    @Expose
    var firebaseToken: String? = ""

    @Expose
    var userId: String? = ""
}