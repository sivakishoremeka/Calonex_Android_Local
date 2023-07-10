package mp.app.calonex.landlord.response


import com.google.gson.annotations.SerializedName

data class PropertyExpenseResponse
    (
    @SerializedName("responseDto"         ) var responseDto         : ResponseDto? = ResponseDto(),
    @SerializedName("data"                ) var data                : Data?        = Data(),
    @SerializedName("responseDescription" ) var responseDescription : String?      = null
    )

data class ResponseDto (

    @SerializedName("responseCode"  ) var responseCode  : Int? = null,
    @SerializedName("exceptionCode" ) var exceptionCode : Int? = null

)

data class ParkingTypeDetails (

    @SerializedName("deleted"           ) var deleted           : Boolean? = null,
    @SerializedName("createdOn"         ) var createdOn         : String?  = null,
    @SerializedName("propertyParkingId" ) var propertyParkingId : Int?     = null,
    @SerializedName("parkingTypeID"     ) var parkingTypeID     : Int?     = null,
    @SerializedName("id"                ) var id                : Int?     = null,
    @SerializedName("createdBy"         ) var createdBy         : Int?     = null

)

data class PropertyExpensesDetails (

    @SerializedName("propertyExpensesId"       ) var propertyExpensesId       : Int? = null,
    @SerializedName("expensesMonth"            ) var expensesMonth            : Int? = null,
    @SerializedName("expensesYear"             ) var expensesYear             : Int? = null,
    @SerializedName("expensesAmount"           ) var expensesAmount           : Int? = null,
    @SerializedName("createdBy"                ) var createdBy                : Int? = null,
    @SerializedName("propertyExpensesDetailID" ) var propertyExpensesDetailID : Int? = null

)

data class Data (

    @SerializedName("deleted"                   ) var deleted                   : Boolean?                           = null,
    @SerializedName("createdOn"                 ) var createdOn                 : String?                            = null,
    @SerializedName("propertyId"                ) var propertyId                : Int?                               = null,
    @SerializedName("userCatalogID"             ) var userCatalogID             : String?                            = null,
    @SerializedName("address"                   ) var address                   : String?                            = null,
    @SerializedName("city"                      ) var city                      : String?                            = null,
    @SerializedName("state"                     ) var state                     : String?                            = null,
    @SerializedName("zipCode"                   ) var zipCode                   : String?                            = null,
    @SerializedName("phone"                     ) var phone                     : String?                            = null,
    @SerializedName("numberOfUnits"             ) var numberOfUnits             : String?                            = null,
    @SerializedName("buildingTypeID"            ) var buildingTypeID            : Int?                               = null,
    @SerializedName("description"               ) var description               : String?                            = null,
    @SerializedName("brokerOrAgentLiscenceID"   ) var brokerOrAgentLiscenceID   : String?                            = null,
    @SerializedName("primaryContact"            ) var primaryContact            : String?                            = null,
    @SerializedName("broker"                    ) var broker                    : String?                            = null,
    @SerializedName("brokerPhone"               ) var brokerPhone               : String?                            = null,
    @SerializedName("brokerEmailID"             ) var brokerEmailID             : String?                            = null,
    @SerializedName("propertyStatusID"          ) var propertyStatusID          : String?                            = null,
    @SerializedName("brokerId"                  ) var brokerId                  : String?                            = null,
    @SerializedName("createdBy"                 ) var createdBy                 : Int?                               = null,
    @SerializedName("propertyDocuments"         ) var propertyDocuments         : String?                            = null,
    @SerializedName("driverLicense"             ) var driverLicense             : String?                            = null,
    @SerializedName("bankAccountNumber"         ) var bankAccountNumber         : String?                            = null,
    @SerializedName("routingNumber"             ) var routingNumber             : String?                            = null,
    @SerializedName("primaryContactId"          ) var primaryContactId          : String?                            = null,
    @SerializedName("brokerAgentPropertyStatus" ) var brokerAgentPropertyStatus : String?                            = null,
    @SerializedName("onBoardedBy"               ) var onBoardedBy               : String?                            = null,
    @SerializedName("allowPropertyAccess"       ) var allowPropertyAccess       : Boolean?                           = null,
    @SerializedName("approvedById"              ) var approvedById              : Int?                               = null,
    @SerializedName("approvedByName"            ) var approvedByName            : String?                            = null,
    @SerializedName("latitude"                  ) var latitude                  : String?                            = null,
    @SerializedName("longitude"                 ) var longitude                 : String?                            = null,
    @SerializedName("buildingFeature"           ) var buildingFeature           : ArrayList<String>                  = arrayListOf(),
    @SerializedName("parkingTypeDetails"        ) var parkingTypeDetails        : ArrayList<ParkingTypeDetails>      = arrayListOf(),
    @SerializedName("propertyExpensesDetails"   ) var propertyExpensesDetails   : ArrayList<PropertyExpensesDetails> = arrayListOf(),
    @SerializedName("id"                        ) var id                        : Int?                               = null,
    @SerializedName("linkedByBrokerAgent"       ) var linkedByBrokerAgent       : Boolean?                           = null

)