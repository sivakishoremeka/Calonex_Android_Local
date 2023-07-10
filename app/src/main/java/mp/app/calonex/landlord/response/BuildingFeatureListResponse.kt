package mp.app.calonex.landlord.response

import mp.app.calonex.landlord.model.BuildingFeature

data class BuildingFeatureListResponse
    (
    var responseCode: Int?,
    var exceptionCode: Int?,
    var responseDescription: String?,
    var data: List<BuildingFeature>
)