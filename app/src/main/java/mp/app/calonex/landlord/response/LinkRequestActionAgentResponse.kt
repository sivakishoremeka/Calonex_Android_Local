package mp.app.calonex.landlord.response

import mp.app.calonex.landlord.model.ResponseDto

data class LinkRequestActionAgentResponse
    (
    var data: String?,
    var responseCode: Int?,
    var exceptionCode: Int?,
    var responseDescription: String?,
    var responseDto: ResponseDto?


    /*{"responseDto":{"responseCode":200,"responseDescription":"Action has been taken","exceptionCode":0},
    "data":"The Action has been taken"}*/
)
