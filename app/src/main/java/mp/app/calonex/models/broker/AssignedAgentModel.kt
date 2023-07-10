package mp.app.calonex.models.broker


import com.google.gson.annotations.SerializedName

data class AssignedAgentModel(
    @SerializedName("accountType")
    val accountType: Any,
    @SerializedName("address1")
    val address1: String,
    @SerializedName("address2")
    val address2: String,
    @SerializedName("agentLicenseNO")
    val agentLicenseNO: String,
    @SerializedName("alternatePhoneNumber")
    val alternatePhoneNumber: String,
    @SerializedName("approvedById")
    val approvedById: Any,
    @SerializedName("approvedByName")
    val approvedByName: Any,
    @SerializedName("authProvider")
    val authProvider: String,
    @SerializedName("bankAccountNO")
    val bankAccountNO: Any,
    @SerializedName("bankPhoneNO")
    val bankPhoneNO: Any,
    @SerializedName("bankUserName")
    val bankUserName: Any,
    @SerializedName("brokerAgentLinkRequestId")
    val brokerAgentLinkRequestId: Any,
    @SerializedName("brokerId")
    val brokerId: Any,
    @SerializedName("brokerLicenseNO")
    val brokerLicenseNO: String,
    @SerializedName("brokerName")
    val brokerName: Any,
    @SerializedName("businessAddress")
    val businessAddress: Any,
    @SerializedName("businessEmail")
    val businessEmail: Any,
    @SerializedName("businessName")
    val businessName: Any,
    @SerializedName("businessPhone")
    val businessPhone: Any,
    @SerializedName("city")
    val city: String,
    @SerializedName("commissionPercentage")
    val commissionPercentage: Int,
    @SerializedName("confirmationToken")
    val confirmationToken: String,
    @SerializedName("createdOn")
    val createdOn: Any,
    @SerializedName("cxId")
    val cxId: Any,
    @SerializedName("deleted")
    val deleted: Boolean,
    @SerializedName("deviceId")
    val deviceId: Any,
    @SerializedName("dob")
    val dob: Long,
    @SerializedName("drivingLicenseNO")
    val drivingLicenseNO: Any,
    @SerializedName("ein")
    val ein: Any,
    @SerializedName("emailId")
    val emailId: String,
    @SerializedName("enableOrDisableAutoPay")
    val enableOrDisableAutoPay: String,
    @SerializedName("enabled")
    val enabled: String,
    @SerializedName("fax")
    val fax: Any,
    @SerializedName("file")
    val `file`: Any,
    @SerializedName("finalStatus")
    val finalStatus: String,
    @SerializedName("fireBaseToken")
    val fireBaseToken: Any,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("imageName")
    val imageName: Any,
    @SerializedName("imagePath")
    val imagePath: Any,
    @SerializedName("invitedById")
    val invitedById: Any,
    @SerializedName("landlordEmail")
    val landlordEmail: Any,
    @SerializedName("landlordId")
    val landlordId: Any,
    @SerializedName("landlordName")
    val landlordName: Any,
    @SerializedName("landlordPhoneNum")
    val landlordPhoneNum: Any,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("listOfProperty")
    val listOfProperty: Any,
    @SerializedName("loginAllowed")
    val loginAllowed: Boolean,
    @SerializedName("middleName")
    val middleName: String,
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    @SerializedName("routingNO")
    val routingNO: Any,
    @SerializedName("setupComplete")
    val setupComplete: Boolean,
    @SerializedName("signupThroughInvite")
    val signupThroughInvite: Boolean,
    @SerializedName("ssn")
    val ssn: String,
    @SerializedName("state")
    val state: String,
    @SerializedName("stateIssued")
    val stateIssued: Any,
    @SerializedName("stripeAccId")
    val stripeAccId: Any,
    @SerializedName("subscriptionDetailsId")
    val subscriptionDetailsId: String,
    @SerializedName("userFullName")
    val userFullName: String,
    @SerializedName("userFullNameWithoutDot")
    val userFullNameWithoutDot: String,
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("userProperty")
    val userProperty: List<Any>,
    @SerializedName("userRoleId")
    val userRoleId: Any,
    @SerializedName("userRoleName")
    val userRoleName: String,
    @SerializedName("voidedBankCheque")
    val voidedBankCheque: Any,
    @SerializedName("zipCode")
    val zipCode: String
)