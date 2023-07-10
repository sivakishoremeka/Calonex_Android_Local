package mp.app.calonex.landlord.response

import mp.app.calonex.landlord.model.UnitUtility

data class UnitUtilitiesResponse
    (
    var responseCode: Int?,
    var exceptionCode: Int?,
    var responseDescription: String?,
    var data: List<UnitUtility>?
)