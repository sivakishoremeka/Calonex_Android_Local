package mp.app.calonex.landlord.model

data class OccupancyDetail(
    val vacant: String = "",
    val inactive: String = "",
    val occupiedOutsideCx: String = "",
    val occupiedInsideCx: String = ""
)