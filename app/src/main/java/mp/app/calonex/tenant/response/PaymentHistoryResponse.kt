package mp.app.calonex.tenant.response

import mp.app.calonex.landlord.model.ResponseDto
import mp.app.calonex.tenant.model.PaymentHistory

data class PaymentHistoryResponse
    (var responseDto: ResponseDto?, var data: ArrayList<PaymentHistory>?)