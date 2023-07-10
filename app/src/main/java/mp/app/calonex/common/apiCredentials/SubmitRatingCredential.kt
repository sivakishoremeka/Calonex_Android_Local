package mp.app.calonex.common.apiCredentials

import com.google.gson.annotations.Expose

class SubmitRatingCredential {

    @Expose
    var fromTenantId: String? = ""

    @Expose
    var landlordId: String? = ""

    @Expose
    var landlordRating: String? = ""

    @Expose
    var propertyId: String? = ""

    @Expose
    var propertyRating: String? = ""

    @Expose
    var unitId: String? = ""

    @Expose
    var unitRating: String? = ""
}