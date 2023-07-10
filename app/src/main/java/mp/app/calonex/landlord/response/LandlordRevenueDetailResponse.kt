package mp.app.calonex.landlord.response

import mp.app.calonex.landlord.model.LandlordRevenueDetail
import mp.app.calonex.landlord.model.ResponseDto

data class LandlordRevenueDetailResponse
    (var responseDto: ResponseDto?, var data: LandlordRevenueDetail?)
