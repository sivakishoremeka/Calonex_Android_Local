package mp.app.calonex.tenant.model

import mp.app.calonex.landlord.model.LateFeeRentDto
import mp.app.calonex.landlord.model.MontWiseRentDto

data class RentBreakup(

    var previousDue: String = "",
    val serviceFee: String = "",
    val lateFeeMonthWise: ArrayList<LateFeeRentDto> = ArrayList<LateFeeRentDto>(),
    var rentMonthWise: MontWiseRentDto = MontWiseRentDto(),
    var rentDueMonthWise: ArrayList<MontWiseRentDto> = ArrayList<MontWiseRentDto>(),

//    val rent: String = "",
//    val applicableLateFees: String = "",
//    val securityAmount: String = "",
//    val amenities: String = "",
    val rentPaid: Boolean = false,
var freeMonth:Boolean=false
)
