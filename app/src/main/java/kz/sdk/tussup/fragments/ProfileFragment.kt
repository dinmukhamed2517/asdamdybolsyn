package kz.sdk.tussup.fragments

import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kz.sdk.tussup.R
import kz.sdk.tussup.base.BaseFragment
import kz.sdk.tussup.databinding.FragmentProfileBinding
import kz.sdk.tussup.firebase.UserDao
import javax.inject.Inject


@AndroidEntryPoint
class ProfileFragment:BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {


    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var userDao: UserDao
    override fun onBindView() {
        super.onBindView()

        userDao.getData()
        with(binding){
            termsButton.setOnClickListener {

            }

            logoutButton.setOnClickListener {
                signOut()
            }
            supportButton.setOnClickListener {

            }
        }

    }
    override fun onStart() {
        super.onStart()
        if(firebaseAuth.currentUser == null){
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        }

    }
    private fun signOut() {
        var alertDialog: AlertDialog? = null
        alertDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Sign out")
            .setMessage("Are you sure you want to sign out?")
            .setPositiveButton("Yes") { _, _ ->
                firebaseAuth.signOut()
                alertDialog?.dismiss()
                findNavController().navigate(
                    R.id.action_profileFragment_to_loginFragment
                )
            }
            .setNegativeButton("Cancel") { _, _ ->
                alertDialog?.dismiss()
            }
            .show()
    }
}