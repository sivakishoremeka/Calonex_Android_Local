package mp.app.calonex.agent.responce

import mp.app.calonex.agent.model.AgentBookKeepingAmount
import mp.app.calonex.agent.model.AgentBookKeepingUser
import mp.app.calonex.agent.model.MontlyRevenueDTO

data class AgentBookKeepingResponse
    (var responseCode: String?, var msg: String?,
     var fromSystem: ArrayList<AgentBookKeepingUser>?,
     var userAdded: ArrayList<AgentBookKeepingUser>?,
     var amount: AgentBookKeepingAmount?,
     var  montlyRevenueDTO:ArrayList<MontlyRevenueDTO>?)

/*
{
  "responseCode": "200",
  "msg": "Data Fetched Successfully",
  "fromSystem": [
    {
      "id": 6247,
      "userId": 2851,
      "type": "earnings",
      "amount": 18.65,
      "date": 1635263464000,
      "category": "Subscription Commission",
      "description": null,
      "updatable": false
    },
    {
      "id": 3316,
      "userId": 2851,
      "type": "earnings",
      "amount": 2.40,
      "date": 1637588394000,
      "category": "Rent Commission",
      "description": null,
      "updatable": false
    }
  ],
  "userAdded": [
    {
      "id": 7177,
      "userId": 2851,
      "type": "earnings",
      "amount": 10.00,
      "date": 1641340800000,
      "category": null,
      "description": "Test new",
      "updatable": true
    },
    {
      "id": 7178,
      "userId": 2851,
      "type": "earnings",
      "amount": 12.00,
      "date": 1641340800000,
      "category": null,
      "description": "Test new123",
      "updatable": true
    },
    {
      "id": 7179,
      "userId": 2851,
      "type": "expense",
      "amount": 15.00,
      "date": 1641340800000,
      "category": null,
      "description": "house rent for Jan 2020",
      "updatable": true
    },
    {
      "id": 7180,
      "userId": 2851,
      "type": "",
      "amount": null,
      "date": 1641340800000,
      "category": null,
      "description": "",
      "updatable": true
    },
    {
      "id": 7181,
      "userId": 2851,
      "type": "expense",
      "amount": 22.00,
      "date": 1641340800000,
      "category": null,
      "description": "#tour",
      "updatable": true
    }
  ],
  "amount": {
    "totalExpenses": 37.00,
    "totalEarnings": 43.05,
    "balance": 6.05
  }
}
 */