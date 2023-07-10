package mp.app.calonex.agent.model

import com.google.gson.annotations.SerializedName

data class BookKeepingAddResponseData (
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("category")
    val category: Int,
    @SerializedName("createdOn")
    val createdOn: Long,
    @SerializedName("date")
    val date: Long,
    @SerializedName("description")
    val description: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("type")
    val type: String,
    @SerializedName("updatedOn")
    val updatedOn: Long,
    @SerializedName("userId")
    val userId: Int
)