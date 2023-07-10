package mp.app.calonex.agent.responce

import com.google.gson.annotations.SerializedName

data class AgentBookKeepingGetCategoriesResponse
    (@SerializedName("id"        ) var id        : Int?    = null,
     @SerializedName("userId"    ) var userId    : Int?    = null,
     @SerializedName("name"      ) var name      : String? = null,
     @SerializedName("createdOn" ) var createdOn : Long?    = null,
     @SerializedName("updatedOn" ) var updatedOn : Long?    = null)

/*
[
  {
    "id": 7172,
    "userId": 1,
    "name": "Rent",
    "createdOn": 1641254400000,
    "updatedOn": 1641254400000
  }
]
 */