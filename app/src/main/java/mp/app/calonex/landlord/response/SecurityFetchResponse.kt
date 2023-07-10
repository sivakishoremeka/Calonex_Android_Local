package mp.app.calonex.landlord.response

import mp.app.calonex.landlord.model.FetchSecurityInfo
import mp.app.calonex.landlord.model.ResponseDataDto

data class SecurityFetchResponse
    (var responseDto: ResponseDataDto?, var data: ArrayList<FetchSecurityInfo>?)

