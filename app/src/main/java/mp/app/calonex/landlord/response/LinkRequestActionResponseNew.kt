package mp.app.calonex.landlord.response

import mp.app.calonex.landlord.model.ResponseDto

data class LinkRequestActionResponseNew
    (
    var responseDto: ResponseDto?,
    var data: String?


    /*{"responseDto":{"responseCode":200,"responseDescription":"Action has been taken","exceptionCode":0},"data":"The Action has been taken"}*/
)
