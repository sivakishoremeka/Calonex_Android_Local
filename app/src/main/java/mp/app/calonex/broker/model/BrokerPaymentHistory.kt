package mp.app.calonex.broker.model

data class BrokerPaymentHistory(
    var propertyAddress: String="",
    var unitNumber: String="",
    var amount: String="",
    var paymentDate: String="",
    var agentName: String="",
    var landlordName: String="",
    var status: String="",
    var paymentCategory: String=""
    )

/*
{
      "propertyAddress": "new_test_address_full_match address_full_match Wisconsin 54465",
      "unitNumber": "1",
      "amount": "2.40",
      "paymentDate": "1637539200000",
      "agentName": "Calonex Test.Agent",
      "status": "Pending"
    }
 */