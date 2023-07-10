package mp.app.calonex.landlord.response

import mp.app.calonex.landlord.model.LandlordPaymentHistory
import mp.app.calonex.landlord.model.ResponseDto

data class LandlordPaymentHistoryListResponse
    (var responseDto: ResponseDto?, var data: ArrayList<LandlordPaymentHistory>?)
