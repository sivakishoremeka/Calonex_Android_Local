package mp.app.calonex.registration.model


class RegistrationPayload2{
    var firstName: String? = ""
    var lastName: String? = ""
    var emailId : String? = ""
    var password: String? = ""
    var confirmPassword : String? = ""
    var drivingLicenseNumber: String? = ""
    var stateIssued: String? = ""
    var ssn: String? = ""
    var userRoleName: String? = ""

    var agentLicenseNumber: String? = ""
    var brokerLicenseNumber: String? = ""
}

/*
{
  "firstName": "test",
  "lastName": "user",
  "emailId": "test501@yopmail.com",
  "password": "U2FsdGVkX186oKiW8aR4qZXRIyD1Dro0jtBEZcwcJ+o=",
  "confirmPassword": "U2FsdGVkX186oKiW8aR4qZXRIyD1Dro0jtBEZcwcJ+o=",
  "drivingLicenseNumber": "321654987",
  "stateIssued": "Alaska",
  "ssn": "987654321",
  "userRoleName": "CX-Landlord"
}
 */