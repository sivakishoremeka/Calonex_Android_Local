package mp.app.calonex.tenant.response

import mp.app.calonex.landlord.model.ResponseDto
import mp.app.calonex.tenant.model.CreditCardInfo

data class GetCreditCardInfoResponse

(var responseDto: ResponseDto?,var responseCode: Int?,var responseDescription: String?, var data: CreditCardInfo?)