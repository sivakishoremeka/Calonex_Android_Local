package mp.app.calonex.landlord.response

import mp.app.calonex.landlord.model.BrokerMarketPlace
import mp.app.calonex.landlord.model.ResponseDto

data class BrokerMarketPlaceResponse
    (var responseDto: ResponseDto?, var data: ArrayList<BrokerMarketPlace>?)
