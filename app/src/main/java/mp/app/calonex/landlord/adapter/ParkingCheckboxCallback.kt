package mp.app.calonex.landlord.adapter

public interface ParkingCheckboxCallback {

    fun sendState(
        state: Boolean,
        parkingTypeId: Long,
        parkingTypeName: String
    ) {

    }
}