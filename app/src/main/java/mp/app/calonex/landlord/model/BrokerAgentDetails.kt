package mp.app.calonex.landlord.model

data class BrokerAgentDetails(
    val licenceId: String,
    val brokerName: String,
    val agentName: String,
    val agentPhone: String,
    val agentEmailID: String,
    val brokerPhone: String,
    val brokerEmailID: String,
    val primaryContact: String,
    val primaryContactId: String,
    val primaryContactType: String,
    val brokerId: String,
    val agentId: String
    )