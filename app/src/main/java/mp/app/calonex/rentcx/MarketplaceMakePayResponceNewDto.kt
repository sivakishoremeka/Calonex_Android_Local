package mp.app.calonex.rentcx

import com.google.gson.annotations.SerializedName


data class MarketplaceMakePayResponceNewDto(
    //Created on 17th June 2023
    //@SerializedName("message" ) var message : String? = null,
    @SerializedName("responseDto") var responseDto: ResponseDto? = ResponseDto()
)

/*
*
* {"responseDto":{"responseCode":201,"responseDescription":"Invoice Payment done successfully.","exceptionCode":0}}
*
* */

