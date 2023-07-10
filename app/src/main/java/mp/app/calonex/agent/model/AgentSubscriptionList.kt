package mp.app.calonex.agent.model

data class AgentSubscriptionList(
    var landlordName: String="",
    var price: Float=0.0f,
    var currentplan: Int=0,
    var numberOfUnits: Int=0,
    var subscriptionStartDate: Long=0,
    var subscriptionEndDate: Long=0,
    var subscriptionCancel: Boolean=false,
    var agentCommission: String="",
    var brokerCommission: String="",
    var brokerId: String="",
    var agentId: String="",
    var cxAmount: String="",
    )

/*
 {
      "landlordName": "Calonex Test.Landlord",
      "price": 207.24,
      "currentplan": 3,
      "numberOfUnits": 49,
      "subscriptionStartDate": 1635263464000,
      "subscriptionEndDate": 1729957864000,
      "subscriptionCancel": false,
      "agentCommission": "18.65",
      "brokerCommission": "62.17",
      "brokerId": "2",
      "agentId": "2851",
      "cxAmount": "207.24"
    }
 */