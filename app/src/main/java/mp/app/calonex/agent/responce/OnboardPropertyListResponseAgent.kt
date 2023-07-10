package mp.app.calonex.agent.responce

import mp.app.calonex.agent.model.PropertyAgent
import mp.app.calonex.landlord.model.ResponseDto

data class OnboardPropertyListResponseAgent
    (var responseDto: ResponseDto?, var data: ArrayList<PropertyAgent>?)