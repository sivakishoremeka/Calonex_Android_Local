package mp.app.calonex.landlord.response

import mp.app.calonex.landlord.model.Property

data class PropertyListResponse
    (
    var responseCode: Int?,
    var exceptionCode: Int?,
    var responseDescription: String?,
    var responseDto: ResponseDto?,
    var userDetailsDto: LandlordDetailsByEmail?,
    var data: ArrayList<Property>?
)