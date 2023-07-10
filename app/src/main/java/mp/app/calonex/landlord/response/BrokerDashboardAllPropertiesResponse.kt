package mp.app.calonex.landlord.response

import mp.app.calonex.landlord.model.BrokerAllProperties
import mp.app.calonex.landlord.model.ResponseDto

data class BrokerDashboardAllPropertiesResponse
    (var responseDto: ResponseDto?, var data: ArrayList<BrokerAllProperties>?)
