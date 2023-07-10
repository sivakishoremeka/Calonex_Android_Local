package mp.app.calonex.landlord.response

import mp.app.calonex.landlord.model.CxMsgUsers
import mp.app.calonex.landlord.model.ResponseDto

data class CxMsgUsersResponse (
    var responseDto: ResponseDto?, var responseCode: Int?, var data: ArrayList<CxMsgUsers>?
)
