package mp.app.calonex.agent.model

data class BookkeepingAddAgent(
    var date: String?="",
    var amount: Float = 0.0f,
    var type: String?="",
    var description: String?="",
    var category: Int? = 0,
    var userId: String? = ""
    )

/*
{"date":"2022-03-15T13:37:53.344Z","amount":123,"type":"earnings","description":"Test new","category":7172}
 */