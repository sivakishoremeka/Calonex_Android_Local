package mp.app.calonex.agent.model


import com.google.gson.JsonObject

class RegistrationPayloadNew{

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
    var confirmPassword : String? = ""
    var drivingLicenseNumber: String? = ""
    var emailId : String? = ""
    var firstName: String? = ""
    var lastName: String? = ""
    var middleName : String? = ""
    var password: String? = ""
    var stateIssued: String? = ""
    var ssn: String? = ""
    var isSignupThroughInvite:Boolean?=false


    var deviceId : String? = ""
    //var landlordEmail: String? = ""
    //var landlordName: String? = ""
    //var landlordPhoneNum: String? = ""
    var userId: String? = ""
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

/*
{
  "emailId": "testuser11@yopmail.com",
  "password": "U2FsdGVkX1/X+JpkAea9O33o9IwdEJ6kB/NI1H5KgRE=",
  "confirmPassword": "Test@123",
  "firstName": "First",
  "middleName": "User",
  "lastName": "Name",
  "businessName": "MyBusiness",
  "address1": "address_fill_match",
  "address2": "",
  "city": "Anchorage",
  "state": "Alaska",
  "zipCode": "99501",
  "phoneNumber": "9999999999",
  "alternatePhoneNumber": "",
  "drivingLicenseNumber": "999999999",
  "stateIssued": "Alaska",
  "ssn": "999999999",
  "dob": "2004-03-04T18:30:00.000Z",
  "skipBank": false,
  "bankUserName": "U2FsdGVkX19IPuK7M99K34Zaj5J00lktDe187Dju5ok=",
  "bankAccountNumber": "U2FsdGVkX1+zmovSFFdDT3RUxa1LK4vHZI3b8J2pMGQ=",
  "routingNumber": "U2FsdGVkX18FS8p6vjqZtlXE5R0mt08XE36HvkIszSI=",
  "userPropertyRegisterDto": {
    "isPrimaryAddress": true,
    "address1": "address_fill_match",
    "city": "Anchorage",
    "state": "Alaska",
    "zipCode": "99501",
    "phoneNumber": "9999999999",
    "brokerId": "",
    "agentId": "79787946464",
    "autoLinkBroker": true,
    "numberOfUnits": 1
  },
  "userRoleName": "CX-Landlord",
  "subscriptionDetailsId": 8502,
  "stripeCustomerId": "cus_LGMrZz3vS9d6Bj",
  "isSignupThroughInvite": true
}
 */