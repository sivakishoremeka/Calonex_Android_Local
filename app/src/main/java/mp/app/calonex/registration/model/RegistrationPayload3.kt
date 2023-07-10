package mp.app.calonex.registration.model


import com.google.gson.JsonObject

class RegistrationPayload3{

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
    var isAutoPay:Int?=0
    var stripeCustomerId: String? = ""
    var skipBank: Boolean? = false
    var signupThroughInvite: Boolean? = false
    var userId: String? = ""
    var autoPay: String? = ""
    var cardNumber:String?=""
    var firstName:String?=""
    var lastName:String?=""
    var ssn_last_4:String?=""
    var ssn_token:String?=""
    var emailId:String?=""

}

/*
{
  "businessName": "test",
  "address1": "address_full_match",
  "address2": "",
  "city": "Anchorage",
  "state": "Alaska",
  "zipCode": "99501",
  "phoneNumber": "9876543210",
  "alternatePhoneNumber": "",
  "dob": "2004-01-05T18:30:00.000Z",
  "bankUserName": "U2FsdGVkX1+hFeUKj9HCTwNAKB7b2uXLXyCAVO8/xMY=",
  "bankAccountNumber": "U2FsdGVkX1/Bo+xJc1373LymGPTl9qWlRWDDTaTPwCo=",
  "routingNumber": "U2FsdGVkX1/4AbXJ336iffAfa609Lsf3tR/v+rrX9kw=",
  "userPropertyRegisterDto": {
    "isPrimaryAddress": true,
    "address1": "address_full_match",
    "city": "Anchorage",
    "state": "Alaska",
    "zipCode": "99501",
    "phoneNumber": "9876543210",
    "brokerId": "",
    "agentId": "",
    "autoLinkBroker": false,
    "numberOfUnits": 1
  },
  "userRoleName": "CX-Landlord",
  "subscriptionDetailsId": 8056,
  "stripeCustomerId": "cus_L1vqGqStB4r1iA",
  "skipBank": false,
  "userId": "2959"
}
 */