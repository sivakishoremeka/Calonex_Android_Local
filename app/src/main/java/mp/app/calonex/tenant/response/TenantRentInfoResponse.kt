package mp.app.calonex.tenant.response

import mp.app.calonex.landlord.model.ResponseDto
import mp.app.calonex.tenant.model.TenantRentInfo

data class TenantRentInfoResponse
    (var responseDto: ResponseDto?,var responseDescription: String?, var data: TenantRentInfo?)