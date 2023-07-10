package mp.app.calonex.agent.model

data class AgentBookKeepingAddResponce(
    val responseCode: String? = "",
    val msg: String? = "",
    val id: String? = "",
    val responseDto: ResponseDto? = null,
    val data: BookKeepingAddResponseData? = null

)

/*

{
  "responseDto": {
    "responseCode": 200,
    "exceptionCode": 0,
    "message": "Added moneybook item successfully"
  },
  "data": {
    "id": 9414,
    "userId": 5670,
    "type": "earnings",
    "amount": 350.0,
    "date": 1672704000000,
    "category": 7172,
    "description": "Earn money from rent ",
    "createdOn": 1674661341205,
    "updatedOn": 1674661341205
  }
}


{"responseCode":"200","msg":"Category saved successfully"}
 */