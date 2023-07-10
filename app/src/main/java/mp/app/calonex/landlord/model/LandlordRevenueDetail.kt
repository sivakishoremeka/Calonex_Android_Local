package mp.app.calonex.landlord.model

data class LandlordRevenueDetail(
    var monthlyIncome: ArrayList<MonthlyIncome>? = null,
    var year: ArrayList<String>? = null,
    var profit: String = "",
    var income: String = "",
var yearlyExpenses:ArrayList<YearlyExpenses>?=null
)