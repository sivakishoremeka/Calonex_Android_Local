package mp.app.calonex.agentRegistration


class RegistrationPayloadAgentModel{

    var address1: String? = ""
    var address2: String? = ""
    var city: String? = ""
    var state: String? = ""
    var zipCode: String? = ""
    var alternatePhoneNumber: String? = ""
    var agentLicenseNumber: String? = ""
    var dob: String? = ""
    var brokerLicenseName: String? = ""
    var brokerLicenseNumber: String? = ""
    var userRoleName: String? = ""
    var userId: String? = ""

    //var phoneNumber : String? = ""
}

/*
{
  "address1": "address_full_match",
  "address2": "",
  "city": "Anchorage",
  "state": "Alaska",
  "zipCode": "99501",
  "alternatePhoneNumber": "",
  "agentLicenseNumber": "98765432100",
  "dob": "2004-03-31T18:30:00.000Z",
  "brokerLicenseName": "cxbroker",
  "brokerLicenseNumber": "98765432111",
  "userRoleName": "CX-Agent",
  "userId": "3164"
}
 */