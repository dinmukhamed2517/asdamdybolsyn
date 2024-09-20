package kz.sdk.tussup.onboarding

import android.content.Context
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kz.sdk.tussup.MainActivity
import kz.sdk.tussup.R
import kz.sdk.tussup.base.BaseFragment
import kz.sdk.tussup.databinding.FragmentSplashBinding
import javax.inject.Inject


@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>(FragmentSplashBinding::inflate) {





    @Inject
    lateinit var firebaseAuth: FirebaseAuth


    @Inject
    lateinit var firestore: FirebaseFirestore

    override var showBottomNavigation: Boolean = false

    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

    override fun onBindView() {
        super.onBindView()
        coroutineScope.launch {
            delay(4000)
            if (onBoardingFinished()) {
                findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
            } else {
                findNavController().navigate(R.id.action_splashFragment_to_viewPagerFragment)
            }
        }
    }
    override fun onStart() {
        super.onStart()
        checkIfUserHasRestaurant()
    }

    private fun checkIfUserHasRestaurant() {
        val currentUser = firebaseAuth.currentUser

        if (currentUser != null) {
            firestore.collection("restaurants")
                .whereEqualTo("userId", currentUser.uid)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        switchToBusinessMode()
                    } else {
                        Toast.makeText(requireContext(), "No restaurant found for user", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), "Error checking restaurant: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onBoardingFinished(): Boolean {
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("Finished", false)
    }

    private fun switchToBusinessMode() {
        (activity as? MainActivity)?.let { mainActivity ->
            mainActivity.setBusinessMode(true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}