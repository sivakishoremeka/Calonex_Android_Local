package mp.app.calonex.landlord.model

 class CxMessageListData{
    var messageId: Long = 0
    var topic: String = ""
    var priority: String = ""
    var recentMsg: String = ""
    var timestamp: Long = 0
    var isClosed: String = ""
    var address: String = ""
    var unitNumber: String = ""
    var unitId: String = ""
    var propertyId: String = ""
    var users= ArrayList<CxMessageUsers>()

}