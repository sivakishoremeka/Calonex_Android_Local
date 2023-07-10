package mp.app.calonex.landlord.response

import mp.app.calonex.landlord.model.FetchDocumentModel
import mp.app.calonex.landlord.model.ResponseDto

data class FetchDocumentResponse
    (var responseDto: ResponseDto?, var data: ArrayList<FetchDocumentModel>?)