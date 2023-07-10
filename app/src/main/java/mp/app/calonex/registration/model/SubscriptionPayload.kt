package mp.app.calonex.registration.model

class SubscriptionPayload {
    var finalPrice: String? = ""
    var numberOfUnits: String? = ""
    var subscriptionPlanDuration: String? = ""

    var isCreditCard: Boolean = false
    var skipBank: Boolean = false
    var isSignupThroughInvite: Boolean = false

    var bankUserName: String? = ""
    var routingNumber: String? = ""
    var bankAccountNumber: String? = ""
    var userCatalogId:String?=""

    var token: String? = ""
}

/*
https://api.calonex.com/api/root-service/api/subscription/createSubscriptionPlan

{
  "bankAccountNumber": "U2FsdGVkX18ADxhQxR8VRd6W+LmP0k+ra6J40hkFjDU\u003d",
  "bankUserName": "U2FsdGVkX1/SuiXqkVzgL8rJGJkTTgJP4d50ST2MlMc\u003d",
  "finalPrice": "79.95",
  "isCreditCard": false,
  "numberOfUnits": "20",
  "routingNumber": "U2FsdGVkX1+GswI+7wCvio2JlAo9Wzap97YwHz8xza8\u003d",
  "subscriptionPlanDuration": "1"
}


{
    "responseDto": {
        "responseCode": 201,
        "responseDescription": "SubscriptionPlan created sucessfully",
        "exceptionCode": 0
    },
    "data": {
        "subscriptionDetailsId": 8054,
        "stripeCustomerId": "cus_L1uoCsPUq7AlNn"
    }
}

--------------------------------------
{
  "finalPrice": "79.95",
  "numberOfUnits": "20",
  "subscriptionPlanDuration": "1",
  "token": "tok_1KNvR2KJ1orMwPHndazw7SB1",
  "isCreditCard": true
}

{
  "responseDto": {
    "responseCode": 201,
    "responseDescription": "SubscriptionPlan created sucessfully",
    "exceptionCode": 0
  },
  "data": {
    "subscriptionDetailsId": 8282,
    "stripeCustomerId": "cus_L43YaaKTAeT5Ub"
  }
}
 */