package mp.app.calonex.landlord.adapter

public interface FeatureCheckboxCallback {

    fun sendState(
        state: Boolean,
        buildingFeatureId: Long,
        buildingFeature: String
    ) {

    }
}