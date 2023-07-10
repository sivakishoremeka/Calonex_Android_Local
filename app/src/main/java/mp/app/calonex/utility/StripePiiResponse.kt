package mp.app.calonex.utility

data class StripePiiResponse(
    val client_ip: String,
    val created: Int,
    val id: String,
    val livemode: Boolean,
    val `object`: String,
    val redaction: Any,
    val type: String,
    val used: Boolean
)