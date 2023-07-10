package mp.app.calonex.broker.responce

import mp.app.calonex.broker.model.BrokerPaymentHistory
import mp.app.calonex.landlord.model.ResponseDto

data class BrokerPaymentHistoryListResponse
    (var responseDto: ResponseDto?, var data: ArrayList<BrokerPaymentHistory>?)
