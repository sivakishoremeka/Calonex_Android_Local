package mp.app.calonex.landlord.response

import mp.app.calonex.landlord.model.UnitFeature

data class UnitFeatureListResponse
    (
    var responseCode: Int?,
    var exceptionCode: Int?,
    var responseDescription: String?,
    var data: List<UnitFeature>?
)