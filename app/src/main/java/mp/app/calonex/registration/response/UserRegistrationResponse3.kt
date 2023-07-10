package mp.app.calonex.registration.response


data class UserRegistrationResponse3
    (val responseCode: Int,
     val responseDescription: String,
     val exceptionCode: Long,
     val propertyId: Long,
     val message: String,
     val status: Int,
     val exceptionDescription: String)
