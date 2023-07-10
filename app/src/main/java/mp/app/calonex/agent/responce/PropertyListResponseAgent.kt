package mp.app.calonex.agent.responce

import mp.app.calonex.agent.model.PropertyAgent
import mp.app.calonex.landlord.model.ResponseDto

data class PropertyListResponseAgent
    (var responseCode: Int?, var exceptionCode: Int?, var responseDescription: String?,
     var responseDto: ResponseDto?, var data: ArrayList<PropertyAgent>?)