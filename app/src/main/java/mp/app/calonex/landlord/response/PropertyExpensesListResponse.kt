package mp.app.calonex.landlord.response

import mp.app.calonex.landlord.model.PropertyExpense

data class PropertyExpensesListResponse
    (
    var responseCode: Int?,
    var exceptionCode: Int?,
    var responseDescription: String?,
    var data: List<PropertyExpense>?
)