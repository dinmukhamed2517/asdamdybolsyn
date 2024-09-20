package kz.sdk.tussup.utils


import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import coil.load
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import kz.sdk.tussup.R

class RestaurantClusterRenderer(
    context: Context,
    map: GoogleMap,
    clusterManager: ClusterManager<MyClusterItem>
) : DefaultClusterRenderer<MyClusterItem>(context, map, clusterManager) {

    private val inflater = LayoutInflater.from(context)

    override fun onBeforeClusterItemRendered(item: MyClusterItem, markerOptions: MarkerOptions) {
        val markerView = inflater.inflate(R.layout.custom_marker, null)
        val markerImageView: ImageView = markerView.findViewById(R.id.marker_image)

        markerImageView.load(item.place.img) {
            placeholder(R.drawable.placeholder)
            target { drawable ->
                markerImageView.setImageDrawable(drawable)
                markerOptions.icon(getBitmapFromView(markerView))
            }
        }
    }

    override fun shouldRenderAsCluster(cluster: Cluster<MyClusterItem>): Boolean {
        return cluster.size > 1
    }

    private fun getBitmapFromView(view: View): BitmapDescriptor {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}
