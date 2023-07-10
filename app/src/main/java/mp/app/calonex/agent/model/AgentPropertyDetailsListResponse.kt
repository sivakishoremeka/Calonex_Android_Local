package mp.app.calonex.agent.model

import com.google.gson.annotations.SerializedName

data class AgentPropertyDetailsListResponse(
    @SerializedName("responseDto"         ) var responseDto         : ResponseDto?    = ResponseDto(),
    @SerializedName("data"                ) var data                : ArrayList<Data> = arrayListOf(),
    @SerializedName("responseDescription" ) var responseDescription : String?         = null,
    @SerializedName("responseCode"        ) var responseCode        : Int?            = null
    )

data class ResponseDto (

    @SerializedName("responseCode"  ) var responseCode  : Int? = null,
    @SerializedName("exceptionCode" ) var exceptionCode : Int? = null

)

data class Data (

    @SerializedName("createdOn"                 ) var createdOn                 : Int?                = null,
    @SerializedName("propertyId"                ) var propertyId                : Int?                = null,
    @SerializedName("userCatalogID"             ) var userCatalogID             : String?             = null,
    @SerializedName("address"                   ) var address                   : String?             = null,
    @SerializedName("city"                      ) var city                      : String?             = null,
    @SerializedName("state"                     ) var state                     : String?             = null,
    @SerializedName("zipCode"                   ) var zipCode                   : String?             = null,
    @SerializedName("numberOfUnits"             ) var numberOfUnits             : String?             = null,
    @SerializedName("description"               ) var description               : String?             = null,
    @SerializedName("brokerOrAgentLiscenceID"   ) var brokerOrAgentLiscenceID   : String?             = null,
    @SerializedName("broker"                    ) var broker                    : String?             = null,
    @SerializedName("brokerPhone"               ) var brokerPhone               : String?             = null,
    @SerializedName("brokerEmailID"             ) var brokerEmailID             : String?             = null,
    @SerializedName("propertyStatusID"          ) var propertyStatusID          : String?             = null,
    @SerializedName("unitsOccupied"             ) var unitsOccupied             : Int?                = null,
    @SerializedName("primaryContactId"          ) var primaryContactId          : String?             = null,
    @SerializedName("landLordName"              ) var landLordName              : String?             = null,
    @SerializedName("brokerAgentPropertyStatus" ) var brokerAgentPropertyStatus : String?             = null,
    @SerializedName("tenantApproved"            ) var tenantApproved            : Int?                = null,
    @SerializedName("unitsVacants"              ) var unitsVacants              : Int?                = null,
    @SerializedName("landlordFullName"          ) var landlordFullName          : String?             = null,
    @SerializedName("tenantSubmitted"           ) var tenantSubmitted           : Int?                = null,
    @SerializedName("allowTenantOnboard"        ) var allowTenantOnboard        : String?             = null,
    @SerializedName("availableNumberOfUnit"     ) var availableNumberOfUnit     : Int?                = null,
    @SerializedName("fileList"                  ) var fileList                  : ArrayList<FileList> = arrayListOf(),
    @SerializedName("linkedByBrokerAgent"       ) var linkedByBrokerAgent       : Boolean?            = null,
    @SerializedName("linkedByBrokerAgentOnAdd"  ) var linkedByBrokerAgentOnAdd  : Boolean?            = null

)

data class FileList (

    @SerializedName("fileName"       ) var fileName       : String? = null,
    @SerializedName("uploadFileType" ) var uploadFileType : String? = null

)

