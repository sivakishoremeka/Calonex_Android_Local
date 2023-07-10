package mp.app.calonex.agent.model

data class AgentPaymentHistory(
    var transactionDate: String="",
    var propertyAddress: String="",
    var propertyId: String="",
    var unitNumber: String="",
    var unitId: String="",
    var amount: String="",
    var status: String="",
    var paymentDate: String="",
    var commission: String="",
    var brokerName: String="",
    var paymentDateTimeStamp: Int=0
    )

/*
 "transactionDate": "1637588394000",
      "propertyAddress": "address_full_match address_full_match Wisconsin 54465",
      "propertyId": "1",
      "unitNumber": "1",
      "unitId": "7147",
      "amount": "2.40",
      "status": "Pending",
      "paymentDate": "1637588394000",
      "commission": "30",
      "brokerName": "Calonex Test.Broker",
      "paymentDateTimeStamp": 0
 */