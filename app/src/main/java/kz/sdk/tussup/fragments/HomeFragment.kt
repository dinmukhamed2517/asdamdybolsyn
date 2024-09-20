package kz.sdk.tussup.fragments

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.clustering.ClusterManager
import dagger.hilt.android.AndroidEntryPoint
import kz.sdk.tussup.MainActivity
import kz.sdk.tussup.R
import kz.sdk.tussup.base.BaseFragment
import kz.sdk.tussup.databinding.FragmentHomeBinding
import kz.sdk.tussup.models.Place
import kz.sdk.tussup.utils.MyClusterItem
import kz.sdk.tussup.utils.RestaurantClusterRenderer
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var clusterManager: ClusterManager<MyClusterItem>

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onBindView() {
        super.onBindView()

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        setupZoomButtons()

        binding.listBtn.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_placeListFragment)
        }
    }

    override fun onStart() {
        super.onStart()
        checkIfUserHasRestaurant()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        if (!isAdded) return

        map = googleMap

        clusterManager = ClusterManager(requireContext(), map)
        clusterManager.renderer = RestaurantClusterRenderer(requireContext(), map, clusterManager)

        map.setOnCameraIdleListener(clusterManager)
        map.setOnMarkerClickListener(clusterManager)


        clusterManager.setOnClusterItemClickListener { item ->
            val place = item.place
            place?.let {
//                Toast.makeText(requireContext(), "Marker clicked", Toast.LENGTH_SHORT).show()
                showBottomSheet(it)
            }
            true
        }

        clusterManager.setOnClusterClickListener { cluster ->
            Toast.makeText(requireContext(), "Cluster clicked with ${cluster.size} items", Toast.LENGTH_SHORT).show()
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(cluster.position, map.cameraPosition.zoom + 1))
            true  // Return true to indicate the click has been handled
        }

        loadPlacesFromFirestore()

        val defaultLocation = LatLng(43.230035174516246, 76.89930070134874)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f))
    }

    private fun loadPlacesFromFirestore() {
        firestore.collection("restaurants")
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (document in documents) {
                        val place = document.toObject(Place::class.java)
                        place?.let {
                            val latLng = LatLng(it.latitude ?: 0.0, it.longitude ?: 0.0)
                            clusterManager.addItem(MyClusterItem(latLng, it))  // Add to Cluster Manager
                        }
                    }
                    clusterManager.cluster()  // Cluster the items
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error loading restaurants: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showBottomSheet(location: Place) {
        if (location.id.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "${location.id}", Toast.LENGTH_SHORT).show()
            return
        }

        val bottomSheetFragment = MapBottomSheetFragment().apply {
            arguments = Bundle().apply {
                putParcelable("location", location)
            }
        }
        bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
    }

    private fun setupZoomButtons() {
        val zoomInButton = binding.zoomIn
        val zoomOutButton = binding.zoomOut

        zoomInButton.setOnClickListener {
            if (::map.isInitialized) map.animateCamera(CameraUpdateFactory.zoomIn())
        }

        zoomOutButton.setOnClickListener {
            if (::map.isInitialized) map.animateCamera(CameraUpdateFactory.zoomOut())
        }
    }

    private fun checkIfUserHasRestaurant() {
        val currentUser = firebaseAuth.currentUser
        currentUser?.let {
            firestore.collection("restaurants")
                .whereEqualTo("userId", currentUser.uid)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        Toast.makeText(requireContext(), "No restaurant found for user", Toast.LENGTH_SHORT).show()
                    } else {
                        switchToBusinessMode()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), "Error checking restaurant: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } ?: run {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun switchToBusinessMode() {
        (activity as? MainActivity)?.let { mainActivity ->
            mainActivity.setBusinessMode(true)
        }
    }
}

