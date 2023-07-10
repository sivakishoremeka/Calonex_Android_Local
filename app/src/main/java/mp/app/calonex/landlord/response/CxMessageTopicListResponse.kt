package mp.app.calonex.landlord.response

import mp.app.calonex.landlord.model.CxMessageListData
import mp.app.calonex.landlord.model.ResponseDto

data class CxMessageTopicListResponse
    (var responseDto: ResponseDto?, var data: ArrayList<CxMessageListData>?)
