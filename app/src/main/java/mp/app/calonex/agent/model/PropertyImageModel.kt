package mp.app.calonex.agent.model


import com.google.gson.annotations.SerializedName

data class PropertyImageModel(
    @SerializedName("fileName")
    val fileName: String,
    @SerializedName("uploadFileType")
    val uploadFileType: String
)