package kz.sdk.tussup.fragments

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kz.sdk.tussup.MainActivity
import kz.sdk.tussup.R
import kz.sdk.tussup.base.BaseFragment
import kz.sdk.tussup.databinding.FragmentRestaurantProfileBinding
import javax.inject.Inject

@AndroidEntryPoint

class RestaurantProfileFragment:BaseFragment<FragmentRestaurantProfileBinding>(FragmentRestaurantProfileBinding::inflate) {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    override fun onBindView() {
        super.onBindView()

        binding.logoutButton.setOnClickListener {
            signOut()
        }
    }

    private fun signOut() {
        var alertDialog: AlertDialog? = null
        alertDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Sign out")
            .setMessage("Are you sure you want to sign out?")
            .setPositiveButton("Yes") { _, _ ->
                firebaseAuth.signOut()
                clearPlaceIdFromPreferences()
                switchToUserMode()
                alertDialog?.dismiss()
//                findNavController().navigate(
//                    R.id.action_restaurantProfileFragment_to_loginFragment2
//                )
            }
            .setNegativeButton("Cancel") { _, _ ->
                alertDialog?.dismiss()
            }
            .show()
    }
    private fun switchToUserMode() {
        (activity as? MainActivity)?.let { mainActivity ->
            mainActivity.setBusinessMode(false)
        }
    }

    private fun clearPlaceIdFromPreferences() {
        val sharedPreferences = requireContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("placeId")
        editor.apply()
    }

}