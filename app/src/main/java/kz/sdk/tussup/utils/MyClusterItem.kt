package kz.sdk.tussup.utils

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import kz.sdk.tussup.models.Place

data class MyClusterItem(
    val latLng: LatLng,
    val place: Place // Add the Place object
) : ClusterItem {
    override fun getPosition(): LatLng = latLng

    override fun getTitle(): String? {
        return place.name // Use the Place name or any other relevant property
    }

    override fun getSnippet(): String? {
        return "Additional Info" // Provide the snippet value here
    }
}