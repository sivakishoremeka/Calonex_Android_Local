package mp.app.calonex.landlord.response

import mp.app.calonex.landlord.model.BrokerAgentDetails
import mp.app.calonex.landlord.model.ResponseDto

data class BrokerAgentDetailResponse
    (var responseDto: ResponseDto?, var statusCode: Int?, var data: BrokerAgentDetails?)