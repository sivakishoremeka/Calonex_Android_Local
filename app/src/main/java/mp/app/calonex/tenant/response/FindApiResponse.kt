package mp.app.calonex.tenant.response

import mp.app.calonex.landlord.model.ResponseDto
import mp.app.calonex.tenant.model.LeaseTenantInfo

data class FindApiResponse
    (var responseDto: ResponseDto?, var data: ArrayList<LeaseTenantInfo>?)