package mp.app.calonex.landlord.response

import mp.app.calonex.landlord.model.ResponseDto
import mp.app.calonex.landlord.model.UserDetail

data class UserDetailResponse (
    var responseDto: ResponseDto?,
    var data: UserDetail?
)