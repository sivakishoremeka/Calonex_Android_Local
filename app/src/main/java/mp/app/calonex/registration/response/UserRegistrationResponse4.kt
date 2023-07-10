package mp.app.calonex.registration.response

import mp.app.calonex.landlord.model.ResponseDto
import mp.app.calonex.registration.model.RegisterDetail

data class UserRegistrationResponse4
    (var responseDto: ResponseDto?,var data:RegisterDetail?)

/*
{
  "responseDto": {
    "responseCode": 200,
    "responseDescription": "Registration Success",
    "exceptionCode": 0
  },
  "data": {
    "propertyId": null,
    "userCatalogID": "3208"
  }
}
 */