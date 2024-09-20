package kz.sdk.tussup.fragments

import android.widget.Toast
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kz.sdk.tussup.R
import kz.sdk.tussup.base.BaseFragment
import kz.sdk.tussup.databinding.FragmentLoginBinding
import javax.inject.Inject


@AndroidEntryPoint
class LoginFragment:BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {
    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override var showBottomNavigation: Boolean = false
    override fun onBindView() {
        super.onBindView()

        binding.loginBtn.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showCustomDialog("Success!", "You've successfully logged in")
                        findNavController().navigate(R.id.profileFragment, null, NavOptions.Builder()
                            .setPopUpTo(R.id.homeFragment, true)
                            .build())

                    } else {
                        binding.tilEmail.isErrorEnabled = true
                        binding.tilPassword.isErrorEnabled = true
                        binding.tilEmail.error = "Something is wrong"
                        binding.tilPassword.error = "Something is wrong"
                        Toast.makeText(context, "Authentication failed: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Enter something", Toast.LENGTH_SHORT).show()
            }
        }
        binding.noAccountBtn.setOnClickListener {
            findNavController().navigate(
                R.id.action_loginFragment_to_registerFragment
            )
        }
        binding.joinAsCompanyBtn.setOnClickListener {
            findNavController().navigate(
                R.id.action_loginFragment_to_choiceFragment
            )
        }

    }
}