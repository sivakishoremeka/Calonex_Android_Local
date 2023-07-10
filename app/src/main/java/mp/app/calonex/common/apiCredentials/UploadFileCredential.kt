package mp.app.calonex.common.apiCredentials

import com.google.gson.annotations.Expose

class UploadFileCredential {

    @Expose
    var file: String? = ""

    @Expose
    var emailId: String? = ""

    @Expose
    var uploadFileType: String? = ""

    @Expose
    var propertyId: String? = ""

    @Expose
    var tenantId: String? = ""

    @Expose
    var unitId: String? = ""

    @Expose
    var userId: String? = ""
}