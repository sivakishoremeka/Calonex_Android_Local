package mp.app.calonex.registration.model

class RegistrationPropertyPayload {
    var address1: String? = ""
    var address2: String? = ""
    var city: String? = ""
    var state: String? = ""
    var zipCode: String? = ""
    var phoneNumber: String? = ""
    var brokerId: String? = ""
    var numberOfUnits: String? = ""
    var isPrimaryAddress: Boolean? = false
    var agentId: String? = ""
    var autoLinkBroker: Boolean? = false
}

/*
"agentId", "state", "autoLinkBroker", "brokerId", "phoneNumber", "city", "address1", "address2", "numberOfUnits", "primaryAddress", "zipCode"

{
  "address1": "fghj",
  "address2": "",
  "city": "Anchorage",
  "state": "Alaska",
  "zipCode": "99501",
  "phoneNumber": "9745225889",
  "brokerId": "",
  "numberOfUnits": "1",
  "isPrimaryAddress": true,
  "agentId": "",
  "autoLinkBroker": false,
}


Unrecognized field "isPrimaryAddress" (class mp.app.calonex.registration.model.RegistrationPropertyPayload), not marked as ignorable (11 known properties: "agentId", "state", "autoLinkBroker", "brokerId", "phoneNumber", "city", "address1", "address2", "numberOfUnits", "primaryAddress", "zipCode"])
        at [Source: {"address1":"fghj","address2":"","agentId":"","autoLinkBroker":false,"brokerId":"","city":"Anchorage","isPrimaryAddress":true,"numberOfUnits":"1","phoneNumber":"9745225889","state":"Alaska","zipCode":"99501"}; line: 1, column: 126] (through reference chain: mp.app.calonex.registration.model.RegistrationPropertyPayload["isPrimaryAddress"])
 */