package mp.app.calonex.agent.model

data class FinalAgentLinkProperty (
    var landlordName: String="",
    var landlordEmailId: String="",
    var landlordPhoneNumber: String="",
    var propAddress1: String="",
    var city: String="",
    var state: String="",
    var zipCode: String="",
    var requestedById: String=""
)

/*
{
  "landlordName": "cxlandlordA",
  "landlordEmailId": "cx.xxx5@yopmail.com",
  "landlordPhoneNumber": "5555555555",
  "propAddress1": "address_full_match",
  "city": "Anchorage",
  "state": "Alaska",
  "zipCode": "99501",
  "requestedById": "2851"
}
 */