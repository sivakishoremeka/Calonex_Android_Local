package mp.app.calonex.landlord.response

import mp.app.calonex.landlord.model.RatingDetail
import mp.app.calonex.landlord.model.ResponseDto

data class LandlordRatingResponse
    (var responseDto: ResponseDto?, var data: RatingDetail?)
