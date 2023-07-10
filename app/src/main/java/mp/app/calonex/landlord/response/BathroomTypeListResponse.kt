package mp.app.calonex.landlord.response

import mp.app.calonex.landlord.model.BathroomType

data class BathroomTypeListResponse
    (
    var responseCode: Int?,
    var exceptionCode: Int?,
    var responseDescription: String?,
    var data: List<BathroomType>?
)