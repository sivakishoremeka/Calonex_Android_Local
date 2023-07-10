package mp.app.calonex.landlord.response

import mp.app.calonex.landlord.model.LeaseRequestInfo
import mp.app.calonex.landlord.model.ResponseDataDto

data class FindLeaseResponse
    (var responseDto: ResponseDataDto?, var data: ArrayList<LeaseRequestInfo>?)