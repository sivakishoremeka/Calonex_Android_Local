package mp.app.calonex.agent.model

data class AgentBookKeepingUser(
    var id: Int=0,
    var userId: Int=0,
    var type: String="",
    var amount: Float=0.0f,
    var date: Long=0,
    var category: String?="",
    var description: String?="",
    var updatable: Boolean=false
    )