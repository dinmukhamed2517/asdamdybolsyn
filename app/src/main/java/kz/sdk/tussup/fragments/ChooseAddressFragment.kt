package kz.sdk.tussup.fragments

import android.os.Bundle
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kz.sdk.tussup.MainActivity
import kz.sdk.tussup.R
import kz.sdk.tussup.base.BaseFragment
import kz.sdk.tussup.databinding.FragmentChooseAddressBinding
import kz.sdk.tussup.firebase.UserDao
import javax.inject.Inject


@AndroidEntryPoint
class ChooseAddressFragment:BaseFragment<FragmentChooseAddressBinding>(FragmentChooseAddressBinding::inflate),
    OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private var selectedLatLng: LatLng? = null
    override var showBottomNavigation = false

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var userDao: UserDao
    override fun onBindView() {
        super.onBindView()
        setupZoomButtons()

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.confirmBtn.setOnClickListener {
            selectedLatLng?.let { latLng ->
                saveSelectedLocation(latLng.latitude, latLng.longitude)
            } ?: run {
                Toast.makeText(context, "Please select a location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Set up long-press listener to get the selected address
        map.setOnMapLongClickListener { latLng ->
            // Clear previous markers
            map.clear()

            // Add a marker to the selected location
            map.addMarker(MarkerOptions().position(latLng).title("Selected Location"))

            // Save the selected LatLng
            selectedLatLng = latLng
        }

        // Optionally set the default camera position
        val defaultLocation = LatLng(43.230035174516246, 76.89930070134874)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10f))
    }
    private fun setupZoomButtons() {
        val zoomInButton = binding.zoomIn
        val zoomOutButton = binding.zoomOut

        zoomInButton.setOnClickListener {
            if (::map.isInitialized) {
                map.animateCamera(CameraUpdateFactory.zoomIn())
            }
        }

        zoomOutButton.setOnClickListener {
            if (::map.isInitialized) {
                map.animateCamera(CameraUpdateFactory.zoomOut())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userDao.getDataLiveData.observe(this){
            if(it?.place == null){
                switchToBusinessMode()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        userDao.getDataLiveData.observe(this){
            if(it?.place == null){
                switchToBusinessMode()
            }
        }
    }
    private fun switchToBusinessMode() {
        (activity as? MainActivity)?.let { mainActivity ->
            mainActivity.setBusinessMode(true)
        }
    }


    private fun saveSelectedLocation(latitude: Double, longitude: Double) {
        val action = ChooseAddressFragmentDirections.actionChooseAddressFragmentToRegisterCompanyFragment(latitude.toFloat(), longitude.toFloat())
        findNavController().navigate(action)
    }
}