package mp.app.calonex.agent.responce

import mp.app.calonex.agent.model.AgentPaymentHistory
import mp.app.calonex.landlord.model.ResponseDto

data class AgentPaymentHistoryListResponse
    (var responseDto: ResponseDto?, var data: ArrayList<AgentPaymentHistory>?)
