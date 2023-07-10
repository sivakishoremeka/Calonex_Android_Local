package mp.app.calonex.landlord.response

import mp.app.calonex.landlord.model.BuildingType

data class BuildingTypeListResponse
    (
    var responseCode: Int?,
    var exceptionCode: Int?,
    var responseDescription: String?,
    var data: List<BuildingType>
)