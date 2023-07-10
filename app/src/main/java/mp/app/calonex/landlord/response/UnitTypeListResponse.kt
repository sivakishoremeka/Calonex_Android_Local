package mp.app.calonex.landlord.response

import mp.app.calonex.landlord.model.UnitType

data class UnitTypeListResponse
    (
    var responseCode: Int?,
    var exceptionCode: Int?,
    var responseDescription: String?,
    var data: List<UnitType>?
)