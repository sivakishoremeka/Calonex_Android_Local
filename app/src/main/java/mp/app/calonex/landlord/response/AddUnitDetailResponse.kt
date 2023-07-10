package mp.app.calonex.landlord.response

import mp.app.calonex.landlord.model.ResponseDto

data class AddUnitDetailResponse
    (var responseDto: ResponseDto?, var responseCode: Int?, var exceptionCode: Int?)