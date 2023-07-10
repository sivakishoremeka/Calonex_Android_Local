package mp.app.calonex.landlord.model

import com.google.gson.JsonObject
import java.io.Serializable

class AddUnitCredentials: Serializable {

    var propertyId: String?=""
    var propertyUnitDetails = JsonObject()
    var numberOfUnits:String?=""
    var id:Long?=0


}