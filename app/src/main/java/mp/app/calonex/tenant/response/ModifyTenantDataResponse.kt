package mp.app.calonex.tenant.response

import mp.app.calonex.landlord.model.ResponseDto
import mp.app.calonex.tenant.model.ModifyTenantDataModel

data class ModifyTenantDataResponse
    (var responseDto: ResponseDto?, var data: ModifyTenantDataModel)