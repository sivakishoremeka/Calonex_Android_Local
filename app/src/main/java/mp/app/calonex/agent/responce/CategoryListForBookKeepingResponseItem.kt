package mp.app.calonex.agent.responce
import com.google.gson.annotations.SerializedName

data class CategoryListForBookKeepingResponseItem(
    @SerializedName("bookKeepingType")
    var bookKeepingType: String,
    @SerializedName("createdOn")
    var createdOn: Long,
    @SerializedName("id")
    var id: Int,
    @SerializedName("name")
    var name: String,
    @SerializedName("roleName")
    var roleName: String,
    @SerializedName("updatedOn")
    var updatedOn: Long,
    @SerializedName("userId")
    var userId: Int
)