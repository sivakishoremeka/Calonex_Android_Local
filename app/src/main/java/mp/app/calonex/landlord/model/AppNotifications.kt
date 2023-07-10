package mp.app.calonex.landlord.model

data class AppNotifications(
    val notificationId: Long = 0,
    val deleted: Boolean = false,
    val toUserId: Long = 0,
    val fromUserId: Long = 0,
    val activityType: String = "",
    val toEmailId: String = "",
    val fromEmailId: String = "",
    val primaryContactName: String = "",
    val licenseNO:String = "",
    val notificationData: String = "",
    val createdDate: String = "",
    val address: String = "",
    val landlordName: String = "",
    val id: Long = 0,
    val agentId:String="",
    var agentLicenseNo:String="",
    var agentName: String="",
    var agentEmailId: String="",
    var agentRequest: String?=""


)