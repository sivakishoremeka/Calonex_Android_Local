package mp.app.calonex.agent.model

data class BookkeepingUpdateAgent(
    var date: String?="",
    var amount: Float = 0.0f,
    var type: String?="",
    var description: String?="",
    var category: Int? = 0,
    var userId: Int? = 0,
    var id: Int? = 0
    )

/*
{"date":"2022-03-12T00:00:00.000Z","amount":336,"type":"earnings","description":"Test","category":7172,"userId":2851,"id":7241}
 */