package mp.app.calonex.landlord.response

import mp.app.calonex.landlord.model.ResponseDto
import mp.app.calonex.tenant.model.UploadFileData

data class UploadFileMultipartResponse
    (var responseDto: ResponseDto?, var data: UploadFileData?)