package mp.app.calonex.brokerRegistration


import com.google.gson.annotations.SerializedName

data class RegistrationPayloadBrokerModel(

    @SerializedName("address1") var address1: String? = null,
    @SerializedName("address2") var address2: String? = null,
    @SerializedName("city") var city: String? = null,
    @SerializedName("state") var state: String? = null,
    @SerializedName("zipCode") var zipCode: String? = null,
    @SerializedName("phoneNumber") var phoneNumber: String? = null,
    @SerializedName("alternatePhoneNumber") var alternatePhoneNumber: String? = null,
    @SerializedName("brokerLicenseNumber") var brokerLicenseNumber: String? = null,
    @SerializedName("dob") var dob: String? = null,
    @SerializedName("userRoleName") var userRoleName: String? = null,
    @SerializedName("userGroupList") var userGroupList: ArrayList<UserGroupList> = arrayListOf(),
    @SerializedName("userGroupPath") var userGroupPath: String? = null,
    @SerializedName("userId") var userId: String? = null,
    @SerializedName("businessName") var businessName: String? = null,
    @SerializedName("businessEmail") var businessEmail: String? = null,
    @SerializedName("businessPhone") var businessPhone: String? = null,
    @SerializedName("businessFax") var businessFax: String? = null,
    @SerializedName("businessAddress") var businessAddress: String? = null
)

data class UserGroupList(

    @SerializedName("childGroup") var childGroup: ArrayList<String> = arrayListOf(),
    @SerializedName("groupName") var groupName: String? = null

)

/*
{
  "address1": "address_full_match",
  "address2": "",
  "city": "Anchorage",
  "state": "Alaska",
  "zipCode": "99501",
  "phoneNumber": "9876543210",
  "alternatePhoneNumber": "9876543210",
  "brokerLicenseNumber": "98766465321",
  "dob": "2004-03-31T18:30:00.000Z",
  "userRoleName": "CX-Broker",
  "userGroupList": [
    {
      "childGroup": [],
      "groupName": "123456789"
    }
  ],
  "userGroupPath": "",
  "userId": "3167",
  "businessName": "demo",
  "businessEmail": "t714@yopmail.com",
  "businessPhone": "9876543210",
  "businessFax": "9876543210",
  "businessAddress": "address_full_match"
}
 */