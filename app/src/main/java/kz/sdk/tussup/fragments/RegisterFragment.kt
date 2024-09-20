package kz.sdk.tussup.fragments

import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kz.sdk.tussup.R
import kz.sdk.tussup.base.BaseFragment
import kz.sdk.tussup.databinding.FragmentRegisterBinding
import javax.inject.Inject


@AndroidEntryPoint
class RegisterFragment:BaseFragment<FragmentRegisterBinding>(FragmentRegisterBinding::inflate) {
    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override var showBottomNavigation: Boolean = false

    override fun onBindView() {
        super.onBindView()


        binding.registerBtn.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etPasswordRepeat.text.toString()



            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password == confirmPassword) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                showCustomDialog("Success!", "You've successfully registered")
                                binding.tilEmail.isErrorEnabled = false
                                findNavController().navigate(
                                    R.id.action_registerFragment_to_homeFragment
                                )
                            } else {
                                binding.tilEmail.isErrorEnabled = true
                                binding.tilPasswordRepeat.isErrorEnabled = true
                                binding.tilPassword.isErrorEnabled = true
                                binding.tilEmail.error = "Something is wrong"
                                binding.tilPasswordRepeat.error = "Something is wrong"
                                binding.tilPassword.error = "Something is wrong"
                            }
                        }

                } else {
                    Toast.makeText(
                        requireContext(),
                        "Passwords are not matching",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(requireContext(), "Enter something", Toast.LENGTH_SHORT).show()
            }
        }
        binding.haveAccountBtn.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }
}