package mp.app.calonex.landlord.model

import com.google.gson.annotations.SerializedName

data class PropertyExpensePayload (
    @SerializedName("propertyId"                 ) var propertyId                 : Int?                                  = null,
    @SerializedName("propertyExpensesTypeDTO"    ) var propertyExpensesTypeDTO    : PropertyExpensesTypeDTO?              = PropertyExpensesTypeDTO(),
    @SerializedName("propertyExpensesDetailsDTO" ) var propertyExpensesDetailsDTO : ArrayList<PropertyExpensesDetailsDTO> = arrayListOf()
)

data class PropertyExpensesTypeDTO (

    @SerializedName("propertyExpensesId" ) var propertyExpensesId : ArrayList<String> = arrayListOf()

)

data class PropertyExpensesDetailsDTO (

    @SerializedName("propertyExpensesId" ) var propertyExpensesId : Int?    = null,
    @SerializedName("expensesAmount"     ) var expensesAmount     : Double?    = null,
    @SerializedName("expensesType"       ) var expensesType       : String? = null,
    @SerializedName("expensesMonth"      ) var expensesMonth      : Long?    = null,
    @SerializedName("expensesYear"       ) var expensesYear       : Long?    = null

)