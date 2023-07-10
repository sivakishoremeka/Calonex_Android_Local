package mp.app.calonex.tenant.response

import mp.app.calonex.landlord.model.ResponseDto
import mp.app.calonex.tenant.model.BankInfo

data class GetBankInfoResponse
    (var responseDto: ResponseDto?, var data: BankInfo?)