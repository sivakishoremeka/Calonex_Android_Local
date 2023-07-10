package mp.app.calonex.tenant.response

import mp.app.calonex.landlord.model.ResponseDto
import mp.app.calonex.tenant.model.TenantProperty

data class TenantPropertyUnitDetailResponse
    (var responseDto: ResponseDto?, var data: ArrayList<TenantProperty>?)