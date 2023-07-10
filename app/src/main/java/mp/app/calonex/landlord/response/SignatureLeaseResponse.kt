package mp.app.calonex.landlord.response


import mp.app.calonex.landlord.model.LeaseSignature
import mp.app.calonex.landlord.model.ResponseDataDto

data class SignatureLeaseResponse
    (var responseDto: ResponseDataDto?, var data: ArrayList<LeaseSignature>?)