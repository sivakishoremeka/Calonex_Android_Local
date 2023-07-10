package mp.app.calonex.landlord.model

import java.io.Serializable

class AddUnitPayload: Serializable {

    var unitTypeId: Long? = 0
    var unitNumber: Long? = 0
    var rentPerMonth: Long? = 0
    var bathroomTypeID: Long? = 0
    var squareFootArea: Long? = 0
    var dateAvailable: String? = null

    var unitFeatureIds = ArrayList<Long>()
    var unitUtilitiesIds = ArrayList<Long>()

    var isActive: Boolean? = false

}