package mp.app.calonex.landlord.response

import mp.app.calonex.landlord.model.OccupancyDetail
import mp.app.calonex.landlord.model.ResponseDto

data class LandlordOccupencyResponse
    (var responseDto: ResponseDto?, var data: OccupancyDetail?)
