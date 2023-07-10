package mp.app.calonex.landlord.model

data class PropertyExpensePD(
    var propertyExpensesId: Long,
    var expensesMonth: Long,
    var expensesYear: Long,
    var expensesAmount: String,
    var propertyExpensesDetailsID: Long,
    var propertyExpensesType: String
    )