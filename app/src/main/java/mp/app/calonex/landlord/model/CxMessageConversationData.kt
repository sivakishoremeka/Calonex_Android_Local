package mp.app.calonex.landlord.model

data class CxMessageConversationData(
    val time: Long = 0,
    var toId: String = "",
    var byId: String = "",
    var message: String = "",
    val subMessage: String = ""
)