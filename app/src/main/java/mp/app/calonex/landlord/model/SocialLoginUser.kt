package mp.app.calonex.landlord.model

import java.util.*

//import androidx.room.Entity
//import androidx.room.PrimaryKey
//import androidx.room.TypeConverter
//import com.google.gson.Gson

//@Entity(tableName = "user")
data class SocialLoginUser(
//    @PrimaryKey
    val token_type: String? = null,
    val refresh_token: String? = null,
    val access_token: String? = null,
    val userName: String? = null,
    val responseDescription: String? = null,
    val userId: String? = null,
    val expires_in: String? = null,
    val userExist: Boolean,
    val subscriptionActive: Boolean,
    val tenantId: String? = null,
    val cxId: String? = null,
    val bankAccountVerified: Boolean = false,
    val amountDebited: Boolean = false,
    val bankAdded: Boolean = false,

    val setupComplete: Boolean = false,

    val authProvider: String?=null,
    val emailID: String?=null,
    val licenseNo: String?=null,


    var userRoles: ArrayList<String> = ArrayList<String>(),
    var userGroups: ArrayList<String> = ArrayList<String>(),
    var headers: headers
)


data class headers(
    var idMyIt:String=""
)
