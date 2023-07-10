package mp.app.calonex.agent.model

data class AgentBookKeepingAmount(
    val totalExpenses: Float = 0.0f,
    val totalEarnings: Float=0.0f,
    val balance: Float=0.0f,
    val otherExpenses: Float=0.0f,
    val otherEarnings: Float=0.0f,
    val rentPaid: Float=0.0f
    )

data class MontlyRevenueDTO (
    var month: String?="",
    var monthName:String?="" ,
    var year: String?="" ,
    var totalAmount:Float=0.0f,
    var type: String?=""
)

data class filtered(
    var month: String?="",
    var totalExpenses:Float=0.0f,
    var totalEarnings: Float=0.0F,
    var totalBalance:Float=0.0F
   )


/*
 "totalExpenses": 6755.00,
    "totalEarnings": 1442.00,
    "balance": -5313.00,
    "otherExpenses": 6655.00,
    "otherEarnings": 1442.00,
    "rentPaid": 100.00


===========OLD==============
{
    "totalExpenses": 37.00,
    "totalEarnings": 43.05,
    "balance": 6.05
  }
 */