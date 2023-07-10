package mp.app.calonex.landlord.response

import com.google.gson.JsonObject

data class CreditCardResponse
    (var responseCode: Int?, var exceptionCode: Int?, var data: JsonObject?)
