package mp.app.calonex.landlord.response

import com.google.gson.JsonObject

data class AddPropertyPostResponse
    (var responseCode: Int?, var exceptionCode: Int?, var data: JsonObject?)