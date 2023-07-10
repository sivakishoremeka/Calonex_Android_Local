package mp.app.calonex.landlord.model

import java.io.Serializable

class NotificationLinkRequestModelNew : Serializable {

    val deleted: Boolean = false
    val createdOn: Long = 0
    val propertyLinkRequestId: Long = 0
    var propertyId: String = ""
    val landlordFullName: String = ""
    val landlordEmaild: String = ""
    val landlordPhoneNumber: String = ""
    val propAddress: String = ""
    val city: String = ""
    val state: String = ""
    val zipCode: String = ""
    val requestedBy: String = ""
    val brokerId: String = ""
    val agentId: String = ""
    val linkRequestStatus: String = ""
    val userId: Long = 0
    val id: Long = 0
    var agentEmailId: String=""
    var location: String=""
    var agentName: String=""
    var agentLicenseNo: String=""
    var commissionPercentage: String=""
    var landlordName: String=""
    var units: String=""
    var agentRequest: Boolean=false;

}