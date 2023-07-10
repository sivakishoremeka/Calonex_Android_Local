package mp.app.calonex.landlord.model

data class ResponseDto2(
    val responseCode: Int,
    val responseDescription: String,
    val exceptionCode: Long,
    val userId: String?,

    val message: String,
    val exceptionDescription: String
    )

/*
, val message: String,
    val exceptionDescription: String
 */

/*
{
  "responseDto": {
    "responseCode": 200,
    "responseDescription": "Registration Success",
    "exceptionCode": 0
  }
}
 */