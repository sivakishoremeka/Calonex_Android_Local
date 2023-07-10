package mp.app.calonex.landlord.response

import mp.app.calonex.landlord.model.LeaseRequestInfo
import mp.app.calonex.landlord.model.ResponseDto

data class BrkrTenantLeaseActionResponse
    (var responseDto: ResponseDto?, var data: LeaseRequestInfo?)