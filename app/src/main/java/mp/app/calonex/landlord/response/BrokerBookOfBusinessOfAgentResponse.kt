package mp.app.calonex.landlord.response

import mp.app.calonex.landlord.model.BrokerBookOfBusinessOfAgent
import mp.app.calonex.landlord.model.ResponseDto

data class BrokerBookOfBusinessOfAgentResponse
    (var responseDto: ResponseDto?, var data: ArrayList<BrokerBookOfBusinessOfAgent>?)
