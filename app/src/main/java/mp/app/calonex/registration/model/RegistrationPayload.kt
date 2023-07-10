package mp.app.calonex.registration.model


import com.google.gson.JsonObject

class RegistrationPayload{

    var businessName: String? = ""
    var address1: String? = ""
    var address2: String? = ""
    var city: String? = ""
    var state: String? = ""
    var zipCode: String? = ""
    var phoneNumber : String? = ""
    var alternatePhoneNumber: String? = ""
    var dob: String? = ""
    var bankUserName: String? = ""
    var bankAccountNumber: String? = ""
    var routingNumber: String? = ""
    var userPropertyRegisterDto = JsonObject()
    var userRoleName: String? = ""
    var subscriptionDetailsId:Int?=0
    var stripeCustomerId: String? = ""
    var skipBank: Boolean? = false
    var userId: String? = ""

    var confirmPassword : String? = ""
    var drivingLicenseNumber: String? = ""
    var deviceId : String? = ""
    var emailId : String? = ""
    var firstName: String? = ""
    var lastName: String? = ""
    var middleName : String? = ""
    var password: String? = ""
    var stateIssued: String? = ""
    var isSignupThroughInvite:Boolean?=false
    var landlordEmail: String? = ""
    var landlordName: String? = ""
    var landlordPhoneNum: String? = ""
}

/*
{
  "address1": "address_full_match",
  "address2": "",
  "alternatePhoneNumber": "",
  "bankAccountNumber": "U2FsdGVkX1+Ij16mfQjsx0T45OeRLRwbf7G3DEQvfOI\u003d",
  "bankUserName": "U2FsdGVkX18UMlfmbZQstAfzkKCFMhzaqidHsn5jsI0\u003d",
  "businessName": "test",
  "city": "Anchorage",
  "confirmPassword": "",
  "deviceId": "884b0972c86349a6",
  "dob": "2004-01-06",
  "drivingLicenseNumber": "",
  "emailId": "",
  "firstName": "",
  "isSignupThroughInvite": false,
  "landlordEmail": "",
  "landlordName": "",
  "landlordPhoneNum": "",
  "lastName": "",
  "middleName": "",
  "password": "",
  "phoneNumber": "9876543210",
  "routingNumber": "U2FsdGVkX1/6l3EE4J5jfrJH2+IZDIp16+beRY/yvuI\u003d",
  "skipBank": false,
  "state": "Alaska",
  "stateIssued": "",
  "stripeCustomerId": "",
  "subscriptionDetailsId": 8055,
  "userId": "",
  "userPropertyRegisterDto": {
    "address1": "address_full_match",
    "address2": "",
    "agentId": "",
    "autoLinkBroker": false,
    "brokerId": "",
    "city": "Anchorage",
    "isPrimaryAddress": true,
    "numberOfUnits": "1",
    "phoneNumber": "9876543210",
    "state": "Alaska",
    "zipCode": "99501"
  },
  "userRoleName": "CX-Landlord",
  "zipCode": "99501"
}
 */