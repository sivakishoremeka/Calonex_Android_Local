package mp.app.calonex.landlord.model

import java.io.Serializable

class EditPropertyAddBroker: Serializable {
    var linkedByBrokerAgentOnAdd : Boolean? = false
    var propertyId : String? = null
    var brokerOrAgentLiscenceID: String? = null
    var primaryContact: String? = null
    var broker: String? = null
    var brokerPhone: String? = null
    var brokerEmailID: String? = null
    var primaryContactId: String? = null
    var linkedByBrokerAgent: Boolean? = false
}