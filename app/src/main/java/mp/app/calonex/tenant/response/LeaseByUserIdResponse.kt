package mp.app.calonex.tenant.response

import mp.app.calonex.landlord.model.ResponseDto
import mp.app.calonex.landlord.model.TenantInfoPayload

data class LeaseByUserIdResponse
    (var responseDto: ResponseDto?, var data: ArrayList<TenantInfoPayload>?)