package mp.app.calonex.landlord.response

import mp.app.calonex.landlord.model.RentPaymentDashboard
import mp.app.calonex.landlord.model.ResponseDto

data class LdDashboardRentPaymentResponse
    (var responseDto: ResponseDto?, var data: RentPaymentDashboard?)
