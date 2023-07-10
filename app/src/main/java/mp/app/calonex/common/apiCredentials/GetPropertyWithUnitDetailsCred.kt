package mp.app.calonex.common.apiCredentials

import com.google.gson.annotations.Expose

class GetPropertyWithUnitDetailsCred {
    @Expose
    var propertyId: Long? = 0
    @Expose
    var propertyUnitId: Long? = 0
}