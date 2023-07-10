package mp.app.calonex.landlord.response

import mp.app.calonex.landlord.model.ParkingType

data class ParkingTypeListResponse
    (
    var responseCode: Int?,
    var exceptionCode: Int?,
    var responseDescription: String?,
    var data: List<ParkingType>?
)