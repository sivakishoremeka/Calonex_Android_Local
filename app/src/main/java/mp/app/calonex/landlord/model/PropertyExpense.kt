package mp.app.calonex.landlord.model

data class PropertyExpense(
    var propertyExpensesId: Long = 0,
    var createdBy: Long = 0,
    var propertyExpenses: String="",
    var expensesType: String=""
    )