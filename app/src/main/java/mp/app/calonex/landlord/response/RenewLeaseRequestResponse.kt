package mp.app.calonex.landlord.response

import mp.app.calonex.landlord.model.ObLeaseTenantPayload
import mp.app.calonex.landlord.model.ResponseDto

data class RenewLeaseRequestResponse
    (var responseDto: ResponseDto?, var data: ObLeaseTenantPayload?)
