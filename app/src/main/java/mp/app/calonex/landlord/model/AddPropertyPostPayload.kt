package mp.app.calonex.landlord.model

import com.google.gson.JsonObject
import java.io.Serializable

class AddPropertyPostPayload: Serializable {

    // add property payload

    var userCatalogID: String?=""
    // To add Property
    var property_id: String?=""
    // To edit Property
    var propertyId: String?=""
    var address: String=""
    var address2: String=""
    var city: String? = ""
    var state: String? = ""
    var zipCode: String? = ""
    var notificationId:String?=""
    var propertyLinkRequestId:String?=""
    var buildingFeatures: String? = ""
    var parking: String? = ""
    var description: String? = ""
    var propertyImages: String? = null
    var propertyLease: String? = null
    var buildingTypeId: Long? = 0
    var buildingFeatureDTO :JsonObject ?= null
    var parkingTypeDTO  :JsonObject ?= null
    var propertyExpensesTypeDTO = JsonObject()
    var propertyExpensesDetailsDTO = ArrayList<PropertyExpenseAddPropertyPayload>()
    var brokerOrAgentLiscenceID: String? = null
    var primaryContact: String? = null
    var broker: String? = null
    var brokerPhone: String? = null
    var brokerEmailID: String? = null
    var primaryContactId: String? = null
    var numberOfUnits: String? = null
    var linkedByBrokerAgent: Boolean? = false
    var linkedByBrokerAgentOnAdd: Boolean? = false




}