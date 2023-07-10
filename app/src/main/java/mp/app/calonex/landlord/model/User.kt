package mp.app.calonex.landlord.model

import com.google.gson.annotations.SerializedName
import java.util.*

//import androidx.room.Entity
//import androidx.room.PrimaryKey
//import androidx.room.TypeConverter
//import com.google.gson.Gson

//@Entity(tableName = "user")
class User {
    @SerializedName("refresh_token")
    var refresh_token: String = ""

    @SerializedName("access_token")
    var access_token: String = ""

    @SerializedName("userName")
    var userName: String = ""

    @SerializedName("responseDescription")
    var responseDescription: String = ""

    @SerializedName("userId")
    var userId: String = ""

    @SerializedName("expires_in")
    var expires_in: String = ""

    @SerializedName("userExist")
    var userExist: Boolean = false

    @SerializedName("subscriptionActive")
    var subscriptionActive: Boolean = false

    @SerializedName("tenantId")
    var tenantId: String = ""

    @SerializedName("agentLicenseNo")
    var agentLicenseNo: String = ""

    @SerializedName("cxId")
    var cxId: String = ""

    @SerializedName("bankAccountVerified")
    var bankAccountVerified: Boolean = false

    @SerializedName("amountDebited")
    var amountDebited: Boolean = false

    @SerializedName("bankAdded")
    var bankAdded: Boolean = false

    @SerializedName("setupComplete")
    var setupComplete: Boolean = false

    @SerializedName("userRoles")
    var userRoles: ArrayList<String> = ArrayList<String>()

    @SerializedName("userGroups")
    var userGroups: ArrayList<String> = ArrayList<String>()
}

/*
* {"access_token":"404",
* "userExist":false,
* "responseDescription":"The password that you've entered is incorrect",
* "userId":"6922","status":203,
* "unauthorized":"true",
* "setupComplete":false,
* "bankAccountVerified":false,
* "bankAdded":false,
* "amountDebited":false,
* "subscriptionActive":false}
* */