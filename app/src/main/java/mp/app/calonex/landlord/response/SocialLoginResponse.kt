package mp.app.calonex.landlord.response

import mp.app.calonex.landlord.model.SocialLoginUser

data class SocialLoginResponse
    (var statusCodeValue: Int?, var statusCode: String?, var body: SocialLoginUser?)