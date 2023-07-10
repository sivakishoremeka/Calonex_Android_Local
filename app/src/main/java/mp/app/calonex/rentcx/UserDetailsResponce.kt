package mp.app.calonex.rentcx

import com.google.gson.annotations.SerializedName


data class UserDetailsResponce(
    @SerializedName("responseDto") var responseDto: ResponseDto? = ResponseDto(),
    @SerializedName("data") var data: Data2? = Data2()
)

data class ResponseDto(
    @SerializedName("responseCode") var responseCode: Int? = null,
    @SerializedName("responseDescription") var responseDescription: String? = null,
    @SerializedName("exceptionCode") var exceptionCode: Int? = null
)

data class Data2(
    @SerializedName("userId") var userId: String? = null,
    @SerializedName("firstName") var firstName: String? = null,
    @SerializedName("lastName") var lastName: String? = null,
    @SerializedName("emailId") var emailId: String? = null,
    @SerializedName("phoneNumber") var phoneNumber: String? = null,
    @SerializedName("userDriverLicenseNumber") var userDriverLicenseNumber: String? = null,
    @SerializedName("userLicenseNumber") var userLicenseNumber: String? = null,
    @SerializedName("agentLicenseNumber") var agentLicenseNumber: String? = null,
    @SerializedName("currentPassword") var currentPassword: String? = null,
    @SerializedName("confirmPassword") var confirmPassword: String? = null,
    @SerializedName("newPassword") var newPassword: String? = null,
    @SerializedName("imagePath") var imagePath: String? = null,
    @SerializedName("brokerName") var brokerName: String? = null,
    @SerializedName("brokerId") var brokerId: Long? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("statusCode") var statusCode: Int? = null,
    @SerializedName("userRoleName") var userRoleName: String? = null,
    @SerializedName("bankUserName") var bankUserName: String? = null,
    @SerializedName("bankAccountNo") var bankAccountNo: String? = "",
    @SerializedName("bankAdded") var bankAdded: Boolean? = false,
    @SerializedName("stripAccountVerified") var stripAccountVerified: Boolean? = false,
    @SerializedName("routingNo") var routingNo: String? = null
)