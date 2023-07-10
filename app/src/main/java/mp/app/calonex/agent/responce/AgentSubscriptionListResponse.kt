package mp.app.calonex.agent.responce

import mp.app.calonex.agent.model.AgentSubscriptionList
import mp.app.calonex.landlord.model.ResponseDto

data class AgentSubscriptionListResponse
    (var responseDto: ResponseDto?, var data: ArrayList<AgentSubscriptionList>?)
