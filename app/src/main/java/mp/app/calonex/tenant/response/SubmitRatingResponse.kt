package mp.app.calonex.tenant.response

import mp.app.calonex.landlord.model.ResponseDto
import mp.app.calonex.tenant.model.TenantSubmitRating

data class SubmitRatingResponse
    (var responseDto: ResponseDto?, var data: TenantSubmitRating?)