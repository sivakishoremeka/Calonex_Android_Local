package mp.app.calonex.landlord.model

data class ResponseDto(
    val responseCode: Int,
    val responseDescription: String,
    val exceptionCode: Long,
    val message: String,
    val propertyUnitId: String,
    val exceptionDescription: String
    )